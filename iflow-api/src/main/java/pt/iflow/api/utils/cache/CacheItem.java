package pt.iflow.api.utils.cache;


public abstract class CacheItem implements Comparable<CacheItem> {

  protected int key;  
  protected long lastAccessed;  

  protected CacheItem(int key) {
    this.key = key;    
  }

  public int getKey() {
    return key;
  }

  public long getLastAccessed() {
    return lastAccessed;
  }
  
  public int compareTo(CacheItem o) {
    if (lastAccessed == o.lastAccessed)
      return 0;
    
    if (lastAccessed < o.lastAccessed)
      return -1;
        
    return 1;
  }
}
