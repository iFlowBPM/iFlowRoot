package pt.iflow.blocks;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.documents.Documents;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;
import pt.iflow.connector.document.Document;
import pt.iknow.utils.StringUtilities;

/**
 * <p>
 * Description: This block adds a template to the internal template table
 * </p>
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * <p>
 * Company: Infosistema
 * </p>
 * 
 * @author
 */

public class BlockAdminMigrateDocument extends Block {
	public Port portIn, portSuccess, portEmpty, portError;

	private static final String DOCID = "docid";
	private static final String PATH = "Path";
	private static final String DELETE = "Delete";

	public BlockAdminMigrateDocument(int anFlowId, int id, int subflowblockid,
			String filename) {
		super(anFlowId, id, subflowblockid, filename);
		hasInteraction = false;
	}

	public Port getEventPort() {
		return null;
	}

	public Port[] getInPorts(UserInfoInterface userInfo) {
		Port[] retObj = new Port[1];
		retObj[0] = portIn;
		return retObj;
	}

	public Port[] getOutPorts(UserInfoInterface userInfo) {
		Port[] retObj = new Port[2];
		retObj[0] = portSuccess;
		retObj[1] = portEmpty;
		retObj[2] = portError;
		return retObj;
	}

	/**
	 * No action in this block
	 * 
	 * @param dataSet
	 *            a value of type 'DataSet'
	 * @return always 'true'
	 */
	public String before(UserInfoInterface userInfo, ProcessData procData) {
		return "";
	}

	/**
	 * No action in this block
	 * 
	 * @param dataSet
	 *            a value of type 'DataSet'
	 * @return always 'true'
	 */
	public boolean canProceed(UserInfoInterface userInfo, ProcessData procData) {
		return true;
	}

	/**
	 * Executes the block main action
	 * 
	 * @param dataSet
	 *            a value of type 'DataSet'
	 * @return the port to go to the next block
	 */
	public Port after(UserInfoInterface userInfo, ProcessData procData) {
		Port outPort = portSuccess;
		Documents docBean = BeanFactory.getDocumentsBean();
		
		try {
			String sDocidVar =  procData.transform(userInfo, this.getAttribute(DOCID));
			if (StringUtilities.isEmpty(sDocidVar)) {
				outPort = portEmpty;
				Logger.error(userInfo.getUtilizador(), this, "after",
						procData.getSignature()
								+ "empty value for docid attribute");
				return outPort;
			}

			Document doc = docBean.getDocument(userInfo, procData,
					Integer.parseInt(sDocidVar));
			if (doc == null) {
				outPort = portEmpty;
				Logger.error(userInfo.getUtilizador(), this, "after",
						procData.getSignature()
								+ "document doesnt exist for docid: "
								+ sDocidVar);
				return outPort;
			}

			// Current mode is DB and this document is stored already in DB, so
			// we do nothing
			if (StringUtils.isEmpty(Const.DOCS_BASE_URL)
					&& StringUtils.isEmpty(doc.getDocurl())) {
				outPort = portEmpty;
				Logger.error(
						userInfo.getUtilizador(),
						this,
						"after",
						procData.getSignature()
								+ "Current mode is DB and this document is stored already in DB, so we do nothing, docid: "
								+ sDocidVar);
				return outPort;
			}

			// DB -> FS
			if ( StringUtils.isNotEmpty(Const.DOCS_BASE_URL)	&& StringUtils.isEmpty(doc.getDocurl())) {
				byte[] content = getDocumentDataFromDB(userInfo, procData, doc);
				String docurl = writeDocumentDataToFileSystem(userInfo,	procData, doc, content);
				updateDocumentUrl(userInfo, procData, doc, docurl);
			}

			// FS -> DB
			else if (StringUtils.isEmpty(Const.DOCS_BASE_URL)
					&& StringUtils.isNotEmpty(doc.getDocurl())) {
				;
			}
		} catch (Exception e) {
			Logger.error(userInfo.getUtilizador(), this, "after",
					procData.getSignature() + e.getMessage(), e);
			outPort = portError;
		}

		return outPort;
	}

