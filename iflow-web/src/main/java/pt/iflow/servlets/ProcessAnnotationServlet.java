package pt.iflow.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.processannotation.ProcessAnnotationManager;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.applet.StringUtils;

/**
 * Servlet implementation class for Servlet: HelpDialogServlet
 * 
 * @web.servlet name="HelpDialog"
 * 
 * @web.servlet-mapping url-pattern="/HelpDialog"
 */
public class ProcessAnnotationServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
  static final long serialVersionUID = 1L;

  // jcosta:20180811 vulnerability Race Condition: Singleton Member Field
  // created private class ProcessAnnotationDataStore
  // 20180803-1
  
  private static final String METHOD_UPDATE_ANNOTATIONS = "updateAnnotations";

  private static final String PARAM_FOWLID = "flowid";
  private static final String PARAM_PID = "pid";
  private static final String PARAM_SUBPID = "subpid";
  private static final String PARAM_DEADLINE = "deadline";
  private static final String PARAM_COMMENT = "comment";
  private static final String PARAM_ADD_LABELS = "addLabels";
  private static final String PARAM_REMOVE_LABELS = "removeLabels";
  private static final String PARAM_SAVE_HISTORY = "saveHistory";
  private static final String PARAM_FORWARD_TO_LABEL_ID = "forwardToLabelId";

  public ProcessAnnotationServlet() {
    super();
  }

  protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    response.setCharacterEncoding("UTF-8");
    response.setContentType("text/html; charset=UTF-8");
    PrintWriter out = response.getWriter();

    HttpSession session = request.getSession();
    UserInfoInterface userInfo = (UserInfoInterface) session.getAttribute(Const.USER_INFO);
    if(userInfo == null) {
      out.print("session-expired");
      return;
    }

    ProcessAnnotationDataStore paDataStore = getProcessData(userInfo, request);
    
    // find what to do
    String method = request.getPathInfo();
    if (method == null) method = "";
    else if (method.indexOf("/") == 0) method = method.substring(1);
    
    // execute
    if (METHOD_UPDATE_ANNOTATIONS.equals(method)) {
      String sDeadline = request.getParameter(PARAM_DEADLINE);
      //Date deadline = (StringUtilities.isEmpty(sDeadline) ? null : Utils.string2date(sDeadline,"dd/MM/yyyy"));     
      String sComment = request.getParameter(PARAM_COMMENT);

      String[] addLabels = getParamListString(request, PARAM_ADD_LABELS);
      String[] removeLabels = getParamListString(request, PARAM_REMOVE_LABELS);

      String forwardToLabelId = request.getParameter(PARAM_FORWARD_TO_LABEL_ID);

      boolean saveHistory = false;
      try {
        saveHistory = Boolean.valueOf(request.getParameter(PARAM_SAVE_HISTORY));
      } catch (Exception e) {} 
      
      out.print(updateAnnotations(userInfo, paDataStore, sDeadline, sComment, addLabels, removeLabels, saveHistory, forwardToLabelId));
    }
  }    
  
  private String updateAnnotations(UserInfoInterface userInfo, ProcessAnnotationDataStore paDataStore, String sDeadline, String sComment, String[] addLabels, String[] removeLabels, boolean saveHistory, String forwardToLabelId) {
    ProcessAnnotationManager pam = BeanFactory.getProcessAnnotationManagerBean();
    
    
    
    if(sDeadline.equals("remove"))
    	pam.addDeadline(userInfo, paDataStore.getFlowid(), paDataStore.getPid(), paDataStore.getSubpid(), "");
    else if(!sDeadline.equals("nochange"))
    	pam.addDeadline(userInfo, paDataStore.getFlowid(), paDataStore.getPid(), paDataStore.getSubpid(), sDeadline, saveHistory);
    
    if(sComment.equals("remove"))
      pam.addComment(userInfo, paDataStore.getFlowid(), paDataStore.getPid(), paDataStore.getSubpid(), "");
    else if(!sComment.equals("nochange"))
      pam.addComment(userInfo, paDataStore.getFlowid(), paDataStore.getPid(), paDataStore.getSubpid(), sComment, saveHistory);
    
    if(addLabels.length > 0)
    	pam.addLabel(userInfo, paDataStore.getFlowid(), paDataStore.getPid(), paDataStore.getSubpid(), addLabels, saveHistory);
    if(removeLabels.length > 0)
    	pam.removeLabel(userInfo, paDataStore.getFlowid(), paDataStore.getPid(), paDataStore.getSubpid(), removeLabels);
    
    if (!StringUtils.isEmpty(forwardToLabelId)){
      String [] forwardToLabel = new String[1];
      forwardToLabel[0] = forwardToLabelId;
      pam.addLabel(userInfo, paDataStore.getFlowid(), paDataStore.getPid(), paDataStore.getSubpid(), forwardToLabel, saveHistory);
    }
    
    return "";
  }
  
  private ProcessAnnotationDataStore getProcessData(UserInfoInterface userInfos, HttpServletRequest request) {

	  int flowid = -1;
	  int pid = -1;
	  int subpid = -1;

	  try {
		  flowid = Integer.valueOf(request.getParameter(PARAM_FOWLID));
	  } catch (Exception e) {}
	  try {
		  pid = Integer.valueOf(request.getParameter(PARAM_PID));
	  } catch (Exception e) {}
	  try {
		  subpid = Integer.valueOf(request.getParameter(PARAM_SUBPID));
	  } catch (Exception e) {}
	  //HttpSession session = request.getSession();
	  //userInfo = (UserInfoInterface) session.getAttribute(Const.USER_INFO);
	  //this.request = request;


	  ProcessAnnotationDataStore paDataStore = new ProcessAnnotationDataStore();
	  paDataStore.setFlowid(flowid);
	  paDataStore.setPid(pid);
	  paDataStore.setSubpid(subpid);
	  return paDataStore;
  }

  private String[] getParamListString(HttpServletRequest request, String paramName) {
	  String param = request.getParameter(paramName);
	  String[] paramlist = new String[0];
	  
	  if(param != null){
		  paramlist = param.split(",");		  
		  if( (paramlist.length == 0) || (paramlist.length == 1 && paramlist[0].equals(""))) 
			  paramlist = new String[0];
	  }
	  return paramlist;
  }
 
  private class ProcessAnnotationDataStore {
	  private int flowid = -1;
	  private int pid = -1;
	  private int subpid = 1;

	  public int getFlowid() {
		  return flowid;
	  }
	  public void setFlowid(int flowid) {
		  this.flowid = flowid;
	  }
	  public int getPid() {
		  return pid;
	  }
	  public void setPid(int pid) {
		  this.pid = pid;
	  }
	  public int getSubpid() {
		  return subpid;
	  }
	  public void setSubpid(int subpid) {
		  this.subpid = subpid;
	  }
  }
}