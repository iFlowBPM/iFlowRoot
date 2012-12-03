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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.core.Repository;
import pt.iflow.api.core.RepositoryFile;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessListItem;
import pt.iflow.api.processdata.ProcessListVariable;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iknow.utils.wsdl.WSDLUtils;

public class BlockWebServiceSinc extends Block {

  public Port portIn, portOk, portError, portTimeout;

  // next block is also defined in iFlowEditor block
  private static final String _sPROPS_PREFIX = "P";
  private static final String _sINPUT_PREFIX = "I";
  private static final String _sOUTPUT_PREFIX = "O";

  // next block is also defined in iFlowEditor block
  private static final String _sWSDL = "WSDL";
  private static final String _sSERVICE = "SERVICE";
  private static final String _sPORT = "PORT";
  private static final String _sOPERATION = "OPERATION";
  private static final String _sTIMEOUT = "TIMEOUT";
  private static final String _sRETRIES = "RETRIES";
  
  private static final String _sAUTHENT = "AUTHENTICATION";
  private static final String _sUSERLOGIN = "USERLOGIN";
  private static final String _sUSERAUTH = "USERAUTH";
  private static final String _sPASSAUTH = "PASSAUTH";
  // next block is also defined in iFlowEditor/UniFlowEditor block
  // private static final String _ESCOLHA = "Escolha";
	  																			//AUTHENTICATION
  // next block is also defined in iFlowEditor/UniFlowEditor block
  private static final String _URL_REP_FILE = ".url";
  private static final String _WSDL_REP_FILE = ".wsdl";

  private String _sWsdl = null;
  private String _sService = null;
  private String _sPort = null;
  private String _sOperation = null;
  private String _sTimeout = null;
  private String _sRetries = null;
  private String _sAuthent = null;
  private String _sUserlogin = null;
  private String _sUserauth = null;
  private String _sPassauth = null;
  
  // key: block full id (flowid/blockid) | value:WSDLUtils for this block
  private static HashMap<String, WSDLUtils> _hmWsdls = null;
  // key: block full id (flowid/blockid) | value: HashMap with input mapping (key: soap field; value=iflow var)
  private static HashMap<String, HashMap<String, String>> _hmInMapping = null;
  // key: block full id (flowid/blockid) | value: HashMap with output mapping (key: soap field; value=iflow var)
  private static HashMap<String, HashMap<String, String>> _hmOutMapping = null;

  static {
    _hmWsdls = new HashMap<String, WSDLUtils>();
    _hmInMapping = new HashMap<String, HashMap<String, String>>();
    _hmOutMapping = new HashMap<String, HashMap<String, String>>();
  }

  public BlockWebServiceSinc(int anFlowId, int id, int subflowblockid, String filename) {
    super(anFlowId, id, subflowblockid, filename);
  }

  public Port[] getInPorts(UserInfoInterface userInfo) {
    Port[] retObj = new Port[1];
    retObj[0] = portIn;
    return retObj;
  }

  public Port getEventPort() {
    return null;
  }

  public Port[] getOutPorts(UserInfoInterface userInfo) {
    Port[] retObj = new Port[3];
    retObj[0] = portOk;
    retObj[1] = portError;
    retObj[2] = portTimeout;
    return retObj;
  }

  public String before(UserInfoInterface userInfo, ProcessData procData) {

    this.init(userInfo);

    return "";
  }

  public boolean canProceed(UserInfoInterface userInfo, ProcessData procData) {
    return true;
  }

