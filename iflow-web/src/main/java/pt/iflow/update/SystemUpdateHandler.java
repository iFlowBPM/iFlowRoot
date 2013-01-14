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
