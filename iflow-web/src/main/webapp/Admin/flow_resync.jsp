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
<%@ include file = "../inc/defs.jsp" %>

<%
	String title = "Administra&ccedil;&atilde;o - Re-Sincroniza&ccedil;&atilde;o Flows"; 
String sPage = "Admin/flow_resync";

StringBuffer sbError = new StringBuffer();
int flowid = -1;
%>
<%@ include file = "auth.jspf" %>
<%

Flow flow = BeanFactory.getFlowBean();

List<List<String>> alData = new ArrayList<List<String>>();
String[] saCols = { "Flow","File","Status","" };
List<String> altmp = null;
String stmp = null;
String params = null;

  IFlowData[] fda = BeanFactory.getFlowHolderBean().listFlows(userInfo);

  if (fda == null) fda = new IFlowData[0];

  for (int i=0; i < fda.length; i++) {
    altmp = new ArrayList<String>();

    if (fda[i].isOnline()) {
      stmp = "<a href=\"#\" disabled>";
    }
    else {
      stmp = "Admin/flow_resync_edit.jsp";
      
     params = DataSetVariables.FLOWID
	+ "=" + fda[i].getId() 
	+ "&flowname=" 
	+ fda[i].getName()
	+ "&ts=" + ts;
      
      stmp = response.encodeURL(stmp);
      
    stmp = "<a href=\"javascript:tabber_right(4, '" + response.encodeURL(stmp) + "', '" + params + "');\">";
    }

    altmp.add(stmp + fda[i].getName() + "</a>");
    altmp.add(stmp + fda[i].getFileName() + "</a>");
    if (fda[i].isOnline()) {
      altmp.add(stmp + "Online</a>");      
      altmp.add("-");
    }
    else {
      altmp.add(stmp + "Offline</a>");      
    altmp.add(stmp + "Edit</a>");
    }
    alData.add(altmp);
  }

%>


      <h1 id="title_admin">Flows &gt; Properties</h1>
<% if (sbError.length() > 0) { %>
      <div class="error_msg">
        <%=sbError.toString()%>
	  </div>
<% } %>
      <div class="table_inc">  
        <table width="100%" cellpadding="2">
          <tr class="tab_header">
<%
  for (int i=0; i < saCols.length; i++) {
%>
          <td>
            <%=saCols[i]%>
          </td>
<%
  }
%>
        </tr>
<%
if (alData.size() > 0) {
  for (int i=0; i < alData.size(); i++) {
    altmp = alData.get(i);
    out.println("        <tr>");
    out.println("        <tr class=\"" + (i%2==0?"tab_row_even":"tab_row_odd") + "\"");
    for (int j=0; j < altmp.size(); j++) {
      out.print("          <td>");
      out.print((String)altmp.get(j));
      out.println("</td>");
    }
    out.println("        </tr>");
  }
}
else {
%>
        <tr>
          <td colspan="<%=saCols.length%>">
            <div align="center" class="error_msg">There are no flows defined</div>
          </td>
        </tr>
<%
}
%>
       </table>
	</div>
	
