package pt.iflow.api.events;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.flows.Flow;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;

/**
 * AsyncWaitEvent.java
 * 
 * @author Pedro Monteiro
 * @author Luis Cabral
 * @since 09.09.2005
 * @version 20.08.2009
 */
public class AsyncWaitEvent extends AbstractEvent {

  public AsyncWaitEvent() {
    super();
  }

  public Integer initialEventCode() {
    return new Integer(EventManager.NOT_READY_TO_PROCESS);
  }

  public Boolean processEvent() {
    return Boolean.TRUE;
  }

  public Boolean processEvent(String userId, Integer id, Integer pid, Integer subpid, Integer fid, Integer blockid, Long starttime,
      String type, String properties) {
    Boolean processed = Boolean.FALSE;
    try {
      Flow flow = BeanFactory.getFlowBean();
      UserInfoInterface userInfoEvent = BeanFactory.getUserInfoFactory().newUserInfoEvent(this, userId);
      flow.eventNextBlock(userInfoEvent, fid.intValue(), pid.intValue(), subpid.intValue());
      processed = Boolean.TRUE;
    } catch (Exception e) {
      Logger.error(userId, this, "processEvent", "Exception caught: ", e);
    }
    return processed;
  }
}
