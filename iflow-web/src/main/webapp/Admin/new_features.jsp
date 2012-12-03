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
<%@ page import = "pt.iflow.features.NewFeaturesData" %>
<%@ page import = "pt.iflow.features.NewFeaturesManager" %>
<if:checkUserAdmin type="org">
	<div class="error_msg"><if:message string="admin.error.unauthorizedaccess"></if:message></div>
</if:checkUserAdmin>

<%
String title = "Gestão das Novas Funcionalidades";
String sPage = "new_features";

StringBuffer sbError = new StringBuffer();

String sOp = fdFormData.getParameter("op");

if (sOp != null) {
    String sId = fdFormData.getParameter("id");
	String sVersion = fdFormData.getParameter("version");
	String sFeature = fdFormData.getParameter("feature");
	String sDescription = fdFormData.getParameter("description");

    try {
	    NewFeaturesData nfd = null;

    	if (sId == null || sId.equals("")) {
	    	nfd = new NewFeaturesData(sVersion, sFeature, sDescription);
	    } else {
		    nfd = new NewFeaturesData(Integer.parseInt(sId), sVersion, sFeature, sDescription);
		}

		if (sOp.equals("1")) {
	        NewFeaturesManager.insert(userInfo, nfd);
    	} else if (sOp.equals("2")) {
	        NewFeaturesManager.update(userInfo, nfd);
    	} else if (sOp.equals("3")) {
    	    NewFeaturesManager.delete(userInfo, nfd);
		}
	}
    catch (NumberFormatException e) {
    }
}


Map<String,List<NewFeaturesData>> httmp = NewFeaturesManager.getNewFeatures(userInfo);

%>
<script>
function hideRow(obj) {
	if (document.getElementById('inserir').style.display == '') {
		document.getElementById('inserir').style.display = 'none';
		document.images['img'].src='../images/sinal_mais.jpg';
	} else {
		document.getElementById('inserir').style.display = '';
		document.images['img'].src='../images/sinal_menos.jpg';
	}
}
function newVersion(select) {
	if (select.value!= "new") return;
	var catName = prompt('Por favor, insira o nome da nova versão:','');
	if (!catName) return;
	var newOption = document.createElement("option");
	newOption.value = catName;
	newOption.appendChild(document.createTextNode(catName));
	select.insertBefore(newOption, select.lastChild);
	select.selectedIndex = select.options.length-2;
}

</script>

<table border="0" cellspacing="0" align="center">
	<tr><td valign="bottom" colspan="2" class="v14bAZU">
		<a href="#" onclick="hideRow()"><img name="img" border="0" src="../images/sinal_mais.jpg"/></a>&nbsp;Inserir Nova Funcionalidade
		<form name="request" method="post">
		<input type="hidden" name="op" value="1"/>
		<table id="inserir" style="display: none" border="1" width="100%" cellspacing="0" cellpadding="4" align="center" bordercolor="#ffffff">
			<tr bgcolor="#4b6e98">
				<td align="left" class="v10bBRAdec" >
					Vers&atilde;o&nbsp;<select name="version" onChange="newVersion(this)">
<%
	if (httmp != null) {
		Iterator<String> it = httmp.keySet().iterator();
		while (it.hasNext()) {
			String version = it.next();
%>						<option value="<%=version%>"><%=version%></option>
<%
		}
	} else {
%>
		<option value=""></option>
<%
	}
%>
						<option value="new">Nova Vers&atilde;o</option>
					</select>
				</td>
			</tr>
			<tr bgcolor="#4b6e98">
				<td align="left" class="v10bBRAdec">
					Funcionalidade:<br><input type="text" name="feature"/>
				</td>
				<td align="left" class="v10bBRAdec">
					Descri&ccedil;&atilde;o:<br><textarea rows="2" name="description" cols="60"></textarea>
				</td>
				<td align="center" class="v10bBRAdec">
					<a title="Inserir" href="javascript:document.request.submit()"><img border="0" alt="actualizar" src="../images/setaon.gif"/></a>
				</td>
			</tr>
		</table>
		</form>
		</td>
	</tr>

	<tr><td valign="bottom">
<%
	if (httmp != null) {
		Iterator<String> it = httmp.keySet().iterator();
		while (it.hasNext()) {
			String version = it.next();
%>
		&nbsp;</td></tr>
	<tr>
		<td align="center" class="v10bBRAdec">
	<table border="1" width="100%" cellspacing="0" cellpadding="4" align="center" bordercolor="#ffffff">
		<tr bgcolor="#4b6e98">
			<td align="center" class="v10bBRAdec">Vers&atilde;o&nbsp;<%=version %></td>
		</tr>
<%
			List<NewFeaturesData> altmp = httmp.get(version);
			for (int i=0; i<altmp.size(); i++) {
			    NewFeaturesData nfd = altmp.get(i);
%>
		<tr bgcolor="#4b6e98">
			<td align="center" class="v10bBRAdec"><%=nfd.getFeature() %></td>
			<td align="center" class="v10bBRAdec">
				<form name="request_<%=nfd.getId() %>" method="post">
					<input type="hidden" name="op" value=""/>
					<input type="hidden" name="id" value="<%=nfd.getId() %>"/>
					<input type="hidden" name="feature" value="<%=nfd.getFeature() %>"/>
					<input type="hidden" name="version" value="<%=version %>"/>
					<textarea rows="2" name="description" cols="60"><%=nfd.getDescription() %></textarea>
				</form>
			</td>
			<td align="center" class="v10bBRAdec">
				<a title="Actualizar" onClick="document.request_<%=nfd.getId() %>.op.value='2';document.request_<%=nfd.getId() %>.submit();"><img border="0" alt="actualizar" src="../images/setaon.gif"/></a>
				&nbsp;&nbsp;&nbsp;
				<a title="Apagar" onClick="document.request_<%=nfd.getId() %>.op.value='3';document.request_<%=nfd.getId() %>.submit();"><img border="0" alt="apagar" src="../images/limpar_form.gif"/></a>
			</td>
		</tr>
<%
			}
%>
	</table>
<%
    	}
    }
%>
	</td></tr>
</table>

<table align="center" border="0">
	<tr><td height="10">&nbsp;</td></tr>
	<tr>
	<td align="center" class="v10bAZUdec">
		<a href="<%= response.encodeURL(sURL_PREFIX + "main.jsp") %>?ts=<%= ts %>"><img border="0" src="../images/seta_back.gif" width="19" height="19"><br>Voltar</a>
	</td>
	</tr>
</table>


