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
 * Upgradable behavior for implementing methods.
 * 
 * There are 2 possible upgradable approaches:<br/>
 *  - System Upgradable - extend this class, implement execute, 
 *    signature and optionaly force (to always execute). Then put
 *    created class in WEB-INF/updates[/optional_folder] and start
 *    application server.<br/>
 *  - Repository Upgradable - extend class RepositoryUpgradable, implement
 *    execute, signature and optionaly force (to always execute). Then
 *    put created class in repository_data/<org_id>/Classes/updates[/optional_folder]
 *    and start application server. This class has an additional method,
 *    getOrganizationId, with the "context" organization.<br/>  
 * 
 * Note: current implementation only supports upgradables in default package
 * 
 * @author Luis Cabral
 * @since 07.12.2009
 * @version 16.12.2009
 */
public abstract class Upgradable {

  private String path;

  /**
   * Defines a signature for the implementing class.<br>
   * Signatures should be unique for each upgradable artifact.<br>
   * A class with a signature already registered as executed is ignored (unless the execution is forced in {@link #force()}).<br>
   * The use of a <i>[VERSION]</i> tag is recommended for each artifact's signature.
   * 
   * @return String signature of implementing class.
   */
  public abstract String signature();

  /**
   * The actions to perform on the upgradable artifact.
   * 
   * @param fullpath
   *          The full path of the directory being run.
   * @throws Exception
   *           If any exception is thrown
   */
  public abstract void execute() throws UpgradableException;

  /**
   * Defines if the upgradable artifact should be parsed, disregarding all blocking mechanisms.
   * 
   * @return True the execution of the upgradable artifact should be forced, false otherwise.
   */
  public boolean force() {
    return false;
  }

  /**
   * Defines if the upgradable artifact is required, causing a system failure if the execution fails with an UpgradableException.
   * 
   * @return True the successful execution of the upgradable artifact is mandatory, false otherwise.
   */
  public boolean isRequired() {
    return false;
  }

  /**
   * Sets the path and performs an execute action on the artifact.
   * 
   * @param path
   *          Path to set
   * @throws Exception
   *           If any exception is thrown
   * @see #execute()
   * @see #setPath(String)
   * @see #getPath()
   */
  public void execute(String path) throws UpgradableException {
    this.setPath(path);
    this.execute();
  }

  /**
   * DO NOT OVERRIDE!
   * 
   * @param path
   *          Path to use
   */
  public void setPath(String path) {
    this.path = path;
  }

  protected String getPath() {
    return this.path;
  }
}
