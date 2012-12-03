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
