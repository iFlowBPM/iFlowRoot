package pt.iflow.blocks;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.Port;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;


/**
 * <p>Title: BlockSQLDelete</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class BlockSQLDelete extends BlockSQL {
	
  private static final String advancedQuery = "advancedQuery";	

  public BlockSQLDelete(int anFlowId,int id, int subflowblockid, String filename) {
    super(anFlowId,id, subflowblockid, filename);
    hasInteraction = false;
    saveFlowState = false;
  }

  /**
   * Executes the block main action
   *
   * @param dataSet a value of type 'DataSet'
   * @return the port to go to the next block
   */
  public Port after(UserInfoInterface userInfo, ProcessData procData) {
    Port outPort = portSuccess;
    StringBuffer logMsg = new StringBuffer();
    String login = userInfo.getUtilizador();

    DataSource ds = null;
    Connection db = null;
    Statement st = null;
    StringBuffer sbQuery = null;


    procData.clearError();

    String sDataSource = null;
    String sTable = null;
    String sWhere = null;
    String sQuery = null;
    
    try{
    	sQuery = this.getAttribute(advancedQuery);
    	 if (StringUtils.isNotEmpty(sQuery)) {
    		 sQuery = procData.transform(userInfo, sQuery, true);
    	 }
         if (StringUtils.isEmpty(sQuery)) sQuery = null;
    }
    catch (Exception e) {
    	sQuery = null;
    }
       
    try {
      sDataSource = this.getAttribute(BlockSQL.sDATASOURCE);
      if (StringUtils.isNotEmpty(sDataSource)) {
        sDataSource = procData.transform(userInfo, sDataSource, true);
      }
      if (StringUtils.isEmpty(sDataSource)) sDataSource = null;
    }
    catch (Exception e) {
      sDataSource = null;
    }
    
    if(sQuery == null){
      try {
    	sTable = this.getAttribute(BlockSQL.sTABLE);
    	if (StringUtils.isNotEmpty(sTable)) {
    		sTable = procData.transform(userInfo, sTable, true);
    	}
    	if (sTable.equals("")) sTable = null;
      }
      catch (Exception e) {
    	sTable = null;
      }
      try {
    	sWhere = this.getAttribute(BlockSQL.sWHERE);
    	if (StringUtils.isNotEmpty(sWhere)) {
    		sWhere = procData.transform(userInfo, sWhere, true);
    	}
    	if (sWhere.equals("")) {
    		sWhere = null;
    	}
      
    	if (sTable == null) {
    		procData.setError("Query inválida.");
    		outPort = portError;
    	} else {
    		if (this.isSystemTable(sDataSource, sTable)) {
    			procData.setError("Não é possível apagar registos em tabelas de sistema.");
    			outPort = portError;
    		} else {
    			if (sWhere == null) {
    				Logger.warning(login,this,"after","Where clause is empty... deleting all table!!!");
    			}
   				int nCols = -1;
   				try {
 					sbQuery = new StringBuffer("delete from ");
   					sbQuery.append(sTable);
   					if (sWhere != null) {
  						sbQuery.append(" where ").append(sWhere);
   					}
   					ds = Utils.getUserDataSource(sDataSource);
  					if (null == ds) {
   						procData.setError("Não foi possível ligar à base de dados.");
   						outPort = portError;
   					} else {
   						db = ds.getConnection();
   						st = db.createStatement();
   						// delete
   						Logger.debug(login,this,"after","Going to execute delete: " + sbQuery.toString());
   						nCols = st.executeUpdate(sbQuery.toString());
   						Logger.debug(login,this,"after","Number of deleted columns = " + nCols);
   						if (nCols == 0) {
  							outPort = portEmpty;
   						}	
   					}
   				} catch (SQLException sqle) {
   					Logger.error(login,this,"after","caught sql exception: " + sqle.getMessage());
   					sqle.printStackTrace();
   					outPort = portError;
   				} catch (Exception e) {
   					Logger.error(login,this,"after","caught exception: " + e.getMessage());
   					e.printStackTrace();
   					outPort = portError;
   				} finally {
   					DatabaseInterface.closeResources(db,st);
   				}
   			}
   		}
   	} catch (Exception e) {
   		sWhere = null;
   		procData.setError("Erro ao preparar a query.");
   		Logger.error(login, this, "after", "Error parsing where clause: "+e.getMessage());
   		e.printStackTrace();
   		outPort = portError;
   	}
   }
  
    if(sQuery != null){
    	int nCols = -1;
        
    	try{
    	  ds = Utils.getUserDataSource(sDataSource);
          if (null == ds) {
        	  procData.setError("Não foi possível ligar à base de dados.");
        	  outPort = portError;
          } 
          else {
        	  db = ds.getConnection();
        	  st = db.createStatement();
    		             
        	  // delete
        	  Logger.debug(login,this,"after","Going to execute delete: " + sQuery);
        	  nCols = st.executeUpdate(sQuery);
        	  Logger.debug(login,this,"after","Number of deleted columns = " + nCols);
    	  
        	  if (nCols == 0) {
           		  outPort = portEmpty;
    		  }
          }
       }catch (SQLException sqle) {
		  Logger.error(login,this,"after","caught sql exception: " + sqle.getMessage());
          sqle.printStackTrace();
          outPort = portError;
       }catch (Exception e) {
     	  Logger.error(login,this,"after","caught exception: " + e.getMessage());
          e.printStackTrace();
       	  outPort = portError;
       }finally {
   		  DatabaseInterface.closeResources(db,st);
   	  }
    }

  	logMsg.append("Using '" + outPort.getName() + "';");
    Logger.logFlowState(userInfo, procData, this, logMsg.toString());
    return outPort;
  }

  public String getDescription (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, true, "SQL Delete");
  }

  public String getResult (UserInfoInterface userInfo, ProcessData dataSet) {
    return this.getDesc(userInfo, dataSet, false, "SQL Delete Efectuado");
  }
}
