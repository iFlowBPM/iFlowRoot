/*****************************************************************************************
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
****************************************************************************************/
package pt.iflow.api.presentation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.ProcessCatalogue;
import pt.iflow.api.flows.Flow;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessListItem;
import pt.iflow.api.processdata.ProcessListVariable;
import pt.iflow.api.processdata.ProcessSimpleVariable;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;

public class ProcessPresentation {

  public static String getProcessHistory(UserInfoInterface userInfo, String sONGOING, List<String> alStates, Map<String, Map<String, String>> hmHist, int flowid, int pid) {
    return getProcessHistory(userInfo, sONGOING, alStates, hmHist, flowid, pid, Integer.parseInt(Const.DEFAULT_SUBPID));
  }

  public static String getProcessHistory(UserInfoInterface userInfo, String sONGOING, List<String> alStates, Map<String, Map<String, String>> hmHist, int flowid, int pid, int subpid) {

    Map<String, String> hmtmp = null;
    
    HashMap<String,Timestamp[]> startStops = new HashMap<String, Timestamp[]>();

    if (flowid > 0 && pid > 0 && subpid > 0) {

      // historico processo
      DataSource dso = Utils.getDataSource();
      Connection db = null;
      PreparedStatement st = null;
      ResultSet rs = null;
      try {
        db = dso.getConnection();
        // first check if process is closed
        st = db.prepareStatement("select count(1) from flow_state where flowid=? and pid=? and subpid=? and closed=0");
        st.setInt(1, flowid);
        st.setInt(2, pid);
        st.setInt(3, subpid);
        rs = st.executeQuery();
        if (rs.next()) {
          int numStates = rs.getInt(1);
          if (numStates == 0)
            sONGOING = "";
        }
        rs.close();
        rs = null;
        st.close();
        st = null;

        st = db.prepareStatement("select * from flow_state_history where flowid=? and pid=? and subpid=? order by mdate asc, mid asc");
        st.setInt(1, flowid);
        st.setInt(2, pid);
        st.setInt(3, subpid);
        rs = st.executeQuery();

        while (rs.next()) {
          String state = rs.getString("state");

          if (hmHist.containsKey(state)) {
            hmtmp = hmHist.get(state);
          } else {
            hmtmp = new HashMap<String, String>();
            hmtmp.put("state", state);
            startStops.put(state, new Timestamp[2]);

            alStates.add(state);
          }

          String result = rs.getString("result");
          if (result == null)
            result = "";
          Timestamp ts = rs.getTimestamp("mdate");

          int nExitFlag = rs.getInt("exit_flag");
          
          if (nExitFlag == 0) {
            hmtmp.put("result", result);
            startStops.get(state)[0] = ts;
          } else {
            hmtmp.put("result2", result);
            startStops.get(state)[1] = ts;
          }

          hmHist.put(state, hmtmp);

        }
        rs.close();
        rs = null;
      } catch (Exception e) {
      } finally {
        Utils.closeDB(db, st, rs);
      }

      // Do some presentation post processing...
      for (String state : hmHist.keySet()) {
        hmtmp = hmHist.get(state);
        Timestamp tsStart = startStops.get(state)[0];
        Timestamp tsStop = startStops.get(state)[1];
        String sStart = "";
        String sStop = "";
        String sDesc = hmtmp.get("result");
        String sResult = hmtmp.get("result2");

        String sDuration = "";
        // check for start state
        if (tsStart == null && sDesc == null &&
            tsStop != null && sResult != null) {
          // first/init/start state
          // adjust fields accordingly...
          tsStart = tsStop;
          tsStop = null;

          sDesc = sResult;
          sResult = "-";

          sDuration = "-";
          sStop = "-";
        }


        if (sDesc == null) sDesc = "";
        if (sResult == null) sResult = sONGOING;
        if (tsStart != null) {
          sStart = DateUtility.formatTimestamp(userInfo, tsStart);
        }
        if (tsStop != null) {
          sStop = DateUtility.formatTimestamp(userInfo, tsStop);
        }

        if (tsStart != null && tsStop != null) {
          sDuration = Utils.getDuration(tsStart,tsStop);
        }

        hmtmp.put("result", sResult);
        hmtmp.put("description", sDesc);
        hmtmp.put("start", sStart);
        hmtmp.put("stop", sStop);
        hmtmp.put("duration", sDuration);
        
      }

    }

    return sONGOING;
  }

  /**
   * 
   * Obtem o detalhe do processo. Apenas são apresentadas marcadas como publicas, isto é, com descrição no catalogo.
   * 
   * @param userInfo
   * @param flowid
   * @param pid
   * @param subpid
   * @return
   */
  public static Map<String,String> getProcessDetail(UserInfoInterface userInfo, ProcessData procData) {
    if(null == userInfo) {
      Logger.error(null, "ProcessPresentation", "getProcessDetail", "Invalid user");
      return null;
    }
    if(null == procData) {
      Logger.error(userInfo.getUtilizador(), "ProcessPresentation", "getProcessDetail", "Invalid process");
      return null;
    }
    Flow flow = BeanFactory.getFlowBean();
    if(null == flow) {
      Logger.error(userInfo.getUtilizador(), "ProcessPresentation", "getProcessDetail", "Could not fetch flow "+procData.getFlowId());
      return null;
    }

    if(null == procData) {
      Logger.error(userInfo.getUtilizador(), "ProcessPresentation", "getProcessDetail", "Process data is not valid");
      return null;
    }
    
    if(!BeanFactory.getProcessManagerBean().canViewProcess(userInfo, procData)) {
      Logger.warning(userInfo.getUtilizador(), "ProcessPresentation", "getProcessDetail", "User not authorized to access process detail");
      return null;
    }

    ProcessCatalogue catalog = flow.getFlowCatalogue(userInfo, procData.getFlowId());
    Map<String,String> result = new HashMap<String,String>();
    
    
    List<String> simpleVars = catalog.getSimpleVariableNames();
    for(String varname : simpleVars) {
    	if (catalog.hasPublicName(varname)) {
    		ProcessSimpleVariable var = procData.get(varname);
    		String value = var.format();
    		result.put(catalog.getPublicName(varname), value);
    		Logger.debug(userInfo.getUtilizador(), "ProcessPresentation", "getProcessDetail", "Added variable "+varname+"; Desciption: "+catalog.getPublicName(varname)+"; Value: "+ value);
    	}
    }
    
    List<String> listVars = catalog.getListVariableNames();
    for(String varname : listVars) {
    	if (catalog.hasPublicName(varname)) {
    		ProcessListVariable var = procData.getList(varname);
    		StringBuilder sb = new StringBuilder();
    		for(int i=0; i < var.size(); i++) {
    			ProcessListItem item = var.getItem(i);
    			String value = "";
    			if (item != null)
    				value = item.format();
    			
    			sb.append("[").append(i).append("] ").append(value).append("<br>");
    		}
    		result.put(catalog.getPublicName(varname), sb.toString());
    		Logger.debug(userInfo.getUtilizador(), "ProcessPresentation", "getProcessDetail", "Added variable "+varname+"; Desciption: "+catalog.getPublicName(varname)+"; Value: "+ sb.toString());
    	}
    }
    
    return result;
  }
}
