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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.core.Activity;
import pt.iflow.api.core.AuthProfile;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.ProcessManager;
import pt.iflow.api.flows.Flow;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessHeader;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;
import pt.iflow.blocks.interfaces.OpenProc;

/**
 * <p>Title: BlockOpenProc</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: iKnow</p>
 * @author
 */

public class BlockOpenProc extends Block implements OpenProc {
  public Port portIn, portFront, portError;

  private static final String sOPEN_PROC_TYPE_LEGACY = "Tipo de abertura";
  private static final String sOPEN_PROC_FID_LEGACY = "ID do fluxo a abrir";
  private static final String sOPEN_PROC_PID_LEGACY = "ID do processo a abrir";
  private static final String sOPEN_PROC_SUBPID_LEGACY = "ID do sub-processo a abrir";
  private static final String sOPEN_PROC_MODE_LEGACY = "Modo de abertura";
  private static final String sOPEN_PROC_BID_LEGACY = "ID do bloco a abrir";
  private static final String sOPEN_PROC_USERMODE_LEGACY = "Abrir em";
  private static final String sOPEN_PROC_USER_LEGACY = "Utilizador/Perfil";
  private static final String sOPEN_PROC_DESCRIPTION_LEGACY = "Descricao abertura";
  private static final String sOPEN_PROC_2IMPORT_LEGACY = "Variaveis a importar";

  private final static String sOPEN_PROC_TYPE_ATTR = "TYPE";
  private final static String sOPEN_PROC_FID_ATTR = "FID";
  private final static String sOPEN_PROC_PID_ATTR = "PID";
  private final static String sOPEN_PROC_SUBPID_ATTR = "SUBPID";
  private final static String sOPEN_PROC_MODE_ATTR = "MODE";
  private final static String sOPEN_PROC_BID_ATTR = "BID";
  private final static String sOPEN_PROC_USERMODE_ATTR = "USERMODE"; 
  private final static String sOPEN_PROC_USER_ATTR = "USER";
  private final static String sOPEN_PROC_DESCRIPTION_ATTR = "DESCRIPTION"; 
  private final static String sOPEN_PROC_2IMPORT_ATTR = "2IMPORT";
  private final static String sOPEN_PROC_CREATED_PID_ATTR = "CREATED_PID";


  public BlockOpenProc(int anFlowId,int id, int subflowblockid, String filename) {
    super(anFlowId,id, subflowblockid, filename);
    hasInteraction = false;
  }

