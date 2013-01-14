package pt.iflow.api.errors;

import java.util.Date;

public interface IError {

	public abstract int getType();

	public abstract String getUserId();

	public abstract void setType(int type);

	public abstract void setDescription(String description);

	public abstract int getFlowId();

	public abstract int getPid();

	public abstract int getSubPid();

	public abstract String getDescription();

	public abstract Date getNow();

	public abstract String toString();

}