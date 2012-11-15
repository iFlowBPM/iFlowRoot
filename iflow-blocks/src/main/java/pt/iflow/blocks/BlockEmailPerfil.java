package pt.iflow.blocks;

import java.util.Hashtable;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.core.AuthProfile;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.notification.Email;
import pt.iflow.api.notification.EmailManager;
import pt.iflow.api.notification.EmailTemplate;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.userdata.UserData;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;


/**
 * <p>Title: BlockEmailPerfil</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: iKnow</p>
 * @author Pedro Monteiro
 */

public class BlockEmailPerfil extends Block {
  public Port portIn, portSuccess, portError;

  static String[] toTypeModes = {"Perfil","PerfilTexto"};


  // check constants with editor altera atributos classes
  private static final String _sSELECT = "Escolha";
  public final static String sEMAIL_FROM = "From";
  public final static String sEMAIL_TO_TYPE = "Type of To";
  public final static String sEMAIL_TO_PERFILTEXTO = "To PerfilTexto";
  public final static String sEMAIL_TO_PERFIL = "To Perfil";
  public final static String sEMAIL_SUBJECT = "Subject";
  public final static String sEMAIL_MESSAGE = "Message";
  public final static String sEMAIL_TEMPLATE = "Template";
   
  public BlockEmailPerfil(int anFlowId,int id, int subflowblockid, String filename) {
    super(anFlowId,id, subflowblockid, filename);
    hasInteraction = false;
  }
  
  public Port[] getOutPorts (UserInfoInterface userInfo) {
    Port[] retObj = new Port[2];
    retObj[0] = portSuccess;
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
    Port outPort = portSuccess;
    StringBuffer logMsg = new StringBuffer();
    
    if(!Const.bUSE_EMAIL) {
      outPort = portError;
    } else {
      try {
        String from = procData.transform(userInfo, getAttribute(sEMAIL_FROM));
        String toType = getAttribute(sEMAIL_TO_TYPE);
        String subject = procData.transform(userInfo, getAttribute(sEMAIL_SUBJECT));
        String message = procData.transform(userInfo, getAttribute(sEMAIL_MESSAGE));
        String template = getAttribute(sEMAIL_TEMPLATE);

        String perfil = null;
        if (toType.equalsIgnoreCase(toTypeModes[0])) {
          // PERFIL
          perfil =  getAttribute(sEMAIL_TO_PERFIL);
        } else {
          // PERFILTEXTO
          perfil =  procData.transform(userInfo, getAttribute(sEMAIL_TO_PERFILTEXTO));
        }

        // validations
        if (StringUtils.isEmpty(perfil)) {
          Logger.error(userInfo.getUtilizador(),this,"after",
              procData.getSignature() + "No perfil defined");
          outPort = portError;
        } else {
          if (StringUtils.isEmpty(template) || template.equals(_sSELECT)) {
            // if no template defined, subject, message and from have to be defined
            // otherwise leave it probably for the template

            if (StringUtils.isEmpty(from)) {
              Logger.error(userInfo.getUtilizador(),this,"after",
                  procData.getSignature() + "No from defined");
              outPort = portError;
            } else if (StringUtils.isEmpty(subject)) {
              Logger.error(userInfo.getUtilizador(),this,"after",
                  procData.getSignature() + "No subject defined");
              outPort = portError;
            } else if (StringUtils.isEmpty(message)) {
              Logger.error(userInfo.getUtilizador(),this,"after",
                  procData.getSignature() + "No message defined");
              outPort = portError;
            }
          }

          if(!outPort.equals(portError)) {
            AuthProfile ap = BeanFactory.getAuthProfileBean();

            Iterator<String> iUsers = (ap.getUsersInProfile(userInfo, perfil)).iterator();
            if (iUsers != null && iUsers.hasNext ()) {
              String sMail = null;
              String sUser = null;

              Email email = null;

              if (!StringUtils.isEmpty(template) && !template.equals(_sSELECT)) {
                EmailTemplate etemp = EmailManager.getEmailTemplate(userInfo, template);
                if (etemp != null) {
                  Hashtable<String,String> htProps = new Hashtable<String,String>();

                  // default variables
                  if (from != null) htProps.put("from", from);
                  if (subject != null) htProps.put("subject", subject);
                  if (message != null) htProps.put("message", message);
                  htProps.put("app_host", Const.APP_HOST);
                  htProps.put("app_port", String.valueOf(Const.APP_PORT));

                  // process variables
                  // XXX list variables needed???
                  for (String sName : procData.getSimpleVariableNames()) {
                    if (htProps.containsKey(sName)) continue;
                    String sValue = procData.getFormatted(sName);
                    if (sValue == null) {
                      sValue = "";
                    }
                    htProps.put(sName, sValue);
                  }

                  email = EmailManager.buildEmail(htProps, etemp);
                }
              }

              if (email == null) {
                email = new Email();
                email.setFrom(from);
                email.setSubject(subject);
                email.setMsgText(message);
                email.setCallingProcess(procData.getProcessHeader());
              }

              while (iUsers.hasNext()) {
                sUser = (String)iUsers.next();
                UserData ud = ap.getUserInfo(sUser);
                sMail = ud.getEmail();
                if (StringUtils.isEmpty(sMail)) {
                  logMsg.append("Empty email for user " + sUser + ";");
                  Logger.warning(userInfo.getUtilizador(), this, "after", 
                      procData.getSignature() + "Empty email for user " + sUser);
                  continue;
                }
                
                email.setTo(sMail);
                
                if (email.sendMsg()) {
                  outPort = portSuccess;
                  logMsg.append("Mail sent To: " + sMail + ";");
                } else {
                  outPort = portError;
                  Logger.warning(userInfo.getUtilizador(), this, "after", "Mail not sent to user: " + sUser);
                }
                
                email.resetTo();
              }
            }
          }
        }
      } catch (Exception e) {
        Logger.error(userInfo.getUtilizador(),this,"after",
            procData.getSignature() + "caught exception: " + e.getMessage(), e);
        outPort = portError;
      }
    }
    
    logMsg.append("Using '" + outPort.getName() + "';");
    Logger.logFlowState(userInfo, procData, this, logMsg.toString());
    return outPort;
  }
  

  public String getDescription (UserInfoInterface userInfo,  ProcessData procData) {
    return this.getDesc(userInfo, procData, true, "Email Perfil");
  }

  public String getResult (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, false, "Email Perfil Enviado");
  }

  public String getUrl (UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }
}
