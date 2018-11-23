package pt.iflow.api.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
import javax.sql.DataSource;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;

public class DatabaseInterface
{
  public static Connection getConnection(UserInfoInterface userInfo)
    throws SQLException
  {
    if ((userInfo != null) && (userInfo.inTransaction())) {
      return userInfo.getTransactionConnection();
    }
    return Utils.getDataSource().getConnection();
  }
  
  public static void commitConnection(Connection conn)
    throws SQLException
  {
    if (conn == null) {
      return;
    }
    if ((conn instanceof DBConnectionWrapper)) {
      return;
    }
    conn.commit();
  }
  
  public static void rollbackConnection(Connection conn)
    throws SQLException
  {
    if (conn == null) {
      return;
    }
    if ((conn instanceof DBConnectionWrapper)) {
      return;
    }
    conn.rollback();
  }
  
  public static void closeResources(Object... resources)
  {
    if (resources == null) {
      return;
    }
    List<Connection> connections = new ArrayList();
    List<Statement> statements = new ArrayList();
    List<ResultSet> resultsets = new ArrayList();
    
    Object[] arrayOfObject = resources;int j = resources.length;
    Object resource;
    for (int i = 0; i < j; i++)
    {
      resource = arrayOfObject[i];
      if (resource != null) {
        if ((resource instanceof Connection)) {
          connections.add((Connection)resource);
        } else if ((resource instanceof Statement)) {
          statements.add((Statement)resource);
        } else if ((resource instanceof ResultSet)) {
          resultsets.add((ResultSet)resource);
        }
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
  
  private static void closeResultSet(ResultSet rs)
  {
    if (rs != null)
    {
      try
      {
        rs.close();
      }
      catch (Exception localException) {}
      rs = null;
    }
  }
  
  private static void closeStatement(Statement st)
  {
    if (st != null)
    {
      try
      {
        st.close();
      }
      catch (Exception localException) {}
      st = null;
    }
  }
  
  private static void closeConnection(Connection db)
  {
    if (db != null)
    {
      try
      {
        db.close();
      }
      catch (Exception dbe)
      {
        try
        {
          db.rollback();
        }
        catch (Exception localException1) {}
        try
        {
          db.close();
        }
        catch (Exception localException2) {}
      }
      db = null;
    }
  }
  
  public static Collection<Map<String, String>> executeQuery(String aQuery, String[] fieldNames)
  {
    Logger.debug("", null, "executeQuery", aQuery);
    ArrayList<Map<String, String>> result = new ArrayList();
    
    ResultSet rs = null;
    PreparedStatement st = null;
    
    Connection con = null;
    try
    {
      con = Utils.getDataSource().getConnection();
      st = con.prepareStatement(aQuery);
      rs = st.executeQuery();
      while (rs.next())
      {
        Hashtable<String, String> tmp = new Hashtable();
        for (int i = 0; i < fieldNames.length; i++) {
          tmp.put(fieldNames[i], rs.getString(fieldNames[i]));
        }
        result.add(tmp);
      }
    }
    catch (Exception e)
    {
      Logger.error(null, null, "executeQuery", "Error excuting query " + aQuery + " : " + e.getMessage());
      e.printStackTrace();
    }
    finally
    {
      closeResources(new Object[] { con, st, rs });
    }
    return result;
  }
  
  public static Collection<Map<String, String>> executeQuery(String aQuery)
  {
    Logger.debug("", null, "executeQuery", aQuery);
    Collection<Map<String, String>> result = new ArrayList();
    


    String parameterizedQuery = "select a.* from (" + aQuery + ") a where 1=?";
    
    ResultSet rs = null;
    PreparedStatement st = null;
    Connection con = null;
    try
    {
      con = Utils.getDataSource().getConnection();
      st = con.prepareStatement(parameterizedQuery);
      st.setInt(1, 1);
      rs = st.executeQuery();
      
      ResultSetMetaData rsmd = rs.getMetaData();
      int numberOfColumns = rsmd.getColumnCount();
      
      String[] fieldNames = new String[numberOfColumns];
      for (int i = 0; i < numberOfColumns; i++) {
        fieldNames[i] = rsmd.getColumnLabel(i + 1);
      }
      while (rs.next())
      {
        Map<String, String> tmp = new Hashtable();
        for (int i = 0; i < fieldNames.length; i++)
        {
          String tmpVal = rs.getString(fieldNames[i]);
          if (tmpVal != null) {
            tmp.put(fieldNames[i], tmpVal);
          }
        }
        result.add(tmp);
      }
    }
    catch (Exception e)
    {
      Logger.error(null, null, "executeQuery", "Error excuting query " + aQuery + " : " + e.getMessage());
      e.printStackTrace();
    }
    finally
    {
      closeResources(new Object[] { con, st, rs });
    }
    return result;
  }
  
  public static Map<String, Map<String, String>> executeQuery(String aQuery, String keyName)
  {
    Logger.debug("", null, "executeQuery", aQuery);
    Map<String, Map<String, String>> result = new HashMap();
    
    ResultSet rs = null;
    PreparedStatement st = null;
    Connection con = null;
    try
    {
      con = Utils.getDataSource().getConnection();
      st = con.prepareStatement(aQuery);
      rs = st.executeQuery();
      
      ResultSetMetaData rsmd = rs.getMetaData();
      int numberOfColumns = rsmd.getColumnCount();
      
      String[] fieldNames = new String[numberOfColumns];
      for (int i = 0; i < numberOfColumns; i++) {
        fieldNames[i] = rsmd.getColumnLabel(i + 1);
      }
      while (rs.next())
      {
        String key = rs.getString(keyName);
        Map<String, String> tmp = new Hashtable();
        for (int i = 0; i < fieldNames.length; i++)
        {
          String tmpVal = rs.getString(fieldNames[i]);
          if (tmpVal != null) {
            tmp.put(fieldNames[i], tmpVal);
          }
        }
        result.put(key, tmp);
      }
    }
    catch (Exception e)
    {
      Logger.error(null, null, "executeQuery", "Error excuting query " + aQuery + " : " + e.getMessage());
      e.printStackTrace();
    }
    finally
    {
      closeResources(new Object[] { con, st, rs });
    }
    return result;
  }
  
  public static void executeUpdates(String... queries)
  {
    Connection db = null;
    try
    {
      db = Utils.getDataSource().getConnection();
      db.setAutoCommit(false);
      String[] arrayOfString = queries;int j = queries.length;
      for (int i = 0; i < j; i++)
      {
        String query = arrayOfString[i];
        executeUpdate(query, db);
      }
      db.commit();
    }
    catch (Exception e)
    {
      Logger.error(null, null, "executeUpdate", 
        "Error excuting updates, transaction will rollback: " + e.getMessage());
      try
      {
        if (db!=null) db.rollback();
      }
      catch (Exception localException1) {}
    }
    finally
    {
      closeResources(new Object[] { db });
    }
  }
  
  private static int executeUpdate(String aQuery, Connection db)
    throws Exception
  {
    int result = -1;
    Statement st = null;
    try
    {
      st = db.createStatement();
      result = st.executeUpdate(aQuery);
    }
    catch (Exception e)
    {
      throw e;
    }
    finally
    {
      // closeResources(new Object[] { st });
    	try {if (st != null) st.close(); } catch (SQLException e) {}
    }
    return result;
  }
  
  public static int executeUpdate(String aQuery)
  {
    int result = -1;
    Connection db = null;
    try
    {
      db = Utils.getDataSource().getConnection();
      result = executeUpdate(aQuery, db);
    }
    catch (Exception e)
    {
      Logger.error(null, null, "executeUpdate", "Error excuting update " + aQuery + " : " + e.getMessage());
      e.printStackTrace();
    }
    finally
    {
      //closeResources(new Object[] { db });
      try {if (db != null) db.close(); } catch (SQLException e) {}
    }
    return result;
  }
  
  public static void safeClose(Statement stmt) {
	  if (stmt != null) {
		  try {
			  stmt.close();
		  } catch (SQLException e) {

		  }
	  }
  }
  public static void safeClose(Connection db) {
	  if (db != null) {
		  try {
			  db.close();
		  } catch (SQLException e) {

		  }
	  }
  }
  public static void safeClose(ResultSet rs) {
	  if (rs != null) {
		  try {
			  rs.close();
		  } catch (SQLException e) {

		  }
	  }
  }
}
