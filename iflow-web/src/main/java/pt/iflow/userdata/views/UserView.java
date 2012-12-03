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

}
