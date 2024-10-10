package pt.iflow.core;

import pt.iflow.api.core.AdministrationFlowScheduleInterface;
import pt.iflow.api.core.AdministrationProcessManager;
import pt.iflow.api.core.AuthProfile;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.CodeTemplateManager;
import pt.iflow.api.core.DatabaseManager;
import pt.iflow.api.core.InterfacesManager;
import pt.iflow.api.core.PassImage;
import pt.iflow.api.core.ProcessManager;
import pt.iflow.api.core.ReportManager;
import pt.iflow.api.core.Repository;
import pt.iflow.api.core.Settings;
import pt.iflow.api.core.UserManager;
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
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoFactory;
import pt.iflow.delegations.DelegationInfoBean;
import pt.iflow.errors.ErrorManager;
import pt.iflow.flows.FlowBean;
import pt.iflow.flows.FlowHolderBean;
import pt.iflow.flows.FlowSettingsBean;
import pt.iflow.processannotation.ProcessAnnotationManagerBean;
import pt.iflow.user_passimage.PassImageBean;
import pt.iflow.utils.UserInfoFactoryImpl;

/**
 * Don't forget to register implementation "mapping" in services resource file
 * Discovery will use it to find which beanfactory implementation to use.
 * 
 */
public class BeanFactoryImpl extends BeanFactory {

  @Override
  protected AuthProfile doGetAuthProfileBean() {
    return AuthProfileBean.getInstance();
  }

  @Override
  protected DelegationInfo doGetDelegationInfoBean() {
    return DelegationInfoBean.getInstance();
  }

  @Override
  @SuppressWarnings({ "rawtypes", "unchecked" })
  protected Documents doGetDocumentsBean() {
	  if(Const.DOCS_DAO_CLASS==null)
		  return DocumentsBean.getInstance();
	  else try {			
			Class DocumentsDAO = Class.forName(Const.DOCS_DAO_CLASS);
			return (Documents) DocumentsDAO.getMethod("getInstance").invoke(null);
		} catch (Exception e) {
			Logger.error("iFlow", this, "doGetDocumentsBean", "Error getting Documents DAO, Class=" + Const.DOCS_DAO_CLASS, e);
			return null;
		}
  }

  @Override
  protected IErrorManager doGetErrorManagerBean() {
    return ErrorManager.getInstance();
  }

  @Override
  protected FlowApplications doGetFlowApplicationsBean() {
    return FlowApplicationsBean.getInstance();
  }

  @Override
  protected Flow doGetFlowBean() {
    return FlowBean.getInstance();
  }

  @Override
  protected FlowHolder doGetFlowHolderBean() {
    return FlowHolderBean.getInstance();
  }

  @Override
  protected FlowSettings doGetFlowSettingsBean() {
    return FlowSettingsBean.getInstance();
  }

  @Override
  protected NotificationManager doGetNotificationManagerBean() {
    return NotificationManagerBean.getInstance();
  }

  @Override
  protected OrganizationTheme doGetOrganizationThemeBean() {
    return OrganizationThemeBean.getInstance();
  }

  @Override
  protected ProcessManager doGetProcessManagerBean() {
    return ProcessManagerBean.getInstance();
  }

  @Override
  protected PassImage doGetPassImageBean() {
    return PassImageBean.getInstance();
  } 
  
  @Override
  protected ReportManager doGetReportManagerBean() {
    return ReportManagerBean.getInstance();
  }

  @Override
  protected Repository doGetRepBean() {
    return RepositoryBean.getInstance();
  }

  @Override
  protected Settings doGetSettingsBean() {
    return SettingsBean.getInstance();
  }

  @Override
  protected UserManager doGetUserManagerBean() {
    return UserManagerBean.getInstance();
  }

  @Override
  protected UserInfoFactory doGetUserInfoFactory() {
    return UserInfoFactoryImpl.getInstance();
  }

  @Override
  protected DatabaseManager doGetDatabaseManagerBean() {
    return DatabaseManagerBean.getInstance();
  }

  @Override
  protected InterfacesManager doGetInterfacesManagerBean() {
    return InterfacesManagerBean.getInstance();
  }

  @Override
  protected AdministrationProcessManager doGetAdministrationProcessManagerBean() {
    return AdministrationProcessManagerBean.getInstance();
  }

  @Override
  protected AdministrationFlowScheduleInterface doGetAdministrationFlowScheduleBean() {
    return AdministrationFlowScheduleBean.getInstance();
  }

  @Override
  protected FolderManagerBean doGetFolderManagerBean() {
    return FolderManagerBean.getInstance();
  }

  @Override
  protected ProcessAnnotationManager doGetProcessAnnotationManagerBean() {
    return ProcessAnnotationManagerBean.getInstance();
  }

  @Override
  protected CodeTemplateManager doGetCodeTemplateManager() {
    return CodeTemplateManagerBean.getInstance();
  }

}
