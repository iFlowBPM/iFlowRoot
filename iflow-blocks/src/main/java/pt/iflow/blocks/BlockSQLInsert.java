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
 * <p>Title: BlockSQLInsert</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class BlockSQLInsert extends BlockSQL {

  private static final String advancedQuery = "advancedQuery";	
	
  public BlockSQLInsert(int anFlowId,int id, int subflowblockid, String filename) {
    super(anFlowId,id, subflowblockid, filename);
    hasInteraction = false;
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

    String sDataSource = null;
    String sInto = null;
    String sNames = null;
    String sValues = null;
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
    } catch (Exception e) {
      sDataSource = null;
      Logger.warning(login, this, "after", "Unable to parse DataSource during insert procedure (received: " + sDataSource + ").", e);
    }
    
    if(sQuery == null) {
    
      try {
        sInto = this.getAttribute(BlockSQL.sINTO);
        if (StringUtils.isNotEmpty(sInto)) {
          sInto = procData.transform(userInfo, sInto, true);
        }
        if (sInto.equals("")) sInto = null;
      } catch (Exception e) {
        sInto = null;
        Logger.warning(login, this, "after", "Unable to parse table name during insert procedure (received: " + sInto + ").", e);
      }
      try {
        sNames = this.getAttribute(BlockSQL.sNAMES);
        if (StringUtils.isNotEmpty(sNames)) {
          sNames = procData.transform(userInfo, sNames, true);
        }
        if (sNames.equals("")) sNames = null;
      } catch (Exception e) {
        sNames = null;
        Logger.warning(login, this, "after", "Unable to parse column names during insert procedure (received: " + sNames + ").", e);
      }
      try {
        sValues = this.getAttribute(BlockSQL.sVALUES);
        String escChar = this.getAttribute(BlockSQL.ESCAPE_CHARACTER);
        if(escChar != null) {
          sValues = Utils.transformStringAndPrepareForDB(userInfo, sValues, procData, escChar);
        } else {
          sValues = Utils.transformStringAndPrepareForDB(userInfo, sValues, procData);
        }
      } catch (Exception e) {
        sValues = null;
        Logger.warning(login, this, "after", "Unable to parse column values during insert procedure (received: " + sValues + ").", e);
      }

      if (sInto == null || sNames == null || sValues == null) {
        Logger.error(login, this, "after", procData.getSignature() + "Empty into or names or values");
        outPort = portError;
      } else {
        if (this.isSystemTable(sDataSource, sInto)) {
          Logger.error(login, this, "after", procData.getSignature() + "Into '" + sInto + "' is a system table");
          outPort = portError;
        } else {
          sbQuery = new StringBuffer("insert into ");
          sbQuery.append(sInto);
          sbQuery.append(" (");
          sbQuery.append(sNames);
          sbQuery.append(") values (");
          sbQuery.append(sValues);
          sbQuery.append(")");
          
          sQuery = sbQuery.toString();
        }
      }
    }

    if (outPort != portError) {
      if (StringUtils.isEmpty(sQuery)) {
        Logger.error(login, this, "after", procData.getSignature() + "Empty query");
        outPort = portError;
      }
      else {
        try {

          ds = Utils.getUserDataSource(sDataSource);
          if (null == ds) {
            Logger.error(login, this, "after", procData.getSignature() + "null datasource " + sDataSource);
            outPort = portError;
          } else {
            db = ds.getConnection();
            st = db.createStatement();

            // insert
            Logger.debug(login,this,"after","Going to execute insert: " + sQuery);
            int nCols = st.executeUpdate(sQuery);
            Logger.debug(login,this,"after","Number of inserted columns = " + nCols);

            if (nCols == 0) {
              outPort = portEmpty;
            }
          }
        } catch (SQLException sqle) {
          Logger.error(login,this,"after","caught sql exception: " + sqle.getMessage(), sqle);
          outPort = portError;
        } catch (Exception e) {
          Logger.error(login,this,"after","caught exception: " + e.getMessage(), e);
          outPort = portError;
        } finally {
          DatabaseInterface.closeResources(db,st);
        }
      }
    }
    
    logMsg.append("Using '" + outPort.getName() + "';");
    Logger.logFlowState(userInfo, procData, this, logMsg.toString());
    return outPort;
  }

  public String getDescription (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, true, "SQL Insert");
  }

  public String getResult (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, false, "SQL Insert Efectuado");
  }
}
