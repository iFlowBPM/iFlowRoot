package pt.iflow.api.errors;

public class ErrorCode {

	public static final ErrorCode UNDEFINED = new ErrorCode(-1, "undefined");
	public static final ErrorCode SUCCESS = new ErrorCode(0, "success");
	public static final ErrorCode FAILURE = new ErrorCode(1, "failure");
	public static final ErrorCode SEND_EMAIL = new ErrorCode(2, "send_mail");

	private int code = -1;
	private String errorKey = "undefined";
	
	protected ErrorCode(int code) {
	  this(code, "undefined");
	}

  protected ErrorCode(int code, String errorKey) {
    super();
    this.code = code;
    this.errorKey = errorKey;
  }

  /* (non-Javadoc)
 * @see pt.iflow.web.errors.IErrorCode#equals(java.lang.Object)
 */
public boolean equals(Object o) {
    if(o instanceof ErrorCode) return equals((ErrorCode) o);
    return false;
  }
  
	/* (non-Javadoc)
	 * @see pt.iflow.web.errors.IErrorCode#equals(pt.iflow.web.errors.ErrorCode)
	 */
	public boolean equals(ErrorCode err) {
		if (err == null) return false; 
		return (this.code == err.getCode());
	}

	public int getCode () {
		return this.code;
	}
	
  public String getErrorKey () {
    return this.errorKey;
  }
  
	protected ErrorCode setCode(int code) {
	  this.code = code;
	  return this;
	}

  public ErrorCode setErrorKey(String errorKey) {
    this.errorKey = errorKey;
    return this;
  }
}
