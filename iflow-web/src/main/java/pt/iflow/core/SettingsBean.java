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
package pt.iflow.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Locale;
import java.util.TimeZone;

import javax.sql.DataSource;

import pt.iflow.api.core.Settings;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.userdata.Tutorial;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.UserSettings;
import pt.iflow.api.utils.Utils;
import pt.iflow.utils.UserSettingsImpl;

/**
 * SettingsBean: Setting management
 */

public class SettingsBean implements Settings {

  // CACHE STUFF

  private static SettingsBean instance = null;

  protected SettingsBean() {
  }

  /**
   * 
   * Get a Settings instance
   * 
   * @return
   */
  public static Settings getInstance() {
    if (null == instance)
      instance = new SettingsBean();
    return instance;
  }

  private UserSettings doGetUserSettings(String user, String orgId) {
    if (null == user)
      return null;
    UserSettings settings = getOrganizationSettings(orgId);
    // get username
    DataSource ds = null;
    Connection db = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();

      // get user settings
      final String query = "select lang,region,timezone, tutorial,help_mode, tutorial_mode from user_settings where userid=?";
      
      pst = db.prepareStatement(query);
      pst.setString(1, user);
      rs = pst.executeQuery();
      if (rs.next()) {
        String lang = rs.getString("lang");
        String region = rs.getString("region");
        String sTimezone = rs.getString("timezone");
        String sTutorial = rs.getString("tutorial");
        int nHelpMode = rs.getInt("help_mode");
        int nTutorialMode = rs.getInt("tutorial_mode");
        settings = new UserSettingsImpl(new Locale(lang, region), TimeZone.getTimeZone(sTimezone), sTutorial, nHelpMode==1?true:false, nTutorialMode==1?true:false);
      }
      rs.close();
      pst.close();
    } catch (SQLException e) {
      Logger.warning("ADMIN", this, "getUserSettings", "Error getting user settings!", e);
    } finally {
      DatabaseInterface.closeResources(db, pst, rs);
    }

