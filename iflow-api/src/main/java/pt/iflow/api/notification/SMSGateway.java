/*
 *
 * Created on Apr 28, 2006 by mach
 *
 */

package pt.iflow.api.notification;

import java.util.Properties;

public abstract  class SMSGateway {
  private SMSData smsData = new SMSData();;

  public SMSData getSmsData() {
    return smsData;
  }

  public void setSmsData(SMSData smsData) {
    this.smsData = smsData;
  }

  public  abstract boolean init(Properties initParams);

  /**
   *
   * @param applicationUser 
   * @return
   */
  public abstract boolean send(String applicationUser);

}