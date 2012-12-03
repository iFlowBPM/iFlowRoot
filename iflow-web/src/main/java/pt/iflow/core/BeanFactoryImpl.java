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
package pt.iflow.core;

import pt.iflow.api.core.AdministrationFlowScheduleInterface;
import pt.iflow.api.core.AdministrationProcessManager;
import pt.iflow.api.core.AuthProfile;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.InterfacesManager;
import pt.iflow.api.core.DatabaseManager;
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
  protected Documents doGetDocumentsBean() {
    return DocumentsBean.getInstance();
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

}
