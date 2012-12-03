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
import pt.iflow.api.userdata.views.OrganizationViewInterface;


public class OrganizationView  extends AbstractView implements IView, OrganizationViewInterface  {
  public OrganizationView(Map<String,String> mapping) {
    super(mapping);
  }

  /* (non-Javadoc)
 * @see pt.iflow.web.userdata.views.OrganizationViewInterface#getOrganizationId()
 */
public String getOrganizationId() {
    return get(ORGANIZATIONID);
  }

  /* (non-Javadoc)
 * @see pt.iflow.web.userdata.views.OrganizationViewInterface#getName()
 */
public String getName() {
    return get(NAME);
  }

  /* (non-Javadoc)
 * @see pt.iflow.web.userdata.views.OrganizationViewInterface#getDescription()
 */
public String getDescription() {
    return get(DESCRIPTION);
  }

  /* (non-Javadoc)
 * @see pt.iflow.web.userdata.views.OrganizationViewInterface#isLocked()
 */
public boolean isLocked() {
    return "1".equals(get(LOCKED));
  }

}
