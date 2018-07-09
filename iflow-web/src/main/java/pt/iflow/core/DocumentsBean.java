package pt.iflow.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import pt.iflow.api.connectors.DMSConnectorUtils;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.db.DBQueryManager;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.documents.DMSDocumentIdentifier;
import pt.iflow.api.documents.DocumentData;
import pt.iflow.api.documents.DocumentIdentifier;
import pt.iflow.api.documents.Documents;
import pt.iflow.api.documents.IFlowDocumentIdentifier;
import pt.iflow.api.documents.NullDocumentIdentifier;
import pt.iflow.api.flows.Flow;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessListVariable;
import pt.iflow.api.transition.FlowRolesTO;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.connector.alfresco.AlfrescoDocument;
import pt.iflow.connector.credentials.DMSCredential;
import pt.iflow.connector.dms.DMSUtils;
import pt.iflow.connector.document.DMSDocument;
import pt.iflow.connector.document.Document;
import pt.iflow.documents.AppendDocuments;
import pt.iknow.utils.StringUtilities;

/**
 * 
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright (c) 2005 iKnow
 * </p>
 * 
 * @author campa
 * @version 1.0
 * 
 * @author iKnow
 * 
 */
public class DocumentsBean implements Documents {

  static final int STREAM_SIZE = 8096;
  static DocumentsBean instance = null;

  static String docsBaseUrl = null;
  static boolean docDataInDB = true;

  DocumentsBean() {
	    //Verifica se existe URL absoluto
	    docsBaseUrl = Const.DOCS_BASE_URL;
	    if (StringUtilities.isNotEmpty(docsBaseUrl)) {
	      docDataInDB = false;
	      File f = new File(docsBaseUrl);
	      if (!f.isDirectory()) {
	        //Verifica se existe URL relativo
	        docsBaseUrl = FilenameUtils.concat(Const.sIFLOW_HOME, Const.DOCS_BASE_URL);
	        f = new File(docsBaseUrl);
	        if (!f.isDirectory()) {
	          //tenta criar URL relativo
	          try {
	            FileUtils.forceMkdir(f);
	          } catch (Exception e) {
	            //tenta criar URL absoluto
	            try {
	              docsBaseUrl = Const.DOCS_BASE_URL;
	              f = new File(docsBaseUrl);
	              FileUtils.forceMkdir(f);
	            } catch (Exception ex) {
	              Logger.error("", "DocumentsBean", "static", "O URL : '" + Const.DOCS_BASE_URL + "' n�o corresponde a uma pasta.");
	              docDataInDB = true;
	              docsBaseUrl = null;
	            }
	          }
	        }
	      }
	    }
	  }

  public static DocumentsBean getInstance() {
    if (null == instance)
      instance = new DocumentsBean();
    return instance;
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iflow.api.documents.Documents#addDocument(pt.iflow.api.utils.UserInfoInterface, pt.iflow.api.processdata.ProcessData,
   * pt.iflow.connector.document.Document)
   */
  public Document addDocument(UserInfoInterface userInfo, ProcessData procData, Document doc) {
    Connection db = null;
    ResultSet rs = null;
    PreparedStatement pst = null;
    try {
      db = DatabaseInterface.getConnection(userInfo);
      db.setAutoCommit(false);
      // if it is remote document, prepare DB
      if (!(doc instanceof DMSDocument)) {
        doc = this.addDocument(userInfo, procData, doc, db);
      } else {
        // add/update local document data
        StringBuffer query = new StringBuffer();
        query.append("SELECT dmsid, docid, uuid FROM external_dms");
        query.append(" WHERE docid=? OR uuid=?");
        pst = db.prepareStatement(query.toString());
        pst.setInt(1, doc.getDocId());
        pst.setString(2, ((DMSDocument) doc).getUuid());
        if (Logger.isDebugEnabled()) {
          Logger.debug(userInfo.getUtilizador(), this, "addDocument", "QUERY=" + query.toString());
        }
        rs = pst.executeQuery();
        boolean found = rs.next();
        int dmsid;
        if (found) {
          dmsid = rs.getInt("dmsid");
          doc.setDocId(rs.getInt("docid"));
          ((DMSDocument) doc).setUuid(rs.getString("uuid"));
          doc = this.updateDocument(userInfo, procData, doc, db, true);
        } else {
          dmsid = 1;
          doc = this.addDocument(userInfo, procData, doc, db);
          query = new StringBuffer();
          query.append("SELECT max(dmsid) AS dmsid FROM external_dms");
          if (Logger.isDebugEnabled()) {
            Logger.debug(userInfo.getUtilizador(), this, "addDocument", "QUERY=" + query.toString());
          }
          pst = db.prepareStatement(query.toString());
          rs = pst.executeQuery();
          if (rs.next()) {
            dmsid += rs.getInt("dmsid");
          }
        }
        DatabaseInterface.closeResources(pst, rs);
        if (doc.getDocId() > 0) {
          this.persistData(userInfo, procData, db, doc, dmsid, found);
        }
      }
      DatabaseInterface.commitConnection(db);
      Logger.debug(userInfo.getUtilizador(), this, "addDocument", procData.getSignature() + "connection commit");
    } catch (Exception e) {
      try {
        DatabaseInterface.rollbackConnection(db);
      } catch (SQLException e1) {
        Logger.error(userInfo.getUtilizador(), this, "addDocument", procData.getSignature() + "error rolling back", e1);
      }
      Logger.error(userInfo.getUtilizador(), this, "addDocument", procData.getSignature()
          + "Error inserting new document into database.", e);
      doc.setDocId(-1);
      if (doc instanceof DocumentData) {
        ((DocumentData) doc).setUpdated(null);
      }
    } finally {
      DatabaseInterface.closeResources(db, pst, rs);
    }
    return doc;
  }

