package pt.iflow.blocks;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;

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

public class BlockAdminMigrateDocumentToFilesystem extends Block {
	public Port portIn, portSuccess, portEmpty, portError;

	private static final String BATCH_SIZE = "batchSize";
	private static final String BATCH_LIMIT = "batchLimit";
	private static final String TIME_LIMIT = "timeLimit";
	private static final String START_DOCID = "startDocId";
	
	public BlockAdminMigrateDocumentToFilesystem(int anFlowId, int id, int subflowblockid,
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
		
		try {
			Integer batchLimit =  Integer.parseInt(procData.transform(userInfo, this.getAttribute(BATCH_LIMIT)));
			Integer batchSize =  Integer.parseInt(procData.transform(userInfo, this.getAttribute(BATCH_SIZE)));
			Integer timeLimit =  Integer.parseInt(procData.transform(userInfo, this.getAttribute(TIME_LIMIT)));
			
			Integer startDocId =  0;
			try{
				startDocId = Integer.parseInt(procData.transform(userInfo, this.getAttribute(START_DOCID)));
			} catch(Exception e){
				startDocId = 0;
			}
			
			Calendar cal = Calendar.getInstance();    
			cal.add(Calendar.MINUTE, timeLimit);    
			Date stopTimeLimitDate = cal.getTime();
			
			if(batchLimit==null || batchLimit==0 || batchSize==null || batchSize==0 || stopTimeLimitDate.before(new Date())){
				Logger.warning(userInfo.getUtilizador(), this, "after",
						procData.getSignature()
								+ "empty values in batchLimit, batchSize or timeLimit");
				return portEmpty;
			}
			
			for(int execution=0; execution<batchLimit; execution++){
				List<Integer> docidList = fetchDocumentsToMigrate(userInfo, procData, batchSize, startDocId);			
				for(Integer docid: docidList){
					if(stopTimeLimitDate.after(new Date())){
						try{
						String result = BeanFactory.getDocumentsBean().migrateDatabaseToFilesystem(userInfo, procData, docid);
						Logger.info(userInfo.getUtilizador(), this, "after",
								procData.getSignature() + " docid:" + docid + " at path:" + result);
						} catch (Exception e){
							Logger.error(userInfo.getUtilizador(), this, "after",
									procData.getSignature() + e.getMessage() + " docid:" + docid, e);
						}
					}
				}								
			}
			
		} catch (Exception e) {
			Logger.error(userInfo.getUtilizador(), this, "after",
					procData.getSignature() + e.getMessage(), e);
			outPort = portError;
		}

		return outPort;
	}

	private List<Integer> fetchDocumentsToMigrate(UserInfoInterface userInfo, ProcessData procData, Integer limit, Integer startDocId) throws Exception {
		ArrayList<Integer> result = new ArrayList<Integer>();
		Connection db = null;
		PreparedStatement pst = null;
		try {
			db = Utils.getDataSource().getConnection();
			db.setAutoCommit(true);
			pst = db.prepareStatement("select docid from documents where docurl is null and docid>? limit 0,? ");
			pst.setInt(1, startDocId);
			pst.setInt(2, limit);
			
			ResultSet rs = pst.executeQuery();
			while(rs.next())
				result.add(rs.getInt(1));
			
			pst.close();						
		} catch (Exception e) {
			Logger.error(userInfo.getUtilizador(),this,	"fetchDocumentsToMigrate",procData.getSignature() + " error getting docid, "+ e.getMessage(), e);
			throw e;
		} finally {
			DatabaseInterface.closeResources(db, pst);
		}
		
		return result;
	}

	@Override
	public String getDescription(UserInfoInterface userInfo,
			ProcessData procData) {
		return this.getDesc(userInfo, procData, true, "AdminMigrateDocumentToFilesystem");
	}

	@Override
	public String getResult(UserInfoInterface userInfo, ProcessData procData) {
		return this.getDesc(userInfo, procData, false, "AdminMigrateDocumentToFilesystem Efectuada");
	}

	@Override
	public String getUrl(UserInfoInterface userInfo, ProcessData procData) {
		return "";
	}

}
