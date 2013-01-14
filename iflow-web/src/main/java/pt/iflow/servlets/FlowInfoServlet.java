package pt.iflow.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.Repository;
import pt.iflow.api.core.RepositoryFile;
import pt.iflow.api.flows.FlowSetting;
import pt.iflow.api.msg.IMessages;
import pt.iflow.api.transition.FlowStateHistoryTO;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iknow.utils.VelocityUtils;

public class FlowInfoServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
  private static final long serialVersionUID = 1L;

  public FlowInfoServlet() {
    super();
  }

  protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    PrintWriter out = response.getWriter();
    HttpSession session = request.getSession();
    UserInfoInterface ui = (UserInfoInterface) session.getAttribute(Const.USER_INFO);
    String login = (ui == null ? "<none>" : ui.getUtilizador());
    String pnumber = request.getParameter("pnumber");
    String sFlowid = request.getParameter("flowid");
    if (StringUtils.isBlank(pnumber) || StringUtils.isBlank(sFlowid)) {
      Logger.warning(login, this, "service", "Required field is missing: flowid=" + sFlowid + "; pnumber=" + pnumber);
      out.print("Error");
    } else {
      if (Logger.isDebugEnabled()) {
        Logger.debug(login, this, "service", "Entered servlet with parameters flowid='" + sFlowid + "' and pnumber='" + pnumber
            + "'");
      }
      IMessages messages = ui.getMessages();
      response.setCharacterEncoding("UTF-8");
      response.setContentType("text/html; charset=UTF-8");
      int flowid = -10;
      try {
        flowid = Integer.parseInt(sFlowid);
      } catch (NumberFormatException ex) {
        Logger.error(login, this, "service", "", ex);
        return;
      }
      if (flowid > 0 && StringUtils.isNotEmpty(pnumber)) {
        String cssPath = Const.APP_URL_PREFIX + "Themes/"
            + BeanFactory.getOrganizationThemeBean().getOrganizationTheme(ui).getThemeName() + "/css/";
        out.print("<link rel=\"stylesheet\" href=\"" + cssPath + "iflow_main.css" + "\" type=\"text/css\">");
        out.print("<link rel=\"stylesheet\" href=\"" + cssPath + "iflow.css" + "\" type=\"text/css\">");
        out.print("<div class=\"hd\">");
        out.print(messages.getString("flowInfo.title"));
        out.print("</div>");
        out.print("<div class=\"bd\"><div class=\"dialogcontent\">");
        out.print("<div id=\"helpwrapper\" class=\"help_box_wrapper\">");
        out.print("<div id=\"helpsection\" class=\"help_box\">");
        List<FlowStateHistoryTO> history = BeanFactory.getProcessManagerBean().getFullProcessHistory(ui, flowid, pnumber);
        if (history.isEmpty()) {
          out.print("<p>" + messages.getString("flowInfo.empty") + "</p>");
        } else {
          FlowSetting fs = BeanFactory.getFlowSettingsBean().getFlowSetting(flowid, Const.sENABLE_HISTORY);
          Hashtable<String, Object> htSubst = new Hashtable<String, Object>();
          htSubst.put("messages", messages);
          htSubst.put("showUser", StringUtils.equals(fs.getValue(), Const.sENABLE_HISTORY_FULL));
          htSubst.put("history", history);
          out.print(processTemplate(ui, "flowInfo.vm", htSubst));
        }
        out.print("</div>");
        out.print("</div>");
        out.print("</div>");
        out.print("</div>");
      }
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
    } catch (Exception e) {
      Logger.error(ui == null ? "<none>" : ui.getUtilizador(), this, "processTemplate", "Unable to process template.", e);
    } finally {
      try {
        if (contentStream != null)
          contentStream.close();
      } catch (Exception e) {
      }
    }
    return null;
  }
}
