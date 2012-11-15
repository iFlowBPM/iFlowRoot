package com.infosistema.proxy.cidadela.properties;

import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;

public final class WebServiceProperties {

  private static final String RESOURCE = "com.infosistema.proxy.cidadela.properties.webservices";
  private static final String USERNAME = "webservice.username";
  private static final String PASSWORD = "webservice.password";
  private static final String KRB5CONF = "webservice.krb5.conf.location";
  private static final String LOGINCONF = "webservice.login.conf.location";
  private static final String BASEURL = "webservice.base.url";
  private static final String HOST = "webservice.host";
  private static final String ACTIONURL = "webservice.acction.url.";

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
  
  public static String getUserName() {
    return properties.getProperty(USERNAME);
  }
  
  public static String getPassword() {
    return properties.getProperty(PASSWORD);
  }

  public static String getKrb5Conf() {
    return properties.getProperty(KRB5CONF);
  }

  public static String getLoginConf() {
    return properties.getProperty(LOGINCONF);
  }

  public static String getBaseURL() {
    try {
      return properties.getProperty(BASEURL);
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
 
  public static String getActionURL(String action) {
    try {
      return properties.getProperty(ACTIONURL + action.toLowerCase());
    } catch (Exception e) {
      return null;
    }
  }
}