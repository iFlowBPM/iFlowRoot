package pt.iflow.api.core;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import pt.iflow.api.utils.UserInfoInterface;

public interface Repository {
  /**
   * Clear repository cache
   */
  public abstract void resetCache(UserInfoInterface userInfo);

  /**
   * Check repository availability
   * @return
   */
  public abstract boolean checkConnection();
  /**
   * Load class from repository
   * @param organization
   * @param className
   * @return
   * @throws ClassNotFoundException
   */
  public Class<?> loadClass(String organization, String className) throws ClassNotFoundException;

  /**
   * Load class from repository
   * @param userInfo
   * @param className
   * @return
   * @throws ClassNotFoundException
   */
  public Class<?> loadClass(UserInfoInterface userInfo, String className) throws ClassNotFoundException;

  public void reloadClassLoaders(UserInfoInterface userInfo);
  
  public void reloadClassLoader(UserInfoInterface userInfo);
  
  
  // new API
  
  /**
   * Return a Stylesheet from resources repository
   * @param userInfo User requesting file
   * @param xsl File name
   * @return file contents or null if not found.
   */
  public RepositoryFile getStyleSheet(UserInfoInterface userInfo, String xsl);
  
  /**
   * Return an Email Template from resources repository
   * 
   * @param userInfo User requesting file
   * @param tpl File name
   * @return file contents or null if not found.
   */
  public RepositoryFile getEmailTemplate(UserInfoInterface userInfo, String tpl);
  
  /**
   * Return an Email Template from resources repository (system template)
   * 
   * @param userInfo User requesting file
   * @param tpl File name
   * @return file contents or null if not found.
   */
  public RepositoryFile getEmailTemplate(String tpl);
  
  /**
   * Return a Printing template from resources repository
   * @param userInfo User requesting file
   * @param tpl File name
   * @return file contents or null if not found.
   */
  public RepositoryFile getPrintTemplate(UserInfoInterface userInfo, String tpl);
  
  /**
   * Return a Web file from resources repository
   * 
   * @param userInfo User requesting file
   * @param file File name
   * @return file contents or null if not found.
   */
  public RepositoryFile getWebFile(UserInfoInterface userInfo, String file);

  /**
   * Return a log file from resources repository
   * 
   * @param userInfo User requesting file
   * @param file File name
   * @return file contents or null if not found.
   */
  public RepositoryFile getLogFile(UserInfoInterface userInfo, String file);
  
  /**
   * Return organization logo
   * 
   * @param userInfo User requesting file
   * @return file contents or null if not found.
   */
  public RepositoryFile getLogo(UserInfoInterface userInfo);

  /**
   * Return a Chart file from resources repository
   * Charts are static and system only.
   * 
   * @param file File name
   * @return file contents or null if not found.
   * @deprecated Please use two arguments version
   */
  @Deprecated
  public RepositoryFile getChartFile(String file);
  /**
   * Return a Chart file from resources repository
   * Charts are static and system only.
   * 
   * @param userInfo user requesting file
   * @param file File name
   * @return file contents or null if not found.
   */
  public RepositoryFile getChartFile(UserInfoInterface userInfo, String file);

  /**
   * Return a Library file from resources repository
   * @param userInfo User requesting file
   * @param file File name
   * @return file contents or null if not found.
   */
  public RepositoryFile getLibrary(UserInfoInterface userInfo, String file);

  /**
   * Return an Icon file from resources repository
   * @param userInfo User requesting file
   * @param file File name
   * @return file contents or null if not found.
   */
  public RepositoryFile getIcon(UserInfoInterface userInfo, String file);

  /**
   * Return an Icon file from resources repository
   * @param userInfo User requesting file
   * @param file File name
   * @return file contents or null if not found.
   */
  public RepositoryFile getAnnotationIcon(UserInfoInterface userInfo, String file);

  /**
   * Return an Icon file from resources repository
   * @param userInfo User requesting file
   * @param file File name
   * @return file contents or null if not found.
   */
  public RepositoryFile getClassFile(String organization, String file);

  /**
   * Return an Icon file from resources repository
   * @param userInfo User requesting file
   * @param file File name
   * @return file contents or null if not found.
   */
  public RepositoryFile getTheme(UserInfoInterface userInfo, String file);

  public RepositoryFile getWebService(UserInfoInterface userInfo, String file);
  
  public RepositoryFile getHelp(UserInfoInterface userInfo, String file);
  
  /**
   * Put a new StyleSheet into resources repository
   * 
   * @param userInfo User uploading file
   * @param xsl File name
   * @param data file contents
   * 
   * @return true if file upload was successfull
   * 
   */
  public boolean setStyleSheet(UserInfoInterface userInfo, String xsl, byte [] data);
  
  /**
   * Put a new Email Template into resources repository
   * 
   * @param userInfo User uploading file
   * @param tpl File name
   * @param data file contents
   * 
   * @return true if file upload was successfull
   * 
   */
  public boolean setEmailTemplate(UserInfoInterface userInfo, String tpl, byte [] data);
  
  /**
   * Put a new Print Template into resources repository
   * 
   * @param userInfo User uploading file
   * @param tpl File name
   * @param data file contents
   * 
   * @return true if file upload was successfull
   * 
   */
  public boolean setPrintTemplate(UserInfoInterface userInfo, String tpl, byte [] data);
  
