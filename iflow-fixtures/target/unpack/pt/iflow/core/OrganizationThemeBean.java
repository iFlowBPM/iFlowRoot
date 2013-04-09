/*
 *
 * Created on Oct 12, 2005 by mach
 *
 */

package pt.iflow.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.presentation.OrganizationTheme;
import pt.iflow.api.presentation.OrganizationThemeData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;

/**
 * XDoclet-based session bean.  The class must be declared
 * public according to the EJB specification.
 *
 * To generate the EJB related files to this EJB:
 *		- Add Standard EJB module to XDoclet project properties
 *		- Customize XDoclet configuration for your appserver
 *		- Run XDoclet
 *
 * Below are the xdoclet-related tags needed for this EJB.
 * 
 */
public class OrganizationThemeBean implements OrganizationTheme {
  
  private static final String QUERY = "SELECT organizationid,theme,style_url,logo_url, menu_location, menu_style, proc_menu_visible FROM organization_theme WHERE organizationid=?";

  private static OrganizationThemeBean instance = null;
  
  private OrganizationThemeBean() { }
  
  public static OrganizationThemeBean getInstance() {
    if(null == instance)
      instance = new OrganizationThemeBean();
    return instance;
  }

  /**
   * Obtains the theme information for an organization
   *
   */
  public OrganizationThemeData getOrganizationTheme(UserInfoInterface userInfo) {
    
    OrganizationThemeData retObj = new OrganizationThemeData(null, null, "default","PublicFiles/iflow.css","Logo","left","list",true);
    if(userInfo == null) {
      if(Logger.isDebugEnabled()) {
        Logger.warning("", this, "getOrganizationTheme", "No user is defined at this point, returning default theme.");
      }
      return retObj;
    }
    String orgId = userInfo.getOrganization();
    String orgName = userInfo.getCompanyName();
    
    DataSource ds = Utils.getDataSource();
    Connection db = null;
    PreparedStatement st = null;
    ResultSet rs = null;

    try {
      db = ds.getConnection();
      st = db.prepareStatement(QUERY);
      st.setString(1,orgId);
      rs = st.executeQuery();
            
      if (rs.next()) {
        retObj = new OrganizationThemeData(orgId, orgName, rs.getString("theme"),rs.getString("style_url"),rs.getString("logo_url"),rs.getString("menu_location"),rs.getString("menu_style"), (rs.getInt("proc_menu_visible") == 1));
      } else {
        retObj = new OrganizationThemeData(orgId, orgName, "default","PublicFiles/iflow.css","Logo","left","list", true);
      }
      rs.close();
    }
    catch (SQLException sqle) {
      Logger.error(userInfo.getUtilizador(), this, "getOrganizationTheme", "Could not retrieve organization theme", sqle);
    }
    finally {
      DatabaseInterface.closeResources(db, st, rs);
    }
    return retObj;
  }
  
  public boolean deleteOrganizationData(UserInfoInterface userInfo) {

    if (!userInfo.isOrgAdmin()) {
      Logger.debug(userInfo.getUtilizador(), this, "deleteOrganizationData", "not administrator, exiting");
      return false;
    }
    
    String orgId = userInfo.getOrganization();
    Logger.debug(userInfo.getUtilizador(), this, "deleteOrganizationData", "Removing theme for " + orgId);


    boolean retObj = false;

    DataSource ds = Utils.getDataSource();
    Connection db = null;
    PreparedStatement st = null;

    try {
      db = ds.getConnection();
      st = db.prepareStatement("delete from organization_theme where organizationid=?");
      st.setString(1, orgId);
      st.executeUpdate();

      retObj = true;
    } catch(SQLException sqle) {
      Logger.error(userInfo.getUtilizador(), this, "deleteOrganizationData", "Could not remove organization theme", sqle);
    }
    finally {
      DatabaseInterface.closeResources(db, st);
    }

    return retObj;
  }

  public boolean insertOrganizationData(UserInfoInterface userInfo, String theme, String style, String logo, String menuLocation, String menuStyle, boolean procMenuVisible) {
    return updateOrganizationData(userInfo, theme, style, logo, menuLocation, menuStyle, procMenuVisible);
  }

