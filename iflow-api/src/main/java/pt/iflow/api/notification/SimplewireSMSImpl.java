/*
 *
 * Created on Apr 28, 2006 by mach
 *
  */

package pt.iflow.api.notification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

import com.simplewire.sms.SMS;

/**
 * 
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright (c) 2006 mach</p>
 * 
 * @author mach
 */

public class SimplewireSMSImpl extends SMSGateway {
  
  public static final String PARAM_SUBSCRIBER_ID = "SW_SUBSCRIBER_ID";
  public static final String PARAM_SUBSCRIBER_PASSWORD = "SW_SUBSCRIBER_PASSWORD";
  public static final String PARAM_SMS_SOURCE_NUMBER = "SMS_SOURCE_NUMBER";
  
  private String _subscriberID;
  private String _subscriberPassword;

  /* (non-Javadoc)
   * @see pt.iknow.notification.SMSGateway#init(java.util.Properties)
   */
  public boolean init(Properties initParams) {
    boolean retValue=false;
    
    if(initParams.containsKey(PARAM_SUBSCRIBER_ID) 
        && initParams.containsKey(PARAM_SUBSCRIBER_PASSWORD)
        && initParams.containsKey(PARAM_SMS_SOURCE_NUMBER)) {
      this._subscriberID = (String)initParams.get(PARAM_SUBSCRIBER_ID);
      this._subscriberPassword = (String)initParams.get(PARAM_SUBSCRIBER_PASSWORD);

      SMSData smsData = new SMSData();
      String sourceNumber = (String)initParams.get(PARAM_SMS_SOURCE_NUMBER);
      smsData.setSender(sourceNumber);
      smsData.setPhoneList(new ArrayList<String>());
      this.setSmsData(smsData);

      retValue = true;
    } 

    return retValue;
  }

  /* (non-Javadoc)
   * @see pt.iknow.notification.SMSGateway#send()
   */
  public boolean send(String applicationUser) {
    boolean retValue = true;
    
    SMS smsEngine = new SMS();
    
    smsEngine.setSubscriberID(this._subscriberID);
    smsEngine.setSubscriberPassword(this._subscriberPassword);
    smsEngine.setSourceAddr(this.getSmsData().getSender());
    smsEngine.setMsgFrom(this.getSmsData().getSubject());
    smsEngine.setMsgText(this.getSmsData().getMessage());
    
    Iterator iter = this.getSmsData().getPhoneList().iterator();
    
    while(iter.hasNext()) {
      smsEngine.setDestinationAddr((String)iter.next());
      if(smsEngine.submit()) {
        iter.remove();
      }        
    }
    
    if(this.getSmsData().getPhoneList().size() > 0) {
      retValue = false;
    } else {
      retValue = true;
    }
    
    return retValue;
  }
}
