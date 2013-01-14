<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ include file="inc/defs.jsp"%>
<%

UserInfoInterface ui = (UserInfoInterface) session.getAttribute(Const.USER_INFO);


pt.iflow.api.core.PassImage pa = BeanFactory.getPassImageManagerBean();

pa.teste(ui);


    
%>

<html>
<body>
<form name="pointform" method="post">

	<div>
		Controlo de versão do java para as imagens da assinatura na BD.
	</div>

</form> 
</body>
</html>