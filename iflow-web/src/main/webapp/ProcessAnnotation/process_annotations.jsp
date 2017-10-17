<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/errorhandler.jsp"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c"%>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if"%>
<%@ include file="../inc/defs.jsp"%>
<%@ page import="pt.iflow.processannotation.ProcessAnnotationManagerBean"%>
<%@page import="pt.iflow.api.processannotation.*"%>
<%
  int flowid = -1;
  int pid = -1;
  int subpid = -1;
  try {
	// use of fdFormData defined in /inc/defs.jsp
    flowid = Integer.parseInt(fdFormData.getParameter("flowid"));
    pid = Integer.parseInt(fdFormData.getParameter("pid"));
    String sSubPid = fdFormData.getParameter("subpid");
    if (StringUtils.isEmpty(sSubPid)) {
      subpid = 1;
    } else {
      subpid = Integer.parseInt(sSubPid);
    }
  } catch (Exception e) {
    e.printStackTrace();
  }
  
  ProcessAnnotationManager pam = BeanFactory.getProcessAnnotationManagerBean();
  String sDeadline = pam.getProcessDeadline(userInfo,flowid,pid,subpid);
  ProcessComment comment = pam.getProcessComment(userInfo,flowid,pid,subpid);
  List<ProcessLabel> labels = pam.getLabelJoin(userInfo,flowid,pid,subpid);
  java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd");
  String from = fdFormData.getParameter("from");
  if (from == null) from = "";
  boolean hasAnnottations = ((sDeadline != null && !"".equals(sDeadline)) || 
		 (comment != null && comment.getComment() != null && !"".equals(comment.getComment())) ||
		 (	labels != null && labels.size() > 0));

  if ("forward".equals(from) && hasAnnottations){
    pam.deleteAnnotations(userInfo,flowid,pid,subpid); 
  }	
  List<ProcessComment> comments = pam.getProcessComment_History(userInfo, flowid, pid, subpid); 
%>

    <%if (!"forward".equals(from)){ %>
    <a href="javascript:menuonoff('verAnotacoes',1);" class="apt_link" id="butinfocol">Ver Comentários</a>
    <%} %>

    <div id="verAnotacoes" 
        style="display:none;position:absolute;z-index:1;background:none repeat scroll 0 0 #FFFFFF;border-color:#888888;
        border-style:solid;border-width:1px 1px 2px;text-align:left;top:-6px;right:-3px;width:40em;padding:1em">

		<div class="yui-skin-sam">
            <div class="container-close" onclick="javascript:menuonoff('verAnotacoes',0);"></div>
        </div>
    
    	<h2 style="background: none repeat scroll 0 0 transparent;border: medium none;color: black;font-size: 14px;padding-left: 10px;">Processo: <%=pid %></h2>
        <div class="apt_reg"><li>Histórico de Comentários</li>
            <div style="overflow: scroll;height: 150px">
                <ul id="spanComments">
                    <% for(int i=0; i < comments.size(); i++){%>
                        <li id="comment_history<%=i+1%>" title="<%=comments.get(i).getComment()%>">
                            <%try{ %>
                                <%=DateUtility.formatFormDate(userInfo, sdf.parse(comments.get(i).getDate()))%>
                            <%}catch(Exception e){%>
                                <%=comments.get(i).getDate()%>
                            <%} %>
                            <%= " - " + comments.get(i).getComment() + " - " + comments.get(i).getUser()%>
                        </li>
                    <%} %>
                </ul>
            </div>
        </div>

        
    </div>
    <%if (!"forward".equals(from)){ %>
  	<a href="javascript:menuonoff('anotacoes',1);" class="apt_link" id="butinfocol">Anota&ccedil;&atilde;o</a>
	<%}%>
	<div id="anotacoes" 
		style="<%if (!"forward".equals(from) || !hasAnnottations){%>display:none;position:absolute;z-index:1;border:1px solid gray;top:10px;<%} else {%>position:relative;z-index:0;bottom:10px;<%}%>background:none repeat scroll 0 0 #FFFFFF;
		text-align:left;right:0px;;width:40em;padding:1em;align:center">
		
		<div class="yui-skin-sam">
            <div class="container-close" onclick="javascript:menuonoff('anotacoes',0);"></div>
        </div>
        <%if (!"forward".equals(from)){ %>
            <h2 style="background: none repeat scroll 0 0 transparent;border: medium none;color: black;font-size: 14px;">Processo: <%=pid %></h2>
        <%} %>
        <%if ("forward".equals(from)){
          String forwardToLabelId = fdFormData.getParameter("forwardToLabelId");
          if (forwardToLabelId == null) {
            forwardToLabelId = "";
          }%>
        <input type="hidden" id="forwardToLabelId" value="<%=forwardToLabelId%>">
        <%}%>
        <%if ("forward".equals(from)){ %>
            <p style="padding-left:10px;12px;">Se o desejar poderá ainda enviar nota associada ao processo</p>
        <%} %>
		<ul class="apt_reg">
			<li style="font-size:12px;color:black;margin-left:0px;margin-bottom:10px"><if:message string="process_annotations.field.comment" /></li>
			<textarea id="comment" rows="4" cols="51"><%=comment.getComment()%></textarea>
			<input type="hidden" id="old_comment" value="<%=comment.getComment()%>">
		</ul>

		<hr class="apt_sep"/>
		<ul class="apt_reg">
			<li style="font-size:12px;color:black;margin-left:0px;margin-bottom:10px"><if:message string="process_annotations.field.labels" /></li>
			<% for(int i=0; i < labels.size(); i++){ 
			if(labels.get(i).getCheck()){%>
				<li>
				<input type="checkbox" onclick="managerLabels(<%=labels.get(i).getId()%>,true)" checked id="checkLabel_<%=labels.get(i).getId()%>" />
				<img src="AnnotationIconsServlet?label_name=<%=labels.get(i).getName()%>&ts='+<%=System.currentTimeMillis() %>+'" width="16px" height="16px"/>
				<%=labels.get(i).getName()%></li>
			<%}else{ %>
			    <li>
				<input type="checkbox" onclick="managerLabels(<%=labels.get(i).getId()%>,false)" id="checkLabel_<%=labels.get(i).getId()%>" />
				<img src="AnnotationIconsServlet?label_name=<%=labels.get(i).getName()%>&ts='+<%=System.currentTimeMillis() %>+'" width="16px" height="16px"/>
				<%=labels.get(i).getName()%></li>
			<%}
			} %>
		</ul>
		<hr class="apt_sep"/>
		<ul class="apt_reg">
			<li style="font-size:12px;color:black;margin-left:0px;margin-bottom:10px"><if:message string="process_annotations.field.deadline" /></li>
			<li style="margin-left:20px;margin-top:10px;"><input class="calendaricon" id="deadline" type="text" size="12" name="deadline" 
		  		value="<%=sDeadline%>" onmouseover="caltasks(this.id);this.onmouseover=null;"/>
			<img class="icon_clear" src="images/icon_delete.png" onclick="javascript:document.getElementById('deadline').value='';" />
			<input type="hidden" id="deadlineini" value="<%=sDeadline%>"></li>
		</ul>
		
		<%if (!"forward".equals(from)) {%>
		<br><input type="button" class="apt_regular_button" value="<if:message string="button.save"/>" id="save" onclick="saveProcessAnnotations('false');">
		<input type="button" class="apt_regular_button" value="<if:message string="button.cancel"/>" id="cancel" onclick="showAnnotations(<%=flowid%>,<%=pid%>,<%=subpid%>);">
		<%}%>	
	</div>    