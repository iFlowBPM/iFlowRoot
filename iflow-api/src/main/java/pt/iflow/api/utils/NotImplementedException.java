package pt.iflow.api.utils;

public class NotImplementedException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = -3363070169112505161L;

  public NotImplementedException() {
    super();
  }

  public String getMessage() {
    StackTraceElement e = getStackTrace()[0];
    String method = e.getMethodName();
    String clazz = e.getClassName();
    return "Method "+clazz+"."+method+"() not implemented";
  }
}
