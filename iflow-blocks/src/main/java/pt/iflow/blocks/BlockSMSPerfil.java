package pt.iflow.blocks;

import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.core.AuthProfile;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.notification.SMSExpressGatewayImpl;
import pt.iflow.api.notification.SMSGateway;
import pt.iflow.api.notification.SimplewireSMSImpl;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.Setup;
import pt.iflow.api.utils.UserInfoInterface;

/**
 * <p>Title: BlockSMSPerfil</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: iKnow</p>
 * @author Pedro Monteiro
 */

public class BlockSMSPerfil extends Block {
  public Port portIn, portSuccess, portError;

  static String[] toTypeModes = {"Perfil","PerfilTexto"};
  public final static String sSMS_TO_TYPE = "Type of To";
  public final static String sSMS_TO_PERFILTEXTO = "To PerfilTexto";
  public final static String sSMS_TO_PERFIL = "To Perfil";
  public final static String sSMS_MESSAGE = "Message";

  public BlockSMSPerfil(int anFlowId,int id, int subflowblockid, String filename) {
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
    String stmp = null;
    try {
      // TYPE
      stmp = this.getAttribute(sSMS_TO_TYPE);
      if (StringUtils.isEmpty(stmp)) {
        Logger.debug(userInfo.getUtilizador(), this, "after", "Attribute ToType not defined");
        outPort = portError;
      } else {
        String toType = procData.transform(userInfo, stmp);
        if (StringUtils.isEmpty(toType)) {
          toType = stmp;
        }

        // MESSAGE
        stmp = this.getAttribute(sSMS_MESSAGE);
        if (StringUtils.isEmpty(stmp)) {
          Logger.debug(userInfo.getUtilizador(), this, "after", "Attribute Message not defined");
          outPort = portError;
        } else {
          String message = procData.transform(userInfo, stmp);
          if (StringUtils.isEmpty(message)) {
            message = stmp;
          }

          String perfil = null;
          if (toType.equals(toTypeModes[0])) {
            // PERFIL
            stmp = this.getAttribute(sSMS_TO_PERFIL);
          } else {
            // PERFILTEXTO
            stmp = this.getAttribute(sSMS_TO_PERFILTEXTO);
          }
          if (StringUtils.isEmpty(stmp)) {
            Logger.debug(userInfo.getUtilizador(), this, "after",
            "Attribute Perfil not defined");
            outPort = portError;
          } else {
            perfil = procData.transform(userInfo, stmp);
            if (StringUtils.isEmpty(perfil)) {
              perfil = stmp;
            }

            AuthProfile ap = BeanFactory.getAuthProfileBean();

            Iterator<String> liUsers = ap.getUsersInProfile(userInfo, perfil).iterator();
            if (liUsers != null && liUsers.hasNext ()) {
              SMSGateway smsGatewayManager = initialiseSMSGateway(userInfo);

              String sUser = null;
              String sPhone = null;
              smsGatewayManager.getSmsData().setMessage (message);

              String buff = "";
              while (liUsers.hasNext()) {
                sUser = liUsers.next();
                sPhone = ap.getUserInfo(sUser).getMobileNumber();
                try {
                  if (StringUtils.isNotEmpty(sPhone)) {
                    smsGatewayManager.getSmsData().addPhone(sPhone);
                    if(StringUtils.isBlank(buff)) {
                      buff += "SMS sent to: " + sPhone;
                    } else {
                      buff += ", " + sPhone;
                    }
                  } else throw new Exception();

                } catch(NumberFormatException e) {
                  Logger.trace(this,"after",userInfo.getUtilizador() + "Field telefonemovel=["+sPhone+"] empty or not a number for user: " + sUser);
                }
              }
              if(StringUtils.isNotBlank(buff)) {
                logMsg.append(buff + ";");
              }

              if (smsGatewayManager.send(userInfo.getUtilizador())) {
                outPort = portSuccess;
              }
            }
          }
        }
      }
    } catch (Exception e) {
      Logger.error(userInfo.getUtilizador(),this,"after","caught exception: " + e.getMessage());
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
    return this.getDesc(userInfo, procData, true, "SMS Perfil");
  }

  public String getResult (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, false, "SMS Perfil Enviado");
  }

  public String getUrl (UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }
}
