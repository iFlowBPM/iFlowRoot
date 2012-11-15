package pt.iflow.applet;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * File abstraction. Can represent a physical file, database file, data array, etc.
 * @author iKnow
 *
 */
public interface IVFile {

  public String getName();
  public InputStream getInputStream();
  public OutputStream getOutputStream();
  public byte [] getData();
  public String getCData();
  
  public boolean canRead();
  public boolean canWrite();
  public boolean isFile();
  public File getFile();
  public long getLength();
  public String getVarName();
}