    return settings;
  }

  public UserSettings getUserSettings(UserInfoInterface userInfo) {
    if (null == userInfo)
      return null;
    return doGetUserSettings(userInfo.getUtilizador(), userInfo.getOrganization());
  }

  public UserSettings getUserSettings(pt.iflow.api.utils.UserInfoInterface userInfo, String userId) {

    if (null == userInfo)
      return null;
    if (!userInfo.isOrgAdmin() && !userInfo.isSysAdmin()) {
      Logger.warning(userInfo.getUtilizador(), this, "getUserSettings", "User not allowed to get " + userId + " settings.");
      return null;
    }
    return doGetUserSettings(userInfo.getUtilizador(), userInfo.getOrganization());
  }

  public void updateUserSettings(UserInfoInterface userInfo, String lang, String region, String timezone) {
    if (null == userInfo)
      return;
    doUpdateSettings(userInfo.getUtilizador(), lang, region, timezone, userInfo.getUserSettings().getTutorial(), userInfo.getUserSettings().isHelpMode(), userInfo.getUserSettings().isTutorialMode());
  }

  public void updateUserSettings(UserInfoInterface userInfo, String lang, String region, String timezone, String tutorial, boolean helpMode, boolean tutorialMode) {
    if (null == userInfo)
      return;
    doUpdateSettings(userInfo.getUtilizador(), lang, region, timezone, tutorial, helpMode, tutorialMode);
  }

  public void updateUserSettings(UserInfoInterface userInfo, UserSettings settings) {
    if (null == userInfo)
      return;
    doUpdateSettings(userInfo.getUtilizador(), settings.getLocale().getLanguage(), settings.getLocale().getCountry(), settings.getTimeZoneID(), settings.getTutorial(), settings.isHelpMode(), settings.isTutorialMode());
  }

  public void updateUserSettings(UserInfoInterface userInfo, String userId, String lang, String region, String timezone, String tutorial, boolean helpMode, boolean tutorialMode) {
    if (null == userInfo)
      return;
    if (!userInfo.isOrgAdmin() && !userInfo.isSysAdmin()) {
      Logger.warning(userInfo.getUtilizador(), this, "updateUserSettings", "User not allowed to update " + userId + " settings.");
      return;
    }
    doUpdateSettings(userId, lang, region, timezone, tutorial, helpMode, tutorialMode);
  }

  private void doUpdateSettings(String userId, String lang, String region, String timezone, String tutorial, boolean helpMode, boolean tutorialMode) {
    // get username
    DataSource ds = null;
    Connection db = null;
    PreparedStatement pst = null;

    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      db.setAutoCommit(false);

      // get user settings
      final String query = "update user_settings set lang=?, region=?, timezone=?, tutorial=?, help_mode=?, tutorial_mode=? where userid=?";
      pst = db.prepareStatement(query);
      pst.setString(1, lang);
      pst.setString(2, region);
      pst.setString(3, timezone);
      pst.setString(4, tutorial);
      pst.setInt(5, helpMode?1:0);
      pst.setInt(6, tutorialMode?1:0);
      pst.setString(7, userId);
      int upd = pst.executeUpdate();
      pst.close();
      
      if(upd == 0) { // do not exists, so lets insert it
        pst = db.prepareStatement("insert into user_settings (lang,region,timezone,userid, tutorial, help_mode, tutorial_mode) values (?,?,?,?,?,?,?)");
        pst.setString(1, lang);
        pst.setString(2, region);
        pst.setString(3, timezone);
        pst.setString(4, userId);
        pst.setString(5, tutorial);
        pst.setInt(6, helpMode?1:0);
        pst.setInt(7, tutorialMode?1:0);
        pst.executeUpdate();
        pst.close();
      }
      
      db.commit();
    } catch (SQLException e) {
      Logger.warning("ADMIN", this, "getUserSettings", "Error updating user settings!", e);
    } finally {
      DatabaseInterface.closeResources(db, pst);
    }
  }

  
  private final Hashtable<String,Locale> orgLocaleCache = new Hashtable<String, Locale>();
  private final Hashtable<String,TimeZone> orgTimeZoneCache = new Hashtable<String, TimeZone>();

  public UserSettings getOrganizationSettings(UserInfoInterface userInfo) {
    if(null == userInfo) return new OrganizationSettingsImpl();
    return getOrganizationSettings(userInfo.getOrganization());
  }
  
  public UserSettings getOrganizationSettings(String orgId) {
    if(null == orgId) return new OrganizationSettingsImpl();
    Locale loc = getOrganizationLocale(orgId);
    TimeZone tz = getOrganizationTimeZone(orgId);
    return new OrganizationSettingsImpl(loc, tz);
  }
  
  
  public Locale getOrganizationLocale(UserInfoInterface userInfo) {
    if(null == userInfo) return Locale.getDefault();
    return getOrganizationLocale(userInfo.getOrganization());
  }
  
  public Locale getOrganizationLocale(String orgId) {
    Locale locale = Locale.getDefault();
    if(null == orgId) return locale;
    
    Locale tmpLocale = orgLocaleCache.get(orgId);
    if(null != tmpLocale) return tmpLocale;
    
    // get username
    DataSource ds = null;
    Connection db = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();

      // get organization settings
      pst = db.prepareStatement("select lang,region from organization_settings where organizationid=?");
      pst.setString(1, orgId);
      rs = pst.executeQuery();
      if (rs.next()) {
        String lang = rs.getString("lang");
        String region = rs.getString("region");
        locale = new Locale(lang, region);
      }
      rs.close();
      pst.close();
    } catch (SQLException e) {
      Logger.warning("ADMIN", this, "getOrganizationLocale", "Error getting organization Locale!", e);
    } finally {
      DatabaseInterface.closeResources(db, pst, rs);
    }

    orgLocaleCache.put(orgId, locale);
    return locale;
  }
  
  public TimeZone getOrganizationTimeZone(UserInfoInterface userInfo) {
    if(null == userInfo) return TimeZone.getDefault();
    return getOrganizationTimeZone(userInfo.getOrganization());
  }
  
  public TimeZone getOrganizationTimeZone(String orgId) {
    TimeZone timeZone = TimeZone.getDefault();
    if(null == orgId) return timeZone;
    
    TimeZone tmpTimeZone = orgTimeZoneCache.get(orgId);
    if(null != tmpTimeZone) return tmpTimeZone;
    
    // get username
    DataSource ds = null;
    Connection db = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();

      // get organization settings
      pst = db.prepareStatement("select timezone from organization_settings where organizationid=?");
      pst.setString(1, orgId);
      rs = pst.executeQuery();
      if (rs.next()) {
        String tzID = rs.getString("timezone");
        timeZone = TimeZone.getTimeZone(tzID);
      }
      rs.close();
      pst.close();
    } catch (SQLException e) {
      Logger.warning("ADMIN", this, "getOrganizationTimeZone", "Error getting organization TimeZone!", e);
    } finally {
      DatabaseInterface.closeResources(db, pst, rs);
    }

    orgTimeZoneCache.put(orgId, timeZone);
    return timeZone;
  }
  
  
  public void updateOrganizationSettings(UserInfoInterface userInfo, String lang, String region, String timezone) {
    updateOrganizationSettings(userInfo, userInfo.getOrganization(), lang, region, timezone);
  }

  public void updateOrganizationSettings(UserInfoInterface userInfo, String orgId, String lang, String region, String timezone) {
    // 2 cases: user is system adm OR user is Org Adm and org is the same as user
    if(!(userInfo.isSysAdmin() || (userInfo.isOrgAdmin() && userInfo.getOrganization().equals(orgId)))) {
      Logger.warning(userInfo.getUtilizador(), this, "updateOrganizationLocale", "User not allowed to update " + orgId + " settings.");
      return;
    }
    
    // get username
    DataSource ds = null;
    Connection db = null;
    PreparedStatement pst = null;

    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      db.setAutoCommit(false);

      // get user settings
      pst = db.prepareStatement("update organization_settings set lang=?, region=?, timezone=? where organizationid=?");
      pst.setString(1, lang);
      pst.setString(2, region);
      pst.setString(3, timezone);
      pst.setString(4, orgId);
      int upd = pst.executeUpdate();
      pst.close();
      
      if(upd == 0) { // do not exists, so lets insert it
        pst = db.prepareStatement("insert into organization_settings (lang,region,timezone,organizationid) values (?,?,?,?)");
        pst.setString(1, lang);
        pst.setString(2, region);
        pst.setString(3, timezone);
        pst.setString(4, orgId);
        pst.executeUpdate();
        pst.close();
      }
      
      db.commit();
      orgLocaleCache.remove(orgId);
      orgTimeZoneCache.remove(orgId);
    } catch (SQLException e) {
      Logger.warning("ADMIN", this, "updateOrganizationSettings", "Error updating organization settings!", e);
    } finally {
      DatabaseInterface.closeResources(db, pst);
    }

  }
  
  private static class OrganizationSettingsImpl extends UserSettingsImpl {
    private static final long serialVersionUID = 5323558153521022156L;

    public OrganizationSettingsImpl() {
      super(Locale.getDefault(), TimeZone.getDefault(), Tutorial.TUTORIAL_DEFAULT, false, false);
    }
    
    public OrganizationSettingsImpl(Locale loc, TimeZone tz) {
      super(loc, tz, Tutorial.TUTORIAL_DEFAULT, false, false);
    }
    
    public boolean isDefault() {
      return true;
    }
  }
  
}
