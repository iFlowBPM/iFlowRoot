package pt.iflow.userdata.views;

import java.util.Map;

import pt.iflow.api.userdata.views.IView;
import pt.iflow.api.userdata.views.OrganizationalUnitViewInterface;


public class OrganizationalUnitView extends AbstractView implements IView, OrganizationalUnitViewInterface {
  public OrganizationalUnitView(Map<String,String> mapping) {
    super(mapping);
  }
  
  /* (non-Javadoc)
 * @see pt.iflow.web.userdata.views.OrganizationalUnitViewInterface#getUnitId()
 */
public String getUnitId() {
    return get(UNITID);
  }

  /* (non-Javadoc)
 * @see pt.iflow.web.userdata.views.OrganizationalUnitViewInterface#getName()
 */
public String getName() {
    return get(NAME);
  }

  /* (non-Javadoc)
 * @see pt.iflow.web.userdata.views.OrganizationalUnitViewInterface#getDescription()
 */
public String getDescription() {
    return get(DESCRIPTION);
  }

  /* (non-Javadoc)
 * @see pt.iflow.web.userdata.views.OrganizationalUnitViewInterface#getOrganizationId()
 */
public String getOrganizationId() {
    return get(ORGANIZATIONID);
  }

  /* (non-Javadoc)
 * @see pt.iflow.web.userdata.views.OrganizationalUnitViewInterface#getParentId()
 */
public String getParentId() {
    return get(PARENTID);
  }

  /* (non-Javadoc)
 * @see pt.iflow.web.userdata.views.OrganizationalUnitViewInterface#getManagerId()
 */
public String getManagerId() {
    return get(MANAGERID);
  }

  /* (non-Javadoc)
 * @see pt.iflow.web.userdata.views.OrganizationalUnitViewInterface#getLevel()
 */
public String getLevel() {
	return get(LEVEL);
  }

}
