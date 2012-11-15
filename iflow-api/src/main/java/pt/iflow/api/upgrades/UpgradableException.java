package pt.iflow.api.upgrades;

public class UpgradableException extends Exception {
  private static final long serialVersionUID = 5139112372709700280L;

  public UpgradableException() {
    super();
  }
  

  public UpgradableException(String msg) {
    super(msg);
  }
  

  public UpgradableException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
