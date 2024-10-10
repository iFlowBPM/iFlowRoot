package pt.iflow.servlets;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.flows.Flow;
import pt.iflow.api.flows.FlowHolder;
import pt.iflow.api.notification.EmailManager;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;
import pt.iflow.api.utils.XslTransformerFactory;
import pt.iflow.flows.FlowData;
import pt.iknow.utils.html.FormData;
import pt.iknow.utils.html.FormUtils;

public class FormCache {

  private static CacheManager _manager;
  private static final String CACHE_NAME = "form";
  
  static InputStream fis = null;
  static {
    fis = FormCache.class.getResourceAsStream("ehcache.xml");
    try {
      CacheManager manager = new CacheManager(fis);
      _manager = manager;
    } catch(Exception e) {
	} finally {
		if( fis != null) Utils.safeClose(fis);
	}    
    // _manager.addCache(CACHE_NAME);
    
    _manager.getCache(CACHE_NAME);

  }
  
  public static FormData parseRequest(HttpServletRequest request) throws Exception {
    return FormUtils.parseRequest(request, Const.nUPLOAD_THRESHOLD_SIZE, Const.nUPLOAD_MAX_SIZE,Const.fUPLOAD_TEMP_DIR);
  }
  
  public static String put(FormData fd) {
    Cache cache = _manager.getCache(CACHE_NAME);
    String key = RandomStringUtils.random(20, true, true);
    cache.put(new Element(key, (Object)fd));
    return key;
  }
  
  
  public static FormData get(String key) {
    Cache cache = _manager.getCache(CACHE_NAME);
    FormData fd = (FormData) cache.get(key).getObjectValue();
    cache.remove(key);
    return fd;
  }
  
  public static String reloadCaches(UserInfoInterface userInfo) {
    StringBuffer sbError = new StringBuffer();
    
    // clear classloader
    BeanFactory.getRepBean().reloadClassLoader(userInfo);
    
    // clear stylesheets (XSL)
    XslTransformerFactory.clearCache();
    
    // clear email templates
    EmailManager.resetEmailCache();
    
    
    // Flows....
    Flow flow = BeanFactory.getFlowBean();
    FlowHolder flowHolder = BeanFactory.getFlowHolderBean();

    String [] files = flowHolder.listFlowNamesOffline(userInfo);
    for (int i=0; files != null && i < files.length; i++) {
      String flowName = files[i];
      String undeployResult = flow.undeployFlow(userInfo, flowName);
      if (StringUtils.isNotEmpty(undeployResult)) {
        sbError.append("<br>").append("Undeploy ").append(flowName).append(": ").append(undeployResult).append("<br>");
      }
    }

    FlowData.reloadClasses(userInfo);

    files = flowHolder.listFlowNamesOnline(userInfo);
    for (int i=0; i < files.length; i++) {
      String flowName = files[i];
      String deployResult = flow.deployFlow(userInfo, flowName);
      if (StringUtils.isNotEmpty(deployResult)) {
        sbError.append("<br>").append("Deploy ").append(flowName).append(": ").append(deployResult).append("<br>");
      }
    }


    return sbError.toString();
  }
}
