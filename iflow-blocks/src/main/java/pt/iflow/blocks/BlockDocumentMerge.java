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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.FormProps;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.connectors.DMSConnectorUtils;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.documents.DocumentData;
import pt.iflow.api.documents.Documents;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessListItem;
import pt.iflow.api.processdata.ProcessListVariable;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.connector.document.Document;
import pt.iknow.utils.html.FormFile;

public class BlockDocumentMerge extends Block {
  public Port portIn, portSuccess, portError;

  private static final String TITLE = "title";
  private static final String DESCRIPTION = "description";
  private static final String AUTHOR = "author";
  private static final String VARIABLE = "variable";
  private static final String LOCK = "lock";
  private static final String VERSIONABLE = "versionable";

  private static final String DOCSVARS = "docsvars";
  private static final String NEWDOC = "newdoc";
  private static final String DOCNAME = "docname";
  final Documents docBean = BeanFactory.getDocumentsBean();

  private static final String PROP_NAME_PREFIX = "name_";
  private static final String PROP_VALUE_PREFIX = "value_";

  public BlockDocumentMerge(int anFlowId, int id, int subflowblockid, String filename) {
    super(anFlowId, id, subflowblockid, filename);
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iflow.api.blocks.Block#getInPorts(pt.iflow.api.utils.UserInfoInterface)
   */
  public Port[] getInPorts(UserInfoInterface userInfo) {
    Port[] retObj = new Port[1];
    retObj[0] = portIn;
    return retObj;
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iflow.api.blocks.Block#getEventPort()
   */
  public Port getEventPort() {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iflow.api.blocks.Block#getOutPorts(pt.iflow.api.utils.UserInfoInterface)
   */
  public Port[] getOutPorts(UserInfoInterface userInfo) {
    Port[] retObj = new Port[2];
    retObj[0] = portSuccess;
    retObj[1] = portError;
    return retObj;
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iflow.api.blocks.Block#before(pt.iflow.api.utils.UserInfoInterface, pt.iflow.api.processdata.ProcessData)
   */
  public String before(UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }

  /*
   * (non-Javadoc)
   * 
   * @see pt.iflow.api.blocks.Block#after(pt.iflow.api.utils.UserInfoInterface, pt.iflow.api.processdata.ProcessData)
   */
  public Port after(UserInfoInterface userInfo, ProcessData procData) {
    Port retObj = portError;
    String login = userInfo.getUtilizador();

    try {
      String docsVarNames = this.getAttribute(DOCSVARS);
      String newDocVarName = this.getAttribute(NEWDOC);
      String newDocName = this.getParsedAttribute(userInfo, DOCNAME, procData);
      if (StringUtils.isNotBlank(docsVarNames) && StringUtils.isNotBlank(newDocVarName)) {
        String[] lstDocVars = StringUtils.split(docsVarNames, ',');

        //Merge Files
        byte[] newPDF = docBean.mergePDFs(userInfo, procData, lstDocVars);

        //Remove os ficheiros existentes na variável
        ProcessListVariable docsVar = procData.getList(newDocVarName);
        ListIterator<ProcessListItem> docs = docsVar != null ? docsVar.getItemIterator() : null;
        List<ProcessListItem> removedDocs = new ArrayList<ProcessListItem>();
        while (docs != null && docs.hasNext()) {
          ProcessListItem docItem = docs.next();
          int nDocId = -1;
          if(docItem != null) nDocId = Integer.parseInt(docItem.format());
          if (nDocId > -1) {
            if (docBean.removeDocument(userInfo, procData, nDocId)) {
              // remove document & update process
              // procurar a posicao no array....
              docItem.clear();
              //docsVar.setItem(docItem);
              // reference file to remove post-processing
              removedDocs.add(docItem);
            } else {
              Logger.error(login, this, "processForm", procData.getSignature() + "error removing file for var " + newDocVarName);
              procData.setError("Ocorreu um erro ao remover o ficheiro");
            }
          }
        }

        // files must be explicitly removed, hence:
        for (ProcessListItem docItem : removedDocs) {
          if (docItem.getValue() == null) {
            docsVar.removeItem(docItem);
          }
        }

        //Add new doc
        if (newPDF != null) {
          Document doc = new DocumentData(newDocName, newPDF);
          doc = docBean.addDocument(userInfo, procData, doc);
          docsVar.parseAndAddNewItem(String.valueOf(doc.getDocId()));
        }
      }
      retObj = portSuccess;

    } catch (Exception e) {
      Logger.error(login, this, "after", procData.getSignature() + "Exception caught: ", e);
    }
  this.addToLog("Using '" + retObj.getName() + "';");
  this.saveLogs(userInfo, procData, this);
  return retObj;
}

/*
 * (non-Javadoc)
 * 
 * @see pt.iflow.api.blocks.Block#canProceed(pt.iflow.api.utils.UserInfoInterface, pt.iflow.api.processdata.ProcessData)
 */
public boolean canProceed(UserInfoInterface userInfo, ProcessData procData) {
  return true;
}

/*
 * (non-Javadoc)
 * 
 * @see pt.iflow.api.blocks.Block#getDescription(pt.iflow.api.utils.UserInfoInterface, pt.iflow.api.processdata.ProcessData)
 */
public String getDescription(UserInfoInterface userInfo, ProcessData procData) {
  return this.getDesc(userInfo, procData, true, "Document Upload");
}

/*
 * (non-Javadoc)
 * 
 * @see pt.iflow.api.blocks.Block#getResult(pt.iflow.api.utils.UserInfoInterface, pt.iflow.api.processdata.ProcessData)
 */
public String getResult(UserInfoInterface userInfo, ProcessData procData) {
  return this.getDesc(userInfo, procData, false, "Document Upload Efectuado");
}

/*
 * (non-Javadoc)
 * 
 * @see pt.iflow.api.blocks.Block#getUrl(pt.iflow.api.utils.UserInfoInterface, pt.iflow.api.processdata.ProcessData)
 */
public String getUrl(UserInfoInterface userInfo, ProcessData procData) {
  return "";
}
}
