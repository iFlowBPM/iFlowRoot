<!--
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
-->

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c"%>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if"%><%
request.setAttribute("inFrame", "true");
%><%@ include file="../inc/defs.jsp"%>
<%@ page import="pt.iflow.api.blocks.Block"%>
<%@page import="pt.iflow.api.blocks.FormOperations"%>
<%@ include file="../inc/initProcInfo.jspf"%>
<%@ include file="../inc/checkProcAccess.jspf"%><%
String title = "Formul&aacute;rio";


// use of fdFormData defined in /inc/defs.jsp
String sOp = fdFormData.getParameter("op");
if (sOp == null) {
	sOp = "0";
}
int op = Integer.parseInt(sOp);

Block bBlockJSP = null;

    String currMid = String.valueOf(pm.getModificationId(userInfo, procData.getProcessHeader()));

HashMap<String,String> hmHidden = new HashMap<String,String>();
hmHidden.put("subpid",String.valueOf(subpid));
hmHidden.put("pid",String.valueOf(pid));
hmHidden.put("flowid",String.valueOf(flowid));
hmHidden.put("op",String.valueOf(op));
hmHidden.put("_serv_field_","-1");
hmHidden.put(Const.sMID_ATTRIBUTE, currMid);
request.setAttribute(Const.sMID_ATTRIBUTE,currMid);

Flow flow = BeanFactory.getFlowBean();
try {
	bBlockJSP = flow.getBlock(userInfo, procData);
	// To appear blockId in title
	title = bBlockJSP.getDescription(userInfo, procData);
	if (Const.nMODE == Const.nDEVELOPMENT) {
		title = title + " block" + bBlockJSP.getId();
	}

	if (!(bBlockJSP instanceof pt.iflow.api.blocks.FormOperations)) {
		throw new Exception("Not Expected Block!");
	}
} catch (Exception e) {
	// send to main page...
	// not able to get flow or process is not in jsp state (if a casting exception occurs..)
	ServletUtils.sendEncodeRedirect(response, "../flow_error.jsp");
	return;
}


// OP: 0 - entering page/reload
//     1 - unused
//     2 - save
//     3 - next
//     4 - cancel
//     5 - service print
//     6 - service print field
//     7 - service export field
//     8 - only process form
//     9 - return to parent

	// check write permissions...
	if (!flow.checkUserFlowRoles(userInfo, flowid, "" + FlowRolesTO.WRITE_PRIV) &&
		!flow.checkUserFlowRoles(userInfo, flowid, "" + FlowRolesTO.SUPERUSER_PRIV)) {

		ServletUtils.sendEncodeRedirect(response, "../nopriv.jsp?flowid="+flowid);
		return;
	}

if (bBlockJSP != null && op == 9) {
	// return to parent 
	ProcessData pdReturn = (ProcessData)session.getAttribute(Const.sSWITCH_PROC_SESSION_ATTRIBUTE);
    session.setAttribute(Const.SESSION_PROCESS + flowExecType, pdReturn);
	String next_page = flow.nextBlock(userInfo, pdReturn);

	if (next_page == null) {
		next_page = sURL_PREFIX + "flow_error.jsp" + "?ts=" + ts;
	} else {
		next_page = sURL_PREFIX + next_page + "&ts=" + ts;
	}

	ServletUtils.sendEncodeRedirect(response, next_page);
	return;
}

