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

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.Repository;
import pt.iflow.api.core.RepositoryFile;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.UserInfoInterface;

/**
 * 
 * Simple tag to present a contextual help box<br>
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
 * @author jcosta
 * 
 */
public class HelpBoxTag extends IknowTag {
   /**
   * 
   */
  private static final long serialVersionUID = -8188610925961831893L;
  
  private String context;

   public void setContext(String context) {
      this.context = context;
   }

   public String getContext() {
     return context;
   }

   private String formatFileName (String context) {
     //return "box_" + context + ".vm";
     return "box_" + context;
   }
   
   public int doEndTag() throws JspException {

     UserInfoInterface userInfo = (UserInfoInterface)pageContext.getSession().getAttribute(Const.USER_INFO);
//     ServletResponse response = pageContext.getResponse();

     StringBuffer output = new StringBuffer();
     if (userInfo != null) {

       try {
         String images = "Themes/";
         images += BeanFactory.getOrganizationThemeBean().getOrganizationTheme(userInfo).getThemeName();
         images += "/images/";
         Repository rep = BeanFactory.getRepBean();
         RepositoryFile contentFile = rep.getHelp(userInfo, formatFileName(getContext()));
         if (contentFile != null) {

//           :::::::to use help mode uncomment this block
//           output.append("<div id=\"context_help_on\" class=\"help_box_open\"><a href=\"javascript:toggleHelpMode();\">");
//           output.append("<img border=\"0\" id=\"helpdivisionopenimg\" src=\"" + images + "");
//
//           if (userInfo.getUserSettings().isHelpMode())
//             output.append("question_mark_qm.png");
//           else
//             output.append("question_mark_qm.png");
//             
//           output.append("\"/>");
//           output.append("</a></div>");
//           if (userInfo.getUserSettings().isHelpMode())
//             output.append("<div id=\"helpdivision\" class=\"help_box_division\">");
//           else
//             output.append("<div id=\"helpdivision\" class=\"help_box_division hidden\">");
//
//           output.append("<div id=\"helpwrapper\" class=\"help_box_wrapper\">");
//           output.append("<div id=\"helpsection\" class=\"help_box\">");
//           output.append("<h1 id=\"title_help\">");
//           output.append(getUserMessages().getString("helpbox.title"));           
//           output.append("</h1>");           
//
//           InputStream contentStream = contentFile.getResourceAsStream();
//           Hashtable<String,Object> htSubst = new Hashtable<String,Object>();
//           htSubst.put("messages", userInfo.getMessages());
//           htSubst.put("navSep", "&#10145;");
//           
//           String sHtml = VelocityUtils.processTemplate(htSubst, new InputStreamReader(contentStream, "UTF-8"));
//           output.append(sHtml);
//
//           output.append("</div>");
//           output.append("</div>");
//           output.append("</div>");
//           output.append("</div>");

         
           output.append("<div id=\"context_help_on\" class=\"help_box_open\">");
           output.append("<a href=\"javascript:showHelpDialogItem('");
           output.append(formatFileName(getContext()));
           output.append("');\">");
           output.append("<img id=\"helpdivisionopenimg\" border=\"0\" src=\"" + images + "question_mark_qm.png\"/>");
           output.append("</a>");
           output.append("</div>");

         }
       }
       catch (Exception e) {

       }


     }

     try {
       pageContext.getOut().write(output.toString());
     }
     catch (IOException e) {
       throw new JspException(e);
     }
     return EVAL_PAGE;
   }

}

