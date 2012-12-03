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
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.Map;

import pt.iflow.api.upgrades.RepositoryUpgradable;
import pt.iflow.api.upgrades.Upgradable;
import pt.iflow.api.utils.Const;

/**
 * Repository update handler.
 * 
 * @author Luis Cabral
 * @since 04.01.2010
 * @version 06.01.2010
 */
public class RepositoryUpdateHandler extends UpdateHandler {

  private static RepositoryUpdateHandler instance = null;

  private static RepositoryUpdateHandler getInstance() {
    if (instance == null) {
      instance = new RepositoryUpdateHandler();
    }
    return instance;
  }

  private RepositoryUpdateHandler() {
  }

  public static void execute() {
    RepositoryUpdateHandler.getInstance().run(new File(Const.IFLOW_HOME + "/repository_data"));
  }

  @SuppressWarnings("unchecked")
  private void run(File root) {
    debug("run", "Start");
    if (isValid(root)) {
      for (File orgRoot : root.listFiles()) {
        if (isValid(orgRoot)) {
          try {
            int orgId = Integer.parseInt(orgRoot.getName().split("_")[0]);
            File container = new File(orgRoot, UpdateManager.ORG_UPDATE);
            if (isValid(container)) {
              Map<Class, String> artifacts = UpdateManager.getArtifacts(container);
              Iterator<Class> iter = artifacts.keySet().iterator();
              while (iter.hasNext()) {
                Class clazz = iter.next();
                if (!canRunUpgradable(clazz))
                  continue;
                
                String path = artifacts.get(clazz);
                try {
                  Object obj = clazz.newInstance();
                  if (obj instanceof RepositoryUpgradable) {
                    RepositoryUpgradable upgradable = RepositoryUpgradable.class.cast(obj);
                    upgradable.setOrganizationId(orgId);
                    executeUpgradable(upgradable, path);
                  }
                } catch (Exception e) {
                  e.printStackTrace();
                }
              }
            }
          } catch (NumberFormatException ex) {
            debug("run", "Ignoring org: " + orgRoot.getName());
          }
        } else {
          debug("run", "Invalid file: " + orgRoot.toString());
        }
      }
    } else {
      debug("run", "Invalid root file: " + root.toString());
    }
    debug("run", "End");
  }

  private boolean isValid(File file) {
    return (file != null && file.exists() && file.isDirectory());
  }

  protected String getSignature(Upgradable upgradable) {
    return "[RepositoryUpdateHandler] " + upgradable.signature();
  }

}
