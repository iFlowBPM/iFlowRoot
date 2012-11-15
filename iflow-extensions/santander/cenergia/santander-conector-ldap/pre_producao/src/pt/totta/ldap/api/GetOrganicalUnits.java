package pt.totta.ldap.api;

import pt.totta.ldap.lowlevel.*;
import pt.totta.ldap.setup.*;
import pt.totta.ldap.objects.*;
import pt.totta.ldap.utils.*;
import java.util.*;
import javax.naming.*;
import javax.naming.directory.*;

/**
 * This class implements the Ldap API GetOrganicalUnits.
 * @author João Lopes (joao.lopes@santander.pt)
 * @version 3.7
 */
public class GetOrganicalUnits
{
  /**
   * This method returns all organical units.
   * <p>
   * This method uses the organizational branch of the LDAP tree.
   * <p>
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
  public Vector business()
  {
    // Create a vector to hold the results.
    Vector resultsVector = new Vector();
    
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
    String searchString = "(objectclass=organizationalunitbank)";
    
    // Setup the main search base.
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
          System.out.println("pt.totta.ldap.api.GetOrganicalUnits.business: result has no attributes.");
          return null;
        }
        
        // Fill OrganizationalUnit info.
        Hashtable resultsHashtable = new Hashtable();
        OrganizationalUnit organizationalUnit = new OrganizationalUnit();
        resultsHashtable = organizationalUnit.fillInfo(attributes);
        if(resultsHashtable == null)
        {
         System.out.println("pt.totta.ldap.api.GetOrganicalUnits.business: unable to fill organizationalunit info.");
          return null;
        }
        
        // Place the resultsHashtable on the resultsVector.
        resultsVector.add(resultsHashtable);
      }
      
      results.close();
      dirContext.close();
      // If no results were found.
      if(resultsFound == 0)
      {
        System.out.println("pt.totta.ldap.api.GetOrganicalUnits.business: no results match the search criteria.");
        return null;
      }
    }
    catch(NamingException e)
    {
      System.out.println("pt.totta.ldap.api.GetOrganicalUnits.business: " + e.getMessage() + ".");
      return null;
    }
    
    return resultsVector;
  }

  public static void main(String args[])
  {
    // Variables.
    Vector resultsVector =  new Vector();
    int vectorSize = 0;
    
    // Small initial message saying we are starting the test.
    System.out.println("");
    System.out.println("GetOrganicalUnits Test");
    System.out.println("----------------------");
    System.out.println("");
    
    // GetOrganicalUnits test.
    GetOrganicalUnits getOrganicalUnits = new GetOrganicalUnits();
    resultsVector = getOrganicalUnits.business();
    
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