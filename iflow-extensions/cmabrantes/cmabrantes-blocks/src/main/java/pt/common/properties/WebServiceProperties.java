package pt.common.properties;

import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;

public final class WebServiceProperties {

  private static final String RESOURCE = "pt.common.properties.webservices";
  private static final String CIDADELAPROXYURL = "webservice.cidadela.proxy.url";

  private static Properties properties = null;

  static  {
    properties = new Properties();
    try {
      ResourceBundle resourceBundle = ResourceBundle.getBundle(RESOURCE);
      Enumeration keys = resourceBundle.getKeys();
      String key;
      while (keys.hasMoreElements()) {
        key = (String) keys.nextElement();
        properties.setProperty(key, resourceBundle.getString(key));
      }
    } catch (Throwable throwable) {
    }
  }

  public static URL getCidadelaProxyURL() {
    try {
      return new URL(properties.getProperty(CIDADELAPROXYURL));
    } catch (Exception e) {
      return null;
    }
  }
}