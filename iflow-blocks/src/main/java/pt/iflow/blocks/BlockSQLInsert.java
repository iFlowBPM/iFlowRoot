package pt.iflow.blocks;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;

public class BlockSQLInsert extends BlockSQL {
  private static final String advancedQuery = "advancedQuery";
  
  public BlockSQLInsert(int anFlowId, int id, int subflowblockid, String filename) {
    super(anFlowId, id, subflowblockid, filename);
    this.hasInteraction = false;
  }
  
  public Port after(UserInfoInterface userInfo, ProcessData procData) {
    Port outPort = this.portSuccess;
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
    try {
        sQuery = getAttribute("advancedQuery");
        Logger.info(login, this, "after", "INFO: Advance query getAttribute: " + sQuery);
        if (StringUtils.isNotEmpty(sQuery)) {
          sQuery = procData.transform(userInfo, sQuery, true); 
        }
        if (StringUtils.isEmpty(sQuery)) {            
          sQuery = null; 
          Logger.info(login, this, "after", "INFO: Advance query is empty");
        }
      } catch (Exception e) {
        sQuery = null;
        Logger.info(login, this, "after", "INFO: Advance query catch expression");
      } 
    try {
      sDataSource = getAttribute("JNDIName");
      if (StringUtils.isNotEmpty(sDataSource))
        sDataSource = procData.transform(userInfo, sDataSource, true); 
      if (StringUtils.isEmpty(sDataSource))
        sDataSource = null; 
    } catch (Exception e) {
      sDataSource = null;
      Logger.warning(login, this, "after", "Unable to parse DataSource during insert procedure (received: " + sDataSource + ").", e);
    } 
    if (sQuery == null) {
      try {
        sInto = getAttribute("Into");
        if (StringUtils.isNotEmpty(sInto))
          sInto = procData.transform(userInfo, sInto, true); 
        if (sInto.equals(""))
          sInto = null; 
      } catch (Exception e) {
        sInto = null;
        Logger.warning(login, this, "after", "Unable to parse table name during insert procedure (received: " + sInto + ").", e);
      } 
      try {
        sNames = getAttribute("Names");
        if (StringUtils.isNotEmpty(sNames))
          sNames = procData.transform(userInfo, sNames, true); 
        if (sNames.equals(""))
          sNames = null; 
      } catch (Exception e) {
        sNames = null;
        Logger.warning(login, this, "after", "Unable to parse column names during insert procedure (received: " + sNames + ").", e);
      } 
      try {
        sValues = getAttribute("Values");
        String escChar = getAttribute("EscapeCharacter");
        if (escChar != null) {
          sValues = Utils.transformStringAndPrepareForDB(userInfo, sValues, procData, escChar);
        } else {
          sValues = Utils.transformStringAndPrepareForDB(userInfo, sValues, procData);
        } 
      } catch (Exception e) {
        sValues = null;
        Logger.warning(login, this, "after", "Unable to parse column values during insert procedure (received: " + sValues + ").", e);
      } 
      if (sInto == null || sNames == null || sValues == null) {
        Logger.error(login, this, "after", procData.getSignature() + "Empty into= " + sInto + " or names=" + sNames + " or values= " + sValues + "");
        outPort = this.portError;
      } else if (isSystemTable(sDataSource, sInto)) {
        Logger.error(login, this, "after", procData.getSignature() + "Into '" + sInto + "' is a system table");
        outPort = this.portError;
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
    if (outPort != this.portError)
      if (StringUtils.isEmpty(sQuery)) {
        Logger.error(login, this, "after", procData.getSignature() + "Empty query");
        outPort = this.portError;
      } else {
        try {
          ds = Utils.getUserDataSource(sDataSource);
          if (null == ds) {
            Logger.error(login, this, "after", procData.getSignature() + "null datasource " + sDataSource);
            outPort = this.portError;
          } else {
            db = ds.getConnection();
            st = db.createStatement();
            Logger.debug(login, this, "after", "Going to execute insert: " + sQuery);
            int nCols = st.executeUpdate(sQuery);
            Logger.debug(login, this, "after", "Number of inserted columns = " + nCols);
            if (nCols == 0)
              outPort = this.portEmpty; 
          } 
        } catch (SQLException sqle) {
          Logger.error(login, this, "after", "caught sql exception: " + sqle.getMessage(), sqle);
          outPort = this.portError;
        } catch (Exception e) {
          Logger.error(login, this, "after", "caught exception: " + e.getMessage(), e);
          outPort = this.portError;
        } finally {
          DatabaseInterface.closeResources(new Object[] { db, st });
        } 
      }  
    logMsg.append("Using '" + outPort.getName() + "';");
    Logger.logFlowState(userInfo, procData, this, logMsg.toString());
    return outPort;
  }
  
  public String getDescription(UserInfoInterface userInfo, ProcessData procData) {
    return getDesc(userInfo, procData, true, "SQL Insert");
  }
  
  public String getResult(UserInfoInterface userInfo, ProcessData procData) {
    return getDesc(userInfo, procData, false, "SQL Insert Efectuado");
  }
}
