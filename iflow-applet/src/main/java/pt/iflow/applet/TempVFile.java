package pt.iflow.applet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TempVFile implements IVFile {
  private static Log log = LogFactory.getLog(TempVFile.class);

  private final File f;
  private final String name;
  private final String varName;
  
  public TempVFile(String name, String varName) throws IOException {
    this(name, varName, null);
  }
  
  public TempVFile(String name, String varName, InputStream in) throws IOException {
    this.f = File.createTempFile("aux", ".tmp"); //$NON-NLS-1$ //$NON-NLS-2$
    this.name = name;
    this.varName = varName;

    if(null != in) {
      
      try (FileOutputStream fout = new FileOutputStream(this.f);){        
        byte[] b = new byte[8192];
        int r = 0;
        while ((r = in.read(b)) != -1)
          fout.write(b, 0, r);
	  } 
    }

  }

  public InputStream getInputStream() {
	  FileInputStream fis = null;  
	  try {
		  fis = new FileInputStream(f);
		  return fis;
	  } catch(FileNotFoundException e) {
		  log.error("Could not open file for input", e); //$NON-NLS-1$
	  }
	  return null;
  }

  public String getName() {
    return name;
  }

  public OutputStream getOutputStream() {
	  FileOutputStream fos = null;
	  try {
		  fos = new FileOutputStream(f);
		  return fos;
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

  protected void finalize() throws Throwable {
    f.delete();
    super.finalize();
  }

  public String toString() {
    return getVarName()+"="+getName(); //$NON-NLS-1$
  }
}
