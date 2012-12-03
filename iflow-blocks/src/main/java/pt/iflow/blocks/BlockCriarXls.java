/*****************************************************************************************
    Infosistema iFlow - workflow and BPM platform
    Copyright(C) 2002-2012 Infosistema, S.A.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

    www.infosistema.com
    iflow@infosistema.com
    Av. Jose Gomes Ferreira, 11 3rd floor, s.34
    Miraflores
    1495-139 Alges Portugal
****************************************************************************************/
package pt.iflow.blocks;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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

public class BlockCriarXls extends Block {
  public Port portIn, portOutOk, portOutError;

  private static final String FILENAME = "filename";
  private static final String VARIABLE = "variable";
  private static final String COLUMN_NAMES = "columnNames";
  private static final String COLUMN_VALUES = "columnValues";

  public BlockCriarXls(int anFlowId, int id, int subflowblockid, String filename) {
    super(anFlowId, id, subflowblockid, filename);
    hasInteraction = false;
    saveFlowState = false;
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
    retObj[0] = portOutOk;
    retObj[1] = portOutError;
    return retObj;
  }

  @Override
  public Port getEventPort() {
    return null;
  }

  @Override
  public Port after(UserInfoInterface userInfo, ProcessData procData) {
    Port outPort = this.portOutError;
    String login = userInfo.getUtilizador();
    StringBuffer logMsg = new StringBuffer();
    String filename = getAttribute(FILENAME);
    String var = getAttribute(VARIABLE);
    String sColNames = getAttribute(COLUMN_NAMES);
    String sColValues = getAttribute(COLUMN_VALUES);
    ProcessListVariable variable = null;
    if (StringUtils.isBlank(filename) || StringUtils.isBlank(var) || StringUtils.isBlank(sColNames)
        || StringUtils.isBlank(sColValues)) {
      Logger.error(login, this, "after", "Unable to process data into file: must set variables (found: filename="
          + filename + "; variable=" + var + "; colNames=" + sColNames + "; colValues=" + sColValues + ")");
    } else {
      variable = procData.getList(var);
      if (variable == null) {
        Logger.error(login, this, "after", "Unable to process data into file: unknown variable (found: variable=" + var
            + ")");
      } else {
        try {
          filename = procData.transform(userInfo, filename);
          sColNames = procData.transform(userInfo, sColNames);
          String[] colNames = sColNames.split(",");
          String[] colValues = sColValues.split(",");
          ByteArrayOutputStream out = new ByteArrayOutputStream();
          String sError = BlockData.exportToSpreadSheet(this, userInfo, filename, parseToDataTable(userInfo, procData,
              colNames, colValues), out);
          if (StringUtils.isNotBlank(sError)) {
            logMsg.append("Unable to export: " + sError);
            Logger.warning(login, this, "after", "Unable to export: " + sError);
          } else {
            DocumentData newDocument = new DocumentData();
            newDocument.setFileName(filename);
            newDocument.setContent(out.toByteArray());
            newDocument.setUpdated(Calendar.getInstance().getTime());
            Document savedDocument = BeanFactory.getDocumentsBean().addDocument(userInfo, procData, newDocument);
            try {
              variable.parseAndAddNewItem(String.valueOf(savedDocument.getDocId()));
              outPort = this.portOutOk;
              logMsg.append("Added '" + savedDocument.getDocId() + "' to '" + var + "';");
            } catch (Exception e) {
              Logger.error(userInfo.getUtilizador(), this, "after", "error parsing document "
                  + savedDocument.getDocId(), e);
            }
          }
        } catch (Exception e) {
          Logger.error(login, this, "after",
              "Unable to process data into file: error processing file (found: filename=" + filename + "; variable="
                  + var + "; colNames=" + sColNames + "; colValues=" + sColValues + ")", e);
        }
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
   * @param dataSet
   *          a value of type 'DataSet'
   * @return always 'true'
   */
  public boolean canProceed(UserInfoInterface userInfo, ProcessData procData) {
    return true;
  }

  public String getDescription(UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, true, "Criar Documento XLS");
  }

  public String getResult(UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, false, "Criar Documento XLS Efectuado");
  }

  public String getUrl(UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }

  private List<List<String>> parseToDataTable(UserInfoInterface userInfo, ProcessData procData, String[] colNames,
      String[] colValues) {
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
        lRowValues.add(item == null ? "" : "" + item.getValue());
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
