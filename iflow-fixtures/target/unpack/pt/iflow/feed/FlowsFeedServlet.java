/*
 *
 * Created on May 23, 2005 by iKnow
 *
 */

package pt.iflow.feed;

import java.io.IOException;
import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.feed.FeedGenerator;
import pt.iflow.api.presentation.FlowApplications;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.ServletUtils;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;
import pt.iflow.info.DefaultInfoGenerator;

import com.infosistema.crypto.Base64;

/**
 * 
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright (c) 2005 iKnow
 * </p>
 * 
 * @author iKnow
 * 
 * @web.servlet name="FeedServlet"
 * 
 * @web.servlet-mapping url-pattern="/FeedServlet"
 */

public class FlowsFeedServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;

  private static FeedGenerator _feedGenerator;
  private static DefaultInfoGenerator _infoGenerator;

  static {
    // TODO the generator implementation should come from config fiel
    try {
      _feedGenerator = (FeedGenerator) Class.forName(Const.FEED_GEN_IMPLEMENTATION).newInstance();
      _infoGenerator = new DefaultInfoGenerator();
    } catch (Exception e) {
      Logger.error(null, null, "initialization", "Unable to instatiate feed generator " + Const.FEED_GEN_IMPLEMENTATION + "  : "
          + e.getMessage());
      _feedGenerator = null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String baseURL = Const.FEED_URL;
    if (_feedGenerator == null) {
      return;
    }
    String feed = request.getParameter("feed");
    String category = request.getParameter("category");
    int nCategory = FlowApplications.ORPHAN_GROUP_ID;
    try {
      nCategory = Integer.parseInt(category);
    } catch (Exception e) {
    }
    UserInfoInterface userInfo = (UserInfoInterface) request.getSession().getAttribute(Const.USER_INFO);
    if (userInfo != null && userInfo.isLogged()) {
      if (feed == null || feed.trim().equalsIgnoreCase("")) {
        _feedGenerator.generateEmptyFeed(baseURL, response.getWriter());
        return;
      } else if (feed.equals("flows")) {
        _feedGenerator.generateFlowsFeed(userInfo, _infoGenerator.generateCategoriesInfo(userInfo, nCategory), baseURL
            + Const.APP_URL_PREFIX, response.getWriter());
        return;
      } else if (feed.equals("tasks")) {
        _feedGenerator.generateTasksFeed(userInfo, _infoGenerator.generateCategoriesInfoToUpdate(userInfo, nCategory), _infoGenerator
            .generateTaskInfo(userInfo), baseURL + Const.APP_URL_PREFIX, response.getWriter());
        return;
      } else if (feed.equals("tasksoverview")) {
        _feedGenerator.generateTasksFeed(userInfo, _infoGenerator.generateCategoriesInfoToUpdate(userInfo, nCategory), _infoGenerator.generateTaskInfo(userInfo), baseURL + Const.APP_URL_PREFIX, response.getWriter(), true);
        return;
      } else if (feed.equals("tasksAtom")) {
          _feedGenerator.generateTasksFeedAtom(userInfo, _infoGenerator.generateCategoriesInfoToUpdate(userInfo, nCategory), _infoGenerator
              .generateTaskInfo(userInfo), baseURL + Const.APP_URL_PREFIX, response.getWriter());
          return;
      } else if (feed.equals("messages")) {
        _feedGenerator.generateMessagesFeed(userInfo, _infoGenerator.generateMessagesInfo(userInfo),
            baseURL + Const.APP_URL_PREFIX, response.getWriter());
        return;
      } else if (feed.equals("categories")) {
        _feedGenerator.generateCategoriesFeed(userInfo, _infoGenerator.generateCategoriesInfo(userInfo, nCategory), baseURL
            + Const.APP_URL_PREFIX, response.getWriter());
        return;
      } else {
        _feedGenerator.generateEmptyFeed(baseURL, response.getWriter());
        return;
      }
    } else if (feed.equals("tasksAtom")){   	
        UserInfoInterface ui = null;
        ui = BeanFactory.getUserInfoFactory().newUserInfo(); 
        Hashtable<String,String> cookies = ServletUtils.getCookies(request);
        if (cookies != null) {
        	ui.setCookieLang(cookies.get(Const.LANG_COOKIE));
        }
        
        String auth = (String) request.getParameter("auth");
        if(auth==null)return;
        String decodedCredentials = new String(Utils.decrypt(auth));        
        String[] cred = decodedCredentials.split(":");
        ui.login(cred[0],cred[1]);
        
        _feedGenerator.generateTasksFeedAtom(ui, _infoGenerator.generateCategoriesInfoToUpdate(ui, nCategory), _infoGenerator
                .generateTaskInfo(ui), baseURL + Const.APP_URL_PREFIX, response.getWriter());
            return;
    }else{
      _feedGenerator.generateEmptyFeed(baseURL, response.getWriter());
      return;
    }
  }
}
