package pt.iflow.api.utils.cache;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

public class CacheImpl implements Cache {

  private Map<Integer, CacheItem> cache = new HashMap<Integer, CacheItem>();
  private TreeSet<CacheItem> rank = new TreeSet<CacheItem>();

  private int cacheMaxElements;
  private long cacheElementTimeout;

  public CacheImpl(int cacheMaxElements, long cacheElementTimeout) {
    this.cacheMaxElements = cacheMaxElements;
    this.cacheElementTimeout = cacheElementTimeout;
  }

  public CacheItem get(int id) {

    CacheItem ret = cache.get(id);

    if (ret != null) {
      long now = new Date().getTime();
      if (now - ret.lastAccessed > cacheElementTimeout){
        // element has expired
        remove(id);
        ret = null;
      }
      else {
        ret.lastAccessed = now;
      }
    }

    return ret;
  }

  public void add(CacheItem item) {
    synchronized (cache) {
      item.lastAccessed =  new Date().getTime();

      cache.put(item.getKey(), item);
      rank.add(item);

      ensureSize();
    }
  }

  public void remove(int id) {
    synchronized (cache) {
      CacheItem item = cache.remove(id);
      if (item != null) {
        rank.remove(item);
      }
    }
  }

  private void ensureSize() {
    if (cache.size() > cacheMaxElements) {
      CacheItem item = rank.last();
      remove(item.getKey());
    }
  }

}
