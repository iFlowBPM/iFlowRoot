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
package pt.iflow.api.documents;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.bouncycastle.util.encoders.UrlBase64;

import pt.iflow.api.utils.UserInfoInterface;
import pt.iknow.utils.StringUtilities;

/**
 * <p>Title: Attribute</p>
 * <p>Description: This class represents a document identification hash </p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: iKnow </p>
 * @author Oscar
 * @version 15052009
 */
public class DocumentHash {
  private static final DateFormat hashDateFmt = new SimpleDateFormat("dd-MM-yyyy");
  
  private String salt;
  private String[] users;
  private DocumentIdentifier docId;
  private Date limit;
  private String hash;
  private static CryptUtils cypher = new CryptUtils();

  public DocumentHash(String hash) {
    this.hash = hash;
    String key = cypher.decrypt(hash);
    String[] values = key.split(";");
    
    this.salt = values[0];

    String[] usrs = new String[] {};
    if (values.length > 3) {
      usrs = new String[values.length - 3];
      for (int i = 3; i < values.length; i++) {
        usrs[i - 3] = values[i];
      }
    }
    this.users = usrs;

    if(StringUtils.isNotEmpty(values[1])) {
      try {
        this.setDocId(DocumentIdentifier.getInstance(values[1]));
      } catch (Exception e) {
      }
    }

    if(values.length > 2 && StringUtils.isNotEmpty(values[2])) {
      try {
        this.setLimit(hashDateFmt.parse(values[2]));
      } catch (ParseException e) {
      }
    }
  }

  public DocumentHash(UserInfoInterface userInfo, DocumentIdentifier docid, Date limit) {
    this(userInfo.getUtilizador(), docid, limit);
  }
  
  /**
   * @param users ";" separated list of users with access to this document.
   */
  public DocumentHash(String users, DocumentIdentifier docid, Date limit) {
	this.salt = RandomStringUtils.random(6, true, true);
    this.users = users.split(";");
    this.limit = limit;
    this.docId = docid;
    generateDocumentHash();
  }

  
  /**
   * Generate a hash code for this user/document
   * 
   * @param userInfo
   * @param procData
   * @return
   */
  private void generateDocumentHash() {
    String limit = "";
    if (this.limit != null) {
      limit = hashDateFmt.format(this.limit);
    }

    String sDocid = this.getDocId().getId();
    String key = this.salt + ";" + sDocid + ";" + limit + ";" + getUsersAsString();

    this.hash = cypher.encrypt(key);
  }

  /**
   * Sets the attribute docId.
   * @param aDocId the attribute docId.
   */
  public void setDocId(DocumentIdentifier aDocId) {
    docId = aDocId;
    generateDocumentHash();
  }

  /**
   * Gets the attribute docId.
   * @return the attribute docId.
   */
  public DocumentIdentifier getDocId() {
    return docId;
  }
  
  public String getUsersAsString() {
    String userString = "";
    if(getUsers() != null) {
      for(String usr : getUsers()) {
        userString += ";" + usr; 
      }
      userString = userString.replaceFirst(";", "");
    }
    return userString;
  }
  
  public String[] getUsers() {
    return users;
  }

  public void setUsers(String[] users) {
    this.users = users;
    generateDocumentHash();
  }

  public Date getLimit() {
    return limit;
  }

  public void setLimit(Date limit) {
    this.limit = limit;
    generateDocumentHash();
  }

  public String getHash() {
    return hash;
  }

  public String toString() {
    return this.getHash();
  }
  
  public boolean isValid(UserInfoInterface userInfo) {
    boolean ret = false;
    if(userInfo != null) {
      for(String user : getUsers()) {
        if(StringUtilities.isEqualIgnoreCase(userInfo.getUtilizador(), user)) {
          ret = true;
          break;
        }
      }
    }
    return ret;
  }
  
  
  private static class CryptUtils {

    private Cipher _encryptor;

    private Cipher _decryptor;

    public CryptUtils() {
      try {
        byte[] keyBytes = new byte[16];
        byte[] b = "segredo".getBytes("UTF-8");
        int len = b.length;
        if (len > keyBytes.length)
          len = keyBytes.length;
        System.arraycopy(b, 0, keyBytes, 0, len);

        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

        // the below may make this less secure, hard code byte array the IV in
        // both java and .net clients
        IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);

        _encryptor = Cipher.getInstance("AES/CBC/PKCS5Padding");
        _decryptor = Cipher.getInstance("AES/CBC/PKCS5Padding");

        _encryptor.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        _decryptor.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

      }
      catch (Exception e) {
        System.err.println("Error building CryptUtils : " + e.getMessage());
      }

    }

    public String encrypt(String text) {

      try {
        byte[] results = this._encryptor.doFinal(text.getBytes("UTF-8"));
        return new String(UrlBase64.encode(results));
      }
      catch (Exception e) {
        e.printStackTrace();
      }
      return "";
    }

    public String decrypt(String text){
      try {
        byte[] results = this._decryptor.doFinal(UrlBase64.decode(text));
        return new String(results, "UTF-8");
      }
      catch (Exception e) {
        e.printStackTrace();
      }
      return "";

    }
  }

}
