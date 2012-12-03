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
package pt.iflow.connector.alfresco;

import java.io.InputStream;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.alfresco.webservice.authoring.LockStatus;
import org.alfresco.webservice.authoring.LockTypeEnum;
import org.alfresco.webservice.content.Content;
import org.alfresco.webservice.repository.QueryResult;
import org.alfresco.webservice.repository.RepositoryFault;
import org.alfresco.webservice.repository.RepositoryServiceSoapBindingStub;
import org.alfresco.webservice.repository.UpdateResult;
import org.alfresco.webservice.types.CML;
import org.alfresco.webservice.types.CMLAddAspect;
import org.alfresco.webservice.types.CMLCreate;
import org.alfresco.webservice.types.CMLDelete;
import org.alfresco.webservice.types.CMLRemoveAspect;
import org.alfresco.webservice.types.CMLUpdate;
import org.alfresco.webservice.types.CMLWriteContent;
import org.alfresco.webservice.types.ContentFormat;
import org.alfresco.webservice.types.NamedValue;
import org.alfresco.webservice.types.Node;
import org.alfresco.webservice.types.ParentReference;
import org.alfresco.webservice.types.Predicate;
import org.alfresco.webservice.types.Query;
import org.alfresco.webservice.types.Reference;
import org.alfresco.webservice.types.ResultSet;
import org.alfresco.webservice.types.ResultSetRow;
import org.alfresco.webservice.types.Store;
import org.alfresco.webservice.util.AuthenticationUtils;
import org.alfresco.webservice.util.Constants;
import org.alfresco.webservice.util.ContentUtils;
import org.alfresco.webservice.util.Utils;
import org.alfresco.webservice.util.WebServiceFactory;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import pt.iflow.api.documents.DocumentData;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.connector.credentials.DMSCredential;
import pt.iflow.connector.dms.ContentResult;
import pt.iflow.connector.dms.DMSUtils;
import pt.iflow.connector.document.DMSDocument;
import pt.iflow.connector.document.Document;

public class AlfrescoUtils extends DMSUtils {

  private static final String SCHEME = "scheme";
  private static final String ADDRESS = "address";

  private static final String ID = "id";
  private static final String PATH = "path";

  private static final String LOCK = "lock";
  private static final String VERSIONABLE = "versionable";

  private static final String TITLE = "title";
  private static final String DESCRIPTION = "description";
  private static final String AUTHOR = "author";

  private static final String[] DATA = new String[] { SCHEME, ADDRESS, ID, PATH, LOCK, VERSIONABLE, TITLE, DESCRIPTION, AUTHOR };

  private static final String DEFAULT_SCHEME = Constants.WORKSPACE_STORE;
  private static final String DEFAULT_ADDRESS = "SpacesStore";

  private static final Map<String, String> mimeTypes;

