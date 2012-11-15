package pt.totta.ldap.api;

import pt.totta.ldap.lowlevel.*;
import pt.totta.ldap.setup.*;
import pt.totta.ldap.objects.*;
import pt.totta.ldap.utils.*;
import java.util.*;
import javax.naming.*;
import javax.naming.directory.*;

/**
 * This class implements the Ldap API GetUserOrganicalUnits.
 * @author João Lopes (joao.lopes@santander.pt)
 * @version 3.7
 */
public class GetUserOrganicalUnits
{
  /**
   * This method returns all the organical units for a given user.
   * <p>
   * This method uses the organizational branch of the LDAP tree.
   * <p>
   * @param userId the user id.
   * @return A vector of hashtables. Keys for the hashtable(s) are:
   *  "codigoestrutura"
   *  "centrocusto"
   *  "nome"
   *  "marcaorgunit"
   *  "moradarua"
   *  "moradalocalidade"
   *  "moradacodigopostal"
   *  "moradacodzona"
   *  "moradadesczona"
   *  "telefoneextensao"
   *  "telefonefixo"
   *  "faxinterno"
   *  "faxexterno"
   *  "emailexterno"
   *  "loginpublicoresponsavel"
   *  "caixaeventual"
   *  "dnvalue"
   */
  public Vector business(String userId)
  {
    // Create a vector to hold the results.
    Vector resultsVector = new Vector();
    
    // Check if userId is null or if it´s an empty string.
    if(userId == null || userId.equals(""))
    {
      System.out.println("pt.totta.ldap.api.GetUserOrganicalUnits.business: userId is invalid.");
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
    String searchString = "(&(objectclass=organizationalunitbank) (loginpublicoresponsavel=*" + userId + "*))";
    
    // Setup the search base.
    Setup setup = new Setup();
    String searchBase = setup.getOrganicalUnitRoot();
    
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
          System.out.println("pt.totta.ldap.api.GetUserOrganicalUnits.business: result has no attributes.");
          return null;
        }
        
        // Fill OrganizationalUnit info.
        Hashtable resultsHashtable = new Hashtable();
        OrganizationalUnit organizationalUnit = new OrganizationalUnit();
        resultsHashtable = organizationalUnit.fillInfo(attributes);
        if(resultsHashtable == null)
        {
          System.out.println("pt.totta.ldap.api.GetUserOrganicalUnits.business: unable to fill organizationalUnit info.");
          return null;
        }
        
        // Place the resultsHashtable on the resultsVector.
        resultsVector.add(resultsHashtable);
      }
      
      results.close();
      // If no results were found.
      if(resultsFound == 0)
      {
        System.out.println("pt.totta.ldap.api.GetUserOrganicalUnits.business: no results match the search criteria.");
        return null;
      }
    }
    catch(NamingException e)
    {
      System.out.println("pt.totta.ldap.api.GetUserOrganicalUnits.business exception: " + e.getMessage() + ".");
      return null;
    }
    
    //return resultsVector;
    // Create a vector to hold the results.
    Vector resultsVector_2 = new Vector();
    
    // Setup the second search string.
    //String searchString_2 = "(&(objectclass=organizationalunit)(idpai=*";
    
    // Cicle first search results.
    int resultsFound_2 = resultsVector.size();
    for(int ciclo = 0; ciclo < resultsVector.size(); ciclo++)
    {
      // Setup the search base.
      Hashtable ldapOrganicalUnit = new Hashtable();
      ldapOrganicalUnit = (Hashtable) resultsVector.get(ciclo);
      //resultsVector_2.add(ldapOrganicalUnit);
      String searchString_2 = "(&(objectclass=organizationalunit)(idpai=*" + (String) ldapOrganicalUnit.get("centrocusto") + "*))";
      //String searchBase_2 = (String) ldapOrganicalUnit.get("dnvalue");
      
      // Search on the organizational branch of the ldap tree.
      NamingEnumeration results_2 = null;
      results_2 = ldap.searchDirectory(dirContext, searchControls, searchString_2, searchBase);
      /*if(results_2 == null)
        return null;*/
      
      // Process the result(s).
      SearchResult result_2 = null;
      Attributes attributes_2 = null;
      try
      {
        // While there are results to process.
        while(results_2.hasMore())
        {
          // Count the number of results found.
          resultsFound_2++;
          
          // Get a result.
          result_2 = (SearchResult) results_2.next();
          
           // Get attributes for the result.
          attributes_2 = result_2.getAttributes();
          
          // Check if this result has any attributes.
          if(attributes_2 == null)
          {
            System.out.println("pt.totta.ldap.api.GetUserOrganicalUnits.business: result has no attributes.");
            return null;
          }
          
          // Fill OrganizationalUnit info.
          Hashtable resultsHashtable_2 = new Hashtable();
          OrganizationalUnit organizationalUnit = new OrganizationalUnit();
          resultsHashtable_2 = organizationalUnit.fillInfo(attributes_2);
          if(resultsHashtable_2 == null)
          {
            System.out.println("pt.totta.ldap.api.GetUserOrganicalUnits.business: unable to fill organizationalUnit info.");
            return null;
          }
          
          // Place the resultsHashtable on the resultsVector.
          //resultsVector_2.add(resultsHashtable_2);
          resultsVector.add(resultsHashtable_2);
        }
        
        results_2.close();
        // If no results were found.
        if(resultsFound_2 == 0)
        {
          System.out.println("pt.totta.ldap.api.GetUserOrganicalUnits.business: no results match the search criteria.");
          return null;
        }
      }
      catch(NamingException e)
      {
        System.out.println("pt.totta.ldap.api.GetUserOrganicalUnits.business: " + e.getMessage() + ".");
        return null;
      }
    }
    
    try {
    	dirContext.close();
    } catch (NamingException ne) {
    	 String namingExceptionExplanation = ne.getExplanation();
      	// Fatal.
      	System.out.println(namingExceptionExplanation + " closing context at pt.totta.ldap.api.GetUserOrganicalUnits");
      	return null;
    }
    
    //return resultsVector_2;
    return resultsVector;
  }
    
  public static void main(String args[])
  {
    // Variables.
    String userId = null;
    Vector resultsVector =  new Vector();
    int vectorSize = 0;
    
    // Small initial message saying we are starting the test.
    System.out.println("");
    System.out.println("GetUserOrganicalUnits Test");
    System.out.println("--------------------------");
    System.out.println("");
    
    // Get userId.
    Utils utils = new Utils();
    userId = utils.userInput("userId: ");
    
    // GetUserOrganicalUnits test.
    GetUserOrganicalUnits getUserOrganicalUnits = new GetUserOrganicalUnits();
    resultsVector = getUserOrganicalUnits.business(userId);
    
    // Show the test results.
    System.out.println("");
    System.out.println("Test returns:");
    System.out.println("");
    if(resultsVector == null)
      System.out.println("Invalid search.");
    else
    {
      vectorSize = resultsVector.size();
      if(vectorSize == 0)
        System.out.println("Empty vector.");
      else
        System.out.println(resultsVector);
    }
  }
}