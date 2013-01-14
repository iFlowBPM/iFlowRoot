package pt.iflow.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.notification.SMSExpressGatewayImpl;
import pt.iflow.api.notification.SMSGateway;
import pt.iflow.api.notification.SimplewireSMSImpl;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessListItem;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.Setup;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;


/**
 * <p>Title: BlockSMS</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: iKnow</p>
 * @author Jose Costa
 */

public class BlockSMS extends Block {
  public Port portIn, portSuccess, portError;
  
  private static final String SEP = ",";
  
  public BlockSMS(int anFlowId,int id, int subflowblockid, String filename) {
    super(anFlowId,id, subflowblockid, filename);
    hasInteraction = false;
  }
  
  public Port[] getInPorts (UserInfoInterface userInfo) {
    Port[] retObj = new Port[1];
    retObj[0] = portIn;
    return retObj;
  }
  
  public Port getEventPort() {
    return null;
  }
  
  public Port[] getOutPorts (UserInfoInterface userInfo) {
    Port[] retObj = new Port[2];
    retObj[0] = portSuccess;
    retObj[1] = portError;
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

    Port outPort = portError;
    StringBuffer logMsg = new StringBuffer();
    
    String login = userInfo.getUtilizador();
    
    try {

      String phone = getAttribute("phone");
      String subject = procData.transform(userInfo, getAttribute("subject"));
      String message = procData.transform(userInfo, getAttribute("message"));
      
      List<String> alPhones = new ArrayList<String>();
      
      if (procData.isListVar(phone)) {
    	  for (ProcessListItem item : procData.getList(phone).getItems()) {
    		  alPhones.add(item.format());
    	  }
      }
      else {
        String stmp = procData.transform(userInfo, phone);
        if (stmp != null) phone = stmp;
        
        if (phone.indexOf(SEP) > -1) {
          alPhones = Utils.tokenize(phone, SEP);
        }
        else {
          alPhones.add(phone);
        }
      }
      
      if (alPhones == null || alPhones.size() == 0) {
        Logger.info(login, this, "after", "No phones to send SMS to (phone var=" + phone + ")");
        outPort = portError;
      } else {
        SMSGateway smsGatewayManager = initialiseSMSGateway(userInfo);

        String buff = "";
        for (int i=0; alPhones != null && i < alPhones.size(); i++) {
          String destination = (String)alPhones.get(i);
          smsGatewayManager.getSmsData().addPhone(destination);
          if(StringUtils.isBlank(buff)) {
            buff += "SMS sent to: " + destination;
          } else {
            buff += ", " + destination;
          }
        }
        if(StringUtils.isNotBlank(buff)) {
          logMsg.append(buff + ";");
        }
         
        String msg = subject + ":\n" + message;
        smsGatewayManager.getSmsData().setMessage(msg);
        
        if(smsGatewayManager.send(login)){
          outPort = portSuccess;
        }
      }
    } catch (Exception e) {
      Logger.error(login,this,"after","caught exception: " + e.getMessage());
      outPort = portError;
    }

    logMsg.append("Using '" + outPort.getName() + "';");
    Logger.logFlowState(userInfo, procData, this, logMsg.toString());
    return outPort;
  }

  private SMSGateway initialiseSMSGateway(UserInfoInterface userInfo) {
    Properties iflowProperties = Setup.getProperties();
    SMSGateway smsGateway = null;

    String useSmsExpress = (String) iflowProperties.get("SMS_EXPRESS_USE");

    if (useSmsExpress != null && "true".equals(useSmsExpress)){
      smsGateway = new SMSExpressGatewayImpl();
    } else {
      smsGateway = new SimplewireSMSImpl();
    }

    smsGateway.init(Setup.getProperties());
    return smsGateway;
  }

  public String getDescription (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, true, "SMS");
  }
  
  public String getResult (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, false, "SMS Enviado");
  }
  
  public String getUrl (UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }
}
