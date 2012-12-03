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

import java.util.Hashtable;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.msg.IMessages;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.msg.Messages;

public abstract class IknowTag extends TagSupport {

  private static final long serialVersionUID = 1923878616768890086L;

  protected IMessages getUserMessages() {
    UserInfoInterface userInfo = (UserInfoInterface) pageContext.getAttribute(Const.USER_INFO);
    if(userInfo == null) {
      HttpSession session = (HttpSession) pageContext.getAttribute(PageContext.SESSION);
      if (session != null) {
        try {
          userInfo = (UserInfoInterface) session.getAttribute(Const.USER_INFO);
        }
        catch (IllegalStateException ise) {
          Logger.warning("", this, "getUserMessages", 
              "illegal state trying to get user info from session... ignoring" );
        }
      }
    }
    
    IMessages msg = null;
    boolean checkCookie = false;
    if(null == userInfo) {
      checkCookie = true;
      msg = Messages.getInstance();
    } else {
      if(userInfo.getUserSettings().isDefault()) {
        checkCookie = true;
      }
      msg = userInfo.getMessages();
    }
    
    if(checkCookie) {
      Hashtable<String,String> cookies = pt.iflow.api.utils.ServletUtils.getCookies((HttpServletRequest)pageContext.getRequest());
      String lang = cookies.get(Const.LANG_COOKIE);
      if(StringUtils.isNotEmpty(lang)) {
        msg = Messages.getInstance(lang);
      }
    }
    
    // just in case....
    if(null == msg) msg = Messages.getInstance();
    return msg;
  }
  
  protected Locale getUserLocale() {
    UserInfoInterface userInfo = (UserInfoInterface) pageContext.getAttribute(Const.USER_INFO);
    Locale loc = null;
    boolean checkCookie = false;
    if(null == userInfo) {
      checkCookie = true;
      loc = Locale.getDefault();
    } else {
      if(userInfo.getUserSettings().isDefault()) {
        checkCookie = true;
      }
      loc = userInfo.getUserSettings().getLocale();
    }
    
    if(checkCookie) {
      Hashtable<String,String> cookies = pt.iflow.api.utils.ServletUtils.getCookies((HttpServletRequest)pageContext.getRequest());
      String lang = cookies.get(Const.LANG_COOKIE);
      if(StringUtils.isNotEmpty(lang)) {
        String [] parts = lang.split("_");
        loc = new Locale(parts[0],parts[1]);
      }
    }
    
    // just in case....
    if(null == loc) loc = Locale.getDefault();
    return loc;
  }
    
}
