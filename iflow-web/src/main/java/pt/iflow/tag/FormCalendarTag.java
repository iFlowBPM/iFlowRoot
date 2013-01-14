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
public class FormCalendarTag extends IknowTag {
  /**
   * 
   */
  private static final long serialVersionUID = 1710739291463847027L;

  private String name;
  private String value;
  private String labelkey;
  private String label;
  private boolean edit;
  private boolean required;

  public FormCalendarTag() {
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
  }
  
  
  private static String generateInputField(String asName, String asValue, String asLabelKey,String asLabel, boolean abEdit, boolean abRequired, IMessages msg) {
    StringBuffer sb = new StringBuffer();

    sb.append("<li>");
    sb.append("<label for=\"");
    sb.append(asName);
    sb.append("\">"); 

    if(null != asLabelKey && asLabelKey.trim().length() > 0) {
      sb.append(msg.getString(asLabelKey));
    } 
    else if(null != asLabel && asLabel.trim().length() > 0) {
      sb.append(asLabel);
    }

    if (abEdit && abRequired)
      sb.append("<em>*</em>");
    sb.append("</label>");

    if (abEdit) {
      sb
      .append("<input type=\"text\" name=\"")
      .append(asName)
      .append("\" id=\"")
      .append(asName)
      .append("\" value=\"")
      .append(asValue)
      .append("\" maxlength=\"12\" size=\"12\" onmouseover=\"caltasks(this.id);this.onmouseover=null;\"/>")
      .append("<img border=\"0\" src=\"images/icon_delete.png\" onclick=\"javascript:document.getElementById('")
      .append(asName)
      .append("').value='';\"/>");
    }
    else {
      sb.append(asValue); 
    }
    sb.append("</li>");


    return sb.toString();
  }

  private String generateInputField() {
    IMessages msg = getUserMessages();
    return generateInputField(this.getName(),this.getValue(),this.getLabelkey(),this.getLabel(),this.isEdit(), this.isRequired(), msg);
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
  
}

