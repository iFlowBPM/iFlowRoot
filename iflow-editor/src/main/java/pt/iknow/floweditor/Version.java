package pt.iknow.floweditor;

import java.io.InputStream;
import java.util.Properties;

public class Version {

  public static final String VERSION;
  
  
  static {
    String version = "4.0"; //$NON-NLS-1$
    try {
      InputStream is = Version.class.getResourceAsStream("/META-INF/maven/pt.iknow.iflow/flow-editor/pom.properties"); //$NON-NLS-1$
      Properties prop = new Properties();
      prop.load(is);
      
      version = prop.getProperty("version", version); //$NON-NLS-1$
    } catch (Throwable t) {
      FlowEditor.log("Version descriptor not available."); //$NON-NLS-1$
    }
    VERSION = version;
    FlowEditor.log("Flow Editor version "+VERSION); //$NON-NLS-1$
  }

}
