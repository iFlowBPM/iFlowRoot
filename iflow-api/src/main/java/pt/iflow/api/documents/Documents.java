package pt.iflow.api.documents;

import java.util.List;

import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessListVariable;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.connector.document.Document;

public interface Documents {
  /**
   * Adds a document to local DB.
   * Checks for updates from DMS.
   * 
   * @param userInfo
   *          User information.
   * @param procData
   *          Process data.
   * @param adoc
   *          Document to add.
   * @return Document data added to DB.
   */
  public abstract Document addDocument(UserInfoInterface userInfo, ProcessData procData, Document adoc);

  /**
   * Updates document with new data.
   * Checks for updates from DMS.
   * 
   * @param userInfo
   *          User information.
   * @param procData
   *          Process data.
   * @param adoc
   *          Document to update.
   * @return Updated document data.
   */
  public abstract Document updateDocument(UserInfoInterface userInfo, ProcessData procData, Document adoc);

  /**
   * Updates document with new data. This method does not update document contents.
   * Checks for updates from DMS.
   * 
   * @param userInfo
   *          User information.
   * @param procData
   *          Process data.
   * @param adoc
   *          Document to update.
   * @return Updated document data.
   */
  public abstract Document updateDocumentInfo(UserInfoInterface userInfo, ProcessData procData, Document adoc);

  /**
   * Retrieves a document from local DB only.
   * @param userInfo
   *          User information.
   * @param procData
   *          Process data.
   * @param aDocId Document ID.
   * @param asFileName Document's filename.
   * @return Retrieved document.
   */
  public abstract Document getDocument(UserInfoInterface userInfo, ProcessData procData, int aDocId, String asFileName);

  /**
   * Retrieves a DMS document from DB.
   * 
   * @param userInfo
   *          User information.
   * @param procData
   *          Process data.
   * @param docid
   *          iFlow document ID.
   * 
   * @return Document retrieved from iFlow DB.
   */
  public abstract Document getDocument(UserInfoInterface userInfo, ProcessData procData, int docid);

  public abstract Document getDocument(UserInfoInterface userInfo, ProcessData procData, DocumentIdentifier docid);

  public abstract Document getDocumentAuth(UserInfoInterface userInfo, ProcessData procData, DocumentIdentifier docid, String user, String pass); 

  /**
   * TODO Add comment for method getDocument
   */
  public abstract Document getDocument(UserInfoInterface userInfo, ProcessData procData, Document adoc);

  /**
   * TODO Add comment for method getDocument
   */
  public abstract Document getDocumentInfo(UserInfoInterface userInfo, ProcessData procData, int aDocId);

  /**
   * TODO Add comment for method getDocument
   */
  public abstract Document getDocumentInfo(UserInfoInterface userInfo, ProcessData procData, DocumentIdentifier docid);

  /**
   * TODO Add comment for method getDocument
   */
  public abstract Document getDocumentInfo(UserInfoInterface userInfo, ProcessData procData, Document adoc);

  /**
   * Delete the document from the system.
   * 
   * @param userInfo
   *          User information.
   * @param procData
   *          Process data.
   * @param docid
   *          Document Identifier for iFlow/DMS.
   * @return True if the document was found and deleted, false otherwise.
   */
  public abstract boolean removeDocument(UserInfoInterface userInfo, ProcessData procData, DocumentIdentifier docid);
  
  /**
   * TODO Add comment for method removeDocument
   */
  public abstract boolean removeDocument(UserInfoInterface userInfo, ProcessData procData, int aDocId);

  /**
   * TODO Add comment for method removeDocument
   */
  public abstract boolean removeDocument(UserInfoInterface userInfo, ProcessData procData, Document adoc);

  
  public abstract boolean removeDocumentAuth(UserInfoInterface userInfo, ProcessData procData, DocumentIdentifier docid, String user, String pass);
  
  /**
   * TODO Add comment for method getDocument
   */
  public abstract List<Document> getDocumentInfoList(UserInfoInterface userInfo, ProcessData procData);

  public abstract byte[] mergePDFs(UserInfoInterface userInfo, ProcessData procData, String[] docsVar);

  public boolean asSignatures(UserInfoInterface userInfo, int docid);
  
  public boolean isToSign(UserInfoInterface userInfo, int docid);
  
  public boolean markDocsToSign(UserInfoInterface userInfo, ProcessListVariable docs, ProcessListVariable values);
}
