package pt.iflow.api.forkjoin;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.ProcessManager;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;


public class JoinManager extends Thread {

  UserInfoInterface _userInfo = null;
  ProcessData _procData = null;

  private JoinManager(UserInfoInterface userInfo, ProcessData procData) {
    _userInfo = userInfo;
    _procData = procData;
  }

  public static boolean registerSubProc(UserInfoInterface userInfo, ProcessData procData, Block block) {

    String userid = userInfo.getUtilizador();

    try {
      ProcessManager pm = BeanFactory.getProcessManagerBean();

      pm.createSubProcess(userInfo, procData, block.getId(), block.getDescription(userInfo, procData));

      JoinManager forkManager = new JoinManager(userInfo, procData);
      forkManager.start();
    } catch (Exception e) {
      Logger.error(userid, null, "registerSubProc", "caught exception: " + e.getMessage(), e);
    } 

    return true;
  }

  public void run() {

  }
}
