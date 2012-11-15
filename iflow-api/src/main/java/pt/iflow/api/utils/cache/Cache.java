package pt.iflow.api.utils.cache;

public interface Cache {

  public CacheItem get(int id);
  public void add(CacheItem item);
  public void remove(int id);
  
}