  private Document persistData(UserInfoInterface userInfo, ProcessData procData, Connection db, Document doc, int dmsid,
      boolean found) throws SQLException {
    DMSDocument dmsDoc = (DMSDocument) doc;
    dmsDoc.setDocId(doc.getDocId());
    doc = dmsDoc;
    PreparedStatement pst = null;
    ResultSet rs = null;
    StringBuffer query = new StringBuffer();
    if (found) {
      query.append("UPDATE external_dms");
      query.append(" SET docid=?, uuid=?, scheme=?, address=?, path=?");
      query.append(" WHERE dmsid=?");
      pst = db.prepareStatement(query.toString());
      pst.setInt(1, doc.getDocId());
      pst.setString(2, dmsDoc.getUuid());
      pst.setString(3, dmsDoc.getScheme());
      pst.setString(4, dmsDoc.getAddress());
      pst.setString(5, dmsDoc.getPath());
      pst.setInt(6, dmsid);
    } else {
      query.append("INSERT INTO external_dms");
      query.append("(dmsid,docid,uuid,scheme,address,path)");
      query.append("VALUES (?,?,?,?,?,?)");
      pst = db.prepareStatement(query.toString());
      pst.setInt(1, dmsid);
      pst.setInt(2, doc.getDocId());
      pst.setString(3, dmsDoc.getUuid());
      pst.setString(4, dmsDoc.getScheme());
      pst.setString(5, dmsDoc.getAddress());
      pst.setString(6, dmsDoc.getPath());
    }
    if (Logger.isDebugEnabled()) {
      Logger.debug(userInfo.getUtilizador(), this, "addDocument", "QUERY=" + query.toString());
    }
    pst.executeUpdate();
    DatabaseInterface.closeResources(pst);
    for (String key : dmsDoc.getComments().keySet()) {
      boolean isInsert = !found;
      if (!isInsert) {
        // if document exists, check if each property is new or needs updating
        query = new StringBuffer();
        query.append("SELECT dmsid, name, value");
        query.append(" FROM external_dms_properties");
        query.append(" WHERE dmsid=? AND name=?");
        pst = db.prepareStatement(query.toString());
        pst.setInt(1, dmsid);
        pst.setString(2, key);
        rs = pst.executeQuery();
        isInsert = !rs.next();
        DatabaseInterface.closeResources(pst, rs);
      }
      if (isInsert) {
        query = new StringBuffer();
        query.append("INSERT INTO external_dms_properties");
        query.append("(dmsid,name,value)");
        query.append("VALUES (?,?,?)");
        pst = db.prepareStatement(query.toString());
        pst.setInt(1, dmsid);
        pst.setString(2, key);
        pst.setString(3, dmsDoc.getComments().get(key));
      } else {
        query = new StringBuffer();
        query.append("UPDATE external_dms_properties");
        query.append(" SET value=?");
        query.append(" WHERE dmsid=? AND name=?");
        pst = db.prepareStatement(query.toString());
        pst.setString(1, dmsDoc.getComments().get(key));
        pst.setInt(2, dmsid);
        pst.setString(3, key);
      }
      if (Logger.isDebugEnabled()) {
        Logger.debug(userInfo.getUtilizador(), this, "addDocument", "QUERY=" + query.toString());
      }
      pst.executeUpdate();
      DatabaseInterface.closeResources(pst);
    }
    DatabaseInterface.closeResources(pst, rs);
    return doc;
  }

