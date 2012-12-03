/*****************************************************************************************
    Infosistema iFlow - workflow and BPM platform
    Copyright(C) 2002-2012 Infosistema, S.A.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

    www.infosistema.com
    iflow@infosistema.com
    Av. Jose Gomes Ferreira, 11 3rd floor, s.34
    Miraflores
    1495-139 Alges Portugal
****************************************************************************************/
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
