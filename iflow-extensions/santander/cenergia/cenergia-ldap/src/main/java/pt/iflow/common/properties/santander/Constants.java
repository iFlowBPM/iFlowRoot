package pt.iflow.common.properties.santander;

import java.io.File;
import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Utils;

public class Constants
{
  private static final String IFLOW_HOME = System.getProperty("iflow.home");
  private static final String CONFIG_FOLDER = "config" + File.separator + "extensions"  + File.separator + "cenergia";
  private static final String CONFIG_FILE = "ldapconfig.properties";

  private static final String APPNAMES = "APPNAMES";
  private static final String APPSSEPARATOR = "APPSSEPARATOR";
  private static final String APPPROFILESEPARATOR = "APPPROFILESEPARATOR";
  private static final String ORGANIZATIONADMINUSER = "ORGANIZATIONADMINUSER";
  private static final String ORGANIZATIONID = "ORGANIZATIONID";
  private static final String ORGANIZATIONNAME = "ORGANIZATIONNAME";
  private static final String ORGANIZATIONDESCRIPTION = "ORGANIZATIONDESCRIPTION";
  private static final String MAP_ORGANIZATIONID = "MAP_ORGANIZATIONID";
  private static final String MAP_ORGANIZATIONNAME = "MAP_ORGANIZATIONNAME";
  private static final String MAP_ORGANIZATIONDESCRIPTION = "MAP_ORGANIZATIONDESCRIPTION";

  private static String appNames = null;
  private static String appsSeparator = null;
  private static String appProfileSeparator = null;
  private static String organizationAdminUser = null;
  private static String organizationId = null;
  private static String organizationName = null;
  private static String organizationDescription = null;
  private static String map_OrganizationId = null;
  private static String map_OrganizationName = null;
  private static String map_OrganizationDescription = null;

  private static Properties properties = null;
  private static String _fileLocation;

  static {
    properties = new Properties();
    try
    {
      _fileLocation = IFLOW_HOME + File.separator + CONFIG_FOLDER + File.separator + CONFIG_FILE;
      properties.load(new FileInputStream(_fileLocation));

      appNames = properties.getProperty(APPNAMES);
      appsSeparator = properties.getProperty(APPSSEPARATOR);
      appProfileSeparator = properties.getProperty(APPPROFILESEPARATOR);
      organizationAdminUser = properties.getProperty(ORGANIZATIONADMINUSER);
      organizationId = properties.getProperty(ORGANIZATIONID);
      organizationName = properties.getProperty(ORGANIZATIONNAME);
      organizationDescription = properties.getProperty(ORGANIZATIONDESCRIPTION);
      map_OrganizationId = properties.getProperty(MAP_ORGANIZATIONID);
      map_OrganizationName = properties.getProperty(MAP_ORGANIZATIONNAME);
      map_OrganizationDescription = properties.getProperty(MAP_ORGANIZATIONDESCRIPTION);
    }
    catch(Throwable e) { 
      e.printStackTrace();
    }
  }

  public static List<String> getAppNames() {
    return Utils.tokenize(appNames, appsSeparator) ;
  }

  public static String getAppProfileSeparator() {
    return appProfileSeparator ;
  }

  public static String getOrganizationAdminUser() {
    return organizationAdminUser;
  }

  public static String getOrganizationId() {
    return organizationId;
  }

  public static String getOrganizationName() {
    return organizationName;
  }

  public static String getOrganizationDescription() {
    return organizationDescription;
  }

  public static String getMap_OrganizationDescription() {
    return map_OrganizationDescription;
  }

  public static String getMap_OrganizationId() {
    return map_OrganizationId;
  }

  public static String getMap_OrganizationName() {
    return map_OrganizationName;
  }

  public static String getProperty(String prop) {
    return properties.getProperty(prop);
  }
}