package pt.iflow.applet.cipher;

public class CipherException extends Exception {

  private static final long serialVersionUID = -5436356840972632063L;

  public CipherException() {
  }

  public CipherException(String message) {
    super(message);
  }

  public CipherException(Throwable cause) {
    super(cause);
  }

  public CipherException(String message, Throwable cause) {
    super(message, cause);
  }

}
