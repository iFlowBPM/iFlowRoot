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
