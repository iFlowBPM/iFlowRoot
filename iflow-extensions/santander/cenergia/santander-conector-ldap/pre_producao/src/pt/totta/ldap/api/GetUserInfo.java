package pt.totta.ldap.api;

import pt.totta.ldap.lowlevel.*;
import pt.totta.ldap.setup.*;
import pt.totta.ldap.objects.*;
import pt.totta.ldap.utils.*;
import java.util.*;
import javax.naming.*;
import javax.naming.directory.*;

/**
 * This class implements the Ldap API GetUserInfo.
 * @author João Lopes (joao.lopes@santander.pt)
 * @version 3.7
 */
public class GetUserInfo
{
  /**
   * This method returns information about a given user.
   * <p>
   * This method uses the organizational branch of the LDAP tree.
   * <p>
   * @param userId the user id.
   * @return An hashtable with the user information.<br>
   * Keys for the hashtable are: 
   *  "loginpublico"
   *  "loginprivado"
   *  "numeroempregado"
   *  "nomeutilizador"
   *  "sexoutilizador"
   *  "marcautilizador"
   *  "marcautilizadorprestacaoservico"
   *  "codigoestrutura"
   *  "centrocusto"
   *  "centrocustoprestacaoservico"
   *  "unidadeorganizativa"
   *  "codigoposto"
   *  "posto"
   *  "tipoestrutura"
   *  "emailexterno"
   *  "emailinterno"
   *  "telefoneextensao"
   *  "telefonefixo"
   *  "telefonemovel"
   *  "faxexterno"
   *  "faxinterno"
   *  "sessionidutilizador"
   *  "propriedades"
   *  "dnvalue"
   *  "perfilexecucao"
   *  "indacessostaff"
   *  "indgestorcliente"
   *  "indgestorconta"
   *  "codigogestor"
   */
  public Hashtable business(String userId)
  {
    // Create the results hashtable.
    Hashtable resultsHashtable = new Hashtable();
    
    // Check if userId is null or if it´s an empty string.
    if(userId == null || userId.equals(""))
    {
      System.out.println("pt.totta.ldap.api.ldap.CheckUserPassword.business: userId is invalid.");
      return null;
    }
    
    // Set a dirContext.
    Ldap ldap = new Ldap();
    DirContext dirContext = ldap.setContext();
    if(dirContext == null)
      return null;
    
    // Setup the searchControls.
    SearchControls searchControls = new SearchControls();
    searchControls.setReturningObjFlag(true);
    searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
    
    // Setup the search string.
    String searchString = "";
    if(Character.isDigit(userId.charAt(0)))
      searchString = "numeroempregado=" + userId;
    else
      searchString = "loginpublico=" + userId;
      
    searchString = "(&(objectclass=personbank)(estadoutilizador=1)(" + searchString + "))";
     
    //System.out.println("Search String --> " + searchString + " <--"); 
    
    // Setup the search base.
    Setup setup = new Setup();
    String searchBase = setup.getUserRoot();
    
    // Search on the organizational branch of the ldap tree.
    NamingEnumeration results = null;
    results = ldap.searchDirectory(dirContext, searchControls, searchString, searchBase);
    if(results == null)
      return null;
    
    // Process the result(s).
    int resultsFound = 0;
    SearchResult result = null;
    Attributes attributes = null;
    try
    {
      // While there are results to process.
      while(results.hasMore())
      {
        // Count the number of results found.
        resultsFound++;
        
        // Get a result.
        result = (SearchResult) results.next();
        
        // Get attributes for the result.
        attributes = result.getAttributes();
        attributes.put(new BasicAttribute("dn", result.getName() + "," + searchBase));
        
        // Check if this result has any attributes.
        if(attributes == null)
        {
          System.out.println("pt.totta.ldap.api.GetOrganicalUnitInfo.business: result has no attributes.");
          return null;
        }
        
        // Fill Person info.
        Person person = new Person();
        resultsHashtable = person.fillInfo(attributes);
        if(resultsHashtable == null)
        {
          System.out.println("pt.totta.ldap.api.GetOrganicalUnitInfo.business: unable to fill person unit info.");
          return null;
        }
      }
      
      results.close();
      dirContext.close();
      // If no results were found.
      if(resultsFound == 0)
      {
        System.out.println("pt.totta.ldap.api.GetOrganicalUnitInfo.business: no results match the search criteria.");
        return null;
      }
    }
    catch(NamingException e)
    {
      System.out.println("pt.totta.ldap.api.GetOrganicalUnitInfo.business: " + e.getMessage() + ".");
      return null;
    }
    
    return resultsHashtable;
  }

  public static void main(String args[])
  {
    // Variables.
    String userId = null;
    Hashtable resultsHashtable =  new Hashtable();
    int hashtableSize = 0;
    Enumeration elements = null;
    Enumeration keys = null;
    
    // Small initial message saying we are starting the test.
    System.out.println("");
    System.out.println("GetUserInfo Test");
    System.out.println("----------------");
    System.out.println("");
    
    // Get userId.
    Utils utils = new Utils();
    userId = utils.userInput("userId: ");
    
    // GetUserInfo test.
    GetUserInfo getUserInfo = new GetUserInfo();
    resultsHashtable = getUserInfo.business(userId);
    
    // Show the test results.
    System.out.println("");
    System.out.println("Test returns:");
    System.out.println("");
    if(resultsHashtable == null)
      System.out.println("Invalid search.");
    else
    {
      hashtableSize = resultsHashtable.size();
      if(hashtableSize== 0)
        System.out.println("Empty hashtable.");
      else
        System.out.println(resultsHashtable);
    }
  }
}