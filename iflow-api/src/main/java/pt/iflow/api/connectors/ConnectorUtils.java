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
package pt.iflow.api.connectors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.NameValuePair;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;

public class ConnectorUtils {

  public static String SEPARATOR = Const.DEFAULT_SEPARATOR;
  public static String CONNECTOR_PKG = Const.CONNECTOR_DEFAULT_PKG;

  
  @SuppressWarnings("unchecked")
  public static Class<? extends ConnectorInterface> fetchConnector(UserInfoInterface userInfo, String connector) 
    throws ClassNotFoundException {

    try {
      return (Class<? extends ConnectorInterface>) BeanFactory.getRepBean().loadClass(userInfo, connector); 
    }
    catch (ClassNotFoundException cne) {
      return (Class<? extends ConnectorInterface>)Class.forName(connector);
    }
  }

  @SuppressWarnings("unchecked")
  public static NameValuePair<String, Class<ConnectorInterface>>[] fetchConnectors(UserInfoInterface userInfo) {
    List<NameValuePair<String, Class<ConnectorInterface>>> retObj = new ArrayList<NameValuePair<String, Class<ConnectorInterface>>>();
    try {
      List<Class<?>> aClazz = findConnectors(userInfo);
      if (aClazz != null) {
        for (Class clazz : aClazz) {
          try {
            Object obj = clazz.newInstance();
            if (obj instanceof ConnectorInterface) {
              ConnectorInterface con = (ConnectorInterface) obj;
              retObj.add(new NameValuePair<String, Class<ConnectorInterface>>(
                  con.getDescription(userInfo), (Class<ConnectorInterface>) con.getClass()));
            }
          } catch (InstantiationException ex) {
            continue; // ignored
          }
        }
      }
    } catch (Exception e) {
      Logger.error(userInfo.getUtilizador(), ConnectorInterface.class, "fetchImplementations", "Exception caught: ", e);
    }
    return (NameValuePair<String, Class<ConnectorInterface>>[]) retObj.toArray(new NameValuePair[retObj.size()]);
  }
  
  @SuppressWarnings("unchecked")
  private static List<Class<?>> findConnectors(UserInfoInterface userInfo) {
    
    List<Class<?>> classes = BeanFactory.getRepBean().listClasses(userInfo, CONNECTOR_PKG);
    
    Set<String> classSet = new HashSet<String>();
    for (Class<?> clazz : classes) {
      classSet.add(clazz.getName());
    }
    
    try {
      List<Class> jvmClasses = Utils.getClasses(CONNECTOR_PKG);
      
      for (Class clazz : jvmClasses) {
        if (classSet.contains(clazz.getName()))
          continue;
        
        classes.add(clazz);
      }
      
    } catch (Exception e) {
      Logger.warning(userInfo.getUtilizador(), ConnectorInterface.class, "findConnectors", 
          "Exception caught getting jvm classes", e);
    }
    
    return classes;
  }
  
}