  Document addDocument(UserInfoInterface userInfo, ProcessData procData, Document adoc, Connection db) throws Exception {

    if (null == userInfo) {
      Logger.error(null, this, "addDocument", "Invalid user");
      adoc.setDocId(-1);
      if (adoc instanceof DocumentData) {
        ((DocumentData) adoc).setUpdated(null);
      }
      return adoc;
    }

    if (null == procData) {
      Logger.error(userInfo.getUtilizador(), this, "addDocument", "Invalid process");
      adoc.setDocId(-1);
      if (adoc instanceof DocumentData) {
        ((DocumentData) adoc).setUpdated(null);
      }
      return adoc;
    }

    if (!canCreate(userInfo, procData, adoc)) {
      Logger.error(userInfo.getUtilizador(), this, "updateDocument", procData.getSignature()
          + "User not authorized to update file.");
      return adoc;
    }

    int ret = -1;

    ResultSet rs = null;
    PreparedStatement pst = null;
    final String[] generatedKeyNames = new String[] { "docid" };

    try {
      Date dateNow = new Date();
      adoc.setFileName((new String(adoc.getFileName().getBytes(),"UTF-8")).replace("?", ""));
      String query = DBQueryManager.getQuery("Documents.ADD_DOCUMENT");            
      // Obtencao de chaves geradas automaticamente. ver
      // http://java-x.blogspot.com/2006/09/oracle-jdbc-automatic-key-generation.html
      pst = db.prepareStatement(query, generatedKeyNames);

      pst.setString(1, adoc.getFileName());
      String filePath = null;
      if (docDataInDB) {
        ByteArrayInputStream isBody = new ByteArrayInputStream(adoc.getContent());
        pst.setBinaryStream(2, isBody, adoc.getContent().length);
      } else {
        pst.setBinaryStream(2, null, 0);
      }
      pst.setInt(3, procData.getFlowId());
      pst.setInt(4, procData.getPid());
      pst.setInt(5, procData.getSubPid());

      try{
    	  pst.executeUpdate();
      } catch( Exception e){
    	  //Extra try due to the possibility of ummappable characters 
          String aux = new String(adoc.getFileName().getBytes(),"UTF-8");
          aux = aux.replace("?", "");
          adoc.setFileName(aux);
          pst.setString(1, adoc.getFileName());
          if (docDataInDB) {
	          ByteArrayInputStream isBody = new ByteArrayInputStream(adoc.getContent());
	          pst.setBinaryStream(2, isBody, adoc.getContent().length);
	        } else {
	          pst.setBinaryStream(2, null, 0);
	        }	
          pst.executeUpdate();
      }
      rs = pst.getGeneratedKeys(); // obtem as chaves geradas automaticamente para o campo docid
      if (rs.next()) {
        ret = rs.getInt(1);
      }

      adoc.setDocId(ret);

      if (!docDataInDB && ((filePath = getDocumentFilePath(adoc.getDocId(), adoc.getFileName())) != null)) {
        try
        {
          DatabaseInterface.closeResources(pst);
          query = DBQueryManager.getQuery("Documents.UPDATE_DOCUMENT_DOCURL");
          pst = db.prepareStatement(query, generatedKeyNames);
          pst.setString(1, filePath);
          pst.setInt(2, adoc.getDocId());
          pst.executeUpdate();
          Logger.debug(userInfo.getUtilizador(), this, "addDocument", "new FileOutputStream:" + filePath);
          OutputStream fos = Files.newOutputStream(Paths.get(filePath), StandardOpenOption.CREATE_NEW);// new FileOutputStream(filePath);
          fos.write(adoc.getContent());
          fos.close();
        } catch(FileNotFoundException ex) {
          Logger.error(userInfo.getUtilizador(), this, "addDocument", procData.getSignature() + " File not Found.", ex);
        } catch(IOException ioe) {
          Logger.error(userInfo.getUtilizador(), this, "addDocument", procData.getSignature() + " IOException.", ioe);
        }
      }

      if (adoc instanceof DocumentData) {
        ((DocumentData) adoc).setUpdated(dateNow);
      }

    } catch (Exception e) {
      Logger.error(userInfo.getUtilizador(), this, "addDocument", procData.getSignature()
          + "Error inserting new document into database.", e);
      adoc.setDocId(-1);
      if (adoc instanceof DocumentData) {
        ((DocumentData) adoc).setUpdated(null);
      }
    } finally {
      DatabaseInterface.closeResources(pst, rs);
    }

    return adoc;
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iflow.api.documents.Documents#updateDocument(pt.iflow.api.utils.UserInfoInterface,
   * pt.iflow.api.processdata.ProcessData, pt.iflow.connector.document.Document)
   */
  public Document updateDocument(UserInfoInterface userInfo, ProcessData procData, Document adoc) {
    Connection db = null;
    try {
      db = DatabaseInterface.getConnection(userInfo);
      adoc = updateDocument(userInfo, procData, adoc, db, true);
    } catch (Exception e) {
      try {
        DatabaseInterface.rollbackConnection(db);
      } catch (Exception e2) {
        Logger.error(userInfo.getUtilizador(), this, "updateDocument", procData.getSignature() + "Error rolling back.", e2);
      }
      Logger.error(userInfo.getUtilizador(), this, "updateDocument", procData.getSignature() + "Error updating existing document.",
          e);
      adoc.setFileName(null);
      if (adoc instanceof DocumentData) {
        ((DocumentData) adoc).setUpdated(null);
      }
      adoc.setContent(new byte[] {});
    } finally {
      //DatabaseInterface.closeResources(db);
    	try { db.close(); } catch (SQLException e) {}
    }
    return adoc;
  }

  Document updateDocument(UserInfoInterface userInfo, ProcessData procData, Document adoc, Connection db, boolean updateContents) throws Exception {
    PreparedStatement pst = null;
    ResultSet rs = null;
    try {
      db.setAutoCommit(false);
      Document dbDoc = getDocumentFromDB(db, adoc.getDocId());
      if (!canUpdate(userInfo, procData, dbDoc)) {
        Logger.error(userInfo.getUtilizador(), this, "updateDocument", procData.getSignature()
            + "User not authorized to update file.");
        throw new Exception("Permission denied");
      }
      Date dateNow = new Date();
      String query = null;
      if(updateContents)
        query = DBQueryManager.getQuery("Documents.UPDATE_DOCUMENT");
      else
        query = DBQueryManager.getQuery("Documents.UPDATE_DOCUMENT_INFO");
      pst = db.prepareStatement(query);
      int pos = 0;
      pst.setString(++pos, adoc.getFileName());
      if(updateContents) {
        String filePath = null;
        if (docDataInDB || ((filePath = getDocumentFilePath(adoc.getDocId(), adoc.getFileName())) == null)) {
          ByteArrayInputStream isBody = new ByteArrayInputStream(adoc.getContent());
          pst.setBinaryStream(++pos, isBody, adoc.getContent().length);
          pst.setString(++pos, null);
        } else {
          String folderPath = getDocumentFilePath(adoc.getDocId(), "");
          File f = new File(folderPath);
          File[] fs = f.listFiles();
          for (int i=0; fs!=null && i<fs.length; i++) fs[i].delete();
          pst.setBinaryStream(++pos, null, 0);
          pst.setString(++pos, filePath);
          try
          {
            FileOutputStream fos = new FileOutputStream(filePath);
            fos.write(adoc.getContent());
            fos.close();
          } catch(FileNotFoundException ex) {
            Logger.error(userInfo.getUtilizador(), this, "addDocument", procData.getSignature() + " File not Found.", ex);
          } catch(IOException ioe) {
            Logger.error(userInfo.getUtilizador(), this, "addDocument", procData.getSignature() + " IOException.", ioe);
          }
        }
      }
      pst.setTimestamp(++pos, new java.sql.Timestamp(dateNow.getTime()));
      pst.setInt(++pos, adoc.getDocId());
      pst.executeUpdate();
      if (adoc instanceof DocumentData) {
        ((DocumentData) adoc).setUpdated(dateNow);
      }
      DatabaseInterface.commitConnection(db);
    } finally {
      DatabaseInterface.closeResources(pst, rs);
    }

    return adoc;
  }

