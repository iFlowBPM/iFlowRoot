package pt.iflow.api.errors;


public class UserErrorCode extends ErrorCode {

	public static final ErrorCode FAILURE_DUPLICATE_USER = new UserErrorCode(
			10001);
	public static final ErrorCode FAILURE_DUPLICATE_EMAIL = new UserErrorCode(
			10002);
	public static final ErrorCode FAILURE_NOT_AUTHORIZED = new UserErrorCode(
			10003);
	public static final ErrorCode FAILURE_USER_NOT_FOUND = new UserErrorCode(
			10004);
	public static final ErrorCode PENDING_ORG_ADM_EMAIL = new UserErrorCode(
			10005);
	public static final ErrorCode USERNAME_TOO_SHORT = new UserErrorCode(
			10006);

	public static final ErrorCode PASSWORD_NOT_COMPLEX = new UserErrorCode(
			10007);

	protected UserErrorCode(int code) {
		super(code);
	}

  protected UserErrorCode(int code, String errorKey) {
    super(code, errorKey);
  }
}
