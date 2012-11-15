/**
 * <p>Title: PresentationManager</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: iKnow</p>
 * @author Pedro Monteiro
 * @version 1.0
 */
package pt.iflow.api.presentation;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.Repository;
import pt.iflow.api.core.RepositoryFile;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.flows.FlowSetting;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.ServletUtils;
import pt.iflow.api.utils.UserInfoFactory;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;
import pt.iknow.utils.VelocityUtils;

public class PresentationManager {

  public static final int nSUBJECT_IDX = 0;

  public static final int nBODY_IDX = 1;

  /**
   * Constructor
   */
  protected PresentationManager() {
  }

  protected static String chooseTheme(String[] saProfiles) {
    String retObj = "default";
    int max = 0;

    if (saProfiles == null || saProfiles.length == 0)
      return retObj;

    DataSource ds = Utils.getDataSource();
    Connection db = null;
    Statement st = null;
    ResultSet rs = null;

    StringBuffer sbQuery = new StringBuffer(
        "select * from profiles_interface where profile in (''");

    for (int i = 0; i < saProfiles.length; i++) {
      sbQuery.append(",'").append(saProfiles[i]).append("'");
    }
    sbQuery.append(")");

    try {
      db = ds.getConnection();
      st = db.createStatement();
      rs = st.executeQuery(sbQuery.toString());

      while (rs.next()) {
        if (rs.getInt("weight") > max) {
          retObj = rs.getString("theme");
          max = rs.getInt("weight");
        }
      }
      rs.close();
    }
    catch (SQLException sqle) {
      sqle.printStackTrace();
    }
    finally {
      DatabaseInterface.closeResources(db, st, rs);
    }
    return retObj;
  }

  public static String buildLoginPage(HttpServletResponse response, UserInfoInterface userInfo, Hashtable<String,Object> htSubst) throws Exception {
    if (userInfo == null) {
      userInfo = BeanFactory.getUserInfoFactory().newGuestUserInfo();
    }
    String login = userInfo.getUtilizador();
    String location = PresentationManager.class.getName();
    String template;
    RepositoryFile target = BeanFactory.getRepBean().getTheme(userInfo, Const.LOGIN_TEMPLATE + ".vm");
    RepositoryFile def = BeanFactory.getRepBean().getTheme(userInfo, Const.LOGIN_TEMPLATE_DEFAULT + ".vm");
    if (target.exists()) {
      if (Logger.isDebugEnabled()) {
        Logger.debug(login, location, "buildLoginPage", "Using login macro: " + target.getName());
      }
      template = Const.LOGIN_TEMPLATE;
    } else if (def.exists()) {
      if (Logger.isDebugEnabled()) {
        Logger.debug(login, location, "buildLoginPage", "Unknown login macro '" + target.getName() + "', defaulting to: " + def.getName());
      }
      template = Const.LOGIN_TEMPLATE_DEFAULT;
    } else {
      Logger.error(login, location, "buildLoginPage", "Missing default login page: " + def.getName());
      throw new Exception("Missing default login page!");
    }
    return PresentationManager.buildPage(response, userInfo, htSubst, template);
  }
  
  public static String buildMessageBoard(HttpServletResponse response, UserInfoInterface userInfo, Hashtable<String,Object> htSubst) {
    return PresentationManager.buildPage(response, userInfo, htSubst, "msgboard");
  }

  public static String buildMainPage(HttpServletResponse response, UserInfoInterface userInfo, Hashtable<String,Object> htSubst) {
    return PresentationManager.buildPage(response, userInfo, htSubst, "main");
  }

  public static String buildMainPageContent(HttpServletResponse response, UserInfoInterface userInfo, Hashtable<String,Object> htSubst) {
    return PresentationManager.buildPage(response, userInfo, htSubst, "main_content");
  }

//  public static String buildPageTop(HttpServletResponse response, UserInfoInterface userInfo, Hashtable<String,Object> htSubst) {
//    if (userInfo.isSysAdmin()) {
//      htSubst.put("user_can_admin", Boolean.TRUE);
//    }
//    else {
//      htSubst.put("user_can_admin", Boolean.FALSE);
//    }
//    return PresentationManager.buildPage(response, userInfo, htSubst, "top");
//  }

  public static String buildPageSimpleTop(HttpServletResponse response, UserInfoInterface userInfo, Hashtable<String,Object> htSubst) {
    return PresentationManager.buildPage(response, userInfo, htSubst, "simple_top");
  }

//  public static String buildPageBottom(HttpServletResponse response, UserInfoInterface userInfo, Hashtable<String,Object> htSubst) {
//    return PresentationManager.buildPage(response, userInfo, htSubst, "bottom");
//  }

  public static String buildPageSimpleBottom(HttpServletResponse response, UserInfoInterface userInfo, Hashtable<String,Object> htSubst) {
    return PresentationManager.buildPage(response, userInfo, htSubst, "simple_bottom");
  }

  public static String buildPage(HttpServletResponse response, UserInfoInterface userInfo, Hashtable<String,Object> htSubst,
      String sTB) {
    return buildPage(response==null?null:new ServletUtils(response), userInfo, htSubst, sTB);
  }
  
