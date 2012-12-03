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
