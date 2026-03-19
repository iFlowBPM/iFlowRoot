package pt.iflow.api.notification;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
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
import java.util.zip.GZIPInputStream;

import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;

public class MailLogManager implements Runnable {
    private static volatile Thread _thread = null;
    private static volatile boolean keepRunning = false;
    private static final Object LOCK = new Object();
    private static volatile long lastStaleAlertTime = 0;

    private static final Pattern PATTERN_QUEUE_ID = Pattern.compile("postfix/\\S+\\[\\d+\\]: (\\w+):");
    private static final Pattern PATTERN_RECIPIENT = Pattern.compile("to=<([^>]+)>");
    private static final Pattern PATTERN_SMTP_CODE_SAID = Pattern.compile("\\b(2\\d{2}|4\\d{2}|5\\d{2})\\b");
    private static final Pattern PATTERN_SMTP_CODE_STATUS = Pattern.compile("status=(?:sent|bounced) \\((\\d{3})");
    private static final Pattern PATTERN_DSN = Pattern.compile("dsn=([245])\\.(\\d)\\.(\\d)");
    private static final Pattern PATTERN_STATUS_EXPIRED = Pattern.compile("status=expired\\b");

    MailLogManager() {}

    public static void startManager() {
        synchronized (LOCK) {
            if (_thread != null && _thread.isAlive()) {
                Logger.warning("system", "MailLogManager", "startManager", "MailLogManager already running");
                return;
            }
            keepRunning = true;
            _thread = new Thread(new MailLogManager(), "MailLogManager");
            _thread.setDaemon(true);
            _thread.start();
        }
    }

    public static void stopManager() {
        synchronized (LOCK) {
            keepRunning = false;
            if (_thread != null) {
                _thread.interrupt();
                _thread = null;
            }
        }
    }

