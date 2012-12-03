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
package pt.iflow.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import pt.iflow.api.msg.IMessages;

/**
 * 
 * Simple tag to present externalized strings values<br>
 * Usage:
 * <p>First of all, you must prepare your web application, copying the file <b>msg.tld</b> to the folder <b>WEB-INF/tlds</b></p>
 * <p>Then you must modify your web.xml file and add the following lines:<br>
 * <pre>
 * &lt;taglib&gt;
 *   &lt;taglib-uri&gt;http://www.iknow.pt/jsp/jstl/msg&lt;/taglib-uri&gt;
 *   &lt;taglib-location&gt;/WEB-INF/tlds/msg.tld&lt;/taglib-location&gt;
 * &lt;/taglib&gt;
 * </pre>
 * 
 * <p>Once the web application has been configured, just add the following line to the top of your JSP file:<br>
 * <pre>
 * &lt;%@ taglib uri="http://www.iknow.pt/jsp/jstl/msg" prefix="msg" %&gt;
 * </pre></p>
 * 
 * <p>Then, just add the following tag where the externalized string must go:<br>
 * <code>&lt;msg:message string="string.name"/&gt;</code></p>
 * 
 * @author oscar
 * 
 */
public class MessageTag extends IknowTag {
   /**
   * 
   */
  private static final long serialVersionUID = 1710739291463847027L;
  
  private String string;
  private String param;

   public void setString(String name) {
      string = name;
   }

   public int doEndTag() throws JspException {
     IMessages msg = getUserMessages();
     String name = null;
     if(param == null || param.trim().length() == 0)
       name = msg.getString(string);
     else 
       name = msg.getString(string, param);
     
     if (name == null) {
       name = string;
     }
     if (name == null) {
       name = "null";
     }
     try {
       pageContext.getOut().write(name);
     }
     catch (IOException e) {
       throw new JspException(e);
     }
     return EVAL_PAGE;
   }

  public String getParam() {
    return param;
  }

  public void setParam(String param) {
    this.param = param;
  }
}

