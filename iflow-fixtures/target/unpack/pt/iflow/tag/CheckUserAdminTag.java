package pt.iflow.tag;

import javax.servlet.jsp.JspException;

import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;

/**
 * 
 * Simple tag to check if current user is administrator or not<br>
 * Usage:
 * <p>First of all, you must prepare your web application, copying the file <b>iflow.tld</b> to folder <b>WEB-INF/tlds</b></p>
 * <p>Then you must modify your web.xml file and add the following lines:<br>
 * <pre>
 * &lt;taglib&gt;
 *   &lt;taglib-uri&gt;http://www.iknow.pt/jsp/jstl/iflow&lt;/taglib-uri&gt;
 *   &lt;taglib-location&gt;/WEB-INF/tlds/iflow.tld&lt;/taglib-location&gt;
 * &lt;/taglib&gt;
 * </pre>
 * 
 * <p>Once the web application has been configured, just add the following line to the top of your JSP file:<br>
 * <pre>
 * &lt;%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %&gt;
 * </pre></p>
 * 
 * <p>Then, just add the following tag where the externalized string must go:<br>
 * <code>&lt;if:message string="string.name"/&gt;</code></p>
 * 
 * <p>Note: Because
 * of the support provided by the ConditionalTagSupport class, this
 * tag is trivial enough not to require a separate base supporting class
 * common to both libraries.</p>
 * 
 * @author oscar
 * 
 */
public class CheckUserAdminTag extends IknowTag {
   /**
   * 
   */
  private static final long serialVersionUID = 1710739291463847027L;

  private final static String TYPE_SYS = "sys";
  private final static String TYPE_ORG = "org";
  private final static String TYPE_BOTH = "both";
  private final static String TYPE_SPV = "spv";
  
  private boolean test = false;
  private String type = null;
  
  public CheckUserAdminTag() {
    super();
    init();
  }
  
  // Releases any resources we may have (or inherit)
  public void release() {
      super.release();
      init();
  }

  public int doStartTag() throws JspException {
    UserInfoInterface userInfo = (UserInfoInterface) pageContext.getSession().getAttribute(Const.USER_INFO);
    
    if (Logger.isDebugEnabled()) {
      Logger.debug(userInfo.getUtilizador(), this, "doStartTag", "entered with type '" + getType()
          + "', user priviledges: SysAdmin?" + userInfo.isSysAdmin() + ", OrgAdmin?" + userInfo.isOrgAdmin() + ", ProcSpv?"
          + userInfo.isProcSupervisor(-1));
    }
    
    if (getType().equals(TYPE_SYS)) {
    	test = userInfo.isSysAdmin();
    }
    else if (getType().equals(TYPE_BOTH)) {
    	test = userInfo.isSysAdmin() || userInfo.isOrgAdmin();
    }
    else if (getType().equals(TYPE_ORG)){
    	test = userInfo.isOrgAdmin();
    } else if(getType().equals(TYPE_SPV)) {
      test = userInfo.isSysAdmin() || userInfo.isOrgAdmin() || userInfo.isProcSupervisor(-1);
    }

    if (Logger.isDebugEnabled()) {
      Logger.debug(userInfo.getUtilizador(), this, "doStartTag", "exit with test?" + test + " and returning="
          + (test ? SKIP_BODY : EVAL_BODY_INCLUDE));
    }
    
    return test?SKIP_BODY:EVAL_BODY_INCLUDE;
  }
  
  
  public int doEndTag() throws JspException {
    
    return test?EVAL_PAGE:SKIP_PAGE;
  }
  
  private void init() {
    test = false;
    type = "org";
  }

public String getType() {
	return type;
}

public void setType(String type) {
	this.type = type;
}
}

