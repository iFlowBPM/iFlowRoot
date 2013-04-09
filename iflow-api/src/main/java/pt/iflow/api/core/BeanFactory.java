package pt.iflow.api.core;

import org.apache.commons.discovery.tools.DiscoverSingleton;

import pt.iflow.api.delegations.DelegationInfo;
import pt.iflow.api.documents.Documents;
import pt.iflow.api.errors.IErrorManager;
import pt.iflow.api.flows.Flow;
import pt.iflow.api.flows.FlowHolder;
import pt.iflow.api.flows.FlowSettings;
import pt.iflow.api.notification.NotificationManager;
import pt.iflow.api.presentation.FlowApplications;
import pt.iflow.api.presentation.OrganizationTheme;
import pt.iflow.api.processannotation.ProcessAnnotationManager;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.Setup;
import pt.iflow.api.utils.UserInfoFactory;

public abstract class BeanFactory {

  private static final class BeanFactoryError extends Error {
    private static final long serialVersionUID = -8666419168994261983L;

    public BeanFactoryError(String msg, Throwable cause) {
      super(msg,cause);
    }

  }

  private static BeanFactory instance = null;

  static BeanFactory getInstance() {
    if(null == instance) {
      try {
        instance = (BeanFactory)DiscoverSingleton.find(BeanFactory.class, Setup.getProperties());
      } catch (Exception e) {
        Logger.error(null, BeanFactory.class, "getInstance", "Error creating BeanFactory instance", e);
        // check if a runtime error should be thrown
        throw new BeanFactoryError("Could not instantiate bean factory", e);
      }
    }
    return instance;
  }

  protected abstract AuthProfile doGetAuthProfileBean();

  protected abstract ProcessManager doGetProcessManagerBean();
  
  protected abstract PassImage doGetPassImageBean();
  
  protected abstract ReportManager doGetReportManagerBean();
  
  protected abstract UserManager doGetUserManagerBean();

  protected abstract DelegationInfo doGetDelegationInfoBean();

  protected abstract Documents doGetDocumentsBean();

  protected abstract Flow doGetFlowBean();

  protected abstract FlowHolder doGetFlowHolderBean();

  protected abstract FlowSettings doGetFlowSettingsBean();

  protected abstract FlowApplications doGetFlowApplicationsBean();

  protected abstract OrganizationTheme doGetOrganizationThemeBean();

  protected abstract Repository doGetRepBean();

  protected abstract Settings doGetSettingsBean();

  protected abstract NotificationManager doGetNotificationManagerBean();

  protected abstract IErrorManager doGetErrorManagerBean();

  protected abstract UserInfoFactory doGetUserInfoFactory();

  protected abstract DatabaseManager doGetDatabaseManagerBean();

  protected abstract InterfacesManager doGetInterfacesManagerBean();
  
  protected abstract FolderManager doGetFolderManagerBean();

  protected abstract ProcessAnnotationManager doGetProcessAnnotationManagerBean();

  protected abstract AdministrationProcessManager doGetAdministrationProcessManagerBean();

  protected abstract AdministrationFlowScheduleInterface doGetAdministrationFlowScheduleBean();

  protected abstract CodeTemplateManager doGetCodeTemplateManager();

  ///////////////////////////////////////////////////////////
  public static AuthProfile getAuthProfileBean() {
    return getInstance().doGetAuthProfileBean();
  }

  public static ProcessManager getProcessManagerBean() {
    return getInstance().doGetProcessManagerBean();
  }

  public static PassImage getPassImageManagerBean() {
        return getInstance().doGetPassImageBean();
      }
  
  public static ReportManager getReportManagerBean() {
    return getInstance().doGetReportManagerBean();
  }

  public static UserManager getUserManagerBean() {
    return getInstance().doGetUserManagerBean();
  }

  public static DelegationInfo getDelegationInfoBean() {
    return getInstance().doGetDelegationInfoBean();
  }

  public static Documents getDocumentsBean() {
    return getInstance().doGetDocumentsBean();
  }

  public static Flow getFlowBean() {
    return getInstance().doGetFlowBean();
  }

  public static FlowHolder getFlowHolderBean() {
    return getInstance().doGetFlowHolderBean();
  }

  public static FlowSettings getFlowSettingsBean() {
    return getInstance().doGetFlowSettingsBean();
  }

  public static FlowApplications getFlowApplicationsBean() {
    return getInstance().doGetFlowApplicationsBean();
  }

  public static OrganizationTheme getOrganizationThemeBean() {
    return getInstance().doGetOrganizationThemeBean();
  }

  public static Repository getRepBean() {
    return getInstance().doGetRepBean();
  }

  public static Settings getSettingsBean() {
    return getInstance().doGetSettingsBean();
  }

  public static NotificationManager getNotificationManagerBean() {
    return getInstance().doGetNotificationManagerBean();
  }

  public static IErrorManager getErrorManagerBean() {
    return getInstance().doGetErrorManagerBean();
  }

  public static UserInfoFactory getUserInfoFactory() {
    return getInstance().doGetUserInfoFactory();
  }

  public static DatabaseManager getDatabaseManagerBean() {
    return getInstance().doGetDatabaseManagerBean();
  }

  public static InterfacesManager getInterfacesManager() {
    return getInstance().doGetInterfacesManagerBean();
  }
  
  public static FolderManager getFolderManagerBean() {
    return getInstance().doGetFolderManagerBean();
  }

  public static ProcessAnnotationManager getProcessAnnotationManagerBean() {
    return getInstance().doGetProcessAnnotationManagerBean();
  }

  public static AdministrationProcessManager getAdministrationProcessManagerBean() {
    return getInstance().doGetAdministrationProcessManagerBean();
  }

  public static AdministrationFlowScheduleInterface getAdministrationFlowScheduleBean() {
    return getInstance().doGetAdministrationFlowScheduleBean();
  }

  public static CodeTemplateManager getCodeTemplateManagerBean() {
    return getInstance().doGetCodeTemplateManager();
  }
}
