package pt.iflow.api.notification;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;

public class MailLogManager extends Thread {
    private static MailLogManager _scanner = null;
    private boolean keepRunning = false;

    private static final String LOG_FILE = Const.MAIL_LOG_FILE;
    private static final SimpleDateFormat LOG_DATE_FORMAT = new SimpleDateFormat(Const.MAIL_LOG_FORMAT, Locale.ENGLISH);

    static {
        LOG_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone(Const.MAIL_LOG_TIMEZONE));
    }

    private MailLogManager() {}

    public static MailLogManager get() {
        if (_scanner == null) {
            _scanner = new MailLogManager();
        }
        return _scanner;
    }

    public static void startManager() {
        MailLogManager scanner = get();
        scanner.keepRunning = true;
        scanner.start();
    }

    public static void stopManager() {
        MailLogManager scanner = get();
        scanner.keepRunning = false;
        scanner.interrupt();
    }

    @Override
    public void run() {
        while (keepRunning) {
            scanAndProcess();
            try {
                sleep(Const.MAIL_LOG_THREAD_CICLE); // wait n seconds between scans
            } catch (InterruptedException e) {
                if (keepRunning) {
                    Thread.currentThread().interrupt();
                }
                break;
            }
        }
    }

    public void scanAndProcess() {
        String lastTimestamp = LogCheckpoint.loadLastTimestamp();
        Date lastDate = parseTimestamp(lastTimestamp);
        Logger.info("system", this, "scanAndProcess", "[MailLogManager] Starting log scan. Last checkpoint: " + lastTimestamp);
        final int[] processedLines = {0};
        final int[] savedStatuses = {0};
        final int[] updatedCheckpoints = {0};
        final String[] lastTimestampWrapper = new String[] { lastTimestamp };
        java.nio.file.Path logPath = Paths.get(LOG_FILE);
        if (!Files.exists(logPath)) {
            Logger.warning("system", this, "scanAndProcess", "[MailLogManager] Log file does not exist: " + LOG_FILE);
            return;
        }
        try (Stream<String> lines = Files.lines(logPath)) {
            lines.map(String::trim)
                 .filter(line -> shouldProcess(line, lastDate))
                 .forEach(line -> {
                     Logger.debug("system", this, "scanAndProcess", "[MailLogManager] Processing log line: " + line);
                     processedLines[0]++;
                     String queueId = extractQueueId(line);
                     String recipient = extractRecipient(line);
                     String smtpCode = extractSmtpCode(line);
                     Logger.debug("system", this, "scanAndProcess", "[MailLogManager] Extracted queueId: " + queueId + ", smtpCode: " + smtpCode+ ", recipient=" + recipient);
 
                     if (queueId != null && recipient != null && smtpCode != null) {
                         EmailStatus status = (smtpCode != null && smtpCode.startsWith("2")) ? EmailStatus.SENT : EmailStatus.FAILED;
                         Logger.info("system", this, "scanAndProcess", "[MailLogManager] Saving email status for queueId=" + queueId + ", status=" + status.getDbValue() + ", smtpCode=" + smtpCode+ ", recipient=" + recipient);
                         EmailManager.saveEmailStatus(null, status, smtpCode, recipient, queueId, true);
                         savedStatuses[0]++;
                     }

                     String logTimestamp = extractTimestamp(line);
                     if (logTimestamp != null && 
                         (lastTimestampWrapper[0] == null || logTimestamp.compareTo(lastTimestampWrapper[0]) > 0)) {
                         // Subtract 1 minute (60,000 ms) to avoid missing entries with the same timestamp
                         long checkpointMillis = Long.parseLong(logTimestamp) - 60000L;
                         if (checkpointMillis < 0) checkpointMillis = 0;
                         Logger.info("system", this, "scanAndProcess", "[MailLogManager] Updating checkpoint to: " + checkpointMillis + " (original: " + logTimestamp + ")");
                         LogCheckpoint.saveLastTimestamp(String.valueOf(checkpointMillis));
                         lastTimestampWrapper[0] = String.valueOf(checkpointMillis);
                         updatedCheckpoints[0]++;
                     }
                 });
            Logger.info("system", this, "scanAndProcess", "[MailLogManager] Log scan completed. Processed lines: " + processedLines[0] + ", Statuses saved: " + savedStatuses[0] + ", Checkpoints updated: " + updatedCheckpoints[0]);
        } catch (IOException e) {
            Logger.error("system", this, "scanAndProcess", "[MailLogManager] Error reading log file: " + e.getMessage(), e);
        }
    }
    
    private boolean shouldProcess(String line, Date lastDate) {
        if (lastDate == null) return true;
        Date current = parseTimestamp(extractTimestamp(line));
        return current != null && current.after(lastDate);
    }

    String extractRequestId(String line) {
        // Assumes format: some postfix line ... requestId=abc-123 ...
        int idx = line.indexOf(Email.X_REQUEST_ID + "=");
        if (idx != -1) {
            int start = idx + 10;
            int end = line.indexOf(' ', start);
            return end != -1 ? line.substring(start, end) : line.substring(start);
        }
        return null;
    }
    
    String extractTimestamp(String line) {
        // Pattern: "Jul 29 14:20:31"
        try {
            String[] parts = line.trim().split("\\s+", 4);  // ensure multiple spaces are handled
            if (parts.length >= 3) {
                String nowYear = String.valueOf(java.time.Year.now().getValue());
                String fullDate = String.format("%s %s %s %s", parts[0], parts[1], parts[2], nowYear);
                Date date = LOG_DATE_FORMAT.parse(fullDate);
                return String.valueOf(date.getTime());
            }
        } catch (ParseException e) {
            // Ignore invalid lines
        }
        return null;
    }
    
    Date parseTimestamp(String epochMillisStr) {
        if (epochMillisStr == null) return null;
        try {
            return new Date(Long.parseLong(epochMillisStr));
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
 // This replaces extractRequestId
    String extractQueueId(String line) {
        // Looks for the pattern: postfix/...]: <queueId>: ...
        Matcher matcher = Pattern.compile("postfix/\\S+\\[\\d+\\]: (\\w+):").matcher(line);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    String extractRecipient(String line) {
        // Extracts the to=<...> address
        Matcher matcher = Pattern.compile("to=<([^>]+)>").matcher(line);
        if (matcher.find()) {
            String toAddress = matcher.group(1);
            if (toAddress != null && toAddress.endsWith(".localdomain")) {
                toAddress = toAddress.substring(0, toAddress.length() - ".localdomain".length());
            }
            return toAddress;
        }
        return null;
    }
    
    String extractSmtpCode(String line) {
        // First, try to find "said:" and extract from there
        int saidIndex = line.indexOf("said:");
        if (saidIndex != -1) {
            String afterSaid = line.substring(saidIndex + 5);
            Matcher matcher = Pattern.compile("\\b(2\\d{2}|4\\d{2}|5\\d{2})\\b").matcher(afterSaid);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }

        // Fallback: look inside parentheses after status=
        Matcher fallbackMatcher = Pattern.compile("status=(?:sent|bounced) \\((\\d{3})").matcher(line);
        if (fallbackMatcher.find()) {
            return fallbackMatcher.group(1);
        }

     // New fallback: extract from dsn=5.4.4 or similar and convert to 3-digit code
        Matcher dsnMatcher = Pattern.compile("dsn=([245])\\.(\\d)\\.(\\d)").matcher(line);
        if (dsnMatcher.find()) {
            return dsnMatcher.group(1) + dsnMatcher.group(2) + dsnMatcher.group(3); // e.g., 5.4.4 → 544
        }

        return null;
    }
    
}