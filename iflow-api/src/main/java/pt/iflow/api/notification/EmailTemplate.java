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
