/*
 *
 * Created on Oct 7, 2005 by mach
 *
  */

package pt.iflow.sidebar;

import pt.iflow.api.utils.UserInfoInterface;

public interface SidebarGenerator {
  public String generateFlowData(UserInfoInterface user);
}
