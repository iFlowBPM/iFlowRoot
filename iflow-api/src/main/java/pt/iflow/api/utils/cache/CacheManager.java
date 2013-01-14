package pt.iflow.api.utils.cache;

import java.util.HashMap;

public class CacheManager {

  public static final long MINUTES = 1000*60;
  public static final long HOURS = 60*MINUTES;

  private static final int DEFAULT_MAX_ELEMENTS = 50;
  private static final long DEFAULT_ELEMENT_TIMEOUT = 1*HOURS;
  
  
  // TODO thread de limpeza de elementos nao acedidos ha mais de CACHE ELEMENT TIMEOUT
  

  private static CacheManager instance = null;
  
  private HashMap<String, Cache> cacheList = new HashMap<String, Cache>();  
  
  private CacheManager() {    
  }
  
  public static CacheManager getInstance() {
    if (instance == null)
      instance = new CacheManager();
    
    return instance;
  }
  
  public void register(String cacheId) throws CacheException {
    register(cacheId, DEFAULT_MAX_ELEMENTS, DEFAULT_ELEMENT_TIMEOUT);
  }
  
  public void register(String cacheId, int cacheMaxElements, long cacheElementTimeout) throws CacheException {
    synchronized (cacheList) {
      if (cacheList.containsKey(cacheId))
        throw new CacheException("Cache for " + cacheId + " already registered");
      
      cacheList.put(cacheId, new CacheImpl(cacheMaxElements, cacheElementTimeout));      
    }
  }
  
  public Cache getCache(String cacheId) throws CacheException {
    if (!cacheList.containsKey(cacheId)) {
      register(cacheId);
    }
    return cacheList.get(cacheId);
  }
}
