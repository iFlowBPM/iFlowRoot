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
