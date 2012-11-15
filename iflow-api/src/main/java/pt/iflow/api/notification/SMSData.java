package pt.iflow.api.notification;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import org.apache.axis.utils.StringUtils;

public class SMSData {
  private String message = "";
  private String subject = "";
  private ArrayList <String> phoneList = new ArrayList <String>();
  private String sender = "";
  private Calendar deliveryDate = Calendar.getInstance();
  private int validity = 24;

  public String getMessage() {
    return this.message;
  }

  public String getSender() {
    return sender;
  }

  public void setSender(String sender) {
    this.sender = sender;
  }

  public Calendar getDeliveryDate() {
    return deliveryDate;
  }

  public void setDeliveryDate(Calendar deliveryDate) {
    this.deliveryDate = deliveryDate;
  }

  public int getValidity() {
    if (validity < 1){
      validity = 24;
    }
    return validity;
  }

  public void setValidity(int validity) {
    this.validity = validity;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getSMSMessageToSubmit() {
    String smsMessage = "";
    if (!StringUtils.isEmpty(this.getSubject())){
      smsMessage = this.getSubject();
    }
    smsMessage += this.message;
    return smsMessage;
  }

  public String getSubject() {
    return this.subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public void addPhone(String aPhone) {
    if(!this.phoneList.contains(aPhone)){
      this.phoneList.add(aPhone);
    }
  }

  public void removePhone(String aPhone) {
    if(this.phoneList.contains(aPhone)){
      this.phoneList.remove(aPhone);
    }
  }

  public void resetPhoneList() {
    this.phoneList.clear();
  }

  public Collection <String> getPhoneList() {
    return this.phoneList;
  }

  public void setPhoneList(ArrayList<String> phoneList) {
    this.phoneList = phoneList;
  }
}
