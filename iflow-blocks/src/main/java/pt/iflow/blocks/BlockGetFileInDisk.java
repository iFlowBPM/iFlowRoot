package pt.iflow.blocks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.documents.DocumentData;
import pt.iflow.api.documents.Documents;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessListVariable;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.connector.document.Document;
import pt.iknow.utils.StringUtilities;

/**
 * <p>
 * Description: This block adds a template to the internal template table
 * </p>
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * <p>
 * Company: Infosistema
 * </p>
 * 
 * @author
 */

public class BlockGetFileInDisk extends Block {
  public Port portIn, portSuccess, portEmpty, portError;

  private static final String DOCUMENT = "Document";
  private static final String PATH = "Path";
  private static final String DELETE = "Delete";

  public BlockGetFileInDisk(int anFlowId, int id, int subflowblockid, String filename) {
    super(anFlowId, id, subflowblockid, filename);
    hasInteraction = false;
  }

  public Port getEventPort() {
    return null;
  }

  public Port[] getInPorts(UserInfoInterface userInfo) {
    Port[] retObj = new Port[1];
    retObj[0] = portIn;
    return retObj;
  }

  public Port[] getOutPorts(UserInfoInterface userInfo) {
    Port[] retObj = new Port[2];
    retObj[0] = portSuccess;
    retObj[1] = portEmpty;
    retObj[2] = portError;
    return retObj;
  }

  /**
   * No action in this block
   * 
   * @param dataSet
   *          a value of type 'DataSet'
   * @return always 'true'
   */
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

  /**
   * Executes the block main action
   * 
   * @param dataSet
   *          a value of type 'DataSet'
   * @return the port to go to the next block
   */
  public Port after(UserInfoInterface userInfo, ProcessData procData) {
    Port outPort = portSuccess;
    String login = userInfo.getUtilizador();
    StringBuffer logMsg = new StringBuffer();
    Documents docBean = BeanFactory.getDocumentsBean();

    String sDocumentVar = this.getAttribute(DOCUMENT);
    String sPathVar = this.getAttribute(PATH);
    String sDeleteVar = this.getAttribute(DELETE);

    if (StringUtilities.isEmpty(sDocumentVar)) {
      Logger.error(login, this, "after", procData.getSignature() + "empty value for Document attribute");
      outPort = portError;
    } else if (StringUtilities.isEmpty(sPathVar)) {
      Logger.error(login, this, "after", procData.getSignature() + "empty value for Path attribute");
      outPort = portError;
    } else
      try {
        ProcessListVariable docsVar = procData.getList(sDocumentVar);
        String sPath = procData.transform(userInfo, sPathVar);
        String sDelete = procData.transform(userInfo, sDeleteVar);

        File directory = new File(sPath);
        if (directory.isDirectory()) {
          File[] fileListing = directory.listFiles();
          if (fileListing.length == 0)
            throw new FileNotFoundException();

          for (File readFile : fileListing) {
            String fileName = readFile.getName();
            Document doc = new DocumentData(fileName, FileUtils.readFileToByteArray(readFile));
            doc = docBean.addDocument(userInfo, procData, doc);

            Logger.info(userInfo.getUtilizador(), this, "processForm", "file (" + doc.getFileName() + ") for var " + sDocumentVar
                + " added.");
            if (StringUtils.equalsIgnoreCase(sDelete, "" + Boolean.TRUE)) {
              readFile.delete();
              Logger.info(userInfo.getUtilizador(), this, "processForm", "file (" + directory.getPath() + ") deleted ");
            }

            // now update process
            docsVar.parseAndAddNewItem(String.valueOf(doc.getDocId()));
          }
        } else {
          String fileName = directory.getName();
          Document doc = new DocumentData(fileName, FileUtils.readFileToByteArray(directory));
          doc = docBean.addDocument(userInfo, procData, doc);
          Logger.info(userInfo.getUtilizador(), this, "processForm", "file (" + doc.getFileName() + ") for var " + sDocumentVar
              + " added.");
          if (StringUtils.equalsIgnoreCase(sDelete, "" + Boolean.TRUE)) {
            directory.delete();
            Logger.info(userInfo.getUtilizador(), this, "processForm", "file (" + directory.getPath() + ") deleted ");
          }

          // now update process
          docsVar.parseAndAddNewItem(String.valueOf(doc.getDocId()));
        }
        outPort = portSuccess;

      } catch (FileNotFoundException e) {
        Logger.error(login, this, "after", procData.getSignature() + "file not found: " + e.getMessage(), e);
        outPort = portEmpty;
      } catch (IOException e) {
        Logger.error(login, this, "after", procData.getSignature() + "IO exception: " + e.getMessage(), e);
        outPort = portEmpty;
      } catch (Exception e) {
        Logger.error(login, this, "after", procData.getSignature() + "caught exception: " + e.getMessage(), e);
        outPort = portError;
      }

    logMsg.append("Using '" + outPort.getName() + "';");
    Logger.logFlowState(userInfo, procData, this, logMsg.toString());
    return outPort;
  }

  @Override
  public String getDescription(UserInfoInterface userInfo, ProcessData procData) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getResult(UserInfoInterface userInfo, ProcessData procData) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getUrl(UserInfoInterface userInfo, ProcessData procData) {
    // TODO Auto-generated method stub
    return null;
  }

}
