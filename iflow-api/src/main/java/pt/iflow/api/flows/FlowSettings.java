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
