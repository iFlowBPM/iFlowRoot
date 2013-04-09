<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c"%>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if"%>
<%@ include file="../../inc/defs.jsp"%>

<%
final String dateFormat = "dd/MM/yyyy";
Calendar cal = Calendar.getInstance();
String sDate = Utils.date2string(cal.getTime(), dateFormat);
%>
<script type="text/javascript">
function updateSelectedProfileStr (profileName){
    var selectedProfileVar = document.getElementById('selectedProfileName');
    selectedProfileVar.value = profileName;
}

function updateSelectedUserName (userName){
    var selectedUserVar = document.getElementById('selectedUserName');
    selectedUserVar.value = userName;
}

function processEventRepeateTimeFrameDiv(ischecked){
    if (ischecked) {
        document.getElementById('eventRepeateTimeFrame').style.visibility="visible";
    } else {
        document.getElementById('eventRepeateTimeFrame').style.visibility="hidden";
    }
}

function validateTimeFormat(timeText){
    var r = new RegExp("^([0-1][0-9]|[2][0-4]):([0-5][0-9])");
    var timetest= r.test(timeText);
    if (!timetest){
        alert ('A hora ['+timeText+'], não tem um formato válido [HH:MM]');
    }
}

function validateFieldsToSubmit (){
    if (document.flow_schedule_add.form_add_flow.selectedIndex == 0){
        alert('<%=messages.getString("flow_schedule.add.field.flow_invalid")%>');
        return false;
    }
    if (document.flow_schedule_add.form_add_profile.selectedIndex == 0){
        alert('<%=messages.getString("flow_schedule.add.field.profile_invalid")%>');
        return false;
    }
    if (document.flow_schedule_add.form_add_user.selectedIndex == 0){
        alert('<%=messages.getString("flow_schedule.add.field.user_invalid")%>');
        return false;
    }
    if (document.flow_schedule_add.eventDate.value == ''){
        alert('<%=messages.getString("flow_schedule.add.field.start_date_not_empty")%>');
        return false;
    }
}
</script>

<if:checkUserAdmin type="org">
    <div class="error_msg">
        <if:message string="admin.error.unauthorizedaccess" />
    </div>
</if:checkUserAdmin>

<c:set var="selectedflow" value="${param.form_add_flow}" scope="request"/>
<c:set var="selecteduser" value="${param.form_add_user}" scope="request"/>
<c:set var="selectedprofile" value="${param.form_add_profile}" scope="request"/>

<c:if test="${empty selectedflow}" >
    <c:set var="selectedflow" value="0" />
</c:if>
<c:if test="${empty selecteduser}" >
    <c:set var="selecteduser" value="" />
</c:if>
<c:if test="${empty selectedprofile}" >
    <c:set var="selectedprofile" value="0" />
</c:if>

<form method="post" name="flow_schedule_add" id="flow_schedule_add">
    <input type="hidden" name="ts"                  id="ts"                     value="<%=ts %>" />
    <input type="hidden" name="selectedUserName"    id="selectedUserName"       value="" />
    <input type="hidden" name="selectedProfileName" id="selectedProfileName"    value="" />

    <h1 id="title_admin"><if:message string="flow_schedule.add.title" /></h1>

    <ol>
        <li>
            <c:if test="${not empty flowItems}">
                <if:formSelect name="form_add_flow" edit="true" labelkey="flow_schedule.add.field.flow" value="${selectedflow}" required="true">
                    <if:formOption value="0" labelkey="flow_schedule.add.field.combobox.default.text"/>
                    <c:forEach var="item" items="${flowItems}">
                        <if:formOption value="${item.comboId}" label="${item.comboName}"/>
                    </c:forEach>
                </if:formSelect>
            </c:if>
        </li>

        <li>
            <c:if test="${not empty profiles}">
                <if:formSelect name="form_add_profile" edit="true" labelkey="flow_schedule.add.field.profile" value="${selectedprofile}" required="true" onchange="javascript:updateSelectedProfileStr(form_add_profile.options[form_add_profile.selectedIndex].text);">
                    <if:formOption value="0" labelkey="flow_schedule.add.field.combobox.default.text"/>
                    <c:forEach var="item" items="${profiles}">
                        <if:formOption value="${item.comboId}" label="${item.comboName}"/>
                    </c:forEach>
                </if:formSelect>
            </c:if>
        </li>

        <li>
            <if:formInput name='form_add_user' labelkey="flow_schedule.add.field.user" type="text" value='${form_add_user}' edit="true" required="true" maxlength="20" />
        </li>

        <li>
            <if:formCalendar name="eventDate" edit="true" value="<%=sDate%>" labelkey="flow_schedule.add.field.start_date" required="true"/>
        </li>

        <li>
            <if:formInput name="eventTime" labelkey="flow_schedule.add.field.start_time" type="text" value="${eventTime}" edit="true" required="true"  size="5" maxlength="5" onblur="javascript:validateTimeFormat(this.value)"/>
        </li>
    </ol>
    
    <ol>
       <if:formInput name="isRepeatable" type="checkbox" value="false" labelkey="flow_schedule.add.field.checkbox.is_repeatable" edit="true" required="false" onchange="javascript:processEventRepeateTimeFrameDiv(this.checked);"/>
    </ol>
    <div id="eventRepeateTimeFrame" style="visibility:hidden">
        <ol>
            <li>
            <if:formInput name="eventInterval" type="text" value="" edit="true" required="true" labelkey="flow_schedule.add.field.repeat_time_frame" size="10"/>
            </li>
            <li>
            <if:formSelect name="form_add_time_frame_time_unit" edit="true" value="0" required="true">
                <c:forEach var="item" items="${timeIntervalsUnits}">
                    <if:formOption value="${item.comboId}" label="${item.comboName}"/>
                </c:forEach>
            </if:formSelect>
            </li>
        </ol>
    </div>

    <fieldset class="submit">
        <input class="regular_button_01" type="button" name="back"
            value="<%=messages.getString("button.back")%>"
            onClick="tabber_right(4, '<%=response.encodeURL("Admin/flow_schedule_list")%>','ts=<%=ts %>');"
        /> 
        <input class="regular_button_01" type="button" name="add" 
            value="<%=messages.getString("button.add")%>"
            onClick="tabber_right(4, '<%=response.encodeURL("Admin/flow_schedule_add_new")%>', get_params(document.flow_schedule_add));"
        />
    </fieldset>

</form>