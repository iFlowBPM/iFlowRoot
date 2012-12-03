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

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.core.AuthProfile;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessListVariable;
import pt.iflow.api.userdata.IMappedData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;

/**
 * <p>Title: BlockGetAuthInfo</p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: iKnow</p>
 * @author
 */

public class BlockGetUsersInProfile extends Block {
  public Port portIn, portSuccess, portEmpty, portError;

  private static final int MAX_USERS = 100;
  private static final String PROFILE = "_profilefield_";

  public BlockGetUsersInProfile(int anFlowId, int id, int subflowblockid, String filename) {
    super(anFlowId, id, subflowblockid, filename);
    hasInteraction = false;
    saveFlowState = false;
  }

  public Port getEventPort() {
    return null;
  }

  public Port[] getInPorts (UserInfoInterface userInfo) {
    Port[] retObj = new Port[1];
    retObj[0] = portIn;
    return retObj;
  }

  public Port[] getOutPorts(UserInfoInterface userInfo) {
    Port[] retObj = new Port[3];
    retObj[0] = portSuccess;
    retObj[1] = portEmpty;
    retObj[2] = portError;
    return retObj;
  }

  /**
   * No action in this block
   * 
   * @param dataSet
   *          a value of type 'DataSet'
   * @return always 'true'
   */
  public String before(UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }

  /**
   * No action in this block
   * 
   * @param dataSet
   *          a value of type 'DataSet'
   * @return always 'true'
   */
  public boolean canProceed(UserInfoInterface userInfo, ProcessData procData) {
    return true;
  }

  /**
   * Executes the block main action
   * 
   * @param dataSet
   *          a value of type 'DataSet'
   * @return the port to go to the next block
   */
  public Port after(UserInfoInterface userInfo, ProcessData procData) {
    Port outPort = portEmpty;
    String login = userInfo.getUtilizador();
    StringBuffer logMsg = new StringBuffer();

    try {
      String sProfileString = this.getAttribute(PROFILE);
      if (StringUtils.isEmpty(sProfileString)) {
        Logger.error(login, this, "after", "empty value for profile attribute");
        outPort = portError;
      } else {
        String sProfile = procData.transform(userInfo, sProfileString);

        Logger.debug(login, this, "after", "profile=" + sProfile);

        if (sProfile != null) {
          sProfile = sProfile.trim();
        }

        if (StringUtils.isEmpty(sProfile)) {
          Logger.error(login, this, "after", "empty value for parsed profile");
          outPort = portError;
        } else {
          Hashtable<String,String> fields = getFields();
          if (fields.size() == 0) {
            Logger.error(login,this,"after","no output variables configured");  
            outPort = portError;
          } else {
            
            // list cleanup
            {
              Iterator<String> fieldsIt = fields.keySet().iterator();
              while (fieldsIt.hasNext()) {
                String field = fieldsIt.next();
                String var = fields.get(field);
                ProcessListVariable pvar = procData.getList(var);
                pvar.clear();
                Logger.debug(login, this, "after", 
                    procData.getSignature() + "cleared list var " + var);
              }
            }
            
            AuthProfile ap = BeanFactory.getAuthProfileBean();
            Collection<String> users = ap.getUsersInProfile(userInfo, sProfile);
            if (users.size() == 0) {
              Logger.info(login,this,"after","no users found in profile" + sProfile);  
              outPort = portEmpty;
            } else {
              Iterator<String> usersIt = users.iterator();
              int counter = 0;
              while (usersIt.hasNext()) {

                if (counter > MAX_USERS) {
                  Logger.info(login,this,"after","found more than " + MAX_USERS + " for profile " + sProfile + "... ignoring them...");
                  break;
                }

                IMappedData mdInfo = ap.getUserInfo(usersIt.next());

                Iterator<String> fieldsIt = fields.keySet().iterator();
                while (fieldsIt.hasNext()) {
                  String field = fieldsIt.next();
                  String var = fields.get(field);
                  String value = mdInfo.get(field);
                  ProcessListVariable pvar = procData.getList(var);
                  pvar.parseAndSetItemValue(counter, value);
                  logMsg.append("Set '" + var + "[" + counter + "]" + "' as '" + value + "';");
                  procData.setList(pvar);
                }

                counter++;
              }
              outPort = portSuccess;
            }
          }
        }
      }
    } catch (Exception e) {
      Logger.error(login, this, "after", 
          procData.getSignature() + "caught exception: " + e.getMessage(), e);
      outPort = portError;
    }

    logMsg.append("Using '" + outPort.getName() + "';");
    Logger.logFlowState(userInfo, procData, this, logMsg.toString());
    return outPort;
  }

  private Hashtable<String,String> getFields() {
    Hashtable<String,String> fields = new Hashtable<String, String>(); 
    Iterator<String> itera = this.getAttributeMap().keySet().iterator();
    while (itera.hasNext()) {
      final String sAttrName = itera.next();

      if (StringUtils.isEmpty(sAttrName) || sAttrName.equals(PROFILE) || sAttrName.equals(sDESCRIPTION) || sAttrName.equals(sRESULT))
        continue;

      String sAttrValue = this.getAttribute(sAttrName);

      if (StringUtils.isEmpty(sAttrValue))
        continue;
      else 
        sAttrValue = sAttrValue.trim();

      fields.put(sAttrName, sAttrValue);
    }

    return fields;
  }

  public String getDescription(UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, true, "Get Users in Profile");
  }

  public String getResult (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, false, "Got Users in Profile");
  }

  public String getUrl (UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }

}
