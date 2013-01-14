package pt.iflow.api.errors;

import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.errors.ErrorCode;

public interface IErrorHandler {

	public abstract boolean isSuccess();

	public abstract void setSuccess(boolean success);

	public abstract ErrorCode getErrorCode();

	public abstract void setErrorCode(ErrorCode errorCode);

	public abstract String getLogMessage(UserInfoInterface ui);

	public abstract String getPageMessage(UserInfoInterface ui);

}