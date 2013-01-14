package pt.iflow.api.utils.mail.parsers;


public class MessageParseException extends Exception {
  private static final long serialVersionUID = -38510783317221L;

  private Exception cause;
  
  public MessageParseException(String message) {
    this(message, null);
  }
  
  public MessageParseException (Exception cause) {
    this(null, cause);
  }
  
  public MessageParseException(String message, Exception cause) {
    super(message);
    this.cause = cause;    
  }
  
  public Exception getCause() {
    return cause;
  }

  @Override
  public StackTraceElement[] getStackTrace() {
    return cause != null ? cause.getStackTrace() : super.getStackTrace();
  }
  
}
