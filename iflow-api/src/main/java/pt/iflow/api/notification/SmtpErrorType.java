package pt.iflow.api.notification;

public enum SmtpErrorType {
    SUCCESS("success"),
    TEMPORARY_FAILURE("temporary_failure"),
    PERMANENT_FAILURE("permanent_failure"),
    UNKNOWN("unknown");

    private final String dbValue;

    SmtpErrorType(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static SmtpErrorType fromDbValue(String value) {
        for (SmtpErrorType type : values()) {
            if (type.dbValue.equalsIgnoreCase(value)) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
