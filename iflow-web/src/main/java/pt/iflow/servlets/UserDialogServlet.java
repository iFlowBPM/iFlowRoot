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