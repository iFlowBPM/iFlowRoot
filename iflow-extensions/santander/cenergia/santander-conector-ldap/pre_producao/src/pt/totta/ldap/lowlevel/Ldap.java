package pt.totta.ldap.lowlevel;

import pt.totta.ldap.setup.Setup;
import java.util.*;
import javax.naming.*;
import javax.naming.directory.*;

/**
 * This class implements the Ldap low level interface.
 * @author João Lopes (joao.lopes@santander.pt)
 * @version 3.7
 */
public class Ldap
{
    
    /**
     * This method will create an initial Directory Context.
     * @return
     */
    public DirContext setContext() {
        return setContext(new Setup());
    }
    
  /**
    * This method will create an initial Directory Context using the given
    * Setup class.
    */
  public DirContext setContext(Setup setup)
  {
    // Create an hashtable to store the Directory service variables.
    Hashtable dirContextVars = new Hashtable();
    
    // Load Directory service variables from properties.
    dirContextVars.put(Context.INITIAL_CONTEXT_FACTORY, setup.getInitialContextFactory());
    dirContextVars.put(Context.PROVIDER_URL, setup.getProviderUrl());
    dirContextVars.put(Context.SECURITY_PRINCIPAL, setup.getSecurityPrincipal());
    dirContextVars.put(Context.SECURITY_AUTHENTICATION, setup.getSecurityAuthentication());
    dirContextVars.put(Context.SECURITY_CREDENTIALS, setup.getSecurityCredentials());
    
    // Creater a null DirContext variable.
    DirContext dirContext = null;
    
    // Create an initial Directory Context.
    // This will create a connection to the Directory service using the attributes listed above.
    // If the attributes are correct and the service is responding then a new Directory Context will be created.
    try
    {
      dirContext = new InitialDirContext(dirContextVars);
    }
    catch(NamingException e)
    {
      // NamingException is the superclass of all exceptions thrown by operations in the Context and DirContext
      // interfaces.
      // We can get a lower level description of the NamingException using the getExplanation() method.
      System.out.println("");
      System.out.println("pt.totta.ldap.lowlevel.Ldap.setContext: " + e.getExplanation() + ".");
      return null;
    }
    return dirContext;
  }
  
  public DirContext setContext(Hashtable dirContextVars)
  {
    // Create an hashtable to store the Directory service variables.
    //Hashtable dirContextVars = new Hashtable();
    
    // Load Directory service variables from properties.
    Setup setup = new Setup();
    dirContextVars.put(Context.INITIAL_CONTEXT_FACTORY, setup.getInitialContextFactory());
    dirContextVars.put(Context.PROVIDER_URL, setup.getProviderUrl());
    //dirContextVars.put(Context.SECURITY_AUTHENTICATION, setup.getSecurityAuthentication());
    /*dirContextVars.put(Context.SECURITY_PRINCIPAL, setup.getSecurityPrincipal());
    dirContextVars.put(Context.SECURITY_CREDENTIALS, setup.getSecurityCredentials());*/
    
    // Creater a null DirContext variable.
    DirContext dirContext = null;
    
    // Create an initial Directory Context.
    // This will create a connection to the Directory service using the attributes listed above.
    // If the attributes are correct and the service is responding then a new Directory Context will be created.
    try
    {
      dirContext = new InitialDirContext(dirContextVars);
    }
    catch(NamingException e)
    {
      // NamingException is the superclass of all exceptions thrown by operations in the Context and DirContext
      // interfaces.
      // We can get a lower level description of the NamingException using the getExplanation() method.
      System.out.println("");
      System.out.println("pt.totta.ldap.lowlevel.Ldap.setContext: " + e.getExplanation() + ".");
      return null;
    }
    return dirContext;
  }

  /**
    * This method will search a Directory.
    * @param dirContext The initial Directory Context.
    * @param searchControls The search controls.
    * @param searchString The search string.
    * @param searchBase The search base.
    * @return All found objects.
    */
  public NamingEnumeration searchDirectory(DirContext dirContext, SearchControls searchControls, String searchString, String searchBase)
  {
    NamingEnumeration searchResults = null;
    
    try
    {
      // Search the Directory for the matching entries.
      searchResults = dirContext.search(searchBase, searchString, searchControls);
            
    }
    catch(InvalidSearchFilterException e)
    {
      System.out.println("pt.totta.ldap.lowlevel.Ldap.searchDirectory: Invalid search filter.");
      return null;
    }
    catch(InvalidSearchControlsException e)
    {
      System.out.println("pt.totta.ldap.lowlevel.Ldap.searchDirectory: Invalid search controls.");
      return null;
    }
    catch(NamingException e)
    {
      // NamingException is the superclass of all exceptions thrown by operations in the Context and DirContext
      // interfaces.
      // We can get a lower level description of the NamingException using the getExplanation() method.
      System.out.println("pt.totta.ldap.lowlevel.Ldap.searchDirectory: " + e.getExplanation() + ".");
      return null;
    }
    
    return searchResults;
  }
}