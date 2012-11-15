package pt.iflow.connector.credentials;


public abstract class DMSCredential {

  /**
   * @deprecated use DMSConnectorUtils
   */
  public static DMSCredential createCredential(String username, byte[] password) {
    return new DMSEncryptedCredential(username, password);
  }

  public abstract String getUsername();

  public abstract byte[] getPassword();
}
