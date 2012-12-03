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
/**
 * 
 */
package pt.iflow.api.core;

import java.util.ArrayList;
import java.util.List;

import pt.iflow.api.transition.ProfilesTO;
import pt.iflow.api.utils.InterfaceInfo;
import pt.iflow.api.utils.UserInfoInterface;

/**
 * @author helder
 *
 */
public interface InterfacesManager{
  
  public InterfaceInfo[] getAllInterfaces();
  public String[] getProfilesForInterface(UserInfoInterface userInfo, String idInterface);

  public boolean addProfileToInterface(UserInfoInterface userInfo, String interfaceId, String profileId);
  public boolean removeProfileFromInterface(UserInfoInterface userInfo, String interfaceId, String profileId);
  public boolean isInterfaceDisabledByDefault(UserInfoInterface userInfo,String idInterface);
  
  
  public int[] tabsRejeitadas(UserInfoInterface userInfo, String perfil);
}
