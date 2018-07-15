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
	FileInputStream fis = null;
    try {
      fis = new FileInputStream(f);
      return fis;
    } catch(FileNotFoundException e) {
      log.error("Could not open file for input", e); //$NON-NLS-1$
    } finally {
    	if( fis != null) IOUtils.safeClose(fis);
	}

    return null;
  }

  public String getName() {
    return f.getName();
  }

  public OutputStream getOutputStream() {
	FileOutputStream fos = null; 
    try {
      fos = new FileOutputStream(f);
      return fos;
    } catch(FileNotFoundException e) {
      log.error("Could not open file for output", e); //$NON-NLS-1$
	} finally {
    	if( fos != null) IOUtils.safeClose(fos);
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