  /**
   * Put a new Web resource into resources repository
   * 
   * @param userInfo User uploading file
   * @param file File name
   * @param data file contents
   * 
   * @return true if file upload was successfull
   * 
   */
  public boolean setWebFile(UserInfoInterface userInfo, String file, byte [] data);
  
  /**
   * Set organization logo and save into resources repository
   * 
   * @param userInfo User uploading file
   * @param data logo contents
   * 
   * @return true if file upload was successfull
   * 
   */
  public boolean setLogo(UserInfoInterface userInfo, byte [] data);
  
  /**
   * Put a new chart file into resources repository
   * 
   * @param userInfo User uploading file
   * @param file File name
   * @param data file contents
   * 
   * @return true if file upload was successfull
   * 
   */
  public boolean setChartFile(UserInfoInterface userInfo, String file, byte [] data);

  /**
   * Put a webservice description file into resources repository
   * @param userInfo
   * @param file
   * @param data
   * @return
   */
  public boolean setWebService(UserInfoInterface userInfo, String file, byte [] data);
  
  // resource listing
  
  /**
   * List all stylesheets available to the user
   * 
   * @param userInfo
   * @return 
   */
  public RepositoryFile [] listStyleSheets(UserInfoInterface userInfo);
  
  /**
   * List all email templates available to the user
   * 
   * @param userInfo
   * @return 
   */
  public RepositoryFile [] listEmailTemplates(UserInfoInterface userInfo);
  
  /**
   * List all print templates available to the user
   * 
   * @param userInfo
   * @return 
   */
  public RepositoryFile [] listPrintTemplates(UserInfoInterface userInfo);
  
  /**
   * List all web (public) files available to the user
   * 
   * @param userInfo
   * @return 
   */
  public RepositoryFile [] listWebFiles(UserInfoInterface userInfo);
  
  /**
   * List all chart related files available to the user
   * 
   * @param userInfo
   * @return 
   */
  public RepositoryFile [] listChartFiles(UserInfoInterface userInfo);
  
  /**
   * List all editor libraries available to the user
   * 
   * @param userInfo
   * @return 
   */
  public RepositoryFile [] listLibraries(UserInfoInterface userInfo);

  /**
   * List all webservice description files available to the user
   * 
   * @param userInfo
   * @return 
   */
  public RepositoryFile [] listWebServices(UserInfoInterface userInfo);
  
  /**
   * List all webservice description files available to the user
   * 
   * @param userInfo
   * @return 
   */
  public RepositoryFile [] listIcons(UserInfoInterface userInfo);
  
  /**
   * List all message bundles available to the user
   * 
   * @param userInfo
   * @return 
   */
  public RepositoryFile [] listMessages(UserInfoInterface userInfo);

  
  /**
   * Lists all classes in the specified package
   * 
   * @param userInfo
   * @return 
   */
  public List<Class<?>> listClasses(UserInfoInterface userInfo, String inPackage);
  
  
  // Remove methods
  
  
  /**
   * Remove a stylesheet from repository
   * 
   * @param userInfo
   * @param file File name to remove
   * @return true if the file was successfully removed
   */
  public boolean removeStyleSheet(UserInfoInterface userInfo, String file);
  
  /**
   * Remove an email template from repository
   * 
   * @param userInfo
   * @param file File name to remove
   * @return true if the file was successfully removed
   */
  public boolean removeEmailTemplate(UserInfoInterface userInfo, String file);
  
  /**
   * Remove a print template from repository
   * 
   * @param userInfo
   * @param file File name to remove
   * @return true if the file was successfully removed
   */
  public boolean removePrintTemplate(UserInfoInterface userInfo, String file);
  
  /**
   * Remove a web (public) file from repository
   * 
   * @param userInfo
   * @param file File name to remove
   * @return true if the file was successfully removed
   */
  public boolean removeWebFile(UserInfoInterface userInfo, String file);
 
  
  // Reset files
  
  /**
   * 
   * @param userInfo
   * @param file File name to reset
   * @return true if the file was successfully reset
   */
  public boolean resetStyleSheet(UserInfoInterface userInfo, String file);

  /**
   * 
   * @param userInfo
   * @param file File name to reset
   * @return true if the file was successfully reset
   */
  public boolean resetEmailTemplate(UserInfoInterface userInfo, String file);

  /**
   * 
   * @param userInfo
   * @param file File name to reset
   * @return true if the file was successfully reset
   */
  public boolean resetPrintTemplate(UserInfoInterface userInfo, String file);

  /**
   * 
   * @param userInfo
   * @param file File name to reset
   * @return true if the file was successfully reset
   */
  public boolean resetWebFile(UserInfoInterface userInfo, String file);

  /**
   * Load a system ResourceBundle
   * 
   * @param bundleName
   * @param locale
   * @return
   */
  public ResourceBundle getBundle(final String bundleName, final Locale locale);
  
  /**
   * Load an organizational ResourceBundle
   * 
   * @param bundleName
   * @param locale
   * @param organization
   * @return
   */
  public ResourceBundle getBundle(final String bundleName, final Locale locale, final String organization);

  /**
   * Load a messages file from repository
   * 
   * @param userInfo
   * @param fileName
   * @return
   */
  public RepositoryFile getMessagesFile(UserInfoInterface userInfo, String fileName);
}