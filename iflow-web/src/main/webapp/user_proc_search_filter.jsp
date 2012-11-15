<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ include file = "inc/defs.jsp" %><% 

String sflowid = fdFormData.getParameter("showflowid");
int flowid = -1;
try  {
  flowid = Integer.parseInt(sflowid);
} catch (Exception e) {}

boolean isAdmin = false;
String sTargetExtra = "style='display:none'"; 
if (userInfo.isOrgAdmin()) {
  isAdmin = true; 
} else if (flowid != -1) {
  Flow flow = BeanFactory.getFlowBean();
  if (flow.checkUserFlowRoles(userInfo, flowid, ""+FlowRolesTO.SUPERUSER_PRIV))
    isAdmin = true; 
}
boolean isSearchableByInterv = false;
if (flowid != -1) {
	FlowSetting fs = BeanFactory.getFlowSettingsBean().getFlowSetting(flowid, Const.sSEARCHABLE_BY_INTERVENIENT);
	if (fs != null && Const.sSEARCHABLE_BY_INTERVENIENT_YES.equals(fs.getValue())) {
	  isSearchableByInterv = true;
	}
}
%>
<% if (isAdmin || isSearchableByInterv) { %>
<p class="item" id="targetuser_label"><if:message string="user_procs_filtro.field.targetuserlabel"/>:</p>
<p class="item_indent" id="targetuser_body">
<if:formSelect name="targetUser" edit="true" value="__MY_PROCS__" noli="true">
	<if:formOption value="__MY_PROCS__" labelkey="user_procs_filtro.field.mytargetuser"/>
	<% if (isAdmin) { %>
	<if:formOption value="__ALL_PROCS__" labelkey="user_procs_filtro.field.alltargetuser"/>
	<% } %>
	<% if (isSearchableByInterv) { %>
	<if:formOption value="__INT_PROCS__" labelkey="user_procs_filtro.field.inttargetuser"/>
	<% } %>
</if:formSelect>
</p>
<% } %>

<p class="item" id="orderby_label"><if:message string="user_procs_filtro.field.orderbylabel"/>:</p>
<p class="item_indent" id="orderby_body">
<if:formSelect name="orderby" edit="true" value="2" noli="true">
	<if:formOption value="f.flowname" label="fluxo"/>
	<if:formOption value="p.pnumber" label="processo"/>
	<!-- <if:formOption value="4" label="estado"/> -->
	<if:formOption value="p.created" label="desde"/>
	<if:formOption value="p.creator" label="dono"/>
</if:formSelect>
<if:formSelect name="ordertype" edit="true" value="2" noli="true">
	<if:formOption value="asc" label="asc"/>
	<if:formOption value="desc" label="desc"/>
</if:formSelect>
</p>

<%
if(flowid == -1) return;

IFlowData flowData = BeanFactory.getFlowHolderBean().getFlow(userInfo, flowid, false);
Map<String,Integer> searchableFields = flowData.getIndexVars();
ProcessCatalogue catalog = flowData.getCatalogue();

if(searchableFields.isEmpty()) return;
%>
<p class="item"></p>
<%
Map<String,Object> hmConfig = (Map<String,Object>)session.getAttribute(Const.SESSION_USER_PROCS);
String[] idx = new String[Const.INDEX_COLUMN_COUNT];
int oldFlowId = -1;
if (hmConfig != null) {
  oldFlowId = (Integer)hmConfig.get(Const.SESSION_USER_PROCS_FLOWID);
  if (oldFlowId == flowid)
	idx = (String[])hmConfig.get(Const.SESSION_USER_PROCS_IDX);
}
if (oldFlowId != flowid) {
  for (int i = 0; i < Const.INDEX_COLUMN_COUNT; i++) idx[i] = "";
}
//"Sort" data
Map<Integer,String> invIdx = new java.util.TreeMap<Integer,String>();
for(String name : searchableFields.keySet()) {
  invIdx.put(searchableFields.get(name), name);
}
String [] fields = new String[Const.INDEX_COLUMN_COUNT];
String [] descrs = new String[Const.INDEX_COLUMN_COUNT];
for(Integer pos : invIdx.keySet()) {
  String name = invIdx.get(pos);
  String desc = catalog.getDisplayName(name);
  if(StringUtils.isEmpty(desc)) desc = name;
  fields[pos] = name;
  descrs[pos] = desc;
%>
<p class="item"><%= desc %></p>
<p class="item_indent"><input id="idx<%=pos%>" name="idx<%=pos%>" value="<%=idx[pos]%>" type="text" size="12" maxlength="1024"><img class="icon_clear" src="images/icon_delete.png" onclick="javascript:document.getElementById('idx<%=pos%>').value='';tabber_right(8, '<%= response.encodeURL("user_procs.jsp") %>', get_params(document.user_procs_filter));"/></p>
<% } %>
<input type="hidden" id="proc_search" name="proc_search" value="true">

