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
package pt.iflow.api.upgrades;

/**
 * Repository upgrade behavior for implementing methods.
 * 
 * @see Upgradable
 * 
 * @author Luis Cabral
 * @since 04.01.2010
 * @version 06.01.2010
 */
public abstract class RepositoryUpgradable extends Upgradable {

  private int organizationId;

  /**
   * Sets the path and performs an execute action on the artifact.
   * 
   * @param path
   *          Path to set
   * @param orgId
   *          Organization to upgrade
   * @throws Exception
   *           If any exception is thrown
   * @see #execute()
   * @see #execute(String)
   * @see #setPath(String)
   * @see #setOrganizationId(int)
   */
  public void execute(String path, int orgId) throws UpgradableException {
    this.setOrganizationId(orgId);
    this.execute(path);
  }

  /**
   * DO NOT OVERRIDE!
   * 
   * @param organizationId
   *          Organization ID
   */
  public void setOrganizationId(int organizationId) {
    this.organizationId = organizationId;
  }

  protected int getOrganizationId() {
    return this.organizationId;
  }
}
