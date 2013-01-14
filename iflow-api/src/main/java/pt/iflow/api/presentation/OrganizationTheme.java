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
