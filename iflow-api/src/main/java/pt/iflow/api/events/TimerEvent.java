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
package pt.iflow.api.events;

import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.flows.Flow;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;

public class TimerEvent extends AbstractEvent {

  public TimerEvent() {
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
      long duration = 0;
      boolean foundTag = false;
      boolean bWorkDays = false;
      boolean bFire = false;
      if (properties == null) {
        throw new Exception("properties == null");
      }
      StringTokenizer stok = new StringTokenizer(properties, ";");
      while (stok.hasMoreTokens()) {
        String token = stok.nextToken();
        if (token.indexOf("minutes") != -1) {
          duration = Long.parseLong(token.substring(token.indexOf("=") + 1).trim());
          foundTag = true;
        } else if (token.indexOf("workingdays") != -1) {
          if (token.substring(token.indexOf("=") + 1).equals("true"))
            bWorkDays = true;
        } else if (token.indexOf("days") != -1) {
          duration = Long.parseLong(token.substring(token.indexOf("=") + 1).trim());
          duration = duration * 60 * 24;
          foundTag = true;
        }
      }
      if (!foundTag) {
        throw new Exception("tag 'minutes' or 'days' not in properties");
      }
      if (bWorkDays) {
        Date dtStartTime = new Date(starttime.longValue());
        Date dtNowTime = Calendar.getInstance().getTime();
        long diffMinutes = Utils.workMinutesDifference(dtStartTime, dtNowTime);
        if (diffMinutes >= duration) {
          bFire = true;
        }
      } else {
        long fireTime = starttime.longValue() + duration * 60000;
        long nowTime = Calendar.getInstance().getTime().getTime();
        if (nowTime >= fireTime) {
          bFire = true;
        }
      }
      if (bFire) {
        Flow flow = BeanFactory.getFlowBean();
        UserInfoInterface userInfoEvent = BeanFactory.getUserInfoFactory().newUserInfoEvent(this, userId);
        flow.eventNextBlock(userInfoEvent, fid.intValue(), pid.intValue(), subpid.intValue());
        processed = Boolean.TRUE;
      }
    } catch (Exception e) {
      Logger.error(userId, this, "processEvent", "Exception caught: ", e);
    }
    return processed;
  }
}
