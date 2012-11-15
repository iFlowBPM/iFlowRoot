package pt.totta.ldap.api;

import java.util.Hashtable;
import java.util.Vector;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import pt.totta.ldap.lowlevel.Ldap;
import pt.totta.ldap.objects.ProfilePersonObj;
import pt.totta.ldap.setup.Setup;
import pt.totta.ldap.utils.Utils;
/**
 * Esta classe retorna
 * 
 * @author i051296
 *
 */
public class GetUsersNameInProfile {

	/**
	 * This method returns all the users for a given applicational profile.
	 * <p>
	 * This method uses the applicational branch of the LDAP tree.
	 * <p>
	 * 
	 * @param applicationName
	 *            the application name.
	 * @param profile
	 *            the application profile.
	 * @return A vector of hashtables. Keys for the hashtable(s) are: "pp"
	 *         "profileuserid" "profileuntildate" "nivel" "dnvalue"
	 *         "creationdate" "creationtype"
	 */
	public Vector business(String applicationName, String profile) {
		// Create a vector to hold the results.
		Vector resultsVector = new Vector();

		// Check if applicationName is null or if it´s an empty string.
		if (applicationName == null || applicationName.equals("")) {
			System.out.println("pt.totta.ldap.api.GetUsersInProfile.business: applicationName is invalid.");
			return null;
		}

		// Check if profile is null or if it´s an empty string.
		if (profile == null || profile.equals("")) {
			System.out.println("pt.totta.ldap.api.GetUsersInProfile.business: profile is invalid.");
			return null;
		}

		// Set a dirContext.
		Ldap ldap = new Ldap();
		DirContext dirContext = ldap.setContext();
		if (dirContext == null)
			return null;

		// Setup the searchControls.
		SearchControls searchControls = new SearchControls();
		searchControls.setReturningObjFlag(true);
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

		// Setup the search string.
		String searchString = "profileuserid=*";

		// Setup the search base.
		Setup setup = new Setup();
		String searchBase = "pf=" + profile + ",a=" + applicationName + "," + setup.getApplicationRoot();

		// Search on the applicational branch of the ldap tree.
		NamingEnumeration results = null;
		results = ldap.searchDirectory(dirContext, searchControls, searchString, searchBase);
		if (results == null)
			return null;

		// Process the result(s).
		int resultsFound = 0;
		SearchResult result = null;
		Attributes attributes = null;
		try {
			// While there are results to process.
			while (results.hasMore()) {
				// Count the number of results found.
				resultsFound++;

				// Get a result.
				result = (SearchResult) results.next();

				// Get attributes for the result.
				attributes = result.getAttributes();
				attributes.put(new BasicAttribute("dn", result.getName() + "," + searchBase));
				
				// Check if this result has any attributes.
				if (attributes == null) {
					System.out.println("pt.totta.ldap.api.GetUsersInProfile.business: result has no attributes.");
					return null;
				}
				
				/*ADICIONAR A INFORMACAO DO NOME DO UTILIZADOR*/
				
				String userId = attributes.get("pp") != null ? (String) attributes.get("pp").get() : null;
				Hashtable resultsuserInfo = getUserInfo(userId);
				if(resultsuserInfo != null) {
					String profileusername = resultsuserInfo.containsKey("cn") ? (String) resultsuserInfo.get("cn") : "";
					String profileuserfullname = resultsuserInfo.containsKey("nomeutilizador") ? (String) resultsuserInfo.get("nomeutilizador") : "";
					String profileuseridandname = userId;
					if(!profileusername.equals("")) {
						profileuseridandname = profileuseridandname + " (" + profileusername + ")";
					}
					
					attributes.put("profileusername", profileusername);
					attributes.put("profileuserfullname", profileuserfullname);
					attributes.put("profileuseridandname", profileuseridandname);
				}

				// Fill ProfilePersonObj info.
				Hashtable resultsHashtable = new Hashtable();
				ProfilePersonObj profilePersonObj = new ProfilePersonObj();
				resultsHashtable = profilePersonObj.fillInfo(attributes);
				if (resultsHashtable == null) {
					System.out.println("pt.totta.ldap.api.GetUsersInProfile.business: unable to fill profilePersonObj info.");
					return null;
				}

				// Place the resultsHashtable on the resultsVector.
				resultsVector.add(resultsHashtable);
			}

			results.close();
			dirContext.close();
			// If no results were found.
			if (resultsFound == 0) {
				System.out.println("pt.totta.ldap.api.GetUsersInProfile.business: no results match the search criteria.");
				return null;
			}
		} catch (NamingException e) {
			System.out.println("pt.totta.ldap.api.GetUsersInProfile.business: " + e.getMessage() + ".");
			return null;
		}

		return resultsVector;
	}
	
	private Hashtable getUserInfo(String userId) {
		if(userId == null) {
			return null;
		}
		
		GetUserInfo getUserInfo = new GetUserInfo();
		Hashtable resultsHashtable = getUserInfo.business(userId);
	    
		return resultsHashtable;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Variables.
		String applicationName = null;
		String profile = null;
		Vector resultsVector = new Vector();
		int vectorSize = 0;

		// Small initial message saying we are starting the test.
		System.out.println("");
		System.out.println("GetUsersNameInProfile Test");
		System.out.println("----------------------");
		System.out.println("");

		// Get applicationName and profile.
		Utils utils = new Utils();
		applicationName = utils.userInput("applicationName: ");
		profile = utils.userInput("profile: ");

		// GetUsersInProfile test.
		GetUsersNameInProfile getUsersNameInProfile = new GetUsersNameInProfile();
		resultsVector = getUsersNameInProfile.business(applicationName, profile);

		// Show the test results.
		System.out.println("");
		System.out.println("Test returns:");
		System.out.println("");
		if (resultsVector == null) {
			System.out.println("Invalid search.");
		} else {
			vectorSize = resultsVector.size();
			if (vectorSize == 0) {
				System.out.println("Empty vector.");
			} else {
				System.out.println(resultsVector);
			}
		}
	}
}
