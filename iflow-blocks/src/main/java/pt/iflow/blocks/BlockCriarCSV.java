package pt.iflow.blocks;

import org.apache.commons.lang.StringUtils;
import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.documents.DocumentData;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessListItem;
import pt.iflow.api.processdata.ProcessListVariable;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.connector.document.Document;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BlockCriarCSV extends Block {
  public Port portIn, portSuccess, portError;

  private static final String FILENAME = "filename";
  private static final String VARIABLE = "variable";
  private static final String SEPARATOR = "separator";
  private static final String COLUMN_VALUES = "columnValues";
  private static final String COLUMN_NAMES = "columnNames";

  public BlockCriarCSV(int anFlowId, int id, int subflowblockid, String filename) {
    super(anFlowId, id, subflowblockid, filename);
    hasInteraction = false;
    saveFlowState = true;
  }

  @Override
  public Port[] getInPorts(UserInfoInterface userInfo) {
    Port[] retObj = new Port[1];
    retObj[0] = portIn;
    return retObj;
  }

  @Override
  public Port[] getOutPorts(UserInfoInterface userInfo) {
    Port[] retObj = new Port[2];
    retObj[0] = portSuccess;
    retObj[1] = portError;
    return retObj;
  }

  @Override
  public Port getEventPort() {
    return null;
  }

  @Override
  public Port after(UserInfoInterface userInfo, ProcessData procData) {
    Port outPort = this.portSuccess;
    String login = userInfo.getUtilizador();
    StringBuffer logMsg = new StringBuffer();

    String sFilename = null;
    String sSeparator = null;
    String sColNames = null;
    String sColValues = null;
    ProcessListVariable outputVariable = null;

    try {
      sFilename = this.getAttribute(FILENAME);
      sSeparator = this.getAttribute(SEPARATOR);
      sColNames = this.getAttribute(COLUMN_NAMES);
      sColValues = this.getAttribute(COLUMN_VALUES);
      outputVariable = procData.getList(this.getAttribute(VARIABLE));

    } catch (Exception e) {
      Logger.error(login, this, "after", procData.getSignature() + "error transforming attributes");
      outPort = portError;
    }

    if (StringUtils.isBlank(sFilename) || StringUtils.isBlank(sSeparator) || StringUtils.isBlank(sColNames) || StringUtils.isBlank(sColValues)) {
      Logger.error(login, this, "after", procData.getSignature() + "empty value for block attributes");
      outPort = portError;
    } else {
      try {
        sFilename = procData.transform(userInfo, sFilename);
        sSeparator = procData.transform(userInfo, sSeparator);
        sColNames = procData.transform(userInfo, sColNames);

        // Split the column names and values
        String[] colNames = sColNames.split(",");
        String[] colValues = sColValues.split(",");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        String sError = BlockData.exportToSpreadSheet(this, userInfo, sFilename, sSeparator, parseToDataTable(userInfo, procData,
                sSeparator,colNames, colValues), out);

        if (StringUtils.isNotBlank(sError)) {
          Logger.error(login, "BlockCriarCSV", "after","Unable to export to spread sheet: " + sError);
          return portError;

        } else {
          DocumentData newDocument = new DocumentData();
          newDocument.setFileName(sFilename);
          newDocument.setContent(out.toByteArray());
          newDocument.setUpdated(Calendar.getInstance().getTime());
          Document savedDocument = BeanFactory.getDocumentsBean().addDocument(userInfo, procData, newDocument);

          try {
            outputVariable.parseAndAddNewItem(String.valueOf(savedDocument.getDocId()));
            Logger.info(login, "BlockCriarCSV", "after","Added " + savedDocument.getDocId() + " to output variable");
          } catch (Exception e) {
            Logger.error(userInfo.getUtilizador(), this, "after", "error parsing document "
                + savedDocument.getDocId(), e);
          }
        }
      } catch (Exception e) {
        Logger.error(login, this, "after", procData.getSignature() + "caught exception: " + e.getMessage(), e);
        outPort = portError;
      }
    }

    logMsg.append("Using '" + outPort.getName() + "';");
    Logger.logFlowState(userInfo, procData, this, logMsg.toString());
    return outPort;
  }

  @Override
  public String before(UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }

  /**
   * No action in this block
   * 
   * @param userInfo
   * @param procData
   * @return always 'true'
   */
  public boolean canProceed(UserInfoInterface userInfo, ProcessData procData) {
    return true;
  }

  public String getDescription(UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, true, "Criar Documento CSV");
  }

  public String getResult(UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, false, "Criar Documento CSV Efectuado");
  }

  public String getUrl(UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }

  private List<List<String>> parseToDataTable(UserInfoInterface userInfo, ProcessData procData, String separator,
		  String[] colNames, String[] colValues) {
    List<List<String>> retObj = new ArrayList<List<String>>();
    List<String> lColNames = new ArrayList<String>();
    for (String colName : colNames) {
      lColNames.add(colName.trim());
    }
    retObj.add(lColNames);
    int maxSize = getMaxSize(procData, colValues);
    int pos = 0;
    while (pos < maxSize) {
      List<String> lRowValues = new ArrayList<String>();
      for (String colValue : colValues) {
        colValue = colValue.trim();        
        ProcessListItem item = procData.getListItem(colValue, pos);
        if(procData.isListVar(colValue) == false) {
        	lRowValues.add("" + procData.get(colValue).getValue());
        }else {
        	lRowValues.add(item == null ? "" : "" + item.getValue());
        }
      }
      retObj.add(lRowValues);
      pos++;
    }
    return retObj;
  }

  private int getMaxSize(ProcessData procData, String[] colValues) {
    int maxSize = 0;
    for (String colValue : colValues) {
      ProcessListVariable list = procData.getList(colValue.trim());
      if (list != null && list.size() > maxSize) {
        maxSize = list.size();
      }
    }
    return maxSize;
  }
}
