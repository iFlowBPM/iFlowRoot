package pt.iflow.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pt.iflow.api.utils.ServletUtils;
import pt.iflow.api.utils.ServletUtilsRoutesEnum;

/**
 * Servlet implementation class for Servlet: GoTo
 * 
 * reformats requests for registered pages from outer accesses
 * 
 * Parameter goto should be used with the page identifier
 * Page identifier syntax examples:
 * 	IFLOW_URI/Admin/admin.jsp -> goto=Admin/admin.jsp
 *  IFLOW_URI/confirmar_agendamento.jsp -> goto=confirmar_agendamento.jsp
 * All extra parameters should follow. If extra goto parameters exist they must come
 * after 
 *
* @author iKnow
* 
* @web.servlet
* name="GoToTask"
* 
* @web.servlet-mapping
* url-pattern="/GoTo"
 */
 public class GoTo extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
   static final long serialVersionUID = 1L;
   
    /* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public GoTo() {
		super();
	}   	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// URL exmplo
		// http://localhost:8085/iFlow/GoTo?goto=confirmar_agendamento.jsp&id=81&owner=jcosta&dkey=8841dffbe014e9ddf18c96e4eec6b179
		String sGoTo = request.getParameter(PageMapper.PARAM_GOTO);
		PageMapper mapper = null;
		if (sGoTo != null && !sGoTo.equals("")) {
			mapper = new PageMapper(request.getParameterMap());
		}
		
      ServletUtils.sendEncodeRedirect(response, ServletUtilsRoutesEnum.MAIN,request.getParameterMap());

		
		return;
	}  	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}   	  	    
}