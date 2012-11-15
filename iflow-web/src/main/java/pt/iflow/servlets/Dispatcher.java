/**
 * Dispatcher.java
 *
 * Description:
 *
 * History: 01/15/02 - jpms - created.
 * $Id: Dispatcher.java 248 2007-08-01 13:54:31 +0000 (Qua, 01 Ago 2007) uid=mach,ou=Users,dc=iknow,dc=pt $
 */

package pt.iflow.servlets;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.exolab.castor.xml.Marshaller;

import pt.iflow.api.annotations.RepositoryWebOp;
import pt.iflow.api.connectors.ConnectorInterface;
import pt.iflow.api.connectors.ConnectorUtils;
import pt.iflow.api.connectors.DMSConnectorUtils;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.Repository;
import pt.iflow.api.core.RepositoryFile;
import pt.iflow.api.events.EventManager;
import pt.iflow.api.flows.FlowHolder;
import pt.iflow.api.flows.IFlowData;
import pt.iflow.api.presentation.DateUtility;
import pt.iflow.api.processannotation.ProcessAnnotationManager;
import pt.iflow.api.processannotation.ProcessLabel;
import pt.iflow.api.transition.FlowStateHistoryTO;
import pt.iflow.api.transition.FlowStateLogTO;
import pt.iflow.api.utils.CheckSum;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.FlowInfo;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.NameValuePair;
import pt.iflow.api.utils.RepositoryWebOpCodes;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;
import pt.iflow.api.xml.ConnectorMarshaller;
import pt.iflow.api.xml.DBTableMarshaller;
import pt.iflow.api.xml.FlowDocsMarshaller;
import pt.iflow.api.xml.ProcessStateHistoryMarshaller;
import pt.iflow.api.xml.ProcessStateLogMarshaller;
import pt.iflow.applet.AbstractAppletServletHelper;
import pt.iflow.connector.dms.ContentResult;
import pt.iflow.connector.dms.DMSUtils;
import pt.iflow.core.AccessControlManager;
import pt.iflow.core.AuthProfileBean;
import pt.iknow.utils.html.FormData;
import pt.iknow.utils.html.FormFile;
import pt.iknow.utils.html.FormUtils;

/**
 * 
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright (c) 2005 iKnow
 * </p>
 * 
 * @author iKnow
 * 
 * @web.servlet name="EditorDispatcherServlet"
 * 
 * @web.servlet-mapping url-pattern="/dispatcher"
 */

public class Dispatcher extends HttpServlet {

  private static final long serialVersionUID = -3446835831907606721L;

  private static final Map<Integer, Method> methodMapping;
  
  static {
    Map<Integer, Method> mm = new HashMap<Integer, Method>();

    Method[] methods = Dispatcher.class.getDeclaredMethods(); // include private, but excludes inherited
    
    for(Method m : methods) {
      RepositoryWebOp annotation = m.getAnnotation(RepositoryWebOp.class);
      if(null == annotation) continue; // no annotation
      mm.put(annotation.code(), m);
    }

    methodMapping = Collections.unmodifiableMap(mm);
  }
  
  
  
  private UserInfoInterface doLogin(HttpServletRequest request, HttpServletResponse response, String login, String password) {
    if(StringUtils.isEmpty(login) && StringUtils.isEmpty(password)) {
      return null;
    }
    
    if (login != null) {
      login = login.trim();
    }

    password = RepositoryWebOpCodes._crypt.decrypt(password);
    UserInfoInterface userInfo = null;
    try {
      AuthenticationServlet.authenticate(request, response, login, password, null);
      HttpSession session = request.getSession();
      userInfo = (UserInfoInterface) session.getAttribute(Const.USER_INFO);
      if(!isAdmin(userInfo)) userInfo = null;  // must be admin
    } catch (Throwable t) {
      Logger.error("ADMIN", this, "doLogin", "could not authenticate user "+login, t);
    }

    return userInfo;
  }

  private boolean isAdmin(UserInfoInterface userInfo) {
    return userInfo.isOrgAdmin() || userInfo.isSysAdmin();
  }

  public Dispatcher() {
  }

  public void init() throws ServletException {
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    response.getWriter().println("Dispatcher is online");
  }

  private void sendData(HttpServletResponse response, RepositoryFile repFile) throws IOException {
    OutputStream out = response.getOutputStream();
    response.setContentLength(repFile.getSize());
    repFile.writeToStream(out);
    out.flush();
    out.close();
  }

  private void sendData(HttpServletResponse response, byte[] data) throws IOException {
    if (null == response || null == data)
      return;
    OutputStream out = response.getOutputStream();
    response.setContentLength(data.length);
    out.write(data);
    out.flush();
    out.close();
  }

  private void sendString(HttpServletResponse response, String str) throws IOException {
    if (null == response || null == str)
      return;
    try {
      sendData(response, str.getBytes("UTF-8"));
    } catch (UnsupportedEncodingException e) {
      logMsg("sendString", e);
    }
  }

  private void sendList(HttpServletResponse response, Object[] items) throws IOException {
    if (null == response || null == items)
      return;
    StringBuffer sb = new StringBuffer();
    for (Object item : items) {
      sb.append(item).append('\n');
    }
    sendString(response, sb.toString());
  }

  private void sendCollection(HttpServletResponse response, Collection<?> stuff) throws IOException {
    if (null == response || null == stuff || stuff.size() == 0)
      return;
    StringBuffer sb = new StringBuffer();
    for (Object obj : stuff) {
      sb.append(obj).append('\n');
    }
    sendString(response, sb.toString());
  }

