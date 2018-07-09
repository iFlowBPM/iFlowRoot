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

String dsType = request.getParameter(DSLoader.POOL_IMPL_CLASS);
if(StringUtils.isBlank(dsType)) {
  dsType = "-1";
}

if(StringUtils.equals(request.getParameter("oper"),"add")) {
  ServletUtils.sendEncodeRedirect(response, "datasources.jsp");
  return;
}

%>
<form method="post" name="formulario" id="formulario">
    <h1 id="title_admin"><%=title%></h1>
    <%
    if(Const.DISABLE_DATASOURCE_MANAGEMENT) {
    %>
    <div class="error_msg">
        A gest&atilde;o de base de dados est&aacute; desligada.
    </div>
    <%
    } else {
      DSLoader dsMan = pt.iflow.datasources.DSLoader.getInstance();

      Map<String,DataSourceEntry> dsTypes = dsMan.getRegisteredDataSources();
      Map<String,DriverEntry> driverTypes = dsMan.getRegisteredJdbcDrivers();
      List<Properties> pools = dsMan.getPoolsByOrganization("1");
      
      String onChangeDS = "javascript:tabber_right(4, '"+response.encodeURL("Admin/db/edit_ds.jsp")+"','oper=datasource&' + get_params(document.formulario));";
      String onChangeDrv = "javascript:tabber_right(4, '"+response.encodeURL("Admin/db/edit_ds.jsp")+"','oper=driver&' + get_params(document.formulario));";
          %>
<fieldset><legend></legend>
    <ol>
    <%-- TODO on change submit--%>
      <if:formSelect name="<%=DSLoader.POOL_IMPL_CLASS %>" edit="true" value="<%= dsType %>" label="DataSource Type" multiple="false" required="true" onchange="<%=onChangeDS%>">
         <if:formOption value="-1" label="Escolha" />
         <% for(Map.Entry<String,DataSourceEntry> e : dsTypes.entrySet()) { %>
         <if:formOption value='<%=e.getKey() %>' label='<%=e.getValue().getDescription() %>' />
         <% } %>
      </if:formSelect>
      <%if (!"-1".equals(dsType)) {
          DataSourceEntry dsEntry = dsTypes.get(dsType);
          String driverAttr = dsEntry.getDriverAttr();
          String urlAttr = dsEntry.getUrlAttr();
          
          Map<String,Class<?>> propsPool = dsMan.getDataSourceProperties(dsType);

          String urlValue = request.getParameter(urlAttr);
          String driverValue = request.getParameter(driverAttr);
          if(StringUtils.isBlank(driverValue)) {
            driverValue="-1";
          }
          String jdbcUrlTemplate = "";
          if(!StringUtils.equals("-1",driverValue)) {
            jdbcUrlTemplate = driverTypes.get(driverValue).getUrl();
          }
          
          String dbUsernameAttr = dsEntry.getUsernameAttr();
          String dbUsername = request.getParameter(dbUsernameAttr);
          if(StringUtils.isBlank(dbUsername)) dbUsername="";
          String dbPasswordAttr = dsEntry.getPasswordAttr();
          String dbPalavrapasse = request.getParameter(dbPasswordAttr);
          if(StringUtils.isBlank(dbPalavrapasse)) dbPalavrapasse="";
          
          String jndiName = request.getParameter(DSLoader.POOL_JNDI_NAME);
          if(StringUtils.isBlank(jndiName)) jndiName="";
          
          
          %>
         <if:formInput name="<%=DSLoader.POOL_JNDI_NAME %>" label="JNDI Name" type="text" value='<%=jndiName%>' edit="true" required="true" maxlength="250" />
         <if:formSelect name="<%=driverAttr %>" label="Jdbc Driver" value='<%= driverValue %>' edit="true" required="true" onchange="<%=onChangeDrv%>">
             <if:formOption value="-1" label="Escolha" />
             <% for(Map.Entry<String,DriverEntry> e : driverTypes.entrySet()) { %>
	         <if:formOption value='<%=e.getKey() %>' label='<%=e.getValue().getDescription() %>' />
	         <% } %>
         </if:formSelect>
         <li><label>Driver Class</label><c:out value='<%= "-1".equals(driverValue)?"":driverValue %>'></c:out></li>
         <if:formInput name='<%=urlAttr %>' label="DB Url" type="text" value='' edit="true" required="true" maxlength="1000" />
         <li><label>Sample URL</label><c:out value='<%= jdbcUrlTemplate %>'></c:out></li>
         <if:formInput name='<%=dbUsernameAttr %>' label="Username" type="text" value='<%=dbUsername %>' edit="true" required="false" maxlength="1000" />
         <if:formInput name='<%=dbPasswordAttr %>' label="Password" type="text" value='<%=dbPalavrapasse %>' edit="true" required="false" maxlength="1000" />
       </ol>
       <ol>
       <li><label>&nbsp;</label>
       <a href="#" onclick="document.getElementById('special_atts').style.display=''">Avançado</a>
       </li>
       </ol>
       <ol id="special_atts" style="display:none">
         <%
         for(Map.Entry<String,Class<?>> entry : propsPool.entrySet()) {
           String attrName = entry.getKey();
           String typeName = entry.getValue().getSimpleName();
           // Pool name and class are special
           if(DSLoader.POOL_IMPL_CLASS.equals(attrName)||DSLoader.POOL_JNDI_NAME.equals(attrName)) continue;
           
           // other special: Driver and URL
           if(driverAttr.equals(attrName)||urlAttr.equals(attrName)) continue;
           
           // other special: Username and Password
           if(dbUsernameAttr.equals(attrName)||dbPasswordAttr.equals(attrName)) continue;
           
           String attrValue = request.getParameter(attrName);
           if(null == attrValue) attrValue="";
           
           %>
         <if:formInput name="<%=attrName %>" label='<%=attrName%>' type="text" value='<%=attrValue%>' edit="true" required="false"  />
           <%
         }
     } %>
    </ol>
</fieldset>
    <% } %>
<fieldset class="submit">
   <input class="regular_button_01" type="button" name="back" value="<%=messages.getString("button.back")%>" onClick="javascript:tabber_right(4, '<%=response.encodeURL("Admin/db/datasources.jsp")%>');" />
<% if(!Const.DISABLE_DATASOURCE_MANAGEMENT) { %>
   <input class="regular_button_01" type="button" name="clear" value="<%=messages.getString("button.clear")%>" onClick="javascript:document.formulario.reset()" />
   <input class="regular_button_01" type="button" name="add" value="<%=messages.getString("button.add")%>" onClick="javascript:tabber_right(4, '<%=response.encodeURL("Admin/db/edit_ds.jsp")%>', 'oper=add&'+get_params(document.formulario));" />
<% } %>
</fieldset>
    
</form>
