package pt.iflow.api.notification;



/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: iKnow</p>
 * @author iKnow
 * @version 1.0
 */

public class EmailTemplate {

  private String sName = "";

  private String sFrom = "";
  private String sSubject = "";
  private String sBody = "";

  private boolean bHtml = false;

  /**
   *
   * @param host
   */
  protected EmailTemplate(String asName, String asFrom, String asSubject, String asBody, boolean abHtml) {
    this.setName(asName);
    this.setFrom(asFrom);
    this.setSubject(asSubject);
    this.setBody(asBody);
    this.setHtml(abHtml);
  }


  public void setName(String asName) {
    this.sName = asName;
  }

  public String getName() {
    return this.sName;
  }

  /**
   *
   * @param from
   */
  public void setFrom(String asFrom) {
    this.sFrom = asFrom;
  }

  public String getFrom() {
    return this.sFrom;
  }


  /**
   *
   * @param subject
   */
  public void setSubject(String asSubject) {
    this.sSubject = asSubject;
  }

  public String getSubject() {
    return this.sSubject;
  }


  /**
   *
   * @param msgText
   */
  public void setBody(String asBody) {
    this.sBody = asBody;
  }

  public String getBody() {
    return this.sBody;
  }

  /**
   *
   * @param abHtml
   */
  public void setHtml(boolean abHtml) {
    this.bHtml = abHtml;
  }

  public boolean getHtml() {
    return this.bHtml;
  }
}
