package pt.totta.ldap.setup;

import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * This class implements the Ldap connection setup parameters.
 * @author João Lopes (joao.lopes@santander.pt)
 * @version 3.7
 */
public class Setup
{
  // Setup data.
  private static final String INITIALCONTEXTFACTORY = "com.sun.jndi.ldap.LdapCtxFactory";

  private static String PROVIDERURL = null;
  private static String DCRHPROVIDERURL = null;

  private static final String SECURITYAUTHENTICATION = "simple";
  private static final String SECURITYPRINCIPAL = "uid=api_ldap, ou=Sistemas, ou=Utilizadores, o=Grupo Santander Totta, c=pt, o=Grupo Santander";

  private static String SECURITYCREDENTIALS = null;
  
  private static final String APPLICATIONROOT = "au=Application,o=Grupo Santander Totta,c=PT,o=Grupo Santander";
  private static final String USERROOT = "ou=Utilizadores,o=Grupo Santander Totta,c=PT,o=Grupo Santander";
  private static final String ORGANICALUNITROOT = "ou=Centros,o=Grupo Santander Totta,c=PT,o=Grupo Santander";

  private static final String PROP_FILE = "pt.totta.ldap.setup.ldap";
  private static Properties properties = null;
  
  public Setup() {
      if(properties == null) {
          properties = getProperties(PROP_FILE);
          PROVIDERURL = properties.getProperty("PROVIDERURL");
          DCRHPROVIDERURL = properties.getProperty("DCRHPROVIDERURL");
          SECURITYCREDENTIALS = properties.getProperty("SECURITYCREDENTIALS");
      }

  }
  
  public static Properties getProperties(String resource)
  {
      Properties properties = new Properties();
      try
      {
          ResourceBundle resourceBundle = ResourceBundle.getBundle(resource);
          Enumeration keys = resourceBundle.getKeys();
          String key = "";
          for(; keys.hasMoreElements(); properties.setProperty(key, resourceBundle.getString(key)))
          {
              key = (String)keys.nextElement();
          }

      }
      catch(Throwable e) { 
          e.printStackTrace();
      }
      
      return properties;
  }
  /**
   * This method returns the InitialContextFactory setup parameter.
   * <p>
   * @return The initial context factory.
   */
  public String getInitialContextFactory()
  {
    return INITIALCONTEXTFACTORY;
  }

  /**
   * This method returns the ProviderUrl setup parameter.
   * <p>
   * @return The provider url.
   */
  public String getProviderUrl()
  {
    return PROVIDERURL;
  }

  /**
   * This method returns the DCRH ProviderUrl setup parameter.
   * <p>
   * @return The DCRH provider url.
   */
  public String getDcrhProviderUrl()
  {
    return DCRHPROVIDERURL;
  }

  /**
   * This method returns the SecurityAuthentication setup parameter.
   * <p>
   * @return The security authentication.
   */
  public String getSecurityAuthentication()
  {
    return SECURITYAUTHENTICATION;
  }

  /**
   * This method returns the SecurityPrincipal setup parameter.
   * <p>
   * @return The security principal.
   */
  public String getSecurityPrincipal()
  {
    return SECURITYPRINCIPAL;
  }

  /**
   * This method returns the SecurityCredentials setup parameter.
   * <p>
   * @return The security credentials.
   */
  public String getSecurityCredentials()
  {
    return SECURITYCREDENTIALS;
  }

  /**
   * This method returns the ApplicationRoot setup parameter.
   * <p>
   * @return The applicational LDAP search root.
   */
  public String getApplicationRoot()
  {
    return APPLICATIONROOT;
  }

  /**
   * This method returns the OrganizationRoot setup parameter.
   * <p>
   * @return The organizational LDAP search root.
   */
  public String getUserRoot()
  {
    return USERROOT;
  }
  
  public String getOrganicalUnitRoot()
  {
    return ORGANICALUNITROOT;
  }
}