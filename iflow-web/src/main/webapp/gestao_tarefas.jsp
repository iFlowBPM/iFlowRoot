<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ include file = "inc/defs.jsp" %>
<%@ page import = "java.sql.*" %>
<%@ page import = "javax.sql.*" %>

<%
  String title = messages.getString("gestao_tarefas.title");
  String sPage = "gestao_tarefas";
  String stmp = null;
  boolean bfirst = true;
  StringBuffer sbtmp = null;
  StringBuffer sbError = new StringBuffer();
  String sStatusDelegated = messages.getString("gestao_tarefas.delegation.status.delegated");
  String sStatusPending = messages.getString("gestao_tarefas.delegation.status.pending");

  HashSet<Integer> hsSuperuser = new HashSet<Integer>();
  HashSet<Integer> hsCreate = new HashSet<Integer>();
  HashSet<Integer> hsWrite = new HashSet<Integer>();
  HashSet<Integer> hsRead = new HashSet<Integer>();

  Flow flow = BeanFactory.getFlowBean();
  try {
    FlowRolesTO[] fra = flow.getUserFlowRoles(userInfo, -1);
    for (int i = 0; null != fra && i < fra.length; i++) {
      if (fra[i].hasPrivilege(FlowRolesTO.SUPERUSER_PRIV)) {
        hsSuperuser.add(new Integer(fra[i].getFlowid()));
      }
      if (fra[i].hasPrivilege(FlowRolesTO.CREATE_PRIV)) {
        hsCreate.add(new Integer(fra[i].getFlowid()));
      }
      if (fra[i].hasPrivilege(FlowRolesTO.WRITE_PRIV)) {
        hsWrite.add(new Integer(fra[i].getFlowid()));
      }
      if (fra[i].hasPrivilege(FlowRolesTO.READ_PRIV)) {
        hsRead.add(new Integer(fra[i].getFlowid()));
      }
    }
  } catch (Exception e) {
    Logger.errorJsp(login, sPage, "exception: " + e.getMessage());
    e.printStackTrace();
    sbError.append("<br>").append(messages.getString("gestao_tarefas.error.noflow"));
  }

  IFlowData[] fda = null;
  fda = BeanFactory.getFlowHolderBean().listFlowsOnline(userInfo, FlowType.WORKFLOW);
  Map<Integer, String> hmfda = new HashMap<Integer, String>();
  for (int i = 0; fda != null && i < fda.length; i++) {
    Integer flowid = new Integer(fda[i].getId());
    hmfda.put(flowid, fda[i].getName());
  }
%>

<script language="javascript">
  function toggleAll(value) {
    for ( var i = 0; i < document.flows.elements.length; i++) {
      document.flows.elements[i].checked = value;
    }
  }

  function getFlows() {
    var flows = "";
    for ( var i = 0; i < document.flows.elements.length; i++) {
      if (document.flows.elements[i].checked == true
          && document.flows.elements[i].value != 'Editar') {
        flows = flows + "," + document.flows.elements[i].value;
      }
    }
    if (flows.length > 0) {
      document.flows.selected.value = flows;
      document.flows.submit();
    } else {
      alert("Tem de seleccionar pelo menos um fluxo");
    }
  }

  function hideRow(obj, img) {
    if (document.getElementById(obj).style.display == '') {
      document.getElementById(obj).style.display = 'none';
      document.images[img].src = 'images/sinal_mais.jpg';
    } else {
      document.getElementById(obj).style.display = '';
      document.images[img].src = 'images/sinal_menos.jpg';
    }
  }
</script>

<%
  Set<String> hsDelegatedFlows = new HashSet<String>();

  DataSource ds = null;
  Connection db = null;
  Statement st = null;
  ResultSet rs = null;

  boolean toggle = false;

  final int APPROVE = 1;
  final int TERMINATE = 2;
  final int REQUEST = 3;
  final int REASSIGN = 4;
  int nAction = -1;

  String sAction = fdFormData.getParameter("action");

  if ("approve".equals(sAction)) {
    nAction = APPROVE;
  } else if ("reject".equals(sAction)) {
    nAction = TERMINATE;
  } else if ("request".equals(sAction)) {
    nAction = REQUEST;
  } else if ("reassign".equals(sAction)) {
    nAction = REASSIGN;
  } else {
    nAction = APPROVE; // default
  }
%>

