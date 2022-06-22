package pt.iflow.servlets;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Setup;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
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

    public static Boolean isOverFailureLimit(ServletContext sc, ServletRequest req) throws UnknownHostException {
        HashMap<InetAddress, LoginAttemptCounter> map = (HashMap<InetAddress, LoginAttemptCounter>) sc.getAttribute(LOGIN_ATTEMPT_COUNTER_MAP_NAME);
        if (map == null || !(map instanceof HashMap<?, ?>))
            return false;

        LoginAttemptCounter lc = map.get(InetAddress.getByName(req.getLocalAddr()));
        if (lc == null)
            return false;
        
        //excedeed attempts but reset time has come
        if (lc.getFailedAttempt() > Setup.getPropertyInt(Const.MAX_LOGIN_ATTEMPTS)
                && lc.getLastFailedAttempt().getTime() < ((new Date()).getTime() - Setup.getPropertyInt(Const.MAX_LOGIN_ATTEMPTS_WAIT))) {
            lc.setFailedAttempt(0);
            map.put(lc.getAddressAttempt(), lc);
            sc.setAttribute(LOGIN_ATTEMPT_COUNTER_MAP_NAME, map);

            return false;
        }

        //excedeed attempts and yet too soon
        if (lc.getFailedAttempt() > Setup.getPropertyInt(Const.MAX_LOGIN_ATTEMPTS)
                && lc.getLastFailedAttempt().getTime() > ((new Date()).getTime() - Setup.getPropertyInt(Const.MAX_LOGIN_ATTEMPTS_WAIT)))
            return true;

        return false;
    }
}
