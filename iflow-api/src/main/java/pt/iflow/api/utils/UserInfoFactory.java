package pt.iflow.api.utils;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.events.AbstractEvent;
import pt.iflow.api.userdata.OrganizationData;

public interface UserInfoFactory {

  public UserInfoInterface newUserInfo();
  public UserInfoInterface newUserInfo(String login, String password);
  public UserInfoInterface newUserInfoDelegate(Block block, String user);
  public UserInfoInterface newUserInfoDelegate(UserInfoInterface supervisor, String user);
  public UserInfoInterface newUserInfoEvent(AbstractEvent evt, String user);
  public UserInfoInterface newGuestUserInfo();
  public UserInfoInterface newSystemUserInfo();
  public UserInfoInterface newArchiverUserInfo();
  public UserInfoInterface newClassManager(String managedClass);
  public UserInfoInterface newOrganizationGuestUserInfo(String orgId);
  public UserInfoInterface newOrganizationGuestUserInfo(OrganizationData orgData);
  
}
