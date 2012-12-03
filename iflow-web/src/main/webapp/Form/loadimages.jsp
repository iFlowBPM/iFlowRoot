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
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c"%>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if"%>
<%@ include file="../inc/defs.jsp"%>
<%
  UserInfoInterface ui = (UserInfoInterface) session.getAttribute(Const.USER_INFO);
%>

<html>
	<head>
	<script type="text/javascript">
		function cancelar(){  
			this.close();
		}
		function confirmload() {
			return confirm('Ao reintroduzir as imagens da assinatura e/ou rúbrica está a eliminar as anteriores e passarão a ser utilizadas as imagens que reintroduzir. Pretende continuar?');
		}
	</script>
	</head>
	<body>
		<form name="pointform" action="<%=response.encodeURL("doloadimages.jsp")%>" method="POST" enctype="multipart/form-data">
		<div><br></br></div>
		<div>&nbsp&nbsp&nbspAssinatura:&nbsp&nbsp<input type="file" name="assFile" /></div>
		<div>&nbsp&nbsp&nbspRubrica:&nbsp&nbsp<input type="file" name="rubFile" /></div>
		<div><br></br></div>
		<div align="center">
			<input type="submit" value="Guardar" OnClick="return confirmload()"/>
			<input type="button" value="Cancelar" OnClick="cancelar()" /></div>
		</form>
	</body>
</html>

