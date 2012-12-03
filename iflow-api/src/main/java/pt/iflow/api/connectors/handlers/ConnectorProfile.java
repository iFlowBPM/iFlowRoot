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
package pt.iflow.api.connectors.handlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pt.iflow.api.connectors.ConnectorInterface;
import pt.iflow.api.core.AuthProfile;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.userdata.UserData;
import pt.iflow.api.utils.UserInfoInterface;

public class ConnectorProfile implements ConnectorInterface {

  private static final String SIGNATURE = "ConnectorProfile";
  private static final String ARG_NAME = SIGNATURE + "_name";

  public List<SelectItem> dispatch(UserInfoInterface userInfo, ProcessData procData, Object... args) {
    List<SelectItem> retObj = new ArrayList<SelectItem>();
    AuthProfile ap = BeanFactory.getAuthProfileBean();
    for (Object arg : args) {
      if (arg instanceof String) {
        Collection<String> users = ap.getUsersInProfile(userInfo, (String) arg);
        for (String user : users) {
          UserData uData = ap.getUserInfo(user);
          retObj.add(new SelectItem(uData.get(UserData.ID), user));
        }
      }
    }
    return retObj;
  }

  public String getDescription(UserInfoInterface userInfo) {
    String description = ConnectorInterface.SIGNATURE + "." + SIGNATURE;
    if (userInfo != null) {
      description = userInfo.getMessages().getString(description);
    }
    return description;
  }

  public String getArgsInfo() {
    return ARG_NAME;
  }
}
