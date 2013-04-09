package pt.iflow.userdata.views;

import java.util.Map;

import pt.iflow.api.userdata.views.IView;
import pt.iflow.api.userdata.views.UserViewInterface;


public class UserView extends AbstractView implements IView, UserViewInterface {

  public UserView(Map<String,String> mapping) {
    super(mapping);
  }
  
  /* (non-Javadoc)
 * @see pt.iflow.web.userdata.views.userViewInterface#getUserId()
 */
public String getUserId() {
    return get(USERID);
  }
  
  /* (non-Javadoc)
 * @see pt.iflow.web.userdata.views.userViewInterface#getEmail()
 */
public String getEmail() {
    return get(EMAIL);
  }
  
  /* (non-Javadoc)
 * @see pt.iflow.web.userdata.views.userViewInterface#getUsername()
 */
public String getUsername() {
    return get(USERNAME);
  }
  
  /* (non-Javadoc)
 * @see pt.iflow.web.userdata.views.userViewInterface#getFirstName()
 */
public String getFirstName() {
    return get(FIRST_NAME);
  }
  
  /* (non-Javadoc)
 * @see pt.iflow.web.userdata.views.userViewInterface#getLastName()
 */
public String getLastName() {
    return get(LAST_NAME);
  }
  
  /* (non-Javadoc)
 * @see pt.iflow.web.userdata.views.userViewInterface#getUnitId()
 */
public String getUnitId() {
    return get(UNITID);
  }
  
  /* (non-Javadoc)
 * @see pt.iflow.web.userdata.views.userViewInterface#getMobileNumber()
 */
public String getMobileNumber() {
    return get(MOBILE);
  }

  /* (non-Javadoc)
 * @see pt.iflow.web.userdata.views.userViewInterface#getFax()
 */
public String getFax() {
    return get(FAX);
  }

  /* (non-Javadoc)
 * @see pt.iflow.web.userdata.views.userViewInterface#getGender()
 */
public String getGender() {
    return get(GENDER);
  }

  /* (non-Javadoc)
 * @see pt.iflow.web.userdata.views.userViewInterface#getCompanyPhone()
 */
public String getCompanyPhone() {
    return get(COMPANY_PHONE);
  }

  /* (non-Javadoc)
 * @see pt.iflow.web.userdata.views.userViewInterface#getPhoneNumber()
 */
public String getPhoneNumber() {
    return get(PHONE);
  }

  /* (non-Javadoc)
 * @see pt.iflow.web.userdata.views.userViewInterface#getActivated()
 */
public String getActivated() {
	return get(ACTIVATED);
  }

  /* (non-Javadoc)
 * @see pt.iflow.web.userdata.views.userViewInterface#getOrgAdm()
 */
public String getOrgAdm() {
    return get(ORGADM);
  }

  public String getOrgAdmFlows() {
    return get(ORGADM_FLOWS);
  }

  public String getOrgAdmOrg() {
    return get(ORGADM_ORG);
  }

  public String getOrgAdmProcesses() {
    return get(ORGADM_PROCESSES);
  }

  public String getOrgAdmResources() {
    return get(ORGADM_RESOURCES);
  }

  public String getOrgAdmUsers() {
    return get(ORGADM_USERS);
  }

}
