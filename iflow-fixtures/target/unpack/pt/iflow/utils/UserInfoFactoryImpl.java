package pt.iflow.utils;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.events.AbstractEvent;
import pt.iflow.api.userdata.OrganizationData;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.UserInfoFactory;
import pt.iflow.api.utils.UserInfoInterface;

public class UserInfoFactoryImpl implements UserInfoFactory {
  
  private static UserInfoFactory instance = null;
  
  public static UserInfoFactory getInstance() {
    if(instance == null) {
      instance = new UserInfoFactoryImpl();
    }
    return instance;
  }

  public UserInfoInterface newUserInfo() {
    return new UserInfo();
  }

  public UserInfoInterface newUserInfo(String login, String password) {
    return new UserInfo(login,password);
  }

  public UserInfoInterface newUserInfoDelegate(Block block, String user) {
    return new UserInfoDelegate(block, user);
  }

  public UserInfoInterface newUserInfoDelegate(UserInfoInterface supervisor, String user) {
    return new UserInfoDelegate(supervisor, user);
  }

  public UserInfoInterface newUserInfoEvent(AbstractEvent evt, String user) {
    return new UserInfoEvent(evt, user);
  }

  public UserInfoInterface newGuestUserInfo() {
    return new UserInfo();
  }

  public UserInfoInterface newSystemUserInfo() {
    return new SystemUserInfo();
  }
  
  
  
  public UserInfoInterface newArchiverUserInfo() {
    return new UserInfoManager(){
      private static final long serialVersionUID = 9095055196095852145L;
      public boolean isSysAdmin() {
        return true;
      }
      public boolean isLogged() {
        return true;
      }
      public String getUtilizador() {
        return "ArchiveJob";
      }
    };
  }

  public UserInfoInterface newClassManager(String managedClass) {
    String myclassname = managedClass;
    if (myclassname.indexOf('.') > -1) {
      myclassname = myclassname.substring(myclassname.lastIndexOf('.')+1);
    }
    final String classname = myclassname;
    return new UserInfoManager(){
      private static final long serialVersionUID = -7471304674922501554L;
      public boolean isSysAdmin() {
        return true;
      }
      public boolean isLogged() {
        return true;
      }
      public String getUtilizador() {
        return "Manager-" + classname;
      }
    };
  }
  
  public UserInfoInterface newOrganizationGuestUserInfo(String orgId) {
    OrganizationData orgData = BeanFactory.getAuthProfileBean().getOrganizationInfo(orgId);
    return newOrganizationGuestUserInfo(orgData);
  }

  public UserInfoInterface newOrganizationGuestUserInfo(OrganizationData orgData) {
    return new UserInfoMock(orgData);
  }
}
