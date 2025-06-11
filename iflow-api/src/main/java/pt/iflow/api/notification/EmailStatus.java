package pt.iflow.api.notification;

public enum EmailStatus {
    PENDING("pending"),
    SENT("sent"),
    FAILED("failed"); // incluído caso ainda precises para estados intermediários ou logs

    private final String dbValue;

    EmailStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static EmailStatus fromDbValue(String dbValue) {
        for (EmailStatus status : values()) {
            if (status.dbValue.equalsIgnoreCase(dbValue)) {
                return status;
            }
        }
        return null;
    }
}
