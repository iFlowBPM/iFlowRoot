package pt.iflow.blocks.form;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.FormProps;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.ServletUtils;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;
import pt.iflow.api.utils.cache.CacheException;
import pt.iflow.api.utils.cache.CacheItem;
import pt.iflow.api.utils.cache.CacheManager;


public class SQLSelection extends Selection {

  private static final String CACHE_ID = SQLSelection.class.getName();
  private static boolean cacheAvailable = true;
  
  static {
    try {
      CacheManager.getInstance().register(CACHE_ID, 100, 2*CacheManager.HOURS);
    }
    catch (CacheException e) {
      Logger.error("admin", "SQLSelection", "static", "unable to register cache", e);
      cacheAvailable = false;
    }
  }
  
  public String getDescription() {
    return "Lista de Selecção SQL";
  }
  
  
  public void setup(UserInfoInterface userInfo, ProcessData procData, Properties props, ServletUtils response) {
    
    String login = userInfo.getUtilizador();
    
    List<String> values = new ArrayList<String>();
    List<String> texts = new ArrayList<String>();

    // process query
    String query = props.getProperty(FormProps.sQUERY);
    if (StringUtils.isNotEmpty(query)) {

      String dsName = props.getProperty(FormProps.sDATASOURCE);

      DataSource ds = null;
      Connection db = null;
      Statement st = null;
      ResultSet rs = null;

      try {
        if (StringUtils.isNotEmpty(dsName)) {
          dsName = procData.transform(userInfo, dsName, true);
        }
        if (StringUtils.isEmpty(dsName)) {
          dsName = null;
        }

        String transfQuery = procData.transform(userInfo, query, true);

        if (StringUtils.isEmpty(transfQuery)) {
          // undo transformation
          transfQuery = query;
        }

        boolean useCache = StringUtils.equals(props.getProperty(FormProps.CACHE_HINT), "true");
        Logger.debug(userInfo.getUtilizador(), this, "setup", "USE CACHE: " + useCache + " (cache available?" + cacheAvailable + ")");
        useCache = cacheAvailable && useCache;
        
        CacheItem cacheItem = null;
        int cachekey = SQLSelection.genKey(dsName, transfQuery);
        if (!useCache || (cacheItem = CacheManager.getInstance().getCache(CACHE_ID).get(cachekey)) == null) { 
          ds = Utils.getUserDataSource(dsName);
          db = ds.getConnection();
          st = db.createStatement();

          Logger.debug(login,this,"setup",
              procData.getSignature() +
              "importing SQL Select attributes for query "
              + transfQuery);

          rs = st.executeQuery(transfQuery);


          while (rs != null && rs.next()) {
            String value = rs.getString(1);
            String text = rs.getString(2);

            value = value != null ? value : "";
            text = text != null ? text : "";

            values.add(value);
            texts.add(text);
          }
          
          if (useCache) {
            CacheItem ci = new SqlCacheItem(cachekey, values, texts);
            CacheManager.getInstance().getCache(CACHE_ID).add(ci);
            Logger.debug(userInfo.getUtilizador(), this, "setup", "DB DATA CACHED");
          }
          
        }
        else {          
          values = ((SqlCacheItem)cacheItem).options;
          texts = ((SqlCacheItem)cacheItem).texts;
          Logger.debug(userInfo.getUtilizador(), this, "setup", "CACHED DATA FETCHED " + cacheItem.getLastAccessed());
        }

      }
      catch (Exception ei) {
        Logger.error(login,this,"setup",
            procData.getSignature() +
            "importing SQL Select attributes: "
            + ei.getMessage(), ei);
      }
      finally {
        DatabaseInterface.closeResources(db,st,rs);
      }
    }

    int sizeVals = values.size();
    for(int i = 0; i < sizeVals; i++) {
      props.setProperty(i+"_value", values.get(i));
      props.setProperty(i+"_text", texts.get(i));
    }

    super.setup(userInfo, procData, props, response);
  }

  private static int genKey(String datasource, String query) {
    return (datasource+query).hashCode();
  }
}


class SqlCacheItem extends CacheItem {
  List<String> options;
  List<String> texts;
  
  public SqlCacheItem(int key) {
    this(key, null, null);
  }
  
  public SqlCacheItem(int key, List<String> options, List<String> texts) {
    super(key);
    this.options = options;
    this.texts = texts;
  }
}