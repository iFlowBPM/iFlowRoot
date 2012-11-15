package pt.common.properties;

import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;

public final class WebServiceProperties {

  private static final String RESOURCE = "pt.common.properties.webservices";
  private static final String USEAUTHENTICATION = "webservice.useathentication";
  private static final String DOMAINUSERNAME = "webservice.domain.username";
  private static final String USERNAME = "webservice.username";
  private static final String PASSWORD = "webservice.password";
  private static final String DOMAIN = "webservice.domain";
  private static final String HOST = "webservice.host";
  private static final String PORT = "webservice.port";
  private static final String CREATEREGISTOASSIDUIDADEURL = "webservice.createregistoassiduidade.url";
  private static final String READREGISTOASSIDUIDADEURL = "webservice.readregistoassiduidade.url";

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
  
  public static String getUserDomainName() {
    return properties.getProperty(DOMAINUSERNAME);
  }
  
  public static String getUserName() {
    return properties.getProperty(USERNAME);
  }
  
  public static String getDomain() {
    return properties.getProperty(DOMAIN);
  }

  public static String getPassword() {
    return properties.getProperty(PASSWORD);
  }

  public static URL getCreateRegistoAssiduidadeURL() {
    try {
      return new URL(properties.getProperty(CREATEREGISTOASSIDUIDADEURL));
    } catch (Exception e) {
      return null;
    }
  }

  public static URL getReadRegistoAssiduidadeURL() {
    try {
      return new URL(properties.getProperty(READREGISTOASSIDUIDADEURL));
    } catch (Exception e) {
      return null;
    }
  }

  public static String getCreateRegistoAssiduidadePath() {
    try {
      return properties.getProperty(CREATEREGISTOASSIDUIDADEURL);
    } catch (Exception e) {
      return null;
    }
  }

  public static String getReadRegistoAssiduidadePath() {
    try {
      return properties.getProperty(READREGISTOASSIDUIDADEURL);
    } catch (Exception e) {
      return null;
    }
  }

  public static String getHost() {
    try {
      return properties.getProperty(HOST);
    } catch (Exception e) {
      return null;
    }
  }

  public static int getPort() {
    try {
      return Integer.parseInt(properties.getProperty(PORT));
    } catch (Exception e) {
      return 0;
    }
  }

  public static boolean useAuthentication() {
    try {
      return properties.getProperty(PORT).equalsIgnoreCase("true");
    } catch (Exception e) {
      return false;
    }
  }
}