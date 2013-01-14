package pt.iflow.blocks;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.presentation.DateUtility;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processtype.DateDataType;
import pt.iflow.api.processtype.ProcessDataType;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;

/**
 * <p>Title: BlockDate</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 * 
 * TODO IMPROVE TO ADD MORE OPERATIONS, MORE TYPES, IMPROVE CODE, ...
 * IMPROVE LOCALIZED MESSAGES! 
 * BASICALLY, IMPROVE!
 */

public class BlockDateOperation extends Block {
  public Port portIn, portSuccess, portError;

  protected static final String INPUT_VAR = "inputVar";
  protected static final String OUTPUT_VAR = "outputVar";
  protected static final String OPERATION = "operation";
  protected static final String TYPE = "type";
  protected static final String VALUE = "value";

  public BlockDateOperation(int anFlowId,int id, int subflowblockid, String filename) {
    super(anFlowId,id, subflowblockid, filename);
    isCodeGenerator = true;
    hasInteraction = false;
  }

  public Port[] getInPorts (UserInfoInterface userInfo) {
      Port[] retObj = new Port[]{portIn};
      return retObj;
  }
  
  public Port getEventPort() {
      return null;
  }
  
  public Port[] getOutPorts (UserInfoInterface userInfo) {
    Port[] retObj = new Port[]{portSuccess, portError};
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

  protected static void performDateOperation(UserInfoInterface userInfo, HashMap<String,String> attrMap, ProcessData procData) 
  throws Exception {
  
    String inputVar = (String)attrMap.get(INPUT_VAR);

    Locale loc = BeanFactory.getSettingsBean().getOrganizationLocale(userInfo);
    Calendar cal = Calendar.getInstance(loc);

    Date data = null;
    
    boolean varIsDate = true;
    
    Object varValue = procData.get(inputVar).getValue();
    if (varValue instanceof Date) {
      data = (Date)varValue;
    }
    else {
      varIsDate = false;
      if (varValue instanceof String) {
        data = DateUtility.parseFormDate(userInfo, (String)varValue);
      }
      else if (varValue instanceof Long) {
        data = new Date((Long)varValue);
      }
    }
    
    
    if (data == null) {
      throw new Exception("Unable to parse data from variable " + inputVar); 
    }
    
    cal.setTime(data);

    String typeValue = (String)attrMap.get(TYPE);
    
    int type = -1;
    if (StringUtils.equalsIgnoreCase(typeValue, "days")) {
      type = Calendar.DAY_OF_MONTH;
    }
    else if (StringUtils.equalsIgnoreCase(typeValue, "months")) {
      type = Calendar.MONTH;
    }
    else if (StringUtils.equalsIgnoreCase(typeValue, "years")) {
      type = Calendar.YEAR; 
    }
    else {
      throw new Exception("No type defined or not supported");
    }

    String valueStr = (String)attrMap.get(VALUE);    
    int value = Integer.parseInt(procData.transform(userInfo, valueStr));
    
    String operation = (String)attrMap.get(OPERATION);
    if (StringUtils.equalsIgnoreCase(operation, "add")) {
      cal.add(type, value);
    }
    else if (StringUtils.equalsIgnoreCase(operation, "remove")) {
      cal.add(type, -value);      
    }
    else {
      throw new Exception("Operation not defined or not supported");
    }
    
    data = cal.getTime();
    
    String outputVar = (String)attrMap.get(OUTPUT_VAR);
    
    if (StringUtils.isEmpty(outputVar)) {
      // assume output var to be the same as input var
      outputVar = inputVar;
    }
    else {
      // check output var type
      ProcessDataType pdt = procData.getVariableDataType(outputVar);
      if (pdt instanceof DateDataType) {
        varIsDate = true;
      }
      else {
        varIsDate = false;        
      }
    }
    
    if (varIsDate) {
      procData.get(outputVar).setValue(data);
    }
    else {
      procData.parseAndSet(outputVar, DateUtility.formatFormDate(userInfo, data));
    }
    
  }
  
  
  /**
   * Executes the block main action
   *
   * @param dataSet a value of type 'DataSet'
   * @return the port to go to the next block
   */
  public Port after(UserInfoInterface userInfo, ProcessData procData) {
    String login = userInfo.getUtilizador();
    Logger.debug(login,this,"after", "Searching for week of month...");
    
    try  {
      HashMap<String,String> attrs = getAttributeMap();
      performDateOperation(userInfo, attrs, procData);
    }
    catch (Exception e) {
      Logger.error(login, this, "after", 
          procData.getSignature() + "Error performing date operation: " + e.getMessage(), e);
      return portError;
    }
    
    return portSuccess;
  }

  public String getDescription (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, true, "Datas");
  }

  public String getResult (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, false, "Datas efectuadas");
  }

  public String getUrl (UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }
}