  public boolean updateOrganizationData(UserInfoInterface userInfo, String theme, String style, String logo, String menuLocation, String menuStyle, boolean procMenuVisible) {
    if(null == userInfo) return false;
    boolean result = false;
    boolean modify=false;

    DataSource ds = null;
    Connection db = null;

    PreparedStatement pst = null;
    ResultSet rs = null;

    if (!userInfo.isOrgAdmin()) {
      Logger.debug(userInfo.getUtilizador(), this, "insertOrUpdateOrganizationData", "not administrator, exiting");
      return false;
    }
    
    String orgId = userInfo.getOrganization();
    Logger.debug(userInfo.getUtilizador(), this, "insertOrUpdateOrganizationData", "Updating theme for " + orgId);


    String curLogo = null;
    String curStyle = null;
    String curTheme = null;
//    String curMenuLocation = null;
//    String curMenuStyle = null;
//    boolean curProcMenuVisible = true;
    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      db.setAutoCommit(false);

      String sql = QUERY;
      if(Logger.isDebugEnabled()) {
        Logger.debug(userInfo.getUtilizador(), this, "insertOrUpdateOrganizationData", "QUERY=" + sql);
      }
      pst = db.prepareStatement(sql);
      pst.setString(1, orgId);
      rs = pst.executeQuery();
      modify = rs.next();
      if(modify) {
        curTheme = rs.getString("theme");
        curLogo = rs.getString("logo_url");
        curStyle = rs.getString("style_url");
//        curMenuLocation = rs.getString("menu_location");
//        curMenuStyle = rs.getString("menu_style");
//        curProcMenuVisible = rs.getInt("proc_menu_visible") == 1;
      }
      rs.close();
      rs = null;
      pst.close();

      if(null == theme) theme = curTheme;
      if(null == style) style = curStyle;
      if(null == style) style = "default"; // style cannot be null?
      if(null == logo) logo = curLogo;
      if(null == logo) logo = "Logo";  // Logo cannot be null?

      if(modify) {
        sql = "update organization_theme set THEME=?,STYLE_URL=?,LOGO_URL=?,MENU_LOCATION=?,MENU_STYLE=?,PROC_MENU_VISIBLE=? where ORGANIZATIONID=?";
        if(Logger.isDebugEnabled()) {
          Logger.debug(userInfo.getUtilizador(), this, "insertOrUpdateOrganizationData", "QUERY=" + sql);
        }
        pst = db.prepareStatement(sql);
      } else {
        sql = "insert into organization_theme (THEME,STYLE_URL,LOGO_URL,MENU_LOCATION,MENU_STYLE,PROC_MENU_VISIBLE,ORGANIZATIONID) VALUES (?,?,?,?,?,?,?)";
        if(Logger.isDebugEnabled()) {
          Logger.debug(userInfo.getUtilizador(), this, "insertOrUpdateOrganizationData", "QUERY=" + sql);
        }
        pst = db.prepareStatement(sql);
      }
      pst.setString(1, theme);
      pst.setString(2, style);
      pst.setString(3, logo);
      pst.setString(4, menuLocation);
      pst.setString(5, menuStyle);
      pst.setInt(6, (procMenuVisible ? 1 : 0));
      pst.setString(7, orgId);
      if (Logger.isDebugEnabled()) {
        Logger.debug(userInfo.getUtilizador(), this, "insertOrUpdateOrganizationData", "values: {1=" + theme + ", 2=" + style + ", 3="
            + logo + ", 4=" + menuLocation + ", 5=" + menuStyle + ", 6=" + orgId + ", 7=" + (procMenuVisible ? 1 : 0) + "}.");
      }
      
      pst.executeUpdate();

      db.commit();
      result = true;

    }
    catch (SQLException e) {
      result = false;
      Logger.error(userInfo.getUtilizador(), this, "insertOrUpdateOrganizationData", "Theme not updated!", e);
    }
    finally {
      DatabaseInterface.closeResources(db, pst, rs);
    }

    return result;
  }


  /**
   * To be removed.
   * @return
   * @deprecated
   */
  List<OrganizationThemeData> getOrganizationThemeData() {
    ArrayList<OrganizationThemeData> altmp = new ArrayList<OrganizationThemeData>();

    DataSource ds = Utils.getDataSource();
    Connection db = null;
    Statement st = null;
    ResultSet rs = null;
    try {
      db = ds.getConnection();
      st = db.createStatement();
      rs = st.executeQuery("select * from organization_theme order by organizationid");

      while (rs.next()) {
        String sOrgId = rs.getString("organizationid");
        String orgName = sOrgId;
        
        OrganizationThemeData themeData = new OrganizationThemeData(sOrgId, orgName, rs.getString("theme"), rs.getString("style_url"), rs.getString("logo_url"), rs.getString("menu_location"), rs.getString("menu_style"), (rs.getInt("proc_menu_visible") == 1));
        altmp.add(themeData);
      }
    } catch (SQLException sqle) {}
    finally {
      DatabaseInterface.closeResources(db, st, rs);
    }
    return altmp;
  }


  public boolean removeOrganizationTheme(UserInfoInterface sysAdm, String organizationId) {

    if (!sysAdm.isSysAdmin()) {
      Logger.debug(sysAdm.getUtilizador(), this, "removeOrganizationTheme", "not administrator, exiting");
      return false;
    }
    
    Logger.debug(sysAdm.getUtilizador(), this, "removeOrganizationTheme", "Removing theme for " + organizationId);


    boolean retObj = false;

    DataSource ds = Utils.getDataSource();
    Connection db = null;
    PreparedStatement st = null;

    try {
      db = ds.getConnection();
      st = db.prepareStatement("delete from organization_theme where organizationid=?");
      st.setString(1, organizationId);
      st.executeUpdate();

      retObj = true;
    } catch(SQLException sqle) {
      Logger.error(sysAdm.getUtilizador(), this, "deleteOrganizationData", "Could not remove organization theme", sqle);
    }
    finally {
      DatabaseInterface.closeResources(db, st);
    }

    return retObj;
  }

}
