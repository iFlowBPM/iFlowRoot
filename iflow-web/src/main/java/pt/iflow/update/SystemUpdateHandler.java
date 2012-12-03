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
package pt.iflow.update;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

import pt.iflow.api.upgrades.Upgradable;

/**
 * System update handler.
 * 
 * @author Luis Cabral
 * @since 04.01.2010
 * @version 06.01.2010
 */
public class SystemUpdateHandler extends UpdateHandler {

  private static SystemUpdateHandler instance = null;

  private static SystemUpdateHandler getInstance() {
    if (instance == null) {
      instance = new SystemUpdateHandler();
    }
    return instance;
  }

  private SystemUpdateHandler() {
  }

  public static void execute(String sysPath) {
    SystemUpdateHandler.getInstance().run(new File(sysPath));
  }

  @SuppressWarnings("unchecked")
  private void run(File root) {
    debug("run", "Start");
    Map<Class, String> artifacts = UpdateManager.getArtifacts(root);
    Iterator<Class> iter = artifacts.keySet().iterator();
    while (iter.hasNext()) {
      Class clazz = iter.next();
      if (!canRunUpgradable(clazz))
        continue;
      
      String path = artifacts.get(clazz);
      try {
        Object obj = clazz.newInstance();
        if (obj instanceof Upgradable) {
          executeUpgradable(Upgradable.class.cast(obj), path);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    debug("run", "End");
  }

  protected String getSignature(Upgradable upgradable) {
    return "[SystemUpdateHandler] " + upgradable.signature();
  }
  
}
