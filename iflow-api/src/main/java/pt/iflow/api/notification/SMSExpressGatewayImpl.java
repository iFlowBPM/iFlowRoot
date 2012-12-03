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
package pt.iflow.api.notification;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Properties;

import org.apache.axis.AxisFault;

import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.Setup;

import com.mobicomp.smsexpress.webservice.server.common.AuthenticationInfo;
import com.mobicomp.smsexpress.webservice.server.message.SmsSubmission;
import com.mobicomp.smsexpress.webservice.server.message.SubmissionManagerProxy;
import com.mobicomp.smsexpress.webservice.server.message.SubmissionStatus;

public class SMSExpressGatewayImpl extends SMSGateway {

  public static final String PARAM_SMS_EXPRESS_USERNAME = "SMS_EXPRESS_USERNAME";
  public static final String PARAM_SMS_EXPRESS_APPLICATION = "SMS_EXPRESS_APPLICATION";
  public static final String PARAM_SMS_EXPRESS_PASSWORD = "SMS_EXPRESS_PASSWORD";
  public static final String PARAM_SMS_EXPRESS_ORGANIZATION_SENDER = "SMS_EXPRESS_ORGANIZATION_SENDER";

  @Override
  public boolean send(String applicationUser) {
    Properties iflowSystemProperties = Setup.getProperties();
    Boolean wasSMSSubmitted = false;

    AuthenticationInfo authentication = this.getSMSExpressAuthenticationInfo(applicationUser, iflowSystemProperties);

    if (authentication != null){
      SubmissionManagerProxy submissionManagerProxy = new SubmissionManagerProxy();

      SmsSubmission submission = this.createSMSSubmissionObject(applicationUser, this.getSmsData(), iflowSystemProperties);

      if (submission != null){
        try {
          SubmissionStatus submisionResult = submissionManagerProxy.sendSmsSubmission(authentication, submission);

          if (submisionResult.getStatus() == 0){
            wasSMSSubmitted = true;
          }
          Logger.debug(applicationUser, this, "send", "SMS submision Result: id ["+submisionResult.getId()+"], message ["+submisionResult.getMessage()+"], status ["+submisionResult.getStatus()+"]");
        } catch (AxisFault axisException){
          wasSMSSubmitted = false;
          Logger.error(applicationUser, this, "send", "Error, while atempting to submit sms, reason ["+axisException.getFaultCode()+" - "+axisException.getFaultString()+"]", axisException);
        } catch (RemoteException remoteException) {
          wasSMSSubmitted = false;
          Logger.error(applicationUser, this, "send", "Error, while atempting to submit sms, problem with the communication", remoteException);
        } catch (Exception e){
          wasSMSSubmitted = false;
          Logger.error(applicationUser, this, "send", "Error, unexpected error while submitting", e);
        }
      } else {
        Logger.error(applicationUser, this, "send", "Unable to send message, error while creating sms submission data");
        wasSMSSubmitted = false;
      }
    } else {
      Logger.error(applicationUser, this, "send", "Unable to send message, error with authentication data provided");
      wasSMSSubmitted = false;
    }
    return wasSMSSubmitted;
  }

  private SmsSubmission createSMSSubmissionObject(String applicationUser, SMSData smsData, Properties iflowSystemProperties) {
    SmsSubmission submission = null;
    String senderUserName = iflowSystemProperties.getProperty(SMSExpressGatewayImpl.PARAM_SMS_EXPRESS_ORGANIZATION_SENDER);

    if (senderUserName != null){
      try {
        submission = new SmsSubmission();
        Collection<String> recepientsList = smsData.getPhoneList();
        submission.setRecipients(recepientsList.toArray(new String[recepientsList.size()]) );
        submission.setMessage(smsData.getSMSMessageToSubmit());
        submission.setSender(senderUserName);
        submission.setValidity(smsData.getValidity());

        Calendar deliveryDate = smsData.getDeliveryDate();
        submission.setDeliveryDate(deliveryDate );
      } catch (Exception e) {
        Logger.error(applicationUser, this, "createSMSSubmissionObject", "Unexpected error while creating smsSubmissionObject", e);
      }
    } else {
      Logger.error(applicationUser, this, "createSMSSubmissionObject", "Error while creating smsSubmissionObject, no sender id provided");
    }

    return submission;
  }

  private AuthenticationInfo getSMSExpressAuthenticationInfo(String applicationUser, Properties iflowSystemProperties) {
    String application = iflowSystemProperties.getProperty(SMSExpressGatewayImpl.PARAM_SMS_EXPRESS_APPLICATION);
    String username = iflowSystemProperties.getProperty(SMSExpressGatewayImpl.PARAM_SMS_EXPRESS_USERNAME);
    String password = iflowSystemProperties.getProperty(SMSExpressGatewayImpl.PARAM_SMS_EXPRESS_PASSWORD);

    AuthenticationInfo authenticationInfo = null;
    if (application != null && username != null && password != null ) {
      try {
        authenticationInfo = new AuthenticationInfo(application, password, username);
      } catch (Exception e) {
        authenticationInfo = null;
        Logger.error(applicationUser, this, "getSMSExpressAuthenticationInfo", "Unexpected error: ",e);
      }
    }

    return authenticationInfo;
  }

  @Override
  public boolean init(Properties initParams) {
    this.setSmsData(new SMSData());
    return true;
  }

}