  private void sendFlowList(HttpServletResponse response, ArrayList<FlowInfo> flows) throws IOException {
    if (null == response || null == flows || flows.size() == 0)
      return;
    ByteArrayOutputStream input = new ByteArrayOutputStream();
    OutputStreamWriter w = new OutputStreamWriter(input, RepositoryWebOpCodes.DEFAULT_ENCODING);

    Marshaller marshaller = new Marshaller(w);
    marshaller.setEncoding(RepositoryWebOpCodes.DEFAULT_ENCODING);
    try {
      marshaller.marshal(flows);
    } catch (Exception e) {
      logMsg("sendFlowList", e);
      throw new IOException("Error marshalling data: " + e.getMessage());
    }

    sendData(response, input.toByteArray());
  }

  private void sendFlowInfo(HttpServletResponse response, FlowInfo flow) throws IOException {
    if (null == response || null == flow)
      return;
    ByteArrayOutputStream input = new ByteArrayOutputStream();
    OutputStreamWriter w = new OutputStreamWriter(input, RepositoryWebOpCodes.DEFAULT_ENCODING);

    Marshaller marshaller = new Marshaller(w);
    marshaller.setEncoding(RepositoryWebOpCodes.DEFAULT_ENCODING);
    try {
      marshaller.marshal(flow);
    } catch (Exception e) {
      logMsg("sendFlowInfo", e);
      throw new IOException("Error marshalling data: " + e.getMessage());
    }

    sendData(response, input.toByteArray());
  }

