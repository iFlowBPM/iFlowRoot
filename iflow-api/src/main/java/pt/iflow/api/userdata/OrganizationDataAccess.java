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
