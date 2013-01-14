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