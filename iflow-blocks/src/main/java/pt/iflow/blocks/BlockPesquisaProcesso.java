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
package pt.iflow.blocks;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.ProcessManager;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessHeader;
import pt.iflow.api.processdata.ProcessListVariable;
import pt.iflow.api.processdata.ProcessSimpleVariable;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.DataSetVariables;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;
import pt.iflow.blocks.interfaces.PesquisaProcesso;


/**
 * <p>Title: BlockPesquisaProcesso</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class BlockPesquisaProcesso extends Block implements PesquisaProcesso {
  public Port portIn, portSuccess, portEmpty, portError;

  private static HashSet<String> _hsUnaryOps = new HashSet<String>();

  private static final String sOP_LIKE = "like";
  private static final String sOP_GROUP = "in";
  private static final String sOP_GROUP_SEPARATOR = "@#$,";

  private static final String sMETHOD_STRINGS_PREFIX = "strings(";
  private static final String sMETHOD_STRINGS_SUFFIX = ")";

  private static final String sRETURN_PIDS="ret_pids";
  private static final String sARRAYS="arrays";

  static {
    _hsUnaryOps.add("is null");
    _hsUnaryOps.add("is not null");
  }


  public BlockPesquisaProcesso(int anFlowId,int id, int subflowblockid, String filename) {
    super(anFlowId,id, subflowblockid, filename);
    hasInteraction = false;
  }

  public Port[] getOutPorts (UserInfoInterface userInfo) {
    Port[] retObj = new Port[3];
    retObj[0] = portSuccess;
    retObj[1] = portEmpty;
    retObj[2] = portError;
    return retObj;
  }

  public Port getEventPort() {
    return null;
  }

  public Port[] getInPorts (UserInfoInterface userInfo) {
    Port[] retObj = new Port[1];
    retObj[0] = portIn;
    return retObj;
  }

  /**
   * No action in this block
   * @return always 'true'
   */
  public String before(UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }

  /**
   * No action in this block
   *
   * @param dataSet a value of type 'DataSet'
   * @return always 'true'
   */
  public boolean canProceed(UserInfoInterface userInfo, ProcessData procData) {
    return true;
  }

  /**
   * Executes the block main action
   *
   * @param dataSet a value of type 'DataSet'
   * @return the port to go to the next block
   */
  public Port after(UserInfoInterface userInfo, ProcessData procData) {
    Port outPort = portSuccess;

    String login = userInfo.getUtilizador();

    DataSource ds;
    Connection db=null;
    Statement st=null;
    ResultSet rs=null;

    Triplet[] oaPidSubpids = null;
    ProcessData[] pda;


    try {
      String retPids = getAttribute(sRETURN_PIDS);
      String arrays = getAttribute(sARRAYS);

      boolean returnPids = StringUtils.equals(retPids, "yes");

      // preliminar validation....
      if(returnPids && StringUtils.isEmpty(arrays)) {
        Logger.error(login,this,"after","Invalid return arrays: arrays="+arrays+";");
        return portError;
      }

      PIDSearchResult result = getIds(userInfo, procData);

      if(null != result && returnPids) {
        // cleanup result data holder....
        result.bClosed = false;
        result.bOpened = false;
        result.hmSpecialVars = null; // which vars?
        result.hmDestVars = null;    // How to??
        result.saVars = null;
        result.sQueryExtra = null;

        if(result.pids.length == 0) return portEmpty;

        ProcessListVariable lvar = procData.getList(arrays);
        for(int i = 0; i < result.pids.length; i++) {
          lvar.parseAndSetItemValue(i, result.pids[i].toString());
        }
        procData.setList(lvar);

        result = null; // invite GC

        Logger.info(login,this,"after","DataSet updated, Pesquisa Processo complete.");

        return portSuccess; 
      }



      // List altmp = result.pids;
      final boolean bOpened = result.bOpened; // indicates that at least one criteria is for opened procs
      final boolean bClosed = result.bClosed; // indicates that at least one criteria is for closed procs
      final HashMap<String,String> hmSpecialVars = result.hmSpecialVars;
      final HashMap<String,String> hmDestVar = result.hmDestVars;  // key: varname; value: destvarname
      final String sQueryExtra = result.sQueryExtra;

      ds = Utils.getDataSource();

      if (result.pids == null) {
        outPort = portError;
      }
      else {
        Logger.debug(login,this,"after","found " + result.pids.length + " procs");


        pda = null;
        if (result.pids.length > 0) {
          // now get data for those procs
          ProcessManager pm = BeanFactory.getProcessManagerBean();

          int nMode = Const.nALL_PROCS_READONLY;
          if (bOpened && !bClosed) {
            nMode = Const.nOPENED_PROCS_READONLY;
          }
          else if (!bOpened && bClosed) {
            nMode = Const.nCLOSED_PROCS_READONLY;
          }

          oaPidSubpids = result.pids;
          ProcessHeader[] phaProcHeaders = new ProcessHeader[result.pids.length];
          for (int i=0; i < result.pids.length; i++) {
            phaProcHeaders[i] = new ProcessHeader(result.pids[i].flowid,
                result.pids[i].pid,  // pid
                result.pids[i].subpid); // subpid
          }

          pda = pm.getProcessesData(userInfo, phaProcHeaders, nMode);
        }

        if (pda == null || pda.length == 0) {
          outPort = portEmpty;
        }
        else {
          HashMap<String, Integer> procIndex = null;
          if (hmSpecialVars.size() > 0 &&
              (hmSpecialVars.containsKey(saTYPES[1]) ||
                  hmSpecialVars.containsKey(saTYPES[2]) ||
                  hmSpecialVars.containsKey(saTYPES[3]))) {
            procIndex = new HashMap<String, Integer>(); // pid index info key=pid, value=proc index
          }

          List<String> importVars = new ArrayList<String>();
          for (String varname : result.saVars) {
            importVars.add(varname);
          }
          if (hmSpecialVars.containsKey(saTYPES[3])) {
            importVars.add(hmSpecialVars.get(saTYPES[3]));
          }

          for (String varname : importVars) {

            String destvarname = varname;
            if (hmDestVar.containsKey(varname)) {
              destvarname = hmDestVar.get(varname);
            }
            ProcessListVariable var = procData.getList(destvarname);

            boolean isCreateDate = 
              hmSpecialVars.containsKey(saTYPES[3]) && 
              StringUtils.equals(varname, hmSpecialVars.get(saTYPES[3]));


            for (int i=0; i < pda.length; i++) {

              if (isCreateDate) {
                // FIXME if catalog has right type (date), use setItemValue(i, pda[i].getCreationDate())
                // old model does not support date types...
                var.parseAndSetItemValue(i, Utils.date2string(pda[i].getCreationDate()));
              }
              else {
                if (pda[i].isListVar(varname)) {
                  continue; // don't process list items.
                }

                if (procIndex != null) {
                  procIndex.put(pda[i].getPid() + "/" + pda[i].getSubPid(), i);
                }

                var.parseAndSetItemValue(i, pda[i].getFormatted(varname));
              }
            }

            procData.setList(var);
          }


          // now special vars
          if (hmSpecialVars.size() > 0 &&
              (hmSpecialVars.containsKey(saTYPES[1]) ||
                  hmSpecialVars.containsKey(saTYPES[2]))) {

            // FIXME this query...
            StringBuilder sbquery = new StringBuilder();

            sbquery.append("select distinct flowid,pid,subpid");

            if  (hmSpecialVars.containsKey(saTYPES[1]) ||
                hmSpecialVars.containsKey(saTYPES[2])) {

              // FLOW_STATE_HISTORY

              sbquery.append(",mid");

              if (hmSpecialVars.containsKey(saTYPES[1])) {
                // data estado
                sbquery.append(",mdate");
              }
              if (hmSpecialVars.containsKey(saTYPES[2])) {
                // descricao estado
                sbquery.append(",result");
              }

              sbquery.append(" from flow_state_history ");
            }

            sbquery.append(" where ").append(sQueryExtra);

            if (!sQueryExtra.equals("")) sbquery.append(" and ");

            sbquery.append("(");
            for (int i=0; oaPidSubpids != null && i < oaPidSubpids.length; i++) {
              if (i > 0) sbquery.append(" or ");
              int ipid    = oaPidSubpids[i].pid;
              int isubpid = oaPidSubpids[i].subpid;
              sbquery.append("(pid=").append(ipid).append(" and subpid=");
              sbquery.append(isubpid).append(")");
            }
            sbquery.append(") order by pid asc, subpid asc");

            if (hmSpecialVars.containsKey(saTYPES[1]) ||
                hmSpecialVars.containsKey(saTYPES[2])) {
              if (hmSpecialVars.containsKey(saTYPES[1])) sbquery.append(", mdate desc");
              sbquery.append(", mid desc");
            }

            Logger.debug(login,this,"after","SPECIALVARS QUERY: " + sbquery.toString());

            if (db == null) {
              db = ds.getConnection();
              st = db.createStatement();
            }
            HashMap<String,String> stateDates = null;
            HashMap<String,String> stateDesc = null;

            if (hmSpecialVars.containsKey(saTYPES[1])) 
              stateDates = new HashMap<String, String>(); // dates
              if (hmSpecialVars.containsKey(saTYPES[2])) 
                stateDesc = new HashMap<String, String>(); // descriptions

              try {
                rs = st.executeQuery(sbquery.toString());
                while (rs.next()) {
                  String procKey = rs.getInt("pid") + "/" + rs.getInt("subpid");
                  if ((stateDates != null &&  stateDates.containsKey(procKey))
                      || (stateDesc != null && stateDesc.containsKey(procKey))) {
                    // already processed proc
                    continue;
                  }
                  if (stateDates != null) {
                    stateDates.put(procKey, Utils.date2string(new java.util.Date((rs.getTimestamp("mdate")).getTime())));
                  }
                  if (stateDesc != null) {
                    stateDesc.put(procKey, rs.getString("result"));
                  }
                }
                rs.close();
                rs = null;
              }
              catch (Exception e) {
                Logger.debug(login,this,"after","error specialvars: " + e.getMessage());
                stateDates = null;
                stateDesc = null;
              }

              if (stateDates == null && stateDesc == null) {
                Logger.debug(login,this,"after","no special vars found");
              }
              else {
                if (procIndex == null || procIndex.size() == 0) {
                  Logger.debug(login,this,"after","special vars found but no pid index info");
                }
                else {
                  if (stateDates != null) {
                    String varname = hmSpecialVars.get(saTYPES[1]);

                    Iterator<String> iter = stateDates.keySet().iterator();
                    while (iter.hasNext()) {
                      String procKey = iter.next();
                      String value = stateDates.get(procKey); 

                      if (procIndex.containsKey(procKey)) {
                        int procPosition = procIndex.get(procKey); 

                        // check alternative destination var name
                        if (hmDestVar.containsKey(varname)) {
                          varname = (String)hmDestVar.get(varname);

                        }
                        ProcessListVariable lvar = procData.getList(varname);
                        lvar.parseAndSetItemValue(procPosition, value);
                        procData.setList(lvar);
                      }
                    }
                  }

                  if (stateDesc != null) {
                    String varname = hmSpecialVars.get(saTYPES[2]);
                    Iterator<String> iter = stateDesc.keySet().iterator();
                    while (iter.hasNext()) {
                      String procKey = iter.next();
                      String value = stateDesc.get(procKey);

                      if (procIndex.containsKey(procKey)) {
                        int procPosition = procIndex.get(procKey); 

                        // check alternative destination var name
                        if (hmDestVar.containsKey(varname)) {
                          varname = (String)hmDestVar.get(varname);

                        }
                        ProcessListVariable lvar = procData.getList(varname);
                        lvar.parseAndSetItemValue(procPosition, value);
                        procData.setList(lvar);

                      }
                    }
                  }
                }
              }

          } // hmSpecialVars...
        }
      }
    }
    catch (SQLException sqle) {
      Logger.error(login,this,"after","caught sql exception: " + sqle.getMessage(), sqle);
      sqle.printStackTrace();
      outPort = portError;
    }
    catch (Exception e) {
      Logger.error(login,this,"after","caught exception: " + e.getMessage(), e);
      e.printStackTrace();
      outPort = portError;
    }
    finally {
      DatabaseInterface.closeResources(db,st,rs);
      db = null;
      st = null;
      rs = null;
    }
    return outPort;
  }



  /**
   * Fetch Process IDs
   *
   * @param dataSet a value of type 'DataSet'
   * @return RearchResutl....
   * 
   */
  private PIDSearchResult getIds(UserInfoInterface userInfo, ProcessData procData) {

    String login = userInfo.getUtilizador();

    DataSource ds = null;
    Connection db = null;
    Statement st = null;
    ResultSet rs = null;

    String stmp = null;    
    String stmp2 = null;    
    String stmp3 = null;    
    StringBuilder sbtmp = null;
    List<String> alValues = null;
    java.util.Date dttmp = null;
    HashMap<String,String> hmDestVar = new HashMap<String,String>();  // key: varname; value: destvarname

    String[][] saData = null;
    List<String> lVars = null;
    List<Triplet> lTriplets = null;

    HashMap<String,String> hmSpecialVars = new HashMap<String,String>();

    boolean bOpened = false; // indicates that at least one criteria is for opened procs
    boolean bClosed = false; // indicates that at least one criteria is for closed procs

    String sQueryExtra = "";

    try {

      int size = 0;
      int toFetch = 0;
      int toSearch = 0;

      for (;(stmp = this.getAttribute(sVAR + size)) != null; size++);


      saData = new String[size][8];

      lVars = new ArrayList<String>();

      for (int i=0; (stmp = this.getAttribute(sVAR + i)) != null; i++) {
        saData[i][nVAR] = stmp;
        saData[i][nFETCH] = this.getAttribute(sFETCH + i);
        saData[i][nMODE] = this.getAttribute(sMODE + i);

        if ((saData[i][nFETCH] == null || saData[i][nFETCH].equals(NO)) &&
            (saData[i][nMODE] == null || saData[i][nMODE].equals(NO))) {
          saData[i][nVAR] = null;
          continue;
        }

        if (saData[i][nFETCH] != null && !saData[i][nFETCH].equals(NO)) {
          toFetch++;
        }

        if (saData[i][nMODE] != null && !saData[i][nMODE].equals(NO)) {
          toSearch++;
        }

        saData[i][nDESTVAR] = this.getAttribute(sDESTVAR + i);
        if (saData[i][nDESTVAR] != null && !saData[i][nDESTVAR].equals("")) {

          Logger.debug(login,this,"after","ADDING ALT VARNAME FOR " + stmp + ": " + saData[i][nDESTVAR]);

          hmDestVar.put(stmp, saData[i][nDESTVAR]);
        }

        stmp2 = this.getAttribute(sOP + i);
        if (StringUtils.isNotEmpty(stmp2)) {
          try {
            stmp3 = procData.transform(userInfo, stmp2);
            if (StringUtils.isNotEmpty(stmp3)) {
              stmp2 = stmp3;
            }
          }
          catch (Exception e2) {
          }
          stmp3 = null;
        }
        if (StringUtils.isNotEmpty(stmp2)) stmp2 = "=";
        else stmp2 = stmp2.trim();
        saData[i][nOP] = stmp2;


        stmp2 = this.getAttribute(sVALUE + i);
        if (saData[i][nOP].equalsIgnoreCase(sOP_GROUP)) {
          if (StringUtils.isEmpty(stmp2)) {
            stmp2 = saData[i][nVAR];
          }

          if (procData.isListVar(stmp2)) {
            sbtmp = new StringBuilder();
            ProcessListVariable lvar = procData.getList(stmp2);
            for (int idx=0; idx < lvar.size(); idx++) {
              if (idx > 0) sbtmp.append(sOP_GROUP_SEPARATOR);
              sbtmp.append(lvar.getFormattedItem(idx));
            }
            stmp2 = sbtmp.toString();
          }
          else {
            if (isStringsOp(stmp2)) {
              stmp2 = getStringsForOpGroup(userInfo, procData, stmp2);
            }
            else {
              stmp2 = procData.getFormatted(stmp2);
            }
          }
        }
        else {
          if (StringUtils.isNotEmpty(stmp2)) {

            if (isStringsOp(stmp2) && saData[i][nOP].equalsIgnoreCase(sOP_LIKE)) {
              // processlater
            }
            else {

              if (isStringsOp(stmp2)) {
                // strings only allowed for like or group operator... un-stringify
                stmp2 = removeStrings(stmp2);
              }

              try {
                stmp3 = procData.transform(userInfo, stmp2);
                if (StringUtils.isNotEmpty(stmp3)){
                  if (stmp3.equals("%") || stmp3.equals("%%")) {
                    // empty value...
                    stmp2 = null;
                  }
                  else {
                    stmp2 = stmp3;
                    // try to convert to raw value...
                    try {
                      ProcessSimpleVariable sv = new ProcessSimpleVariable(procData.get(saData[i][nVAR]));
                      sv.setValue(stmp2);
                      stmp2 = sv.getRawValue();
                    }
                    catch (Exception ec) {
                    }
                  }
                }
              }
              catch (Exception e2) {
              }
              stmp3 = null;
            }
          }
          else {
            stmp2 = procData.get(saData[i][nVAR]).getRawValue();
          }
        }
        saData[i][nVALUE] = stmp2;

        saData[i][nCASE] = this.getAttribute(sCASE + i);

        stmp2 = this.getAttribute(sTYPE + i);
        if (StringUtils.isEmpty(stmp2)) {
          stmp2 = saTYPES[0];
        }
        saData[i][nTYPE] = stmp2; 
        hmSpecialVars.put(stmp2,stmp);


        if (saData[i][nFETCH] != null && saData[i][nFETCH].equals(YES)) {
          if (saData[i][nTYPE].equals(saTYPES[0])) {
            lVars.add(stmp);
          }
        }

        if (saData[i][nMODE] != null &&
            (saData[i][nMODE].equals(YES_OPEN) || 
                saData[i][nMODE].equals(YES_CLOSED) || 
                saData[i][nMODE].equals(YES_ALL))) {

          if (stmp.toLowerCase().equals(DataSetVariables.FLOWID.toLowerCase())) {

            if (saData[i][nOP].equalsIgnoreCase(sOP_GROUP)) {
              alValues = Utils.tokenize(saData[i][nVALUE], sOP_GROUP_SEPARATOR);

              if (alValues != null && alValues.size() > 0) {

                if (!sQueryExtra.equals("")) sQueryExtra += " and ";
                sQueryExtra += " flowid " + saData[i][nOP] + " (";        

                sbtmp = new StringBuilder();
                for (int idx=0; idx < alValues.size(); idx++) {
                  double dtmp = Double.NaN;
                  try {
                    dtmp = Double.parseDouble((String)alValues.get(idx));
                  }
                  catch (Exception ei) {
                    continue;
                  }

                  if (Double.isNaN(dtmp)) continue;

                  if (sbtmp.length() > 0) sbtmp.append(",");
                  sbtmp.append((int)dtmp);
                }

                sQueryExtra += sbtmp.toString() + ")";        
              }
            }
            else {

              try {
                double dtmp = Double.parseDouble(saData[i][nVALUE]);
                if (!Double.isNaN(dtmp)) {
                  if (!sQueryExtra.equals("")) sQueryExtra += " and ";
                  sQueryExtra += " flowid " + saData[i][nOP];       
                  if (!_hsUnaryOps.contains(saData[i][nOP])) {
                    sQueryExtra += " " + (int)dtmp;
                  }
                }
              }
              catch (Exception ei) {
              }
            }
          }
          else if (stmp.toLowerCase().equals(DataSetVariables.PID.toLowerCase())) {

            if (saData[i][nOP].equalsIgnoreCase(sOP_GROUP)) {
              alValues = Utils.tokenize(saData[i][nVALUE], sOP_GROUP_SEPARATOR);

              if (alValues != null && alValues.size() > 0) {

                if (!sQueryExtra.equals("")) sQueryExtra += " and ";
                sQueryExtra += " pid " + saData[i][nOP] + " (";       

                sbtmp = new StringBuilder();
                for (int idx=0; idx < alValues.size(); idx++) {
                  double dtmp = Double.NaN;
                  try {
                    dtmp = Double.parseDouble((String)alValues.get(idx));
                  }
                  catch (Exception ei) {
                    continue;
                  }

                  if (Double.isNaN(dtmp)) continue;

                  if (sbtmp.length() > 0) sbtmp.append(",");
                  sbtmp.append((int)dtmp);
                }

                sQueryExtra += sbtmp.toString() + ")";        
              }
            }
            else {
              try {
                double dtmp = Double.parseDouble(saData[i][nVALUE]);
                if (!Double.isNaN(dtmp)) {
                  if (!sQueryExtra.equals("")) sQueryExtra += " and ";
                  sQueryExtra += " pid " + saData[i][nOP];      
                  if (!_hsUnaryOps.contains(saData[i][nOP])) {
                    sQueryExtra += " " + (int)dtmp;
                  }
                }
              }
              catch (Exception ei) {
              }
            }
          }
          else if (stmp.toLowerCase().equals(DataSetVariables.SUBPID.toLowerCase())) {

            if (saData[i][nOP].equalsIgnoreCase(sOP_GROUP)) {
              alValues = Utils.tokenize(saData[i][nVALUE], sOP_GROUP_SEPARATOR);

              if (alValues != null && alValues.size() > 0) {

                if (!sQueryExtra.equals("")) sQueryExtra += " and ";
                sQueryExtra += " subpid " + saData[i][nOP] + " (";       

                sbtmp = new StringBuilder();
                for (int idx=0; idx < alValues.size(); idx++) {
                  double dtmp = Double.NaN;
                  try {
                    dtmp = Double.parseDouble((String)alValues.get(idx));
                  }
                  catch (Exception ei) {
                    continue;
                  }

                  if (Double.isNaN(dtmp)) continue;

                  if (sbtmp.length() > 0) sbtmp.append(",");
                  sbtmp.append((int)dtmp);
                }

                sQueryExtra += sbtmp.toString() + ")";        
              }
            }
            else {
              try {
                double dtmp = Double.parseDouble(saData[i][nVALUE]);
                if (!Double.isNaN(dtmp)) {
                  if (!sQueryExtra.equals("")) sQueryExtra += " and ";
                  sQueryExtra += " subpid " + saData[i][nOP];      
                  if (!_hsUnaryOps.contains(saData[i][nOP])) {
                    sQueryExtra += " " + (int)dtmp;
                  }
                }
              }
              catch (Exception ei) {
              }
            }
          }
        }
      }

      if (size == 0) {
        throw new Exception("Não foram criados critérios de pesquisa.");
      }
      if (toSearch == 0) {
        throw new Exception("Não foram especificados critérios de pesquisa.");
      }
      if (toFetch == 0) {
        throw new Exception("Não foram especificadas variáveis para obter da pesquisa.");
      }


      // DATASET LIST CLEANUP
      // now clean all dataset's vars to be searched and/or displayed
      for (int i=0; i < saData.length; i++) {
        if (saData[i][nVAR] == null) continue;

        stmp = saData[i][nDESTVAR];
        if (stmp == null || stmp.equals("")) stmp = saData[i][nVAR];
        Logger.debug(login,this,"after","Removing dataset list variable: " + stmp);
        procData.clearList(stmp);
      }

      // SEARCH

      Map<String,Integer> idxCatalog = BeanFactory.getFlowHolderBean().getFlow(userInfo, procData.getFlowId()).getIndexVars();

      sbtmp = new StringBuilder();
      sbtmp.append(sQueryExtra);
      for (int i=0; i < saData.length; i++) {

        String field = null;
        boolean addAnd = !sbtmp.equals("");
        String[] data = saData[i];

        if (data[nVAR] == null) 
          continue;

        if (StringUtils.isEmpty(data[nMODE]) || StringUtils.equals(data[nMODE],NO)) 
          continue;

        if (data[nTYPE].equals(saTYPES[3])) {
          // data criacao
          dttmp = Utils.string2date(stmp2);
          if (dttmp == null) {
            Logger.warning(login, this, "after", "creation date: unable to convert to date string: " + stmp2);
            continue;
          }
          stmp2 = Utils.genSQLDate(dttmp);
          data[nVALUE] = stmp2;            

          field = "created";

          sbtmp.append(addWhereCriteria(addAnd, field, data));
        }
        else if (idxCatalog.containsKey(data[nVAR])) {
          field = "idx" + idxCatalog.get(data[nVAR]);
          sbtmp.append(addWhereCriteria(addAnd, field, data));
        }
        else {
          // TODO: ignore or search in xml??
          Logger.warning(login,this,"after","Ignoring non-indexed search variable: " + saData[i][nVAR]);
          continue;
        }
      }

      if (sbtmp.length() == 0) {
        throw new Exception("Não existem critérios de pesquisa.");
      }

      sbtmp.insert(0, "select distinct flowid, pid, subpid from process where closed=0 and ");

      Logger.info(login,this,"after","flowid=" 
          + procData.getFlowId() + ", pid=" + procData.getPid() + ", subpid=" + procData.getSubPid() +": QUERY=" + sbtmp.toString());

      lTriplets = new ArrayList<Triplet>();
      if (sbtmp.length() > 0) {
        // open db connection
        ds = Utils.getDataSource();
        db = ds.getConnection();
        st = db.createStatement();

        try {
          rs = st.executeQuery(sbtmp.toString());
          while (rs.next()) {
            Triplet t = new Triplet();
            t.flowid = rs.getInt("flowid");
            t.pid = rs.getInt("pid");
            t.subpid = rs.getInt("subpid");
            lTriplets.add(t);
          }
          rs.close();
          rs = null;
        }
        catch (Exception e) {
          Logger.debug(login,this,"after","error: " + e.getMessage());
          lTriplets = null;
        }
      }
    }
    catch (SQLException sqle) {
      Logger.error(login,this,"after","caught sql exception: " + sqle.getMessage());
      sqle.printStackTrace();
    }
    catch (Exception e) {
      Logger.error(login,this,"after","caught exception: " + e.getMessage());
      e.printStackTrace();
    }
    finally {
      DatabaseInterface.closeResources(db,st,rs);
    }
    PIDSearchResult result = new PIDSearchResult();
    result.bClosed = bClosed;
    result.bOpened = bOpened;
    result.pids = lTriplets != null ? (Triplet[])lTriplets.toArray(new Triplet[lTriplets.size()]) : null;
    result.hmSpecialVars = hmSpecialVars;
    result.hmDestVars = hmDestVar;
    result.sQueryExtra = sQueryExtra;
    result.saVars = lVars != null ? (String [])lVars.toArray(new String[lVars.size()]) : null;

    return result;
  }


  private String addWhereCriteria(boolean appendAnd, String field, String[] data) {
    StringBuilder sb = new StringBuilder();

    if (appendAnd) sb.append(" and ");

    if ((StringUtils.equals(data[nCASE],YES)) ||
        _hsUnaryOps.contains(data[nOP])) {
      sb.append(" ").append(field).append(" ");
    }
    else {
      sb.append(" lower(").append(field).append(") ");
      data[nVALUE] = data[nVALUE].toLowerCase();
    }

    sb.append(data[nOP]);

    if (!_hsUnaryOps.contains(data[nOP])) {
      sb.append(" ");

      if (data[nOP].toLowerCase().indexOf(sOP_LIKE) > -1) {
        sb.append("'%");
      }
      else if (data[nOP].equalsIgnoreCase(sOP_GROUP)) {
        sb.append("(");
      }
      else {
        sb.append("'");
      }

      sb.append(data[nVALUE]);

      if (data[nOP].toLowerCase().indexOf(sOP_LIKE) > -1) {
        sb.append("%'");
      }
      else if (data[nOP].equalsIgnoreCase(sOP_GROUP)) {
        sb.append(") ");
      }
      else {
        sb.append("'");
      }
    }

    return sb.toString();
  }

  private static boolean isStringsOp(String sVal) {
    boolean retObj = false;

    if (StringUtils.isNotEmpty(sVal) && sVal.trim().toLowerCase().startsWith(sMETHOD_STRINGS_PREFIX)) {
      retObj = true;
    }

    return retObj;
  }

  private static String removeStrings(String sVal) {
    String retObj = sVal;

    try {
      retObj = sVal.substring(sMETHOD_STRINGS_PREFIX.length());
      retObj = retObj.substring(0,retObj.indexOf(sMETHOD_STRINGS_SUFFIX));
    }
    catch (Exception e) {
      retObj = sVal;
    }

    return retObj;
  }

  private String processStrings(UserInfoInterface userInfo, ProcessData procData, String sVal) {
    String retObj = sVal;

    String stmp = removeStrings(sVal);

    try {
      retObj = procData.transform(userInfo, stmp);
    }
    catch (Exception e) {
      retObj = null;
    }
    if (StringUtils.isEmpty(retObj)) retObj = stmp;

    return retObj;
  }

  private String getStringsForOpGroup(UserInfoInterface userInfo, ProcessData procData, String sVal) {
    StringBuffer retObj = new StringBuffer();

    List<String> alValues = Utils.tokenize(processStrings(userInfo, procData, sVal), " ");

    for (int i=0; i < alValues.size(); i++) {
      if (i > 0) retObj.append(",");
      retObj.append((String)alValues.get(i));
    }

    return retObj.toString();
  }

  public String getDescription (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, true, "Pesquisa Processo");
  }

  public String getResult (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, false, "Pesquisa Efectuada");
  }

  public String getUrl (UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }

  private static class PIDSearchResult {
    Triplet [] pids = null;
    boolean bOpened = false;
    boolean bClosed = false;
    HashMap<String,String> hmSpecialVars = null;
    HashMap<String,String> hmDestVars = null;
    String [] saVars = null;
    String sQueryExtra = null;
  }

  private static class Triplet {
    int flowid;
    int pid;
    int subpid;
    public String toString() {
      return flowid+"/"+pid+"/"+subpid;
    }
  }

}
