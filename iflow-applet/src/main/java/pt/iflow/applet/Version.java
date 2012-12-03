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
package pt.iflow.applet;

import java.io.InputStream;
import java.util.Properties;

public class Version {

  private static final String VERSION;
  static {
    String version = "4.0.0-SNAPSHOT"; //$NON-NLS-1$
    try {
      InputStream is = Version.class.getResourceAsStream("/META-INF/maven/pt.iknow.iflow/iflow-applet/pom.properties"); //$NON-NLS-1$
      Properties prop = new Properties();
      prop.load(is);
      
      version = prop.getProperty("version", version); //$NON-NLS-1$
    } catch (Throwable t) {
      System.out.println("Version descriptor not available."); //$NON-NLS-1$
    }
    VERSION = version;
    System.out.println("iFlow Utility applet version "+VERSION); //$NON-NLS-1$
  }

  public static String getVersion() {
    return VERSION;
  }
}
