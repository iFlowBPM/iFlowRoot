package pt.iflow.api.errors;

import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.UserInfoInterface;

public interface IErrorManager {

	public static final int sGENERIC_ERROR = 1;
	public static final int sBEAN_ERROR = 2;
	public static final int sDB_ERROR = 3;
	public static final int sMAIL_ERROR = 4;
	public static final int sIO_ERROR = 5;
	public static final int sJVM_ERROR = 6;

	public abstract String init(UserInfoInterface userInfo, Object objClass,
			String method);

	public abstract String init(UserInfoInterface userInfo,
			ProcessData procData, Object objClass, String method);

	public abstract void register(String key, int type, String description);

	public abstract void fire(String key);

	public abstract void close(String key);

}