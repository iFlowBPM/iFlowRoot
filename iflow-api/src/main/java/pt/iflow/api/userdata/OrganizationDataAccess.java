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
 * Created on May 16, 2005 by iKnow
 *
  */

package pt.iflow.api.userdata;

import java.util.Properties;

import pt.iflow.api.userdata.OrganizationData;
import pt.iflow.api.userdata.OrganizationalUnitData;

/**
 * 
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright (c) 2005 iKnow</p>
 * 
 * @author iKnow
 */

public interface OrganizationDataAccess {

  public abstract OrganizationalUnitData getOrganizationalUnit(String unitId);
  public abstract OrganizationalUnitData getOrganizationalUnitParent(String unitId);
  public abstract OrganizationData getOrganization(String orgId);
  public abstract OrganizationData[] getOrganizations();
  public abstract void init(Properties params);
  public abstract boolean canModifyOrganization();

  // API improvements? Hope so!
  /**
   * 
   * Get the Organization to which the organizational unit belongs. 
   * 
   * @param unitId
   * @return
   */
  public abstract OrganizationData getUnitOrganization(String unitId);
}
