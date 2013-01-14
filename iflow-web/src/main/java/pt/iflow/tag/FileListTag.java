package pt.iflow.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.Repository;
import pt.iflow.api.core.RepositoryFile;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.servlets.ResourceNavConsts;

/**
 * 
 * Simple tag to list repository files<br>
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
 * <code>&lt;msg:getFileList var="varname" type="list.type"/&gt;</code></p>
 * 
 * @author oscar
 * 
 */
public class FileListTag extends IknowTag {
   /**
   * 
   */
  private static final long serialVersionUID = 1710739291463847027L;
  
  
  public static RepositoryFile[] listFiles(UserInfoInterface userInfo, String type) {
    RepositoryFile[] result = null;
    Repository rep = BeanFactory.getRepBean();
    if(ResourceNavConsts.STYLESHEETS.equals(type)) {
      result = rep.listStyleSheets(userInfo);
    } else if(ResourceNavConsts.EMAIL_TEMPLATES.equals(type)) {
      result = rep.listEmailTemplates(userInfo);
    } else if(ResourceNavConsts.PUBLIC_FILES.equals(type)) {
      result = rep.listWebFiles(userInfo);
    } else if(ResourceNavConsts.PRINT_TEMPLATES.equals(type)) {
      result = rep.listPrintTemplates(userInfo);
    } else {
      Logger.warning("", "", "", "Erro! Tipo desconhecido: "+type);
    }
    
    return result;
  }
  
  
  private String var;
  private String type;

  public String getVar() {
    return var;
 }

   public void setVar(String var) {
      this.var = var;
   }

   public void setType(String type) {
     this.type = type;
  }


   public int doEndTag() throws JspException {
     UserInfoInterface userInfo = (UserInfoInterface) pageContext.getSession().getAttribute(Const.USER_INFO);
     pageContext.setAttribute(var, listFiles(userInfo, type), PageContext.PAGE_SCOPE);
     return EVAL_PAGE;
   }
}

