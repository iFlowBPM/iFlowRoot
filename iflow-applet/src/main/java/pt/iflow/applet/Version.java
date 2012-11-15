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
