package pt.iflow.utils;

import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.UserInfoManagerInterface;

public class UserInfoManager extends UserInfo implements UserInfoManagerInterface {
  private static final long serialVersionUID = -8947079402134702534L;

  private String orgId = Const.SYSTEM_ORGANIZATION;

  @Override
  public String getOrganization() {
    return orgId;
  }

  public void resetOrganizationId() {
    this.orgId = Const.SYSTEM_ORGANIZATION;
  }

  public void setOrganizationId(String orgId) {
    this.orgId = orgId;    
  }
}