  // To preserve the current transaction...
  protected Document getDocumentFromDB(Connection db, int docid) throws SQLException {
    PreparedStatement pst = null;
    DocumentData dbDoc = new DocumentData();
    ResultSet rs = null;
    pst = db.prepareStatement(DBQueryManager.processQuery("Documents.GET_DOCUMENT", new Object[] { "", "" }));
    try {
      pst.setInt(1, docid);
      rs = pst.executeQuery();
      if (rs.next()) {
        dbDoc.setDocId(rs.getInt("docid"));
        dbDoc.setFlowid(rs.getInt("flowid"));
        dbDoc.setPid(rs.getInt("pid"));
        dbDoc.setSubpid(rs.getInt("subpid"));
        String docURL = rs.getString("docurl");
        if (StringUtilities.isNotEmpty(docURL)) {
          String fileName = rs.getString("filename");
          File f = new File(getDocumentFilePath(dbDoc.getDocId(), fileName));
          dbDoc.setLength((int)f.length());
        } else {
          dbDoc.setLength(rs.getInt("length"));
        }
      } else {
        throw new Exception("File not found.");
      }
    } catch (SQLException e) {
      // Notify caller....
      throw e;
    } catch (Exception e) {
      dbDoc = null;
    } finally {
      DatabaseInterface.closeResources(pst, rs);
    }
    return dbDoc;
  }

  public Document getDocumentInfo(UserInfoInterface userInfo, ProcessData procData, int docid) {
    return this.getDocumentInfo(userInfo, procData, new DocumentData(docid, null));
  }

  public Document getDocumentInfo(UserInfoInterface userInfo, ProcessData procData, DocumentIdentifier docid) {
    if (docid instanceof IFlowDocumentIdentifier) {
      return getDocumentInfo(userInfo, procData, ((IFlowDocumentIdentifier) docid).getIntId());
    }
    return getDocument(userInfo, procData, docid);
  }

  public Document getDocument(UserInfoInterface userInfo, ProcessData procData, int aDocId, String asFileName) {
    return this.getDocument(userInfo, procData, new DocumentData(aDocId, asFileName));
  }

  public Document getDocument(UserInfoInterface userInfo, ProcessData procData, Document adoc) {
    return this.getDocument(userInfo, procData, adoc, true);
  }

  public Document getDocument(UserInfoInterface userInfo, ProcessData procData, DocumentIdentifier docid) {

    if (docid instanceof NullDocumentIdentifier) {
      return null;
    } else if (docid instanceof IFlowDocumentIdentifier) {
      return getDocument(userInfo, procData, ((IFlowDocumentIdentifier) docid).getIntId());
    }

    DMSCredential cred = DMSConnectorUtils.createCredential(userInfo, procData);
    Map<String, String> props = new HashMap<String, String>();
    props.put("id", docid.getId());
    try {
      return DMSUtils.getInstance().getDocument(cred, props);
    } catch (Exception e) {
      Logger.error(userInfo.getUtilizador(), this, "getDocument", procData.getSignature() + "Error retrieving dms document "
          + docid.getId(), e);
    }

    return null;
  }

  public Document getDocumentAuth(UserInfoInterface userInfo, ProcessData procData, DocumentIdentifier docid, String user, String pass) {

    if (docid instanceof NullDocumentIdentifier) {
      return null;
    } else if (docid instanceof IFlowDocumentIdentifier) {
      return getDocument(userInfo, procData, ((IFlowDocumentIdentifier) docid).getIntId());
    }

    DMSCredential cred = DMSConnectorUtils.createCredentialAuth(user, pass);
    Map<String, String> props = new HashMap<String, String>();
    props.put("id", docid.getId());
    try {
      return DMSUtils.getInstance().getDocument(cred, props);
    } catch (Exception e) {
      Logger.error(userInfo.getUtilizador(), this, "getDocument", procData.getSignature() + "Error retrieving dms document "
          + docid.getId(), e);
    }

    return null;
  }

  public Document getDocument(UserInfoInterface userInfo, ProcessData procData, int docid) {
    return this.getDocument(userInfo, procData, docid, null);
  }

  public Document getDocumentInfo(UserInfoInterface userInfo, ProcessData procData, Document adoc) {
    return this.getDocument(userInfo, procData, adoc, false);
  }

  public Document getDocument(UserInfoInterface userInfo, ProcessData procData, Document adoc, boolean abFull) {
    Document retObj = adoc;
    final String login = (null != userInfo) ? userInfo.getUtilizador() : "<none>";
    Connection db = null;
    try {
      db = DatabaseInterface.getConnection(userInfo);
      retObj = getDocument(userInfo, procData, adoc, db, abFull);
    } catch (Exception e) {
      Logger.error(login, this, "getDocument", procData.getSignature() + "Error retrieving document from database.", e);
    } finally {
      DatabaseInterface.closeResources(db);
    }
    return retObj;
  }

