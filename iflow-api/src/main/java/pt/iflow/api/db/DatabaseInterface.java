/*
 *
 * Created on May 12, 2005 by iKnow
 *
 */

package pt.iflow.api.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;


/**
 * 
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright (c) 2005 iKnow</p>
 * 
 * @author iKnow
 */

public class DatabaseInterface {

  public static Connection getConnection(UserInfoInterface userInfo) throws SQLException {
    if (userInfo != null && userInfo.inTransaction()) {
      return userInfo.getTransactionConnection();
    }
    return Utils.getDataSource().getConnection();
  }

  public static void commitConnection(Connection conn) throws SQLException {
    if (conn == null)
      return;

    if (conn instanceof DBConnectionWrapper)
      return;

    conn.commit();
  }

  public static void rollbackConnection(Connection conn) throws SQLException {
    if (conn == null)
      return;

    if (conn instanceof DBConnectionWrapper)
      return;

    conn.rollback();
  }

  public static void closeResources(Object... resources) {

    if (resources == null)
      return;

    List<Connection> connections = new ArrayList<Connection>();
    List<Statement> statements = new ArrayList<Statement>();
    List<ResultSet> resultsets = new ArrayList<ResultSet>();

    for (Object resource : resources) {
      if (resource == null)
        continue;
      if (resource instanceof Connection) {
        connections.add((Connection)resource);
      }
      else if (resource instanceof Statement) {
        statements.add((Statement)resource);          
      } 
      else if (resource instanceof ResultSet) {
        resultsets.add((ResultSet)resource);          
      } 
    }

    for (ResultSet rs : resultsets) {
      closeResultSet(rs);
    }      
    resultsets = null;

    for (Statement st : statements) {
      closeStatement(st);
    }
    statements = null;

    for (Connection connection : connections) {
      closeConnection(connection);
    }
    connections = null;
  }

  private static void closeResultSet(ResultSet rs) {
    if (rs != null) {
      try { 
        rs.close(); 
      } 
      catch (Exception rse) {}
      rs = null;
    }      
  }
  private static void closeStatement(Statement st) {
    if (st != null) {
      try { 
        st.close(); 
      } 
      catch (Exception ste) {}
      st = null;
    }
  }
  private static void closeConnection(Connection db) {
    if (db != null) {
      try { 
        db.close(); 
      } 
      catch (Exception dbe) {
        try { db.rollback(); } catch (Exception dbe2) { }
        try { db.close(); } catch (Exception dbe2) { }
      }
      db = null;
    }
  }

  /**
   * 
   * TODO Add comment for method executeQuery on DatabaseInterface
   * 
   * @param aQuery
   * @param fieldNames
   * @return
   */
  public static Collection<Map<String,String>> executeQuery(String aQuery,String[] fieldNames) {

    Logger.debug("",null,"executeQuery",aQuery);
    ArrayList<Map<String,String>> result = new ArrayList<Map<String,String>>();

    ResultSet rs = null;
    Statement st = null;
    Connection con = null;
    try {
      con = Utils.getDataSource().getConnection();
      st = con.createStatement();
      rs = st.executeQuery(aQuery);

      while (rs.next()) {
        Hashtable<String,String> tmp = new Hashtable<String,String>();
        for (int i=0;i<fieldNames.length;i++) {
          tmp.put(fieldNames[i],rs.getString(fieldNames[i]));
        }
        result.add(tmp);
      }
    } catch (Exception e) {
      Logger.error(null,null,"executeQuery","Error excuting query " + aQuery + " : " + e.getMessage());
      e.printStackTrace();
    } finally {
      DatabaseInterface.closeResources(con,st,rs);
    }

    return result;
  }