  public Port after(UserInfoInterface userInfo, ProcessData procData) {
    Port retObj = portOk;
    StringBuffer logMsg = new StringBuffer();
    try {
      WSDLUtils wu = this.getWSDLUtils();

      int nTO = -1;
      try {
        nTO = Integer.parseInt(this._sTimeout);
      } catch (Exception e) {
      }

      int nRetryCounter = 1;
      int nRetries = 1;
      try {
        nRetries = Integer.parseInt(this._sRetries);
      } catch (Exception e) {
      }

      // INPUTS
      HashMap<String, Object> hmInputs = new HashMap<String, Object>();
      HashMap<String, String> hmInMapping = this.getMapping(true);

      Iterator<String> iter = hmInMapping.keySet().iterator();
      while (iter.hasNext()) {
        String sKey = (String) iter.next();
        String sVal = (String) hmInMapping.get(sKey);
        String stmp = null;
        Object result = sVal;
        if (sVal != null && !sVal.equals("")) {
          if (procData.isListVar(sVal)) {
            ArrayList<String> values = new ArrayList<String>();
            ProcessListVariable lv = procData.getList(sVal);
            for (ProcessListItem item : lv.getItems()) {
              values.add(item.format());
            }
            result = values;
          } else {
            try {
              stmp = procData.transform(userInfo, sVal);
            } catch (Exception e) {
            }
          }
        }
        if (stmp != null) {
          result = stmp;
        }
        hmInputs.put(sKey, result);
      }
      HashMap<?, ?> hmOutput = null;
      Logger.info(userInfo.getUserId(), this, "after","CALLING: service: " + this._sService);
      Logger.debug(userInfo.getUserId(), this, "after","CALLING: service: " + this._sService
          + "; port: " + this._sPort
          + "; operation: " + this._sOperation
          + "; timeout: " + this._sTimeout
          + "; retries: " + this._sRetries
          + "; INPUTS: " + hmInputs);

      while (hmOutput == null && nRetryCounter <= nRetries) {
        Logger.debug(userInfo.getUserId(), this, "after","Calling service (attempt #" + nRetryCounter + " of " + nRetries + " )");

        
//AUTHENTICATION         
//        if (this._sAuthent.equals("true")){
//	        String user= "";
//	        String password = "";
//	        	if(this._sUserlogin.equals("true")){ 
//	        		user = userInfo.getUtilizador();
//	        		password = new String(userInfo.getPassword());
//	        	} else {
//	        		user = this._sUserauth;
//	        		password = this._sPassauth;
//	        	}
//	        hmOutput = wu.callService(this._sService, this._sPort, this._sOperation, nTO, hmInputs, user, password);
//        }else{      
//        	hmOutput = wu.callService(this._sService, this._sPort, this._sOperation, nTO, hmInputs, null, null);
//        } 
        hmOutput = wu.callService(this._sService, this._sPort, this._sOperation, nTO, hmInputs);
        
      nRetryCounter++;
      }

      if (hmOutput == null && nRetryCounter >= nRetries) {
        Logger.info(userInfo.getUserId(), this, "after","SERVICE timedout");
        retObj = portTimeout;
      } else {

        Logger.info(userInfo.getUserId(), this, "after","SERVICE CALLED OK");

        Logger.debug(userInfo.getUserId(), this, "after","Service output: " + hmOutput);

        // process outputs
        HashMap<String, String> hmOutMapping = this.getMapping(false);

        // first cleanup previous values
        iter = hmOutMapping.values().iterator();

        while (iter.hasNext()) {
          String sVar = (String) iter.next();

          if (sVar == null || sVar.trim().equals("")) continue;

          if (procData.isListVar(sVar)) {
            Logger.debug(userInfo.getUserId(), this, "after","Cleaning list for var " + sVar);
            procData.clearList(sVar);
          }
          else {
            Logger.debug(userInfo.getUserId(), this, "after","Cleaning  var " + sVar);
            procData.clear(sVar);
          }
        }

        Iterator<?> outputIter = hmOutput.keySet().iterator();
        while (outputIter.hasNext()) {
          String sField = (String) outputIter.next();
          Object o = hmOutput.get(sField);
          String sVar = (String) hmOutMapping.get(sField);

          if (sVar == null || (sVar = sVar.trim()).equals("")) {
            continue;
          }

          Logger.debug(userInfo.getUserId(), this, "after", "FIELD=" + sField + " => VAR=" + sVar + " => VALUE=" + o);

          String sValue = "";
          if (o != null) {
            if (o instanceof ArrayList) {
              ArrayList<?> alValues = (ArrayList<?>) o;
              ProcessListVariable lv = procData.getList(sVar);
              for (int i = 0; i < alValues.size(); i++) {
                o = alValues.get(i);
                if (o == null) sValue = "";
                else sValue = o.toString();

                lv.parseAndSetItemValue(i, sValue);
              }
              procData.setList(lv);
            } else {
              if (o instanceof byte[]) {
                sValue = new String((byte[]) o); // This should no be supported!!
              } else {
                sValue = o.toString();
              }
              procData.parseAndSet(sVar, sValue);
            }
          } else {
            procData.parseAndSet(sVar, sValue);
          }
        }
      }
    } catch (Exception e) {
      Logger.error(userInfo.getUserId(), this, "after", "cauth exception: " + e.getMessage());
      retObj = portError;
    }

    logMsg.append("Using '" + retObj.getName() + "';");
    Logger.logFlowState(userInfo, procData, this, logMsg.toString());
    return retObj;
  }

  private WSDLUtils getWSDLUtils() {
    return _hmWsdls.get(this.getFullId());
  }

  private HashMap<String, String> getMapping(boolean abInput) {
    HashMap<String, String> retObj = null;
    if (abInput) {
      retObj = _hmInMapping.get(this.getFullId());
    } else {
      retObj = _hmOutMapping.get(this.getFullId());
    }
    return retObj;
  }

  private String getFullId() {
    return this.getFlowId() + "/" + this.getId();
  }

  /**
   * clean/remake block's cache
   */
  public void refreshCache(UserInfoInterface userInfo) {
    String sId = this.getFullId();

    if (_hmWsdls != null && _hmWsdls.containsKey(sId)) {
      _hmWsdls.remove(sId);
    }
    this.init(userInfo);
  }

