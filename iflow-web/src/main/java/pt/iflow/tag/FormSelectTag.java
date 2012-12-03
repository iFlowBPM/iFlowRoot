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

import org.apache.commons.lang.StringUtils;

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
public class FormSelectTag extends IknowTag {
  /**
   * 
   */
  private static final long serialVersionUID = 1710739291463847027L;

  private String name;
  private String value;
  private String labelkey;
  private String label;
  private String onchange;
  private boolean edit;
  private boolean required;
  private boolean noli;
  private boolean multiple;
  private int size;

  public FormSelectTag() {
    super();
    init();
  }

  /**
   * Release resources
   */
  public void release() {
      super.release();
      init();
  }

  /**
   * Set default tag values
   */
  private void init() {
    setEdit(false);
    setRequired(false);
    setOnchange(null);
    setMultiple(false);
    setSize(1);
  }
  
  private static boolean isNotClean(String str) {
    return null != str && str.trim().length() > 0;
  }
  
  private String genereateOpenSelect() {
    IMessages msg = getUserMessages();

    StringBuffer sb = new StringBuffer();

    if(!isNoli())
      sb.append("<li>");
    boolean doLabel = true;
    if(isNoli())
      doLabel = isNotClean(getLabelkey()) || isNotClean(getLabel());
    
    if(doLabel) {
      sb.append("<label for=\"");
      sb.append(getName());
      sb.append("\">"); 
      if(isNotClean(getLabelkey())) {
        sb.append(msg.getString(getLabelkey()));
      } else if(isNotClean(getLabel())) {
        sb.append(getLabel());
      }
      if (isEdit() && isRequired())
        sb.append("<em>*</em>");
      sb.append("</label>");

    }

    if (isEdit()) {
      sb.append("<select name=\"").append(getName()).append("\" id=\"").append(getName()).append("\"");
      if(StringUtils.isNotEmpty(onchange))
        sb.append(" onchange=\"").append(onchange).append("\"");
      if (multiple)
    	  sb.append(" MULTIPLE ");
      if (size > 1)
    	  sb.append(" SIZE=").append(size);
      sb.append(">");
    }

    return sb.toString();
  }

  public String generateCloseSelect() {
    StringBuffer sb = new StringBuffer();
    
    if(isEdit()) sb.append("</select>");
    if(!isNoli())
      sb.append("</li>");
    return sb.toString();
  }
  
  public int doStartTag() throws JspException {
    String openSelect = genereateOpenSelect();
    try {
      pageContext.getOut().write(openSelect);
    }
    catch (IOException e) {
      throw new JspException(e);
    }
    return EVAL_BODY_INCLUDE;
  }
  
  public int doEndTag() throws JspException {
    String closeSelect = generateCloseSelect();
    try {
      pageContext.getOut().write(closeSelect);
    }
    catch (IOException e) {
      throw new JspException(e);
    }
    return EVAL_PAGE;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public boolean isEdit() {
    return edit;
  }

  public void setEdit(boolean edit) {
    this.edit = edit;
  }

  public String getLabelkey() {
    return labelkey;
  }
  
  public void setLabelkey(String labelkey) {
    this.labelkey = labelkey;
  }
  
  public String getLabel() {
    return label;
  }
  
  public void setLabel(String label) {
    this.label = label;
  }
  
  public boolean isRequired() {
    return required;
  }
  
  public void setRequired(boolean required) {
    this.required = required;
  }

  public String getOnchange() {
    return onchange;
  }

  public void setOnchange(String onchange) {
    this.onchange = onchange;
  }

  public boolean isNoli() {
    return noli;
  }
  
  public void setNoli(boolean noli) {
    this.noli = noli;
  }

  public boolean isMultiple() {
    return multiple;
  }
	  
  public void setMultiple(boolean multiple) {
    this.multiple = multiple;
  }
  
  public int getSize() {
    return size;
  }
	  
  public void setSize(int size) {
    this.size = size;
  }

}

