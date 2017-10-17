<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c"%>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if"%><%
request.setAttribute("inFrame", "true");
%><%@ include file="../inc/defs.jsp"%>
<%@ page import="pt.iflow.api.blocks.Block"%>
<%@ page import="pt.iflow.api.blocks.FormOperations"%>
<%@ include file="../inc/initProcInfo.jspf"%>
<%@ include file="../inc/checkProcAccess.jspf"%><%


//TODO
String popupReturnBlockId = null;

    String title = "Formul&aacute;rio";
    // use of fdFormData defined in /inc/defs.jsp
    String sOp = fdFormData.getParameter("op");
    if (sOp == null) {
        sOp = "0";
    }
    int op = Integer.parseInt(sOp);
    
    Block bBlockJSP = null;
    if(fdFormData.getParameter("_button_clicked_id")!=null)
    	session.setAttribute("_button_clicked_id", fdFormData.getParameter("_button_clicked_id"));
	
	Enumeration<String>paramNames = fdFormData.getParameterNames();
	while(paramNames.hasMoreElements()){
		String auxParamName = paramNames.nextElement(); 
		if(auxParamName.startsWith("_tabholder_selected"))
			session.setAttribute(auxParamName, fdFormData.getParameter(auxParamName));
	}    	
    	
    String currMid = String.valueOf(pm.getModificationId(userInfo, procData.getProcessHeader()));

    HashMap<String, String> hmHidden = new HashMap<String, String>();
    hmHidden.put("subpid", String.valueOf(subpid));
    hmHidden.put("pid", String.valueOf(pid));
    hmHidden.put("flowid", String.valueOf(flowid));
    hmHidden.put("op", String.valueOf(op));
    hmHidden.put(Const.FLOWEXECTYPE, flowExecType);
    hmHidden.put("_serv_field_", "-1");
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
        
        if (bBlockJSP.getClass().getName().indexOf("BlockFormulario") == -1) {
            throw new Exception("Not BlockFormulario!");
        }
    } catch (Exception e) {
        // send to main page...
        // not able to get flow or process is not in jsp state (if
        // a casting exception occurs..)
        ServletUtils.sendEncodeRedirect(response, "../flow_error.jsp");
        return;
    }
    
    //Check if went from a form to another within the same flow       	   	
   	if(StringUtils.equals("" + session.getAttribute("last_flowid"), "" + bBlockJSP.getFlowId()) && !StringUtils.equals("" + session.getAttribute("last_blockid"), "" + bBlockJSP.getId()))
   		session.setAttribute("_changed_form", "_changed_form");
   	else
   		session.setAttribute("_changed_form", null);
	session.setAttribute("last_flowid" , "" + bBlockJSP.getFlowId());
    session.setAttribute("last_blockid", "" + bBlockJSP.getId());   			
    

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
    
	if (bBlockJSP != null && op == 0) {
	    // Variables
	    String description = title;
		//TODO: corrigir isto
	    String url = "Form/form.jsp?flowid=" + flowid + "&pid=" + pid + "&subpid=" + subpid;

	    Logger.trace(this, "before", login + " call with subpid=" + subpid + ",pid=" + pid + ",flowid=" + flowid);

	    String nextPage = url;

	    Activity activity = null;
	    String stmp = null;

	    try {
	      // Get the ProcessManager EJB

	      activity = new Activity(login, flowid, pid, subpid, 0, 0, description, Block.getDefaultUrl(userInfo, procData), 1);
	      activity.setRead();
	      activity.mid = procData.getMid();
	      pm.updateActivity(userInfo, activity);

	    } catch (Exception e) {
	      Logger.error(login, this, "before", procData.getSignature() + "Caught an unexpected exception scheduling activities: "
	          + e.getMessage(), e);
	    }
	}
	
    if (bBlockJSP != null && op == 9) {
        // return to parent 
  		ProcessData pdReturn = (ProcessData)session.getAttribute(Const.sSWITCH_PROC_SESSION_ATTRIBUTE);
        session.setAttribute(Const.SESSION_PROCESS + flowExecType, pdReturn);
        String next_page = flow.nextBlock(userInfo, pdReturn);
        
        if (next_page == null) {
            next_page = sURL_PREFIX + "flow_error.jsp" + "?"+ Const.FLOWEXECTYPE +"=" + flowExecType + "&ts=" + ts;
        } else {
            next_page = sURL_PREFIX + next_page + "&"+ Const.FLOWEXECTYPE +"=" + flowExecType + "&ts=" + ts;
        }

        ServletUtils.sendEncodeRedirect(response, next_page);
        return;
        
    }
    
    Object[] oa = null;
    if (bBlockJSP != null && (op == 2 || op == 3 || op == 5 || op == 6 || op == 7 || op == FormOperations.OP_ONCHANGE_SUBMIT)) {
        
      String formMid = fdFormData.getParameter(Const.sMID_ATTRIBUTE);
      request.setAttribute(Const.sMID_ATTRIBUTE, formMid);
      boolean procAccessOk = StringUtils.isBlank(formMid) || StringUtils.equals(currMid, formMid);
      
      if (procAccessOk) {
            oa = new Object[5];
            oa[0] = userInfo;
            oa[1] = procData;
            oa[2] = fdFormData;
            oa[3] = new ServletUtils(request, response);
            oa[4] = (op == FormOperations.OP_GENERATE_FORM || op == FormOperations.OP_ONCHANGE_SUBMIT);

            
            // 3: processForm
            procData = (ProcessData) bBlockJSP.execute(3, oa);
            
            oa = new Object[1];
            oa[0] = procData;
            // 4: hasError
            if (!((Boolean) bBlockJSP.execute(4, oa)).booleanValue()) {
                
				if (op == 2 || (pid != Const.nSESSION_PID && (op == 5 || op == 6 || op == 7 /*|| op == 8*/))) {
                    // save dataset if op = 2 (save) or refresh(8)/print(5,6)/export(7) and process is in DB
                    // don't save it if op = 3 because nextBlock does it!
					int mid = flow.saveDataSet(userInfo, procData, request);
					currMid = String.valueOf(mid);
                    // now don't forget to update flowid and pid and subpid
                    flowid = procData.getFlowId();
                    pid = procData.getPid();
                    subpid = procData.getSubPid();
                    hmHidden.put("subpid", String.valueOf(subpid));
                    hmHidden.put("pid", String.valueOf(pid));
                    hmHidden.put("flowid", String.valueOf(flowid));
                    hmHidden.put(Const.FLOWEXECTYPE, flowExecType);
                	hmHidden.put(Const.sMID_ATTRIBUTE, currMid);
                	request.setAttribute(Const.sMID_ATTRIBUTE, currMid);
                }
                
        			boolean autoAdvance = (op==FormOperations.OP_ONCHANGE_SUBMIT?((Boolean)bBlockJSP.execute(14, oa)).booleanValue():false);
                    if (op == 3 || autoAdvance) {
						String next_page = flow.nextBlock(userInfo, procData);

					    //Caso esteja a correr em popup e o bloco não seja de popup, sair no erro
					    if( popupReturnBlockId != null && !flow.getBlock(userInfo, procData).canRunInPopupBlock()){
							next_page = sURL_PREFIX + "flow_popup_error.jsp" + "?" + Const.FLOWEXECTYPE + "=" + flowExecType + "&ts=" + ts;
					    }else{
	                        boolean bStay = false;
							if (StringUtils.isNotEmpty(procData.getAppData(Const.STAY_IN_PAGE))) {
	                            bStay = true;
	                        } else {
	                            if (next_page == null) {
									next_page = sURL_PREFIX + "flow_error.jsp" + "?"+ Const.FLOWEXECTYPE +"=" + flowExecType + "&ts=" + ts;
								} else {
	                                bStay = false;
									next_page = sURL_PREFIX + next_page + "&"+ Const.FLOWEXECTYPE +"=" + flowExecType + "&ts=" + ts;
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
            }
        } else {
            oa = new Object[2];
            oa[0] = procData;
            oa[1] = messages.getString("form.proc_change_error");
            // 5: setError
            bBlockJSP.execute(5, oa);
        }
    } // op == 2 || op == 3 || op == 5 || op == 6 || op == 7 || op == 8
    else if (bBlockJSP != null && op == 4) {
        // cancel proc
        String next_page = flow.endProc(userInfo, procData);
        if (next_page == null) {
            next_page = sURL_PREFIX + "flow_error.jsp" + "?"+ Const.FLOWEXECTYPE +"=" + flowExecType + "&ts=" + ts;
        } else {
            next_page = sURL_PREFIX + next_page + "&"+ Const.FLOWEXECTYPE +"=" + flowExecType + "&ts=" + ts;
        }
        ServletUtils.sendEncodeRedirect(response, next_page);
        return;
    } // op == 4
    
    String sHtml = "";
    String sFormName = "";
    
    if (bBlockJSP != null) {
        // return to parent additional stuff
        boolean bRemoveParentProc = false;
        ProcessData pdReturn = (ProcessData) session.getAttribute(Const.sSWITCH_PROC_SESSION_ATTRIBUTE);
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
        
        //if chabged form scroll back to top
        hmHidden.put("_changed_form", ""+session.getAttribute("_changed_form"));
     	
        //button_clicked_id for auto scrolling the form
        hmHidden.put("_button_clicked_id", ""+session.getAttribute("_button_clicked_id"));
     	session.removeAttribute("_button_clicked_id");
     	
     	//to keep previously selected tabs visible
     	Enumeration<String> attrNames =  session.getAttributeNames();
     	while(attrNames.hasMoreElements()){
     		String auxAttrName = attrNames.nextElement();
     		if(auxAttrName.startsWith("_tabholder_selected")){
     			hmHidden.put(auxAttrName, ""+session.getAttribute(auxAttrName));
     			session.removeAttribute(auxAttrName);
     		}
     	} 
        
        oa = new Object[4];
        oa[0] = userInfo;
        oa[1] = procData;
        oa[2] = hmHidden;
        oa[3] = new ServletUtils(response);
        // 2: generateForm
        sHtml = (String) bBlockJSP.execute(2, oa);
        
        // 7: var FORM_NAME
        sFormName = (String) bBlockJSP.execute(7, null);
    }
%>

<%@ include file="servicesjs.jspf"%>

<%
    if (op == 5 || op == 6 || op == 7) {
        String sField = fdFormData.getParameter("_serv_field_");
        
        if (op == 5) {
            // print
%>

<script language="JavaScript" type="text/javascript">
    <!--
      PrintServiceOpen();
    //-->
</script>
<%
    } else if (op == 6) {
            // printfield
%>
<script language="JavaScript" type="text/javascript">
    <!--
      PrintServiceOpen(<%=sField%>);
    //-->
</script>
<%
    } else if (op == 7) {
            // exportfield
%>
<script language="JavaScript" type="text/javascript">
    <!--
      ExportServiceOpen(<%=sField%>);
    //-->
</script>
<%
    }
    }
%>
<%
if (pid > 0) {
%>
<script language="JavaScript" type="text/javascript">
  parent.showAnnotations(<%=flowid%>,<%=pid%>,<%=subpid%>,'<%=from%>');
</script>
<%} else {%>
<script language="JavaScript" type="text/javascript">
  parent.hideAnnotations();
</script>
<%}%><%=sHtml%>
<%@ include file="../inc/initProcInfoEndPage.jspf"%>