  static {
    if (instance == null) {
      instance = new AlfrescoUtils();
    }

    mimeTypes = new HashMap<String, String>();
    mimeTypes.put("xls", "application/vnd.ms-excel");
    mimeTypes.put("doc", "application/msword");
    mimeTypes.put("html", "text/html");
    mimeTypes.put("htm", "text/html");
    mimeTypes.put("jpg", "image/jpeg");
    mimeTypes.put("jpeg", "image/jpeg");
    mimeTypes.put("bmp", "image/bmp");
    mimeTypes.put("pdf", "application/pdf");
    mimeTypes.put("ppt", "application/vnd.ms-powerpoint");
    mimeTypes.put("zip", "application/vnd.ms-zip");
    mimeTypes.put("xml", Constants.MIMETYPE_XML);
    mimeTypes.put("css", Constants.MIMETYPE_TEXT_CSS);
    mimeTypes.put("txt", Constants.MIMETYPE_TEXT_PLAIN);
    mimeTypes.put("", Constants.MIMETYPE_TEXT_PLAIN);
    Collections.unmodifiableMap(mimeTypes);
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iflow.connector.dms.DMSUtils#updateDocument(pt.iflow.connector.credentials.DMSCredential,
   * pt.iflow.connector.document.Document, java.util.Map)
   */
  public DMSDocument updateDocument(final DMSCredential credentials, Document document, final Map<String, String> properties)
      throws Exception {
    if (Logger.isDebugEnabled()) {
      Logger.debug(credentials.getUsername(), this, "updateDocument", "Entered method!");
    }
    if (!(document instanceof AlfrescoDocument)) {
      if (document instanceof DocumentData) {
        DocumentData docData = (DocumentData) document;
        document = new AlfrescoDocument(properties.get(SCHEME), properties.get(ADDRESS), properties.get(ID), properties.get(PATH));
        document.setContent(docData.getContent());
        document.setFileName(docData.getFileName());
        document.setDocId(docData.getDocId());
      }
    }
    if (document instanceof AlfrescoDocument) {
      final AlfrescoDocument doc = (AlfrescoDocument) document;
      return (DMSDocument) action(credentials, new AlfrescoAction() {
        public Object execute() throws Exception {
          return updateContent(credentials, doc, properties);
        }
      });
    }
    Logger.warning(credentials.getUsername(), this, "updateDocument", "Unable to validate document into DMS standards.");
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iflow.connector.dms.DMSUtils#getDocument(pt.iflow.connector.dms.DMSCredential, java.util.Map)
   */
  public DMSDocument getDocument(final DMSCredential credentials, Map<String, String> properties) throws Exception {
    if (Logger.isDebugEnabled()) {
      Logger.debug(credentials.getUsername(), this, "getDocument", "Entered method!");
    }
    String scheme = properties.get(SCHEME);
    String address = properties.get(ADDRESS);
    String uuid = properties.get(ID);
    String path = properties.get(PATH);
    scheme = (StringUtils.isBlank(scheme) ? DEFAULT_SCHEME : scheme);
    address = (StringUtils.isBlank(address) ? DEFAULT_ADDRESS : address);
    final AlfrescoDocument doc = new AlfrescoDocument(scheme, address, uuid, path);

    final boolean reserve = Boolean.valueOf(properties.get(LOCK));
    return (AlfrescoDocument) action(credentials, new AlfrescoAction() {
      public Object execute() throws Exception {
        Predicate predicate = new Predicate(new Reference[] { doc.reference }, doc.reference.getStore(), null);
        Node[] nodes = WebServiceFactory.getRepositoryService().get(predicate);
        if (nodes != null && nodes.length > 0) {
          Reference[] ref = WebServiceFactory.getAuthoringService().lock(predicate, false, LockTypeEnum.read);
          // locked
          {
            Node node = nodes[0];
            for (NamedValue namedValue : node.getProperties()) {
              if (namedValue.getName().endsWith(Constants.PROP_NAME)) {
                doc.setFileName(namedValue.getValue());
              } else if (!namedValue.getName().endsWith(Constants.PROP_CONTENT)) {
                doc.addComment(namedValue.getName(), namedValue.getValue());
              }
            }
            predicate = new Predicate(ref, doc.reference.getStore(), null);
            Content[] readResult = WebServiceFactory.getContentService().read(predicate, Constants.PROP_CONTENT);
            if (readResult.length > 0) {
              InputStream contentStream = ContentUtils.getContentAsInputStream(readResult[0]);
              doc.setContent(IOUtils.toByteArray(contentStream));
            }
          }
          if (!reserve) {
            WebServiceFactory.getAuthoringService().unlock(predicate, false);
          }
          if (Logger.isDebugEnabled()) {
            Logger.debug(credentials.getUsername(), this, "action>execute>getDocument", "Document '" + doc.getFileName()
                + "' retrieved successfully, status: " + (reserve ? "reserved" : "unreserved"));
          }
        }
        return doc;
      }
    });
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iflow.connector.dms.DMSUtils#peek(DMSCredential, AlfrescoDocument)
   */
  public ContentResult peek(final DMSCredential credentials, final DMSDocument doc) throws Exception {
    ContentResult contentResult = new ContentResult(doc.getUuid());
    for (String key : doc.getComments().keySet()) {
      NamedValue namedValue = new NamedValue(key, false, doc.getComments().get(key), null);
      if (namedValue.getName().endsWith(Constants.PROP_CREATED)) {
        contentResult.setCreateDate(formatToDate(namedValue.getValue()));
      } else if (namedValue.getName().endsWith(Constants.createQNameString(Constants.NAMESPACE_CONTENT_MODEL, "modified"))) {
        contentResult.setModifiedDate(formatToDate(namedValue.getValue()));
      } else if (namedValue.getName().endsWith(Constants.PROP_NAME)) {
        contentResult.setName(namedValue.getValue());
      } else if (namedValue.getName().endsWith(Constants.PROP_DESCRIPTION)) {
        contentResult.setDescription(namedValue.getValue());
      } else if (namedValue.getName().endsWith(Constants.PROP_CONTENT)) {
        String contentString = namedValue.getValue();
        String[] values = contentString.split("[|=]");
        contentResult.setUrl(values[1]);
      } else if (namedValue.getName().endsWith(Constants.PROP_TITLE)) {
        contentResult.setTitle(namedValue.getValue());
      } else if (namedValue.getName().endsWith(Constants.createQNameString(Constants.NAMESPACE_CONTENT_MODEL, AUTHOR))) {
        contentResult.setAuthor(namedValue.getValue());
      } else {
        contentResult.addComment(namedValue.getName(), namedValue.getValue());
      }
    }
    return contentResult;
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iflow.connector.dms.DMSUtils#getDirectDescendents(pt.iflow.connector.dms.DMSCredential, java.lang.String,
   * java.lang.String)
   */
  public ContentResult getDirectDescendents(final DMSCredential credentials, final String searchValue, final String path)
      throws Exception {
    return (ContentResult) action(credentials, new AlfrescoAction() {
      public Object execute() throws Exception {
        ContentResult initialContent = new ContentResult();
        ParentReference ref = getPathReference(credentials, path);
        if (ref == null) {
          return initialContent;
        }
        initialContent.setPath(path);
        initialContent.setId(ref.getUuid());
        initialContent.setName(ref.getPath());
        initialContent.setScheme(ref.getStore().getScheme());
        initialContent.setAddress(ref.getStore().getAddress());
        return getFiles(credentials, initialContent, searchValue, 1);
      }
    });
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iflow.connector.dms.DMSUtils#getAllDescendents(pt.iflow.connector.dms.DMSCredential, java.lang.String,
   * java.lang.String)
   */
  public ContentResult getAllDescendents(final DMSCredential credentials, final String searchValue, final String path)
      throws Exception {
    return (ContentResult) action(credentials, new AlfrescoAction() {
      public Object execute() throws Exception {
        ContentResult initialContent = new ContentResult();
        ParentReference ref = getPathReference(credentials, path);
        if (ref == null) {
          return initialContent;
        }
        initialContent.setPath(path);
        initialContent.setId(ref.getUuid());
        initialContent.setName(ref.getPath());
        initialContent.setScheme(ref.getStore().getScheme());
        initialContent.setAddress(ref.getStore().getAddress());
        return getFiles(credentials, initialContent, searchValue, -1);
      }
    });
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iflow.connector.dms.DMSUtils#getDescendents(pt.iflow.connector.credentials.DMSCredential, java.lang.String, int)
   */
  public ContentResult getDescendents(final DMSCredential credentials, final String path, final int depth) throws Exception {
    return (ContentResult) action(credentials, new AlfrescoAction() {
      public Object execute() throws Exception {
        ContentResult root = new ContentResult();
        ParentReference ref = getPathReference(credentials, path);
        root.setId(ref.getUuid());
        root.setName(ref.getPath());
        if (ref.getStore() != null) {
          root.setAddress(ref.getStore().getAddress());
          root.setScheme(ref.getStore().getScheme());
        }
        return getFiles(credentials, root, null, depth);
      }
    });
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iflow.connector.dms.DMSUtils#getAllFiles(pt.iflow.connector.dms.DMSCredential, java.lang.String)
   */
  public ContentResult getAllFiles(final DMSCredential credentials, final String searchValue) throws Exception {
    return (ContentResult) action(credentials, new AlfrescoAction() {
      public Object execute() throws Exception {
        ContentResult root = new ContentResult();
        ParentReference ref = getPathReference(credentials, null);
        if (ref == null) {
          return root;
        }
        root.setId(ref.getUuid());
        root.setName(ref.getPath());
        root.setScheme(ref.getStore().getScheme());
        root.setAddress(ref.getStore().getAddress());
        return getFiles(credentials, root, searchValue, -1);
      }
    });
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iflow.connector.dms.DMSUtils#isLocked(pt.iflow.connector.credentials.DMSCredential,
   * pt.iflow.connector.document.Document)
   */
  public boolean isLocked(final DMSCredential credentials, final Document doc) throws Exception {
    return (Boolean) action(credentials, new AlfrescoAction() {
      public Object execute() throws Exception {
        boolean locked = false;
        if (doc instanceof DMSDocument) {
          Map<String, String> map = ((DMSDocument) doc).getComments();
          for (String key : map.keySet()) {
            map.get(key);
          }
          DMSDocument dmsDoc = (DMSDocument) doc;
          dmsDoc.setScheme((StringUtils.isBlank(dmsDoc.getScheme()) ? DEFAULT_SCHEME : dmsDoc.getScheme()));
          dmsDoc.setAddress((StringUtils.isBlank(dmsDoc.getAddress()) ? DEFAULT_ADDRESS : dmsDoc.getAddress()));
          Reference ref = new Reference(new Store(dmsDoc.getScheme(), dmsDoc.getAddress()), dmsDoc.getUuid(), dmsDoc.getPath());
          LockStatus[] status = WebServiceFactory.getAuthoringService().getLockStatus(
              new Predicate(new Reference[] { ref }, ref.getStore(), null));
          if (status != null && status.length > 0) {
            locked = status[0].getLockType().equals(LockTypeEnum._read)
                && !StringUtils.equalsIgnoreCase(status[0].getLockOwner(), credentials.getUsername());
          }
        }
        return locked;
      }
    });
  }
  
  @Override
  public boolean removeDocument(final DMSCredential credentials, final UUID docId) throws Exception {
    return (Boolean) action(credentials, new AlfrescoAction() {
      public Object execute() throws Exception {
        boolean retObj = false;
        AlfrescoDocument alfDoc = new AlfrescoDocument(DEFAULT_SCHEME, DEFAULT_ADDRESS, docId.toString(), null);
        Predicate predicate = new Predicate(new Reference[] { alfDoc.reference }, alfDoc.reference.getStore(), null);
        CMLDelete delete = new CMLDelete(predicate);
        CML cml = new CML();
        cml.setDelete(new CMLDelete[] { delete });
        UpdateResult[] result = WebServiceFactory.getRepositoryService().update(cml);
        if (result != null && result.length > 0) {
          retObj = true;
          if (Logger.isDebugEnabled()) {
            for (int i = 0; i < result.length; i++) {
              Logger.debug(credentials.getUsername(), this, "validatePath", "DMS document deleted: '" + docId.toString() + "'");
            }
          }
        }
        return retObj;
      }
    });
  }

  public DMSDocument updateContent(DMSCredential credentials, AlfrescoDocument doc, Map<String, String> properties)
      throws Exception {
    DMSDocument retObj = doc;

    boolean reserve = Boolean.valueOf(properties.get(LOCK));
    boolean versionable = Boolean.valueOf(properties.get(VERSIONABLE));
    Predicate predicate;
    Reference[] ref;
    String scheme = properties.get(SCHEME);
    String address = properties.get(ADDRESS);
    scheme = (StringUtils.isBlank(scheme) ? DEFAULT_SCHEME : scheme);
    address = (StringUtils.isBlank(address) ? DEFAULT_ADDRESS : address);
    Store store = new Store(scheme, address);
    if (Logger.isDebugEnabled()) {
      Logger.debug(credentials.getUsername(), this, "updateContent", "Using store '" + store + "'");
    }
    ParentReference parentReference = validatePath(credentials, store, properties.get(PATH), true);
    List<NamedValue> comments = new ArrayList<NamedValue>();
    try {
      // check if file with same name already exists in the target directory
      String uuid = "", path = "";
      QueryResult qr = WebServiceFactory.getRepositoryService().queryChildren(parentReference);
      ResultSet rs = qr.getResultSet();
      for (ResultSetRow row : rs.getRows()) {
        for (NamedValue item : row.getColumns()) {
          if (item.getName().endsWith(Constants.PROP_NAME) && StringUtils.equals(item.getValue(), doc.getFileName())) {
            Logger.warning(credentials.getUsername(), this, "updateContent", "Document '" + doc.getFileName()
                + "' already exists in Alfresco folder '" + properties.get(PATH) + "', ignoring insert.");

            int nFound = 0;
            for (NamedValue prop : row.getColumns()) {
              if (prop.getName().endsWith(Constants.createQNameString(Constants.NAMESPACE_SYSTEM_MODEL, "node-uuid"))) {
                uuid = prop.getValue();
                doc.setUuid(prop.getValue());
                nFound++;
              }
              if (prop.getName().endsWith(Constants.createQNameString(Constants.NAMESPACE_CONTENT_MODEL, "path"))) {
                path = prop.getValue();
                doc.setPath(prop.getValue());
                nFound++;
              }
              if (nFound>=2) break;
            }
            break;
          }
        }
      }
      Reference reference = new Reference(store, uuid, path);
      if (StringUtils.isNotBlank(doc.getFileName())) {
        comments.add(Utils.createNamedValue(Constants.PROP_NAME, doc.getFileName()));
      }
      //try's to lock the existing file, fails if the file does not exists
      predicate = new Predicate(new Reference[] { reference }, doc.reference.getStore(), null);
      ref = WebServiceFactory.getAuthoringService().lock(predicate, false, LockTypeEnum.write);
      Logger.info(credentials.getUsername(), this, "updateContent", "Updating file: " + doc.getFileName());
    } catch (Exception ex) {
      Logger.info(credentials.getUsername(), this, "updateContent", "Creating new file: " + doc.getFileName());
      NamedValue[] property = new NamedValue[] { Utils.createNamedValue(Constants.PROP_NAME, doc.getFileName()) };
      CMLCreate create = new CMLCreate(null, parentReference, null, null, null, Constants.TYPE_CONTENT, property);
      CML cmlC = new CML();
      cmlC.setCreate(new CMLCreate[] { create });
      UpdateResult[] results = WebServiceFactory.getRepositoryService().update(cmlC);
      ref = new Reference[results.length];
      for (int i = 0; i < ref.length; i++) {
        ref[i] = results[i].getDestination();

        if (ref[i] != null) {
          if (doc.reference == null) {
            doc.reference = ref[i];
          } else if (StringUtils.isEmpty(doc.reference.getUuid())) {
            doc.setUuid(ref[i].getUuid());
          }
        }
      }
      predicate = new Predicate(ref, doc.reference.getStore(), null);
      ref = WebServiceFactory.getAuthoringService().lock(predicate, false, LockTypeEnum.write);
    }
    // locked
    {
      CML cml = new CML();
      // add aspects
      if (versionable) {
        CMLAddAspect addAspect = new CMLAddAspect(Constants.ASPECT_VERSIONABLE, null, predicate, null);
        cml.setAddAspect(new CMLAddAspect[] { addAspect });
      } else {
        CMLRemoveAspect removeAspect = new CMLRemoveAspect(Constants.ASPECT_VERSIONABLE, predicate, null);
        cml.setRemoveAspect(new CMLRemoveAspect[] { removeAspect });
      }
      // add properties
      for (String key : doc.getComments().keySet()) {
        String value = doc.getComments().get(key);
        if (StringUtils.isNotBlank(value)) {
          comments.add(Utils.createNamedValue(key, value));
        }
      }
      for (String key : properties.keySet()) {
        String value = properties.get(key);
        if (StringUtils.isNotBlank(value)) {
          if (!contains(DATA, key)) {
            comments.add(Utils.createNamedValue(Constants.createQNameString(DEFAULT_NAMESPACE, key), value));
          } else if (StringUtils.equals(key, TITLE)) {
            comments.add(Utils.createNamedValue(Constants.PROP_TITLE, value));
          } else if (StringUtils.equals(key, DESCRIPTION)) {
            comments.add(Utils.createNamedValue(Constants.PROP_DESCRIPTION, value));
          } else if (StringUtils.equals(key, AUTHOR)) {
            comments.add(Utils.createNamedValue(Constants.createQNameString(Constants.NAMESPACE_CONTENT_MODEL, AUTHOR), value));
          }
        }
      }
      if (comments != null && comments.size() > 0) {
        CMLUpdate update = new CMLUpdate(comments.toArray(new NamedValue[comments.size()]), predicate, null);
        cml.setUpdate(new CMLUpdate[] { update });
      }
      // add content

      CMLWriteContent writeContent = new CMLWriteContent(Constants.PROP_CONTENT, doc.getContent(), getContentFormat(doc),
          predicate, null);
      cml.setWriteContent(new CMLWriteContent[] { writeContent });
      // perform update
      UpdateResult[] updateResults = WebServiceFactory.getRepositoryService().update(cml);
      if (Logger.isDebugEnabled()) {
        for (UpdateResult updateResult : updateResults) {
          String sourceId = "none";
          Reference source = updateResult.getSource();
          if (source != null) {
            sourceId = source.getUuid();
          }
          String destinationId = "none";
          Reference destination = updateResult.getDestination();
          if (destination != null) {
            destinationId = destination.getUuid();
          }
          Logger.debug(credentials.getUsername(), this, "updateContent", "Command = " + updateResult.getStatement() + "; Source = "
              + sourceId + "; Destination = " + destinationId);
        }
      }
    }
    if (!reserve) {
      WebServiceFactory.getAuthoringService().unlock(predicate, false);
    }
    if (Logger.isDebugEnabled()) {
      String append = "";
      append += "reserved ? " + (reserve ? "yes" : "no") + "; ";
      append += "versionable ? " + (versionable ? "yes" : "no") + "; ";
      Logger.debug(credentials.getUsername(), this, "updateContent", "Document '" + doc.getFileName()
          + "' updated successfully, status: " + append);
    }
    return retObj;
  }

  private ContentResult getFiles(final DMSCredential credentials, final ContentResult initialContent, final String searchValue,
      final int maxSearchDepth) throws Exception {
    initialContent.setScheme(StringUtils.isBlank(initialContent.getScheme()) ? DEFAULT_SCHEME : initialContent.getScheme());
    initialContent.setAddress(StringUtils.isBlank(initialContent.getAddress()) ? DEFAULT_ADDRESS : initialContent.getAddress());
    return recursiveTreeSearch(initialContent, searchValue, 0, maxSearchDepth);
  }

  private ContentResult recursiveTreeSearch(ContentResult currentNode, String searchValue, int currentDepth, int maxSearchDepth)
      throws Exception {
    if (maxSearchDepth < 0 || currentDepth < maxSearchDepth) {
      Store store = new Store(currentNode.getScheme(), currentNode.getAddress());
      Reference reference = new Reference(store, currentNode.getId(), currentNode.getName());
      currentNode.setChildren(doQuery(reference, searchValue));
      for (int i = 0, lim = currentNode.getChildren().size(); i < lim; i++) {
        ContentResult node = currentNode.getChildren().get(i);
        node.setScheme(currentNode.getScheme());
        node.setAddress(currentNode.getAddress());
        node = recursiveTreeSearch(node, searchValue, currentDepth++, maxSearchDepth);
        currentNode.getChildren().set(i, node);
      }
    }
    return currentNode;
  }

  private List<ContentResult> doQuery(Reference reference, String searchValue) throws Exception {
    searchValue = (StringUtils.isBlank(searchValue) ? "" : searchValue.trim().startsWith("+") ? searchValue : " +TEXT:\""
        + searchValue + "\"");
    Store store = reference.getStore();
    List<ContentResult> results = new ArrayList<ContentResult>();
    RepositoryServiceSoapBindingStub repositoryService = WebServiceFactory.getRepositoryService();
    Predicate predicate = new Predicate(new Reference[] { reference }, null, null);
    Node[] nodes = null;
    try {
      nodes = repositoryService.get(predicate);
    } catch (Exception e) {
      return results;
    }
    Query query = new Query(Constants.QUERY_LANG_LUCENE, "+PARENT:\"" + store.getScheme() + "://" + store.getAddress() + "/"
        + nodes[0].getReference().getUuid() + "\"" + searchValue);
    QueryResult queryResult = repositoryService.query(store, query, false);
    ResultSet resultSet = queryResult.getResultSet();
    ResultSetRow[] rows = resultSet.getRows();
    if (rows != null) {
      for (ResultSetRow row : rows) {
        String nodeId = row.getNode().getId();
        ContentResult contentResult = new ContentResult(nodeId);
        contentResult.setAddress(store.getAddress());
        contentResult.setScheme(store.getScheme());
        for (String aspect : row.getNode().getAspects()) {
          contentResult.addAspect(aspect);
        }
        for (NamedValue namedValue : row.getColumns()) {
          if (namedValue.getName().endsWith(Constants.PROP_CREATED)) {
            contentResult.setCreateDate(formatToDate(namedValue.getValue()));
          } else if (namedValue.getName().endsWith(Constants.createQNameString(Constants.NAMESPACE_CONTENT_MODEL, "modified"))) {
            contentResult.setModifiedDate(formatToDate(namedValue.getValue()));
          } else if (namedValue.getName().endsWith(Constants.PROP_NAME)) {
            contentResult.setName(namedValue.getValue());
          } else if (namedValue.getName().endsWith(Constants.PROP_DESCRIPTION)) {
            contentResult.setDescription(namedValue.getValue());
          } else if (namedValue.getName().endsWith(Constants.PROP_CONTENT)) {
            String contentString = namedValue.getValue();
            String[] values = contentString.split("[|=]");
            contentResult.setUrl(values[1]);
          } else if (namedValue.getName().endsWith(Constants.PROP_TITLE)) {
            contentResult.setTitle(namedValue.getValue());
          } else if (namedValue.getName().endsWith(Constants.createQNameString(Constants.NAMESPACE_CONTENT_MODEL, AUTHOR))) {
            contentResult.setAuthor(namedValue.getValue());
          } else {
            contentResult.addComment(namedValue.getName(), namedValue.getValue());
          }
        }
        contentResult = validate(contentResult);
        results.add(contentResult);
      }
    }
    return results;
  }
  
  private static Object action(DMSCredential credentials, AlfrescoAction behavior) throws Exception {
    Object retObj = null;
    String endpoint = Const.DMS_END_POINT_ADDRESS;
    String login = credentials.getUsername();
    String password = new String(credentials.getPassword());
    WebServiceFactory.setEndpointAddress(endpoint);
    if (Logger.isDebugEnabled()) {
      Logger.debug(credentials.getUsername(), AlfrescoUtils.class, "action", "Attempting remote authentication: '" + login + "'@'"
          + endpoint + "'");
    }
    AuthenticationUtils.startSession(login, password);
    if (Logger.isDebugEnabled()) {
      Logger.debug(credentials.getUsername(), AlfrescoUtils.class, "action", "Authentication successful!");
    }
    try {
      retObj = behavior.execute();
    } finally {
      AuthenticationUtils.endSession();

      if (Logger.isDebugEnabled()) {
        Logger.debug(credentials.getUsername(), AlfrescoUtils.class, "action", "Session terminated for: '" + login + "'@'"
            + endpoint + "'");
      }
    }
    return retObj;
  }

  private ParentReference getPathReference(DMSCredential credentials, String path) throws Exception {
    return validatePath(credentials, null, path, false);
  }

  private ParentReference validatePath(DMSCredential credentials, Store store, String path, boolean allowCreate) throws Exception {
    if (store == null) {
      store = new Store();
    }
    store.setScheme(StringUtils.isBlank(store.getScheme()) ? DEFAULT_SCHEME : store.getScheme());
    store.setAddress(StringUtils.isBlank(store.getAddress()) ? DEFAULT_ADDRESS : store.getAddress());
    path = (StringUtils.isBlank(path) ? "" : path);
    path = path.replace("\\", "/");
    path = (!path.startsWith("/") ? "/" + path : path);
    String[] dirs = path.split("/");
    if (Logger.isDebugEnabled()) {
      Logger.debug(credentials.getUsername(), this, "validatePath", "Validating path: '" + path + "'");
    }
    RepositoryServiceSoapBindingStub repositoryService = WebServiceFactory.getRepositoryService();
    ParentReference parentReference = new ParentReference(store, null, "/app:company_home", Constants.ASSOC_CONTAINS, Constants
        .createQNameString(Constants.NAMESPACE_CONTENT_MODEL, Constants.PROP_NAME));
    for (int i = 0, lim = dirs.length; i < lim; i++) {
      String dir = dirs[i];
      if (StringUtils.isNotBlank(dir)) {
        NamedValue directory = Utils.createNamedValue(Constants.PROP_NAME, dir);
        Reference reference = null;
        boolean isVoid = true;
        QueryResult qr = repositoryService.queryChildren(parentReference);
        ResultSet rs = qr.getResultSet();
        if (rs != null && rs.getRows() != null) {
          for (ResultSetRow row : rs.getRows()) {
            for (NamedValue item : row.getColumns()) {
              if (item.getName().endsWith(Constants.PROP_NAME) && StringUtils.equals(item.getValue(), directory.getValue())) {
                isVoid = false;
                reference = new Reference(store, row.getNode().getId(), null);

                if (Logger.isDebugEnabled()) {
                  String fullDir = "";
                  for (int j = 0; j < i; j++) {
                    fullDir += "/" + dirs[j];
                  }
                  fullDir += "/" + dir;
                  Logger.debug(credentials.getUsername(), this, "validatePath", "DMS folder found: '" + fullDir + "'");
                }

                break;
              }
            }
            if (!isVoid) {
              break;
            }
          }
        }
        if (allowCreate && isVoid) {
          CML service = new CML();
          service.setCreate(new CMLCreate[] { new CMLCreate(null, parentReference, null, null, null, Constants.TYPE_FOLDER,
              new NamedValue[] { directory }) });
          UpdateResult[] rst = repositoryService.update(service);
          reference = rst[0].getDestination();

          if (Logger.isDebugEnabled()) {
            String fullDir = "";
            for (int j = 0; j < i; j++) {
              fullDir += "/" + dirs[j];
            }
            fullDir += "/" + dir;
            Logger.debug(credentials.getUsername(), this, "validatePath", "DMS folder created: '" + fullDir + "'");
          }
        }

        if (reference == null) {
          parentReference = null;
        } else {
          parentReference = new ParentReference(store, reference.getUuid(), null, Constants.ASSOC_CONTAINS, Constants
              .createQNameString(Constants.NAMESPACE_CONTENT_MODEL, Constants.PROP_NAME));
        }
      }
    }
    return parentReference;
  }

  private ContentFormat getContentFormat(DMSDocument doc) {
    String ext = FilenameUtils.getExtension(doc.getFileName()).toLowerCase();
    String contType = mimeTypes.get(ext);
    if (StringUtils.isEmpty(contType)) {
      contType = Constants.MIMETYPE_TEXT_PLAIN;
    }

    return new ContentFormat(contType, "UTF-8");
  }

  private boolean contains(Object[] list, Object item) {
    boolean retObj = false;
    for (Object node : list) {
      if (node != null && node.equals(item)) {
        retObj = true;
        break;
      }
    }
    return retObj;
  }

  private Date formatToDate(String value) throws ParseException {
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    String[] strArr = value.split("\\.");
    if (strArr.length == 2) {
      value = strArr[0];
      value += "." + strArr[1].replace("Z", "+00:00");
      value = value.substring(0, value.length() - 3) + value.substring(value.length() - 2, value.length());
    }
    return formatter.parse(value);
  }
  
  private ContentResult validate(ContentResult contentResult) throws RepositoryFault, RemoteException, ParseException {
    if (StringUtils.isNotEmpty(contentResult.getId()) && StringUtils.isNotEmpty(contentResult.getScheme())
        && StringUtils.isNotEmpty(contentResult.getAddress()) && StringUtils.isEmpty(contentResult.getName())) {
      Reference reference = new Reference(new Store(contentResult.getScheme(), contentResult.getAddress()), contentResult.getId(),
          null);
      Predicate predicate = new Predicate(new Reference[] { reference }, reference.getStore(), null);
      Node[] nodes = WebServiceFactory.getRepositoryService().get(predicate);
      if (nodes != null && nodes.length > 0) {
        Node node = nodes[0];
        for (NamedValue namedValue : node.getProperties()) {
          if (namedValue.getName().endsWith(Constants.PROP_CREATED)) {
            contentResult.setCreateDate(contentResult.getCreateDate() == null ? formatToDate(namedValue.getValue()) : contentResult.getCreateDate());
          } else if (namedValue.getName().endsWith(Constants.createQNameString(Constants.NAMESPACE_CONTENT_MODEL, "modified"))) {
            contentResult.setModifiedDate(contentResult.getModifiedDate() == null ? formatToDate(namedValue.getValue()) : contentResult.getModifiedDate());
          } else if (namedValue.getName().endsWith(Constants.PROP_NAME)) {
            contentResult.setName(StringUtils.isEmpty(contentResult.getName()) ? namedValue.getValue() : contentResult.getName());
          } else if (namedValue.getName().endsWith(Constants.PROP_DESCRIPTION)) {
            contentResult.setDescription(StringUtils.isEmpty(contentResult.getDescription()) ? namedValue.getValue()
                : contentResult.getDescription());
          } else if (namedValue.getName().endsWith(Constants.PROP_CONTENT)) {
            if (!contentResult.isLeaf()) {
              String contentString = namedValue.getValue();
              String[] values = contentString.split("[|=]");
              contentResult.setUrl(values[1]);
            }
          } else if (namedValue.getName().endsWith(Constants.PROP_TITLE)) {
            contentResult.setTitle(StringUtils.isEmpty(contentResult.getTitle()) ? namedValue.getValue() : contentResult.getTitle());
          } else if (namedValue.getName().endsWith(Constants.createQNameString(Constants.NAMESPACE_CONTENT_MODEL, AUTHOR))) {
            contentResult.setAuthor(StringUtils.isEmpty(contentResult.getAuthor()) ? namedValue.getValue() : contentResult.getAuthor());
          } else {
            contentResult.addComment(namedValue.getName(), namedValue.getValue());
          }
        }
      }
    }
    return contentResult;
  }
}
