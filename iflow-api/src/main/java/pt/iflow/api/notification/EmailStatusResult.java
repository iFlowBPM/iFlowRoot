package pt.iflow.api.notification;

import java.sql.Timestamp;

public class EmailStatusResult {
    private String status;
    private String smtpCode;
    private String smtpMessage;
    private String errorType;
    private Timestamp processedAt;

    // Getters and Setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSmtpCode() { return smtpCode; }
    public void setSmtpCode(String smtpCode) { this.smtpCode = smtpCode; }

    public String getSmtpMessage() { return smtpMessage; }
    public void setSmtpMessage(String smtpMessage) { this.smtpMessage = smtpMessage; }

    public String getErrorType() { return errorType; }
    public void setErrorType(String errorType) { this.errorType = errorType; }

    public Timestamp getProcessedAt() { return processedAt; }
    public void setProcessedAt(Timestamp processedAt) { this.processedAt = processedAt; }
}
