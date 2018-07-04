<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
%><%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c"
%><%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if"
%><%@ include file="../inc/defs.jsp"
%><%@page import="pt.iflow.api.blocks.Block"
%><%@page import="pt.iflow.api.blocks.Port"
%><%@page import="pt.iflow.api.utils.mail.MailConfig"
%><%@page import="pt.iknow.utils.crypt.CryptUtils"
%><%@page import="pt.iflow.api.utils.hotfolder.HotFolderConfig"
%><%@page import="pt.iflow.api.connectors.DMSConnectorUtils"
%><%
    String title = messages.getString("flow_settings_edit.title");
    String sPage = "Admin/flow_settings_edit";
    
    StringBuffer sbError = new StringBuffer();
    
    String sSELECT = messages.getString("flow_settings_edit.const.select");
    
    Flow flow = BeanFactory.getFlowBean();
    
    String sOp = fdFormData.getParameter("op");
    if (sOp == null) {
      sOp = "0";
    }
    int op = Integer.parseInt(sOp);
    
    int flowid = 0;
    
    try {
        // necessary var checking
        if (flow == null) {
            throw new Exception();
        }
        flowid = Integer.parseInt(fdFormData.getParameter(DataSetVariables.FLOWID));
    } catch (Exception e) {
        op = 1;
    }
    
    if (op == 1) {
        ServletUtils.sendEncodeRedirect(response, "flow_settings.jsp");
        return;
    }
