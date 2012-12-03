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
<%@ page import="pt.iflow.datasources.*"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c"%>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if"%>
<%@ include file="../../inc/defs.jsp"%>

<%-- So permitimos acesso ao sys adm --%>
<if:checkUserAdmin type="sys">
	<div class="error_msg"><if:message string="admin.error.unauthorizedaccess" /></div>
</if:checkUserAdmin>
<%
final int nLogFiles = 0;
final int nLogUpgradables = 1;

String sPage = "Admin/db/";

String title = messages.getString("admin-datasources.title");

%>
<form>
    <h1 id="title_admin"><%=title%></h1>

<fieldset><legend></legend>
    <div class="table_inc">
        <table class="item_list">
            <tr class="tab_header">
                <td>DataSources disponíveis</td>
            </tr>
          <% 
          String [] jndiNames = Utils.getDataSources(userInfo);
            for(int i = 0; i < jndiNames.length; i++) { 
          %>
            <tr class="<%=((i % 2) == 0) ? "tab_row_even" : "tab_row_odd" %>">
                <td><c:out value='<%=jndiNames[i] %>'></c:out></td>
            </tr>
            <% } %>
        </table>
    </div>
    
    
    <%
    if(!Const.DISABLE_DATASOURCE_MANAGEMENT) {
      DSLoader dsMan = pt.iflow.datasources.DSLoader.getInstance();
      Map<String,DataSourceEntry> dsTypes = dsMan.getRegisteredDataSources();
      Map<String,DriverEntry> driverTypes = dsMan.getRegisteredJdbcDrivers();
      List<Properties> pools = dsMan.getPoolsByOrganization("1");
      %>
    <div class="table_inc">
        <table class="item_list">
            <tr class="tab_header">
                <td>DataSource</td>
                <td>JNDI Name</td>
                <td>Jdbc Driver</td>
                <td>URL</td>
            </tr>
          <% 
            for(int i = 0; i < pools.size(); i++) { 
              Properties pool = pools.get(i);
              DataSourceEntry dsEntry = dsTypes.get(pool.getProperty(DSLoader.POOL_IMPL_CLASS));
              DriverEntry driverEntry = driverTypes.get(pool.getProperty(dsEntry.getDriverAttr()));
              String url = pool.getProperty(dsEntry.getUrlAttr());
              String jndiName = pool.getProperty(DSLoader.POOL_JNDI_NAME);
          %>
            <tr class="<%=((i % 2) == 0) ? "tab_row_even" : "tab_row_odd" %>">
                <td><c:out value='<%=dsEntry.getDescription() %>' /></td>
                <td><c:out value='<%=jndiName %>' /></td>
                <td><c:out value='<%=driverEntry.getDescription() %>' /></td>
                <td><c:out value='<%=url %>' /></td>
            </tr>
            <% } %>
        </table>
    </div>
    <div class="table_inc">
        <table class="item_list">
            <tr class="tab_header">
                <td>Implementações DataSource Registadas</td>
            </tr>
            <%int kk = 0; 
            for(Map.Entry<String,DataSourceEntry> e : dsTypes.entrySet()) { %>
            <tr class="<%=((kk++ % 2) == 0) ? "tab_row_even" : "tab_row_odd" %>">
                <td><c:out value='<%=e.getValue().getDescription() %>' /></td>
            </tr>
            <% } %>
        </table>
    </div>
    <div class="table_inc">
        <table class="item_list">
            <tr class="tab_header">
                <td>Drivers JDBC Registados</td>
            </tr>
            <%kk = 0; 
            for(Map.Entry<String,DriverEntry> e : driverTypes.entrySet()) { %>
            <tr class="<%=((kk % 2) == 0) ? "tab_row_even" : "tab_row_odd" %>">
                <td><c:out value='<%=e.getValue().getDescription() %>' /></td>
            </tr>
            <% } %>
        </table>
    </div>
    <% } %>
</fieldset>
<% if(!Const.DISABLE_DATASOURCE_MANAGEMENT) { %>
<fieldset class="submit">
   <input class="regular_button_01" type="button" name="add" value="<%=messages.getString("button.add")%>" onClick="javascript:tabber_right(4, '<%=response.encodeURL("Admin/db/edit_ds.jsp")%>', '');" />
</fieldset>
<% } %>
</form>
