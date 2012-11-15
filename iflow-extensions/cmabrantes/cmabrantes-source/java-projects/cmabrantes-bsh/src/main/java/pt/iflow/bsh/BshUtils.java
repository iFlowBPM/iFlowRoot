package pt.iflow.bsh;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import pt.iflow.api.audit.AuditData;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.ProcessCatalogue;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.documents.DocumentData;
import pt.iflow.api.flows.FlowType;
import pt.iflow.api.flows.IFlowData;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.DataSetVariables;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;

public class BshUtils {

    final static String PROCESS_STATE_CLOSED = "1";
    final static String PROCESS_STATE_OPEN = "0";
    final static String PROCESS_STATE_UNDEFINED = "-1";
	private static final String EMPTY = "##EMPTY##";
    
	public static String[][] listFlows(UserInfoInterface userInfo, String profile) {
			return listFlows(userInfo, profile, null);
	}
	
		//res[i][0] = flowName
	//res[i][1] = flowId
	private static String[][] listFlows(UserInfoInterface userInfo, String profile, String flowId) {

		String[][] res = new String[0][0];

		Connection db = null;
		//PreparedStatement pst = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			db = DatabaseInterface.getConnection(userInfo);

			List<String> flowIdListIDs = new ArrayList<String>();
			List<String> flowIdListNames = new ArrayList<String>();
			if (profile == null || StringUtils.isEmpty(profile)) {
				IFlowData[] flows = BeanFactory.getFlowHolderBean().listFlowsOnline(userInfo,FlowType.WORKFLOW);
				for (int i = 0; i < flows.length; i++) {
					flowIdListIDs.add(String.valueOf(flows[i].getId()));
					flowIdListNames.add(flows[i].getName());					
				}
			}
			else {
				st = db.createStatement();
				
				String query = "SELECT f.flowid, f.flowname FROM profiles p , flow_roles r, flow f where r.profileid = p.profileid and f.flowid = r.flowid " 
					+ ((flowId==null || "-1".equals(flowId))?"":"and f.flowid = "+flowId) 
					+ " and r.permissions like '%RCW%' and p.organizationid = " +  userInfo.getOrganization() + " and p.name = '" + profile + "'";
				
				rs = st.executeQuery(query);

				while (rs.next()) {
					flowIdListIDs.add(rs.getString("flowid"));
					flowIdListNames.add(rs.getString("flowname"));
				}
			}
			res = new String[flowIdListIDs.size()][2];
			for (int i=0; i<flowIdListIDs.size(); i++){
				res[i][0] = flowIdListNames.get(i);
				res[i][1] = flowIdListIDs.get(i);
			}

		} catch (SQLException sqle) {
			Logger.error(userInfo.getUtilizador(), null, "getUserProcesses", "Error retrieving data from DB.", sqle);
		} catch (Exception e) {
			Logger.error(userInfo.getUtilizador(), null, "getUserProcesses", "Error retrieving processes.", e);
		} finally {
			//DatabaseInterface.closeResources(db,st,pst,rs);
			DatabaseInterface.closeResources(db,st,rs);
		}   
		return res;
	}    


  public static String[] listSelectedFields(String[] searchFieldIdxs, int[] searchFieldSelected) {
    List<String> retObj = new ArrayList<String>();
    for (int i = 0; i < searchFieldSelected.length; i++) {
      if (searchFieldSelected[i] == 1) retObj.add(searchFieldIdxs[i]);
    }
    return retObj.toArray(new String[retObj.size()]);
  }
  //res[i][0] = FieldName
  //res[i][1] = FieldIdx
  public static String[][] getSeachableFields(UserInfoInterface userInfo, String sFlowId) {
    String[][] res = new String[0][0];

    if ("-1".equals(sFlowId)) return res;

    IFlowData flowData = BeanFactory.getFlowHolderBean().getFlow(userInfo, Integer.parseInt(sFlowId));
    Map<String,Integer> searchableFields = flowData.getIndexVars();
    if(searchableFields.isEmpty()) return res;
    ProcessCatalogue catalog = flowData.getCatalogue();

    res = new String[searchableFields.size()][4];
    int i = 0;
    for(String name : searchableFields.keySet()) {
      res[i][0] = catalog.getDisplayName(name);
      res[i++][1] = searchableFields.get(name).toString();
    }

    return res;
  }

  //res[i][0] = flowName
  //res[i][1] = flowId
  //res[i][2] = pid
  //res[i][3] = pid
  @SuppressWarnings("unchecked")
