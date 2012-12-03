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

import java.util.Calendar;
import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.RepositoryFile;
import pt.iflow.api.documents.DocumentData;
import pt.iflow.api.processdata.EvalException;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessListVariable;
import pt.iflow.api.processtype.TextDataType;
import pt.iflow.api.repository.RepositoryURIResolver;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.connector.document.Document;
import pt.iknow.pdf.PDFGenerator;
import pt.iknow.xslfo.FoTemplate;

public class BlockCriarDocumento extends Block {
  public Port portIn, portOutOk, portOutError;

  private static final String TEMPLATE = "template";
  private static final String VARIABLE = "variable";
  private static final String FILENAME = "filename";

  public BlockCriarDocumento(int anFlowId, int id, int subflowblockid, String filename) {
    super(anFlowId, id, subflowblockid, filename);
    hasInteraction = false;
    saveFlowState = false;
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
    retObj[0] = portOutOk;
    retObj[1] = portOutError;
    return retObj;
  }

  /**
   * No action in this block
   * 
   * @return always empty string
   */
  public String before(UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }

  /**
   * Executes the block main action
   * 
   * @param dataSet
   *          a value of type 'DataSet'
   * @return the port to go to the next block
   */
  public Port after(UserInfoInterface userInfo, ProcessData procData) {
    Port outPort = this.portOutError;
    String login = userInfo.getUtilizador();
    StringBuffer logMsg = new StringBuffer();

    RepositoryFile template = null;
    ProcessListVariable variable = null;

    String tmpl = getAttribute(TEMPLATE);
    String var = getAttribute(VARIABLE);
    String filename = getAttribute(FILENAME);
    if (StringUtils.isBlank(tmpl) || StringUtils.isBlank(var) || StringUtils.isBlank(filename)) {
      Logger.error(login, this, "after", "Unable to process data into file: must set variables (found: template=" + tmpl
          + "; variable=" + var + "; filename=" + filename + ")");
    } else {
      variable = procData.getList(var);
      if (variable == null) {
        Logger.error(login, this, "after", "Unable to process data into file: unknown variable (found: variable=" + var + ")");
      } else {
        template = BeanFactory.getRepBean().getPrintTemplate(userInfo, tmpl);
        // check that template exists or is a variable instead
        if (!template.exists() && procData.getVariableDataType(tmpl) instanceof TextDataType) {
          try {
            template = BeanFactory.getRepBean().getPrintTemplate(userInfo, "" + procData.eval(userInfo, tmpl));
          } catch (EvalException e) {
            template = null;
          }
        }
        if (template == null) {
          Logger.error(login, this, "after", "Unable to process data into file: unknown template (found: template=" + tmpl + ")");
        } else {
          try {
            FoTemplate tpl = FoTemplate.compile(template.getResourceAsStream());
            tpl.setUseLegacyExpressions(true);
            PDFGenerator pdfGen = new PDFGenerator(tpl);
            pdfGen.addURIResolver(new RepositoryURIResolver(userInfo));
            DocumentData newDocument = new DocumentData();
            newDocument.setFileName(procData.transform(userInfo, filename));
//            newDocument.setContent(pdfGen.getContents(getProcessSimpleVariables(procData)));
            bsh.Interpreter bsh = procData.getInterpreter(userInfo);
            newDocument.setContent(pdfGen.getContents(bsh));
            newDocument.setUpdated(Calendar.getInstance().getTime());
            Document savedDocument = BeanFactory.getDocumentsBean().addDocument(userInfo, procData, newDocument);
            try {
              variable.parseAndAddNewItem(String.valueOf(savedDocument.getDocId()));
              outPort = this.portOutOk;
              logMsg.append("Added '" + savedDocument.getDocId() + "' to '" + var + "';");
            } catch (Exception e) {
              Logger.error(userInfo.getUtilizador(), this, "after", "error parsing document " + savedDocument.getDocId(), e);
            }
          } catch (Exception e) {
            Logger.error(login, this, "after", "Unable to process data into file: error processing file (found: template=" + tmpl
                + "; variable=" + var + "; filename=" + filename + ")", e);
          }
        }
      }
    }
    
    logMsg.append("Using '" + outPort.getName() + "';");
    Logger.logFlowState(userInfo, procData, this, logMsg.toString());
    return outPort;
  }

  
  private Map<String, String> getProcessSimpleVariables(ProcessData procData) {
    Map<String, String> htProps = new Hashtable<String, String>();
    for (String sName : procData.getSimpleVariableNames()) {
      if (!htProps.containsKey(sName)) {
        String sValue = procData.getFormatted(sName);
        if (sName != null) {
          if (sValue == null) {
            sValue = "";
          }
          htProps.put(sName, sValue);
        }
      }
    }
    return htProps;
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
    return this.getDesc(userInfo, procData, true, "Criar Documento");
  }

  public String getResult(UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, false, "Criar Documento Efectuado");
  }

  public String getUrl(UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }
}