  private Document getDocument(UserInfoInterface userInfo, ProcessData procData, Document adoc, Connection db, boolean abFull) {
    Document retObj = null;
    final String login = (null != userInfo) ? userInfo.getUtilizador() : "<none>";
    Statement st = null;
    ResultSet rs = null;
    try {
      retObj = this.getDocumentData(userInfo, procData, adoc, db, abFull);
      if (retObj != null) {
        StringBuffer query = new StringBuffer();
        query.append("SELECT * FROM external_dms");
        query.append(" WHERE docid=" + retObj.getDocId());
        st = db.createStatement();
        rs = st.executeQuery(query.toString());
        int dmsid = -1;
        if (rs.next()) {
          dmsid = rs.getInt("dmsid");
          String scheme = rs.getString("scheme");
          String address = rs.getString("address");
          String uuid = rs.getString("uuid");
          String path = rs.getString("path");
          // convert the object from DocumentData do DMSDocument
          Document doc = retObj;
          retObj = new AlfrescoDocument(scheme, address, uuid, path);
          ((DMSDocument) retObj).setDocId(doc.getDocId());
          ((DMSDocument) retObj).setFileName(doc.getFileName());
          ((DMSDocument) retObj).setContent(doc.getContent());
        }
        DatabaseInterface.closeResources(st, rs);
        if (retObj != null && retObj instanceof DMSDocument) {
          query = new StringBuffer();
          query.append("SELECT * FROM external_dms_properties");
          query.append(" WHERE dmsid=" + dmsid);
          st = db.createStatement();
          rs = st.executeQuery(query.toString());
          while (rs.next()) {
            String name = rs.getString("name");
            String value = rs.getString("value");
            ((DMSDocument) retObj).addComment(name, value);
          }
        }
      }
    } catch (Exception e) {
      Logger.error(login, this, "getDocument", procData.getSignature() + "Error retrieving document from database.", e);
    } finally {
      DatabaseInterface.closeResources(st, rs);
    }
    if(retObj == null) 
    	return new DocumentData(0, "");
    else
    	return retObj;
  }

  Document getDocumentData(UserInfoInterface userInfo, ProcessData procData, Document adoc, Connection db, boolean abFull) {
    DocumentData retObj = null;
    if (adoc instanceof DocumentData) {
      retObj = (DocumentData) adoc;
    } else if (adoc instanceof DMSDocument) {
      if (!this.isLocked(userInfo, procData, adoc.getDocId())) {
        adoc = this.getDocument(userInfo, procData, adoc);
      }
      retObj = new DocumentData(adoc.getDocId(), adoc.getFileName());
    } else {
      retObj = new DocumentData(adoc.getDocId(), adoc.getFileName());
    }
    final String login = (null != userInfo) ? userInfo.getUtilizador() : "<none>";

    PreparedStatement st = null;
    ResultSet rs = null;
    InputStream dataStream = null;
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    // reset datadoc
    retObj.setContent(new byte[] {});

    try {
      String fileName = retObj.getFileName();

      String[] params = new String[] { "", "" };
      if (abFull)
        params[0] = ",datadoc,docurl";
      if (StringUtils.isNotEmpty(fileName))
        params[1] = "and filename=?";

      String query = DBQueryManager.processQuery("Documents.GET_DOCUMENT", (Object[]) params);

      st = db.prepareStatement(query);
      st.setInt(1, retObj.getDocId());
      if (StringUtils.isNotEmpty(fileName))
        st.setString(2, fileName);

      rs = st.executeQuery();

      if (rs.next()) {

        String sFilename = rs.getString("filename");
        Date dtUpdated = rs.getTimestamp("updated");
        int flowid = rs.getInt("flowid");
        int pid = rs.getInt("pid");
        int subpid = rs.getInt("subpid");
        int length = rs.getInt("length");

        String filePath = rs.getString("docurl");
        if (StringUtils.isNotEmpty(filePath)) {
        	retObj.setDocurl(filePath);
            File f = new File(filePath);
            length = (int)f.length();
        }

        retObj.setFileName(sFilename);
        retObj.setUpdated(dtUpdated);
        retObj.setFlowid(flowid);
        retObj.setPid(pid);
        retObj.setSubpid(subpid);
        retObj.setLength(length);

        if (!canRead(userInfo, procData, adoc)) {
          retObj = null;
          Logger.error(login, this, "getDocument", procData.getSignature() + "User does not have permission to retrieve file");
          throw new Exception("Permission denied");
        }

        if (abFull) {
          if (StringUtils.isNotEmpty(filePath)) {
              dataStream = new FileInputStream(filePath);
          } else {
            dataStream = rs.getBinaryStream("datadoc");
          }
          if (null != dataStream) {
            byte[] r = new byte[STREAM_SIZE];
            int j = 0;
            while ((j = dataStream.read(r, 0, STREAM_SIZE)) != -1)
              baos.write(r, 0, j);
            dataStream.close();
          }
          baos.flush();
          baos.close();
          retObj.setContent(baos.toByteArray());
        }
      } else {
        retObj = null;
        Logger.warning(login, this, "getDocument", procData.getSignature() + "Document not found.");
      }

    } catch (Exception e) {
      Logger.error(login, this, "getDocument", procData.getSignature() + "Error retrieving document from database.", e);
    } finally {
      try {
        if (dataStream != null)
          dataStream.close();
      } catch (Exception e) {
      }
      DatabaseInterface.closeResources(st, rs);
    }
    return retObj;
  }

  private boolean isDMSDocument(UserInfoInterface userInfo, ProcessData procData, int docid) {
    Document doc = getDocument(userInfo, procData, docid);
    return (doc instanceof DMSDocument) && StringUtils.isNotBlank(((DMSDocument) doc).getUuid());
  }

