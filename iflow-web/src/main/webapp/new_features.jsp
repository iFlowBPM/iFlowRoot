<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ include file = "inc/defs.jsp" %>

<%@ page import = "pt.iflow.features.NewFeaturesData" %>
<%@ page import = "pt.iflow.features.NewFeaturesManager" %>

<%
String title = "Novas Funcionalidades";
String sPage = "new_features";

StringBuffer sbError = new StringBuffer();

%>
<%@ include file = "inc/simpleTop.jspf" %>

<form name="request" method="post">
<input type="hidden" name="allversions" value=""/>
<table border="0" cellspacing="0" align="center">
	<tr><td valign="bottom">
<%
	String sAllVersions = fdFormData.getParameter("allversions");

Map<String,List<NewFeaturesData>> httmp = NewFeaturesManager.getNewFeatures(userInfo);

if (httmp != null && httmp.size() > 0) {

	Iterator<String> it = httmp.keySet().iterator();
	while (it.hasNext()) {
		String version = it.next();
%>
		&nbsp;</td></tr>
	<tr>
		<td align="center" class="v12bBRAdec">
	<table border="1" width="100%" cellspacing="0" cellpadding="4" align="center" bordercolor="#ffffff">
		<tr bgcolor="#4b6e98">
			<td align="center" class="v12bBRAdec" colspan="2">Vers&atilde;o&nbsp;<%=version%></td>
		</tr>
<%
		List<NewFeaturesData> altmp = httmp.get(version);
		for (int i=0; i<altmp.size(); i++) {
		    NewFeaturesData nfd = altmp.get(i);
%>
		<tr>
			<td align="left" class="v12bAZU" ><%=nfd.getFeature()%></td>
			<td align="left" class="v10AZU" ><%=nfd.getDescription()%></td>
		</tr>
<%
	}
%>
	</table>
<%
	if (sAllVersions == null || sAllVersions.trim().equals(""))
	break;
    }
    if (sAllVersions == null || sAllVersions.trim().equals("")) {
%>
		</td>
	</tr>
	<tr>
		<td align="left" class="v10bAZUdec">
			Pesquisa limitada &agrave; vers&atilde;o actual.<br>
			Clique <a href="javascript:document.request.allversions.value=1;document.request.submit()">aqui</a> para ver todas as vers&otilde;es.
<%
			}
		} else {
		%>
	<div class="txtError">N&atilde;o foram encontradas novas funcionalidades!</div>
<%
	}
%>
	</td></tr>
</table>
</form>


<%@ include file = "inc/simpleBottom.jspf" %>
