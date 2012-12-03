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
public class TitleBubbleTag extends IknowTag {
  /**
   * 
   */
  private static final long serialVersionUID = 1710739291463847027L;

  private String message;
  private String floatpos;
  private boolean transparent;
  private String marginpos;

  public TitleBubbleTag() {
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
    setMessage("");
    setFloatpos("left");
    setTransparent(false);
  }
  
  
  private String generateTitleBubble(String asMessage, String asFloat, boolean abTransparent, String asMarginPos) {
    IMessages msg = getUserMessages();

    StringBuffer sb = new StringBuffer();

    String firstImg = "left";
    String secondImg = "right";
    String firstMargin = "";
    String secondMargin = "";
    boolean bFloatRight = false;
    
    if ("right".equals(asFloat)) {
    	firstImg = "right";
    	secondImg = "left";
    	bFloatRight = true;
    }
    
    if ("right".equals(asMarginPos)) {
    	if (bFloatRight) {
    		firstMargin = "rp_margin_right";
    	}
    	else {
    		secondMargin = "rp_margin_right";
    	}
    }
    else if ("left".equals(asMarginPos)) {
    	if (bFloatRight) {
    		secondMargin = "rp_margin_left";
    	}
    	else {
    		firstMargin = "rp_margin_left";
    	}    	
    }
    
	sb.append("<div class=\"rp_").append(firstImg).append("_img rp_").append(asFloat);
	if (abTransparent) { 
		sb.append(" rp_disabled");
	}
	sb.append(" ").append(firstMargin);
	sb.append("\"></div>");
	sb.append("<div class=\"rp_title rp_").append(asFloat);
	if (abTransparent) { 
		sb.append(" rp_disabled");
	}	
	sb.append("\" style=\"float: ").append(asFloat).append(";\">");
	sb.append(msg.getString(asMessage));
	sb.append("</div>");				
	sb.append("<div class=\"rp_").append(secondImg).append("_img rp_").append(asFloat);
	if (abTransparent) { 
		sb.append(" rp_disabled");
	}
	sb.append(" ").append(secondMargin);
	sb.append("\"></div>");

    return sb.toString();
  }
  
  private String generateTitleBubble() {

    return generateTitleBubble(this.getMessage(),this.getFloatpos(),this.isTransparent(), this.getMarginpos());
  }


  public int doEndTag() throws JspException {

    String result = generateTitleBubble();
    try {
      pageContext.getOut().write(result);
    }
    catch (IOException e) {
      throw new JspException(e);
    }
    return EVAL_PAGE;
  }

public String getMessage() {
	return message;
}

public void setMessage(String message) {
	this.message = message;
}

public String getFloatpos() {
	return floatpos;
}

public void setFloatpos(String floatpos) {
	this.floatpos = floatpos;
}

public boolean isTransparent() {
	return transparent;
}

public void setTransparent(boolean required) {
	this.transparent = required;
}

public String getMarginpos() {
	return marginpos;
}

public void setMarginpos(String marginpos) {
	this.marginpos = marginpos;
}

}