public static String[][] searchProcesses(UserInfoInterface userInfo, String searchText, String searchApps, String sFlowId, 
      Date createdFrom, Date createdTo, Date alterFrom, Date alterTo, String creator, String assigned,
      String processState, String[] searchFields, String[] searchTextFields, boolean participantsOnly, String profile, 
      String orderBy, int maxResults) {

    String[][] res = new String[0][0];

    // first, fix dates
    createdFrom = Utils.fixDateBefore(createdFrom);
    createdTo = Utils.fixDateAfter(createdTo);
    alterFrom = Utils.fixDateBefore(alterFrom);
    alterTo = Utils.fixDateAfter(alterTo);

    Connection db = null;
    //PreparedStatement pst = null;
    Statement st = null;
    ResultSet rs = null;

    String[][] flowInfo = null;
//    if ("-1".equals(sFlowId)) {
      flowInfo = BshUtils.listFlows(userInfo, profile, sFlowId);
//    } else {
//    	IFlowData fd = BeanFactory.getFlowHolderBean().getFlow(userInfo, Integer.parseInt(sFlowId));
//      flowInfo = new String[][] {{fd.getName()},{String.valueOf(fd.getId())}};
//    }
    

    // para cada fluxo mapear os idxs de pesquisa para os nomes
	HashMap<String, HashMap<String,String>> flowMap = new HashMap<String, HashMap<String,String>>();
    for(int i=0; i < flowInfo.length; i++) {
    	String thisFid = flowInfo[i][1];
    	HashMap<String,String> flowPartMap = new HashMap<String,String>();
        String[][] auxFields = BshUtils.getSeachableFields(userInfo, thisFid);
    	
        for (int j=0; j<auxFields.length; j++) {
        	flowPartMap.put("idx"+auxFields[j][1], auxFields[j][0]);
        }
        
        flowMap.put(String.valueOf(thisFid), flowPartMap);
    }
    
    try {
      db = DatabaseInterface.getConnection(userInfo);
      
      int numIdxFields=0;
	  String[] fieldList = new String[]{
			  "p.flowid"
			  ,"p.pid"
			  ,"f.flowname"
			  ,"p.pnumber"
			  ,"a.profilename"
			  ,"a.description"
			  ,"a.started"
			  ,"p.creator"
			  ,"p.created"
			  ,"p.closed"
			  ,"p.idx"+(numIdxFields++)
			  ,"p.idx"+(numIdxFields++)
			  ,"p.idx"+(numIdxFields++)
			  ,"p.idx"+(numIdxFields++)
			  ,"p.idx"+(numIdxFields++)
			  ,"p.idx"+(numIdxFields++)
			  ,"p.idx"+(numIdxFields++)
			  ,"p.idx"+(numIdxFields++)
			  ,"p.idx"+(numIdxFields++)
			  ,"p.idx"+(numIdxFields++)
			  ,"p.idx"+(numIdxFields++)
			  ,"p.idx"+(numIdxFields++)
			  ,"p.idx"+(numIdxFields++)
			  ,"p.idx"+(numIdxFields++)
			  ,"p.idx"+(numIdxFields++)
			  ,"p.idx"+(numIdxFields++)
			  ,"p.idx"+(numIdxFields++)
			  ,"p.idx"+(numIdxFields++)
			  ,"p.idx"+(numIdxFields++)
			  ,"p.idx"+(numIdxFields++)
	  };
      
	  // reset do numero
	  numIdxFields--;
	  
      int NUM_FIELDS = fieldList.length;
      
      StringBuilder sbQueryOpen = new StringBuilder(2048);
      StringBuilder sbQueryClosed = new StringBuilder(2048);

      sbQueryOpen.append("select ");
      sbQueryClosed.append("select ");
      for (int i = 0; i < fieldList.length; i++) {
    	  if (i>0) {
    		  sbQueryOpen.append(",");
    		  sbQueryClosed.append(",");
    	  }
    	  sbQueryOpen.append(fieldList[i]);
    	  sbQueryClosed.append(fieldList[i]);
      }
      
      sbQueryOpen.append(" from activity a, process p, flow f");
      if (participantsOnly) sbQueryOpen.append(", activity_history ah");
      sbQueryOpen.append(" where a.pid = p.pid and p.flowid = f.flowid");
      if (participantsOnly) sbQueryOpen.append(" and a.pid = ah.pid and ah.userid = '" + userInfo.getUtilizador() + "'");
      sbQueryOpen.append(" and a.mid in (select max(mid) from activity a1 where a1.pid = a.pid and a1.flowid = a1.flowid) ");
      sbQueryOpen.append(" and p.canceled = 0 and p.closed = 0");

      sbQueryClosed.append(" from activity_history a, process_history p, flow f");
      if (participantsOnly) sbQueryClosed.append(", activity_history ah");
      sbQueryClosed.append(" where a.pid = p.pid and p.flowid = f.flowid");
      if (participantsOnly) sbQueryClosed.append(" and a.pid = ah.pid and ah.userid = '" + userInfo.getUtilizador() + "'");
      sbQueryClosed.append(" and a.mid in (select max(mid) from activity_history a1 where a1.pid = a.pid and a1.flowid = a1.flowid) ");
      sbQueryClosed.append(" and p.canceled = 0 and p.closed = 1");

      sbQueryOpen.append(" and p.flowid in (");
      sbQueryClosed.append(" and p.flowid in (");
      boolean comma = false;
      for (int i = 0; i < flowInfo.length; i++) {
        if (comma) {
          sbQueryOpen.append(",");
          sbQueryClosed.append(",");
        }
        comma = true;
        sbQueryOpen.append(flowInfo[i][1]);
        sbQueryClosed.append(flowInfo[i][1]);
      }

      sbQueryOpen.append(")");
      sbQueryClosed.append(")");

      if (StringUtils.isNotEmpty(creator)) {
    	  String[] creatorList = creator.split(",");
    	  
    	  sbQueryOpen.append(" and (p.creator='").append(creatorList[0]).append("'");
    	  sbQueryClosed.append(" and (p.creator='").append(creatorList[0]).append("'");
    	  for (int i = 1; i < creatorList.length; i++) {
    		  sbQueryOpen.append(" or p.creator='").append(creatorList[i]).append("'");
    		  sbQueryClosed.append(" or p.creator='").append(creatorList[i]).append("'");
    	  }
    	  sbQueryOpen.append(")");
    	  sbQueryClosed.append(")");
      }

      if (StringUtils.isNotEmpty(assigned)) {
    	  String[] assignedList = assigned.split(",");
    	  
    	  sbQueryOpen.append(" and (a.profilename='").append(assignedList[0]).append("'");
    	  sbQueryClosed.append(" and (a.profilename='").append(assignedList[0]).append("'");
    	  for (int i = 1; i < assignedList.length; i++) {
    		  sbQueryOpen.append(" or a.profilename='").append(assignedList[i]).append("'");
    		  sbQueryClosed.append(" or a.profilename='").append(assignedList[i]).append("'");
    	  }
    	  sbQueryOpen.append(")");
    	  sbQueryClosed.append(")");
      }

      DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

      Calendar cal = Calendar.getInstance();
      Date tmpDate = null;
      if (createdFrom != null) {
      	cal.setTime(createdFrom);
      	cal.add(Calendar.DAY_OF_MONTH, -1);
      	tmpDate = cal.getTime();
        sbQueryOpen.append(" and p.created >= STR_TO_DATE('").append(df.format(tmpDate)).append("','%Y-%m-%d')");
        sbQueryClosed.append(" and p.created >= STR_TO_DATE('").append(df.format(tmpDate)).append("','%Y-%m-%d')");
      }

      if (createdTo != null) {
      	cal.setTime(createdTo);
      	cal.add(Calendar.DAY_OF_MONTH, 1);
      	tmpDate = cal.getTime();
        sbQueryOpen.append(" and p.created < STR_TO_DATE('").append(df.format(tmpDate)).append("','%Y-%m-%d')");
        sbQueryClosed.append(" and p.created < STR_TO_DATE('").append(df.format(tmpDate)).append("','%Y-%m-%d')");
      }

      if (alterFrom != null) {
      	cal.setTime(alterFrom);
      	cal.add(Calendar.DAY_OF_MONTH, -1);
      	tmpDate = cal.getTime();
        sbQueryOpen.append(" and p.lastupdate >= STR_TO_DATE('").append(df.format(tmpDate)).append("','%Y-%m-%d')");
        sbQueryClosed.append(" and p.lastupdate >= STR_TO_DATE('").append(df.format(tmpDate)).append("','%Y-%m-%d')");
      }

      if (alterTo != null) {
      	cal.setTime(alterTo);
      	cal.add(Calendar.DAY_OF_MONTH, 1);
      	tmpDate = cal.getTime();
        sbQueryOpen.append(" and p.lastupdate < STR_TO_DATE('").append(df.format(tmpDate)).append("','%Y-%m-%d')");
        sbQueryClosed.append(" and p.lastupdate < STR_TO_DATE('").append(df.format(tmpDate)).append("','%Y-%m-%d')");
      }

      StringBuilder sbFields = new StringBuilder();

      for (int i=0; searchFields!=null && i<searchFields.length && i <Const.INDEX_COLUMN_COUNT; i++) {
        if (StringUtils.isNotEmpty(searchTextFields[i])) {
          if (sbFields.length() > 0) sbFields.append(" or ");
          sbFields.append("lower(idx" + searchFields[i] + ") like lower('%").append(StringEscapeUtils.escapeSql(searchTextFields[i])).append("%')");
        }
      }

      if (sbFields.length() > 0) {
        sbQueryOpen.append(" and (").append(sbFields.toString()).append(")");
        sbQueryClosed.append(" and (").append(sbFields.toString()).append(")");
      }

      if (StringUtils.isNotEmpty(searchText)) {
    	  
    	  String[] textList = searchText.split(" ");
    	  for (int i = 0; i < textList.length; i++) {
			String text = StringUtils.trimToNull(textList[i]);
			if (text != null) {
				sbQueryOpen.append(" and extractvalue(procdata,'//a[@n]') like '%").append(text).append("%'");
				sbQueryClosed.append(" and extractvalue(procdata,'//a[@n]') like '%").append(text).append("%'");
			}
    	  }    	  
      }

      sbQueryOpen.append(" group by a.pid, a.flowid");
      sbQueryClosed.append(" group by a.pid, a.flowid");

      if (orderBy == null) {
      	orderBy = " order by started desc, flowname asc, closed asc, pnumber asc";
      }
//      else {
//      	orderBy = " order by " + orderBy;
//      }
      
      StringBuilder sbQueryUnion = new StringBuilder(4096);
      if (PROCESS_STATE_UNDEFINED.equals(processState)) {
    	  sbQueryUnion.append(sbQueryOpen).append(" union all ").append(sbQueryClosed);
    	  sbQueryUnion.append(orderBy);
      }
      else {
    	  sbQueryOpen.append(orderBy);
    	  sbQueryClosed.append(orderBy);
      }

	  Statement stmt = db.createStatement();

      if (PROCESS_STATE_UNDEFINED.equals(processState)) {
    	  Logger.debug(userInfo.getUserId(), "MyBshUtils" , "searchProcesses", "query union = " + sbQueryUnion);
    	  rs = stmt.executeQuery(sbQueryUnion.toString());
    	  //pst = db.prepareStatement(sbQueryUnion.toString());	  
      }
      else if (PROCESS_STATE_OPEN.equals(processState)) {
    	  Logger.debug(userInfo.getUserId(), "MyBshUtils" , "searchProcesses", "query open = " + sbQueryOpen);
    	  rs = stmt.executeQuery(sbQueryOpen.toString());
    	  // pst = db.prepareStatement(sbQueryOpen.toString());
      }
      else {
    	  Logger.debug(userInfo.getUserId(), "MyBshUtils" , "searchProcesses", "query closed = " + sbQueryClosed);
    	  rs = stmt.executeQuery(sbQueryClosed.toString());
    	  //pst = db.prepareStatement(sbQueryClosed.toString());
      }

      //int pos = 0;

      //rs = pst.executeQuery();

      int counter = 0;
      List<String> flowNames = new ArrayList<String>();
      List<String> flowIds = new ArrayList<String>();
      List<String> pids = new ArrayList<String>();
      List<String> pnumbers = new ArrayList<String>();
      List<String> currentUsers = new ArrayList<String>();
      List<String> descriptions = new ArrayList<String>();
      List<String> dates = new ArrayList<String>();
      List<String> creators = new ArrayList<String>();
      List<String> createdDates = new ArrayList<String>();
      List<String> status = new ArrayList<String>();
      List<String>[] extended = new ArrayList[numIdxFields];
      for (int i = 0; i < extended.length; i++) {
		extended[i] = new ArrayList<String>();
	}
      int numRes = 0;
      while (rs.next() && (maxResults <= 0 || numRes++ < maxResults)) {
        flowNames.add(rs.getString("FLOWNAME"/*DataSetVariables.FLOWNAME*/));
        flowIds.add(Integer.toString((rs.getInt(DataSetVariables.FLOWID))));
        pids.add(rs.getString(DataSetVariables.PID));
        pnumbers.add(rs.getString(DataSetVariables.PNUMBER));
        currentUsers.add(rs.getString("PROFILENAME"));
        descriptions.add(rs.getString("DESCRIPTION"));
        dates.add(rs.getString("STARTED"));
        creators.add(rs.getString("CREATOR"));
        createdDates.add(rs.getString("CREATED"));
        status.add(rs.getString("CLOSED"));
        for(int i=0;i < numIdxFields; i++) {
        	String val = rs.getString("idx"+i);
        	if (val != null && StringUtils.isNotEmpty(val)) {
        		extended[i].add(val);
        	}
        	else {
        		extended[i].add(EMPTY);
        	}
        }
        counter++;
      }
      
      
      
      // Numero de Campos - os que vem da query
      
      res = new String[counter][NUM_FIELDS];      
      for (int i = 0; i < counter; i++) {
    	String resFlowId = flowIds.get(i);
        res[i][0] = flowNames.get(i).toString();
        res[i][1] = resFlowId;
        res[i][2] = pids.get(i).toString();
        res[i][3] = pnumbers.get(i).toString();
        res[i][4] = currentUsers.get(i).toString();
        res[i][5] = descriptions.get(i).toString();
        res[i][6] = dates.get(i).toString();
        res[i][7] = creators.get(i).toString();
        res[i][8] = createdDates.get(i).toString();
        res[i][9] = status.get(i).toString();
        String extField = "";
        for(int j=0; j < numIdxFields; j++) {
        	String value = extended[j].get(i);
        	String name = null;
        	if (!EMPTY.equals(value)) {
        		name = flowMap.get(resFlowId).get("idx"+j);
        	}
        	if (name != null) {
        		if (j>0) extField += ";";
        		extField += name + " = " + value;
        	}
        }
        
        res[i][10] = extField;
      }

    } catch (SQLException sqle) {
      Logger.error(userInfo.getUtilizador(), null, "getUserProcesses", "Error retrieving data from DB.", sqle);
    } catch (Exception e) {
      Logger.error(userInfo.getUtilizador(), null, "getUserProcesses", "Error retrieving processes.", e);
    } finally {
      //DatabaseInterface.closeResources(db,st,pst,rs);
      DatabaseInterface.closeResources(db,st,rs);
    }   

    return res;
  }

  public static String[][] returnProcessStaticsFromFlow(UserInfoInterface userInfo, String flowname, String process){
    StringBuffer sql = new StringBuffer("SELECT AH.profilename, AVG(time_to_sec(timediff(AH.archived,AH.created))) ");
    sql.append("FROM activity_history AH, flow F, process P ");
    sql.append("WHERE AH.archived IS NOT NULL AND AH.undoflag=0 AND AH.flowid = F.flowid AND F.flowname = '").append(flowname).append("'");
    sql.append(" AND AH.flowid = P.flowid AND AH.pid = P.pid ");
    if (StringUtils.isNotEmpty(process)) {
      sql.append(" AND P.pnumber = '").append(process).append("' ");
    }
    sql.append("GROUP BY AH.profilename HAVING AVG(AH.archived-AH.created) > 0");

    DataSource ds = null;
    Connection db = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    List<AuditData> alData = new ArrayList<AuditData>();
    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      pst = db.prepareStatement(sql.toString());
      rs = pst.executeQuery();
      while (rs.next()) {
        String sName = rs.getString(1);
        String sValue = rs.getString(2);
        alData.add(new AuditData(sName, sValue));
      }
      rs.close();
      rs = null;
    } catch (SQLException sqle) {
      Logger.error(userInfo.getUtilizador(), "", "returnProcessStaticsFromFlow", "sql exception: " + sqle.getMessage(), sqle);
    } catch (Exception e) {
      Logger.error(userInfo.getUtilizador(), "", "returnProcessStaticsFromFlow", "exception: " + e.getMessage(), e);
    } finally {
      DatabaseInterface.closeResources(db, pst, rs);
    }

    String[][] result = new String[alData.size()][2];
    for (int i=0; i<alData.size(); i++){
      result[i][0] = alData.get(i).getDisplayName();
      result[i][1] = alData.get(i).getValue();
    }
    return result;
  }

  public static String[] filterPaths(String[] _crit_Pastas){
    if (_crit_Pastas!=null) {
      List<String> filter = new ArrayList<String>();

      for (int i=0; i<_crit_Pastas.length; i++) {
        if (!_crit_Pastas[i].startsWith("/Data Dictionary") &&
            !_crit_Pastas[i].startsWith("/Guest Home") &&
            !_crit_Pastas[i].startsWith("/User Homes") &&
            !_crit_Pastas[i].startsWith("/Sites") &&
            !_crit_Pastas[i].startsWith("/Web Projects")) {
          filter.add(_crit_Pastas[i]);
        }
      }
      _crit_Pastas = filter.toArray(new String[filter.size()]);
    }
    return _crit_Pastas;
  }

  public static String getCritiriaString(String _crit_SearchText, String searchApps, String _crit_FlowId, Date _crit_CreatedFrom, 
      Date _crit_CreatedTo, Date _crit_AlterFrom, Date _crit_AlterTo, String _crit_Creator, String _crit_Assigned, String _crit_ProcessState, 
      String[] _critList_FlowIds, String[] _critList_FlowNames, int[] _critList_SearchFields_checkedtext, String[] critList_SearchFieldNames) {

    boolean hasCriteria = false;
    String s = "";
    DateFormat dtFormat = new SimpleDateFormat("dd-MM-yyyy");

    if (StringUtils.isNotEmpty(_crit_SearchText)) {
      s += "Pesquisar por '" + _crit_SearchText + "'";
      hasCriteria = true;
    }
    if (!StringUtils.equals(_crit_FlowId, "-1")) {
      String flowName = "";
      for (int i=0; i<_critList_FlowIds.length; i++) {
        if (StringUtils.equals(_crit_FlowId, _critList_FlowIds[i])) {
          flowName = _critList_FlowNames[i];
        }
      }
      s += (hasCriteria ? "; n" : "N") + "o processo '" + flowName + "'";
      hasCriteria = true;
    }
    if (_crit_CreatedFrom!=null) {
      s += (hasCriteria ? "; i" : "I") + "niciados a partir de '" + dtFormat.format(_crit_CreatedFrom) + "'";
      hasCriteria = true;
    }
    if (_crit_CreatedTo!=null) {
      s += (hasCriteria ? "; i" : "I") + "niciados até '" + dtFormat.format(_crit_CreatedTo) + "'";
      hasCriteria = true;
    }
    if (_crit_AlterFrom!=null) {
      s += (hasCriteria ? "; i" : "I") + "niciados a partir de '" + dtFormat.format(_crit_AlterFrom) + "'";
      hasCriteria = true;
    }
    if (_crit_AlterTo!=null) {
      s += (hasCriteria ? "; i" : "I") + "niciados a partir de '" + dtFormat.format(_crit_AlterTo) + "'";
      hasCriteria = true;
    }
    if (StringUtils.isNotEmpty(_crit_Creator)) {
    	_crit_Creator = _crit_Creator.replaceAll(",", " ou ");
      s += (hasCriteria ? "; c" : "C") + "riado por '" + _crit_Creator + "'";
      hasCriteria = true;
    }
    if (StringUtils.isNotEmpty(_crit_Assigned)) {
    	_crit_Assigned = _crit_Assigned.replaceAll(",", " ou ");
      s += (hasCriteria ? "; a" : "A") + "tribuido a '" + _crit_Assigned + "'";
      hasCriteria = true;
    }
    if (StringUtils.equals(_crit_ProcessState, "0")) {
      s += (hasCriteria ? "; n" : "N") + "o estado 'Aberto'";
      hasCriteria = true;
    } else if (StringUtils.equals(_crit_ProcessState, "1")) {
      s += (hasCriteria ? "; n" : "N") + "o estado 'Fechado'";
      hasCriteria = true;
    }

    boolean hasFields = false;
    String f = "";
      for (int i=0; i<_critList_SearchFields_checkedtext.length; i++) {
        if (_critList_SearchFields_checkedtext[0]==1) {
          f += (hasFields ? ", " : "") + "'" + critList_SearchFieldNames[i] + "'";
          hasFields = true;
        }
      }

    if (hasFields) {
      s += (hasCriteria ? "; n" : "N") + "o(s) campo(s) " + f;
      hasCriteria = true;
    }

    return hasCriteria ? s : "(sem critérios)";
  }
  
  
	public static DocumentData updateDocument(UserInfoInterface userInfo, ProcessData procData, DocumentData docData) {

		Connection db = null;

		try {
			db = DatabaseInterface.getConnection(userInfo);
			PreparedStatement pst = null;
			ResultSet rs = null;
			try {
				db.setAutoCommit(false);
				Date dateNow = new Date();
				pst = db.prepareStatement("UPDATE documents set pid=?,subpid=?,flowid=?,filename=?,updated=? WHERE docid=?");
				pst.setInt(1, docData.getPid());
				pst.setInt(2, docData.getSubpid());
				pst.setInt(3, docData.getFlowid());
				pst.setString(4, docData.getFileName());
				pst.setTimestamp(5, new java.sql.Timestamp(dateNow.getTime()));
				pst.setInt(6, docData.getDocId());
				docData.setUpdated(dateNow);
			    pst.executeUpdate();
				DatabaseInterface.commitConnection(db);
			} finally {
				DatabaseInterface.closeResources(pst, rs);
			}
		} catch (Exception e) {
			try {
				DatabaseInterface.rollbackConnection(db);
			} catch (Exception e2) {
				Logger.error(userInfo.getUtilizador(), "MyBshUtils", "updateDocument", procData.getSignature() + "Error rolling back.", e2);
			}
			Logger.error(userInfo.getUtilizador(), "MyBshUtils", "updateDocument", procData.getSignature() + "Error updating existing document.", e);
		} finally {
			DatabaseInterface.closeResources(db);
		}
		return docData;
	}

}

