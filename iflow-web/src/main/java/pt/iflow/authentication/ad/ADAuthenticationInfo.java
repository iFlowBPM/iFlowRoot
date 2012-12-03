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
/*
 *
 * Created on May 12, 2005 by iKnow
 *
  */

package pt.iflow.authentication.ad;

import java.util.Map;

import pt.iflow.api.authentication.AuthenticationInfo;

/**
 * 
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright (c) 2005 iKnow</p>
 * 
 * @author iKnow
 */

public class ADAuthenticationInfo implements AuthenticationInfo {

  private String _userId;
  private String _sessionId;
  
  public ADAuthenticationInfo(Map<String,String> userMap, String sessionId) {
    this._userId = userMap.get("uid");
    this._sessionId = sessionId;
  }

  /* (non-Javadoc)
   * @see pt.iknow.authentication.AuthenticationInfo#getUserId()
   */
  public String getUserId() {
    return  this. _userId;
  }

  /* (non-Javadoc)
   * @see pt.iknow.authentication.AuthenticationInfo#getSessionId()
   */
  public String getSessionId() {
    return this._sessionId;
  }

}
