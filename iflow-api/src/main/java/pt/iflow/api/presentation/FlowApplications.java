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
package pt.iflow.api.presentation;

import java.util.Collection;

import pt.iflow.api.flows.FlowType;
import pt.iflow.api.flows.IFlowData;
import pt.iflow.api.utils.UserInfoInterface;

public interface FlowApplications {
   public static final int ORPHAN_GROUP_ID = -1;
   public static final String ORPHAN_GROUP_DESC = "";
  
   public Collection<IFlowData> getApplicationFlows( UserInfoInterface userInfo, int anAppID);

   public FlowMenu getAllApplicationOnlineFlows( UserInfoInterface userInfo);
   public FlowMenu getAllApplicationOnlineFlows( UserInfoInterface userInfo, FlowType type);
   public FlowMenu getAllApplicationOnlineFlows( UserInfoInterface userInfo, FlowType type, FlowType[] typeExclude);
   public FlowMenu getAllApplicationOnlineFlows( UserInfoInterface userInfo, int anAppID);
   public FlowMenu getAllApplicationOnlineFlows( UserInfoInterface userInfo, int anAppID, FlowType type, FlowType[] typeExclude);
   
   public FlowMenu getAllApplicationOnlineMenu( UserInfoInterface userInfo );
   public FlowMenu getAllApplicationOnlineMenu( UserInfoInterface userInfo, FlowType type);
   public FlowMenu getAllApplicationOnlineMenu( UserInfoInterface userInfo, FlowType type, FlowType[] typeExclude);
   public FlowMenu getAllApplicationOnlineMenu( UserInfoInterface userInfo, int anAppID);
   public FlowMenu getAllApplicationOnlineMenu( UserInfoInterface userInfo, int anAppID, char flowRolesTOPriv);
   public FlowMenu getAllApplicationOnlineMenu( UserInfoInterface userInfo, int anAppID, FlowType type, FlowType[] typeExclude);
   public FlowMenu getAllApplicationOnlineMenu( UserInfoInterface userInfo, int anAppID, FlowType type, FlowType[] typeExclude, char flowRolesTOPriv);
   public FlowMenu getAllApplicationOnlineMenu( UserInfoInterface userInfo, int anAppID, FlowType type, FlowType[] typeExclude, char flowRolesTOPriv, boolean showOnlyFlowsToBePresentInMenu);

   public FlowMenu getAllApplicationOnlineMenu( UserInfoInterface userInfo, FlowType type, boolean showOnlyFlowsToBePresentInMenu);
   public FlowMenu getAllApplicationOnlineMenu( UserInfoInterface userInfo, FlowType type, FlowType[] typeExclude, boolean showOnlyFlowsToBePresentInMenu);
}
