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
