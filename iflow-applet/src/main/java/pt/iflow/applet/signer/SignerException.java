package pt.iflow.applet.signer;

public class SignerException extends Exception {

  private static final long serialVersionUID = -5436356840972632063L;

  public SignerException() {
  }

  public SignerException(String message) {
    super(message);
  }

  public SignerException(Throwable cause) {
    super(cause);
  }

  public SignerException(String message, Throwable cause) {
    super(message, cause);
  }

}
