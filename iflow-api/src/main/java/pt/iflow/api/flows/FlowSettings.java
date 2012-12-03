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
package pt.iflow.api.flows;

import java.io.PrintStream;
import java.util.List;
import java.util.Set;

import pt.iflow.api.utils.UserInfoInterface;

public interface FlowSettings {

  public void saveFlowSettings(UserInfoInterface userInfo, FlowSetting[] afsaSettings);

  public void saveFlowSettings(UserInfoInterface userInfo, FlowSetting[] afsaSettings, boolean abInitSettings);

  public void exportFlowSettings(UserInfoInterface userInfo, int flowid, PrintStream apsOut);

  public String importFlowSettings(UserInfoInterface userInfo, int flowid, byte[] file);

  public FlowSetting[] getFlowSettings(UserInfoInterface userInfo, int flowid);

  public void refreshFlowSettings(UserInfoInterface userInfo, int flowid);
  
  public FlowSetting getFlowSetting(int flowid, String settingVar);

  public boolean removeFlowSetting(UserInfoInterface userInfo, int flowId, String name);

  public FlowSetting[] getFlowSettings(int flowid);
  
  public List<FlowSetting> getDefaultSettings(int anFlowId);
  
  public Set<String> getDefaultSettingsNames();
  
  public boolean isGuestAccessible(UserInfoInterface userInfo, int flowId);
  
  public void addFlowSettingsListener(String id, FlowSettingsListener listener);
  public void removeFlowSettingsListener(String id);
}
