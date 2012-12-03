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
