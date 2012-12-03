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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.flows.FlowSetting;
import pt.iflow.api.flows.IFlowData;
import pt.iflow.api.presentation.PresentationManager;
import pt.iflow.api.presentation.ProcessPresentation;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessHeader;
import pt.iflow.api.repository.RepositoryURIResolver;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iknow.pdf.PDFGenerator;
import pt.iknow.utils.html.FormData;
import pt.iknow.utils.html.FormUtils;
import pt.iknow.xslfo.FoTemplate;

/**
 * Servlet implementation class for Servlet: UserProcPrintServlet
 * 
 * @web.servlet name="UserProcPrintServlet"
 * @web.servlet-mapping url-pattern="/UserProcPrint"
 */
public class UserProcPrintServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
  static final long serialVersionUID = 1L;

  public static String PARAM_FLOWID = "flowid";
  public static String PARAM_PID = "pid";
  public static String PARAM_SUBPID = "subpid";

  /*
   * (non-Java-doc)
   * 
   * @see javax.servlet.http.HttpServlet#HttpServlet()
   */
  public UserProcPrintServlet() {
    super();
  }

  @Override
  protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    UserInfoInterface userInfo = (UserInfoInterface) request.getSession().getAttribute(Const.USER_INFO);
    if (null == userInfo) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Operation not permitted");
      PrintWriter w = response.getWriter();
      w.println("Not authorized.");
      w.close();
      return;
    }

    int flowid = -1;
    int pid = -1;
    int subpid = -1;

    ProcessData procData = null;

    try {
      FormData formData = FormUtils.parseRequest(request, Const.nUPLOAD_THRESHOLD_SIZE, Const.nUPLOAD_MAX_SIZE,
          Const.fUPLOAD_TEMP_DIR);
      String sflowid = formData.getParameter(PARAM_FLOWID);
      String spid = formData.getParameter(PARAM_PID);
      String ssubpid = formData.getParameter(PARAM_SUBPID);
      flowid = Integer.parseInt(sflowid);
      pid = Integer.parseInt(spid);
      subpid = Integer.parseInt(ssubpid);
    } catch (Exception e) {
      Logger.error(userInfo.getUtilizador(), this, "service", "Error parsing request", e);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
      return;
    }

    try {
      procData = BeanFactory.getProcessManagerBean().getProcessData(userInfo, new ProcessHeader(flowid, pid, subpid), Const.nALL_PROCS);
    } 
    catch (Exception e) {
      Logger.error(userInfo.getUtilizador(), this, "service", "Error fetching process data", e);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
      return;
    }

    if (!BeanFactory.getProcessManagerBean().canViewProcess(userInfo, procData)) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Operation not permitted");
      PrintWriter w = response.getWriter();
      w.println("Not authorized.");
      w.close();
      return;
    }

    IFlowData flow = BeanFactory.getFlowHolderBean().getFlow(userInfo, flowid);

    FlowSetting setting = BeanFactory.getFlowSettingsBean().getFlowSetting(flowid, Const.sDETAIL_PRINT_STYLESHEET);
    if(null == setting || StringUtils.isEmpty(setting.getValue())) {
      Logger.info(userInfo.getUtilizador(), this, "service", "No print template defined");
      Map<String, Object> env = new HashMap<String, Object>();
      env.put("flowid", flowid);
      env.put("pid", pid);
      env.put("subpid", subpid);
      env.put("flowname", flow.getName());
      env.put("application", flow.getApplicationName());
      env.put("pnumber", procData==null?"":procData.getPNumber());

      Map<String, String> processDetail = ProcessPresentation.getProcessDetail(userInfo, procData);
      String[] vars, vals;
      if (null == processDetail) {
        vars = new String[0];
        vals = new String[0];
      } else {
        vars = new String[processDetail.size()];
        vals = new String[processDetail.size()];
        int n = 0;
        Iterator<String> iter = processDetail.keySet().iterator();
        while (iter.hasNext()) {
          vars[n] = iter.next();
          vals[n] = processDetail.get(vars[n]);
          ++n;
        }
      }

      env.put("variables", vars);
      env.put("values", vals);

      Hashtable<String,Object> htSubst = new Hashtable<String, Object>();
      htSubst.put("processDetail", processDetail);
      htSubst.put("make_head",true);
      htSubst.put("url_prefix", Const.APP_URL_PREFIX);
      htSubst.put("sJSP", "#");
      htSubst.put("hmHidden", new HashMap<String,String>());
      int procFlowid = 0;
      int procPid = 0;
      int procSubpid = 0;
      if(procData != null) {
        procFlowid = procData.getFlowId();
        procPid = procData.getPid();
        procSubpid = procData.getSubPid();
      }

      htSubst.put("procFlowid", procFlowid);
      htSubst.put("procPid", procPid);
      htSubst.put("procSubpid", procSubpid);

      //  messages.....
      htSubst.put("noDetail",userInfo.getMessages().getString("user_proc_detail.msg.noProcessDetail"));
      htSubst.put("variableLabel",userInfo.getMessages().getString("user_proc_detail.field.variable"));
      htSubst.put("valueLabel",userInfo.getMessages().getString("user_proc_detail.field.value"));

      // buttons
      htSubst.put("buttonList", new ArrayList<Map<String,String>>());


      String page = PresentationManager.buildPage(response, userInfo, htSubst, "proc_detail");

      PrintWriter writer = response.getWriter();
      writer.println(page);
      writer.println("<script language=\"JavaScript\" type=\"text/javascript\">");
      writer.println("if (window.print) window.print();");
      writer.println("</script>");
      writer.flush();
      return;
    }

    InputStream data = BeanFactory.getRepBean().getPrintTemplate(userInfo, setting.getValue()).getResourceAsStream();
    ByteArrayOutputStream pdfOut = new ByteArrayOutputStream();
    bsh.Interpreter engine = procData.getInterpreter(userInfo);
    try {
      FoTemplate tpl = FoTemplate.compile(data);
      tpl.setUseLegacyExpressions(true);
      PDFGenerator generator = new PDFGenerator(tpl);
      generator.addURIResolver(new RepositoryURIResolver(userInfo));
      generator.setPdfImgUrl(null);
      //      for(String key : env.keySet())
      //        bsh.set(key, env.get(key));

      generator.getContents(engine, pdfOut);

    } catch (Throwable t) {
      Logger.error(userInfo.getUtilizador(), this, "service", "Ocorreu excepcao a gerar o PDF", t);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not generate PDF. Cause: "+t.getMessage());
      return;
    } finally {
      procData.returnInterpreter(engine);
      data.close();
      data = null;
    }

    response.setContentType("application/x-pdf");
    response.addHeader("Content-Disposition", "attachment;filename=detalhe.pdf");
    response.setContentLength(pdfOut.size());
    java.io.OutputStream outputStream = response.getOutputStream();
    pdfOut.writeTo(outputStream);
    outputStream.flush();
    response.flushBuffer();
    outputStream.close();
  }
}
