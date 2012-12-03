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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FileVFile implements IVFile {
  private static Log log = LogFactory.getLog(FileVFile.class);
  
  private File f;
  private final String varName;
  
  public FileVFile(File f, String varName) {
    this.f = f;
    this.varName = varName;
  }
  
  public InputStream getInputStream() {
    try {
      return new FileInputStream(f);
    } catch(FileNotFoundException e) {
      log.error("Could not open file for input", e); //$NON-NLS-1$
    }
    return null;
  }

  public String getName() {
    return f.getName();
  }

  public OutputStream getOutputStream() {
    try {
      return new FileOutputStream(f);
    } catch(FileNotFoundException e) {
      log.error("Could not open file for output", e); //$NON-NLS-1$
    }
    return null;
  }

  public boolean canRead() {
    return f.canRead();
  }

  public boolean canWrite() {
    return f.canWrite();
  }

  public boolean isFile() {
    return f.isFile();
  }
  
  public File getFile() {
    return f;
  }
  
  public long getLength() {
    return f.length();
  }

  public String getCData() {
    return null;
  }
  
  public byte[] getData() {
    return null;
  }
  
  public String getVarName() {
    return this.varName;
  }
  
  public String toString() {
    return getVarName()+"="+getName(); //$NON-NLS-1$
  }
}
