<%@ page language="java" contentType="text/plain; charset=UTF-8" pageEncoding="UTF-8"%>
<%
for(String name : pt.iflow.api.utils.Utils.getDataSources(null)) {
  out.println(name);
}
%>