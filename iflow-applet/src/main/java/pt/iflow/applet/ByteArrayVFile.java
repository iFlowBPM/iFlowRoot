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
package pt.iflow.applet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ByteArrayVFile implements IVFile {
  private static Log log = LogFactory.getLog(ByteArrayVFile.class);
  private String name;
  private byte [] data;
  private ByteArrayOutputStream out;
  private final String varName;
  
  public ByteArrayVFile(String name, byte [] data, String varName) {
    this.name = name;
    this.data = data;
    this.varName = varName;
  }
  
  public ByteArrayVFile(String name, String varName) {
    this.name = name;
    this.varName = varName;
  }

  public InputStream getInputStream() {
    if(null != out)
      data = out.toByteArray();
    return new ByteArrayInputStream(data);
  }

  public String getName() {
    return name;
  }

  public OutputStream getOutputStream() {
    return out = new ByteArrayOutputStream();
  }

  public boolean canRead() {
    return true;
  }

  public boolean canWrite() {
    return true;
  }

  public boolean isFile() {
    return true;
  }
  
  public File getFile() {
    return null;
  }

  public long getLength() {
    if(out != null)
      return out.size();
    if(null != data)
    return data.length;
    return 0L;
  }

  public String getCData() {
    try {
      if(out != null) return out.toString("UTF-8"); //$NON-NLS-1$
      if(null != data) return new String(data, "UTF-8"); //$NON-NLS-1$
    } catch(UnsupportedEncodingException e) {
      log.error("Algo esta mal no reino do UTF-8", e); //$NON-NLS-1$
    }
    return null;
  }
  
  public byte[] getData() {
    if(out != null)
      return out.toByteArray();
    return data;
  }
  
  public String getVarName() {
    return this.varName;
  }

  public String toString() {
    return getVarName()+"="+getName(); //$NON-NLS-1$
  }
}
