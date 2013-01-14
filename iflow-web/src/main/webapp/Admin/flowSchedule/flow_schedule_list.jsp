<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c"%>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if"%>
<%@ include file="../../inc/defs.jsp"%>

<if:checkUserAdmin type="org">
    <div class="error_msg"><if:message string="admin.error.unauthorizedaccess" /></div>
</if:checkUserAdmin>

<c:set var="presentationMsg" value="${flowScheduleMsgListMsgToUser}" scope="request"/>
<c:set var="listOfEvents" value="${sessionScope.flow_events_list}" />

<form method="post" name="flow_schedule_list_form">
<h1 id="title_admin"><if:message string="flow_schedule.list.title" /></h1>

<c:if test="${not empty presentationMsg}">
    <div class="error_msg">
        <c:out value="${presentationMsg}" />
    </div>
</c:if>

<div class="table_inc">
<table class="item_list">
    <tr class="tab_header">
        <td align="center" ><if:message string="flow_schedule.list.field.scheduleName" /></td>
        <td align="center" ><if:message string="flow_schedule.list.field.flow" /></td>
        <td align="center" ><if:message string="flow_schedule.list.field.user" /></td>
        <td align="center" ><if:message string="flow_schedule.list.field.profile" /></td>
        <td align="center" ><if:message string="flow_schedule.list.field.start_date" /></td>
        <td align="center" ><if:message string="flow_schedule.list.field.next_execution" /></td>
        <td align="center" ><if:message string="flow_schedule.list.field.timeBetweenEvents" /></td>
        <td align="center" >&#160;</td>
    </tr>

    <c:choose>
        <c:when test="${not empty listOfEvents}">
            <c:forEach var="event" items="${listOfEvents}" varStatus="status">
                <tr class="${ status.index%2==0?'tab_row_even':'tab_row_odd' }">
                    <td><c:out value="${event.jobName}" /></td>
                    <td><c:out value="${event.flowName}" /></td>
                    <td><c:out value="${event.userAssigned}" /></td>
                    <td><c:out value="${event.userAssignedProfile}" /></td>
                    <td align="center"><c:out value="${event.startTimeJsp}" /></td>
                    <td align="center"><c:out value="${event.nextFireDateJsp}" /></td>
                    <td><c:out value="${event.formatedTimeBetweenExecutions}" /></td>
                    <td>
                        <a class="cell_button"
                            href="javascript:if (confirm('<if:message string="flow_schedule.list.msg.confirm.delete" />') ) tabber_right(4, '<%=response.encodeURL("Admin/flow_schedule_delete")%>','jobName=<c:out value="${event.jobName}" />&oper=deletejob' + get_params(document.flow_schedule_list_form) );" >
                            <if:message string="flow_schedule.list.link.remove" />
                       </a>
                    </td>
                </tr>
            </c:forEach>
        </c:when>
        <c:otherwise>
            <tr />
            <tr>
                <td align="center" colspan="7">
                    <b><if:message string="flow_schedule.list.msg.error.no_data_available"/></b>
                </td>
            </tr>
        </c:otherwise>
    </c:choose>

</table>

<fieldset class="submit">
    <input class="regular_button_01" type="button" name="add_flow_schedule"
        value="<if:message string="button.add"/>"
        onClick="tabber_right(4, '<%=response.encodeURL("Admin/flow_schedule_add")%>','ts=<%=ts%>');"/>
    </fieldset>
</div>
</form>
<if:generateHelpBox context="flow_schedule_list" />
<!-- TODO FIX HELP PAGE -->