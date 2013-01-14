/*
 *
 * Created on Jan 12, 2006 by mach
 *
 */

package pt.iflow.api.repository;

import java.util.Collection;

import pt.iflow.api.core.RepositoryObject;


public interface RepositoryAccess {

  
  /**
   * 
   * Sets a zip file, meaning that all files in the given zip file will be set in repository
   * 
   * @param path the root path to extract the files
   * @param data the zip file
   * @return flag indicating success (true) or failure (false)
   */
  public boolean setZipFile(String path, byte[] data);

  /**
   * 
   * Sets the given file in the repository
   * 
   * @param name full path file name
   * @param data the file
   * @return flag indicating success (true) or failure (false)
   */
  public boolean setFile(String name,byte[] data);

  /**
   * 
   * Sets the given file in the repository, with the optional dir flag
   * 
   * @param name full path file name
   * @param data the file
   * @param bIsDir flag to indicate that file is actually a directory
   * @return flag indicating success (true) or failure (false)
   */
  public boolean setFile(String name,byte[] data, boolean bIsDir);

  /**
   * 
   * Deletes the given file in the repository
   * 
   * @param name full path file name
   * @return flag indicating success (true) or failure (false)
   */
  public boolean deleteFile(String name);

  /**
   * 
   * Deletes the given file in the repository, including all file's children depending on process recursive flag
   * 
   * @param name full path file name
   * @param abProcessRecursively flag indicating that all file's children should also be deleted
   * @return flag indicating success (true) or failure (false)
   */
  public boolean deleteFile(String name, boolean abProcessRecursively,Collection deletedItmes);

  /**
   * 
   * List existing files in the repository from the given path to all tree leaves.
   * 
   * @param repname full path dir to list
   * @return file list
   */
  public String[] listFilesAllLeaves(String repname);

  /**
   * 
   * List existing files in the repository
   * 
   * @param name full path dir to list
   * @return file list
   */
  public String[] listFiles(String name);
  
  /**
   * 
   * List repository files otaining each file info
   * 
   * @param name full path dir to list
   * @return file info list
   */
  public RepositoryObject[] listFilesInfo(String name);

  /**
   * 
   * Moves one file from one dir to another
   * 
   * @param name full path file name
   * @param from source directory
   * @param to destination directory
   * @return flag indicating success (true) or failure (false)
   */
  public boolean moveFile(String name, String from, String to);

  /**
   * 
   * Checks if this access is available, meaning that a valid connection to a valid database can be established
   * 
   * @return true is repository is available; false otherwise
   */
  public boolean checkAvailability();
  
  /**
   * 
   * Get data from the repository
   * 
   * @param name full path file name
   * @return file
   */
  public byte[] getData(String fullname);

}