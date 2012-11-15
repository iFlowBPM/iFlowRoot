package pt.iflow.connector.dms;

import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.discovery.tools.DiscoverSingleton;

import pt.iflow.connector.credentials.DMSCredential;
import pt.iflow.connector.document.DMSDocument;
import pt.iflow.connector.document.Document;

public abstract class DMSUtils {

  public static final String DEFAULT_NAMESPACE = "http://www.iflow.pt/alfresco/model/content/1.0";

  protected static DMSUtils instance;

  /**
   * Retrieves singleton instance.
   * 
   * @return DMSUtils instance.
   * @throws Exception
   *           If any exception occurs, meant to be handled by caller.
   */
  public static DMSUtils getInstance() throws Exception {
    if (null == instance) {
      instance = (DMSUtils) DiscoverSingleton.find(DMSUtils.class, new Properties());
    }
    return instance;
  }

  /**
   * Update document to DMS service.
   * 
   * @param credentials
   *          User credentials used for authentication
   * @param doc
   *          Document to use during the operation.
   * @param properties
   *          Properties to be used during the operation, these can vary according to the DMS implementation being used.
   * @return Updated Document
   * @throws Exception
   *           If any exception occurs, meant to be handled by caller.
   */
  public abstract DMSDocument updateDocument(DMSCredential credentials, Document doc, Map<String, String> properties)
      throws Exception;

  /**
   * Retrieve document from DMS service.
   * 
   * @param credentials
   *          User credentials used for authentication
   * @param properties
   *          Properties to be used during the operation, these can vary according to the DMS implementation being used.
   * @return Document retrieved from DMS service.
   * @throws Exception
   *           If any exception occurs, meant to be handled by caller.
   */
  public abstract DMSDocument getDocument(DMSCredential credentials, Map<String, String> properties) throws Exception;

  /**
   * Remove the document from DMS service.
   * 
   * @param credentials
   *          User credentials used for authentication
   * @param docId
   *          Document ID
   * @return True if the document was found and deleted, false otherwise.
   * @throws Exception
   *           If any exception occurs, meant to be handled by caller.
   */
  public abstract boolean removeDocument(DMSCredential credentials, UUID docId) throws Exception;

  /**
   * Parses document comments into a ContentResult object, for easy handling. This method does not communicate with the DMS server,
   * but the document parsing uses DMS server specifications to detect default properties in the comments.
   * 
   * @param credentials
   *          User credentials used for authentication
   * @param doc
   *          Document to use during the operation
   * @return Formatted comments for easy handling.
   * @throws Exception
   */
  public abstract ContentResult peek(final DMSCredential credentials, final DMSDocument doc) throws Exception;

  /**
   * Retrieves files and folders from the DMS service which are directly contained in the given path. This is the recommended
   * operation to use when fetching content information.
   * 
   * @param credentials
   *          User credentials used for authentication
   * @param searchValue
   *          Filter to be used, can either be a <a href="http://en.wikipedia.org/wiki/Lucene">Lucene</a> query, or plain text
   *          (defaults to Lucene +TEXT query).
   * @param path
   *          Path folder to start the query in, results are limited to children of this folder.
   * @return Lightweight results for the query, containing folder and file information, but no actual content.
   * @throws Exception
   *           If any exception occurs, meant to be handled by caller.
   */
  public abstract ContentResult getDirectDescendents(final DMSCredential credentials, String searchValue, String path)
      throws Exception;

  /**
   * Retrieves all files and folders from the DMS service which are contained in the given path, properly sorted. This operation can
   * be slow if there are a lot of resources to fetch.
   * 
   * @param credentials
   *          User credentials used for authentication
   * @param searchValue
   *          Filter to be used, can either be a <a href="http://en.wikipedia.org/wiki/Lucene">Lucene</a> query, or plain text
   *          (defaults to Lucene +TEXT query).
   * @param path
   *          Path folder to start the query in, results are limited to all the children (recursively) of this folder.
   * @return Lightweight results for the query, containing folder and file information, but no actual content.
   * @throws Exception
   *           If any exception occurs, meant to be handled by caller.
   */
  public abstract ContentResult getAllDescendents(final DMSCredential credentials, String searchValue, String path)
      throws Exception;

  /**
   * Retrieves the descendents of the path specified, limited by the given depth.
   * 
   * @param credentials
   *          User credentials used for authentication
   * @param path
   *          Path to start the search in, the parent path is included in the search result as the root content
   * @param depth
   *          Maximum depth to search for. Defining a negative depth will cause it to be ignored and return all possible folders.
   * @return The descendents of the path specified, limited by the given depth.
   * @throws Exception
   *           If any exception occurs, meant to be handled by caller.
   */
  public abstract ContentResult getDescendents(final DMSCredential credentials, final String path, final int depth)
      throws Exception;

  /**
   * Retrieves all files and folders from the DMS service, properly sorted. This operation can be slow if there are a lot of
   * resources to fetch.
   * 
   * @param credentials
   *          User credentials used for authentication
   * @param searchValue
   *          Filter to be used, can either be a <a href="http://en.wikipedia.org/wiki/Lucene">Lucene</a> query, or plain text
   *          (defaults to Lucene +TEXT query).
   * @return Lightweight results for the query, containing folder and file information, but no actual content.
   * @throws Exception
   *           If any exception occurs, meant to be handled by caller.
   */
  public abstract ContentResult getAllFiles(final DMSCredential credentials, String searchValue) throws Exception;

  /**
   * Checks if document access is locked, restricting access by the user.
   * 
   * @param credentials
   *          User credentials used for authentication
   * @param doc
   *          Document to use during the operation.
   * @return True if document is locked. False if the document is unlocked or locked, but accessible by the user.
   * @throws Exception
   *           If any exception occurs, meant to be handled by caller.
   */
  public abstract boolean isLocked(final DMSCredential credentials, Document doc) throws Exception;
}
