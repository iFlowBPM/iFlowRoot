package pt.iflow.update;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import pt.iflow.api.utils.Logger;

/**
 * Update Manager.
 * 
 * @author Luis Cabral
 * @since 07.12.2009
 * @version 04.01.2010
 */
public class UpdateManager {

  public static final String SYS_UPDATE = "/WEB-INF/updates";
  public static final String ORG_UPDATE = "/Classes/updates";

  private UpdateManager() {
  }

  public static void run(String sysPath) {
    SystemUpdateHandler.execute(sysPath);
    RepositoryUpdateHandler.execute();
  }

  @SuppressWarnings("unchecked")
  public static Map<Class, String> getArtifacts(File root) {
    String myName = UpdateManager.class.getName();

    if (Logger.isAdminDebugEnabled()) {
      Logger.adminDebug(myName, "getArtifacts", "Searching through artifacts in: " + root.getPath());
    }
    Map<Class, String> retObj = new HashMap<Class, String>();
    try {
      if (root.exists()) {
        List<File> paths = new ArrayList<File>();
        for (File file : root.listFiles()) {
          if (file.isDirectory()) {
            paths.add(file);
          }
        }
        paths.add(root);
        List<URL> classpath = new ArrayList<URL>();
        Map<String, String> classes = new HashMap<String, String>();
        for (File path : paths) {
          classpath.add(path.toURL());
          for (File file : path.listFiles()) {
            if (file.isDirectory()) {
              // TODO add recursive support for class packages
              // classes.add((paths.contains(path) ? "" : path.getName() + ".")
              // + file.getName().substring(0, file.getName().length() - 6));
            } else if (file.getName().endsWith(".class")) {
              String name = file.getName().substring(0, file.getName().length() - 6);
              classes.put(name, path.getPath());
            }
          }
        }
        URL[] urls = (URL[]) classpath.toArray(new URL[classpath.size()]);
        ClassLoader cl = new URLClassLoader(urls, UpdateManager.class.getClassLoader());

        Iterator<String> iter = classes.keySet().iterator();
        while (iter.hasNext()) {
          String sClazz = iter.next();
          String sPath = classes.get(sClazz);
          try {
            Class clazz = (Class) cl.loadClass(sClazz);
            retObj.put(clazz, sPath);
          } catch (Exception ex) {
            Logger.adminError(myName, "getArtifacts", "Exception caught: ", ex);
          }
        }
        if (Logger.isAdminDebugEnabled()) {
          Logger.adminDebug(myName, "getArtifacts", "Found " + retObj.size() + " classes.");
        }
      }
    } catch (IOException e) {
      Logger.adminError(myName, "getArtifacts", "Exception caught: ", e);
    }
    return retObj;
  }
}
