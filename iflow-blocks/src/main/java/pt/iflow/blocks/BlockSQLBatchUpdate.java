package pt.iflow.blocks;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.Port;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessListVariable;
import pt.iflow.api.processtype.ProcessDataType;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;
import pt.iflow.blocks.msg.Messages;

/**
 * <p>Title: BlockSQLInsert</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: Infosistema</p>
 * @author Miguel Guilherme
 */

public class BlockSQLBatchUpdate extends BlockSQL {

  private static final String advancedQuery = "advancedQuery";  
	
  public BlockSQLBatchUpdate(int anFlowId, int id, int subflowblockid, String filename) {
    super(anFlowId, id, subflowblockid, filename);
    hasInteraction = false;
    saveFlowState = true;
  }

  /**
   * Executes the block main action
   *
   * @param dataSet a value of type 'DataSet'
   * @return the port to go to the next block.
   */
  public Port after(UserInfoInterface userInfo, ProcessData procData) {
    Port outPort = portSuccess;

    String login = userInfo.getUtilizador();

    Connection connection = null;
    PreparedStatement pstmt = null;
    StringBuffer query = null;

    String sDataSource = null;
    String sTable = null;
    String sSet = null;
    String sWhere = null;
    String[] saSetVars = null;
    String[] saWhereVars = null;
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
      // DataSource
      sDataSource = this.getAttribute(BlockSQL.sDATASOURCE);
      if (StringUtils.isNotEmpty(sDataSource)) {
        sDataSource = procData.transform(userInfo, sDataSource, true);
      }
      else {
        sDataSource = null;
      }
    }
    catch (Exception e) {
    	sDataSource = null;
    }
    
    try{
    	String sSetVars = this.getAttribute(BlockSQL.sSETVARS);
  	  	// As vars terão que ser interpretadas...
      // if(sSetVars.startsWith("\"")) <-- Before "y" + X, X and "y", were allowed
      // Now "y" + X, X, "y", y${X}, ${X} and y, are allowed
      // if sSetVars is "${X}", X cannot be a var
      String store = sSetVars.replace("${", "").replace("}", "");
      if (procData.getCatalogue().hasVar(store)) {
        sSetVars = store;
      } else {
        if (procData.isTransformable(sSetVars, false))
          sSetVars = procData.transform(userInfo, sSetVars, true);
      }
      Logger.debug(userInfo.getUtilizador(), this,
  			  "after", "Transformed var string: " + sSetVars);    
  	  	List<String> al = Utils.tokenize(sSetVars, ",");
  	  	saSetVars = new String[al.size()];

  	  	for (int i = 0; i < saSetVars.length; i++) {
  	  		saSetVars[i] = al.get(i);
  	  		if (saSetVars[i] != null) {
  	  			saSetVars[i] = saSetVars[i].trim();
  	  		}
   	  	}
  	} 
    catch (Exception e) {
    	saSetVars = null;
    }
    
    try{
     	String sWhereVars = this.getAttribute(BlockSQL.sWHEREVARS);
     	// As vars terão que ser interpretadas...
      // if(sWhereVars.startsWith("\"")) {
      // Now "y" + X, X, "y", y${X}, ${X} and y, are allowed
      // if sWhereVars is "${X}", X cannot be a var
      String store = sWhereVars.replace("${", "").replace("}", "");
      if (procData.getCatalogue().hasVar(store)) {
        sWhereVars = store;
      } else {
        if (procData.isTransformable(sWhereVars, false))
          sWhereVars = procData.transform(userInfo, sWhereVars, true);
      }
      Logger.debug(userInfo.getUtilizador(), this, "after",
     			"Transformed var string: " + sWhereVars);
     	List<String> alWhere = Utils.tokenize(sWhereVars, ",");
     	saWhereVars = new String[alWhere.size()];
     	for (int i = 0; i < saWhereVars.length; i++) {
     		saWhereVars[i] = alWhere.get(i);
     		if (saWhereVars[i] != null) {
     			saWhereVars[i] = saWhereVars[i].trim();
     		}
     	}
    }
    catch (Exception e) {
      Logger.error(login, this, "after", procData.getSignature() + "Error in where: '"+ saWhereVars +"'");
      outPort = portError;
    	saWhereVars = null;
    }
    
