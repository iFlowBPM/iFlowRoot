package pt.iflow.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.Tag;

import pt.iflow.api.msg.IMessages;

/**
 * 
 * Simple tag to present externalized strings values<br>
 * Usage:
 * <p>
 * First of all, you must prepare your web application, copying the file
 * <b>msg.tld</b> to the folder <b>WEB-INF/tlds</b>
 * </p>
 * <p>
 * Then you must modify your web.xml file and add the following lines:<br>
 * 
 * <pre>
 * &lt;taglib&gt;
 *   &lt;taglib-uri&gt;http://www.iknow.pt/jsp/jstl/msg&lt;/taglib-uri&gt;
 *   &lt;taglib-location&gt;/WEB-INF/tlds/msg.tld&lt;/taglib-location&gt;
 * &lt;/taglib&gt;
 * </pre>
 * 
 * <p>
 * Once the web application has been configured, just add the following line to
 * the top of your JSP file:<br>
 * 
 * <pre>
 * &lt;%@ taglib uri=&quot;http://www.iknow.pt/jsp/jstl/msg&quot; prefix=&quot;msg&quot; %&gt;
 * </pre>
 * 
 * </p>
 * 
 * <p>
 * Then, just add the following tag where the externalized string must go:<br>
 * <code>&lt;msg:message string="string.name"/&gt;</code>
 * </p>
 * 
 * @author oscar
 * 
 */
public class FormOptionTag extends IknowTag {
  /**
   * 
   */
  private static final long serialVersionUID = 1710739291463847027L;

  private String value;
  private String labelkey;
  private String label;

  public FormOptionTag() {
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
  }

  public String generateOptionField(FormSelectTag parent, IMessages msg) {

    String value = getValue();
    String label = value;
    if (null != getLabelkey() && getLabelkey().trim().length() > 0) {
      label = msg.getString(getLabelkey());
    } else if (null != getLabel() && getLabel().trim().length() > 0) {
      label = getLabel();
    }
    String parentValue = parent.getValue();
    if(null == parentValue) parentValue = "";
    boolean parentValueMatch = parentValue.equals(value);
    
    String result = null;
    if(parent.isEdit()) {
      StringBuffer sb = new StringBuffer();

      sb.append("<option value=\"").append("${fn:escapeXml("+value+")}").append("\"");

      // check if selected value is good
      if(parentValueMatch) {
        sb.append(" selected=\"selected\"");
      }
      sb.append(">").append(label).append("</option>");

      result = sb.toString();
    } else if(parentValueMatch){
      result = label;
    }
    return result;
  }

  public int doEndTag() throws JspException {

    IMessages msg = getUserMessages();

    // First, find parent
    Tag parent = findAncestorWithClass(this, FormSelectTag.class);
    
    if(null == parent)
      throw new JspTagException(msg.getString("Erro"));

    // handle conditional behavior
    String optionLine = generateOptionField((FormSelectTag)parent, msg);
    if(null != optionLine) {
      try {
        pageContext.getOut().write(optionLine);
      }
      catch (IOException e) {
        throw new JspException(e);
      }
    }


    return EVAL_PAGE;
  }
  
  
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public void setValue(int value) {
    this.value = String.valueOf(value);
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

}