%>
<%@ include file="auth.jspf"%>
<%
    String sFlowName = fdFormData.getParameter("flowname");
    IFlowData fd = flow.getFlow(userInfo, flowid);
    if (StringUtils.isBlank(sFlowName)) {
      if (fd != null) {
	      sFlowName = fd.getName();
      }
      if (StringUtils.isBlank(sFlowName)) {
        sFlowName = "";
      }
    }
    
    FlowSetting[] fsa = flow.getFlowSettings(userInfo, flowid);
    if (fsa == null)
        fsa = new FlowSetting[0];
    FlowSetting fs = null;
    
    CryptUtils mailCrypt = new CryptUtils(MailConfig.class.getName());
    CryptUtils dmsCrypt = new CryptUtils(DMSConnectorUtils.class.getName());
    
    String sSave = "";
    boolean btmp = false;
    if (op == 2 || op == 3 || op == 4 || op == 5 || op == 6) { // include propagate to flow
        if (op == 2) {
            for (int i = 0; i < fsa.length; i++) {
                fs = fsa[i];
                if (fs.isListSetting()) {
                    ArrayList<String> values = new ArrayList<String>();
                    
                    String[] saValsToSave = fs.getValuesToSave();
                    
                    for (int j = 0; j < saValsToSave.length; j++) {
                        String propName = Utils.genListVar(fs.getName(), j);
                        String value = fdFormData.getParameter("h_" + propName);
                        if (StringUtils.equals(value, "1")) {
                            value = saValsToSave[j];
                        } else {
                            value = fdFormData.getParameter(propName);
                        }
                        values.add(value);
                    }
                    fs.setValues(values);
                } else {
                    String value = fdFormData.getParameter(fs.getName());
                    if (fs.getName().equals(Const.sGUEST_ACCESSIBLE)) {
                      boolean guestProp = StringUtilities.isAnyOfIgnoreCase(value, new String[] {
                          Const.sGUEST_ACCESSIBLE_YES,"sim", "yes","true", "1"});
					  if (guestProp) {                      
                    	String procDB = fdFormData.getParameter(Const.sPROCESS_LOCATION);
                    	if (StringUtilities.isEqual(procDB, Const.sPROCESS_IN_DB)
                    		|| !flow.getFlow(userInfo, flowid).isGuestCompatible(userInfo)) {
                        	sbError.append("<br>").append(messages.getString("flow_settings_edit.error.unguestable"));
                        	value = Const.sGUEST_ACCESSIBLE_NO;
                    	}
					  }
                    }
                    else if (fs.getName().startsWith(MailConfig.CONFIG_PREFIX)) {
                      // mail special settings processing and validation
                      if (fs.getName().equals(MailConfig.CONFIG_PASS)) {
                        if (!StringUtils.isEmpty(value)) {                          
                          value = mailCrypt.encrypt(value);
                        }
                      }
                      else if (StringUtils.isNotEmpty(value) && !StringUtils.isNumeric(value)) {
                        if (fs.getName().equals(MailConfig.CONFIG_PORT)) {
                          sbError.append("<br>").append(messages.getString("flow_settings_edit.error.mail.numericport"));                            
                        }
                        else if (fs.getName().equals(MailConfig.CONFIG_CHECK_INTERVAL)) {
                          sbError.append("<br>").append(messages.getString("flow_settings_edit.error.mail.numericcheckinterval"));
                        }
                      }
                    }
                    else if (fs.getName().startsWith(DMSConnectorUtils.CONFIG_PREFIX)) {
                      if (fs.getName().equals(DMSConnectorUtils.CONFIG_DMS_PASS)) {
                        if (!StringUtils.isEmpty(value)) {
                          value = dmsCrypt.encrypt(value);
                        }
                      }
                    }
                    else if (fs.getName().startsWith(HotFolderConfig.CONFIG_PREFIX)) {
                      if (fs.getName().equals(HotFolderConfig.SEARCH_DEPTH) && 
                          StringUtils.isNotEmpty(value) && 
                          !StringUtils.isNumeric(value)) {
                        sbError.append("<br>").append(messages.getString("flow_settings_edit.error.hotfolder.numericdepth"));                            
                      }
                    }
                    fs.setValue(value);
                }
                fsa[i] = fs;
            }
            
            // validate email
            MailConfig mailconfig = MailConfig.parse(fsa);
            if (mailconfig.isOn()) {
              if (!mailconfig.validate()) {
              	sbError.append("<br>").append(messages.getString("flow_settings_edit.error.mail.invalid"));
              }
            }
            else {
              // reset mail stuff in settings
              for (int i = 0; i < fsa.length; i++) {
                if (!fsa[i].isListSetting()) {
                  if (fsa[i].getName().startsWith(MailConfig.CONFIG_PREFIX) &&
                      !fsa[i].getName().equals(MailConfig.CONFIG_ONOFF)) {
					fsa[i].setValue("");
                  }
                }
              }
            }
            
            // validate dms
            DMSConnectorUtils dmsconfig = DMSConnectorUtils.parse(fsa);
            if (dmsconfig.isOn()) {
              if (!dmsconfig.validate()) {
              	sbError.append("<br>").append(messages.getString("flow_settings_edit.dms.error.invalid"));
              }
            } else {
              // reset dms stuff in settings
              for (int i = 0; i < fsa.length; i++) {
                if (!fsa[i].isListSetting()) {
                  if (fsa[i].getName().startsWith(DMSConnectorUtils.CONFIG_PREFIX) &&
                      !fsa[i].getName().equals(DMSConnectorUtils.CONFIG_DMS)) {
					fsa[i].setValue("");
                  }
                }
              }
            }

            // validate hotfolder
            HotFolderConfig hotfolderconfig = HotFolderConfig.parse(flowid, fsa);
            if (hotfolderconfig.isOn()) {
              if (!hotfolderconfig.validate()) {
              	sbError.append("<br>").append(messages.getString("flow_settings_edit.hotfolder.error.invalid"));
              }
            } else {
              // reset hotfolder stuff in settings
              for (int i = 0; i < fsa.length; i++) {
                if (!fsa[i].isListSetting()) {
                  if (fsa[i].getName().startsWith(HotFolderConfig.CONFIG_PREFIX) &&
                      !fsa[i].getName().equals(HotFolderConfig.ONOFF)) {
					fsa[i].setValue("");
                  }
                }
              }
            }
            
        } else if (op == 3 || op == 4 || op == 5) {
            String sVarname = fdFormData.getParameter("varname");
            
            String sVarPrefix = Utils.getListVarName(sVarname);
            
            if (sVarname != null) {
                String sValue = fdFormData.getParameter(sVarname); // value
                
                if (StringUtils.isEmpty(sValue)) {
                    // try in hidden value
                    sValue = fdFormData.getParameter("h_" + sVarname);
                }
                if (StringUtils.isNotEmpty(sValue)) {
                    
                    // first get flow setting object
                    for (int s = 0; fsa != null && s < fsa.length; s++) {
                        fs = fsa[s];
                        String sSettingName = fs.getName();
                        
                        if (!sSettingName.equals(sVarPrefix)) {
                            continue;
                        }
                        
                        ArrayList<String> alValues = new ArrayList<String>();
                        ArrayList<Integer> alValuePositions = new ArrayList<Integer>();
                        String[] saValsToSave = fs.getValuesToSave();
                        for (int j = 0; j < saValsToSave.length; j++) {
                            alValues.add(saValsToSave[j]);
                            if (fs.isQueryValue(j)) {
                                alValuePositions.add(j);
                            }
                        }
                        if (op == 3) {
                            // remove
                            alValues.remove(saValsToSave.length - 1);
                            sValue = null;
                        } else if (op == 4) {
                            // text
                        } else if (op == 5) {
                            // query
                            alValuePositions.add(saValsToSave.length);
                        }
                        
                        alValues.add(sValue);
                        
                        fs.setValues(alValues, alValuePositions);
                        break;
                    }
                } else {
                    String[] saParams = { sVarPrefix };
                    sbError.append("<br>").append(
                            messages.getString("flow_settings_edit.msg.itemempty",saParams));
                }
            }
        } // else if (op == 3 || op == 4 || op == 5)
        else if (op == 6) {
            String sVarname = fdFormData.getParameter("varname");
            
            if (ArrayUtils.contains(new String[] {
                    Const.sFORCE_NOTIFY_FOR_PROFILES,
                    Const.sDENY_NOTIFY_FOR_PROFILES }, sVarname)) {
                // now 
                String sProfileValue = fdFormData.getParameter(sVarname
                        + "_profile");
                if (StringUtils.isEmpty(sProfileValue)
                        || StringUtils.equals(sProfileValue, sSELECT)) {
                    String[] saParams = { sVarname };
                    sbError.append("<br>").append(
                            messages.getString(
                                    "flow_settings_edit.msg.noprof",
                                    saParams));
                } else {
                    String sValues = fdFormData.getParameter(sVarname);
                    List<String> alValues = null;
                    if (StringUtils.isEmpty(sValues)) {
                        alValues = new ArrayList<String>();
                    } else {
                        alValues = Utils.tokenize(sValues,
                                Const.sNOTIFY_FOR_PROFILES_SEPARATOR);
                    }
                    
                    alValues.add(sProfileValue);
                    
                    sValues = Utils.unTokenize(alValues, Const.sNOTIFY_FOR_PROFILES_SEPARATOR);
                    
                    // now find right flow setting object and save new value
                    for (int i = 0; i < fsa.length; i++) {
                        if (fsa[i].getName().equals(sVarname)) {
                            fsa[i].setValue(sValues);
                            break;
                        }
                    }
                }
            } else {
                // error
            }
        } // else if (op == 6)
        
        if (sbError.length() == 0) {
            flow.saveFlowSettings(userInfo, fsa);
            flow.refreshFlowSettings(userInfo, flowid); // include propagate to flow

            sSave = "<div class=\"info_msg\">"
                    + messages.getString("flow_settings_edit.msg.propsaved")
                    + "</div>";
        } else {
            sbError.insert(0, "<br>");
            sbError.append("<br>");
        }
        
        // refresh settings list
        fsa = flow.getFlowSettings(userInfo, flowid);
        
    } // if (op == 2 || op == 3 || op == 4 || op == 5 || op == 6) {
    
    Collection<?> cProfiles = ap.getAllProfiles(userInfo.getOrganization());
    String[] saProfiles = null;
    
    if (null != cProfiles) {
        saProfiles = (String[]) cProfiles.toArray(new String[cProfiles .size()]);
    }
    
    // String[] saProfiles = rep.listFilesAllLeaves(userInfo, "Profiles");
%>
<script type="text/javascript">
	function toggleFlowSettingDisplays(target, accept) {
		var trs = $$('tr.' + target);
		for (var i=0; i < trs.length; i++) {
			if (accept) {
				trs[i].style.display='';
			} else {
				trs[i].style.display='none';
			}
		}
	}
</script>
<script type="text/javascript">
	if (typeof fs_mailconfig == 'undefined') {
		var fs_mailconfig = function(isOn) {

			var trs = $$('tr.mailconfig');
			for (var i=0; i < trs.length; i++) {
				if (isOn == '<%=MailConfig.CONFIG_OPTION_YES%>')
					trs[i].style.display='';
				else
					trs[i].style.display='none';
			}		
		};
	}