    if(sQuery == null){
      
    	try{
    	  // Table
    	  sTable = this.getAttribute(BlockSQL.sTABLE);
    	  if (StringUtils.isNotEmpty(sTable)) {
          sTable = procData.transform(userInfo, sTable, true);
    	  }
    	  else {
    		  sTable = null;
      	  }
    	}
        catch (Exception e) {
        	sTable = null;
        }
        
        try{
          //Set
       	  sSet = this.getAttribute(BlockSQL.sSET);
       	  sSet = Utils.transformStringAndPrepareForDB(userInfo, sSet, procData);
        }
        catch (Exception e) {
        	sSet = null;
        }

        try{
        	// Where
         	sWhere = this.getAttribute(BlockSQL.sWHERE);
         	sWhere = Utils.transformStringAndPrepareForDB(userInfo, sWhere, procData);
        }
        catch (Exception e) {
          Logger.error(login, this, "after", procData.getSignature() + "Error in where: '"+ sWhere +"'");
          outPort = portError;
        	sWhere = null;
        }

     	if (sTable == null || sSet == null || saSetVars == null
     			|| sWhere == null || saWhereVars== null) {
     	  Logger.error(login, this, "after", procData.getSignature() + "empty table or set or vars or where or wherevars");
     	  return portError;
     	}
     	if (this.isSystemTable(sDataSource, sTable)) {
     	  Logger.error(login, this, "after", procData.getSignature() + "Table '" + sTable + "' is a system table");
     	  return portError;
     	}
     	
     	try{
     		int nCols = 0;

     		query = new StringBuffer("update ");
     		query.append(sTable);
     		query.append(" set ");
     		query.append(sSet);
     		query.append(" where ");
     		query.append(sWhere);

     		Logger.debug(login, this, "after", "TABLE= " + sTable);
     		Logger.debug(login, this, "after", "WHERE= " + sWhere);
     		Logger.debug(login, this, "after", "SET= " + sSet);

     		if (StringUtils.isEmpty(sDataSource)) {
     			connection = DatabaseInterface.getConnection(userInfo);
     		}
     		else {
     			connection = Utils.getUserDataSource(sDataSource).getConnection();
     			connection.setAutoCommit(false);
     		}
     		pstmt = connection.prepareStatement(query.toString());

     		Logger.debug(login, this, "after", "Query: " + query.toString());

     		int nTotalUpdates = 0;
     		// Concatenar os 2 arrays com as Vars
     		String[] newArray = new String[saSetVars.length + saWhereVars.length];
	      	for(int i = 0; i < saSetVars.length; i++) {
	      		newArray[i] = saSetVars[i];
	      		if (procData.getList(saSetVars[i]).size() > nTotalUpdates)
	      			nTotalUpdates = procData.getList(saSetVars[i]).size();
	      	}
	      	for(int i = 0; i < saWhereVars.length; i++) {
	      		newArray[i+saSetVars.length] = saWhereVars[i];
	      	}

	      	if (nTotalUpdates == 0) {
	      		return portEmpty; 
	      	}

	      	// updates
	      	for (int row = 0; row < nTotalUpdates; row++) {

	      		for (int col = 0; col < newArray.length; col++) {

	      			ProcessListVariable var = procData.getList(newArray[col]); 
	      			ProcessDataType dataType = var.getType(); 
	      			Class<?> dataTypeClass = dataType.getSupportingClass();           

	      			if (dataTypeClass == Date.class) {
	      				java.sql.Date val = null; 
	      				if (var.size() > row) {
	      					Date dt = (Date)var.getItemValue(row);
	      					val = new java.sql.Date(dt.getTime());
	      				}

	      				pstmt.setDate((col+1), val);
	      				Logger.debug(login, this, "after", "SETTING DATE COL " + 
	    					  (col+1) + " WITH " + newArray[col] + "[" + row + "]=" + 
	    					  val);
	      			}
	      			else if (dataTypeClass == int.class) {
	      				Integer val = 0;
	      				if (var.size() > row) {
	      					val = (Integer)var.getItemValue(row);
	      				}
	      				pstmt.setInt((col+1), val);
	      				Logger.debug(login, this, "after", "SETTING INT COL " + 
	    					  (col+1) + " WITH " + newArray[col] + "[" + row + "]=" + 
	    					  val);            
	      			}
	      			else if (dataTypeClass == double.class) {
	      				Float val = 0F;
	      				if (var.size() > row) {
	    				  val = (Float)var.getItemValue(row);
	    			  }
	    			  pstmt.setFloat((col+1), val);
	    			  Logger.debug(login, this, "after", "SETTING FLOAT COL " + 
	    					  (col+1) + " WITH " + newArray[col] + "[" + row + "]=" + 
	    					  val);                        
	    		  }
	    		  else {
	    			  String val = "";
	    			  if (var.size() > row) {
	    				  val = (String)var.getItemValue(row);
	    			  }
	    			  pstmt.setString((col+1), val);
	    			  Logger.debug(login, this, "after", "SETTING STRING COL " + 
            			(col+1) + " WITH " + newArray[col] + "[" + row + "]=" + 
            			val);                        
	    		  }
	    	  }
	    	  Logger.info(login, this, "after", 
	    			  procData.getSignature() + "Going to execute update " + (row+1) + " of " + nTotalUpdates);

	    	  pstmt.executeUpdate();
	    	  nCols += pstmt.executeUpdate();
	        	  
	      }

	      connection.commit();
          Logger.info(login,this,"after",procData.getSignature() + "connection committed");
	      	
	      if (nCols == 0) {
	    	  outPort = portEmpty;
	      }
	      
      }catch (Exception e) {
    	  Logger.error(login, this, "after", 
                 procData.getSignature() + "caught exception: " + e.getMessage(), e);
          outPort = portError;
          if (connection != null) {
            try {
              connection.rollback();
            }
            catch (Exception ex) {
              Logger.error(login,this,"after",procData.getSignature() + "unable to rollback: " + ex.getMessage(), ex);
            }
          }
      }finally {
    	  DatabaseInterface.closeResources(connection, pstmt);
      }
	      
    }
    
