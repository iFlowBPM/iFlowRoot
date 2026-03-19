package pt.iflow.api.notification;

import junit.framework.TestCase;

public class MailLogManagerTest extends TestCase {
    private MailLogManager manager;

    public MailLogManagerTest() {
        super();
    }

    protected void setUp() {
        manager = new MailLogManager();
    }

    protected void tearDown() {
        MailLogManager.stopManager();
    }

    public void testStartAndStopManager() {
        MailLogManager.startManager();
        // Verify stop doesn't throw
        MailLogManager.stopManager();
    }

    public void testStartManagerIdempotent() {
        MailLogManager.startManager();
        // Second call should not throw (already running)
        MailLogManager.startManager();
        MailLogManager.stopManager();
    }

    public void testExtractQueueId() {
        String line = "Jul 29 14:20:31 postfix/smtp[1234]: ABC123: to=<user@example.com> status=sent (250 2.0.0 Ok)";
        String queueId = manager.extractQueueId(line);
        assertEquals("ABC123", queueId);
    }

    public void testExtractQueueIdNoMatch() {
        String line = "Jul 29 14:20:31 some random line without queue id";
        assertNull(manager.extractQueueId(line));
    }

    public void testExtractRecipient() {
        String line = "Jul 29 14:20:31 postfix/smtp[1234]: ABC123: to=<user@example.com> status=sent (250 2.0.0 Ok)";
        String recipient = manager.extractRecipient(line);
        assertEquals("user@example.com", recipient);
    }

    public void testExtractRecipientLocalDomain() {
        String line = "Jul 29 14:20:31 postfix/smtp[1234]: ABC123: to=<user@example.com.localdomain> status=sent";
        String recipient = manager.extractRecipient(line);
        assertEquals("user@example.com", recipient);
    }

    public void testExtractRecipientNoMatch() {
        String line = "Jul 29 14:20:31 some random line";
        assertNull(manager.extractRecipient(line));
    }

    public void testExtractSmtpCodeFromStatus() {
        String line = "Jul 29 14:20:31 postfix/smtp[1234]: ABC123: to=<user@example.com> status=sent (250 2.0.0 Ok: queued)";
        String smtpCode = manager.extractSmtpCode(line);
        assertEquals("250", smtpCode);
    }

    public void testExtractSmtpCodeFromSaid() {
        String line = "Jul 29 14:20:31 postfix/smtp[1234]: ABC123: said: 550 5.1.1 User unknown";
        String smtpCode = manager.extractSmtpCode(line);
        assertEquals("550", smtpCode);
    }

    public void testExtractSmtpCodeFromDsn() {
        String line = "Jul 29 14:20:31 postfix/smtp[1234]: ABC123: dsn=5.1.1 some error";
        String smtpCode = manager.extractSmtpCode(line);
        assertEquals("511", smtpCode);
    }

    public void testExtractSmtpCodeNoMatch() {
        String line = "Jul 29 14:20:31 some random line";
        assertNull(manager.extractSmtpCode(line));
    }

    public void testExtractTimestamp() {
        String line = "Jul 29 14:20:31 postfix/smtp[1234]: ...";
        String timestamp = manager.extractTimestamp(line);
        assertNotNull(timestamp);
    }

    public void testExtractTimestampInvalidLine() {
        String line = "invalid";
        assertNull(manager.extractTimestamp(line));
    }

    public void testParseTimestamp() {
        String now = String.valueOf(System.currentTimeMillis());
        assertNotNull(manager.parseTimestamp(now));
        assertNull(manager.parseTimestamp(null));
        assertNull(manager.parseTimestamp("notanumber"));
    }
}
