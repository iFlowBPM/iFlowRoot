package pt.iflow.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.Repository;
import pt.iflow.api.core.RepositoryFile;
import pt.iflow.api.msg.IMessages;
import pt.iflow.api.userdata.Tutorial;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.core.Version;
import pt.iknow.utils.VelocityUtils;

/**
 * Servlet implementation class for Servlet: HelpDialogServlet
 * 
 * @web.servlet name="HelpDialog"
 * 
 * @web.servlet-mapping url-pattern="/HelpDialog"
 */
public class HelpDialogServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
  static final long serialVersionUID = 1L;

  static final String USERS_CONFIG_LINK = "javascript:tabber_save(4,'Admin/admin_nav.jsp','sel=6','Admin/UserManagement/useradm.jsp','');selectedItem('admin',6);";
  static final String ORG_UNITS_CONFIG_LINK = "javascript:tabber_save(4,'Admin/admin_nav.jsp','sel=9','Admin/UserManagement/unitadm.jsp','');selectedItem('admin',9);";
  static final String PROFILES_CONFIG_LINK = "javascript:tabber_save(4,'Admin/admin_nav.jsp','sel=7','Admin/UserManagement/profileadm.jsp','');selectedItem('admin',7);";
  static final String FLOWS_CONFIG_LINK = "javascript:tabber_save(4,'Admin/admin_nav.jsp','sel=29','Admin/flow_editor.jsp','');selectedItem('admin',29);";
  static final String PERMISSIONS_CONFIG_LINK = "javascript:tabber_save(4,'Admin/admin_nav.jsp','sel=13','Admin/flow_settings.jsp','');selectedItem('admin',13);";
  static final String MENUS_CONFIG_LINK = "javascript:tabber_save(4,'Admin/admin_nav.jsp','sel=15','Admin/flow_menu_edit','');selectedItem('admin',15);";
  static final String CLOSE_FUNCTION = "javascript:closeHelpDialog();tutorialModeOn();";
  static final String INIT_PROC_LINK = "javascript:closeHelpDialog();showBubble();updateTutorial('users','false');tutorialModeOn();openTutorial('users','false');";

  public static final String METHOD_OPEN_HELP = "openHelp";

  public HelpDialogServlet() {
    super();
  }

  protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    response.setCharacterEncoding("UTF-8");
    response.setContentType("text/html; charset=UTF-8");
    PrintWriter out = response.getWriter();

    HttpSession session = request.getSession();
    UserInfoInterface ui = (UserInfoInterface) session.getAttribute(Const.USER_INFO);
    
    if(null == ui) {
      out.print("session-expired");
      return;
    }

    Hashtable<String,Object> htSubst = null;
    
    String file = request.getParameter("id");
    
    // find what to do
    String method = request.getPathInfo();
    if (method == null) {
      method = "";
    }
    else if (method.indexOf("/") == 0) {
      method = method.substring(1);
    }

    IMessages messages = ui.getMessages();

    // execute
    if (METHOD_OPEN_HELP.equals(method)) {
      htSubst = new Hashtable<String,Object>();
      
      htSubst.put("linkTasks", "javascript:tabber('help','help_nav.jsp','sel=3','help.jsp','');show_help('help_tasks');");
      htSubst.put("messages", messages);
      htSubst.put("navSep", "»");
      
      file = file + ".vm";
      out.print("<div class=\"hd\">");
      out.print(messages.getString("help_nav.title"));
      out.print("</div>");
      out.print("<div class=\"bd\"><div class=\"dialogcontent\">");
      out.print("<div id=\"helpwrapper\" class=\"help_box_wrapper\">");
      out.print("<div id=\"helpsection\" class=\"help_box\">");
      out.print(processTemplate(ui, file, htSubst));
      out.print("</div>");
      out.print("</div>");
      out.print("</div>");
      out.print("</div>");

    }
    else {
      out.print("<div class=\"hd\">");
      out.print(messages.getString("HelpDialogServlet.dialog.title"));
      out.print("</div>");
      out.print("<div class=\"bd\">");

      String item = request.getParameter("item");
      
      final String TUTORIAL_ACTIVE_CLASS = "active";

      String prevString = messages.getString("button.previous");
      String nextString = messages.getString("button.next");
      String closeString = messages.getString("button.close");

      String tutorial_active = "";
      String users_active = "";
      String org_units_active = "";
      String profiles_active = "";
      String flows_active = "";
      String permissions_active = "";
      String menus_active = "";
      String closeFunction = CLOSE_FUNCTION;

      if (StringUtils.isEmpty(item)) {
        item = ui.getUserSettings().getTutorial();
      }

      if (Tutorial.TUTORIAL_DEFAULT.equals(item)) {
        tutorial_active = TUTORIAL_ACTIVE_CLASS;
        prevString = "";      
        nextString = "";
        closeString = "";
      }
      else if (Tutorial.TUTORIAL_USERS.equals(item)) {
        users_active = TUTORIAL_ACTIVE_CLASS;
      }
      else if (Tutorial.TUTORIAL_ORG_UNITS.equals(item)) {
        org_units_active = TUTORIAL_ACTIVE_CLASS;
      }
      else if (Tutorial.TUTORIAL_PROFILES.equals(item)) {
        profiles_active = TUTORIAL_ACTIVE_CLASS;
      }
      else if (Tutorial.TUTORIAL_FLOWS.equals(item)) {
        flows_active = TUTORIAL_ACTIVE_CLASS;
      }
      else if (Tutorial.TUTORIAL_PERMISSIONS.equals(item)) {
        permissions_active = TUTORIAL_ACTIVE_CLASS;
      }
      else if (Tutorial.TUTORIAL_MENUS.equals(item)) {
        menus_active = TUTORIAL_ACTIVE_CLASS;
        nextString = "";
      }

      htSubst = new Hashtable<String,Object>();
      htSubst.put("messages", messages);
      
      htSubst.put("navSep", "»");
      htSubst.put("version", Version.VERSION);
      htSubst.put("username", ui.getUserFullName());    
      htSubst.put("tutorialActive", tutorial_active);   
      htSubst.put("usersActive", users_active);   
      htSubst.put("orgUnitsActive", org_units_active);  
      htSubst.put("profilesActive", profiles_active);  
      htSubst.put("flowsActive", flows_active);  
      htSubst.put("permissionsActive", permissions_active);  
      htSubst.put("menusActive", menus_active);  
      htSubst.put("users_config_link", USERS_CONFIG_LINK);    
      htSubst.put("org_units_config_link", ORG_UNITS_CONFIG_LINK);  
      htSubst.put("profiles_config_link", PROFILES_CONFIG_LINK);  
      htSubst.put("flows_config_link", FLOWS_CONFIG_LINK);  
      htSubst.put("permissions_config_link", PERMISSIONS_CONFIG_LINK);  
      htSubst.put("menus_config_link", MENUS_CONFIG_LINK);  
      htSubst.put("close_function", closeFunction);  

      htSubst.put("prevString", prevString);  
      htSubst.put("nextString", nextString);  
      htSubst.put("closeString", closeString);
      htSubst.put("initProcLink", INIT_PROC_LINK);  
      
      out.print(processTemplate(ui, file, htSubst));
      out.print("</div>");

    }

  }
  
  private String processTemplate(UserInfoInterface ui, String file, Hashtable<String, Object> htSubst) {
    InputStream contentStream = null;
    try {
      Repository rep = BeanFactory.getRepBean();
      RepositoryFile contentFile = rep.getHelp(ui, file);
      if (contentFile != null) {
        contentStream = contentFile.getResourceAsStream();

        String sHtml = VelocityUtils.processTemplate(htSubst, new InputStreamReader(contentStream, "UTF-8"));

        return sHtml;
      }
    }
    catch (Exception e) {
      Logger.error(ui.getUtilizador(), this, "processTemplate", "Unable to process template.", e);
    }
    finally { 
      try { if (contentStream != null) contentStream.close(); } catch (Exception e) {}
    }

    return null;
  }
  
}