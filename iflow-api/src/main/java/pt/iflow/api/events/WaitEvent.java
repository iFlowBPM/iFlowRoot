package pt.iflow.api.events;

import pt.iflow.api.utils.Logger;

/**
 * WaitEvent.java
 * 
 * @author Pedro Monteiro
 * @author Luis Cabral
 * @since 22.06.2005
 * @version 20.08.2009
 */
public class WaitEvent extends AbstractEvent {

  private long _waitTime = 0;

  public WaitEvent(long waitTime) {
    _waitTime = waitTime;
  }

  public Integer initialEventCode() {
    return new Integer(EventManager.READY_TO_PROCESS);
  }

  public Boolean processEvent(String userId, Integer id, Integer pid, Integer subpid, Integer fid, Integer blockid, Long starttime,
      String type, String properties) {
    Boolean retObj = Boolean.FALSE;
    try {
      Thread.sleep(_waitTime);
      retObj = Boolean.TRUE;
    } catch (Exception e) {
      Logger.error(userId, this, "processEvent", "Exception caught: ", e);
    }
    return retObj;
  }

  public Boolean processEvent() {
    Boolean retObj = Boolean.FALSE;
    try {
      Thread.sleep(_waitTime);
      retObj = Boolean.TRUE;
    } catch (Exception e) {
      Logger.error("WaitEvent", this, "processEvent", "Exception caught: ", e);
    }
    return retObj;
  }
}
