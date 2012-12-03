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

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;

import pt.iflow.api.msg.IMessages;
import pt.iflow.api.userdata.UserData;
import pt.iflow.api.userdata.UserDataAccess;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.core.AccessControlManager;

/**
 * Servlet implementation class for Servlet: UserDialogServlet
 * 
 * @web.servlet name="UserDialog"
 * 
 * @web.servlet-mapping url-pattern="/UserDialog"
 */
public class UserDialogServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
  static final long serialVersionUID = 1L;

  public UserDialogServlet() {
    super();
  }

  protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    ServletOutputStream out = response.getOutputStream();
    HttpSession session = request.getSession();
    UserInfoInterface ui = (UserInfoInterface) session.getAttribute(Const.USER_INFO);
    if(null == ui) {
      out.print("session-expired");
      return;
    }
    IMessages msg = ui.getMessages();

    String userId = request.getParameter("userid");
    

    out.print("<div class=\"hd\">");
    out.print(msg.getString("user_procs.dialog.title"));
    out.print("</div>");
    out.print("<div class=\"bd\">");

    try {
      UserDataAccess userDA = AccessControlManager.getUserDataAccess();
      UserData user = userDA.getUserData(userId);

      String sName = user.getName();
      if (sName == null) {
        sName = "";
      }
      out.print("<p>" + StringEscapeUtils.escapeHtml(sName) + "</p>");

      String sDept = user.getUnit();
      if (sDept == null) {
        sDept = "";
      }
      out.print("<p>" + StringEscapeUtils.escapeHtml(sDept) + "</p>");

      String sEmail = user.getEmail();
      if (sEmail == null) {
        sEmail = "";
      }
      out.print("<p>" + StringEscapeUtils.escapeHtml(sEmail) + "</p>");

      String sPhone = user.getMobileNumber();
      if (sPhone == null) {
        sPhone = "";
      }
      out.print("<p>" + StringEscapeUtils.escapeHtml(sPhone) + "</p>");
    } catch (Exception e) {
      out.print("<div class=\"error_msg\">");
      out.print(msg.getString("user_procs.dialog.error"));
      out.print("</div>");
    }
    out.println("</div>");
  }
}
