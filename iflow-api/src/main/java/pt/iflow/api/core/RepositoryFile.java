package pt.iflow.api.core;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * Proxy abstraction to a repository file
 * 
 * @author oscar
 *
 */
public interface RepositoryFile {
  /**
   * Get the file name
   * 
   * @return File name
   */
  public String getName();
  
  /**
   * Check if this is a system file
   * 
   * @return true if this is a system file
   */
  public abstract boolean isSystem();

  /**
   * Check if this is an organization file
   * 
   * @return true if this file belongs to the organization
   */
  public abstract boolean isOrganization();
  
  /**
   * Open an InputStream to the resource referenced by this RepositoryFile.
   * 
   * This InputStream instance MUST be closed explicitly by caller.
   * 
   * @return InputStream instance or null if file does not exist.
   */
  public abstract InputStream getResourceAsStream();
  
  /**
   * Write the contents of this RepositoryFile to the OutputStream.
   * 
   * The OutputStream instance will NOT be closed by this method.
   * 
   * @param outStream OutputStream instance.
   */
  public abstract void writeToStream(OutputStream outStream);
  
  /**
   * Return a byte array with the contents of this RepositoryFile
   * 
   * @return byte array or null if not found.
   */
  public abstract byte [] getResouceData();
  
  /**
   * Return this file size
   * 
   * @return file size. Return 0 if file does not exists.
   */
  public abstract int getSize();
  
  /**
   * Return when this file was last modified
   * 
   * @return timestamp in milisseconds. Return 0 if file does not exists.
   */
  public abstract long getLastModified();
  
  public abstract boolean exists();
  
  public abstract URL getURL();

}
