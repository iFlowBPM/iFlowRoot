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
/*
 *
 * Created on May 31, 2005 by iKnow
 *
 */

package pt.iflow.userdata.ad;

import java.util.Properties;

import pt.iflow.api.userdata.OrganizationDataAccess;
import pt.iflow.api.utils.Logger;

/**
 * Connector to AD directory
 * 
 * @author iKnow
 * @deprecated please use ADSingleOrganizationDataAccess or
 *             ADMultiOrganizationDataAccess
 */
@Deprecated
public class ADOrganizationDataAccess extends ADSingleOrganizationDataAccess implements OrganizationDataAccess {

  public void init(Properties parameters) {
    Logger.warning(null, this, "init", "Using deprecated class ADOrganizationDataAccess.");
    Logger.warning(null, this, "init", "Please update to pt.iknow.userdata.ad.ADSingleOrganizationDataAccess");
    super.init(parameters);
  }
}
