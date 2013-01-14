package pt.iflow.tag;

import java.io.IOException;
import java.io.Writer;
import java.util.Locale;

import javax.servlet.jsp.JspException;

import org.apache.hivemind.util.StringUtils;

import pt.iflow.api.core.Settings;
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
public class FormLocaleTag extends FormSelectTag {
  private static final long serialVersionUID = 171073929146389407L;
  
  
  private void writeLocaleData() throws IOException {
    Writer out = pageContext.getOut();
    IMessages msg = getUserMessages();
    
    FormOptionTag optTag = new FormOptionTag();
    optTag.setPageContext(pageContext);
    optTag.setParent(this);
    for (Locale tzId : Settings.localeKeys) {
      optTag.setValue(tzId.toString());
      optTag.setLabel(StringUtils.capitalize(tzId.getDisplayLanguage(tzId)));
      out.write(optTag.generateOptionField(this, msg));
    }
  }
  
  public int doStartTag() throws JspException {
    super.doStartTag();
    try {
      writeLocaleData();
    }
    catch (IOException e) {
      throw new JspException(e);
    }
    return EVAL_BODY_INCLUDE;
  }

}