  protected boolean isLocked(UserInfoInterface userInfo, ProcessData procData, int docid) {
    if (isDMSDocument(userInfo, procData, docid)) {
      try {
        return DMSUtils.getInstance().isLocked(DMSConnectorUtils.createCredential(userInfo, procData),
            this.getDocument(userInfo, procData, docid));
      } catch (Exception e) {
        Logger.error(userInfo.getUtilizador(), this, "isLocked", "Exception caught: ", e);
      }
    }
    return false;
  }

  protected boolean canCreate(UserInfoInterface userInfo, ProcessData procData, Document adoc) {
	if (Const.DISABLE_DOCS_PERMISSIONS)
	  return true;
    if (null == userInfo) {
      Logger.debug("<unknown>", this, "canCreate", "Invalid user");
      return false; // invalid user
    }
    if (null == procData) {
      Logger.debug(userInfo.getUtilizador(), this, "canCreate", "Invalid process");
      return false; // invalid process
    }
    if (null == adoc) {
      Logger.debug(userInfo.getUtilizador(), this, "canCreate", "Invalid document");
      return false; // invalid document
    }

    Flow flowBean = BeanFactory.getFlowBean();

    boolean canWrite = flowBean.checkUserFlowRoles(userInfo, procData.getFlowId(), "" + FlowRolesTO.WRITE_PRIV);
    if (!canWrite) {
      Logger.debug(userInfo.getUtilizador(), this, "canCreate", "User cant update the process.");
      return false;
    }

    Logger.debug(userInfo.getUtilizador(), this, "canCreate", "Access granted!");
    return true;
  }

  protected boolean canUpdate(UserInfoInterface userInfo, ProcessData procData, Document adoc) {
	if (Const.DISABLE_DOCS_PERMISSIONS)
		return true;
    if (null == userInfo) {
      Logger.warning("<unknown>", this, "canUpdate", "Invalid user");
      return false; // invalid user
    }
    if (null == procData) {
      Logger.warning(userInfo.getUtilizador(), this, "canUpdate", "Invalid process");
      return false; // invalid process
    }
    if (null == adoc) {
      Logger.warning(userInfo.getUtilizador(), this, "canUpdate", "Invalid document");
      return false; // invalid document
    }

    Flow flowBean = BeanFactory.getFlowBean();

    if (adoc instanceof DocumentData) {
      boolean canWrite = flowBean.checkUserFlowRoles(userInfo, ((DocumentData) adoc).getFlowid(), "" + FlowRolesTO.WRITE_PRIV);
      if (!canWrite) {
        Logger.debug(userInfo.getUtilizador(), this, "canUpdate", "User cant update the documents process.");
        return false;
      }
    }

    Logger.debug(userInfo.getUtilizador(), this, "canUpdate", "Access granted!");
    return true;
  }

