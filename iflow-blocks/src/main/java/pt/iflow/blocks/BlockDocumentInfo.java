package pt.iflow.blocks;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.documents.DocumentHash;
import pt.iflow.api.documents.DocumentIdentifier;
import pt.iflow.api.documents.IFlowDocumentIdentifier;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.DataSetUtils;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;
import pt.iknow.utils.DataSet;
import pt.iknow.utils.StringUtilities;

public class BlockDocumentInfo extends Block {
  public Port portIn, portSuccess, portError;

  private static final String DATASOURCE = "JNDIName";
  private static final String DOC_ID = "docid";
  private static final String FILENAME = "filename";
  private static final String UPDATED = "updated";
  private static final String URL = "url";
  private static final String USER = "user";

  public BlockDocumentInfo(int anFlowId, int id, int subflowblockid, String filename) {
    super(anFlowId, id, subflowblockid, filename);
    saveFlowState = true;
  }

  public Port[] getInPorts(UserInfoInterface userInfo) {
    Port[] retObj = new Port[1];
    retObj[0] = portIn;
    return retObj;
  }

  public Port getEventPort() {
    return null;
  }

  public Port[] getOutPorts(UserInfoInterface userInfo) {
    Port[] retObj = new Port[2];
    retObj[0] = portSuccess;
    retObj[1] = portError;
    return retObj;
  }

  /**
   * No action in this block.
   * 
   * @see Block#before(UserInfoInterface, ProcessData)
   * @return Always an empty string.
   */
  public String before(UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }

