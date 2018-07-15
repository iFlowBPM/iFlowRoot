package pt.iflow.api.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Properties;

import org.apache.commons.lang.StringEscapeUtils;

import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.Utils;

public class DBQueryManager {

  private static Properties _queries = new Properties();
  private static String _queriesFileLocation;
  private static long _lastModification = 0;

  static {
    //_queriesFileLocation = System.getProperty("iflow.home") + File.separator + "dbqueries";
	  _queriesFileLocation = Const.sIFLOW_HOME + File.separator + "dbqueries";
    String dbType = Const.DB_TYPE;
    _queriesFileLocation = _queriesFileLocation + File.separator + "queries_" + dbType + ".properties";
    File queriesFile = new File(_queriesFileLocation);
    _lastModification = queriesFile.lastModified();
    reloadQueries();
  }

  private static void reloadQueries() {
	  FileInputStream fileInputStream = null;  
    Logger.debug("", "DBQueryManager", "", "LOADING QUERIES FILE");
    try {
    	fileInputStream = new FileInputStream(_queriesFileLocation);
      _queries.load(fileInputStream);
      Logger.adminInfo("DBQueryManager", "reloadQueries", "queries reloaded");
    } catch (IOException e) {
      Logger.adminError("DBQueryManager", "reloadQueries", "Error reading queries.", e);
    }
    finally {
    	if( fileInputStream != null) Utils.safeClose(fileInputStream);
	}
  }
  

  private static void checkFile() {
    File queriesFile = new File(_queriesFileLocation);
    if(_lastModification < queriesFile.lastModified()) {
      _lastModification = queriesFile.lastModified();
      reloadQueries();
    }
  }

  public static String getQuery(String queryName){
    checkFile();
    Logger.adminDebug("DBQueryManager","getQuery", "RETURNING QUERY = ");
    return _queries.getProperty(queryName);
  }

  public static String processQuery(String queryName) {
    return processQuery(queryName,new Object[]{});
  }

  public static String processQuery(String queryName, Object ... arguments) {
    checkFile();
    String query = _queries.getProperty(queryName);
    if(query != null && query.trim().length() > 0) {
      query = MessageFormat.format(query, arguments);
    }
    if(Logger.isAdminDebugEnabled()) {
      Logger.adminDebug("DBQueryManager", "processQuery", "RETURNING QUERY = ");
    }
    return query;
  }
  
  public static String toQueryValue(Object obj) {
    if(obj == null) return "null";
    if(obj instanceof Number) {
      return obj.toString();
    }
    if(obj instanceof java.util.Date) {
      return processQuery("SQL.DATETIME", obj);
    }
    return "'"+StringEscapeUtils.escapeSql(obj.toString())+"'";
  }
  
}