  protected boolean canRead(UserInfoInterface userInfo, ProcessData procData, Document adoc) {
	if (Const.DISABLE_DOCS_PERMISSIONS)
	  return true;
    if (null == userInfo) {
      Logger.debug("<unknown>", this, "canRead", "Invalid user");
      return false; // invalid user
    }
    int flowid = -1;
    if (procData != null)
      flowid = procData.getFlowId();
    else if (adoc instanceof DocumentData) 
      flowid = ((DocumentData)adoc).getFlowid();
    String sUseDocHash = BeanFactory.getFlowSettingsBean().getFlowSetting(flowid, Const.sHASHED_DOCUMENT_URL)
    .getValue();
    boolean useDocHash = StringUtils.equalsIgnoreCase(Const.sHASHED_DOCUMENT_URL_YES, sUseDocHash);

    if (!useDocHash && procData == null) {
      Logger.debug(userInfo.getUtilizador(), this, "canRead", "Invalid process");
      return false; // invalid process
    }
    if (null == adoc) {
      Logger.debug(userInfo.getUtilizador(), this, "canRead", "Invalid document");
      return false; // invalid document
    }

    Flow flowBean = BeanFactory.getFlowBean();

    if (adoc instanceof DocumentData) {
      boolean canRead = flowBean.checkUserFlowRoles(userInfo, ((DocumentData) adoc).getFlowid(), "" + FlowRolesTO.READ_PRIV)
      || flowBean.checkUserFlowRoles(userInfo, ((DocumentData) adoc).getFlowid(), "" + FlowRolesTO.SUPERUSER_PRIV)
      || (flowBean.checkUserFlowRoles(userInfo, ((DocumentData) adoc).getFlowid(), "" + FlowRolesTO.WRITE_PRIV) && true /*
       * must
       * be
       * task
       * owner
       */);
      if (!canRead) {
        Logger.debug(userInfo.getUtilizador(), this, "canRead", "User cannot read documents process.");
        return false;
      }
    }

    Logger.debug(userInfo.getUtilizador(), this, "canRead", "Access granted!");
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iflow.api.documents.Documents#removeDocument(pt.iflow.api.utils.UserInfoInterface, pt.iflow.api.processdata.ProcessData, pt.iflow.api.documents.DocumentIdentifier)
   */
  public boolean removeDocument(UserInfoInterface userInfo, ProcessData procData, DocumentIdentifier docid) {
    try {
      if (docid instanceof NullDocumentIdentifier) {
        return false;
      } else if (docid instanceof IFlowDocumentIdentifier) {
        return this.removeDocument(userInfo, procData, ((IFlowDocumentIdentifier) docid).getIntId());
      } else if (docid instanceof DMSDocumentIdentifier) {
        DMSCredential cred = DMSConnectorUtils.createCredential(userInfo, procData);
        return DMSUtils.getInstance().removeDocument(cred, ((DMSDocumentIdentifier) docid).getUUID());
      }
    } catch (Exception e) {
      Logger.error(userInfo.getUtilizador(), this, "getDocument", procData.getSignature() + "Error removing dms document "
          + docid.getId(), e);
    }
    return false;
  }

  public boolean removeDocumentAuth(UserInfoInterface userInfo, ProcessData procData, DocumentIdentifier docid, String user, String pass) {
    try {
      if (docid instanceof NullDocumentIdentifier) {
        return false;
      } else if (docid instanceof IFlowDocumentIdentifier) {
        return this.removeDocument(userInfo, procData, ((IFlowDocumentIdentifier) docid).getIntId());
      } else if (docid instanceof DMSDocumentIdentifier) {

        DMSCredential cred = DMSConnectorUtils.createCredentialAuth(user, pass);

        return DMSUtils.getInstance().removeDocument(cred, ((DMSDocumentIdentifier) docid).getUUID());
      }
    } catch (Exception e) {
      Logger.error(userInfo.getUtilizador(), this, "getDocument", procData.getSignature() + "Error removing dms document "
          + docid.getId(), e);
    }
    return false;
  }

  public boolean removeDocument(UserInfoInterface userInfo, ProcessData procData, int aDocId) {
    return this.removeDocument(userInfo, procData, new DocumentData(aDocId));
  }

  public boolean removeDocument(UserInfoInterface userInfo, ProcessData procData, Document adoc) {

    boolean ret = false;

    Connection db = null;
    PreparedStatement st = null;

    try {
      db = DatabaseInterface.getConnection(userInfo);
      db.setAutoCommit(false);

      Document dbDoc = getDocumentFromDB(db, adoc.getDocId());

      if (!canUpdate(userInfo, procData, dbDoc)) {
        Logger.error(userInfo.getUtilizador(), this, "removeDocument", procData.getSignature()
            + "User not authorized to remove file.");
        throw new Exception("Permission denied");
      }

      String query = DBQueryManager.getQuery("Documents.REMOVE_DOCUMENT");
      st = db.prepareStatement(query);
      st.setInt(1, adoc.getDocId());

      st.executeUpdate();

      if (!docDataInDB)
        (new File(getDocumentFilePath(adoc.getDocId(), adoc.getFileName()))).delete();

      st.close();
      st = null;

      DatabaseInterface.commitConnection(db);
      ret = true;
    } catch (Exception e) {
      Logger.error(userInfo.getUtilizador(), this, "removeDocument", procData.getSignature() + "Caught exception : "
          + e.getMessage(), e);
      try {
        //DatabaseInterface.rollbackConnection(db);
    	  db.close();
      } catch (Exception e2) {
        Logger.error(userInfo.getUtilizador(), this, "removeDocument", procData.getSignature() + "Exception rolling back: "
            + e2.getMessage(), e2);
      }
    } finally {
      DatabaseInterface.closeResources(db, st);
    }

    return ret;
  }

  public List<Document> getDocumentInfoList(UserInfoInterface userInfo, ProcessData procData) {

    Connection db = null;
    Statement st = null;
    ResultSet rs = null;
    List<Document> listDocTemp = new ArrayList<Document>();

    try {
      db = DatabaseInterface.getConnection(userInfo);
      st = db.createStatement();
      String query = DBQueryManager.getQuery("Documents.LIST_DOCUMENTS");

      rs = st.executeQuery(query);

      while (rs.next()) {
        int docIdTemp = rs.getInt("docid");
        String fileNameTemp = rs.getString("filename");
        Date updatedTemp = rs.getTimestamp("updated");
        int flowid = rs.getInt("flowid");
        int pid = rs.getInt("pid");
        int subpid = rs.getInt("subpid");
        int length = rs.getInt("length");

        String filePath = rs.getString("docurl");
        if (StringUtils.isNotEmpty(filePath)) {
            File f = new File(filePath);
            length = (int)f.length();
        }

        DocumentData docTemp = new DocumentData(docIdTemp, fileNameTemp, (byte[]) null, updatedTemp, flowid, pid, subpid);
        docTemp.setLength(length);
        if (canRead(userInfo, procData, docTemp))
          listDocTemp.add(docTemp);
      }

    } catch (Exception e) {
      Logger.error(userInfo.getUtilizador(), this, "getDocumentInfoList",
          procData.getSignature() + "Error fetching document list.", e);
    } finally {
      DatabaseInterface.closeResources(db, st, rs);
    }

    return listDocTemp;
  }

  public Document updateDocumentInfo(UserInfoInterface userInfo, ProcessData procData, Document adoc) {
    Connection db = null;
    try {
      db = DatabaseInterface.getConnection(userInfo);
      adoc = updateDocument(userInfo, procData, adoc, db, false);
    } catch (Exception e) {
      try {
        DatabaseInterface.rollbackConnection(db);
      } catch (Exception e2) {
        Logger.error(userInfo.getUtilizador(), this, "updateDocument", procData.getSignature() + "Error rolling back.", e2);
      }
      Logger.error(userInfo.getUtilizador(), this, "updateDocument", procData.getSignature() + "Error updating existing document.", e);
      adoc.setFileName(null);
      if (adoc instanceof DocumentData) {
        ((DocumentData) adoc).setUpdated(null);
      }
      adoc.setContent(new byte[] {});
    } finally {
      //DatabaseInterface.closeResources(db);
    	try { db.close(); } catch (SQLException e) {}
    }
    return adoc;
  }

  //File System
  private String getDocumentFilePath(int docID, String fileName) {
    String strDocIdUrl = "0000000000" + docID;
    strDocIdUrl = strDocIdUrl.substring(strDocIdUrl.length()-10, strDocIdUrl.length());
    String docIdUrl = strDocIdUrl.substring(0, 2) + "\\" + strDocIdUrl.substring(2, 4) + "\\" + 
                      strDocIdUrl.substring(4, 6) + "\\" + strDocIdUrl.substring(6, 8) + "\\" +
                      strDocIdUrl.substring(8, 10); 
    if (!docDataInDB) {
      String url = FilenameUtils.concat(docsBaseUrl, docIdUrl);
      try {
        File f = new File(url);
        if (!f.isDirectory()) FileUtils.forceMkdir(f);
        Path dir = Paths.get(url);
        Path path = dir.resolve(fileName);
        return path.toAbsolutePath().toString();
        //return FilenameUtils.concat(url,fileName);
      } catch (Exception e) {
      }
    }
    return null;
  }

  public byte[] mergePDFs(UserInfoInterface userInfo, ProcessData procData, String[] docsVar) {
    return AppendDocuments.mergePDFs(userInfo, procData, docsVar);
  }

  public boolean asSignatures(UserInfoInterface userInfo, int docid) {
    Connection db = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    int num = -1;

    try {
      db = DatabaseInterface.getConnection(userInfo);
      pst = db.prepareStatement("SELECT numass FROM documents where docid=?");
      pst.setInt(1, docid);
      rs = pst.executeQuery();   

      rs.next(); 
      num = rs.getInt(1);
    } 
    catch (SQLException e) {    return false; } 
    finally {  DatabaseInterface.closeResources(pst, rs, db);}

    if(num>0)
      return true;
    else
      return false;
  }
  
  public boolean isToSign(UserInfoInterface userInfo, int docid) {
    Connection db = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    int num = -1;

    try {
      db = DatabaseInterface.getConnection(userInfo);
      pst = db.prepareStatement("SELECT tosign FROM documents where docid=?");
      pst.setInt(1, docid);
      rs = pst.executeQuery();   

      rs.next(); 
      num = rs.getInt(1);
    } 
    catch (SQLException e) {    return false; } 
    finally {  DatabaseInterface.closeResources(pst, rs, db);}

    if(num == 1)
      return true;
    else
      return false;
  }
  
  public boolean markDocsToSign(UserInfoInterface userInfo, ProcessListVariable docs, ProcessListVariable values) {   
    Connection db = null;
    Statement st = null;
    String queryUpdate0 = "";
    String queryUpdate1 = "";
    boolean flag0 = false;
    boolean flag1 = false;
    
    for(int i = 0; i < docs.size(); i++){     
        if(values.getItem(i)!= null && values.getFormattedItem(i).equals("1"))
          queryUpdate1 += ", "+docs.getItem(i).format();      
        else
          queryUpdate0 += ", "+docs.getItem(i).format();
    }
    
    if(!StringUtils.isEmpty(queryUpdate0)){
      queryUpdate0 += " )";
      queryUpdate0 = queryUpdate0.replaceFirst(",", "(");
      flag0 = true;
    }
    
    if(!StringUtils.isEmpty(queryUpdate1)){
      queryUpdate1 += " )";
      queryUpdate1 = queryUpdate1.replaceFirst(",", "(");
      flag1 = true;
    }
    
    try {
      db = DatabaseInterface.getConnection(userInfo);
      if(flag0){
        st = db.createStatement();
        st.executeUpdate("UPDATE documents set tosign=0 where docid in "+queryUpdate0);
        st.close();
      }
      if(flag1){
        st = db.createStatement();
        st.executeUpdate("UPDATE documents set tosign=1 where docid in "+queryUpdate1);
      }
     } catch (SQLException sqle) {
          Logger.error(userInfo.getUtilizador(), this, "markDocsToSign","caught sql exception: " + sqle.getMessage(), sqle);
          return false;
     } finally {
          DatabaseInterface.closeResources(db, st);
     }
     Logger.debug(userInfo.getUtilizador(), this, "markDocsToSign", "Update to not sign "+queryUpdate0+" and to sign "+queryUpdate1);
     return true;
  }
  
  public String migrateDatabaseToFilesystem(UserInfoInterface userInfo, ProcessData procData, Integer docid) throws Exception{
	  Connection db = null;
	  PreparedStatement pst = null;
	  String result = null;
	  try{
		  db = DatabaseInterface.getConnection(userInfo);
		  db.setAutoCommit(false);
	      Document dbDoc = getDocumentFromDB(db, docid);
	      if (!canUpdate(userInfo, procData, dbDoc)) {
	    	  Logger.error(userInfo.getUtilizador(), this, "migrateDatabaseToFilesystem", procData.getSignature() + "User not authorized to update file.");
	    	  throw new Exception("Permission denied");
	      }
	      
	      dbDoc = getDocumentData(userInfo, procData, dbDoc, db , true);
	      String filePath = getDocumentFilePath(dbDoc.getDocId(), dbDoc.getFileName());
	      OutputStream fos = Files.newOutputStream(Paths.get(filePath));
          fos.write(dbDoc.getContent());
          fos.close();
          
          String query = DBQueryManager.getQuery("Documents.UPDATE_DOCUMENT_DOCURL");
          pst = db.prepareStatement(query);
          pst.setString(1, filePath);
          pst.setInt(2, docid);
          pst.executeUpdate();
          
          result = filePath;
	  } finally {
          //DatabaseInterface.closeResources(db, pst);
		  db.close();
		  pst.close();
	  }
	  return result;
  }
}
