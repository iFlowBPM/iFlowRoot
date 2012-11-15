package pt.iknow.floweditor;

public class InvalidFlowException extends RuntimeException {
  private static final long serialVersionUID = 838542765645930274L;

  public InvalidFlowException() {
  }

  public InvalidFlowException(String message) {
    super(message);
  }

  public InvalidFlowException(Throwable cause) {
    super(cause);
  }

  public InvalidFlowException(String message, Throwable cause) {
    super(message, cause);
  }

}
