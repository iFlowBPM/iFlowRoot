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