  protected void init(UserInfoInterface userInfo) {
    String sId = this.getFullId();

    if (_hmWsdls == null || !_hmWsdls.containsKey(sId)) {
      generateWSDLInfo(userInfo);
    }
  }

  private void generateWSDLInfo(UserInfoInterface userInfo) {

    this._sWsdl = this.getAttribute(_sPROPS_PREFIX + _sWSDL);
    this._sService = this.getAttribute(_sPROPS_PREFIX + _sSERVICE);
    this._sPort = this.getAttribute(_sPROPS_PREFIX + _sPORT);
    this._sOperation = this.getAttribute(_sPROPS_PREFIX + _sOPERATION);
    this._sTimeout = this.getAttribute(_sPROPS_PREFIX + _sTIMEOUT);
    this._sRetries = this.getAttribute(_sPROPS_PREFIX + _sRETRIES);
    
    this._sAuthent = this.getAttribute(_sPROPS_PREFIX + _sAUTHENT);
    this._sUserlogin = this.getAttribute(_sPROPS_PREFIX + _sUSERLOGIN);
    this._sUserauth = this.getAttribute(_sPROPS_PREFIX + _sUSERAUTH);
    this._sPassauth = this.getAttribute(_sPROPS_PREFIX + _sPASSAUTH); //AUTHENTICATION
    
    Logger.debug("", this, "", "WSDL=" + this._sWsdl);
    Logger.debug("", this, "", "SERV=" + this._sService);
    Logger.debug("", this, "", "PORT=" + this._sPort);
    Logger.debug("", this, "", "OPER=" + this._sOperation);
    Logger.debug("", this, "", "TIMEO=" + this._sTimeout);
    Logger.debug("", this, "", "RETR=" + this._sRetries);
    Logger.debug("", this, "", "AUTH=" + this._sAuthent);
    Logger.debug("", this, "", "LOGN=" + this._sUserlogin);
    Logger.debug("", this, "", "USER=" + this._sUserauth);
    Logger.debug("", this, "", "PASS=" + this._sPassauth);

    Repository rep = this.getRepBean();

    InputStream in = null;
    try {
      RepositoryFile rfWSDLFile = rep.getWebService(userInfo, this._sWsdl);

      in = rfWSDLFile.getResourceAsStream();
      if(in == null) throw new Exception("Could not open WSDL definition file.");

      String sUrl = this.getWSDLURL(userInfo, this._sWsdl);
      Logger.debug("", this, "", "URL=" +  sUrl);

      WSDLUtils wu = setWSDLUtils(in, sUrl);

      // input/output mappings
      HashMap<String, String> hmInput = new HashMap<String, String>();
      HashMap<String, String> hmOutput = new HashMap<String, String>();

      Iterator<String> iter = this.getAttributeMap().keySet().iterator();
      while (iter.hasNext()) {
        String sAtr = iter.next();
        String sVal = this.getAttribute(sAtr);

        if (sAtr.startsWith(_sINPUT_PREFIX)) {
          hmInput.put(sAtr.substring(1), sVal);
        } else if (sAtr.startsWith(_sOUTPUT_PREFIX)) {
          hmOutput.put(sAtr.substring(1), sVal);
        } else {
          continue;
        }
      }

      storeWSDL(this, wu, hmInput, hmOutput);

    } catch (Exception e) {
      Logger.error(null, this, "generateWSDL", "caught exception: ", e);
    } finally {
      if (null != in) {
        try {
          in.close();
        } catch (IOException e) {
        }
      }
    }

  }

  // synchronized to avoid multiple/concurrent cache insert
  private static synchronized void storeWSDL(BlockWebServiceSinc abBlock,
      WSDLUtils awu, HashMap<String, String> ahmInMapping,
      HashMap<String, String> ahmOutMapping) {

    _hmWsdls.put(abBlock.getFullId(), awu);
    _hmInMapping.put(abBlock.getFullId(), ahmInMapping);
    _hmOutMapping.put(abBlock.getFullId(), ahmOutMapping);
  }

  private String getWSDLURL(UserInfoInterface userInfo, String asWSDL) {
    String retObj = null;

    try {
      Repository rep = this.getRepBean();

      String url = asWSDL.substring(0, asWSDL.lastIndexOf("."));
      url = url + _URL_REP_FILE;

      retObj = new String(rep.getWebService(userInfo, url).getResouceData(), "UTF-8");
    } catch (Exception e) {
      Logger.warning(userInfo.getUtilizador(), this, "getWSDLURL", "Error retrieving webservice URL", e);
    }

    if (retObj == null) retObj = "";

    return retObj;
  }

  public String getDescription (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, true, "Invocação de WebServiceSinc ( " + _sWsdl + ")");
  }

  public String getResult(UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, false, "Invocação de WebServiceSinc ( " + _sWsdl + ")  efectuada");
  }

  public String getUrl(UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }

}
