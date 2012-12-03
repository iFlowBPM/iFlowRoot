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
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ include file = "../../inc/defs.jsp" %>
<%@ page import = "java.sql.*" %>
<%@ page import = "javax.sql.*" %>

<%
	String title = messages.getString("flow_state_procs.title");
String sPage = "Admin/ProcManagement/flow_states";

StringBuffer sbError = new StringBuffer();
String sESCOLHA = messages.getString("const.choose");
int nBAR_WIDTH = 150;

int flowid = -1;
try {
  flowid = Integer.parseInt(fdFormData.getParameter("flowid"));
  session.setAttribute(Const.SESSION_ATTRIBUTE_FLOWID, flowid);
} catch(Exception e) {
  try {
    flowid = Integer.parseInt(session.getAttribute(Const.SESSION_ATTRIBUTE_FLOWID).toString());
  } catch(Exception ex) {
    session.setAttribute(Const.SESSION_ATTRIBUTE_FLOWID, -1);
  }
}

%>
<%@ include file = "../auth.jspf" %>
<%


Flow flow = BeanFactory.getFlowBean();

int ntmp = 0;
int ntmp2 = 0;
String stmp = null;
String stmp2 = null;
List<String> altmp2 = null;
Map<String,String> hmStates = null;
String sFlowHtml = "";
IFlowData fd = null;
List<BlockInfo> alData = null;
int nTotalProcs = 0;

if (flow != null) {
  
  IFlowData[] fda = BeanFactory.getFlowHolderBean().listFlows(userInfo);

  if (fda == null) fda = new IFlowData[0];

  List<String> altmp = null;
  altmp = new ArrayList<String>(fda.length);
  altmp2 = new ArrayList<String>(fda.length);

  altmp.add("-1");
  altmp2.add(sESCOLHA);

  for (int i=0; i < fda.length; i++) {
    altmp.add(String.valueOf(fda[i].getId()));
    altmp2.add(fda[i].getName());
  }

  sFlowHtml = Utils.genHtmlSelect("flowid",
				  "onchange=\"javascript:tabber_right(4, '"+response.encodeURL("Admin/ProcManagement/flow_states.jsp")+"', get_params(document.flowstates));\"",
				  "" + flowid,
				  altmp,
				  altmp2);



  if (flowid > -1) {
    
    alData = flow.getFlowInfo(userInfo, flowid);
    
    if (alData != null) {
      // first get number of processes in each state from db
      DataSource dso = Utils.getDataSource();
      Connection db = null;
      Statement st = null;
      ResultSet rs = null;
      try {
	db = dso.getConnection();
	st = db.createStatement();
	// first check if process is closed
	rs = st.executeQuery("select state,count(pid) from flow_state where flowid="
			     + flowid + " group by state");

	hmStates = new HashMap<String,String>();
	while (rs.next()) {
	  stmp = rs.getString(1);
	  ntmp = rs.getInt(2);
	  nTotalProcs += ntmp;
	  hmStates.put(stmp,String.valueOf(ntmp));
	}
	rs.close();
	rs = null;
      }
      catch (Exception e) {
      }
      finally {
	Utils.closeDB(db,st,rs);
      }

      if (hmStates == null) {
      	sbError.append("<br>").append(messages.getString("flow_states.error.noProcessInfo"));
      }
    }
    else {
    	sbError.append("<br>").append(messages.getString("flow_states.error.noStateInfo"));
    }
  }
}




%>


<form name="flowstates" method="POST">

  <h1 id="title_admin"><%=title%></h1>

<% if (sbError != null && sbError.length() > 0) { %>
  <div class="error_msg">
    <%=sbError.toString()%>
  </div>
<% } %>

  <fieldset>
    <legend><%=messages.getString("flow_states.msg.selectFlow")%></legend>
    <ol>
      <li>
        <label for="flowid"><%=messages.getString("flow_states.field.flow")%></label>
        <%=sFlowHtml%>
      </li>
    </ol>
  </fieldset>
  <fieldset class="submit">
    <%-- input class="regular_button_01" type="button" name="show" value="<%=messages.getString("button.show")%>" onClick="javascript:tabber_right(4, 'Admin/ProcManagement/flow_states.jsp', get_params(document.flowstates));"/> --%>
  </fieldset>

<% if (alData != null) { %>
  <div class="table_inc">  
    <table class="item_list">
      <tr class="tab_header">
        <td><%=messages.getString("flow_states.field.id")%></td>
        <td><%=messages.getString("flow_states.field.type")%></td>
        <td><%=messages.getString("flow_states.field.interaction")%></td>
        <td><%=messages.getString("flow_states.field.outPorts")%></td>
        <td><%=messages.getString("flow_states.field.number")%></td>
      </tr>
  
      <% for (int i=0; i < alData.size(); i++) {
        BlockInfo bInfo = alData.get(i);
        stmp = bInfo.getId();
        List<Map<String,String>> altmp = bInfo.getOutblocks(); 
        ntmp = 0;
        try {
          ntmp = Integer.parseInt((String)hmStates.get(stmp));
        }
        catch (Exception e) {
        }
        ntmp2 = (nTotalProcs>0)?(ntmp*nBAR_WIDTH)/nTotalProcs:0;
      %>
            
        <tr class="<%=i%2==0?"tab_row_even":"tab_row_odd"%>" 
          <% if (ntmp > 0) { %>
             onclick="javascript:tabber_right(4, '<%=response.encodeURL("Admin/ProcManagement/flow_state_procs.jsp") %>', 'flowid=<%=flowid%>&state=<%=stmp%>');"
             style="cursor:pointer;" 
          <% } %>               
        >
          <td><%=stmp%></td>
          <td><%=bInfo.getType()%></td>
          <td><%=bInfo.isInteraction()?messages.getString("const.yes"):messages.getString("const.no")%></td>
          <td>
  <%   
        for (int ii=0; altmp != null && ii < altmp.size(); ii++) {
          if (ii > 0) { 
            out.print("<br>");
          }
          Map<String,String> outblock = altmp.get(ii);
          out.print(outblock.get("name"));
          out.print("&nbsp;/&nbsp;");
          out.print(outblock.get("connectedblockid"));
        }
  %>
          </td>
          <td>
            <table cellpadding="0" cellspacing="0" style="width:100%">
              <tr height="6"><td colspan="2"></td></tr>
              <tr height="8">
                <td class="gauge_full" style="<%=(ntmp2 > 0)?"border-right:none":"border:none"%>" width="<%=ntmp2%>">
                </td>
                <td class="gauge_empty" style="<%=(ntmp2 > 0)?"border-left:none":""%>" width="<%=nBAR_WIDTH-ntmp2%>">
                </td>
                <td rowspan="3" style="width:10em;">
                  (<%=ntmp%>/<%=nTotalProcs%>)              
                </td>
              </tr>
              <tr height="6"><td colspan="2"></td></tr>
            </table>
          </td>
        <tr>
  
      <% } %>  
    </table>
  </div>
<% } %>

</form>

<if:generateHelpBox context="flow_states"/>