</script>
<form name="flows" method="post">
	<input type="hidden" name="flowid" value="<%=flowid%>" />
	<input type="hidden" name="flowname" value="<%=sFlowName%>" />
	<input type="hidden" name="op" value="0" />
	<input type="hidden" name="varname" value="" />

<h1 id="title_admin"><%=title.replace("{0}", sFlowName) %></h1>

<%
    if (sbError.length() > 0) {
%>
<div class="error_msg"><%=sbError.toString()%></div>
<%
    }
%> <%=sSave%>

<%
IFlowData[] fda = BeanFactory.getFlowHolderBean().listFlows(userInfo);
List<String> aalValues = new ArrayList<String>(fda.length);
List<String> aalNames = new ArrayList<String>(fda.length);

for (int i=0; i < fda.length; i++) {
  aalValues.add(String.valueOf(fda[i].getId()));
  aalNames.add(fda[i].getName());
}

StringBuffer redirect = new StringBuffer();
redirect.append("tabber_right(");
redirect.append("4");
redirect.append(", '" + response.encodeURL("Admin/flow_settings_edit.jsp") + "'");
redirect.append(", 'flowid=' + this.value + '&ts="+ts+"');");
StringBuffer extra = new StringBuffer();
extra.append("onchange=\"javascript:");
extra.append("if(confirm('" + messages.getString("flow_settings_edit.confirm.redirect") + "')) { ");
extra.append("document.flows.op.value='2';");
extra.append("if(W != null) { W.close(); }");
extra.append("tabber_right(4, '" + response.encodeURL("Admin/flow_settings_edit.jsp") + "','' + get_params(document.flows));");
extra.append(redirect);
extra.append(" } else { ");
extra.append(redirect);
extra.append(" }\"");

String sFlowHtml = Utils.genHtmlSelect("flowSelect",
			  extra.toString(),
			  "" + flowid,
			  aalValues,
			  aalNames);
%>
<div class="table_inc">
<ol>
  <li>
    <label for="flowSelect"><%=messages.getString("flow_settings_edit.field.flow")%></label>
    <%=sFlowHtml%>
  </li>
