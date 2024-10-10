package pt.iflow.services;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.events.EventManager;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;

/**
 * 
 * <p>
 * Title: EventManagerRemote
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * <p>
 * Company: iKnow
 * </p>
 * 
 * @author ptgm
 * @version 1.0
 */
public class EventManagerRemote {

  public boolean deRegisterEvent(String user, String password, int fid, int pid, int subpid, int blockid) throws Exception {
    UserInfoInterface ui = BeanFactory.getUserInfoFactory().newUserInfo(user, password);
    if (!ui.isLogged()) {
      Logger.warning(user, this, "deRegisterEvent", "Wrong user/password!");
      return false;
    }
    EventManager ev = EventManager.get();
    return ev.deRegisterEvent(ui, fid, pid, subpid, blockid);
  }

  public boolean registerEvent(String user, String password, int fid, int pid, int subpid, int blockid, String type,
      String properties) throws Exception {
    UserInfoInterface ui = BeanFactory.getUserInfoFactory().newUserInfo(user, password);
    if (!ui.isLogged()) {
      Logger.warning(user, this, "registerEvent", "Wrong user/password!");
      return false;
    }
    EventManager ev = EventManager.get();
    return ev.registerEvent(ui, fid, pid, subpid, blockid, type, properties);
  }

  public boolean setReadyToProcess(String user, String password, int fid, int pid, int subpid, int blockid) throws Exception {
    UserInfoInterface ui = BeanFactory.getUserInfoFactory().newUserInfo(user, password);
    if (!ui.isLogged()) {
      Logger.warning(user, this, "setReadyToProcess", "Wrong user/password!");
      return false;
    }
    EventManager ev = EventManager.get();
    return ev.setReadyToProcess(fid, pid, subpid, blockid);
  }
}
