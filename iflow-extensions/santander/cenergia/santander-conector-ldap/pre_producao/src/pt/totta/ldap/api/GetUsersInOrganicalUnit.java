package pt.totta.ldap.api;

import pt.totta.ldap.lowlevel.*;
import pt.totta.ldap.setup.*;
import pt.totta.ldap.objects.*;
import pt.totta.ldap.utils.*;
import java.util.*;
import javax.naming.*;
import javax.naming.directory.*;

/**
 * This class implements the Ldap API GetUsersInOrganicalUnit.
 * @author João Lopes (joao.lopes@santander.pt)
 * @version 3.7
 */
public class GetUsersInOrganicalUnit
{
  /**
   * This method returns all the users for a given organical unit.
   * <p>
   * This method uses the organizational branch of the LDAP tree.
   * <p>
   * @param organicalUnit the organicalUnit id.
   * @param subTreeScope "yes" means searching searching all subtrees. "no" means searching one level.
   * @return A vector of hashtables. Keys for the hashtable(s) are:
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
   */
  public Vector business(String organicalUnit, String subTreeScope)
  {
    // Create a vector to hold the results.
    Vector resultsVector = new Vector();
    
    // Check if organicalUnit is null or if it´s an empty string.
    if(organicalUnit == null || organicalUnit.equals(""))
    {
      System.out.println("pt.totta.ldap.api.GetUsersInOrganicalUnit.business: organicalUnit is invalid.");
      return null;
    }
    
    // Check if subTreeScope is null or if it´s an empty string.
    if(subTreeScope == null || subTreeScope.equals(""))
    {
      System.out.println("pt.totta.ldap.api.GetUsersInOrganicalUnit.business: subTreeScope is invalid.");
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
    if(organicalUnit.length() == 4)
    	//if(subTreeScope.equals("no"))
      		searchString = "(&(objectclass=organizationalunitbank)(centrocusto=*" + organicalUnit + "*))";
      	/*else if(subTreeScope.equals("yes"))
      		searchString = "(&(objectclass=organizationalunitbank)(|(centrocusto=*" + organicalUnit + "*)(idpai=*" + organicalUnit + ")))";
      	else {
      		System.out.println("pt.totta.ldap.api.GetUsersInOrganicalUnit.business: invalid subtree scope.");
      		return null;
      	}*/
    /*else
      searchString = "(&(objectclass=organizationalunitbank) (codigoestrutura=" + organicalUnit + "))";*/
      
    System.out.println("OU Search String --> " + searchString + " <--");
    
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
    boolean continueSearch = true;
    Vector scopeSearchVector = new Vector();
    try
    {
      while (continueSearch) {
      	  
      // While there are results to process.
	      while(results.hasMore()) {
	      	//System.out.println("HAS RESULTS");
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
	          System.out.println("pt.totta.ldap.api.GetUsersInOrganicalUnit.business: result has no attributes.");
	          return null;
	        }
	        
	        // Fill OrganizationalUnit info.
	        Hashtable resultsHashtable = new Hashtable();
	        OrganizationalUnit organizationalUnit = new OrganizationalUnit();
	        resultsHashtable = organizationalUnit.fillInfo(attributes);
	        if(resultsHashtable == null)
	        {
	          System.out.println("pt.totta.ldap.api.GetUsersInOrganicalUnit.business: unable to fill organizationalUnit info.");
	          return null;
	        }
	        
	        // Place the resultsHashtable on the resultsVector.
	        //System.out.println("Organical Unit --> " + resultsHashtable + " <--");
	        resultsVector.add(resultsHashtable);
	        scopeSearchVector.add((String)resultsHashtable.get("centrocusto"));
	      }
	      
	      try {
	      
		      if(subTreeScope.equals("yes")) {
	      		searchString = "(&(objectclass=organizationalunitbank)(idpai=*" + (String)scopeSearchVector.elementAt(0) + "))";
	      		System.out.println("OU Parent Search String --> " + searchString + " <--");
		  	    results = ldap.searchDirectory(dirContext, searchControls, searchString, searchBase);
			  } else {
			  	continueSearch = false;
			  }
		  } catch (Exception sse) {
			  System.out.println("pt.totta.ldap.api.GetUsersInOrganicalUnit.business: no results for IDPAI.");
		  } finally {
			  if (scopeSearchVector.size() == 0)
			    continueSearch = false;
			  if (scopeSearchVector.size() > 0)
			  	scopeSearchVector.removeElementAt(0);
		  }
			  
	  }
      
      results.close();
      // If no results were found.
      if(resultsFound == 0)
      {
        System.out.println("pt.totta.ldap.api.GetUsersInOrganicalUnit.business: no results match the search criteria.");
        return null;
      }
    }
    catch(NamingException e)
    {
      System.out.println("pt.totta.ldap.api.GetUsersInOrganicalUnit.business: " + e.getMessage() + ".");
      return null;
    }
    
    // Create a vector to hold the results.
    Vector resultsVector_2 = new Vector();
    
    // Setup the searchControls.
    /*if(subTreeScope.equals("no"))
      searchControls.setSearchScope(SearchControls.ONELEVEL_SCOPE);
    else if(!subTreeScope.equals("yes"))
      System.out.println("pt.totta.ldap.api.GetUsersInOrganicalUnit.business: invalid subtree scope.");*/
    
    // Setup the second search string.
    //String searchString_2 = "(&(objectclass=personbank)(centrocusto=*" + organicalUnit + "*))";
    
    // Cicle first search results.
    int resultsFound_2 = 0;
    for(int ciclo = 0; ciclo < resultsVector.size(); ciclo++)
    {
      // Setup the search base.
      Hashtable ldapOrganicalUnit = new Hashtable();
      ldapOrganicalUnit = (Hashtable) resultsVector.get(ciclo);
      //System.out.println("Organical Unit --> " + ldapOrganicalUnit + " <--");
      String centrocusto = (String)ldapOrganicalUnit.get("centrocusto");
      String searchString_2 = "(&(objectclass=personbank)(estadoutilizador=1)(centrocusto=*" + centrocusto + "*))";
      //System.out.println("User Search String --> " + searchString_2 + " <--");
      //String searchBase_2 = (String) ldapOrganicalUnit.get("dnvalue");
      String searchBase_2 = setup.getUserRoot();
      
      // Search on the organizational branch of the ldap tree.
      NamingEnumeration results_2 = null;
      results_2 = ldap.searchDirectory(dirContext, searchControls, searchString_2, searchBase_2);
      if(results_2 == null)
        return null;
      
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
          attributes_2.put(new BasicAttribute("dn", result_2.getName() + "," + searchBase_2));
          
          // Check if this result has any attributes.
          if(attributes_2 == null)
          {
            System.out.println("pt.totta.ldap.api.GetUsersInOrganicalUnit.business: result has no attributes.2");
            return null;
          }
          
          // Fill Person info.
          Hashtable resultsHashtable_2 = new Hashtable();
          Person person = new Person();
          resultsHashtable_2 = person.fillInfo(attributes_2);
          if(resultsHashtable_2 == null)
          {
            System.out.println("pt.totta.ldap.api.GetUsersInOrganicalUnit.business: unable to fill person info.2");
            return null;
          }
          
          // Place the resultsHashtable on the resultsVector.
          //System.out.println("User --> " + resultsHashtable_2 + " <--");
          resultsVector_2.add(resultsHashtable_2);
        }
        
        results_2.close();
        
        
      }
      catch(NamingException e)
      {
        System.out.println("pt.totta.ldap.api.GetUsersInOrganicalUnit.business: " + e.getMessage() + ".2");
        return null;
      }
    }
    
    // If no results were found.
    if(resultsFound_2 == 0) {
		System.out.println("pt.totta.ldap.api.GetUsersInOrganicalUnit.business: no results match the search criteria.2");
		return null;
	}
    
    try {
    	dirContext.close();
    } catch (NamingException ne) {
    	 String namingExceptionExplanation = ne.getExplanation();
      	// Fatal.
      	System.out.println(namingExceptionExplanation + " closing context at pt.totta.ldap.api.GetUsersInOrganicalUnit");
      	return null;
    }
    
    return resultsVector_2;
  }

  public static void main(String args[])
  {
    // Variables.
    String organicalUnit = null;
    String subTreeScope = null;
    Vector resultsVector =  new Vector();
    int vectorSize = 0;
    
    // Small initial message saying we are starting the test.
    System.out.println("");
    System.out.println("GetUsersInOrganicalUnit Test");
    System.out.println("----------------------------");
    System.out.println("");
    
    // Get organicalUnit and subTreeScope.
    Utils utils = new Utils();
    organicalUnit = utils.userInput("organicalUnit: ");
    subTreeScope = utils.userInput("subTreeScope (yes/no): ");
    
    // GetUsersInOrganicalUnit test.
    GetUsersInOrganicalUnit getUsersInOrganicalUnit = new GetUsersInOrganicalUnit();
    resultsVector = getUsersInOrganicalUnit.business(organicalUnit, subTreeScope);
    
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