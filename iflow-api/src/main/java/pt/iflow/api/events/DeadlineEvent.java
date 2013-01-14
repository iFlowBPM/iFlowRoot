package pt.iflow.api.events;

import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.flows.Flow;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessHeader;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;

public class DeadlineEvent extends AbstractEvent {

  public DeadlineEvent() {
  }

  public Boolean processEvent() {
    return Boolean.TRUE;
  }

  public Integer initialEventCode() {
    return new Integer(EventManager.READY_TO_PROCESS);
  }

  public Boolean processEvent(String userId, Integer id, Integer pid, Integer subpid, Integer fid, Integer blockid, Long starttime,
      String type, String properties) {
    Boolean processed = Boolean.FALSE;
    try {
      boolean foundTag = false;
      boolean bFire = false;
      String dateVar = null;
      if (properties == null) {
        throw new Exception("properties == null");
      }
      StringTokenizer stok = new StringTokenizer(properties, ";");
      while (stok.hasMoreTokens()) {
        String token = stok.nextToken();
        if (token.indexOf("dateVar") != -1) {
          dateVar = token.substring(token.indexOf("=") + 1).trim();
          if (StringUtils.isNotEmpty(dateVar)) {
            foundTag = true;
          }
          break;
        }
      }
      if (!foundTag) {
        throw new Exception("tag 'dateVar' not in properties");
      }
      
      UserInfoInterface userInfoEvent = BeanFactory.getUserInfoFactory().newUserInfoEvent(this, userId);
      ProcessHeader ph = new ProcessHeader(fid, pid, subpid);
      ProcessData pd = BeanFactory.getProcessManagerBean().getProcessData(userInfoEvent, ph); 
      if (pd == null) {
        throw new Exception("unable to load process " + fid + ":" + pid + ":" + subpid);
      }
      Date now = Calendar.getInstance().getTime();
      Date deadline = (Date)(pd.get(dateVar).getValue());
      
      bFire = now.compareTo(deadline) > 0;

      if (bFire) {
        Flow flow = BeanFactory.getFlowBean();
        flow.eventNextBlock(userInfoEvent, fid.intValue(), pid.intValue(), subpid.intValue());
        processed = Boolean.TRUE;
      }
    } catch (Exception e) {
      Logger.error(userId, this, "processEvent", 
          "[" + fid + ":" + pid + ":" + subpid + "] Exception caught: ", e);
    }
    return processed;
  }
}