  public Port[] getOutPorts (UserInfoInterface userInfo) {
    Port[] retObj = new Port[2];
    retObj[0] = portFront;
    retObj[1] = portError;
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
   *
   * @param dataSet a value of type 'DataSet'
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
    Port outPort = portFront;
    StringBuffer logMsg = new StringBuffer();

    String sType = null;
    int nFid = -1;
    int nPid = -1;
    int nSubPid = -1;
    int nBid = -1;
    String sMode = null;
    String sUserMode = null;
    String sUser = null;
    String sDescription = null;
    String s2Import = null;
    String sCreatedPid = null;
    List<String> al2Import = null;
    ProcessManager pm = BeanFactory.getProcessManagerBean();
    ProcessData newProcData = null;
    ProcessData oldProcData = null;
    String sCreator = null;
    UserInfoInterface newUserInfo = null;
    List<String> alUsers = null;

    try {

      // PROCESS ATTRIBUTES

      // OPEN TYPE
      try {
        sType = getAttr(sOPEN_PROC_TYPE_ATTR, sOPEN_PROC_TYPE_LEGACY);
      }                                                  
      catch (Exception e) {
        sType = null;
      }

      // FID
      try {
        String stmp = getAttr(sOPEN_PROC_FID_ATTR, sOPEN_PROC_FID_LEGACY);
        String stmp2 = procData.transform(userInfo, stmp);    
        if (stmp2 != null) stmp = stmp2;
        nFid = (int)Double.parseDouble(stmp);                   
      }                                                  
      catch (Exception e) {
        nFid = -1;
      }                                                  


      // PID
      try {
        String stmp = getAttr(sOPEN_PROC_PID_ATTR, sOPEN_PROC_PID_LEGACY);
        String stmp2 = procData.transform(userInfo, stmp);
        if (stmp2 != null) stmp = stmp2;
        nPid = (int)Double.parseDouble(stmp);
      }
      catch (Exception e) {
        nPid = -1;
      }

      // SUBPID
      try {
        String stmp = getAttr(sOPEN_PROC_SUBPID_ATTR, sOPEN_PROC_SUBPID_LEGACY);
        String stmp2 = procData.transform(userInfo, stmp);
        if (stmp2 != null) stmp = stmp2;
        nSubPid = (int)Double.parseDouble(stmp);
      }
      catch (Exception e) {
        nSubPid = -1;
      }


      // BID
      try {
        String stmp = getAttr(sOPEN_PROC_BID_ATTR, sOPEN_PROC_BID_LEGACY);
        String stmp2 = procData.transform(userInfo, stmp);
        if (stmp2 != null) stmp = stmp2;
        nBid = (int)Double.parseDouble(stmp);
      }
      catch (Exception e) {
        nBid = -1;
      }


      // MODE
      try {
        sMode = getAttr(sOPEN_PROC_MODE_ATTR, sOPEN_PROC_MODE_LEGACY);
      }                                                  
      catch (Exception e) {
        sMode = null;
      }

      // USERMODE
      try {
        sUserMode = getAttr(sOPEN_PROC_USERMODE_ATTR, sOPEN_PROC_USERMODE_LEGACY);
      }                                                  
      catch (Exception e) {
        sUserMode = null;
      }


      // USER
      try {
        sUser = getAttr(sOPEN_PROC_USER_ATTR, sOPEN_PROC_USER_LEGACY);
        String stmp = procData.transform(userInfo, sUser);
        if (stmp != null) sUser = stmp;
      }
      catch (Exception e) {
        sUser = null;
      }

      //DESCRIPTION
      try {
        sDescription = getAttr(sOPEN_PROC_DESCRIPTION_ATTR, sOPEN_PROC_DESCRIPTION_LEGACY);
        String stmp = procData.transform(userInfo, sDescription);
        if (stmp != null) sDescription = stmp;
      }
      catch (Exception e) {
        sDescription = null;
      }

      // IMPORT VARS
      s2Import = getAttr(sOPEN_PROC_2IMPORT_ATTR, sOPEN_PROC_2IMPORT_LEGACY);


      // CREATED PID
      sCreatedPid = getAttr(sOPEN_PROC_CREATED_PID_ATTR, null);


      if (Logger.isDebugEnabled()) {
        // DEBUG
        Logger.debug(userInfo.getUtilizador(), this, "after", "Attr TYPE=" + sType);
        Logger.debug(userInfo.getUtilizador(), this, "after", "Attr FID=" + nFid);
        Logger.debug(userInfo.getUtilizador(), this, "after", "Attr PID=" + nPid);
        Logger.debug(userInfo.getUtilizador(), this, "after", "Attr SUBPID=" + nSubPid);
        Logger.debug(userInfo.getUtilizador(), this, "after", "Attr BID=" + nBid);
        Logger.debug(userInfo.getUtilizador(), this, "after", "Attr MODE=" + sMode);
        Logger.debug(userInfo.getUtilizador(), this, "after", "Attr USERMODE=" + sUserMode);
        Logger.debug(userInfo.getUtilizador(), this, "after", "Attr USER=" + sUser);
        Logger.debug(userInfo.getUtilizador(), this, "after", "Attr DESCRIPTION=" + sDescription);
        Logger.debug(userInfo.getUtilizador(), this, "after", "Attr 2IMPORT=" + s2Import);
        // DEBUG END
      }

      // VAR PROCESSING AND VALIDATIONS

      // TYPE
      if (StringUtils.isEmpty(sType)) {
        Logger.error(userInfo.getUtilizador(), this, "after", "Attribute TYPE not defined (PID=" + procData.getPid() + ")");
        outPort = portError;
      } else {
        boolean validType = false;
        for (int i=0; i < openProcessTypes.length; i++) {
          if (openProcessTypes[i].equals(sType)) {
            validType=true;
            break;
          }
        }
        if (!validType) {
          Logger.error(userInfo.getUtilizador(), this, "after", "Attribute TYPE invalid (PID=" + procData.getPid() + ")");
          outPort = portError;
        } else {
          // FID
          if (nFid <= 0) {
            Logger.error(userInfo.getUtilizador(), this, "after", "Attribute FID not defined (PID=" + procData.getPid() + ")");
            outPort = portError;
          } else {
            if (sType.equals(openProcessTypes[0])) {
              // PID
              if (nPid <= 0) {
                Logger.error(userInfo.getUtilizador(), this, "after", "Attribute PID not defined (PID=" + procData.getPid() + ")");
                outPort = portError;
              } else {
             // SUBPID
                if (nSubPid <= 0) {
                  Logger.error(userInfo.getUtilizador(), this, "after", "Attribute SUBPID not defined (PID=" + procData.getPid() + ")");
                  outPort = portError;
                } else {
                  // MODE
                  if (StringUtils.isEmpty(sMode)) {
                    Logger.error(userInfo.getUtilizador(), this, "after", "Attribute MODE not defined (PID=" + procData.getPid() + ")");
                    outPort = portError;
                  } else {
                    boolean validMode = false;
                    for (int i=0; i < openProcessModes.length; i++) {
                      if (openProcessModes[i].equals(sMode)) {
                        validMode=true;
                        break;
                      }
                    }
                    if (!validMode) {
                      Logger.error(userInfo.getUtilizador(), this, "after", "Attribute MODE invalid (PID=" + procData.getPid() + ")");
                      outPort = portError;
                    }
                  }
                }
              }
            }

            if (!outPort.equals(portError)) {
              // USER MODE
              if (StringUtils.isEmpty(sUserMode)) {
                Logger.error(userInfo.getUtilizador(), this, "after", "Attribute USERMODE not defined (PID=" + procData.getPid() + ")");
                outPort = portError;
              } else {
                boolean validUserMode = false;
                for (int i=0; i < userModes.length; i++) {
                  if (userModes[i].equals(sUserMode)) {
                    validUserMode=true;
                    break;
                  }
                }

                if (!validUserMode) {
                  Logger.error(userInfo.getUtilizador(), this, "after", "Attribute USERMODE invalid (PID=" + procData.getPid() + ")");
                  outPort = portError;
                } else {
                  if (sUserMode.equals(userModes[0])) {
                    if (!sType.equals(openProcessTypes[0])) {
                      // TYPE
                      Logger.error(userInfo.getUtilizador(), this, "after", 
                          "Attribute USERMODE is not compatible with selected type (PID=" + procData.getPid() + ")");
                      outPort = portError;    
                    } else if (nPid <= 0) {
                      // PID
                      Logger.error(userInfo.getUtilizador(), this, "after", 
                          "Attribute PID not defined (required for usermode " + userModes[0] + ") (PID=" + procData.getPid() + ")");
                      outPort = portError;
                    } else if (nSubPid <= 0) {
                      // SUBPID
                      Logger.error(userInfo.getUtilizador(), this, "after", 
                          "Attribute SUBPID not defined (required for usermode " + userModes[0] + ") (PID=" + procData.getPid() + ")");
                      outPort = portError;
                    }
                  } else {
                    // USER
                    if (StringUtils.isEmpty(sUser)) {
                      if (sUserMode.equals(userModes[3])) {
                        Logger.info(userInfo.getUtilizador(), this, "after", 
                            "Attribute USER not defined and USERMODE " + sUserMode + 
                            ". Assuming current user for USER attribute (PID=" + procData.getPid() + ")");
                        sUser = userInfo.getUtilizador();
                      } else {
                        Logger.error(userInfo.getUtilizador(), this, "after", "Attribute USER not defined (PID=" + procData.getPid() + ")");
                        outPort = portError;
                      }
                    }
                  }

                  if (!outPort.equals(portError)) {
                    if (StringUtils.isNotEmpty(s2Import)) {
                      al2Import = Utils.tokenize(s2Import, VAR_SEPARATOR);
                    }
                    // VAR PROCESSING AND VALIDATIONS END

                    // DEBUG
                    if (Logger.isDebugEnabled()) {
                      Logger.debug(userInfo.getUtilizador(), this, "after", "Attr after validations TYPE=" + sType);
                      Logger.debug(userInfo.getUtilizador(), this, "after", "Attr after validations FID=" + nFid);
                      Logger.debug(userInfo.getUtilizador(), this, "after", "Attr after validations PID=" + nPid);
                      Logger.debug(userInfo.getUtilizador(), this, "after", "Attr after validations SUBPID=" + nSubPid);
                      Logger.debug(userInfo.getUtilizador(), this, "after", "Attr after validations BID=" + nBid);
                      Logger.debug(userInfo.getUtilizador(), this, "after", "Attr after validations MODE=" + sMode);
                      Logger.debug(userInfo.getUtilizador(), this, "after", "Attr after validations USERMODE=" + sUserMode);
                      Logger.debug(userInfo.getUtilizador(), this, "after", "Attr after validations USER=" + sUser);
                      Logger.debug(userInfo.getUtilizador(), this, "after", "Attr after validations DESCRIPTION=" + sDescription);
                      Logger.debug(userInfo.getUtilizador(), this, "after", "Attr after validations 2IMPORT=" + s2Import);
                    }
                    // DEBUG END

                    // old proc data
                    if (sType.equals(openProcessTypes[0])) {
                      ProcessHeader oldProcHeader = new ProcessHeader(nFid, nPid, nSubPid);

                      // Vai buscar os dados/dataset do processo antigo 
                      oldProcData = pm.getProcessData(userInfo, oldProcHeader, Const.nCLOSED_PROCS_READONLY);

                      if (oldProcData == null) {
                        Logger.error(userInfo.getUtilizador(), this, "after", 
                            "No DataSet found for fid="+nFid+" and pid="+nPid+" and subpid="+nSubPid+" (PID="+procData.getPid()+")");
                        outPort = portError;
                      } else if (sUserMode.equals(userModes[0])) {
                        // Vai buscar o criador do processo antigo
                        sCreator = oldProcData.getCreator();
                      }
                    }

                    if (!outPort.equals(portError)) {
                      // CREATOR
                      if (sCreator == null) {
                        // if profile, select first user from users in profile; else use sUser

                        if (sUserMode.equals(userModes[3])) {
                          sCreator = sUser;
                        }
                        else {
                          AuthProfile ap = BeanFactory.getAuthProfileBean();

                          Collection<String> liUsers = ap.getUsersInProfile(userInfo, sUser);

                          if (liUsers != null && !liUsers.isEmpty()){
                            alUsers = new ArrayList<String>(liUsers);
                            sCreator = alUsers.get(0);
                          }
                        }
                      }

                      if (sCreator == null) {
                        Logger.error(userInfo.getUtilizador(), this, "after", "Not able to define creator (PID=" + procData.getPid() + ")");
                        outPort = portError;
                      } else {
                        try {
                          newUserInfo = BeanFactory.getUserInfoFactory().newUserInfoDelegate(this, sCreator);

                          // Criacao de um novo processo para o fluxo fid
                          newProcData = pm.createProcess(newUserInfo, nFid);

                          if (sType.equals(openProcessTypes[0])) {
                            // importa os dados do processo antigo sem "estragar" os da inicializacao do novo
                            ProcessData tempProc = new ProcessData(newProcData);
                            newProcData.importDataFrom(oldProcData);
                            newProcData.importDataFrom(tempProc);
                          }

                          // import additional variables
                          if (al2Import != null && !al2Import.isEmpty()) {
                            // import each variable in al2Import from local process to new process
                            for (String sVarImport : al2Import) {
                              String sVar = sVarImport.trim();
                              try {
                                if (procData.isListVar(sVar)) {
                                  newProcData.setList(procData.getList(sVar));
                                }
                                else {
                                  newProcData.set(procData.get(sVar));
                                }
                              }catch (Exception e) {
                                Logger.error(userInfo.getUtilizador(), this, "after", "Error migrating variable: " + sVar, e);
                                throw(e);
                              }
                            }   
                          }

                          this.saveDataSet(newUserInfo, newProcData);

                          Logger.info(userInfo.getUtilizador(), this, "after", 
                              procData.getSignature() + 
                              "created process " + newProcData.getSignature() + " with mid " + newProcData.getMid() + 
                              " for user " + newUserInfo.getUtilizador());

                          
                          // int startBid = flow.getStartBlockId(newUserInfo, newDataSet);
                          Block block = null;
                          Flow flow = BeanFactory.getFlowBean();

                          // se for jumpTo, tem de saber para onde!!
                          // nao inicializa as variaveis do inicio do fluxo ate ao blockID
                          if (sMode.equals(openProcessModes[1]) && nBid > 0) {
                            flow.jumpToBlock(newUserInfo, newProcData, nBid);
                            block = flow.getBlock(newUserInfo, newProcData);

                          } // e para avancar normalmente... "passThru"
                          else {
                            flow.nextBlock(newUserInfo, newProcData);        
                            block = flow.getBlock(newUserInfo, newProcData);

                            if (nBid > 0) {

                              // faz o passThru qd o Bid foi indicado (nBid > 0)
                              while (!block.isEndBlock() && nBid > 0) {

                                int currBlock = block.getId();

                                // first check if reached bid
                                if (currBlock == nBid) {
                                  break;
                                }
                                // now check if process already passed bid during nextBlock execution
                                List<String> alStates = flow.getFlowStates(userInfo, newProcData);
                                if (alStates != null && alStates.indexOf(String.valueOf(nBid)) > -1) {
                                  // process already passed bid... stop
                                  break;
                                }

                                //String user = newProcData.getFormatted(DataSetVariables.USER_ID);
                                String user = newProcData.getCurrentUser();
                                try {
                                  newUserInfo = BeanFactory.getUserInfoFactory().newUserInfoDelegate(this, user);
                                } catch(Exception e) {
                                  Logger.debug(newUserInfo.getUtilizador(), this, "after", 
                                      "Could not create an UserInfo for user=" + user + " (PID=" + newProcData.getPid() + ")");
                                  outPort = portError;
                                  break;
                                }

                                flow.nextBlock(newUserInfo, newProcData);
                                block = flow.getBlock(newUserInfo, newProcData);

                                if (currBlock == block.getId()) {
                                  // same state... stop
                                  break;
                                }
                              }
                            }
                          }

                          if (!outPort.equals(portError)) {
                            int newPid = newProcData.getPid();
                            int newSubPid = newProcData.getSubPid();

                            this.saveDataSet(newUserInfo, newProcData);

                            if (StringUtils.isNotEmpty(sCreatedPid)) {
                              procData.parseAndSet(sCreatedPid, String.valueOf(newPid));
                            }

                            Logger.info(userInfo.getUtilizador(), this, "after", 
                                procData.getSignature() + "created process " + newProcData.getSignature() + " stopped execution.");
                            
                            // now schedule process in user
                            try {
                              if (StringUtils.isEmpty(sDescription)) {
                                sDescription = block.getDescription(newUserInfo, newProcData); 
                              }

                              String url = Block.getDefaultUrl(newUserInfo, newProcData);

                              Activity activity = new Activity(newUserInfo.getUtilizador(), 
                                  nFid,
                                  newPid,
                                  newSubPid,
                                  0,
                                  0,
                                  sDescription, 
                                  url);
                              activity.mid = newProcData.getMid();
                              pm.updateActivity(newUserInfo, activity);

                              // check if need to create activity for any profile
                              if (newUserInfo.getUtilizador().equals(sCreator) &&
                                  (sUserMode.equals(userModes[1]) || sUserMode.equals(userModes[2])) &&
                                  alUsers != null && !alUsers.isEmpty()) {
                                // process still in creator and target user is a profile...
                                for (String u : alUsers) {

                                  if (u.equals(sCreator)) continue;

                                  activity = new Activity(u, 
                                      nFid,
                                      newPid,
                                      newSubPid,
                                      0,
                                      0,
                                      sDescription, 
                                      url);
                                  activity.profilename = u;
                                  activity.mid = newProcData.getMid();
                                  pm.createActivity(newUserInfo, activity, true, true);
                                }
                              }
                            } catch (Exception ei) {
                              outPort = portError;
                            }
                          }
                        } catch(Exception e) {
                          Logger.error(userInfo.getUtilizador(), this, "after", 
                              procData.getSignature() + "Could not create an UserInfo for user=" + sCreator, e);
                          outPort = portError;
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    } catch (Exception e) {
      Logger.error(userInfo.getUtilizador(), this, "after", 
          procData.getSignature() + "Caught Exception: " + e.getMessage(), e);
      outPort = portError;
    }

    logMsg.append("Using '" + outPort.getName() + "';");
    Logger.logFlowState(userInfo, procData, this, logMsg.toString());
    return outPort;
  }

  public String getDescription (UserInfoInterface userInfo, ProcessData procData) {
    String retObj = "Criacao de Processo.";
    return this.getDesc(userInfo, procData,true,retObj);
  }

  public String getResult (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, false, "Criacao de Processo Concluida");
  }

  public String getUrl (UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }

  public Object execute (int op, Object[] aoa) {
    Object retObj = null;
    return retObj;
  }

  private String getAttr(String attr, String legacy) {
    String val = getAttribute(attr);
    if (StringUtils.isNotEmpty(val))
      return val;
    
    if (legacy != null) {
      val = getAttribute(legacy);
      if (StringUtils.isNotEmpty(val))
        return val;
    }

    return null;     
  }
}
