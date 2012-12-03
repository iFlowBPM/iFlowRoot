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

import java.util.HashSet;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.ProcessManager;
import pt.iflow.api.events.AbstractEvent;
import pt.iflow.api.events.WaitEvent;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;

/**
 * <p>Title: BlockJuncaoExclusiva</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: iKnow</p>
 * @author Pedro Monteiro
 */

public class BlockJuncaoExclusiva extends Block {
  public Port portIn, portInSubProc, portOut;

  HashSet<String> _hsLocks = new HashSet<String>();
  
  public BlockJuncaoExclusiva(int anFlowId,int id, int subflowblockid, String filename) {
    super(anFlowId,id, subflowblockid, filename);
    hasInteraction = false;
    bProcInDBRequired = true;
    canRunInPopupBlock = false;
  }

  private synchronized void procLock(ProcessData procData) {
      String key = procData.getFlowId() + "/" + procData.getPid() + "/" + this.getId();
      _hsLocks.add(key);
  }
 
  public Port[] getOutPorts (UserInfoInterface userInfo) {
      Port[] retObj = new Port[1];
      retObj[0] = portOut;
      return retObj;
  }
  
  public Port getEventPort() {
      return null;
  }
  
  public Port[] getInPorts (UserInfoInterface userInfo) {
      Port[] retObj = new Port[2];
      retObj[0] = portIn;
      retObj[1] = portInSubProc;
      return retObj;
  }

  /**
   * No action in this block
   *
   * @param dataSet a value of type 'DataSet'
   * @return always 'true'
   */
  public String before(UserInfoInterface userInfo, ProcessData procData) {
      String login = userInfo.getUtilizador();
      
        ProcessManager pm = BeanFactory.getProcessManagerBean();
          
          String key = procData.getFlowId() + "/" + procData.getPid() + "/" + this.getId();
          // if it was the first to arrive
          if (!_hsLocks.contains(key) && 
              !pm.checkBlockIsLocked(userInfo, procData, this.getId())) {

              this.procLock(procData); // lock in memory

              // deploy mines between start block till this block
              pm.deployMinesInProcess(userInfo, procData, this.getId());
              
              // this must be after deploy mines
              // cause field 'locked' is inside mined record
              pm.lockBlock(userInfo, procData, this.getId()); // lock in DB
              
          } else {

              AbstractEvent wt = null;
              
              while (_hsLocks.contains(key) || 
                      pm.checkBlockIsLocked(userInfo, procData, this.getId())) {
                  Logger.debug(login, this, "before", 
                    "BlockJuncaoExclusiva: before: While LOCK!!!");

                  if (wt == null) wt = new WaitEvent(100);
                  
                  wt.processEvent();
              }
          }
      
      return "";
  }

  /**
   * No action in this block
   *
   * @param dataSet a value of type 'DataSet'
   * @return 
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
      Port outPort = null;

        ProcessManager pm = BeanFactory.getProcessManagerBean();
      
          String key = procData.getFlowId() + "/" + procData.getPid() + "/" + this.getId();
          _hsLocks.remove(key);
          pm.unlockBlock(userInfo, procData, this.getId());
          
          outPort = portOut;
       return outPort;
  }

  public String getDescription (UserInfoInterface userInfo, ProcessData procData) {
      return this.getDesc(userInfo, procData, true, "Junção AND de Processo.");
  }

  public String getResult (UserInfoInterface userInfo, ProcessData procData) {
      return this.getDesc(userInfo, procData, false, "Junção AND de Processo Concluída.");
  }

  public String getUrl (UserInfoInterface userInfo, ProcessData procData) {
      return "";
  }  
}
