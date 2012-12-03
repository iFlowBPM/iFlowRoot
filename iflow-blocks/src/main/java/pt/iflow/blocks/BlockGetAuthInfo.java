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
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.core.AuthProfile;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.userdata.IMappedData;
import pt.iflow.api.userdata.OrganizationalUnitData;
import pt.iflow.api.userdata.UserData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;

/**
 * <p>Title: BlockGetAuthInfo</p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: iKnow</p>
 * @author
 */

public class BlockGetAuthInfo extends Block {
  public Port portIn, portSuccess, portError;

  protected static final int nOP_GET_USER_INFO = 0;
  protected static final int nOP_GET_ORG_UNIT_INFO = 1;
  protected static final int nOP_GET_ORG_UNIT_PARENT = 2;
  
  protected static final String sDEFAULT_KEY_USER_ID = UserData.ID;
  protected static final String sDEFAULT_KEY_NUM_EMP = UserData.EMPLOYEE_NUMBER;
  protected static final String sDEFAULT_KEY_USER_EMAIL = UserData.EMAIL;
  protected static final String sDEFAULT_KEY_ORGANICAL_UNIT = OrganizationalUnitData.UNITCODE;

  protected static final char cINPUT = 'I';
  protected static final char cDEFAULT = 'D';
  protected static final char cNON_DEFAULT = 'N';
  protected static final char cOUTPUT = 'O';

  private static final String sKEY_ATTRIBUTE = "_inputfield_";

  protected int _nOp = -1;
  protected String _sDefaultKey = null;
  protected String _sDefaultKey2 = null;
  protected String _sDefaultKey3 = null;
  
  public BlockGetAuthInfo(int anFlowId, int id, int subflowblockid, String filename) {
    super(anFlowId, id, subflowblockid, filename);
    hasInteraction = false;
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
    Port[] retObj = new Port[2];
    retObj[0] = portSuccess;
    retObj[1] = portError;
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
    Port outPort = portSuccess;
    StringBuffer logMsg = new StringBuffer();
    String login = userInfo.getUtilizador();

    try {
      String sKeyName = null;
      String sKeyList = null;
      String sKey = null;
      final List<IMappedData> alInfos = new ArrayList<IMappedData>();
      IMappedData mdInfo = null;

      sKeyName = this.getKeyAttributeName(userInfo);

      String sKeyAttribute = this.getAttribute(sKeyName);
      sKeyList = procData.transform(userInfo, sKeyAttribute);

      Logger.debug(login, this, "after", "keyname=" + sKeyName
          + "; keyattribute=" + sKeyAttribute + "; keyattributevalue=" + sKeyList);

      if (sKeyList != null) {
        sKeyList = sKeyList.trim();
      }

      if (StringUtils.isEmpty(sKeyList)) {
        Logger.error(login, this, "after", 
            procData.getSignature() + "empty value for key " + sKeyName);
        outPort = portError;
      } else {
        AuthProfile ap = BeanFactory.getAuthProfileBean();
        // add support for multiple values in key attribute
        List<String> alKeys = Utils.tokenize(sKeyList, ";");
        if (alKeys == null) {
          alKeys = new ArrayList<String>();
          alKeys.add(sKeyList);
        }

        for (int i = 0; i < alKeys.size(); i++) {
          sKey = alKeys.get(i);

          if (sKey == null)
            continue;
          sKey = sKey.trim();
          if (sKey.equals("")) {
            continue;
          }

          switch (this._nOp) {
          case nOP_GET_USER_INFO:
            mdInfo = ap.getUserInfo(sKey, getKeyName(sKeyName));
            break;
          case nOP_GET_ORG_UNIT_INFO:
            mdInfo = ap.getOrganicalUnitInfo(sKey);
            break;
          case nOP_GET_ORG_UNIT_PARENT:
            mdInfo = ap.getOrganicalUnitParent(sKey);
            break;
          default:
            Logger.error(login, this, "after", 
                procData.getSignature() + "operation " + this._nOp + " not support.");
            outPort = portError;
            break;
          }

          if (!outPort.equals(portError)) {
            if (mdInfo == null) {
              Logger.error(login, this, "after",
                  "null or empty ldap info for operation " + this._nOp
                  + " and key: " + sKey);
              outPort = portError;
            }

            alInfos.add(mdInfo);
          }
        }

        if (!outPort.equals(portError)) {
          Iterator<String> iter = this.getAttributeMap().keySet().iterator();
          while (iter.hasNext()) {
            final String sAttrName = iter.next();

            if (sAttrName.equals(sKeyName)) {
              // already processed
              continue;
            }
            else if (sAttrName.equals(sKEY_ATTRIBUTE)) {
              // input field not processed
              continue;
            }
            else if (sAttrName.equals(sDESCRIPTION)) {
              continue;
            }
            else if (sAttrName.equals(sRESULT)) {
              continue;
            }   

            String sAttrValue = this.getAttribute(sAttrName);
            if (sAttrValue != null) sAttrValue = sAttrValue.trim();

            if (StringUtils.isEmpty(sAttrValue)) {
              Logger.debug(login,this,"after","empty attribute value for " + sAttrName);      
              continue;
            }

            // ALTERADO: p apanhar userid no caso da chave ser o numemp
//          if(sKeyName.equals("INnumeroempregado"))  
//          if (stmp.equals("IDuserId"))
//          stmp = "loginpublico";          

            sKey = getKeyName(sAttrName);

            final StringBuilder sbMultiValue = new StringBuilder();
            for (int i=0; i < alInfos.size(); i++) {
              mdInfo = alInfos.get(i);
              if (mdInfo == null) continue;

              String sKeyValue = mdInfo.get(sKey);
              if (sKeyValue == null || sKeyValue.equalsIgnoreCase("null")) sKeyValue = "";

              if (i > 0) {
                sbMultiValue.append(",");
              }
              sbMultiValue.append(sKeyValue);
            }

            final String sMultiValue = sbMultiValue.toString();
            Logger.debug(login,this,"after","setting " + sAttrValue + "(" + sAttrName + ")=" + sMultiValue);      
            procData.parseAndSet(sAttrValue,sMultiValue);
            logMsg.append("Set '" + sAttrValue + "' as '" + sMultiValue + "';");
          }
        }
      }
    } catch (Exception e) {
      Logger.error(login, this, "after", "caught exception: " + e.getMessage());
      outPort = portError;
    }

    logMsg.append("Using '" + outPort.getName() + "';");
    Logger.logFlowState(userInfo, procData, this, logMsg.toString());
    return outPort;
  }

