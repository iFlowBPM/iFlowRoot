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
