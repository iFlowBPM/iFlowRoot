package pt.iflow.core;

class RegisterException extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = -5836248512506500593L;
  
  private int errorCode;

  
  RegisterException(int code) {
    super();
    this.errorCode = code;
  }

  RegisterException(int code, String msg) {
    super(msg);
    this.errorCode = code;
  }
  
  public int getErrorCode() {
    return errorCode;
  }

}
