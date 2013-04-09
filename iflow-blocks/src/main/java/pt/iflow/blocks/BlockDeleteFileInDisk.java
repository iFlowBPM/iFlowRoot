package pt.iflow.blocks;

import java.io.File;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.documents.Documents;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iknow.utils.StringUtilities;

/**
 * Copyright: Copyright (c) 2012 </p>
 * <p>
 * Company: Infosistema
 * </p>
 * 
 * @author
 */

public class BlockDeleteFileInDisk extends Block {
  public Port portIn, portSuccess, portEmpty, portError;

  private static final String DOCUMENT = "Document";
  private static final String PATH = "Path";
  private static final String DELETE = "Delete";

  public BlockDeleteFileInDisk(int anFlowId, int id, int subflowblockid, String filename) {
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

    String sPathVar = this.getAttribute(PATH);

    if (StringUtilities.isEmpty(sPathVar)) {
      Logger.error(login, this, "after", procData.getSignature() + "empty value for Path attribute");
      outPort = portError;
    } else
      try {
        String sPath = procData.transform(userInfo, sPathVar);
        File directory = new File(sPath);

        if (directory.isDirectory()) {
          File[] fileListing = directory.listFiles();

          for (File readFile : fileListing) {
            readFile.delete();
            Logger.info(userInfo.getUtilizador(), this, "processForm", "file (" + readFile.getName() + ") deleted.");
          }
        } else {
          directory.delete();
          Logger.info(userInfo.getUtilizador(), this, "processForm", "file (" + directory.getName() + ") deleted.");
        }
        outPort = portSuccess;
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
