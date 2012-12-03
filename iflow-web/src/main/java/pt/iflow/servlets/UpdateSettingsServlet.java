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
package pt.iflow.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.msg.IMessages;
import pt.iflow.api.userdata.Tutorial;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.core.SettingsBean;

/**
 * Servlet implementation class for Servlet: HelpDialogServlet
 * 
 * @web.servlet name="HelpDialog"
 * 
 * @web.servlet-mapping url-pattern="/HelpDialog"
 */
public class UpdateSettingsServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
  static final long serialVersionUID = 1L;

  public static final String METHOD_GENERATE_OPTIONS = "generateOptions";
  public static final String METHOD_UPDATE_TUTORIAL = "updateTutorial";
  public static final String METHOD_TOGGLE_HELP_MODE = "toggleHelpMode";
  public static final String METHOD_TUTORIAL_MODE_ON = "tutorialModeOn";
  public static final String METHOD_TUTORIAL_MODE_OFF = "tutorialModeOff";
  
  public UpdateSettingsServlet() {
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
    
    IMessages messages = ui.getMessages();

    // find what to do
    String method = request.getPathInfo();
    if (method == null) method = "";
    else if (method.indexOf("/") == 0) method = method.substring(1);
    
    // execute
    if (METHOD_GENERATE_OPTIONS.equals(method)) {
      Tutorial tutorial = Tutorial.getInstance(ui);  
      //out.print(tutorial.generateOptionsHtml());
      out.print(createDialogOutput(tutorial.generateOptionsHtml(), messages));

    }
    else if (METHOD_UPDATE_TUTORIAL.equals(method)) {
      String nextStep = null;
      String option = request.getParameter("option");
      String next = request.getParameter("next");
      
      if (Tutorial.TUTORIAL_DEFAULT.equals(option)) {
        nextStep = Tutorial.TUTORIAL_DEFAULT;
      }
      else if ("true".equals(next)) {
        nextStep = Tutorial.nextStep(option);
      }
      else {
        nextStep = option;
      }
      
      if (StringUtils.isEmpty(nextStep)) {
        out.print("error");
      }
      else {
        ui.getUserSettings().setTutorial(nextStep);
        SettingsBean.getInstance().updateUserSettings(ui, ui.getUserSettings());
  
        Tutorial tutorial = Tutorial.getInstance(ui);
        tutorial.setCurrentOption(option);
        
        //out.print(tutorial.generateOptionsHtml());
        out.print(createDialogOutput(tutorial.generateOptionsHtml(), messages));
      }
   
    }
    else if (METHOD_TOGGLE_HELP_MODE.equals(method)) {
      
      boolean helpMode;
      try {
        helpMode = ui.getUserSettings().isHelpMode();
        helpMode = !helpMode;
        ui.getUserSettings().setHelpMode(helpMode);
        SettingsBean.getInstance().updateUserSettings(ui, ui.getUserSettings());
        out.print(helpMode?"true":"false");
      }
      catch (Exception e) {
        out.print("error");        
      }
      
    }
    else if (METHOD_TUTORIAL_MODE_OFF.equals(method)) {      
      try {
        ui.getUserSettings().setTutorialMode(false);
        SettingsBean.getInstance().updateUserSettings(ui, ui.getUserSettings());
        out.print(ui.getUserSettings().getTutorial());
      }
      catch (Exception e) {
        out.print("error");        
      }      
    }
    else if (METHOD_TUTORIAL_MODE_ON.equals(method)) {      
      try {
//        if (ui.getUserSettings().getTutorial().equals(Tutorial.TUTORIAL_DEFAULT)) {
//          ui.getUserSettings().setTutorial(Tutorial.TUTORIAL_USERS);
//        }
        ui.getUserSettings().setTutorialMode(true);
        SettingsBean.getInstance().updateUserSettings(ui, ui.getUserSettings());
        String option = ui.getUserSettings().getTutorial();
        
        Tutorial tutorial = Tutorial.getInstance(ui);
        tutorial.setCurrentOption(option);
        
        out.print(createDialogOutput(tutorial.generateOptionsHtml(), messages));

      }
      catch (Exception e) {
        out.print("error");        
      }      
    }
    
  }    
    
  
  private String createDialogOutput(String content, IMessages messages) {
    StringBuffer sb = new StringBuffer();
    
    sb.append("<div id=\"tutorial_header\"class=\"hd\">");
    sb.append(messages.getString("tutorial.title"));
    sb.append("</div>");
    sb.append("<div class=\"bd tutorialdialogbody\">");
    sb.append("<div id=\"tutorialdialogbuttonbox\"");
    sb.append("<a href=\"javascript:resetTutorial();\"><img src=\"images/reset.png\"/></a>");
    sb.append("<a href=\"javascript:closeTutorial();\"><img src=\"images/close.png\"/></a>");
    sb.append("</div>");
    sb.append("<div class=\"tutorialoptions\">");
    sb.append(content);
    sb.append("</div>");
    sb.append("</div>");

    return sb.toString();
  }
  
}