  public static String buildPage(ServletUtils response, UserInfoInterface userInfo, Hashtable<String,Object> htSubst,
      String sTB) {

//--------------------------------------------INSERIR NA HASH TABS SEM PERMISSOES  
if(userInfo.isLogged()){
    int [] ID_Interface = new int[0];                    
    String nome = "Tab";                                 
    ID_Interface = tabsRejeitadas(userInfo);            //Array com as nao permissoes   
    //Inserir na Tabela de Hash as chaves das tabs que n�o tem acesso Controlo feito no Main.vm
    for(int i = 0 ; i< ID_Interface.length;i++)
    {   
    String chave = nome + ID_Interface[i];             //Prefixo para chave na BD ("Tab" + ID)
    htSubst.put(chave, "no");
    }
}//-----------------------------------------FIM

    if(null == htSubst) htSubst = new Hashtable<String,Object>();
    
    if(null != response) htSubst.put("response", response);
    
    String sTheme = null;

    String sHtml = null;
    InputStream isTmp = null;
    try {
      OrganizationTheme orgTheme = BeanFactory.getOrganizationThemeBean();
      OrganizationThemeData orgThemeData = orgTheme.getOrganizationTheme(userInfo);
      if(orgThemeData !=null) {
        sTheme = orgThemeData.getThemeName();
      }
      if(sTheme == null) {
        Logger.warning(userInfo.getUtilizador(), "PresentationManager", "buildPage", "No theme found. Using default");
        sTheme = "default";
      }
      Repository rep = BeanFactory.getRepBean();
      String themeFile = sTheme + "/" + sTB + ".vm";
      isTmp = rep.getTheme(userInfo, themeFile).getResourceAsStream();
      if (isTmp == null)
        throw new Exception("Couldn't get " + sTB + " theme file '" + themeFile + "' from repository");

      boolean displayInfo = false;
      String infoContent = "alert('No peeking allowed!');";
      try {
        if (htSubst.containsKey("flowid")) {
          int flowid = Integer.valueOf("" + htSubst.get("flowid"));
          FlowSetting fs = BeanFactory.getFlowSettingsBean().getFlowSetting(flowid, Const.sENABLE_HISTORY);
          if (fs != null && !StringUtils.equals(fs.getValue(), Const.sENABLE_HISTORY_NO)) {
            displayInfo = true;
            infoContent = "showFlowInfoItem('" + flowid + "');";
          }
        }
      } catch (Exception ex) {
        Logger.error(userInfo.getUtilizador(), PresentationManager.class, "buildPage", "Exception caught: ", ex);
      }
      htSubst.put("showButInfo", displayInfo);
      htSubst.put("infoContent", infoContent.toString());

      htSubst.put("orgTheme", BeanFactory.getOrganizationThemeBean().getOrganizationTheme(userInfo).getThemeName());
      sHtml = VelocityUtils.processTemplate(htSubst, new InputStreamReader(isTmp, "UTF-8"));

      if (sHtml == null) {
        StringBuffer sbtmp = new StringBuffer("<html>");
        sbtmp.append("<script language=\"JavaScript\">\n");
        sbtmp.append("  <!--\n");
        sbtmp.append("    self.location.href=\"error.jsp\";");
        sbtmp.append("  // -->");
        sbtmp.append("</script>");
        return sbtmp.toString();
      }

    }
    catch (Exception e) {
      e.printStackTrace();
    } finally {
      if(null != isTmp) {
        try {
          isTmp.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return sHtml;
  }
//------------------------------------------ TIRAR DA BD ARRAY IDs TABS SEM PERMISSAO
  public static int[] tabsRejeitadas( UserInfoInterface userInfo )
  {   
    int [] ids = new int [0];
    int i = 0;
    int tam = 0;
    DataSource ds = Utils.getDataSource();
    Connection db = null;
    Statement st = null;
    ResultSet rs = null;
    try {
      db = ds.getConnection();
      st = db.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
      
      String sqlQuery;

      //Tirar vector com nao permissoes da organiza�ao e dos perfis
      if (StringUtils.isNumeric(userInfo.getUserId())) {
        sqlQuery = "SELECT tabid FROM profiles_tabs " 
          + "WHERE profileid in (SELECT profileid FROM userprofiles WHERE userid="+userInfo.getUserId()+") "
          + "UNION SELECT tabid FROM organizations_tabs WHERE organizationid="+userInfo.getOrganization();
      } else {
        StringBuilder sbProfiles = new StringBuilder();
        String aux = "('";
        for (String profile : userInfo.getProfiles()) {
          sbProfiles.append(aux);
          sbProfiles.append(profile);
          aux = "', '";
        }
        sbProfiles.append("'))");
        sqlQuery = "SELECT tabid FROM profiles_tabs" 
          + " WHERE profileid in (SELECT profileid FROM profiles WHERE name in " + sbProfiles.toString()
          + " UNION SELECT tabid FROM organizations_tabs WHERE organizationid=" + userInfo.getOrganization();
      }
      
      rs = st.executeQuery(sqlQuery);

   //Inicializar array com numero de tabs
   while (rs.next()) tam++;
   rs.beforeFirst();  
   ids = new int[tam];
    
      //Preencher array com IDs das tabs
      while (rs.next()){
      ids[i] = rs.getInt(1);
      i++;
      }     
      rs.close();
    }
    catch (SQLException sqle) {
      sqle.printStackTrace();
    }
    finally {
      DatabaseInterface.closeResources(db, st, rs);
    }   
    return ids;
  }
//------------------------------------------ FIM
}
