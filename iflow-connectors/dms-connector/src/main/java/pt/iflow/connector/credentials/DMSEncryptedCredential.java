package pt.iflow.connector.credentials;

import pt.iknow.utils.crypt.CryptUtils;

public class DMSEncryptedCredential extends DMSCredential {

  private String username;
  private String password;
  private static final CryptUtils crypt;

  static {
    crypt = new CryptUtils(String.valueOf(new DMSEncryptedCredential().hashCode()));
  }

  private DMSEncryptedCredential() {
  }

  protected DMSEncryptedCredential(String username, byte[] password) {
    this.username = username;
    this.password = crypt.encrypt(new String(password));
  }

  @Override
  public byte[] getPassword() {
    return crypt.decrypt(password).getBytes();
  }

  @Override
  public String getUsername() {
    return username;
  }
}
