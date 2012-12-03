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
public class FormInputTag extends IknowTag {
  /**
   * 
   */
  private static final long serialVersionUID = 1710739291463847027L;

  private String type;
  private String name;
  private String value;
  private String labelkey;
  private String label;
  private boolean edit;
  private boolean required;
  private String maxlength;
  private String size;
  private String onchange;
  private String onblur;

  public FormInputTag() {
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
    setType("text");
    setEdit(false);
    setRequired(false);
    setMaxlength("");
    setOnchange("");
    setOnblur("");
  }

  private static String generateInputField(String asType, String asName, String asValue, String asLabelKey,String asLabel, boolean abEdit, boolean abRequired, String maxlength, String size, String onchange, String onblur, IMessages msg) {
    if(!abEdit && "challenge".equalsIgnoreCase(asType)) return ""; // No need to challenge user

    StringBuffer sb = new StringBuffer();

    sb.append("<li>");
    sb.append("<label for=\"");
    sb.append(asName);
    sb.append("\">"); 
    if("challenge".equalsIgnoreCase(asType)) {
      sb.append("<img src=\"kaptcha?ts="+System.currentTimeMillis()+"\" id=\""+asName+"_kap\" alt=\"");
    }
    
    if(null != asLabelKey && asLabelKey.trim().length() > 0) {
      sb.append(msg.getString(asLabelKey));
    } 
    else if(null != asLabel && asLabel.trim().length() > 0) {
      sb.append(asLabel);
    }
    
    if("challenge".equals(asType)) {
      sb.append("\" />");
      sb.append("<img src=\"images/icon_resync.png\" style=\"top: -34px; position: relative;\" onclick=\"document.getElementById('"+asName+"_kap').src=document.getElementById('"+asName+"_kap').src+'x';\"/>");
      asType = "text";
      asValue = "";
    }
    
    

    if (abEdit && abRequired)
      sb.append("<em>*</em>");
    sb.append("</label>");

    if("logo".equalsIgnoreCase(asType)) {
      sb.append("<img src=\"Logo?ts="+System.currentTimeMillis()+"\" alt=\"logo\" />");
      
      if(abEdit) {
        sb.append("<input type=\"file\" name=\"").append(asName).append("\" id=\"").append(asName).append("\">");
      }
    } else if("checkbox".equals(asType)) {
      sb.append("<input type=\"checkbox\" name=\"").append(asName);
      sb.append("\" id=\"").append(asName).append("\" value=\"true");
      if(StringUtils.equalsIgnoreCase("true", asValue)) sb.append("\" checked=\"checked");
      if(!abEdit) sb.append("\" disabled=\"disabled");
      sb.append("\"");
      if (StringUtils.isNotEmpty(onchange)) {
      	sb.append(" onchange=\"").append(onchange).append("\"");
      }
      if (StringUtils.isNotEmpty(onblur)) {
        sb.append(" onblur=\"").append(onblur).append("\"");
      }
      sb.append("/>");
    } else {
      if (abEdit) {
        sb.append("<input type=\"");
        sb.append(asType);
        sb.append("\" name=\"");
        sb.append(asName);
        sb.append("\" id=\"");
        sb.append(asName);
        sb.append("\" value=\"");
        sb.append(asValue);
        if(("text".equals(asType) || "password".equals(asType))&&!StringUtils.isEmpty(maxlength)) {
          sb.append("\" maxlength=\"");
          sb.append(maxlength);
        }
        if(("text".equals(asType) || "password".equals(asType))&&!StringUtils.isEmpty(size)) {
          sb.append("\" size=\"");
          sb.append(size);
        }
        sb.append("\"");
        if (StringUtils.isNotEmpty(onchange)) {
        	sb.append(" onchange=\"").append(onchange).append("\"");
        }
        if (StringUtils.isNotEmpty(onblur)) {
          sb.append(" onblur=\"").append(onblur).append("\"");
        }
        sb.append("/>");
      }
      else {
        sb.append(asValue); 
      }
    }
    sb.append("</li>");


    return sb.toString();
  }

  private String generateInputField() {
    IMessages msg = getUserMessages();
    return generateInputField(this.getType(),this.getName(),this.getValue(),this.getLabelkey(),this.getLabel(),this.isEdit(), this.isRequired(), this.getMaxlength(), this.getSize(), this.getOnchange(), this.getOnblur(), msg);
  }


  public int doEndTag() throws JspException {

    String result = generateInputField();
    try {
      pageContext.getOut().write(result);
    }
    catch (IOException e) {
      throw new JspException(e);
    }
    return EVAL_PAGE;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
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
  
  public String getMaxlength() {
    return maxlength;
  }
  
  public void setMaxlength(String maxlength) {
    this.maxlength = maxlength;
  }

  public void setValue(boolean v) {
    this.value = String.valueOf(v);
  }
  
  public void setValue(int v) {
    this.value = String.valueOf(v);
  }
  
  public void setValue(float v) {
    this.value = String.valueOf(v);
  }
  
  public void setValue(long v) {
    this.value = String.valueOf(v);
  }
  
  public void setValue(double v) {
    this.value = String.valueOf(v);
  }

  public String getSize() {
    return size;
  }

  public void setSize(String size) {
    this.size = size;
  }
  
  public String getOnchange() {
    return onchange;
  }

  public void setOnchange(String onchange) {
    this.onchange = onchange;
  }

  public String getOnblur() {
    return onblur;
  }

  public void setOnblur(String onblur) {
    this.onblur = onblur;
  }
}