	private void updateDocumentUrl(UserInfoInterface userInfo,
			ProcessData procData, Document doc, String docurl) throws Exception {
		Connection db = null;
		PreparedStatement pst = null;
		try {
			db = Utils.getDataSource().getConnection();
			db.setAutoCommit(true);
			pst = db.prepareStatement("UPDATE documents SET docurl = ? WHERE docid = ? ");
			;

			pst.setString(1, docurl);
			pst.setInt(2, doc.getDocId());
			pst.execute();
			pst.close();
		} catch (Exception e) {
			Logger.error(
					userInfo.getUtilizador(),
					this,
					"updateDocumentUrl",
					procData.getSignature() + " error updating docurl, "
							+ e.getMessage(), e);
			throw e;
		} finally {
			DatabaseInterface.closeResources(db, pst);
		}

	}

	private String writeDocumentDataToFileSystem(UserInfoInterface userInfo,
			ProcessData procData, Document doc, byte[] content) throws Exception {
		String docurlResult = null;
		//get base directory
		String docsBaseUrl = Const.DOCS_BASE_URL;
		File f = new File(docsBaseUrl);
		if (!f.isDirectory()) {
			// Verifica se existe URL relativo
			docsBaseUrl = FilenameUtils.concat(Const.IFLOW_HOME,
					Const.DOCS_BASE_URL);
			f = new File(docsBaseUrl);
			if (!f.isDirectory()) {
				// tenta criar URL relativo
				try {
					FileUtils.forceMkdir(f);
				} catch (Exception e) {
					// tenta criar URL absoluto
					try {
						docsBaseUrl = Const.DOCS_BASE_URL;
						f = new File(docsBaseUrl);
						FileUtils.forceMkdir(f);
					} catch (Exception ex) {
						Logger.error(userInfo.getUtilizador(),this,"writeDocumentDataToFileSystem",procData.getSignature() + " not a directory, " + Const.DOCS_BASE_URL);
						throw ex;
					}
				}
			}
		}
		
		//get complete fielname
	    String strDocIdUrl = "0000000000" + doc.getDocId();
	    strDocIdUrl = strDocIdUrl.substring(strDocIdUrl.length()-10, strDocIdUrl.length());
	    String docIdUrl = strDocIdUrl.substring(0, 2) + "\\" + strDocIdUrl.substring(2, 4) + "\\" + 
	                      strDocIdUrl.substring(4, 6) + "\\" + strDocIdUrl.substring(6, 8) + "\\" +
	                      strDocIdUrl.substring(8, 10); 
	   
	    String url = FilenameUtils.concat(docsBaseUrl, docIdUrl);
	    try {
	    	File f1 = new File(url);
	        if (!f1.isDirectory()) FileUtils.forceMkdir(f1);
	        docurlResult =  FilenameUtils.concat(url,doc.getFileName());
	    } catch (Exception e) {
	    	Logger.error(userInfo.getUtilizador(),this,"writeDocumentDataToFileSystem",procData.getSignature() + " not a valid fielname, " + url + "-" + doc.getFileName() );
			throw e;
	    }
	    
	    //write file
	    try {
		    FileOutputStream fos = new FileOutputStream(docurlResult);
	        fos.write(content);
	        fos.close();
	    } catch (Exception e){
	    	Logger.error(userInfo.getUtilizador(),this,"writeDocumentDataToFileSystem",procData.getSignature() + " not possible to write file, " + e.getMessage(), e);
			throw e;
	    }
	    return docurlResult;
	}

	private byte[] getDocumentDataFromDB(UserInfoInterface userInfo, ProcessData procData, Document doc) throws Exception {
		byte[] content = null;
		Connection db = null;
		PreparedStatement pst = null;
		try {
			db = Utils.getDataSource().getConnection();
			db.setAutoCommit(true);
			pst = db.prepareStatement("SELECT datadoc FROM documents WHERE docid = ? ");
						
			pst.setInt(1, doc.getDocId());
			ResultSet rs = pst.executeQuery();
			rs.next();
			content = rs.getBytes(1);
			pst.close();
			
			if(content==null || content.length==0)
				throw new Exception("No binary content found");
		} catch (Exception e) {
			Logger.error(userInfo.getUtilizador(),this,	"getDocumentDataFromDB",procData.getSignature() + " error getting data, "+ e.getMessage(), e);
			throw e;
		} finally {
			DatabaseInterface.closeResources(db, pst);
		}
		
		return content;
	}

	@Override
	public String getDescription(UserInfoInterface userInfo,
			ProcessData procData) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getResult(UserInfoInterface userInfo, ProcessData procData) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUrl(UserInfoInterface userInfo, ProcessData procData) {
		// TODO Auto-generated method stub
		return null;
	}

}
