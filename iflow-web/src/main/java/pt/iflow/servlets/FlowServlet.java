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
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.ProcessCatalogue;
import pt.iflow.api.flows.Flow;
import pt.iflow.api.flows.IFlowData;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.UserInfoInterface;

/**
 * Servlet implementation class for Servlet: FlowServlet
 *
 *@web.servlet name="FlowServlet"
 *@web.servlet-mapping url-pattern="/FlowServlet"
 */
 public class FlowServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	 static final long serialVersionUID = 1L;

	 public static String PARAM_ACTION = "action";
	 public static String PARAM_FLOWID = "flowid";


	 public static String GET_FLOW_VARIABLES = "GetFlowVariables";


   
    /* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public FlowServlet() {
		super();
	}   	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		service(request, response);
	}  	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		service(request, response);
	}

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		UserInfoInterface userInfo = (UserInfoInterface)request.getSession().getAttribute(Const.USER_INFO);
	    if(null == userInfo) {
	      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Operation not permitted");
	      return;
	    }

		String action = request.getParameter(PARAM_ACTION);
		
		if (GET_FLOW_VARIABLES.equals(action)) {
			doGetFlowVariables(userInfo, request, response);
		}
		else {
		      response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, "Action not supported");
		      return;
		}
	}

	private void doGetFlowVariables(UserInfoInterface userInfo, HttpServletRequest request, HttpServletResponse response) {
		try {
			int flowid = Integer.parseInt(request.getParameter(PARAM_FLOWID));
			Flow flow = BeanFactory.getFlowBean();
			IFlowData fd = flow.getFlow(userInfo, flowid);
			
			ProcessCatalogue catalogue = fd.getCatalogue();
			TreeSet<String> names = new TreeSet<String>();
			names.addAll(catalogue.getSimpleVariableNames());
			names.addAll(catalogue.getListVariableNames());
			
			if (catalogue == null)
				return;
			
			JSONObject jsonObj = new JSONObject();
			for (String var : names) {
				// NOTE: antigamente era guardado o par (var, var). 
			  // Exisitia uma nota a dizer que deveria usar o public name e o desejo foi concedido!
			  jsonObj.put(var, catalogue.getDisplayName(var));
			}
			
			
			response.getOutputStream().println(jsonObj.toString());			
			
		}
		catch (Exception e) {
		}
	} 	
	
}
