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
