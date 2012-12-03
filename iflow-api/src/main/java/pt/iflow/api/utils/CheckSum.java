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
package pt.iflow.api.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.bouncycastle.util.encoders.Hex;

import pt.iflow.api.core.RepositoryFile;

public class CheckSum implements Serializable {
  private static final long serialVersionUID = -6085314254930285801L;
  
  private String checksum;
  private String file;

  public CheckSum(String checksum, String file) {
    this.file = file;
    this.checksum = checksum;
  }
  
  public String getChecksum() {
    return checksum;
  }

  public void setChecksum(String checksum) {
    this.checksum = checksum;
  }

  public String getFile() {
    return file;
  }

  public void setFile(String file) {
    this.file = file;
  }
  
  public String toString() {
    return checksum+" *"+file;
  }

  public static CheckSum parse(String line) {
    if(null == line || line.length() == 0) return null;
    
    // ignore lines starting with #
    if(line.startsWith("#")) return null;
    
    int sepPos = line.indexOf(' ');
    if(sepPos == -1) return null;
    int binPos = sepPos + 1;
    if(binPos >= line.length()) return null;
    char binChar = line.charAt(binPos);
    if(binChar == ' ') Logger.warning(null, "CheckSum", "parse", "This is an ascii line! Only binary is supported. Checksums may be different.");
    else if(binChar != '*') Logger.warning(null, "CheckSum", "parse", "Unknown mode found. Assuming binary");
    
    int filePos = binPos+1;
    if(filePos >= line.length()) return null;

    return new CheckSum(line.substring(0, sepPos), line.substring(filePos));
  }
  
  public static CheckSum[] unmarshall(byte [] data) {
    if(null == data) return null;
    ArrayList<CheckSum> sums = new ArrayList<CheckSum>();
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data), "UTF-8"));
      String line = null;
      while((line=br.readLine()) != null) {
        CheckSum sum = parse(line);
        if(null != sum)
          sums.add(sum);
      }
    } catch (IOException e) {
      Logger.warning(null, "CheckSum", "unmarshall", "Could not parse checksum data", e);
    }
    return sums.toArray(new CheckSum[sums.size()]);
  }
  
  
  public static byte [] marshall(CheckSum[] sums) {
    return marshall(Arrays.asList(sums));
  }
  
  public static byte [] marshall(Collection<CheckSum> sums) {
    if(null == sums) return null;
    StringBuilder sb = new StringBuilder();
    
    for(CheckSum sum : sums) {
      sb.append(sum).append("\n");
    }
    try {
      return sb.toString().getBytes("UTF-8");
    } catch (UnsupportedEncodingException e) {
      Logger.warning(null, "CheckSum", "marshall", "Could convert string to UTF-8 bytes", e);
    }
    return null;
  }
  
  public static String digest(RepositoryFile file) {
    if(null == file) return null;
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-1");
      md.update(file.getResouceData());
      byte [] digest = md.digest();
      return new String(Hex.encode(digest));
    } catch (NoSuchAlgorithmException e) {
    }
    return null;
  }
  
  public static String digest(RepositoryFile [] files) {
    if(null == files) return null;
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-1");
      for(RepositoryFile f : files) {
        md.update(f.getResouceData());
      }
      byte [] digest = md.digest();
      return new String(Hex.encode(digest));
    } catch (NoSuchAlgorithmException e) {
    }
    return null;
  }

  public static String digest(File file) {
    if(null == file) return null;
    InputStream in = null;
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-1");
      in = new FileInputStream(file);

      byte [] b = new byte[8192];
      int r;

      while((r = in.read(b))>=0)
        md.update(b, 0, r);

      byte [] digest = md.digest();
      return new String(Hex.encode(digest));
    } catch (Exception e) {
    } finally {
      if(null != in) {
        try {
          in.close();
        } catch (IOException e) {
        }
      }
    }
    return null;
  }
  
  public static String digest(byte [] data) {
    if(null == data) return null;
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-1");
      md.update(data);
      byte [] digest = md.digest();
      return new String(Hex.encode(digest));
    } catch (NoSuchAlgorithmException e) {
    }
    return null;
  }

}
