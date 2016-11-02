package pt.iflow.blocks;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.Port;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessListItem;
import pt.iflow.api.processdata.ProcessListVariable;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;


/**
 * <p>Title: BlockSQLInsert</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class BlockSQLBatchInsert extends BlockSQL {

  private static final String advancedQuery = "advancedQuery";  	
	
  public BlockSQLBatchInsert(int anFlowId, int id, int subflowblockid, String filename) {
    super(anFlowId,id, subflowblockid, filename);
    hasInteraction = false;
    saveFlowState = true;
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
    
    int flowid   = procData.getFlowId();
    int pid = procData.getPid();
    int subpid = procData.getSubPid();

    DataSource ds = null;
    Connection db = null;
    PreparedStatement pst = null;
    StringBuffer sbQuery = null;

    String sDataSource = null;
    String sInto = null;
    String sNames = null;
    String sValues = null;
    String[] saVars = null;
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
    
    String sVars = this.getAttribute(BlockSQL.sVARS);
	try {
		// As vars terão que ser interpretadas...
      // if(sVars.startsWith("\"")) <-- Before "y" + X, X and "y", were admited
      // Now "y" + X, X, "y", y${X}, ${X} and y, are admited
      // if sVars is "${X}", X cannot be a var
      String store = sVars.replace("${", "").replace("}", "");
      if (procData.getCatalogue().hasVar(store)) {
        sVars = store;
      } else {
        if (procData.isTransformable(sVars, false))
          sVars = procData.transform(userInfo, sVars, true);
      }
		Logger.debug(userInfo.getUtilizador(), this, "after", "Transformed var string: "+sVars);

		List<String> al = Utils.tokenize(sVars, ",");
		saVars = new String[al.size()];
		for (int i=0; i < saVars.length; i++) {
			saVars[i] = al.get(i);
			if (saVars[i] != null) saVars[i] = saVars[i].trim();
		}
	
	}
	catch (Exception e) {
		saVars = null;
	}
    
    if(sQuery == null ){
    	try {
    		sInto = this.getAttribute(BlockSQL.sINTO);
    		if (StringUtils.isNotEmpty(sInto)) {
          sInto = procData.transform(userInfo, sInto, true);
    		}
    		if (sInto.equals("")) sInto = null;
    	}
    	catch (Exception e) {
    		sInto = null;
    	}
    	try {
    		sNames = this.getAttribute(BlockSQL.sNAMES);
    		if (StringUtils.isNotEmpty(sNames)) {
          sNames = procData.transform(userInfo, sNames, true);
    		}
    		if (sNames.equals("")) sNames = null;
    	}
    	catch (Exception e) {
    		sNames = null;
    	}
    	try {
    		sValues = this.getAttribute(BlockSQL.sVALUES);

    		sValues = Utils.transformStringAndPrepareForDB(userInfo, sValues, procData);
    	}
    	catch (Exception e) {
    		sValues = null;
    	}

    	if (sInto == null || sNames == null || sValues == null || saVars == null) {
    	  Logger.error(login, this, "after", procData.getSignature() + "empty into or names or values or vars");
    	  outPort = portError;
    	} else {
    		if (this.isSystemTable(sDataSource, sInto)) {
    		  Logger.error(login, this, "after", procData.getSignature() + "Into '" + sInto + "' is a system table");
    		  outPort = portError;
    		} else {
    			int nCols = 0;

    			try {

    				sbQuery = new StringBuffer("insert into ");
    				sbQuery.append(sInto);
    				sbQuery.append(" (");

    				sbQuery.append(sNames);

    				sbQuery.append(") values (");


    				sbQuery.append(sValues);

    				sbQuery.append(")");


    				Logger.debug(login,this,"after","INTO= " + sInto);
    				Logger.debug(login,this,"after","NAMES= " + sNames);
    				Logger.debug(login,this,"after","VALUES= " + sValues);

    				ds = Utils.getUserDataSource(sDataSource);
    				if (null == ds) {
    				  Logger.error(login, this, "after", procData.getSignature() + "null datasource " + sDataSource);
    				  outPort = portError;
    				} else {
    					db = ds.getConnection();
    					db.setAutoCommit(false);
    					pst = db.prepareStatement(sbQuery.toString());

    					Logger.debug(login,this,"after","Query: " + sbQuery.toString());

    					// inserts
              final int MAX_LOGS = 100;
    					int nTotalInserts = procData.getList(saVars[0]).size();
    					for (int row=0; row < nTotalInserts; row++) {

    						for (int col=0; col < saVars.length; col++) {
    							ProcessListVariable lv = procData.getList(saVars[col]);

    							ProcessListItem item = lv.getItem(row);
    							String value = (null == item?null:item.format());
    							pst.setString((col+1), value);

    							Logger.debug(login,this,"after","STRING SETTING COL " 
    									+ (col+1) + " WITH " + saVars[col] + "[" + row + "]=" 
    									+  value);

    						}
                if (row < MAX_LOGS)
                  Logger.debug(login, this, "after", "Going to execute insert " + (row + 1) + " of " + nTotalInserts);
                else if (row == MAX_LOGS)
                  Logger.debug(login, this, "after", "...Executing next inserts of " + nTotalInserts);

    						nCols += pst.executeUpdate();
    					}

    					db.commit();
    					Logger.info(login,this,"after",procData.getSignature() + "connection committed");
    					
    					if (nCols == 0) {
    						outPort = portEmpty;
    					}
    				}
    			} catch (Exception e) {
    				Logger.error(login,this,"after",procData.getSignature() + "caught exception: " + e.getMessage(), e);
    				outPort = portError;
                    if (db != null) {
                      try {
                        db.rollback();
                      }
                      catch (Exception ex) {
                        Logger.error(login,this,"after",procData.getSignature() + "unable to rollback: " + ex.getMessage(), ex);
                      }
                    }
    			} finally {
    			  DatabaseInterface.closeResources(db,pst);
    			}
    		}
    	}
    }
       
    if(sQuery != null ){
    	int nCols = 0;
    	
    	try{
			ds = Utils.getUserDataSource(sDataSource);
			if (null == ds) {
			  Logger.error(login, this, "after", procData.getSignature() + "null datasource " + sDataSource);
			  outPort = portError;
			} else {
				db = ds.getConnection();
				db.setAutoCommit(false);
				
				pst = db.prepareStatement(sQuery);
	
				Logger.debug(login,this,"after","Query: " + sQuery);
				
				// inserts
				int nTotalInserts = procData.getList(saVars[0]).size();
				for (int row=0; row < nTotalInserts; row++) {
					for (int col=0; col < saVars.length; col++) {
					
						ProcessListVariable lv = procData.getList(saVars[col]);
						ProcessListItem item = lv.getItem(row);
						String value = (null == item?null:item.format());
						pst.setString((col+1), value);

						Logger.debug(login,this,"after","STRING SETTING COL " 
								+ (col+1) + " WITH " + saVars[col] + "[" + row + "]=" 
								+  value);

					}
					Logger.debug(login,this,"after","Going to execute insert " + (row+1) + " of " + nTotalInserts);
					nCols += pst.executeUpdate();
				}		
				
				db.commit();
				
				if (nCols == 0) {
					outPort = portEmpty;
				}
			}	
    		
        } catch (Exception e) {
          Logger.error(login,this,"after",procData.getSignature() + "caught exception: " + e.getMessage(), e);
          outPort = portError;
		} finally {
          if (db != null) {
            try {
              db.rollback();
            }
            catch (Exception e) {
              Logger.error(login,this,"after",procData.getSignature() + "unable to rollback: " + e.getMessage(), e);
            }
          }
		  DatabaseInterface.closeResources(db,pst);
		}
    }
    
    logMsg.append("Using '" + outPort.getName() + "';");
    Logger.logFlowState(userInfo, procData, this, logMsg.toString());
    return outPort;
  
  }
  

  public String getDescription (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, true, "SQL Batch Insert");
  }

  public String getResult (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, false, "SQL Batch Insert Efectuado");
  }
}
