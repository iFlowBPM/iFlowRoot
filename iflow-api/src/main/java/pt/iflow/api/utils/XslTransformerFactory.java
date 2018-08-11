package pt.iflow.api.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Hashtable;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.RepositoryFile;
import pt.iflow.api.core.ResourceModifiedListener;

public class XslTransformerFactory {

  private static Hashtable<String, CacheElem> templateCache = new Hashtable<String, CacheElem>();

  private static final String DEFAULT_XSL = "default.xsl";
  
  private static String getCacheKey(UserInfoInterface userInfo, String sXsl) {
    return userInfo.getOrganization()+"*"+sXsl;
  }
  
  public static Transformer getDefaultTransformer(UserInfoInterface userInfo) {
    return getTransformer(userInfo, DEFAULT_XSL);
  }

  public static Templates getDefaultTemplates(UserInfoInterface userInfo) {
    return getTemplates(userInfo, DEFAULT_XSL);
  }

  public synchronized static Templates getTemplates(UserInfoInterface userInfo, String sXsl) {
    Logger.debug("ADMIN", "XslTransformerFactory", "getTemplates", "Templates for: "+sXsl);

    if (StringUtils.isEmpty(sXsl))
      return null;
    
    // Must build a cache key based on the xsl file name
    String cacheKey = getCacheKey(userInfo, sXsl);
    
    CacheElem cacheElem = null;
    cacheElem = (CacheElem) templateCache.get(cacheKey);

    // Cache new transformer if does not exist in cache
    RepositoryFile repFile = BeanFactory.getRepBean().getStyleSheet(userInfo, sXsl);
    boolean notInCache = false;
    notInCache = ((null == cacheElem) || (repFile.getLastModified() != cacheElem.getTimestamp()));
    
    
    if (notInCache) {
      Templates templates = null;
      Logger.debug("ADMIN", "XslTransformerFactory", "getTemplates", "Nao esta na cache. Processing...");
      
      try (InputStream isXslStream = repFile.getResourceAsStream()) {
	      if (isXslStream != null) {
	        TransformerFactory tFactory = TransformerFactory.newInstance();
	        tFactory.setURIResolver(new RepositoryURIResovler(userInfo, tFactory.getURIResolver()));
	        try {
	          templates = tFactory.newTemplates(new StreamSource(isXslStream));
	          cacheElem = new CacheElem(repFile.getLastModified(), templates);
	          templateCache.put(cacheKey, cacheElem);
	        } catch (TransformerConfigurationException e) {
	          Logger.error(userInfo.getUtilizador(), "XslTransformerFactory", "getTemplates", "Error building transformer template", e);
	          templateCache.remove(cacheKey);
	          cacheElem = null;
	        } finally {
	          try {
	            isXslStream.close();
	          } catch (IOException e) {
	            Logger.warning(userInfo.getUtilizador(), "XslTransformerFactory", "getTemplates", "Exception caught closing stream", e);
	          }
	        }
	      }
      }
      catch (Exception e) {}
    }

    Templates result = null;
    if (cacheElem != null) {
      result = cacheElem.tpl;
    }

    return result;
  }

  public synchronized static Transformer getTransformer(UserInfoInterface userInfo, String sXsl) {
    Logger.debug("ADMIN", "XslTransformerFactory", "getTransformer", "Transformer for: "+sXsl);

    if (StringUtils.isEmpty(sXsl))
      return null;
    
    Templates templates = getTemplates(userInfo, sXsl);
    
    Transformer result = null;
    if (templates != null) {
      try {
        result = templates.newTransformer();
      } catch (TransformerConfigurationException e) {
        e.printStackTrace();
      }
    }

    return result;
  }

  public static synchronized void clearCache() {
    templateCache.clear();
  }
  
  public static synchronized void clearCacheElement(UserInfoInterface userInfo, String name) {
    Logger.debug("ADMIN", "XslTransformerFactory", "clearCacheElement", "Removing cache element for: "+name);
    templateCache.remove(getCacheKey(userInfo,name));
  }
  
  private static final class CacheElem {
    final private long ts;
    final private Templates tpl;

    public CacheElem(long ts, Templates tpl) {
      this.ts = ts;
      this.tpl = tpl;
    }

    public long getTimestamp() {
      return ts;
    }

    public Templates getTemplates() {
      return tpl;
    }
  }

  
  private static ResourceModifiedListener modificationEvent = null;
 
  public static ResourceModifiedListener getResourceModifiedListener() {
    if(null == modificationEvent) {
      modificationEvent = new ResourceModifiedListener() {
        public void resourceModified(UserInfoInterface userInfo, String resource, String fullname) {
          clearCacheElement(userInfo,resource);
        }
      };
    }
    
    return modificationEvent;
  }
  
  public static Transformer getIdentityTransformer() {
    try {
      return TransformerFactory.newInstance().newTransformer();
    } catch (Throwable e) {
      Logger.warning(null, "XslTransformerFactory", "getIdentityTransformer", "Error retrieving identity transformer", e);
    }
    return null;
  }

  /**
   * Quick and dirty URIResolver for XSL templates 
   * @author ombl
   *
   */
  private static class RepositoryURIResovler implements URIResolver {
    private UserInfoInterface userInfo;
    private URIResolver parent;
    
    
    public RepositoryURIResovler(UserInfoInterface userInfo, URIResolver parent) {
      this.userInfo = userInfo;
      this.parent = parent;
    }


    public Source resolve(String href, String base) throws TransformerException {
      Source result = null;
      InputStream inputStream = null;
      try {
        URI uri = new URI(href);
        String path = uri.getPath();
        RepositoryFile repFile = BeanFactory.getRepBean().getStyleSheet(userInfo, path);
        inputStream = repFile.getResourceAsStream();
        if(null != repFile && repFile.exists())
          result = new StreamSource(inputStream);
      } catch (URISyntaxException e) {
      }
      finally {
    	  try {
    	  if( inputStream != null ) inputStream.close();
			} catch (IOException e) {}
	}
      
      if(null != parent)
        result = parent.resolve(href, base);
      
      return result;
    }
    
  }
}