</ol>
<% if (fsa != null && fsa.length > 0) { %>
<table class="item_list">
	<tr class="tab_header">
		<!--td><if:message string="flow_settings_edit.field.variable" /></td-->
		<td><if:message string="flow_settings_edit.field.property" /></td>
		<td><if:message string="flow_settings_edit.field.type" /></td>
		<td><if:message string="flow_settings_edit.field.value" /></td>
	</tr>
	<%
	   MailConfig mailConfig = MailConfig.parse(fsa);
	   int i=0;
	   for (i = 0; i < fsa.length; i++) {
	     String stmp = fsa[i].getName();
	     String settingDesc = null;
	     
	      if (stmp.startsWith(MailConfig.CONFIG_PREFIX) || stmp.startsWith(DMSConnectorUtils.CONFIG_PREFIX)
	          || stmp.startsWith(HotFolderConfig.CONFIG_PREFIX)) {
			continue;
	      }

	     if (messages.hasKey("flow_settings_edit.desc." + stmp)) {
	       settingDesc = messages.getString("flow_settings_edit.desc." + stmp); 
	     }
	     else {
	       settingDesc = fsa[i].getDescription();
	     }
	     String stmp3 = null;
	     String stmp4 = null;
	     
	     if (StringUtils.isEmpty(settingDesc)) {
	       settingDesc = stmp;
	     }
	%>
	<tr class="<%=(i % 2 == 0 ? "tab_row_even" : "tab_row_odd")%>">
		<!--td>< %=stmp%></td-->
		<td><%=settingDesc%></td>
		<td><%=fsa[i].isListSetting() ? "List" : "Simple"%></td>
		<td width="50%" class="txt" align="left">
		<%
		    if (fsa[i].isListSetting()) {
		                String[] saVals = fsa[i].getValuesToSave();
		                if (saVals == null)
		                    saVals = new String[0];
		%>
		<table border="0" cellspacing="0" cellpadding="0" align="center">
			<%
			    for (int val = 0, valp = 1; val < saVals.length; val++, valp++) {
			                    stmp3 = saVals[val];
			                    if (stmp3 == null) {
			                        stmp3 = "";
			                    }
			                    stmp4 = Utils.genListVar(stmp, val);
			                    
			                    if (valp == saVals.length) {
			                        // enable remove link
			                        btmp = false;
			                    } else {
			                        // disable remove link
			                        btmp = true;
			                    }
			%>
			<tr>
				<td><%=valp%>:&nbsp; <%
     if (fsa[i].isQueryValue(val)) {
 %> <%=stmp3%> <input type="hidden" name="h_<%=stmp4%>" value="1">
				<%
				    } else {
				%> <input type="text" class="txt" name="<%=stmp4%>"
					value="<%=StringEscapeUtils.escapeHtml(stmp3)%>" size="30"
					maxlength="100"> <%
     }
 %> &nbsp;</td>
				<td width="20%" class="txt" align="left">&nbsp;<%=fsa[i].isQueryValue(val) ? "(QUERY)"
                                    : "(TEX)"%>&nbsp; <a
					<%=Utils.genHRef("javascript:document.flows.op.value='3';document.flows.varname.value='"
                                          + Utils.escapeJavaScript(stmp4)
                                          + "';tabber_right(4, '"
                                          + response.encodeURL("Admin/flow_settings_edit.jsp")
                                          + "',get_params(document.flows));\"", 
                                         	btmp)%>>
				<if:message string="flow_settings_edit.link.remove" /></a></td>
			</tr>
			<%
			    }
			                stmp4 = Utils.genListVar(stmp, saVals.length);
			%>
			<tr>
				<td width="80%" class="txt" align="left"><%=(saVals.length + 1)%>:&nbsp;
				<input type="text" class="txt" name="<%=stmp4%>" value="" size="30"
					maxlength="1000">&nbsp;</td>
				<td width="20%" class="txt" align="left">&nbsp;<if:message
					string="flow_settings_edit.field.add" />&nbsp; <a
					href="javascript:document.flows.op.value='4';document.flows.varname.value='<%=stmp4%>';tabber_right(4, '<%=response.encodeURL("Admin/flow_settings_edit.jsp")%>',get_params(document.flows));">
				<if:message string="flow_settings_edit.link.value" /> </a>
				&nbsp;|&nbsp; <a
					href="javascript:document.flows.op.value='5';document.flows.varname.value='<%=stmp4%>';tabber_right(4, '<%=response.encodeURL("Admin/flow_settings_edit.jsp")%>',get_params(document.flows));">
				<if:message string="flow_settings_edit.link.query" /> </a></td>
			</tr>
		</table>
		<%
		    } else {
		        stmp3 = fsa[i].getValue();
		        out.print("&nbsp;&nbsp;&nbsp;&nbsp;");
		        if (stmp.equals(Const.sNOTIFY_USER)) {
		            if (stmp3 == null
		                    || stmp3.equals("")
		                    || stmp3.equalsIgnoreCase(Const.sNOTIFY_USER_YES)
		                    || stmp3.equalsIgnoreCase("sim")
		                    || stmp3.equalsIgnoreCase("yes")) {
		                stmp3 = Const.sNOTIFY_USER_YES;
		            } else {
		                stmp3 = Const.sNOTIFY_USER_NO;
		            }
		%> <select class="txt" name="<%=stmp%>">
			<option value="<%=Const.sNOTIFY_USER_YES%>"
				<%=stmp3.equals(Const.sNOTIFY_USER_YES) ? "selected"
                                            : ""%>><%=Const.sNOTIFY_USER_YES%>
			</option>
			<option value="<%=Const.sNOTIFY_USER_NO%>"
				<%=stmp3.equals(Const.sNOTIFY_USER_NO) ? "selected"
                                            : ""%>><%=Const.sNOTIFY_USER_NO%>
			</option>
		</select> <%
     			} else if (stmp.equals(Const.sSEARCHABLE_BY_INTERVENIENT)) {
		            if (stmp3 == null
		                    || stmp3.equals("")
		                    || stmp3.equalsIgnoreCase(Const.sSEARCHABLE_BY_INTERVENIENT_NO)
		                    || stmp3.equalsIgnoreCase("nao")
		                    || stmp3.equalsIgnoreCase("no")) {
		                stmp3 = Const.sSEARCHABLE_BY_INTERVENIENT_NO;
		            } else {
		                stmp3 = Const.sSEARCHABLE_BY_INTERVENIENT_YES;
		            }
		%> <select class="txt" name="<%=stmp%>">
			<option value="<%=Const.sSEARCHABLE_BY_INTERVENIENT_YES%>"
				<%=stmp3.equals(Const.sSEARCHABLE_BY_INTERVENIENT_YES) ? "selected"
                                            : ""%>><%=Const.sSEARCHABLE_BY_INTERVENIENT_YES%>
			</option>
			<option value="<%=Const.sSEARCHABLE_BY_INTERVENIENT_NO%>"
				<%=stmp3.equals(Const.sSEARCHABLE_BY_INTERVENIENT_NO) ? "selected"
                                            : ""%>><%=Const.sSEARCHABLE_BY_INTERVENIENT_NO%>
			</option>
		</select> <%

     			} else if (stmp.equals(Const.sFORCE_NOTIFY_FOR_PROFILES)
                         || stmp.equals(Const.sDENY_NOTIFY_FOR_PROFILES)) {
                     
                     HashSet<String> hs = null;
                     List<String> altmp = null;
                     
                     if (stmp3 == null)
                         stmp3 = "";
                     else
                         altmp = Utils.tokenize(stmp3,
                                 Const.sNOTIFY_FOR_PROFILES_SEPARATOR);
                     
                     if (altmp != null) {
                         hs = new HashSet<String>();
                         for (int iii = 0; iii < altmp.size(); iii++) {
                             hs.add(altmp.get(iii));
                         }
                     }
                     
                     altmp = new ArrayList<String>();
                     altmp.add(sSELECT);
                     if (saProfiles != null) {
                         for (int iii = 0; iii < saProfiles.length; iii++) {
                             if (hs != null
                                     && hs.contains(saProfiles[iii]))
                                 continue;
                             altmp.add(saProfiles[iii]);
                         }
                     }
 %> <input type="text" class="txt" name="<%=stmp%>" value="<%=stmp3%>"
			size="30" maxlength="250"><br>
		&nbsp;&nbsp;&nbsp;&nbsp; <%=Utils.genHtmlSelect(stmp + "_profile",
                                            "class=\"txt\"", sSELECT, altmp,
                                            altmp)%> &nbsp;&nbsp; <a
			<%=Utils.genHRef("javascript:document.flows.op.value='6';"
                             + "document.flows.varname.value='"
                             + stmp
                             + "';tabber_right(4, '"
                             + response.encodeURL("Admin/flow_settings_edit.jsp")
                             + "',get_params(document.flows));",(altmp.size() < 2))%>>
		<if:message string="flow_settings_edit.link.addprof" /> </a> <%
     } else if (stmp.equals(Const.sSHOW_SCHED_USERS)) {
                     if (StringUtils.isEmpty(stmp3)
                             || ArrayUtils.contains(new Object[] {
                                     Const.sSHOW_NO, "nao", "no" },
                                     stmp3.toLowerCase())) {
                         stmp3 = Const.sSHOW_NO;
                     } else {
                         stmp3 = Const.sSHOW_YES;
                     }
 %> <select class="txt" name="<%=stmp%>">
			<option value="<%=Const.sSHOW_YES%>"
				<%=stmp3.equals(Const.sSHOW_YES) ? "selected"
                                            : ""%>><%=Const.sSHOW_YES%></option>
			<option value="<%=Const.sSHOW_NO%>"
				<%=stmp3.equals(Const.sSHOW_NO) ? "selected"
                                            : ""%>><%=Const.sSHOW_NO%>
			</option>
		</select> <%
     } else if (stmp.equals(Const.sSHOW_ASSIGNED_TO)) {
                     if (stmp3 == null || stmp3.equals("")
                             || stmp3.equalsIgnoreCase(Const.sSHOW_NO)
                             || stmp3.equalsIgnoreCase("nao")
                             || stmp3.equalsIgnoreCase("no")) {
                         stmp3 = Const.sSHOW_NO;
                     } else {
                         stmp3 = Const.sSHOW_YES;
                     }
 %> <select class="txt" name="<%=stmp%>">
			<option value="<%=Const.sSHOW_YES%>"
				<%=stmp3.equals(Const.sSHOW_YES) ? "selected"
                                            : ""%>><%=Const.sSHOW_YES%></option>
			<option value="<%=Const.sSHOW_NO%>"
				<%=stmp3.equals(Const.sSHOW_NO) ? "selected"
                                            : ""%>><%=Const.sSHOW_NO%>
			</option>
		</select> <%
     } else if (stmp.equals(Const.sRUN_MAXIMIZED)) {
                     if (stmp3 == null
                             || stmp3.equals("")
                             || stmp3.equalsIgnoreCase(Const.sRUN_MAXIMIZED_NO)
                             || stmp3.equalsIgnoreCase("nao")
                             || stmp3.equalsIgnoreCase("no")) {
                         stmp3 = Const.sRUN_MAXIMIZED_NO;
                     } else {
                         stmp3 = Const.sRUN_MAXIMIZED_YES;
                     }
 %> <select class="txt" name="<%=stmp%>">
			<option value="<%=Const.sRUN_MAXIMIZED_YES%>"
				<%=stmp3.equals(Const.sRUN_MAXIMIZED_YES) ? "selected" : ""%>>
			<%=Const.sRUN_MAXIMIZED_YES%></option>
			<option value="<%=Const.sRUN_MAXIMIZED_NO%>"
				<%=stmp3.equals(Const.sRUN_MAXIMIZED_NO) ? "selected" : ""%>><%=Const.sRUN_MAXIMIZED_NO%>
			</option>
		</select> <%
     } else if (stmp.equals(Const.sENABLE_HISTORY)) {
       if (StringUtils.isBlank(stmp3)
               || StringUtils.equalsIgnoreCase(stmp3, Const.sENABLE_HISTORY_NO)
               || StringUtils.equalsIgnoreCase(stmp3, messages.getString(Const.sENABLE_HISTORY_NO))) {
           stmp3 = Const.sENABLE_HISTORY_NO;
       } else if (StringUtils.equalsIgnoreCase(stmp3, Const.sENABLE_HISTORY_HIDDEN)
               || StringUtils.equalsIgnoreCase(stmp3, messages.getString(Const.sENABLE_HISTORY_HIDDEN))) {
           stmp3 = Const.sENABLE_HISTORY_HIDDEN;
       } else {
         stmp3 = Const.sENABLE_HISTORY_FULL;
       }
       %> 
    <select class="txt" name="<%=stmp%>">
		<option value="<%=Const.sENABLE_HISTORY_NO%>"
			<%=stmp3.equals(Const.sENABLE_HISTORY_NO) ? "selected" : ""%>>
			<%=messages.getString(Const.sENABLE_HISTORY_NO) %>
		</option>
		<option value="<%=Const.sENABLE_HISTORY_HIDDEN%>"
			<%=stmp3.equals(Const.sENABLE_HISTORY_HIDDEN) ? "selected" : ""%>>
			<%=messages.getString(Const.sENABLE_HISTORY_HIDDEN) %>
		</option>
		<option value="<%=Const.sENABLE_HISTORY_FULL%>"
			<%=stmp3.equals(Const.sENABLE_HISTORY_FULL) ? "selected" : ""%>>
			<%=messages.getString(Const.sENABLE_HISTORY_FULL) %>
		</option>
	</select> <%
     } else if (stmp.equals(Const.sPROCESS_LOCATION)) {
       if(null != fd && (FlowType.SUPPORT.equals(fd.getFlowType()) || FlowType.SEARCH.equals(fd.getFlowType()))) {
         %> <select class="txt" name="<%=stmp%>" disabled="disabled">
			<option value="<%=Const.sPROCESS_IN_SESSION%>" selected="selected"><%=Const.sPROCESS_IN_SESSION%></option>
		</select> <%
       } else {

                     if (stmp3 != null
                             && stmp3.equalsIgnoreCase(Const.sPROCESS_IN_SESSION)) {
                         stmp3 = Const.sPROCESS_IN_SESSION;
                     } else if (stmp3 != null
                             && stmp3.equalsIgnoreCase(Const.sPROCESS_IN_DB)) {
                         stmp3 = Const.sPROCESS_IN_DB;
                     } else {
                         // default
                         stmp3 = Const.sDEFAULT_PROCESS_LOCATION;
                     }
 %> <select class="txt" name="<%=stmp%>">
			<option value="<%=Const.sPROCESS_IN_SESSION%>"
				<%=stmp3.equals(Const.sPROCESS_IN_SESSION) ? "selected" : ""%>>
			<%=Const.sPROCESS_IN_SESSION%></option>
			<option value="<%=Const.sPROCESS_IN_DB%>"
				<%=stmp3.equals(Const.sPROCESS_IN_DB) ? "selected" : ""%>><%=Const.sPROCESS_IN_DB%>
			</option>
		</select> <%
       }
     } else if (stmp.equals(Const.sHASHED_DOCUMENT_URL)) {
                     final String yes = Const.sHASHED_DOCUMENT_URL_YES;
                     final String no = Const.sHASHED_DOCUMENT_URL_NO;
                     if (StringUtilities.isAnyOfIgnoreCase(stmp3,
                             new String[] { no, "nao", "no", "false" })) {
                         stmp3 = no;
                     } else if (StringUtilities.isAnyOfIgnoreCase(stmp3,
                             new String[] { yes, "sim", "yes", "true" })) {
                         stmp3 = yes;
                     } else {
                         // default value in const
                         stmp3 = Const.HASHED_DOCUMENT_URL ? yes : no;
                     }
 %> <select class="txt" name="<%=stmp%>">
		<option value="<%=yes%>" <%=stmp3.equals(yes) ? "selected" : ""%>><%=yes%></option>
		<option value="<%=no%>" <%=stmp3.equals(no) ? "selected" : ""%>><%=no%></option>
	</select> <%
     } else if (stmp.equals(Const.sGUEST_ACCESSIBLE)) {
                     final String yes = Const.sGUEST_ACCESSIBLE_YES;
                     final String no = Const.sGUEST_ACCESSIBLE_NO;
                     if (StringUtilities.isAnyOfIgnoreCase(stmp3,
                             new String[] { no, "nao", "no", "false",
                                     "0" })) {
                         stmp3 = no;
                     } else if (StringUtilities.isAnyOfIgnoreCase(stmp3,
                             new String[] { yes, "sim", "yes", "true",
                                     "1" })) {
                         stmp3 = yes;
                     } else {
                         // default value in const
                         stmp3 = Const.GUEST_ACCESSIBLE ? yes : no;
                     }
 %> <select class="txt" name="<%=stmp%>">
			<option value="<%=yes%>" <%=stmp3.equals(yes) ? "selected" : ""%>>
			<%=yes%></option>
			<option value="<%=no%>" <%=stmp3.equals(no) ? "selected" : ""%>><%=no%>
			</option>
		</select> <%
     } else if (stmp.equals(Const.sDEFAULT_STYLESHEET)) { %>
       <select class="txt" name="<%=stmp%>">
           <option value="" 
           	<% if(StringUtils.isBlank(stmp3)) { %> selected="selected" <% } %>>
           		<%=sSELECT %>
           </option>
       <%	Repository rep = BeanFactory.getRepBean();
       		RepositoryFile[] styleSheets = rep.listStyleSheets(userInfo);
       		for(RepositoryFile styleSheet : styleSheets) {
       %>
           <option value="<%=styleSheet.getName() %>" 
           	<% if(StringUtils.equals(stmp3, styleSheet.getName())) { %> selected="selected" <% } %>>
           		<%=StringEscapeUtils.escapeHtml(styleSheet.getName()) %>
           </option>
       <% } %>
       </select>
       <% } else if (stmp.equals(Const.sFLOW_MENU_ACCESSIBLE)) {
               if (stmp3 == null || stmp3.equals("") || stmp3.equalsIgnoreCase(Const.sFLOW_MENU_ACCESSIBLE_YES)
                       || stmp3.equalsIgnoreCase("sim") || stmp3.equalsIgnoreCase("yes")) {
                   stmp3 = Const.sRUN_MAXIMIZED_YES;
               } else {
                   stmp3 = Const.sRUN_MAXIMIZED_NO;
               }%>
       <select class="txt" name="<%=stmp%>">
           <option value="<%=Const.sFLOW_MENU_ACCESSIBLE_YES%>"
               <%=stmp3.equals(Const.sFLOW_MENU_ACCESSIBLE_YES) ? "selected" : ""%>>
               <%=Const.sFLOW_MENU_ACCESSIBLE_YES%>
           </option>
           <option value="<%=Const.sFLOW_MENU_ACCESSIBLE_NO%>"
               <%=stmp3.equals(Const.sFLOW_MENU_ACCESSIBLE_NO) ? "selected" : ""%>>
               <%=Const.sFLOW_MENU_ACCESSIBLE_NO%>
           </option>
       </select>
     <% } else {
         if (stmp3 == null) {
             stmp3 = "";
         }%>
         <input type="text" class="txt" name="<%=stmp%>" value="<%=StringEscapeUtils.escapeHtml(stmp3)%>" size="30" maxlength="100">
     <% }
     }%>
		</td>
	</tr>
	<% } // for %>
	<%
	if (mailConfig != null) {
		String hidden = mailConfig.isOn() ? "" : "style=\"display:none;\"";  
		String prefix = "<img src=\"images/pico2.png\"/>";
		%>
		<tr class="<%=(i++ % 2 == 0 ? "tab_row_even" : "tab_row_odd")%>">
			<td><%=messages.getString(MailConfig.configMessageKey(MailConfig.CONFIG_ONOFF))%></td>
			<td>Simple</td>
			<td width="50%" class="txt" align="left">&nbsp;&nbsp;&nbsp;&nbsp;
		  		<select class="txt" name="<%=MailConfig.CONFIG_ONOFF%>" onChange="fs_mailconfig(this.value);">
					<option value="<%=MailConfig.CONFIG_OPTION_YES%>" <%=mailConfig.isOn() ? "selected" : ""%>><%=messages.getString(MailConfig.configMessageKey(MailConfig.CONFIG_OPTION_YES_DESC))%></option>
					<option value="<%=MailConfig.CONFIG_OPTION_NO%>" <%=!mailConfig.isOn() ? "selected" : ""%>><%=messages.getString(MailConfig.configMessageKey(MailConfig.CONFIG_OPTION_NO_DESC))%></option>
				</select>
			</td>
		</tr>		

	 	<tr class="<%=(i++ % 2 == 0 ? "tab_row_even" : "tab_row_odd")%> mailconfig" <%=hidden %>>
			<td><%=prefix+messages.getString(MailConfig.configMessageKey(MailConfig.CONFIG_HOST))%></td>
			<td>Simple</td>
			<td width="50%" class="txt" align="left">&nbsp;&nbsp;&nbsp;&nbsp;
		  		<input type="text" class="txt" name="<%=MailConfig.CONFIG_HOST%>"
				value="<%=StringEscapeUtils.escapeHtml(mailConfig.getHost())%>" size="30" maxlength="100">
			</td>
		</tr>		
	 	<tr class="<%=(i++ % 2 == 0 ? "tab_row_even" : "tab_row_odd")%> mailconfig" <%=hidden %>>
			<td><%=prefix+messages.getString(MailConfig.configMessageKey(MailConfig.CONFIG_PORT))%></td>
			<td>Simple</td>
			<td width="50%" class="txt" align="left">&nbsp;&nbsp;&nbsp;&nbsp;
		  		<input type="text" class="txt" name="<%=MailConfig.CONFIG_PORT%>"
				value='<%=mailConfig.getPort()>-1?StringEscapeUtils.escapeHtml(String.valueOf(mailConfig.getPort())):""%>' size="30" maxlength="100">
			</td>
		</tr>		
	 	<tr class="<%=(i++ % 2 == 0 ? "tab_row_even" : "tab_row_odd")%> mailconfig" <%=hidden %>>
			<td><%=prefix+messages.getString(MailConfig.configMessageKey(MailConfig.CONFIG_USER))%></td>
			<td>Simple</td>
			<td width="50%" class="txt" align="left">&nbsp;&nbsp;&nbsp;&nbsp;
		  		<input type="text" class="txt" name="<%=MailConfig.CONFIG_USER%>"
				value="<%=StringEscapeUtils.escapeHtml(mailConfig.getUser())%>" size="30" maxlength="100">
			</td>
		</tr>		
	 	<tr class="<%=(i++ % 2 == 0 ? "tab_row_even" : "tab_row_odd")%> mailconfig" <%=hidden %>>
			<td><%=prefix+messages.getString(MailConfig.configMessageKey(MailConfig.CONFIG_PASS))%></td>
			<td>Simple</td>
			<td width="50%" class="txt" align="left">&nbsp;&nbsp;&nbsp;&nbsp;
		  		<input type="password" class="txt" name="<%=MailConfig.CONFIG_PASS%>" value="" size="30" maxlength="100"> 
			<% if (!ArrayUtils.isEmpty(mailConfig.getPass())) { 
			%> <span style="font-size: smaller;"><if:message string="flow_settings_edit.mail.passinfo" /></span> <% 
			}%>
			</td>
		</tr>		
	 	<tr class="<%=(i++ % 2 == 0 ? "tab_row_even" : "tab_row_odd")%> mailconfig" <%=hidden %>>
			<td><%=prefix+messages.getString(MailConfig.configMessageKey(MailConfig.CONFIG_INBOX))%></td>
			<td>Simple</td>
			<td width="50%" class="txt" align="left">&nbsp;&nbsp;&nbsp;&nbsp;
		  		<input type="text" class="txt" name="<%=MailConfig.CONFIG_INBOX%>"
				value="<%=StringEscapeUtils.escapeHtml(mailConfig.getInbox())%>" size="30" maxlength="100">
			</td>
		</tr>		
	 	<tr class="<%=(i++ % 2 == 0 ? "tab_row_even" : "tab_row_odd")%> mailconfig" <%=hidden %>>
			<td><%=prefix+messages.getString(MailConfig.configMessageKey(MailConfig.CONFIG_SUBSCRIBED_FOLDERS))%></td>
			<td>Simple</td>
			<td width="50%" class="txt" align="left">&nbsp;&nbsp;&nbsp;&nbsp;
		  		<input type="text" class="txt" name="<%=MailConfig.CONFIG_SUBSCRIBED_FOLDERS%>"
				value="<%=StringEscapeUtils.escapeHtml(mailConfig.getSubsFoldersAsString())%>" size="30" maxlength="100">
			</td>
		</tr>		
	 	<tr class="<%=(i++ % 2 == 0 ? "tab_row_even" : "tab_row_odd")%> mailconfig" <%=hidden %>>
			<td><%=prefix+messages.getString(MailConfig.configMessageKey(MailConfig.CONFIG_CHECK_INTERVAL))%></td>
			<td>Simple</td>
			<td width="50%" class="txt" align="left">&nbsp;&nbsp;&nbsp;&nbsp;
		  		<input type="text" class="txt" name="<%=MailConfig.CONFIG_CHECK_INTERVAL%>"
				value="<%=StringEscapeUtils.escapeHtml(String.valueOf(mailConfig.getCheckIntervalInSeconds()))%>" size="30" maxlength="100">
			</td>
		</tr>		
 		<tr class="<%=(i++ % 2 == 0 ? "tab_row_even" : "tab_row_odd")%> mailconfig" <%=hidden %>>
			<td><%=prefix+messages.getString(MailConfig.configMessageKey(MailConfig.CONFIG_SECURE))%></td>
			<td>Simple</td>
			<td width="50%" class="txt" align="left">&nbsp;&nbsp;&nbsp;&nbsp;
		  		<select class="txt" name="<%=MailConfig.CONFIG_SECURE%>">
					<option value="<%=MailConfig.CONFIG_OPTION_YES%>" <%=mailConfig.isSecure() ? "selected" : ""%>><%=messages.getString(MailConfig.configMessageKey(MailConfig.CONFIG_OPTION_YES_DESC))%></option>
					<option value="<%=MailConfig.CONFIG_OPTION_NO%>" <%=!mailConfig.isSecure() ? "selected" : ""%>><%=messages.getString(MailConfig.configMessageKey(MailConfig.CONFIG_OPTION_NO_DESC))%></option>
				</select>
			</td>
		</tr>
	<% } %>
	<%
	DMSConnectorUtils dmsUtils = DMSConnectorUtils.parse(fsa);
	if(dmsUtils != null) {
		String hidden = dmsUtils.isOn() ? "" : "style=\"display:none;\"";
		String prefix = "<img src=\"images/pico2.png\"/>";
		%>
	 	<tr class="<%=(i++ % 2 == 0 ? "tab_row_even" : "tab_row_odd")%>">
			<td><%=messages.getString(DMSConnectorUtils.configMessageKey(DMSConnectorUtils.CONFIG_DMS))%></td>
			<td>Simple</td>
			<td width="50%" class="txt" align="left">&nbsp;&nbsp;&nbsp;&nbsp;
		  		<select class="txt" name="<%=DMSConnectorUtils.CONFIG_DMS %>" onChange="toggleFlowSettingDisplays('dmsconfig', (this.value=='<%=DMSConnectorUtils.CONFIG_OPTION_YES %>'));">
					<option value="<%=DMSConnectorUtils.CONFIG_OPTION_YES %>" <%=dmsUtils.isOn() ? "selected" : "" %>><%=messages.getString(DMSConnectorUtils.configMessageKey(DMSConnectorUtils.CONFIG_OPTION_YES_DESC)) %></option>
					<option value="<%=DMSConnectorUtils.CONFIG_OPTION_NO %>" <%=dmsUtils.isOn() ? "" : "selected" %>><%=messages.getString(DMSConnectorUtils.configMessageKey(DMSConnectorUtils.CONFIG_OPTION_NO_DESC)) %></option>
				</select>
			</td>
		</tr>
	 	<tr class="<%=(i++ % 2 == 0 ? "tab_row_even" : "tab_row_odd")%> dmsconfig" <%=hidden %>>
			<td><%=prefix + messages.getString(DMSConnectorUtils.configMessageKey(DMSConnectorUtils.CONFIG_DMS_USER)) %></td>
			<td>Simple</td>
			<td width="50%" class="txt" align="left">&nbsp;&nbsp;&nbsp;&nbsp;
		  		<input type="text" class="txt" name="<%=DMSConnectorUtils.CONFIG_DMS_USER %>"
					value="<%=dmsUtils.getUserAsHtml() %>" size="30" maxlength="100">
			</td>
		</tr>
	 	<tr class="<%=(i++ % 2 == 0 ? "tab_row_even" : "tab_row_odd")%> dmsconfig" <%=hidden %>>
			<td><%=prefix + messages.getString(DMSConnectorUtils.configMessageKey(DMSConnectorUtils.CONFIG_DMS_PASS)) %></td>
			<td>Simple</td>
			<td width="50%" class="txt" align="left">&nbsp;&nbsp;&nbsp;&nbsp;
	  			<%-- <input type="password" class="txt" name="<%=DMSConnectorUtils.CONFIG_DMS_PASS %>"
	  				value="<%=dmsUtils.getPassForHtml() %>" size="30" maxlength="100">
	  			--%>	
			</td>
		</tr>
	<% } %>
	<%
	HotFolderConfig hotConfig = HotFolderConfig.parse(flowid, fsa);
	if(hotConfig != null) {
		String hidden = hotConfig.isOn() ? "" : "style=\"display:none;\"";
		String prefix = "<img src=\"images/pico2.png\"/>";
		%>	
	 	<tr class="<%=(i++ % 2 == 0 ? "tab_row_even" : "tab_row_odd")%>">
			<td><%=messages.getString(HotFolderConfig.configMessageKey("name"))%></td>
			<td>Simple</td>
			<td width="50%" class="txt" align="left">&nbsp;&nbsp;&nbsp;&nbsp;
		  		<select class="txt" name="<%=HotFolderConfig.ONOFF %>" onChange="toggleFlowSettingDisplays('hotfolderconfig', (this.value=='<%=HotFolderConfig.CONFIG_OPTION_YES %>'));">
					<option value="<%=HotFolderConfig.CONFIG_OPTION_YES %>" <%=hotConfig.isOn() ? "selected" : "" %>><%=messages.getString(HotFolderConfig.configMessageKey(HotFolderConfig.CONFIG_OPTION_YES_DESC)) %></option>
					<option value="<%=HotFolderConfig.CONFIG_OPTION_NO %>" <%=hotConfig.isOn() ? "" : "selected" %>><%=messages.getString(HotFolderConfig.configMessageKey(HotFolderConfig.CONFIG_OPTION_NO_DESC)) %></option>
				</select>
			</td>
		</tr>
		<tr class="<%=(i++ % 2 == 0 ? "tab_row_even" : "tab_row_odd")%> hotfolderconfig" <%=hidden %>>
			<td><%=prefix+messages.getString(HotFolderConfig.configMessageKey("subs_folders"))%></td>
			<td>Simple</td>
			<td width="50%" class="txt" align="left">&nbsp;&nbsp;&nbsp;&nbsp;
		  		<input type="text" class="txt" name="<%=HotFolderConfig.SUBS_FOLDERS%>"
				value="<%=StringEscapeUtils.escapeHtml(hotConfig.getSubsFoldersAsString())%>" size="30" maxlength="100">
			</td>
		</tr>		
	 	<tr class="<%=(i++ % 2 == 0 ? "tab_row_even" : "tab_row_odd")%> hotfolderconfig" <%=hidden %>>
			<td><%=prefix+messages.getString(HotFolderConfig.configMessageKey("search_depth"))%></td>
			<td>Simple</td>
			<td width="50%" class="txt" align="left">&nbsp;&nbsp;&nbsp;&nbsp;
		  		<input type="text" class="txt" name="<%=HotFolderConfig.SEARCH_DEPTH%>"
				value='<%=hotConfig.getDepth()>-1?StringEscapeUtils.escapeHtml(String.valueOf(hotConfig.getDepth())):""%>' size="30" maxlength="100">
			</td>
		</tr>		
		<tr class="<%=(i++ % 2 == 0 ? "tab_row_even" : "tab_row_odd")%> hotfolderconfig" <%=hidden %>>
			<td><%=prefix+messages.getString(HotFolderConfig.configMessageKey("doc_var"))%></td>
			<td>Simple</td>
			<td width="50%" class="txt" align="left">&nbsp;&nbsp;&nbsp;&nbsp;
		  		<input type="text" class="txt" name="<%=HotFolderConfig.DOC_VAR%>"
				value="<%=StringUtils.isEmpty(hotConfig.getDocVar()) ? "" : StringEscapeUtils.escapeHtml(hotConfig.getDocVar())%>" size="30" maxlength="100">
			</td>
		</tr>		
		<tr class="<%=(i++ % 2 == 0 ? "tab_row_even" : "tab_row_odd")%> hotfolderconfig" <%=hidden %>>
			<td><%=prefix+messages.getString(HotFolderConfig.configMessageKey("in_user"))%></td>
			<td>Simple</td>
			<td width="50%" class="txt" align="left">&nbsp;&nbsp;&nbsp;&nbsp;
		  		<input type="text" class="txt" name="<%=HotFolderConfig.IN_USER%>"
				value="<%=StringUtils.isEmpty(hotConfig.getUser()) ? "" : StringEscapeUtils.escapeHtml(hotConfig.getUser())%>" size="30" maxlength="100">
			</td>
		</tr>		
	<% } %>
	
</table>
</div>
<%
    } else {
%>
<div class="info_msg">
	<if:message string="flow_settings_edit.msg.noProperties" />
</div>
<%
    }
