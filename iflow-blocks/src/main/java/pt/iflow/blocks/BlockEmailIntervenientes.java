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
import java.util.Hashtable;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.core.AuthProfile;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.notification.Email;
import pt.iflow.api.notification.EmailManager;
import pt.iflow.api.notification.EmailTemplate;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;

/**
 * <p>Title: BlockEmailIntervenientes</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: iKnow</p>
 * @author iKnow
 */

public class BlockEmailIntervenientes extends Block {
  public Port portIn, portSuccess, portError;

  // check constants with editor altera atributos classes
  private static final String _sSELECT = "Escolha";
  public final static String sEMAIL_FROM = "from";
  public final static String sEMAIL_TO = "to";
  public final static String sEMAIL_SUBJECT = "subject";
  public final static String sEMAIL_MESSAGE = "message";
  public final static String sEMAIL_INTERV = "intervenientes";
  public final static String sEMAIL_TEMPLATE = "template";


  public BlockEmailIntervenientes(int anFlowId,int id, int subflowblockid, String filename) {
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
        String to = procData.transform(userInfo, getAttribute(sEMAIL_TO));
        String from = procData.transform(userInfo, getAttribute(sEMAIL_FROM));
        String subject = procData.transform(userInfo, getAttribute(sEMAIL_SUBJECT));
        String message = procData.transform(userInfo, getAttribute(sEMAIL_MESSAGE));
        String intervenientes = getAttribute(sEMAIL_INTERV);
        String template = getAttribute(sEMAIL_TEMPLATE);

        // validations
        if (to == null || to.equals("")) {
          Logger.error(userInfo.getUtilizador(),this,"after",
              procData.getSignature() + "No to defined");
          outPort = portError;
        } else {
          if (template == null || template.equals("") || template.equals(_sSELECT)) {
            // if no template defined, subject, message and from have to be defined
            // otherwise leave it probably for the template

            if (from == null || from.equals("")) {
              Logger.error(userInfo.getUtilizador(),this,"after",
                  procData.getSignature() + "No from defined");
              outPort = portError;
            } else if (subject == null || subject.equals("")) {
              Logger.error(userInfo.getUtilizador(),this,"after",
                  procData.getSignature() + "No subject defined");
              outPort = portError;
            } else if (message == null || message.equals("")) {
              Logger.error(userInfo.getUtilizador(),this,"after",
                  procData.getSignature() + "No message defined");
              outPort = portError;
            }
          }

          if(!outPort.equals(portError)) {
            boolean bCc = false;
            if (intervenientes != null && intervenientes.trim().equalsIgnoreCase("cc")) {
              bCc = true;
            }

            Email email = null;

            if (template != null && !template.equals("") && !template.equals(_sSELECT)) {
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
            }

            ArrayList<String> alInterv = new ArrayList<String>();
            ArrayList<String> alEmailsTo = new ArrayList<String>();
            ArrayList<String> alEmailsCc = new ArrayList<String>();

            alInterv.addAll(BeanFactory.getProcessManagerBean().getProcessIntervenients(userInfo, procData));
            
            // now that we have users, get users' emails
            AuthProfile ap = BeanFactory.getAuthProfileBean();

            alEmailsTo.add(to);

            String sMail = null;
            for (int i=0; i < alInterv.size(); i++) {
              sMail = ap.getUserInfo((String)alInterv.get(i)).getEmail();
              if (sMail == null || sMail.trim().equals("") || 
                  sMail.trim().equalsIgnoreCase("null")) {
                continue;
              }
              sMail = sMail.trim();
              if (sMail.equals("") || sMail.equalsIgnoreCase("null")) {
                continue;
              }
              if (sMail.equalsIgnoreCase(to)) {
                continue;
              }
              if (bCc) {
                alEmailsCc.add(sMail);
              }
              else {
                alEmailsTo.add(sMail);
              }
            }

            if (email != null) {
              email.setTo(alEmailsTo);
              email.setCc(alEmailsCc);
              email.setCallingProcess(procData.getProcessHeader());
              
              if (email.sendMsg()){
                outPort = portSuccess;
                logMsg.append("Mail sent To: " + alEmailsTo + ";");
                logMsg.append("     With CC: " + alEmailsCc + ";");
              } else {
                outPort = portError;
              }
            } else {
              Logger.error(userInfo.getUtilizador(),this,"after",
                  procData.getSignature() + "email is null");
              outPort = portError;
            }
          }
        }
      } catch (Exception e) {
        Logger.error(userInfo.getUtilizador(),this,"after",
            procData.getSignature() + "caught exception: " + e.getMessage());
        outPort = portError;
      }
    }

    logMsg.append("Using '" + outPort.getName() + "';");
    Logger.logFlowState(userInfo, procData, this, logMsg.toString());
    return outPort;
  }

  public String getDescription (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, true, "Email Intervenientes");
  }

  public String getResult (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, false, "Email Enviado aos Intervenientes");
  }

  public String getUrl (UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }
}