  /**
   * Executes the block main action.
   * 
   * @see Block#after(UserInfoInterface, ProcessData)
   * @return the port to go to the next block
   */
  public Port after(UserInfoInterface userInfo, ProcessData procData) {
    String login = userInfo.getUtilizador();
    if (Logger.isDebugEnabled()) {
      Logger.debug(login, this, "after", "Entered method!");
    }
    Port retObj = portError;
    DataSource ds = null;
    Connection db = null;
    Statement st = null;
    ResultSet rs = null;
    try {
      int flowid = procData.getFlowId();
      int pid = procData.getPid();
      int subpid = procData.getSubPid();

      DocumentIdentifier docId = null;      
      try {
        docId = DocumentIdentifier.getInstance(this.getValue(userInfo, getAttribute(DOC_ID), procData).toString());
      } catch (Exception e) {
        Logger.warning(userInfo.getUtilizador(), this, "after", "ID do documento é uma variável obrigatória");
      }
      if (docId != null) {
        String dataSource = null;
        try {
          dataSource = this.getAttribute(DATASOURCE);
          if (StringUtilities.isNotEmpty(dataSource)) {
            dataSource = procData.transform(userInfo, dataSource);
          }
          if (StringUtilities.isEmpty(dataSource)) {
            dataSource = null;
          }
        } catch (Exception e) {
          dataSource = null;
        }
        String filename = this.getAttribute(FILENAME);
        String updated = this.getAttribute(UPDATED);
        String url = this.getAttribute(URL);
        
        String user = this.getAttribute(USER);
        if (StringUtils.isBlank(user)) {
          user = userInfo.getUtilizador();
        } else {
          user = procData.transform(userInfo, user);
          user = user.replace(",", ";");
        }

        if (StringUtils.isBlank(filename) && StringUtils.isBlank(updated) && StringUtils.isBlank(url)) {
          Logger.warning(userInfo.getUtilizador(), this, "after",
              "No output data provided, the block is executing but storing no information.");
        }

        if (StringUtils.isNotBlank(url)) {
          StringBuffer buffer = new StringBuffer();
          buffer.append(Const.APP_PROTOCOL).append("://").append(Const.APP_HOST);
          if (StringUtils.isNotBlank("" + Const.APP_PORT)) {
            buffer.append(":").append(Const.APP_PORT);
          }
          buffer.append(Const.APP_URL_PREFIX);
          buffer.append("document?");
          String sUseDocHash = BeanFactory.getFlowSettingsBean().getFlowSetting(flowid, Const.sHASHED_DOCUMENT_URL).getValue();
          boolean useDocHash = StringUtils.equalsIgnoreCase(Const.sHASHED_DOCUMENT_URL_YES, sUseDocHash);
          if (useDocHash) {
            DocumentHash docHsh = new DocumentHash(user, docId, null);
            buffer.append("hdoc=").append(docHsh.getHash());
          } else {
            buffer.append("docid=").append(docId.getId());
          }
          buffer.append("&flowid=").append(flowid);
          buffer.append("&pid=").append(pid);
          buffer.append("&subpid=").append(subpid);
          String value = buffer.toString();
          if (Logger.isDebugEnabled()) {
            Logger.debug(userInfo.getUtilizador(), this, "after", "Saving information:" + url + "=" + value);
          }
          procData.parseAndSet(url, value);
          this.addToLog("Set '" + url + "' as '" + value + "';");
        }
        if (StringUtils.isNotBlank(filename) || StringUtils.isNotBlank(updated)) {
          ds = Utils.getUserDataSource(dataSource);
          if (ds == null) {
            Logger.warning(userInfo.getUtilizador(), this, "after", "Não foi possível ligar à base de dados. (" + DATASOURCE + "="
                + dataSource + ")");
          } else if (docId instanceof IFlowDocumentIdentifier) {
            StringBuffer query = new StringBuffer();
            query.append("SELECT * FROM documents");
            //query.append(" WHERE flowid=" + flowid);
            //query.append(" AND pid=" + pid);
            //query.append(" AND subpid=" + subpid);
            //query.append(" AND docid=" + docId.getId());
            query.append(" WHERE docid=" + docId.getId());
            if (Logger.isDebugEnabled()) {
              Logger.debug(userInfo.getUtilizador(), this, "after", "QUERY=" + query.toString());
            }
            db = ds.getConnection();
            st = db.createStatement();

            rs = st.executeQuery(query.toString());
            if (rs.next()) {
              if (StringUtils.isNotBlank(filename)) {
                String value = rs.getString("filename");
                if (Logger.isDebugEnabled()) {
                  Logger.debug(userInfo.getUtilizador(), this, "after", "Saving information:" + filename + "=" + value);
                }
                procData.parseAndSet(filename, value);
                this.addToLog("Set '" + filename + "' as '" + value + "';");
              }
              if (StringUtils.isNotBlank(updated)) {
                String value = rs.getDate("updated").toString();
                if (Logger.isDebugEnabled()) {
                  Logger.debug(userInfo.getUtilizador(), this, "after", "Saving information:" + updated + "=" + value);
                }
                procData.parseAndSet(updated, value);
                this.addToLog("Set '" + updated + "' as '" + value + "';");
              }
            }
          }
        }
      }
      retObj = portSuccess;
    } catch (Exception ex) {
      Logger.error(userInfo.getUtilizador(), this, "after", 
          procData.getSignature() + "Unable to perform operation.", ex);
      retObj = portError;
    } finally {
      DatabaseInterface.closeResources(db, st, rs);
    }
    this.addToLog("Using '" + retObj.getName() + "';");
    this.saveLogs(userInfo, procData, this);
    return retObj;
  }

  private Object getValue(UserInfoInterface userInfo, String name, ProcessData procData) throws Exception {
    Object retObj;
    if (DataSetUtils.isArrayVar(name)) {
      // Extra care must be taken. if var is abc[123] dataset will assume an array var, so we must extract key index.
      String key = DataSet.getKey(name);
      if (name.equals(key)) { // no index info... just regular array
        retObj = procData.getList(key);
      } else { // two "scalars" with index
        String value = procData.getListItemFormatted(DataSetUtils.getArrayVarName(name), DataSetUtils.getArrayVarPosition(name, procData));
        if (value == null) {
          value = procData.transform(userInfo, name);
        }
        retObj = value;
      }
    } else {
      // Not array, this can be an expression or just a single value, so evaluate name
      retObj = procData.transform(userInfo, name);
    }
    return retObj;
  }

  /**
   * No action in this block.
   * 
   * @see Block#canProceed(UserInfoInterface, ProcessData)
   * @return always 'true'
   */
  public boolean canProceed(UserInfoInterface userInfo, ProcessData procData) {
    return true;
  }

  public String getDescription(UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, true, "Document Info");
  }

  public String getResult(UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, false, "Document Info efectuada");
  }

  public String getUrl(UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }
}