  /**
   * 
   * TODO Add comment for method executeQuery on DatabaseInterface
   * 
   * @param aQuery
   * @return
   */
  public static Collection<Map<String,String>> executeQuery(String aQuery) {

    Logger.debug("",null,"executeQuery",aQuery);
    Collection<Map<String,String>> result = new ArrayList<Map<String,String>>();

    ResultSet rs = null;
    Statement st = null;
    Connection con = null;
    try {
      con = Utils.getDataSource().getConnection();
      st = con.createStatement();
      rs = st.executeQuery(aQuery);

      ResultSetMetaData rsmd = rs.getMetaData();
      int numberOfColumns = rsmd.getColumnCount();

      String[] fieldNames = new String[numberOfColumns];

      for (int i = 0; i < numberOfColumns; i++) {
        // given the query: select a as b from c
        // getColumnName() should return "a" (the column)
        // getColumnLabel() should return "b" (the alias/label)
        fieldNames[i] = rsmd.getColumnLabel(i+1);
      }

      while (rs.next()) {
        Map<String,String> tmp = new Hashtable<String,String>();
        for (int i=0;i<fieldNames.length;i++) {
          
          String tmpVal = rs.getString(fieldNames[i]);
          if(tmpVal != null) {
            tmp.put(fieldNames[i], tmpVal);
          }
        }
        result.add(tmp);
      }
    } catch (Exception e) {
      Logger.error(null,null,"executeQuery","Error excuting query " + aQuery + " : " + e.getMessage());
      e.printStackTrace();
    } finally {
      DatabaseInterface.closeResources(con,st,rs);
    }
    return result;
  }

  /**
   * 
   * TODO Add comment for method executeQuery on DatabaseInterface
   * 
   * @param aQuery
   * @return
   */
  public static Map<String, Map<String,String>> executeQuery(String aQuery, String keyName) {

    Logger.debug("",null,"executeQuery",aQuery);
    Map<String, Map<String,String>> result = new HashMap<String, Map<String,String>>();

    ResultSet rs = null;
    Statement st = null;
    Connection con = null;
    try {
      con = Utils.getDataSource().getConnection();
      st = con.createStatement();
      rs = st.executeQuery(aQuery);

      ResultSetMetaData rsmd = rs.getMetaData();
      int numberOfColumns = rsmd.getColumnCount();

      String[] fieldNames = new String[numberOfColumns];

      for (int i = 0; i < numberOfColumns; i++) {
        // given the query: select a as b from c
        // getColumnName() should return "a" (the column)
        // getColumnLabel() should return "b" (the alias/label)
        fieldNames[i] = rsmd.getColumnLabel(i+1);
      }

      while (rs.next()) {
        String key = rs.getString(keyName);
        Map<String,String> tmp = new Hashtable<String,String>();
        for (int i=0;i<fieldNames.length;i++) {
          String tmpVal = rs.getString(fieldNames[i]);
          if(tmpVal != null) {
            tmp.put(fieldNames[i],tmpVal);
          }
        }
        result.put(key, tmp);
      }
    } catch (Exception e) {
      Logger.error(null,null,"executeQuery","Error excuting query " + aQuery + " : " + e.getMessage());
      e.printStackTrace();
    } finally {
      DatabaseInterface.closeResources(con,st,rs);
    }
    return result;
  }


  public static void executeUpdates(String... queries) {
    Connection db = null;
    try {
      db = Utils.getDataSource().getConnection();
      db.setAutoCommit(false);
      for (String query : queries) {
        executeUpdate(query, db);
      }
      db.commit();
    } catch (Exception e) {
      Logger.error(null,null,"executeUpdate",
          "Error excuting updates, transaction will rollback: " + e.getMessage());
      try {
        db.rollback();
      } catch (Exception e1) {}
    } finally {
      DatabaseInterface.closeResources(db);
    }
  }

  private static int executeUpdate(String aQuery, Connection db) throws Exception {
    int result = -1;
    Statement st = null;
    try {
      st = db.createStatement();
      result = st.executeUpdate(aQuery);
    } catch (Exception e) {
      throw e;
    } finally {
      DatabaseInterface.closeResources(st);
    }
    return result;
  }
  
  public static int executeUpdate(String aQuery) {
    int result = -1;
    Connection db = null;
    try {
      db = Utils.getDataSource().getConnection();
      result = executeUpdate(aQuery, db);
    } catch (Exception e) {
      Logger.error(null,null,"executeUpdate","Error excuting update " + aQuery + " : " + e.getMessage());
      e.printStackTrace();
    } finally {
      DatabaseInterface.closeResources(db);
    }
    return result;
  }

}
