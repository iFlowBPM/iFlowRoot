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
 * Created on Oct 7, 2005 by mach
 *
  */

package pt.iflow.sidebar;

import java.util.Iterator;
import java.util.Map;

import pt.iflow.api.core.Activity;
import pt.iflow.api.flows.IFlowData;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.info.DefaultInfoGenerator;

/**
 * 
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright (c) 2005 mach</p>
 * 
 * @author mach
 */

public class DesktopSidebarGenerator implements SidebarGenerator {
  
  private static DefaultInfoGenerator _infoGenerator;
  
  static {
    _infoGenerator = new DefaultInfoGenerator();
  }

  /* (non-Javadoc)
   * @see pt.iknow.sidebar.SidebarGenerator#generateFlowData(pt.iknow.utils.UserInfo)
   */
  public String generateFlowData(UserInfoInterface user) {
    Iterator<IFlowData> iter = _infoGenerator.generateFlowsInfo(user, -1).iterator();
    Map<Integer,Iterator<Activity>> map = _infoGenerator.generateTaskInfo(user);
    StringBuffer buffer = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    buffer.append("<data>");
    while(iter.hasNext()) {
      IFlowData data = iter.next();
      buffer.append("<flow>");
      buffer.append("<name>").append(data.getName()).append("</name>");
      buffer.append("<id>").append(data.getId()).append("</id>");
      
      Iterator<Activity> flowActivities = map.get(new Integer(data.getId()));
      
      while(flowActivities.hasNext()) {
        Activity theActivity = flowActivities.next();
        buffer.append("<activity>");
        buffer.append("<description>").append(theActivity.getDescription()).append("</description>");
        buffer.append("<id>").append(theActivity.getPid()).append("</id>");
        buffer.append("<subid>").append(theActivity.getSubpid()).append("</subid>");
        buffer.append("</activity>");
      }

      buffer.append("</flow>");
      
    }
    buffer.append("</data>");
    return buffer.toString();
    
  }



}
