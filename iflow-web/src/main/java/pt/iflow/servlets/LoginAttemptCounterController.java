package pt.iflow.servlets;

import org.apache.commons.lang.StringUtils;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Setup;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;

public class LoginAttemptCounterController {

    static final String LOGIN_ATTEMPT_COUNTER_MAP_NAME = "LOGIN_ATTEMPT_COUNTER_MAP_NAME";

    @SuppressWarnings("unchecked")
    public static void markFailedAttempt(ServletContext sc, ServletRequest req) throws UnknownHostException {
        HashMap<InetAddress, LoginAttemptCounter> map = (HashMap<InetAddress, LoginAttemptCounter>) sc.getAttribute(LOGIN_ATTEMPT_COUNTER_MAP_NAME);
        if (map == null || !(map instanceof HashMap<?, ?>))
            map = new HashMap<InetAddress, LoginAttemptCounter>();

        LoginAttemptCounter lc = map.get(InetAddress.getByName(req.getLocalAddr()));
        if (lc == null)
            lc = new LoginAttemptCounter();

        lc.setAddressAttempt(InetAddress.getByName(req.getLocalAddr()));
        lc.setFailedAttempt(lc.getFailedAttempt() + 1);
        lc.setLastFailedAttempt(new Date());

        map.put(lc.getAddressAttempt(), lc);
        sc.setAttribute(LOGIN_ATTEMPT_COUNTER_MAP_NAME, map);
    }

    public static Boolean isOverFailureLimit(ServletContext sc, ServletRequest req, String username) throws UnknownHostException {
        HashMap<InetAddress, LoginAttemptCounter> map = (HashMap<InetAddress, LoginAttemptCounter>) sc.getAttribute(LOGIN_ATTEMPT_COUNTER_MAP_NAME);
        if (map == null || !(map instanceof HashMap<?, ?>))
            return false;

        LoginAttemptCounter lc = map.get(InetAddress.getByName(req.getLocalAddr()));
        if (lc == null)
            return false;

        //excedeed attempts but reset time has come
        /*if (lc.getFailedAttempt() > Setup.getPropertyInt(Const.MAX_LOGIN_ATTEMPTS)
                && lc.getLastFailedAttempt().getTime() < ((new Date()).getTime() - Setup.getPropertyInt(Const.MAX_LOGIN_ATTEMPTS_WAIT))) {
            lc.setFailedAttempt(0);
            map.put(lc.getAddressAttempt(), lc);
            sc.setAttribute(LOGIN_ATTEMPT_COUNTER_MAP_NAME, map);

            return false;
        }*/

        //excedeed attempts and yet too soon
        /*if (lc.getFailedAttempt() > Setup.getPropertyInt(Const.MAX_LOGIN_ATTEMPTS)
                && lc.getLastFailedAttempt().getTime() > ((new Date()).getTime() - Setup.getPropertyInt(Const.MAX_LOGIN_ATTEMPTS_WAIT))) {
            if ( Const.BLOCK_USER_ON_FAILED_ATTEMPTS&& StringUtils.isNotEmpty(username)) {
                BeanFactory.getUserManagerBean().blockUser(username);
                lc.setFailedAttempt(0);
                map.put(lc.getAddressAttempt(), lc);
                sc.setAttribute(LOGIN_ATTEMPT_COUNTER_MAP_NAME, map);
            }
            return true;
        }*/
        
        //excedeed attempts -> blocks user without wait time
        if (lc.getFailedAttempt() > Setup.getPropertyInt(Const.MAX_LOGIN_ATTEMPTS)) {
        	if ( Const.BLOCK_USER_ON_FAILED_ATTEMPTS && StringUtils.isNotEmpty(username)) {
                BeanFactory.getUserManagerBean().blockUser(username);
                lc.setFailedAttempt(0);
                map.put(lc.getAddressAttempt(), lc);
                sc.setAttribute(LOGIN_ATTEMPT_COUNTER_MAP_NAME, map);
            }
        }

        return false;
    }
    
    public static Boolean isBlocked(HttpServletRequest request) {
    	return BeanFactory.getUserManagerBean().isUserBlocked(request.getParameter("login"));
    }
    
    public static Boolean isBlocked(String username) {
    	return BeanFactory.getUserManagerBean().isUserBlocked(username);
    }
}
