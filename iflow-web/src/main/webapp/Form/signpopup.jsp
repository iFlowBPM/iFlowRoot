<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ include file="../inc/defs.jsp"%><%

pt.iflow.documents.PdfSampleImages pdfFile = new pt.iflow.documents.PdfSampleImages();

//Verificar se é para inserir
String oper = fdFormData.getParameter("oper");
int docid = Integer.parseInt(fdFormData.getParameter("docid"));

UserInfoInterface ui = (UserInfoInterface) session.getAttribute(Const.USER_INFO);

	if(oper.equals("In")){
		int x = Integer.parseInt(fdFormData.getParameter("x"));
		int y = Integer.parseInt(fdFormData.getParameter("y"));
		int pag = Integer.parseInt(fdFormData.getParameter("p"));
		String rub = fdFormData.getParameter("r");
		
		if(rub.equals("true"))
		pdfFile.insertImage((x*2), (y*2), pag, docid, true, ui);
		else
		pdfFile.insertImage((x*2), (y*2), pag, docid, false, ui);
	}
		
	
int totalpag = pdfFile.getNumPages(docid);
int [] tamAss = pdfFile.getTamAss(ui);
%>

<html>
<head>
<script language="JavaScript">

<%if(oper.equals("In")){%>
alert("O documento foi assinado com sucesso.\nAbrindo novamente o documento poderá ver a versão assinada.");
window.close();
<%}%>

function point_it(event){
	pos_x = event.offsetX?(event.offsetX):event.pageX-document.getElementById("pointer_div").offsetLeft;
	pos_y = event.offsetY?(event.offsetY):event.pageY-document.getElementById("pointer_div").offsetTop;
	document.getElementById("cross").style.left = (pos_x) ;
	document.getElementById("cross").style.top = (pos_y - document.getElementById("cross").offsetHeight ) ;
	document.getElementById("cross").style.visibility = "visible" ;
	document.pointform.form_x.value = document.getElementById("cross").offsetLeft;
	document.pointform.form_y.value = document.getElementById("cross").offsetTop;
}
function point_it2(event){
	document.pointform.form_x.value = document.getElementById("cross").offsetLeft;
	document.pointform.form_y.value = document.getElementById("cross").offsetTop;
}

function pagesIndex(to){
	pagactual = document.pointform.pagina.value;
	 if(to==1 && pagactual > 1){                            //anterior
		 document.pointform.pagina.value = pagactual-1;
		 var url = "background-image:url('../pdfsample?f=<%=docid%>&p="+document.pointform.pagina.value+"');width:297px;height:420px;";
		 
		 var myRater = document.getElementById("pointer_div");
		 myRater.removeAttribute("style");
		 myRater.setAttribute("style", url);
		 myRater.style.cssText = url;
	     }
	 if(to==2 && pagactual < <%=totalpag%>){                //proxima
	     document.pointform.pagina.value = pagactual-(-1);
	     var url = "background-image:url('../pdfsample?f=<%=docid%>&p="+document.pointform.pagina.value+"');width:297px;height:420px;";

		 var myRater = document.getElementById("pointer_div");
		 myRater.removeAttribute("style");
		 myRater.setAttribute("style", url);
		 myRater.style.cssText = url;
		 }
}

function assinar(){  
	 document.location.href=('signpopup.jsp?oper=In&docid=<%=docid%>&x='+document.pointform.form_x.value+'&y='+document.pointform.form_y.value+'&p='+document.pointform.pagina.value+'&r='+document.pointform.rubrica.checked);  
}

function loadImages(){
    var windowReference = window.open('loadimages.jsp?docid=<%=docid%>','CarregarImagens','toolbar=no,location=no,status=no,menubar=no,scrollbars=NO,resizable=NO,width=400,height=200');
    if (!windowReference.opener) windowReference.opener = self;
}
</script>

<script type="text/javascript" src="../javascript/jquery.min.js"></script>
<script type="text/javascript" src="../javascript/jquery-ui.min.js"></script>
<script type="text/javascript">
$(function() {
    $("#cross").draggable();
});
</script>



</head>
<body>
<form name="pointform" method="post">
	<div id="pointer_div" onClick="point_it(event)" style="background-image:url('../pdfsample?f=<%=docid%>&p=<%=totalpag%>');width:297px;height:420px;">
		<img 	src="../signsample?u=<%=ui.getUserId()%>" id="cross" class="drag-image" onClick="point_it2(event)"  style="position:relative;visibility:hidden;z-index:2;;width:<%=tamAss[0] %>px;height:<%=tamAss[1] %>px;">
	</div>
	<hr align="left" width="300">
	<div>
		<input type="button" value="<<" OnClick="pagesIndex(1)" />
		<input type="button" value=">>" OnClick="pagesIndex(2)" />
		&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp
		Página: <input type="text"  id="pagina" name="pagina" size="2" readonly="readonly" value="<%=totalpag%>"/> /<%=totalpag%>
	</div>
	<div>
	    <input type="checkbox" name="rubrica"> Rubricar	
		<input type="text" name="form_x" size="1" value="0" style="visibility:hidden;"/> 
		<input type="text" name="form_y" size="1" value="0" style="visibility:hidden;"/>
		&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp
		<input type="button" value="Assinar" OnClick="assinar()" />	
	</div>
	<div>
	 <input type="button" name="load"  value="Carregar" OnClick="loadImages()"/>
	</div>
</form> 
</body>
</html>