Object[] oa = null;
if (bBlockJSP != null && (op == 2 || op == 3 || op == 5 || op == 6 || op == 7 || op == 8)) {
  String formMid = fdFormData.getParameter(Const.sMID_ATTRIBUTE);
  request.setAttribute(Const.sMID_ATTRIBUTE, formMid);
  boolean procAccessOk = StringUtils.isBlank(formMid) || StringUtils.equals(currMid, formMid);

  if (procAccessOk) {

    oa = new Object[5];
	oa[0] = userInfo;
	oa[1] = procData;
	oa[2] = fdFormData;
    oa[3] = new ServletUtils(request, response);
    oa[4] = op == 2;
		// 3: processForm
		procData = (ProcessData)bBlockJSP.execute(3,oa);

		oa = new Object[1];
		oa[0] = procData;
		// 4: hasError
		if (!((Boolean)bBlockJSP.execute(4,oa)).booleanValue()) {

		if (op == 2 || (pid != Const.nSESSION_PID && (op == 5 || op == 6 || op == 7 /*|| op == 8*/))) {
				// save dataset if op = 2 (save) or refresh(8)/print(5,6)/export(7) and process is in DB
				// don't save it if op = 3 because nextBlock does it!
	int mid = flow.saveDataSet(userInfo, procData, request);
	currMid = String.valueOf(mid);
				// now don't forget to update flowid and pid and subpid
				flowid = procData.getFlowId();
				pid = procData.getPid();
				subpid = procData.getSubPid();
				hmHidden.put("subpid",String.valueOf(subpid));
				hmHidden.put("pid",String.valueOf(pid));
				hmHidden.put("flowid",String.valueOf(flowid));
	hmHidden.put(Const.sMID_ATTRIBUTE, currMid);
	request.setAttribute(Const.sMID_ATTRIBUTE, currMid);
			}

				boolean autoAdvance = (op==FormOperations.OP_ONCHANGE_SUBMIT?((Boolean)bBlockJSP.execute(14, oa)).booleanValue():false);
				if (op == 3 || autoAdvance) {
					String next_page = flow.nextBlock(userInfo, procData);
					boolean bStay = false;
					if (StringUtils.isNotEmpty(procData.getAppData(Const.STAY_IN_PAGE))) {
						bStay = true;
                        } else {
			if (next_page == null) {
						next_page = sURL_PREFIX + "flow_error.jsp" + "?ts=" + ts;
					} else {
						bStay = false;
						next_page = sURL_PREFIX + next_page + "&ts=" + ts;
					}
		}
		if (!bStay) {
                          if (!procData.isInDB()) {
                            session.setAttribute(Const.SESSION_PROCESS + flowExecType, procData);
                          }
						ServletUtils.sendEncodeRedirect(response, next_page);
						return;
					}
				}
			}
	} else {
		oa = new Object[2];
		oa[0] = procData;
    oa[1] = messages.getString("form.proc_change_error");
		// 5: setError
		bBlockJSP.execute(5,oa);
	}
} // op == 2 || op == 3 || op == 5 || op == 6 || op == 7 || op == 8
else if (bBlockJSP != null && op == 4) {
	// cancel proc
	String next_page = flow.endProc(userInfo, procData);
	if (next_page == null) {
		next_page = sURL_PREFIX + "flow_error.jsp" + "?ts=" + ts;
	} else {
		next_page = sURL_PREFIX + next_page + "&ts=" + ts;
	}
	ServletUtils.sendEncodeRedirect(response, next_page);
	return;
} // op == 4


String sHtml = "";
String sFormName = "";

if (bBlockJSP != null) {
	// return to parent additional stuff
	boolean bRemoveParentProc = false;
	ProcessData pdReturn = (ProcessData)session.getAttribute(Const.sSWITCH_PROC_SESSION_ATTRIBUTE);
	if (pdReturn != null) {
		String sSwitchFlag = pdReturn.getTempData(Const.sSWITCH_PROC_FLAG);
		String sRetProcFid = pdReturn.getTempData(Const.sSWITCH_PROC_TEMP_FID);
		String sRetProcPid = pdReturn.getTempData(Const.sSWITCH_PROC_TEMP_PID);
		String sRetProcSubPid = pdReturn.getTempData(Const.sSWITCH_PROC_TEMP_SUBPID);

            if (sSwitchFlag == null || !sSwitchFlag.equals("true")
                    || sRetProcFid == null || sRetProcPid == null
                    || sRetProcSubPid == null) {
			bRemoveParentProc = true;			
            } else {
                if (!sRetProcFid.equals(String.valueOf(flowid))
                        || !sRetProcPid.equals(String.valueOf(pid))
                        || !sRetProcSubPid.equals(String.valueOf(subpid))) {
			bRemoveParentProc = true;
		} else {
			// enable switch proc return to parent
                    procData.setTempData(
                            Const.sSWITCH_PROC_RETURN_PARENT, "true");
		}
		}
	} else {
		bRemoveParentProc = true;
	}

	if (bRemoveParentProc) {
		procData.setTempData(Const.sSWITCH_PROC_RETURN_PARENT, null);
		session.removeAttribute(Const.sSWITCH_PROC_SESSION_ATTRIBUTE);
	}
  
	if (op == 5 || op == 6 || op == 7) {
		String sField = fdFormData.getParameter("_serv_field_");
		String next_page = null;
		String __printForm = Const.APP_URL_PREFIX+"/WebForm/print.jsp?";
		String __exportForm = Const.APP_URL_PREFIX+"/WebForm/export.jsp?";

		if (op == 5) {
			// print
			next_page = __printForm+"flowid="+flowid+"&pid="+pid+"&subpid="+subpid+"&ts="+ts;
		} else if (op == 6) {
			// printfield
			next_page = __printForm+"flowid="+flowid+"&pid="+pid+"&subpid="+subpid+"&ts="+ts+"&field="+sField;
		} else if (op == 7) {
			// exportfield
			next_page = __exportForm+"flowid="+flowid+"&pid="+pid+"&subpid="+subpid+"&ts="+ts+"&field="+sField;
		}
		ServletUtils.sendEncodeRedirect(response, next_page);
		return;
	}

	oa = new Object[4];
	oa[0] = userInfo;
	oa[1] = procData;
	oa[2] = hmHidden;
	oa[3] = new ServletUtils(request,response);
	// 2: generateForm
	sHtml = (String)bBlockJSP.execute(2,oa);

	// 7: var FORM_NAME
	sFormName = (String)bBlockJSP.execute(7,null);
}
%>
<%=sHtml%>
<%@ include file="../inc/initProcInfoEndPage.jspf"%>
 
