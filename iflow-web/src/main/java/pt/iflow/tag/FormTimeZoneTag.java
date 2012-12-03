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
import java.io.Writer;

import javax.servlet.jsp.JspException;

import pt.iflow.api.msg.IMessages;
import pt.iflow.api.presentation.DateUtility;

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
public class FormTimeZoneTag extends FormSelectTag {
  /**
   * 
   */
  private static final long serialVersionUID = 1710739291849327027L;

  private void writeTZData() throws IOException {
    Writer out = pageContext.getOut();
    IMessages msg = getUserMessages();
    
    String [] tzIDs = DateUtility.getAvailableTimezones();
    FormOptionTag optTag = new FormOptionTag();
    optTag.setPageContext(pageContext);
    optTag.setParent(this);
    for (String tzId : tzIDs) {
      optTag.setValue(tzId);
      optTag.setLabel(tzId);
      out.write(optTag.generateOptionField(this, msg));
    }
  }
  
  public int doStartTag() throws JspException {
    super.doStartTag();
    try {
      writeTZData();
    }
    catch (IOException e) {
      throw new JspException(e);
    }
    return EVAL_BODY_INCLUDE;
  }

}