    @Override
    public void run() {
        while (keepRunning) {
            scanAndProcess();
            try {
                Thread.sleep(Const.MAIL_LOG_THREAD_CICLE);
            } catch (InterruptedException e) {
                if (!keepRunning) {
                    break;
                }
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void scanAndProcess() {
        String lastTimestamp = LogCheckpoint.loadLastTimestamp();
        long lastFileSize = LogCheckpoint.loadLastFileSize();
        Date lastDate = parseTimestamp(lastTimestamp);
        Logger.info("system", this, "scanAndProcess",
                "[MailLogManager] Starting log scan. Last checkpoint: " + lastTimestamp
                + ", lastFileSize: " + lastFileSize);

        final int[] processedLines = {0};
        final int[] savedStatuses = {0};
        final int[] updatedCheckpoints = {0};
        final String[] lastTimestampWrapper = new String[] { lastTimestamp };

        java.nio.file.Path logPath = Paths.get(Const.MAIL_LOG_FILE);

        // Log rotation detection: if current file size < last known size, the file was rotated
        boolean rotationDetected = false;
        if (Files.exists(logPath)) {
            try {
                long currentSize = Files.size(logPath);
                if (lastFileSize >= 0 && currentSize < lastFileSize) {
                    rotationDetected = true;
                    Logger.warning("system", this, "scanAndProcess",
                            "[MailLogManager] Log rotation detected: current size="
                            + currentSize + " < last known size=" + lastFileSize);
                }
            } catch (IOException e) {
                Logger.error("system", this, "scanAndProcess",
                        "[MailLogManager] Error checking file size: " + e.getMessage(), e);
            }
        }

        // If rotation detected, scan rotated file first to recover missed entries
        if (rotationDetected) {
            java.nio.file.Path rotatedPath = Paths.get(Const.MAIL_LOG_FILE + ".1");
            java.nio.file.Path rotatedGzPath = Paths.get(Const.MAIL_LOG_FILE + ".1.gz");

            if (Files.exists(rotatedPath)) {
                Logger.info("system", this, "scanAndProcess",
                        "[MailLogManager] Scanning rotated file: " + rotatedPath);
                scanFile(rotatedPath, false, lastDate, processedLines,
                         savedStatuses, updatedCheckpoints, lastTimestampWrapper);
            } else if (Files.exists(rotatedGzPath)) {
                Logger.info("system", this, "scanAndProcess",
                        "[MailLogManager] Scanning rotated gzip file: " + rotatedGzPath);
                scanFile(rotatedGzPath, true, lastDate, processedLines,
                         savedStatuses, updatedCheckpoints, lastTimestampWrapper);
            } else {
                Logger.warning("system", this, "scanAndProcess",
                        "[MailLogManager] Rotation detected but no .1 or .1.gz file found");
            }
        }

        // Scan current log file
        if (!Files.exists(logPath)) {
            Logger.warning("system", this, "scanAndProcess",
                    "[MailLogManager] Log file does not exist: " + Const.MAIL_LOG_FILE);
            return;
        }
        scanFile(logPath, false, lastDate, processedLines,
                 savedStatuses, updatedCheckpoints, lastTimestampWrapper);

        // Save checkpoint with current file size
        try {
            long newFileSize = Files.size(logPath);
            if (lastTimestampWrapper[0] != null) {
                LogCheckpoint.saveCheckpoint(lastTimestampWrapper[0], newFileSize);
            }
        } catch (IOException e) {
            Logger.error("system", this, "scanAndProcess",
                    "[MailLogManager] Error saving checkpoint: " + e.getMessage(), e);
        }

        Logger.info("system", this, "scanAndProcess",
                "[MailLogManager] Log scan completed. Processed lines: "
                + processedLines[0] + ", Statuses saved: " + savedStatuses[0]
                + ", Checkpoints updated: " + updatedCheckpoints[0]);

        // Stale 150 alert check
        checkStaleAlerts();
    }

    private void scanFile(java.nio.file.Path filePath, boolean gzipped,
                          Date lastDate, int[] processedLines, int[] savedStatuses,
                          int[] updatedCheckpoints, String[] lastTimestampWrapper) {
        try {
            Stream<String> lines;
            if (gzipped) {
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                        new GZIPInputStream(Files.newInputStream(filePath)),
                        StandardCharsets.UTF_8));
                lines = reader.lines();
            } else {
                lines = Files.lines(filePath);
            }

            try (Stream<String> stream = lines) {
                stream.map(String::trim)
                      .filter(line -> shouldProcess(line, lastDate))
                      .forEach(line -> processLine(line, processedLines, savedStatuses,
                                                    updatedCheckpoints, lastTimestampWrapper));
            }
        } catch (IOException e) {
            Logger.error("system", this, "scanFile",
                    "[MailLogManager] Error reading log file " + filePath + ": "
                    + e.getMessage(), e);
        }
    }

    private void processLine(String line, int[] processedLines, int[] savedStatuses,
                             int[] updatedCheckpoints, String[] lastTimestampWrapper) {
        Logger.debug("system", this, "processLine",
                "[MailLogManager] Processing log line: " + line);
        processedLines[0]++;

        String queueId = extractQueueId(line);
        String recipient = extractRecipient(line);
        String smtpCode = extractSmtpCode(line);

        // Diagnostic logging for partial matches
        if (queueId != null && recipient != null && smtpCode == null) {
            if (isExpiredStatus(line)) {
                smtpCode = EmailManager.SMTP_EXPIRED_CODE;
                Logger.info("system", this, "processLine",
                        "[MailLogManager] Expired email detected: queueId=" + queueId
                        + ", recipient=" + recipient);
            } else {
                Logger.debug("system", this, "processLine",
                        "[MailLogManager] Partial match: queueId=" + queueId
                        + ", recipient=" + recipient + " but no smtpCode. Line: " + line);
            }
        } else if (queueId != null && recipient == null) {
            Logger.debug("system", this, "processLine",
                    "[MailLogManager] Partial match: queueId=" + queueId
                    + " but no recipient. Line: " + line);
        }

        if (queueId != null && recipient != null && smtpCode != null) {
            EmailStatus status = smtpCode.startsWith("2") ? EmailStatus.SENT : EmailStatus.FAILED;
            Logger.info("system", this, "processLine",
                    "[MailLogManager] Saving email status for queueId=" + queueId
                    + ", status=" + status.getDbValue() + ", smtpCode=" + smtpCode
                    + ", recipient=" + recipient);
            EmailManager.saveEmailStatus(null, status, smtpCode, recipient, queueId, true);
            savedStatuses[0]++;
        }

        String logTimestamp = extractTimestamp(line);
        if (logTimestamp != null
            && (lastTimestampWrapper[0] == null
                || logTimestamp.compareTo(lastTimestampWrapper[0]) > 0)) {
            long checkpointMillis = Long.parseLong(logTimestamp) - 60000L;
            if (checkpointMillis < 0) checkpointMillis = 0;
            lastTimestampWrapper[0] = String.valueOf(checkpointMillis);
            updatedCheckpoints[0]++;
        }
    }

    private void checkStaleAlerts() {
        try {
            long now = System.currentTimeMillis();
            long cooldownMs = Const.MAIL_LOG_STALE_ALERT_COOLDOWN_MINUTES * 60000L;

            if (lastStaleAlertTime > 0 && (now - lastStaleAlertTime) < cooldownMs) {
                return;
            }

            long thresholdMs = Const.MAIL_LOG_STALE_THRESHOLD_MINUTES * 60000L;
            java.sql.Timestamp threshold = new java.sql.Timestamp(now - thresholdMs);

            int staleCount = EmailManager.countStale150Records(threshold);
            if (staleCount >= Const.MAIL_LOG_STALE_ALERT_THRESHOLD) {
                Logger.warning("system", this, "checkStaleAlerts",
                        "[MailLogManager] ALERT: " + staleCount
                        + " emails stuck at smtp_code=150 for over "
                        + Const.MAIL_LOG_STALE_THRESHOLD_MINUTES
                        + " minutes. Threshold: " + Const.MAIL_LOG_STALE_ALERT_THRESHOLD
                        + ". This may indicate Postfix log processing issues.");
                lastStaleAlertTime = now;
            }
        } catch (Exception e) {
            Logger.error("system", this, "checkStaleAlerts",
                    "[MailLogManager] Error checking stale alerts: " + e.getMessage(), e);
        }
    }

    private boolean shouldProcess(String line, Date lastDate) {
        if (lastDate == null) return true;
        Date current = parseTimestamp(extractTimestamp(line));
        return current != null && current.after(lastDate);
    }

    private boolean isExpiredStatus(String line) {
        return PATTERN_STATUS_EXPIRED.matcher(line).find();
    }

    String extractTimestamp(String line) {
        try {
            String[] parts = line.trim().split("\\s+", 4);
            if (parts.length >= 3) {
                SimpleDateFormat sdf = new SimpleDateFormat(Const.MAIL_LOG_FORMAT, Locale.ENGLISH);
                sdf.setTimeZone(TimeZone.getTimeZone(Const.MAIL_LOG_TIMEZONE));

                int currentYear = java.time.Year.now().getValue();
                String fullDate = String.format("%s %s %s %s", parts[0], parts[1], parts[2], currentYear);
                Date date = sdf.parse(fullDate);

                Date now = new Date();
                if (date.after(now)) {
                    fullDate = String.format("%s %s %s %s", parts[0], parts[1], parts[2], currentYear - 1);
                    date = sdf.parse(fullDate);
                }

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

    String extractQueueId(String line) {
        Matcher matcher = PATTERN_QUEUE_ID.matcher(line);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    String extractRecipient(String line) {
        Matcher matcher = PATTERN_RECIPIENT.matcher(line);
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
        int saidIndex = line.indexOf("said:");
        if (saidIndex != -1) {
            String afterSaid = line.substring(saidIndex + 5);
            Matcher matcher = PATTERN_SMTP_CODE_SAID.matcher(afterSaid);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }

        Matcher fallbackMatcher = PATTERN_SMTP_CODE_STATUS.matcher(line);
        if (fallbackMatcher.find()) {
            return fallbackMatcher.group(1);
        }

        Matcher dsnMatcher = PATTERN_DSN.matcher(line);
        if (dsnMatcher.find()) {
            return dsnMatcher.group(1) + dsnMatcher.group(2) + dsnMatcher.group(3);
        }

        return null;
    }
}