%>
<div class="button_box"><input class="regular_button_01"
	type="button" name="back"
	value="<%=messages.getString("button.back")%>"
	onClick="javascript:tabber_right(4, '<%=response.encodeURL("Admin/flow_settings.jsp")%>', '');" />
<input class="regular_button_01" type="button" name="export"
	value="<%=messages.getString("button.export")%>"
	onClick="W=window.open('<%=response.encodeURL("Admin/flow_settings_export.jsp?"
	    + DataSetVariables.FLOWID + "="
	    + flowid + "&ts="
	    + ts)%>','_export','menubar=no,status=no,scrollbars=no,width=1,height=1');W.focus()" />
<input class="regular_button_01" type="button" name="import"
	value="<%=messages.getString("button.import")%>"
	onClick="javascript:if (W != null) W.close();tabber_right(4, '<%=response
                                    .encodeURL("Admin/flow_settings_import.jsp")%>',get_params(document.flows));" />
<input class="regular_button_01" type="button" name="save"
	value="<%=messages.getString("button.save")%>"
	onClick="javascript:document.flows.op.value='2';if (W != null) W.close();tabber_right(4, '<%=response.encodeURL("Admin/flow_settings_edit.jsp")%>','' + get_params(document.flows));" />
<if:generateHelpBox context="flow_settings_edit" />
</div>

</form>
