package pt.iflow.api.notification;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import junit.framework.TestCase;

public class MailLogManagerTest extends TestCase {
    private MailLogManager manager;

    public MailLogManagerTest() {
        super();
    }

    protected void setUp() {
        manager = MailLogManager.get();
    }

    protected void tearDown() {
        MailLogManager.stopManager();
    }

    public void testStartAndStopManager() {
        MailLogManager.startManager();
        assertTrue(manager.isAlive());
        MailLogManager.stopManager();
        assertFalse(manager.isAlive() && manager.isInterrupted());
    }

    public void testScanAndProcessWithNoLogLines() throws IOException {
        Path logPath = Paths.get("test-maillog.log");
        Files.write(logPath, Collections.emptyList());
        // You may want to set LOG_FILE to this test file if possible
        manager.scanAndProcess();
        Files.deleteIfExists(logPath);
        // No exception means pass
    }

    public void testExtractRequestId() {
        String line = "Jul 29 14:20:31 postfix/smtp[1234]: requestId=abc-123 status=sent (250 2.0.0 Ok: queued as ...)";
        String requestId = manager.extractRequestId(line);
        assertEquals("abc-123", requestId);
    }

    public void testExtractSmtpCode() {
        String line = "Jul 29 14:20:31 postfix/smtp[1234]: requestId=abc-123 status=sent (250 2.0.0 Ok: queued as ...)";
        String smtpCode = manager.extractSmtpCode(line);
        assertEquals("250", smtpCode);
    }

    public void testExtractTimestamp() {
        String line = "Jul 29 14:20:31 postfix/smtp[1234]: ...";
        String timestamp = manager.extractTimestamp(line);
        assertNotNull(timestamp);
    }

    public void testParseTimestamp() {
        String now = String.valueOf(System.currentTimeMillis());
        assertNotNull(manager.parseTimestamp(now));
        assertNull(manager.parseTimestamp(null));
        assertNull(manager.parseTimestamp("notanumber"));
    }
}