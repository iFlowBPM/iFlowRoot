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
import pt.iflow.api.userdata.views.ThemeViewInterface;


public class ThemeView  extends AbstractView implements IView, ThemeViewInterface  {
  public ThemeView(Map<String,String> mapping) {
    super(mapping);
  }

  /* (non-Javadoc)
 * @see pt.iflow.web.userdata.views.ThemeViewInterface#getOrganizationId()
 */
public String getOrganizationId() {
    return get(ORGANIZATIONID);
  }

  /* (non-Javadoc)
 * @see pt.iflow.web.userdata.views.ThemeViewInterface#getTheme()
 */
public String getTheme() {
    return get(THEME);
  }
  /* (non-Javadoc)
 * @see pt.iflow.web.userdata.views.ThemeViewInterface#getStyle()
 */
public String getStyle() {
    return get(STYLE_URL);
  }
  /* (non-Javadoc)
 * @see pt.iflow.web.userdata.views.ThemeViewInterface#getLogo()
 */
public String getLogo() {
    return get(LOGO_URL);
  }
}