    if(sQuery != null){
    	int nCols = 0;
    	
    	try{
    	
    		if (StringUtils.isEmpty(sDataSource)) {
    			connection = DatabaseInterface.getConnection(userInfo);
    		}
    		else {
    			connection = Utils.getUserDataSource(sDataSource).getConnection();
    		}
    		pstmt = connection.prepareStatement(sQuery);
    		
    		Logger.debug(login, this, "after", "Query: " + sQuery);

     		int nTotalUpdates = 0;
     		
     		// Concatenar os 2 arrays com as Vars
     		String[] newArray = new String[saSetVars.length + saWhereVars.length];
	      	for(int i = 0; i < saSetVars.length; i++) {
	      		newArray[i] = saSetVars[i];
	      		if (procData.getList(saSetVars[i]).size() > nTotalUpdates)
	      			nTotalUpdates = procData.getList(saSetVars[i]).size();
	      	}
	      	for(int i = 0; i < saWhereVars.length; i++) {
	      		newArray[i+saSetVars.length] = saWhereVars[i];

	      	}

	      	if (nTotalUpdates == 0) {
	      		return portEmpty; 
	      	}
	      	
	      	// updates
	      	for (int row = 0; row < nTotalUpdates; row++) {
	      		for (int col = 0; col < newArray.length; col++) {
	      			      			
	      			ProcessListVariable var = procData.getList(newArray[col]); 
	      			ProcessDataType dataType = var.getType(); 
	      			Class<?> dataTypeClass = dataType.getSupportingClass();           

	      			if (dataTypeClass == Date.class) {
	      				
	      				java.sql.Date val = null; 
	      				if (var.size() > row) {
	      					Date dt = (Date)var.getItemValue(row);
	      					val = new java.sql.Date(dt.getTime());
	      				}

	      				pstmt.setDate((col+1), val);
	      				Logger.debug(login, this, "after", "SETTING DATE COL " + 
	    					  (col+1) + " WITH " + newArray[col] + "[" + row + "]=" + 
	    					  val);
	      			}
	      			else if (dataTypeClass == int.class) {
	      			
	      				Integer val = 0;
	      				if (var.size() > row) {
	      					val = (Integer)var.getItemValue(row);
	      				}
	      				pstmt.setInt((col+1), val);
	      				Logger.debug(login, this, "after", "SETTING INT COL " + 
	    					  (col+1) + " WITH " + newArray[col] + "[" + row + "]=" + 
	    					  val);            
	      			}
	      			else if (dataTypeClass == double.class) {
	      			
	      				Float val = 0F;
	      				if (var.size() > row) {
	    				  val = (Float)var.getItemValue(row);
	      				}
	    			  pstmt.setFloat((col+1), val);
	    			  Logger.debug(login, this, "after", "SETTING FLOAT COL " + 
	    					  (col+1) + " WITH " + newArray[col] + "[" + row + "]=" + 
	    					  val);                        
	    		  }
	    		  else {
	    			  
	    			  String val = null;
	    			  if (var.size() > row) {
	    				  val = (String)var.getItemValue(row);
	    			  }
	    			  Logger.debug("", this, "", "Val: "+val);
	    			  
	    			  pstmt.setString((col+1), val);
	    			    			  
	    			  Logger.debug(login, this, "after", "SETTING STRING COL " + 
            			(col+1) + " WITH " + newArray[col] + "[" + row + "]=" + 
            			val);                        
	    		  }
	    	  }
	    	  Logger.info(login, this, "after", 
	    			  procData.getSignature() + "Going to execute update " + (row+1) + " of " + nTotalUpdates);

	    	  pstmt.executeUpdate();
	    	  nCols += pstmt.executeUpdate();

	      }
	      	
	      if (nCols == 0) {
	    	  outPort = portEmpty;
	      }	   		
    	}
    	catch (Exception e) {
    		Logger.error(login, this, "after", 
               procData.getSignature() + "caught exception: " + e.getMessage());
    		outPort = portError;
    	}finally {
    		DatabaseInterface.closeResources(connection, pstmt);
    	}
    }
    
    return outPort;
  }

  /**
   * Retrieve description.
   */
  public String getDescription(UserInfoInterface userInfo, ProcessData procData) {
    Messages msg = Messages.getInstance(BeanFactory.getSettingsBean().getOrganizationLocale(userInfo));
    return this.getDesc(userInfo, procData, true, msg.getString("BlockSQLBatchUpdate.description"));
  }

  /**
   * Retrieve result.
   */
  public String getResult(UserInfoInterface userInfo, ProcessData procData) {
    Messages msg = Messages.getInstance(BeanFactory.getSettingsBean().getOrganizationLocale(userInfo));
    return this.getDesc(userInfo, procData, true, msg.getString("BlockSQLBatchUpdate.result"));
  }

}
