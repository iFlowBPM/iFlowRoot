package pt.iflow.blocks;

import java.net.URL;

import NET.webserviceX.www.Currency;
import NET.webserviceX.www.CurrencyConvertorLocator;
import NET.webserviceX.www.CurrencyConvertorSoap;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iknow.utils.StringUtilities;

/**
 * </p>
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * <p>
 * Company: Infosistema
 * </p>
 * 
 * @author
 */

public class BlockCurrencyConvertor extends Block {
  public Port portIn, portSuccess, portEmpty, portError;

  private static final String URL = "URL";
  private static final String FROM = "FROM";
  private static final String TO = "TO";
  private static final String RATE = "RATE";

  public BlockCurrencyConvertor(int anFlowId, int id, int subflowblockid, String filename) {
    super(anFlowId, id, subflowblockid, filename);
    hasInteraction = false;
  }

  public Port getEventPort() {
    return null;
  }

  public Port[] getInPorts(UserInfoInterface userInfo) {
    Port[] retObj = new Port[1];
    retObj[0] = portIn;
    return retObj;
  }

  public Port[] getOutPorts(UserInfoInterface userInfo) {
    Port[] retObj = new Port[2];
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
	    Port outPort = portSuccess;
	    String login = userInfo.getUtilizador();
	    StringBuffer logMsg = new StringBuffer();

	    String sURLVar = getParsedAttribute(userInfo, URL, procData);
	    String sFROMVar = getParsedAttribute(userInfo, FROM, procData);
	    String sTOVar = getParsedAttribute(userInfo, TO, procData);
	    String sRATEVar = getAttribute(RATE);

	    if (StringUtilities.isEmpty(sURLVar) || StringUtilities.isEmpty(sFROMVar) || StringUtilities.isEmpty(sTOVar) || StringUtilities.isEmpty(sRATEVar)) {
	      Logger.error(login, this, "after", procData.getSignature() + "empty value for attributes");
	      outPort = portError;
	    } else
	      try {	    	  
	    	  CurrencyConvertorLocator locator = new CurrencyConvertorLocator();
	  		  CurrencyConvertorSoap ccs = locator.getCurrencyConvertorSoap(new URL(sURLVar));
	  		  double result = ccs.conversionRate(Currency.fromString(sFROMVar), Currency.fromString(sTOVar));
	  		  
	  		  procData.set(sRATEVar, result);
	      } catch (Exception e) {
	        Logger.error(login, this, "after", procData.getSignature() + "caught exception: " + e.getMessage(), e);
	        outPort = portError;
	      }

	    logMsg.append("Using '" + outPort.getName() + "';");
	    Logger.logFlowState(userInfo, procData, this, logMsg.toString());
	    return outPort;
	  }

  @Override
  public String getDescription(UserInfoInterface userInfo, ProcessData procData) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getResult(UserInfoInterface userInfo, ProcessData procData) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getUrl(UserInfoInterface userInfo, ProcessData procData) {
    // TODO Auto-generated method stub
    return null;
  }

}