  protected String getKeyAttributeName(UserInfoInterface userInfo) {
    String retObj = null;

    String stmp = null;
    Iterator<String> iter = null;
    iter = this.getAttributeMap().keySet().iterator();
    while (iter != null && iter.hasNext()) {
      stmp = (String) iter.next();
      if (stmp == null || stmp.equals("") || stmp.length() < 1)
        continue;

      if (stmp.equals(sKEY_ATTRIBUTE)) {
        stmp = this.getAttribute(stmp);
        retObj = stmp;
        break;
      }

      if (stmp.charAt(0) == cINPUT) {
        if (stmp.charAt(1) == cDEFAULT) {
          // make default input element a key
          retObj = stmp;
        }
        else if (retObj == null) {
          // keep the first input element as key if key not set
          retObj = stmp;
        }
      }
      else {
        continue;
      }
    }

    if (retObj == null) {
      // old attributes
      if (_sDefaultKey != null) {
        retObj = _sDefaultKey;
      }
      else if (_sDefaultKey2 != null) {
        retObj = _sDefaultKey2;
      }else if (_sDefaultKey3 != null) {
        retObj = _sDefaultKey3;
      }
    }
    return retObj;
  }

  private static String getKeyName(String asKey) {
    String retObj = asKey;

    try {
      if (asKey.charAt(0) == cINPUT) {
        retObj = asKey.substring(2);
      }
      else if (asKey.charAt(0) == cOUTPUT) {
        retObj = asKey.substring(1);
      }
    }
    catch (Exception e) {
    }

    return retObj;
  }

  public String getDescription(UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, true, "Get LDAP Info");
  }

  public String getResult (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, false, "Got LDAP Info");
  }

  public String getUrl (UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }

}