<h1 id="title_delegations">
<%if (nAction == APPROVE) {%>
  <%=(title + " > " + messages.getString("gestao_tarefas.section.title.approve"))%>
<%} else if (nAction == TERMINATE) {%>
  <%=(title + " > " + messages.getString("gestao_tarefas.section.title.terminate"))%>
<%} else if (nAction == REQUEST) {%>
  <%=(title + " > " + messages.getString("gestao_tarefas.section.title.request"))%>
<%} else if (nAction == REASSIGN) {%>
  <%=(title + " > " + messages.getString("gestao_tarefas.section.title.reassign"))%>
<%}%>
</h1>
  <div class="table_inc">  
        
  <%if (nAction == APPROVE) {%>
        
    <form name="requests" method="post">
      <input type="hidden" name="owner" value=""/>
      <input type="hidden" name="ahid" value=""/>
      <input type="hidden" name="cb_accept" value=""/>
      <input type="hidden" name="cb_reject" value=""/>
      <table class="itemlist">

    <%bfirst = true;
      try {
        ds = Utils.getDataSource();
        db = ds.getConnection();
        st = db.createStatement();

        sbtmp = new StringBuffer();
        sbtmp.append("select * from activity_hierarchy where pending=1 ");
        sbtmp.append("and userid='");
        sbtmp.append(userInfo.getUtilizador()).append("'");
        rs = st.executeQuery(sbtmp.toString());

        String sUrl = Const.APP_URL_PREFIX + "confirmar_agendamento.jsp";
        String sParams = "id=";

        toggle = false;
        while (rs.next()) {
          Integer flowid = new Integer(rs.getInt("flowid"));
          if (bfirst) {%>

        <tr class="tab_header">
          <td><input type="checkbox" name="cb_ignore" value="cb_ignore" onclick="javascript:toggle_all_cb(this, this.form.cb_flowid)" /></td>
          <td><%=messages.getString("gestao_tarefas.field.owner")%></td>
          <td><%=messages.getString("gestao_tarefas.field.flow")%></td>
          <td><%=messages.getString("gestao_tarefas.field.enddate")%></td>
          <td></td>
          <td></td>
        </tr>

         <%
            bfirst = false;
          }
          String akey = rs.getString("acceptkey");
          String rkey = rs.getString("rejectkey");
          String ownerid = rs.getString("ownerid");
		  Integer id = rs.getInt("hierarchyid");
		  
          sParams = sParams + id + "&owner=" + ownerid + "&dkey=";
          %>
        <tr class="<%=(toggle = !toggle) ? "tab_row_even" : "tab_row_odd"%>">
          <td><input type="checkbox" name="cb_flowid" value="<%=id%>;<%=ownerid%>;<%=akey%>;<%=rkey%>" /></td>
          <td><%=ownerid%></td>
          <td><%=hmfda.get(flowid)%></td>
          <td><%=DateUtility.formatTimestamp(userInfo, rs.getTimestamp("expires"))%></td>
          <td><a class="cell_button" href="javascript:tabber_right(5, '<%=sUrl%>', '<%=sParams + akey%>&' + get_params(document.requests));"><%=messages.getString("button.accept")%></a></td>
          <td><a class="cell_button" href="javascript:tabber_right(5, '<%=sUrl%>', '<%=sParams + rkey%>&' + get_params(document.requests));"><%=messages.getString("button.refuse")%></a></td>
        </tr>
      <%}%>
      </table>
      <%pt.iflow.api.db.DatabaseInterface.closeResources(db, st, rs);
      } catch (Exception e) {
        db.rollback();
        e.printStackTrace();
        pt.iflow.api.db.DatabaseInterface.closeResources(db, st, rs);
      }
      if (bfirst) {%>
      <table>
        <tr><td class="error_msg"><%=messages.getString("gestao_tarefas.msg.nodeleg")%></td></tr>
      </table>
      <%} else {%>
      <div class="button_box"> 
        <input class="regular_button_03" type="button" name="add" value="<if:message string="button.accept_checked"></if:message>" onClick="javascript:document.requests.cb_accept.value=true;tabber_right(5, '<c:url value="confirmar_agendamento.jsp"></c:url>','ts=<%=ts%>&' + get_params(document.requests));"></input>
        <input class="regular_button_03" type="button" name="add" value="<if:message string="button.refuse_checked"></if:message>" onClick="javascript:document.requests.cb_reject.value=true;tabber_right(5, '<c:url value="confirmar_agendamento.jsp"></c:url>','ts=<%=ts%>&' + get_params(document.requests));"></input>
      </div>
      <%}%>

    </form>
  <%} else if (nAction == TERMINATE) {%>
    <form name="terminar" method="post">
      <input type="hidden" name="id" value=""/>
      <input type="hidden" name="op" value=""/>
      <input type="hidden" name="cb_terminate" value=""/>
      <table class="itemlist">
    <%
      try {
        ds = Utils.getDataSource();
        db = ds.getConnection();
        st = db.createStatement();

        String owner = userInfo.getUtilizador();
        bfirst = true;

        // todas as tarefas mesmo dele que agendou
        sbtmp = new StringBuffer();
        sbtmp.append("select * from activity_hierarchy ");
        sbtmp.append("where parentid=0 and ownerid='");
        sbtmp.append(owner).append("'");
        rs = st.executeQuery(sbtmp.toString());

        toggle = false;
        while (rs.next()) {
          Integer flowid = new Integer(rs.getInt("flowid"));
          int id = rs.getInt("hierarchyid");
          String estado = null;

          if (rs.getInt("pending") == 0) {
            estado = sStatusDelegated;
          } else {
            estado = sStatusPending;
          }

          if (bfirst) {
      %>
        <tr class="tab_header">
          <td><input type="checkbox" name="cb_ignore" value="cb_ignore" onclick="javascript:toggle_all_cb(this, this.form.cb_flowid)" /></td>
          <td><%=messages.getString("gestao_tarefas.field.owner")%></td>
          <td><%=messages.getString("gestao_tarefas.field.flow")%></td>
          <td><%=messages.getString("gestao_tarefas.field.teamworker")%></td>
          <td><%=messages.getString("gestao_tarefas.field.status")%></td>
          <td></td>
        </tr>
        <%
            bfirst = false;
          }
          hsDelegatedFlows.add(flowid.toString() + owner);
        %>
        <tr class="<%=(toggle = !toggle) ? "tab_row_even" : "tab_row_odd"%>">
          <td><input type="checkbox" name="cb_flowid" value="<%=id%>" /></td>
          <td><%=owner%></td>
          <td><%=hmfda.get(flowid)%></td>
          <td><%=rs.getString("userid")%></td>
          <td><%=estado%></td>
          <td>
            <a class="cell_button" href="javascript:document.terminar.id.value=<%=id%>;tabber_right(5, '<%=response.encodeURL("terminar_agendamento.jsp")%>', 'ts=<%=ts%>&' + get_params(document.terminar));"><%=messages.getString("button.terminate")%></a>
          </td>
        </tr>
      <%
        }
        rs.close();

        // todas as tarefas que lhe agendaram, que ele agendou
        sbtmp = new StringBuffer();
        sbtmp.append("select * from activity_hierarchy where ");
        sbtmp.append("parentid in (select hierarchyid from activity_hierarchy where ");
        sbtmp.append("userid='").append(userInfo.getUtilizador());
        sbtmp.append("' and ownerid <> '").append(userInfo.getUtilizador());
        sbtmp.append("')");
        rs = st.executeQuery(sbtmp.toString());

        toggle = false;
        while (rs.next()) {
          Integer flowid = new Integer(rs.getInt("flowid"));
          int id = rs.getInt("hierarchyid");
          String estado = null;
          owner = rs.getString("ownerid");

          if (rs.getInt("pending") == 0) {
            estado = sStatusDelegated;
          } else {
            estado = sStatusPending;
          }

          if (bfirst) {
        %>
        <tr class="tab_header">
          <td><%=messages.getString("gestao_tarefas.field.owner")%></td>
          <td><%=messages.getString("gestao_tarefas.field.flow")%></td>
          <td><%=messages.getString("gestao_tarefas.field.teamworker")%></td>
          <td><%=messages.getString("gestao_tarefas.field.status")%></td>
          <td></td>
        </tr>
          <%
            bfirst = false;
          }
          hsDelegatedFlows.add(flowid.toString() + owner);
          %>
        <tr class="<%=(toggle = !toggle) ? "tab_row_even" : "tab_row_odd"%>">
          <td><%=owner%></td>
          <td><%=hmfda.get(flowid)%></td>
          <td><%=rs.getString("userid")%></td>
          <td><%=estado%></td>
          <td><a class="cell_button" href="javascript:document.terminar.id.value=<%=id%>;tabber_right(5, '<%=response.encodeURL("terminar_agendamento.jsp")%>', 'ts=<%=ts%>&' + get_params(document.terminar));"><%=messages.getString("button.terminate")%></a></td>
        </tr>
      <%
        }
        pt.iflow.api.db.DatabaseInterface.closeResources(db, st, rs);
      %>
      </table>
      <%
	
      } catch (Exception e) {
        db.rollback();
        e.printStackTrace();
        pt.iflow.api.db.DatabaseInterface.closeResources(db, st, rs);
      }

      if (bfirst) {%>
      <table>
        <tr><td class="info_msg"><%=messages.getString("gestao_tarefas.msg.nodelegothers")%></td></tr>
      </table>
      <%} else {%>
      <div class="button_box"> 
        <input class="regular_button_03" type="button" name="add" value="<if:message string="button.terminate_checked"></if:message>" onClick="javascript:document.terminar.cb_terminate.value=true;tabber_right(5, '<c:url value="terminar_agendamento.jsp"></c:url>','ts=<%=ts%>&' + get_params(document.terminar));"></input>
      </div>
      <%}%>

    </form>
  <%} else if (nAction == REQUEST) {%>
    <form name="requisitar" method="post">
      <input type="hidden" name="flowid" value="-1"/>
      <input type="hidden" name="ownerid" value=""/>
      <input type="hidden" name="cb_request" value=""/>
      <input type="hidden" name="action" value="<%=sAction%>"/>
      <table class="itemlist">
      <%try {
          ds = Utils.getDataSource();
          db = ds.getConnection();
          st = db.createStatement();

          bfirst = true;
          String ownerid = userInfo.getUtilizador();

          for (int i = 0; (fda != null) && (i < fda.length); i++) {
            Integer flowid = new Integer(fda[i].getId());
            String tmpKey = flowid.toString() + ownerid;

            // se o fluxo ja esta delegado nao pode ser delegado outra vez
            if (hsDelegatedFlows.contains(tmpKey)) {
              continue;
            }

            // se nao tem permissoes
            if (!hsSuperuser.contains(flowid) && !hsCreate.contains(flowid) && !hsWrite.contains(flowid) && 
                !hsRead.contains(flowid)) {
              continue;
            }

            if (bfirst) {
      %>
        <tr class="tab_header">
          <td><input type="checkbox" name="cb_ignore" value="cb_ignore" onclick="javascript:toggle_all_cb(this, this.form.cb_flowid)" /></td>
          <td><%=messages.getString("gestao_tarefas.field.owner")%></td>
          <td><%=messages.getString("gestao_tarefas.field.flow")%></td>
          <td></td>
        </tr>
            <%
              bfirst = false;
            }
            %>
        <tr class="<%=i % 2 == 0 ? "tab_row_even" : "tab_row_odd"%>">
          <td><input type="checkbox" name="cb_flowid" value="<%=fda[i].getId()%>;<%=ownerid%>" /></td>
          <td><%=ownerid%></td>
          <td><%=fda[i].getName()%></td>
          <td><a class="cell_button" href="javascript:document.requisitar.flowid.value=<%=flowid.intValue()%>;document.requisitar.ownerid.value='<%=ownerid%>';tabber_right(5, '<%=response.encodeURL("requisitar_agendamento.jsp")%>', 'ts=<%=ts%>&' + get_params(document.requisitar));"><%=messages.getString("button.request")%></a></td>
        </tr>
        <%
          }

          /* as tarefas que lhe foram delegadas */
          sbtmp = new StringBuffer();
          sbtmp.append("select * from activity_hierarchy where pending=0 ");
          sbtmp.append("and slave=1 and userid='");
          sbtmp.append(userInfo.getUtilizador()).append("'");
          rs = st.executeQuery(sbtmp.toString());

          while (rs.next()) {
            Integer flowid = new Integer(rs.getInt("flowid"));
            String tmpKey = flowid.toString() + rs.getString("ownerid");

            // se o fluxo ja esta delegado nao pode ser delegado outra vez
            if (hsDelegatedFlows.contains(tmpKey)) {
              continue;
            }
            ownerid = rs.getString("ownerid");

            if (bfirst) {
        %>
        <tr class="tab_header">
          <td><input type="checkbox" name="cb_ignore" value="cb_ignore" onclick="javascript:toggle_all_cb(this, this.form.cb_flowid)" /></td>
          <td><%=messages.getString("gestao_tarefas.field.owner")%></td>
          <td><%=messages.getString("gestao_tarefas.field.flow")%></td>
          <td></td>
        </tr>
            <%
              bfirst = false;
            }
            %>
        <tr class="<%=(toggle = !toggle) ? "tab_row_even" : "tab_row_odd"%>">
          <td><input type="checkbox" name="cb_flowid" value="<%=flowid%>;<%=ownerid%>" /></td>
          <td><%=ownerid%></td>
          <td><%=hmfda.get(flowid)%></td>
          <td><a class="cell_button" href="javascript:document.requisitar.flowid.value=<%=flowid.intValue()%>;document.requisitar.ownerid.value='<%=ownerid%>';tabber_right(5, '<%=response.encodeURL("requisitar_agendamento.jsp")%>', 'ts=<%=ts%>&' + get_params(document.requisitar));"><%=messages.getString("button.request")%></a></td>
        </tr>
                
        <%}
          pt.iflow.api.db.DatabaseInterface.closeResources(db, st, rs);

        } catch (Exception e) {
          db.rollback();
          e.printStackTrace();
          pt.iflow.api.db.DatabaseInterface.closeResources(db, st, rs);
        }
        %>
      </table>
      <%if (bfirst) {%>
      <table>
        <tr>
          <td class="error_msg"><%=messages.getString("gestao_tarefas.msg.nomorerequests")%></td>
        </tr>
      </table>
      <%} else {%>
      <div class="button_box"> 
        <input class="regular_button_03" type="button" name="add" value="<if:message string="button.request_checked"></if:message>" onClick="javascript:document.requisitar.cb_request.value=true;tabber_right(5, '<c:url value="requisitar_agendamento.jsp"></c:url>','ts=<%=ts%>&' + get_params(document.requisitar));"></input>
      </div>
      <%}%>
    </form>
  <% } else if (nAction == REASSIGN) {%>
    <form name="reatribuir" method="post">
      <input type="hidden" name="flowid" value=""/>
      <input type="hidden" name="ownerid" value=""/>                        
      <input type="hidden" name="action" value="<%=sAction%>"/>
      <table class="itemlist">
    <%try {
        ds = Utils.getDataSource();
        db = ds.getConnection();
        st = db.createStatement();

        bfirst = true;

        for (int i = 0; (fda != null) && (i < fda.length); i++) {
          Integer flowid = new Integer(fda[i].getId());

          // TODO verificar se fluxo foi delegado com permissao de supervisao. se assim for, continue
          // se nao tem permissoes
          if (!hsSuperuser.contains(flowid)) {
            continue;
          }

          if (bfirst) {%>
        <tr class="tab_header">
          <td><%=messages.getString("gestao_tarefas.field.flow")%></td>
          <td></td>
        </tr>
          <%
            bfirst = false;
          }
          %>
        <tr class="<%=i % 2 == 0 ? "tab_row_even" : "tab_row_odd"%>">
          <td><%=fda[i].getName()%></td>
          <td><a class="cell_button" href="javascript:document.reatribuir.flowid.value=<%=flowid.intValue()%>;tabber_right(5, '<%=response.encodeURL("requisitar_agendamento.jsp")%>', 'ts=<%=ts%>&' + get_params(document.reatribuir));"><%=messages.getString("button.reassign")%></a></td>
        </tr>
      <%}
        /* as tarefas que lhe foram delegadas */
        sbtmp = new StringBuffer();
        sbtmp.append("select * from activity_hierarchy where pending=0 ");
        sbtmp.append("and slave=1 and userid='");
        sbtmp.append(userInfo.getUtilizador()).append("' and permissions like '%S%'");
        rs = st.executeQuery(sbtmp.toString());

        while (rs.next()) {
          Integer flowid = new Integer(rs.getInt("flowid"));
          if (!hmfda.containsKey(flowid))
            continue;
          if (bfirst) {
      %>
        <tr class="tab_header">
          <td><%=messages.getString("gestao_tarefas.field.flow")%></td>
          <td></td>
        </tr>
          <%
            bfirst = false;
          }
          %>
        <tr class="<%=(toggle = !toggle) ? "tab_row_even" : "tab_row_odd"%>">
          <td><%=hmfda.get(flowid)%></td>
          <td><a class="cell_button" href="javascript:document.reatribuir.flowid.value=<%=flowid.intValue()%>;tabber_right(5, '<%=response.encodeURL("requisitar_agendamento.jsp")%>', 'ts=<%=ts%>&' + get_params(document.reatribuir));"><%=messages.getString("button.reassign")%></a></td>
        </tr>
      </table>
      <%
        }
        pt.iflow.api.db.DatabaseInterface.closeResources(db, st, rs);

      } catch (Exception e) {
        db.rollback();
        e.printStackTrace();
        pt.iflow.api.db.DatabaseInterface.closeResources(db, st, rs);
      }
      if (bfirst) {
      %>
      <table>
        <tr>
          <td class="error_msg"><%=messages.getString("gestao_tarefas.msg.nomorerequests")%></td>
        </tr>
      </table>
    <%}%>
    </form>
  <%}%>
  </div>
