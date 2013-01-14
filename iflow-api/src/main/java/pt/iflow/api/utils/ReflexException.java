package pt.iflow.api.utils;

/**
 * Reflex exception wrapper
 * @author oscar
 *
 */
public class ReflexException extends Exception {

  private static final long serialVersionUID = 2769283916532657850L;

  public ReflexException() {
    super();
  }

  public ReflexException(String message, Throwable cause) {
    super(message, cause);
  }

  public ReflexException(String message) {
    super(message);
  }

  public ReflexException(Throwable cause) {
    super(cause);
  }

}
