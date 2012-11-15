package pt.iflow.api.licensing;

public class LicenseServiceException extends Exception {
  private static final long serialVersionUID = 3525986599509719599L;

  public LicenseServiceException() {
    super();
  }

  public LicenseServiceException(String message, Throwable cause) {
    super(message, cause);
  }

  public LicenseServiceException(String s) {
    super(s);
  }

  public LicenseServiceException(Throwable cause) {
    super(cause);
  }

}
