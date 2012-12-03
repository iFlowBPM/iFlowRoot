/*****************************************************************************************
    Infosistema iFlow - workflow and BPM platform
    Copyright(C) 2002-2012 Infosistema, S.A.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

    www.infosistema.com
    iflow@infosistema.com
    Av. Jose Gomes Ferreira, 11 3rd floor, s.34
    Miraflores
    1495-139 Alges Portugal
****************************************************************************************/
package pt.iflow.api.core;

import java.sql.SQLException;
import java.util.Collection;

import javax.naming.NamingException;

import pt.iflow.api.userdata.UserData;
import pt.iflow.api.utils.UserInfoInterface;

/**
 * Remote interface for AuthProfile.
 * @wtp generated
 */
public interface AuthProfile {
  
   /**
    * verifica se um utilizador e valido, dado o par username-password
    * @param username - String com o username do utilizador
    * @param password - String com a password em plain-text
    * @return - valor logico com o sucesso da autenticacao
    */
   public boolean checkUser( java.lang.String username,java.lang.String password );
   /**
    * verifica se um utilizador de sistema e valido, dado o par username-password
    * @param username - String com o username do utilizador
    * @param password - String com a password em plain-text
    * @return - valor logico com o sucesso da autenticacao
    */
   public boolean checkSystemUser( java.lang.String username,java.lang.String password );
   /**
    * Authenticates an user using his login information and the sessionId generated when he logged on in the intranet using the LDAP framework
    * @param userId user login identification
    * @param sessionId a number that uniqlly identifies that validates the user
    * @return a boolean value indicating if user is autheticated sucessufull
    */
   public boolean authenticateIntranetUser( java.lang.String username,java.lang.String sessionId );

   /**
    * Gets user info from ldap.
    * @param asUser user to get information
    * @param searchField field to search
    * @return hashtable with user's ldap information
    */
   public pt.iflow.api.userdata.UserData getUserInfo( java.lang.String asUser, String searchField );
   
   /**
    * Gets user info from ldap.
    * @param asUser user to get information
    * @return hashtable with user's ldap information
    */
   public pt.iflow.api.userdata.UserData getUserInfo( java.lang.String asUser );
   
   /**
    * Gets system user info from BD.
    * @param asUser user to get information
    * @return hashtable with user's BD information
    */
   public pt.iflow.api.userdata.UserData getSystemUserInfo( java.lang.String asUser );

   /**
    * Get a list of users that belong to a given profile
    * @param profile the name of the profile
    * @return a value of type 'ListIterator' with the users ids belonging to the given profile
    * @exception NamingException if an error occurs
    * @exception SQLException if an error occurs
    */
   public java.util.Collection<String> getUsersInProfile( UserInfoInterface userInfo,java.lang.String profile );

   /**
    * Get the logged user profile list
    * @param 
    * @return a value of type 'Iterator' profiles for the logged user
    * @exception NamingException if an error occurs
    * @exception SQLException if an error occurs
    */
   public java.util.Collection<String> getUserProfiles( UserInfoInterface userInfo );

   /**
    * Get a user profile list
    * @param userinfo
    * @param userName
    * @return a value of type 'Iterator' profiles for the logged user
    * @exception NamingException if an error occurs
    * @exception SQLException if an error occurs
    */
   public java.util.Collection<String> getUserProfilesForUser( UserInfoInterface userInfo, java.lang.String userName );

   /**
    * Get the logged user profile list
    * @param 
    * @return a value of type 'Iterator' profiles for the logged user
    * @exception NamingException if an error occurs
    * @exception SQLException if an error occurs
    */
   public java.util.Collection<String> getUserProfiles( java.lang.String userName,java.lang.String organization );

   /**
    * Get the logged user profile list
    * @param 
    * @return a value of type 'ListIterator' profiles for the logged user
    * @exception NamingException if an error occurs
    * @exception SQLException if an error occurs
    */
   public java.util.Collection<String> getUserProfiles( UserInfoInterface userInfo,java.lang.String application );

   /**
    * List all profiles available
    * @return 
    */
   public java.util.Collection<String> getAllProfiles( java.lang.String organization );

   /**
    * Get a user profile list
    * @param 
    * @return a value of type 'ListIterator' profiles for the given user
    * @exception NamingException if an error occurs
    * @exception SQLException if an error occurs
    */
   public java.util.Collection<String> getUserProfiles( java.lang.String asUser,java.lang.String organization,java.lang.String application );

   /**
    * Get's the manager for the given user - unit manager if user is not the given user - parent's unit manager, if unit manager not found for user's unit or unit manager is user itself (recursively until found or reached top)
    * @param asUser
    * @return the unit manager or null if no unit manager found
    */
   public java.lang.String getUpperNode( java.lang.String asUser );

   /**
    * TODO Add comment for method getOrganicalUnitInfo on AuthProfileBean
    * @param asKey
    * @return 
    */
   public pt.iflow.api.userdata.OrganizationData getOrganizationInfo( java.lang.String asKey );

   /**
    * Gets organical unit info from ldap.
    * @param asKey key to get information
    * @return hashtable with oganical unit's ldap information
    */
   public pt.iflow.api.userdata.OrganizationalUnitData getOrganicalUnitInfo( java.lang.String asKey );

   /**
    * Gets organical unit parent info from ldap.
    * @param asKey key to get information
    * @return hashtable with oganical unit parent's ldap information
    */
   public pt.iflow.api.userdata.OrganizationalUnitData getOrganicalUnitParent( java.lang.String asKey );

   /**
    * Checks if a user is subordinate of another user.
    * @param userInfo the chiefe user
    * @param user the user to check
    * @return true if userInfo is the manager of user.
    */
   public boolean isSubordinate(UserInfoInterface userInfo, String user);
   
   /**
    * Pre process username before login.
    * 
    * @param username
    * @return processed username
    */
   public abstract String fixUsername(String username);

   
   /**
    * List all users in a organization. Use with caution.
    * 
    * @param organization
    * @return
    */
   public abstract Collection<UserData> getAllUsers(String orgId);
}
