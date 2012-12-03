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
package pt.iflow.api.presentation;

import pt.iflow.api.utils.UserInfoInterface;

/**
 * Gestão de temas para uma organização. A maior parte deste código foi refactoziado para este método.
 * Originalmente estava em UserManager.
 * @author vários
 *
 */
public interface OrganizationTheme {
   /**
    * Obtains the theme information for an organization
    */
   public OrganizationThemeData getOrganizationTheme( UserInfoInterface userInfo );
   /**
    * Remove theme information for an organization.
    * @param userInfo
    * @return
    */
   public boolean deleteOrganizationData(UserInfoInterface userInfo);
   /**
    * Insert or updates theme information for organization
    * @param userInfo
    * @param theme
    * @param sStyleUrl
    * @param sLogoUrl
    * @return
    * @see updateOrganizationData
    */
   public boolean insertOrganizationData(UserInfoInterface userInfo, String theme, String sStyleUrl, String sLogoUrl, String menuLocation, String menuStyle, boolean procMenuVisible);
   /**
    * Insert or updates theme information for organization
    * @param userInfo
    * @param theme
    * @param sStyleUrl
    * @param sLogoUrl
    * @return
    * @see insertOrganizationData
    */
   public boolean updateOrganizationData(UserInfoInterface userInfo, String theme, String sStyleUrl, String sLogoUrl, String menuLocation, String menuStyle, boolean procMenuVisible);
   
   /**
    * System adm call to remove organization theme
    * @param userInfo system administrator
    * @param organizationId organization id
    * @return
    */
   public boolean removeOrganizationTheme(UserInfoInterface userInfo, String organizationId);
}