  private void sendZippedList(HttpServletResponse response, RepositoryFile [] files) throws IOException {
    if(null == files || files.length == 0) return;
    
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    ZipOutputStream zout = new ZipOutputStream(bout);
    zout.setLevel(9);
    for (RepositoryFile file : files) {
      byte [] data = file.getResouceData();
      if(null != data) {
        ZipEntry ze = new ZipEntry(file.getName());
        zout.putNextEntry(ze);
        zout.write(data);
        zout.closeEntry();
      }
    }
    zout.close();
    
    sendData(response, bout.toByteArray());
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    try {
      FormData fdFormData = FormUtils.parseRequest(request, Const.nUPLOAD_THRESHOLD_SIZE, Const.nUPLOAD_MAX_SIZE, Const.fUPLOAD_TEMP_DIR);
      FormFile formFile = null;
      String login = null;
      String password = null;
      String op = null;
      String name = null;
      String desc = null;
      String comment = null;
      int opCode = RepositoryWebOpCodes.INVALID_REQUEST;

      byte[] buffer = null;

      login = fdFormData.getParameter("login");
      password = fdFormData.getParameter("password");
      op = fdFormData.getParameter("op");
      name = fdFormData.getParameter("name");
      desc = fdFormData.getParameter("desc");
      comment = fdFormData.getParameter("comment");
      formFile = fdFormData.getFileParameter("file");

      if (null != formFile) {
        // submitted file name is ignored. name parameter used instead.
        buffer = formFile.getData();
      }

      try {
        opCode = Integer.parseInt(op);
      } catch (Throwable t) {
        opCode = RepositoryWebOpCodes.INVALID_REQUEST;
        logMsg("doPost", t);
      }

      // logMsg(userInfo, "Username: "+login+" Password: "+password+" OP: "+op+" value: "+value);
      Repository rep = BeanFactory.getRepBean();
      HttpSession session = request.getSession();
      UserInfoInterface userInfo = (UserInfoInterface) session.getAttribute(Const.USER_INFO);

      // First check operations that do not require a valid login
      if (opCode == RepositoryWebOpCodes.CHECK_CONNECTION) {
        boolean bOk = rep.checkConnection();

        String data = String.valueOf(bOk);

        sendString(response, data);
        return;
      } else if (opCode == RepositoryWebOpCodes.LOGOUT) {
        session.invalidate();
        return;
      } else if (opCode == RepositoryWebOpCodes.AUTHENTICATE_USER) {
        userInfo = doLogin(request, response, login, password);

        String data = String.valueOf(null != userInfo && userInfo.isLogged());
        sendString(response, data);
        return;
      }

      if (null == userInfo) { // Try and authenticate the user just in case the session has expired
        userInfo = doLogin(request, response, login, password);
      }

      if (null != userInfo && userInfo.isLogged()) {
        // Dynamic options
        Method m = methodMapping.get(opCode);
        if(null != m) {
          m.invoke(this, userInfo, response, rep, name, desc, buffer, comment);
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
      logMsg("doPost", e);
    }
  }

  private static void logMsg(UserInfoInterface userInfo, String msg) {
    Logger.debug(userInfo.getUtilizador(), "Dispatcher", msg, msg);
  }
  
  private static void logMsg(String msg, Throwable t) {
    Logger.error(null, "Dispatcher", msg, msg, t);
  }
  
  
  //
  // Anotated methods:
  // 1. Create a method with the following signature:
  // void funcion (UserInfoInterface userInfo, HttpServletResponse response, Repository rep, String name, String desc, byte[] buffer, String comment) throws Exception;
  //
  // 2. Annotate with @RepositoryWebOp(code=<Operation Code>)
  // 
  // 3. Works!
  //
  
  
  // 1. Listers

  @RepositoryWebOp(code=RepositoryWebOpCodes.LIST_PROFILES)
  void listProfiles(UserInfoInterface userInfo, HttpServletResponse response, Repository rep, String name, String desc, byte[] buffer, String comment) throws Exception {

    Collection<?> profiles = AuthProfileBean.getInstance().getAllProfiles(userInfo.getOrganization());
    sendCollection(response, profiles);
    logMsg(userInfo, "listProfiles");
  }

  @RepositoryWebOp(code=RepositoryWebOpCodes.LIST_EXTRA_PROPERTIES)
  void listExtraProperties(UserInfoInterface userInfo, HttpServletResponse response, Repository rep, String name, String desc, byte[] buffer, String comment) throws Exception {

    sendCollection(response, AccessControlManager.getUserDataAccess().getListExtraProperties());
    userInfo.getUserData().getUsername();
    logMsg(userInfo, "listExtraProperties");
  }

  @RepositoryWebOp(code=RepositoryWebOpCodes.LIST_STYLESHEETS)
  void listStyleSheets(UserInfoInterface userInfo, HttpServletResponse response, Repository rep, String name, String desc, byte[] buffer, String comment) throws Exception {
    sendList(response, rep.listStyleSheets(userInfo));
    logMsg(userInfo, "listStyleSheets");
  }

  @RepositoryWebOp(code=RepositoryWebOpCodes.LIST_EMAILS)
  void listEmailTemplates(UserInfoInterface userInfo, HttpServletResponse response, Repository rep, String name, String desc, byte[] buffer, String comment) throws Exception {
    sendList(response, rep.listEmailTemplates(userInfo));
    logMsg(userInfo, "listEmailTemplates");
  }

  @RepositoryWebOp(code=RepositoryWebOpCodes.LIST_PRINTS)
  void listPrintTemplates(UserInfoInterface userInfo, HttpServletResponse response, Repository rep, String name, String desc, byte[] buffer, String comment) throws Exception {
    sendList(response, rep.listPrintTemplates(userInfo));
    logMsg(userInfo, "listPrintTemplates");
  }

  @RepositoryWebOp(code=RepositoryWebOpCodes.LIST_CHARTS)
  void listCharts(UserInfoInterface userInfo, HttpServletResponse response, Repository rep, String name, String desc, byte[] buffer, String comment) throws Exception {
    sendList(response, rep.listChartFiles(userInfo));
    logMsg(userInfo, "listChartFiles");
  }

  @RepositoryWebOp(code=RepositoryWebOpCodes.LIST_FLOWS)
  void listFlows(UserInfoInterface userInfo, HttpServletResponse response, Repository rep, String name, String desc, byte[] buffer, String comment) throws Exception {
    sendList(response, BeanFactory.getFlowHolderBean().listFlowNames(userInfo));
    logMsg(userInfo, "listFlows");
  }

  @RepositoryWebOp(code=RepositoryWebOpCodes.LIST_SUB_FLOWS)
  void listSubFlows(UserInfoInterface userInfo, HttpServletResponse response, Repository rep, String name, String desc, byte[] buffer, String comment) throws Exception {
    sendList(response, BeanFactory.getFlowHolderBean().listSubFlows(userInfo));
    logMsg(userInfo, "listSubFlows");
  }

  @RepositoryWebOp(code=RepositoryWebOpCodes.LIST_LIBRARIES)
  void listLibraries(UserInfoInterface userInfo, HttpServletResponse response, Repository rep, String name, String desc, byte[] buffer, String comment) throws Exception {
    sendList(response, rep.listLibraries(userInfo));
    logMsg(userInfo, "listLibraries");
  }

  @RepositoryWebOp(code=RepositoryWebOpCodes.LIST_WEBSERVICES)
  void listWebServices(UserInfoInterface userInfo, HttpServletResponse response, Repository rep, String name, String desc, byte[] buffer, String comment) throws Exception {
    sendList(response, rep.listWebServices(userInfo));
    logMsg(userInfo, "listWebServices");
  }

  @RepositoryWebOp(code=RepositoryWebOpCodes.LIST_EVENTS)
  void listEvents(UserInfoInterface userInfo, HttpServletResponse response, Repository rep, String name, String desc, byte[] buffer, String comment) throws Exception {
    sendList(response, EventManager.get().listEvents(userInfo));
    logMsg(userInfo, "listEvents");
  }

  @RepositoryWebOp(code=RepositoryWebOpCodes.LIST_PROCESS_TASK_ANNOTATION_LABELS)
  void listProcessTaskAnnotationLabels(UserInfoInterface userInfo, HttpServletResponse response, Repository rep, String name, String desc, byte[] buffer, String comment) throws Exception {
    List<String> returnLabels = new ArrayList<String>();
    try {
    ProcessAnnotationManager processAnnotationManager = BeanFactory.getProcessAnnotationManagerBean();
    List<ProcessLabel> dbLabels = processAnnotationManager.getLabelListToIflowEditor();

    if (dbLabels != null){
      for (ProcessLabel label:dbLabels){
        returnLabels.add(label.getName()+" - " + label.getDescription());
      }
    }
    } catch (Exception e) {
      log("listProcessTaskAnnotationLabels error", e);
    }

    sendList(response, returnLabels.toArray(new String[returnLabels.size()]));
    logMsg(userInfo, "listProcessTaskAnnotationLabels");
  }

  // 2. Getters

  @RepositoryWebOp(code=RepositoryWebOpCodes.GET_FLOW)
  void getFlow(UserInfoInterface userInfo, HttpServletResponse response, Repository rep, String name, String desc, byte[] buffer, String comment) throws Exception {
    sendData(response, BeanFactory.getFlowHolderBean().readFlowData(userInfo, name));
    logMsg(userInfo, "getFlow");
  }

  @RepositoryWebOp(code=RepositoryWebOpCodes.GET_SUBFLOW)
  void getSubFlow(UserInfoInterface userInfo, HttpServletResponse response, Repository rep, String name, String desc, byte[] buffer, String comment) throws Exception {
    sendData(response, BeanFactory.getFlowHolderBean().readSubFlowData(userInfo, name));
    logMsg(userInfo, "getSubFlow");
  }

  @RepositoryWebOp(code=RepositoryWebOpCodes.GET_LIBRARY)
  void getLibrary(UserInfoInterface userInfo, HttpServletResponse response, Repository rep, String name, String desc, byte[] buffer, String comment) throws Exception {
    sendData(response, rep.getLibrary(userInfo, name));
    logMsg(userInfo, "getLibrary");
  }

  @RepositoryWebOp(code=RepositoryWebOpCodes.GET_WEBSERVICE)
  void getWebservice(UserInfoInterface userInfo, HttpServletResponse response, Repository rep, String name, String desc, byte[] buffer, String comment) throws Exception {
    sendData(response, rep.getWebService(userInfo, name));
    logMsg(userInfo, "getWebService");
  }

  @RepositoryWebOp(code=RepositoryWebOpCodes.GET_EVENT)
  void getEvent(UserInfoInterface userInfo, HttpServletResponse response, Repository rep, String name, String desc, byte[] buffer, String comment) throws Exception {
    sendString(response, EventManager.get().getEventDescription(userInfo, name));
    logMsg(userInfo, "getEvent");
  }

  @RepositoryWebOp(code=RepositoryWebOpCodes.GET_ICON)
  void getIcon(UserInfoInterface userInfo, HttpServletResponse response, Repository rep, String name, String desc, byte[] buffer, String comment) throws Exception {
    sendData(response, rep.getIcon(userInfo, name));
    logMsg(userInfo, "getIcon");
  }

  @RepositoryWebOp(code=RepositoryWebOpCodes.GET_CLASS)
  void getClass(UserInfoInterface userInfo, HttpServletResponse response, Repository rep, String name, String desc, byte[] buffer, String comment) throws Exception {
    sendData(response, getClassFile(userInfo, rep, name));
    logMsg(userInfo, "getClass");
  }

  @RepositoryWebOp(code=RepositoryWebOpCodes.GET_FLOW_STATE_HISTORY)
  void getFlowState(UserInfoInterface userInfo, HttpServletResponse response, Repository rep, String name, String desc, byte[] buffer, String comment) throws Exception {
    int flowid = -1;
    String pnumber = null;
    try {
      flowid = Integer.parseInt(name.split(";")[0]);
    } catch (NumberFormatException ex) {
      Logger.error(userInfo.getUtilizador(), this, "getFlowState", "Unable to convert to number (value=" + name.split(";")[0] + ")", ex);
    }
    pnumber = name.split(";")[1];
    List<FlowStateHistoryTO> flowStateHistory = BeanFactory.getProcessManagerBean().getFullProcessHistory(userInfo, flowid, pnumber);
    byte[] data = ProcessStateHistoryMarshaller.marshall(flowStateHistory);
    sendData(response, data);
    logMsg(userInfo, "getFlowState");
  }

  @RepositoryWebOp(code=RepositoryWebOpCodes.GET_FLOW_STATE_LOGS)
  void getFlowStateLogs(UserInfoInterface userInfo, HttpServletResponse response, Repository rep, String name, String desc, byte[] buffer, String comment) throws Exception {
    String[] params = name.split(";");
    int flowid = convertToNumber(userInfo, params, 0);
    String pnumber = params[1];
    int subpid = convertToNumber(userInfo, params, 2);
    int state = convertToNumber(userInfo, params, 3);
    List<FlowStateLogTO> flowStateLog = BeanFactory.getProcessManagerBean().getFlowStateLogs(userInfo, flowid, pnumber, subpid, state);
    byte[] data = ProcessStateLogMarshaller.marshall(flowStateLog);
    sendData(response, data);
    logMsg(userInfo, "getFlowStateLogs");
  }
  
  private int convertToNumber(UserInfoInterface userInfo, String[] number, int index) {
    int ret = -1;
    try {
      ret = Integer.parseInt(number[index]);
    } catch (NumberFormatException ex) {
      Logger.error(userInfo.getUtilizador(), this, "convertToNumber", "Unable to convert to number (value=" + number[index] + ")", ex);
    } catch (IndexOutOfBoundsException ex) {
      Logger.error(userInfo.getUtilizador(), this, "convertToNumber", "Supplied index larger than given array (index=" + index + "; array length="
          + number.length + ")", ex);
    }
    return ret;
  }

  // 3. Setters

  @RepositoryWebOp(code=RepositoryWebOpCodes.SET_FLOW)
  void setFlow(UserInfoInterface userInfo, HttpServletResponse response, Repository rep, String name, String desc, byte[] buffer, String comment) throws Exception {
    if (null == name || null == desc || null == buffer || buffer.length == 0)
      return; // sacana do editor

    int result = BeanFactory.getFlowHolderBean().writeFlowData(userInfo, name, desc, buffer);
    sendString(response, String.valueOf(result));
    logMsg(userInfo, "setFlow");
  }

  @RepositoryWebOp(code=RepositoryWebOpCodes.SET_SUBFLOW)
  void setSubFlow(UserInfoInterface userInfo, HttpServletResponse response, Repository rep, String name, String desc, byte[] buffer, String comment) throws Exception {
    if (null == name || null == desc || null == buffer || buffer.length == 0)
      return; // sacana do editor

    int result = BeanFactory.getFlowHolderBean().writeSubFlowData(userInfo, name, desc, buffer);
    sendString(response, String.valueOf(result));
    logMsg(userInfo, "setSubFlow");
  }

  @RepositoryWebOp(code=RepositoryWebOpCodes.SET_WEBSERVICE)
  void setWebService(UserInfoInterface userInfo, HttpServletResponse response, Repository rep, String name, String desc, byte[] buffer, String comment) throws Exception {
    if (null == name || null == desc || null == buffer || buffer.length == 0)
      return; // sacana do editor

    rep.setWebService(userInfo, name, buffer);
    logMsg(userInfo, "setWebService");
  }

  // Extended API
  @RepositoryWebOp(code=RepositoryWebOpCodes.HAS_EXTENDED_API)
  void hasExtendedAPI(UserInfoInterface userInfo, HttpServletResponse response, Repository rep, String name, String desc, byte[] buffer, String comment) throws Exception {
    sendString(response, "true");
    logMsg(userInfo, "hasExtendedAPI");
  }

  @RepositoryWebOp(code=RepositoryWebOpCodes.GET_FLOW_INFO)
  void getFlowInfo(UserInfoInterface userInfo, HttpServletResponse response, Repository rep, String name, String desc, byte[] buffer, String comment) throws Exception {
    IFlowData flowData = BeanFactory.getFlowHolderBean().getFlow(userInfo, name);
    
    if (null != flowData) {
      String created = DateUtility.formatTimestamp(userInfo, new Date(flowData.getCreated()));
      String modif = DateUtility.formatTimestamp(userInfo, new Date(flowData.getLastModified()));
      FlowInfo finfo = new FlowInfo(flowData.getId(), flowData.getName(), flowData.getFileName(), flowData.isOnline(), created, modif, flowData.getMaxBlockId());
      sendFlowInfo(response, finfo);
    }
    logMsg(userInfo, "getFlowInfo");
  }

  @RepositoryWebOp(code=RepositoryWebOpCodes.LIST_FLOWS_EXTENDED)
  void listFlowsExtended(UserInfoInterface userInfo, HttpServletResponse response, Repository rep, String name, String desc, byte[] buffer, String comment) throws Exception {
    FlowHolder fh = BeanFactory.getFlowHolderBean();
    ArrayList<FlowInfo> flows = new ArrayList<FlowInfo>();
    IFlowData[] data = fh.listFlows(userInfo);
    for (IFlowData flowData : data) {
      String created = DateUtility.formatTimestamp(userInfo, new Date(flowData.getCreated()));
      String modif = DateUtility.formatTimestamp(userInfo, new Date(flowData.getLastModified()));
      FlowInfo finfo = new FlowInfo(flowData.getId(), flowData.getName(), flowData.getFileName(), flowData.isOnline(), created, modif);
      flows.add(finfo);
    }
    sendFlowList(response, flows);
    logMsg(userInfo, "listFlows");
  }
  
  @RepositoryWebOp(code=RepositoryWebOpCodes.GET_ZIPPED_ICONS)
  void getZippedIcons(UserInfoInterface userInfo, HttpServletResponse response, Repository rep, String name, String desc, byte[] buffer, String comment) throws Exception {
    RepositoryFile [] files = rep.listIcons(userInfo);
    sendZippedList(response, files);
    logMsg(userInfo, "getZippedIcons");
  }

  @RepositoryWebOp(code=RepositoryWebOpCodes.GET_ZIPPED_LIBRARIES)
  void getZippedLibraries(UserInfoInterface userInfo, HttpServletResponse response, Repository rep, String name, String desc, byte[] buffer, String comment) throws Exception {
    RepositoryFile [] files = rep.listLibraries(userInfo);
    sendZippedList(response, files);
    logMsg(userInfo, "getZippedLibraries");
  }

  
  @RepositoryWebOp(code=RepositoryWebOpCodes.GET_CLASS_JAR)
  void getZippedClasses(UserInfoInterface userInfo, HttpServletResponse response, Repository rep, String name, String desc, byte[] buffer, String comment) throws Exception {
    logMsg(userInfo, "getZippedClasses Not Implemented");
  }
  
  
  @RepositoryWebOp(code=RepositoryWebOpCodes.GET_LAST_MODIFIED_ICONS)
  void getLastModifiedIcons(UserInfoInterface userInfo, HttpServletResponse response, Repository rep, String name, String desc, byte[] buffer, String comment) throws Exception {
    RepositoryFile [] files = rep.listIcons(userInfo);
    long lmod = 0L;
    for (RepositoryFile repositoryFile : files) {
      long l = repositoryFile.getLastModified();
      if(l > lmod) lmod = l;
    }
    sendString(response, String.valueOf(lmod));
    logMsg(userInfo, "getLastModifiedIcons");
  }

  @RepositoryWebOp(code=RepositoryWebOpCodes.GET_LAST_MODIFIED_LIBRARIES)
  void getLastModifiedLibraries(UserInfoInterface userInfo, HttpServletResponse response, Repository rep, String name, String desc, byte[] buffer, String comment) throws Exception {
    RepositoryFile [] files = rep.listLibraries(userInfo);
    long lmod = 0L;
    for (RepositoryFile repositoryFile : files) {
      long l = repositoryFile.getLastModified();
      if(l > lmod) lmod = l;
    }
    sendString(response, String.valueOf(lmod));
    logMsg(userInfo, "getLastModifiedLibraries");
  }
  
  @RepositoryWebOp(code=RepositoryWebOpCodes.GET_USER_LOCALE)
  void getUserLocale(UserInfoInterface userInfo, HttpServletResponse response, Repository rep, String name, String desc, byte[] buffer, String comment) throws Exception {
    Locale loc = userInfo.getUserSettings().getLocale();
    String lang = loc.getLanguage()+"_"+loc.getCountry();
    sendString(response, lang);
    logMsg(userInfo, "getUserLocale");
  }
  
  // Hashing

  @RepositoryWebOp(code=RepositoryWebOpCodes.HASH_FLOW)
  void getFlowHash(UserInfoInterface userInfo, HttpServletResponse response, Repository rep, String name, String desc, byte[] buffer, String comment) throws Exception {
    IFlowData fd = BeanFactory.getFlowHolderBean().getFlow(userInfo, name);
    sendString(response, String.valueOf(fd.getLastModified()));
    logMsg(userInfo, "getFlowHash");
  }
  
  @RepositoryWebOp(code=RepositoryWebOpCodes.HASH_SUBFLOW)
  void getSubFlowHash(UserInfoInterface userInfo, HttpServletResponse response, Repository rep, String name, String desc, byte[] buffer, String comment) throws Exception {
    logMsg(userInfo, "getSubFlowHash Not Implemented");
  }
  
  @RepositoryWebOp(code=RepositoryWebOpCodes.HASH_LIBRARY)
  void getLibraryHash(UserInfoInterface userInfo, HttpServletResponse response, Repository rep, String name, String desc, byte[] buffer, String comment) throws Exception {
    RepositoryFile file = rep.getLibrary(userInfo, name);
    sendString(response, String.valueOf(file.getLastModified()));
    logMsg(userInfo, "getLibraryHash");
  }
  
  @RepositoryWebOp(code=RepositoryWebOpCodes.HASH_WEBSERVICE)
  void getWebServiceHash(UserInfoInterface userInfo, HttpServletResponse response, Repository rep, String name, String desc, byte[] buffer, String comment) throws Exception {
    RepositoryFile file = rep.getWebService(userInfo, name);
    sendString(response, String.valueOf(file.getLastModified()));
    logMsg(userInfo, "getWebServiceHash");
  }
  
  @RepositoryWebOp(code=RepositoryWebOpCodes.HASH_ICON)
  void getIconHash(UserInfoInterface userInfo, HttpServletResponse response, Repository rep, String name, String desc, byte[] buffer, String comment) throws Exception {
    RepositoryFile file = rep.getIcon(userInfo, name);
    sendString(response, String.valueOf(file.getLastModified()));
    logMsg(userInfo, "getIconHash");
  }
  
  @RepositoryWebOp(code=RepositoryWebOpCodes.HASH_CLASS)
  void getClassHash(UserInfoInterface userInfo, HttpServletResponse response, Repository rep, String name, String desc, byte[] buffer, String comment) throws Exception {
    RepositoryFile file = getClassFile(userInfo, rep, name);
    sendString(response, CheckSum.digest(file));
    logMsg(userInfo, "getClassHash");
  }
  

  @RepositoryWebOp(code=RepositoryWebOpCodes.GET_ICONS_HASH)
  void getIconsHash(UserInfoInterface userInfo, HttpServletResponse response, Repository rep, String name, String desc, byte[] buffer, String comment) throws Exception {
    RepositoryFile [] files = rep.listIcons(userInfo);
    String digest = CheckSum.digest(files);
    sendString(response, digest);
    logMsg(userInfo, "getIconsHash");
  }

  @RepositoryWebOp(code=RepositoryWebOpCodes.GET_LIBRARIES_HASH)
  void getLibrariesHash(UserInfoInterface userInfo, HttpServletResponse response, Repository rep, String name, String desc, byte[] buffer, String comment) throws Exception {
    RepositoryFile [] files = rep.listLibraries(userInfo);
    String digest = CheckSum.digest(files);
    sendString(response, digest);
    logMsg(userInfo, "getLibrariesHash");
  }
  
  @RepositoryWebOp(code=RepositoryWebOpCodes.GET_MODIFIED_CLASS_HASH)
  void getModifiedClassHash(UserInfoInterface userInfo, HttpServletResponse response, Repository rep, String name, String desc, byte[] buffer, String comment) throws Exception {
    byte[] modified = computeModifiedClasses(userInfo, rep, buffer);
    sendData(response, modified);
    logMsg(userInfo, "getModifiedClassHash");
  }
  

// versioning
  @RepositoryWebOp(code=RepositoryWebOpCodes.LIST_FLOW_VERSIONS)
  void listFlowVersions(UserInfoInterface userInfo, HttpServletResponse response, Repository rep, String name, String desc, byte[] buffer, String comment) throws Exception {
    sendList(response, BeanFactory.getFlowHolderBean().getFlowVersions(userInfo, name));
    logMsg(userInfo, "listFlowVersions");
  }

  @RepositoryWebOp(code=RepositoryWebOpCodes.LIST_SUB_FLOW_VERSIONS)
  void listSubFlowVersions(UserInfoInterface userInfo, HttpServletResponse response, Repository rep, String name, String desc, byte[] buffer, String comment) throws Exception {
    sendList(response, BeanFactory.getFlowHolderBean().getSubFlowVersions(userInfo, name));
    logMsg(userInfo, "listSubFlowVersions");
  }

  @RepositoryWebOp(code=RepositoryWebOpCodes.GET_FLOW_VERSION_COMMENT)
  void getFlowVersionComment(UserInfoInterface userInfo, HttpServletResponse response, Repository rep, String name, String desc, byte[] buffer, String comment) throws Exception {
    int pos = name.indexOf(';');
    String flowid = name.substring(pos+1);
    int version = Integer.parseInt(name.substring(0, pos));
    sendString(response, BeanFactory.getFlowHolderBean().getFlowComment(userInfo, flowid, version));
    logMsg(userInfo, "getFlowVersionComment");
  }

  @RepositoryWebOp(code=RepositoryWebOpCodes.GET_SUB_FLOW_VERSION_COMMENT)
  void getSubFlowVersionComment(UserInfoInterface userInfo, HttpServletResponse response, Repository rep, String name, String desc, byte[] buffer, String comment) throws Exception {
    int pos = name.indexOf(';');
    String flowid = name.substring(pos+1);
    int version = Integer.parseInt(name.substring(0, pos));
    sendString(response, BeanFactory.getFlowHolderBean().getSubFlowComment(userInfo, flowid, version));
    logMsg(userInfo, "getSubFlowVersionComment");
  }

  @RepositoryWebOp(code=RepositoryWebOpCodes.GET_FLOW_VERSION)
  void getFlowVersion(UserInfoInterface userInfo, HttpServletResponse response, Repository rep, String name, String desc, byte[] buffer, String comment) throws Exception {
    int pos = name.indexOf(';');
    String flowid = name.substring(pos+1);
    int version = Integer.parseInt(name.substring(0, pos));
    sendData(response, BeanFactory.getFlowHolderBean().readFlowData(userInfo, flowid, version));
    logMsg(userInfo, "getFlowVersion");
  }

  @RepositoryWebOp(code=RepositoryWebOpCodes.GET_SUB_FLOW_VERSION)
  void getSubFlowVersion(UserInfoInterface userInfo, HttpServletResponse response, Repository rep, String name, String desc, byte[] buffer, String comment) throws Exception {
    int pos = name.indexOf(';');
    String flowid = name.substring(pos+1);
    int version = Integer.parseInt(name.substring(0, pos));
    sendData(response, BeanFactory.getFlowHolderBean().readSubFlowData(userInfo, flowid, version));
    logMsg(userInfo, "getSubFlowVersion");
  }

  @RepositoryWebOp(code=RepositoryWebOpCodes.SET_FLOW_VERSION)
  void setFlowVersioned(UserInfoInterface userInfo, HttpServletResponse response, Repository rep, String name, String desc,
      byte[] buffer, String comment) throws Exception {
    if (null == name || null == desc || null == buffer || buffer.length == 0)
      return; // sacana do editor

    int result = BeanFactory.getFlowHolderBean().writeFlowData(userInfo, name, desc, buffer, comment);
    sendString(response, String.valueOf(result));
    logMsg(userInfo, "setFlow");
  }

  @RepositoryWebOp(code=RepositoryWebOpCodes.SET_SUB_FLOW_VERSION)
  void setSubFlowVersioned(UserInfoInterface userInfo, HttpServletResponse response, Repository rep, String name, String desc,
      byte[] buffer, String comment) throws Exception {
    if (null == name || null == desc || null == buffer || buffer.length == 0)
      return; // sacana do editor

    int result = BeanFactory.getFlowHolderBean().writeSubFlowData(userInfo, name, desc, buffer, comment);
    sendString(response, String.valueOf(result));
    logMsg(userInfo, "setSubFlow");
  }


  @RepositoryWebOp(code=RepositoryWebOpCodes.LIST_DATA_SOURCES)
  void listDataSources(UserInfoInterface userInfo, HttpServletResponse response, Repository rep, String name, String desc, byte[] buffer, String comment) throws Exception {
    sendList(response, Utils.getDataSources(userInfo));
    logMsg(userInfo, "listDataSources");
  }

  @RepositoryWebOp(code=RepositoryWebOpCodes.GET_CONNECTORS)
  void getConnectors(UserInfoInterface userInfo, HttpServletResponse response, Repository rep, String name, String desc, byte[] buffer, String comment) throws Exception {
    NameValuePair<String, Class<ConnectorInterface>>[] connectors = ConnectorUtils.fetchConnectors(userInfo);
    byte[] data = ConnectorMarshaller.marshall(connectors);
    sendData(response, data);
    logMsg(userInfo, "listConnectors");
  }

  @RepositoryWebOp(code=RepositoryWebOpCodes.FLOW_DOC_GET_FILES)
  void flowDocsGetFiles(UserInfoInterface userInfo, HttpServletResponse response, Repository rep, String name, String desc, byte[] buffer, String comment) throws Exception {
    String[] params = name.split(";", -1);
    String searchValue = params[0];
    String path = params[1];
    ContentResult content = DMSUtils.getInstance().getDirectDescendents(DMSConnectorUtils.createCredential(userInfo), searchValue, path);
    byte[] data = FlowDocsMarshaller.marshallContentResult(content);
    sendData(response, data);
    logMsg(userInfo, "flowDocsGetFiles");
  }


  @RepositoryWebOp(code=RepositoryWebOpCodes.LIST_SIGNATURE_TYPES)
  void listSignatureTypes(UserInfoInterface userInfo, HttpServletResponse response, Repository rep, String name, String desc, byte[] buffer, String comment) throws Exception {
    sendList(response, AbstractAppletServletHelper.getFeatures());
    logMsg(userInfo, "signatureTypes");
  }
  
  
  @RepositoryWebOp(code=RepositoryWebOpCodes.GET_I18N_MESSAGES)
  void getMessages(UserInfoInterface userInfo, HttpServletResponse response, Repository rep, String name, String desc, byte[] buffer, String comment) throws Exception {
    RepositoryFile f = rep.getMessagesFile(userInfo, name);
    sendData(response, f);
    logMsg(userInfo, "getMessages");
  }
  
  @RepositoryWebOp(code=RepositoryWebOpCodes.GET_MODIFIED_I18N_HASH)
  void getModifiedMessageHash(UserInfoInterface userInfo, HttpServletResponse response, Repository rep, String name, String desc, byte[] buffer, String comment) throws Exception {
    byte[] modified = computeModifiedMessages(userInfo, rep, buffer);
    sendData(response, modified);
    logMsg(userInfo, "getModifiedClassHash");
  }
  
  @RepositoryWebOp(code=RepositoryWebOpCodes.DB_TABLE_DESC)
  void getTableDesc(UserInfoInterface userInfo, HttpServletResponse response, Repository rep, String name, String desc, byte[] buffer, String comment) throws Exception {
    String[] params = name.split(";", -1);
    String jndi = params[0];
    String table = params[1];
    byte[] data = DBTableMarshaller.marshall(BeanFactory.getDatabaseManagerBean().getTableDescription(jndi, table));
    sendData(response, data);
    logMsg(userInfo, "getTableDesc");
  }
  
  private RepositoryFile getClassFile(UserInfoInterface userInfo, Repository rep, String name) {
    RepositoryFile classFile = rep.getClassFile(userInfo.getOrganization(), name);
    if(null == classFile || !classFile.exists()) {
      classFile = getClasspathFile(name);
    }
    return classFile;
  }
  
  private static RepositoryFile getClasspathFile(final String name) {
    RepositoryFile result = null;
    InputStream in = null;
    try {
      URL url = Dispatcher.class.getClassLoader().getResource(name);
      if(null == url) return null;
      URLConnection conn = url.openConnection();
      
      in = conn.getInputStream();
      long lmod = conn.getLastModified();
      int r;
      byte [] b = new byte[8192];
      
      ByteArrayOutputStream bout = new ByteArrayOutputStream(16384);
      
      while((r = in.read(b))>= 0) {
        bout.write(b, 0, r);
      }
      
      result = new ClasspathRepositoryFile(name, bout.toByteArray(), lmod);
      bout = null;
      
    } catch (Exception e) {
      
    } finally {
      if(in != null) {
        try {
          in.close();
        } catch (IOException e) {
        }
      }
    }
    
    return result;
  }
  
  private byte [] computeModifiedClasses(UserInfoInterface userInfo, Repository rep, byte [] data) {
    if(null == data || data.length == 0) return new byte[0];
    
    CheckSum [] sums = CheckSum.unmarshall(data);
    if(null == sums || sums.length == 0) return new byte[0];
    
    ArrayList<CheckSum> badSums = new ArrayList<CheckSum>();
    
    for(CheckSum sum : sums) {
      RepositoryFile file = getClassFile(userInfo, rep, sum.getFile());
      String hash;
      if(null == file) {
        hash = "xxx"; // removed. just create a dummy hash value so hash doesn't match
      } else {
        hash = CheckSum.digest(file);
        if(null == hash)
          hash = "xxx"; // mark as removed
      }
      
      if(!hash.equals(sum.getChecksum())) {
        sum.setChecksum(hash);
        badSums.add(sum);
      }
    }
    
    return CheckSum.marshall(badSums);
  }
  
  private byte [] computeModifiedMessages(UserInfoInterface userInfo, Repository rep, byte [] data) {
    if(null == data || data.length == 0) return new byte[0];
    
    CheckSum [] sums = CheckSum.unmarshall(data);
    if(null == sums || sums.length == 0) return new byte[0];
    
    ArrayList<CheckSum> badSums = new ArrayList<CheckSum>();
    
    for(CheckSum sum : sums) {
      RepositoryFile file = rep.getMessagesFile(userInfo, sum.getFile());
      String hash;
      if(null == file) {
        hash = "xxx"; // removed. just create a dummy hash value so hash doesn't match
      } else {
        hash = CheckSum.digest(file);
        if(null == hash)
          hash = "xxx"; // mark as removed
      }
      
      if(!hash.equals(sum.getChecksum())) {
        sum.setChecksum(hash);
        badSums.add(sum);
      }
    }
    
    return CheckSum.marshall(badSums);
  }
  
  private static class ClasspathRepositoryFile implements RepositoryFile {
    
    private String name;
    private byte [] data;
    private long lmod;
    
    ClasspathRepositoryFile(String name, byte [] data, long lmod) {
      this.name = new File(name).getName();
      this.data = data;
      this.lmod = lmod;
    }
    
    public void writeToStream(OutputStream outStream) {
      try {
        outStream.write(data);
      } catch (IOException e) {
        Logger.warning(null, "ClasspathRepositoryFile", "writeToStream", "Error writing data to stream");
      }
    }
    
    public boolean isSystem() {
      return true;
    }
    
    public boolean isOrganization() {
      return false;
    }
    
    public URL getURL() {
      return null;
    }
    
    public int getSize() {
      return null == data ? 0 : data.length;
    }
    
    public InputStream getResourceAsStream() {
      return new ByteArrayInputStream(data);
    }
    
    public byte[] getResouceData() {
      return data;
    }
    
    public String getName() {
      return name;
    }
    
    public long getLastModified() {
      return lmod;
    }
    
    public boolean exists() {
      return getSize()>0;
    }
    
    public String toString() {
      return getName();
    }
  }
  

}
