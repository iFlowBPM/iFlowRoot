package pt.iflow.applet;

public class InterruptedListenerException extends Error {

  private static final long serialVersionUID = -2952298908308303420L;

  public InterruptedListenerException() {
    super();
  }

  public InterruptedListenerException(String message, Throwable cause) {
    super(message, cause);
  }

  public InterruptedListenerException(String message) {
    super(message);
  }

  public InterruptedListenerException(Throwable cause) {
    super(cause);
  }

}
