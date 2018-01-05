/*
 *
 * Created on Jan 12, 2006 by mach
 *
 */

package pt.iflow.repository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.sql.DataSource;

import pt.iflow.api.core.RepositoryObject;
import pt.iflow.api.repository.RepositoryAccess;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;

public class RepositoryAccessOracleImpl implements RepositoryAccess {

  protected static final int CHUNK_SIZE = 8096;


  private String _DBUrl;

  private String _DBUser;

  private String _DBPass;

  private DataSource _data;

  private Connection _conn;

  public RepositoryAccessOracleImpl(Properties props) {
    String host = props.getProperty("REP_DB_HOST");
    String port = props.getProperty("REP_DB_PORT");
    String name = props.getProperty("REP_DB_NAME");

    this._DBUser = props.getProperty("REP_DB_USER");
    this._DBPass = props.getProperty("REP_DB_PASS");

    try {
      Class.forName("oracle.jdbc.OracleDriver");
    }
    catch (ClassNotFoundException e) {
      return;
    }

    this._DBUrl = "jdbc:oracle:thin:@" + host + ":" + port + ":" + name;
    this._data = null;
    try {
      this._conn = this.getConnection();
    }
    catch (SQLException e) {
      e.printStackTrace();
      return;
    }
  }

  public RepositoryAccessOracleImpl(DataSource ds) {
    this._data = ds;
  }

  private synchronized Connection getConnection() throws SQLException {
    if (this._data == null) {
      if(this._conn == null)
          this._conn = DriverManager.getConnection(_DBUrl, _DBUser, _DBPass);
      return this._conn;
    }
    else {
      return this._data.getConnection();
    }
  }

  private synchronized void release(Connection conn) {
    if (this._data == null) {
      return;
    }
    if(conn == null) {
      return;
    }
    try {
        conn.close();
    }
    catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public boolean delete(String fullname, boolean abProcessRecursively, Collection deletedElements) {

    boolean retObj = false;

    Connection db = null;
    Statement st = null;
    int id = 0;
    ArrayList alIds = new ArrayList();
    String[] files = null;
    StringBuffer sbQuery = new StringBuffer();

    try {

      if (abProcessRecursively) {
        Stack fileStack = new Stack();
        fileStack.push(fullname);

        while (!fileStack.empty()) {
          String fileNamePrefix = (String) fileStack.pop();

          id = this.lookupId(fileNamePrefix);

          if (id != 0) {
            Logger.debug("", this, "", "ADDING ID " + id + " FOR " + fileNamePrefix);
            alIds.add(String.valueOf(id));
          }

          files = this.listFiles(fileNamePrefix);   // TODO improve THIS

          for (int ii = 0; files != null && ii < files.length; ii++) {
            fileStack.push(fileNamePrefix + "/" + files[ii]);
            if (deletedElements != null) {
              deletedElements.add(fileNamePrefix + "/" + files[ii]);
            }
          }

        }
      }
      else {
        id = this.lookupId(fullname);
        if (id != 0) {
          alIds.add(String.valueOf(id));
        }
      }

      if (alIds.size() > 0) {

        sbQuery = new StringBuffer("DELETE FROM repository_data WHERE id IN (");
        for (int jj = 0, ii = (alIds.size() - 1); ii >= 0; ii--, jj++) {
          if (jj > 0) {
            sbQuery.append(",");
          }
          sbQuery.append((String) alIds.get(ii));
        }
        sbQuery.append(")");

        db = this.getConnection();
        db.setAutoCommit(true);
        st = db.createStatement();

        st.executeUpdate(sbQuery.toString());

        retObj = true;

      }
      else {
        // no ids to delete... no error...
        retObj = true;
      }
    }
    catch (SQLException sqle) {
      retObj = false;
      Logger.error("", this, "", "RepositoryBean:delete exception (QUERY IS: " + sbQuery.toString() + ")", sqle);
    }
    finally {
      try {
        if (st != null) st.close();
      }
      catch (Exception e) {
      }
      this.release(db);
    }

    return retObj;
  }

  /**
   * 
   * 
   * Gets the data for the given full path name. 
   * 
   * @param asFullName the full path name
   * @return data
   */
  public synchronized byte[] getData(String asFullName) {

    byte[] retObj = null;

    Connection db = null;
    Statement st = null;
    ResultSet rs = null;
    java.io.InputStream is = null;
    ByteArrayOutputStream baos = null;
    int id = 0;

    try {
      id = lookupId(asFullName);

      if (id != 0) {

        db = this.getConnection();
        st = db.createStatement();
        st.execute("SELECT data FROM repository_data WHERE id=" + id);

        byte[] r = new byte[CHUNK_SIZE];

        rs = st.getResultSet();

        if (rs.next()) {
          baos = new ByteArrayOutputStream();
          is = rs.getBinaryStream("data");
          int j = 0;
          while ((j = is.read(r, 0, CHUNK_SIZE)) != -1) {
            baos.write(r, 0, j);
          }
          baos.flush();
          baos.close();
          is.close();
        }
        rs.close();
        rs = null;
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    finally {
      try {
        if (is != null) is.close();
      }
      catch (Exception e) {
      }
      try {
        if (rs != null) rs.close();
      }
      catch (Exception e) {
      }
      try {
        if (st != null) st.close();
      }
      catch (Exception e) {
      }
      this.release(db);
    }
    if(null != baos)
      retObj = baos.toByteArray();

    return retObj;
  }

  private int createId(String fullname) {

    Connection db = null;
    Statement st = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    int id = 0;

    try {
      id = this.lookupId(fullname);

      // create the file if it doesn't exist
      if (id == 0) {

        db = this.getConnection();
        db.setAutoCommit(false);
        st = db.createStatement();
        pst = db.prepareStatement("INSERT INTO repository_data (id,parentid,name,value,data,modification) VALUES (seq_repository_data.nextval,?,?,'Dir',EMPTY_BLOB(),SYSDATE)", new String[]{"id"});

        // creates directory path
        StringTokenizer stok = new StringTokenizer(fullname, "/");
        int parentid = 0;
        while (stok.hasMoreTokens()) {
          String dir = stok.nextToken();
          boolean found = false;
          if (parentid == 0) {
            rs = st.executeQuery("SELECT id,parentid FROM repository_data WHERE parentid is null and name='" + dir + "'");
          }
          else {
            rs = st.executeQuery("SELECT id,parentid FROM repository_data WHERE parentid=" + parentid + " and name='" + dir + "'");
          }
          
          if(rs.next()) {
            id = rs.getInt("id");
            found = true;
          }
          rs.close();
          
          if (!found) {
            if(parentid==0)
              pst.setNull(1, java.sql.Types.NUMERIC);
            else 
              pst.setInt(1, parentid);
            pst.setString(2, dir);
            pst.executeUpdate();

            rs = pst.getGeneratedKeys();
            if(rs.next())
              id = rs.getInt(1);
            rs.close();
          }
          
          parentid = id;
        }
        db.commit();
      }
    }
    catch (SQLException sqle) {
      sqle.printStackTrace();
      try {
        if (db != null) db.rollback();
      }
      catch (Exception e) {
      }
    }
    finally {
      try {
        if (rs != null) rs.close();
      }
      catch (Exception e) {
      }
      try {
        if (st != null) st.close();
      }
      catch (Exception e) {
      }
      this.release(db);
    }
    return id;
  }

  private ArrayList listFilesRecursive(Statement st, String topDir, String prefix, String repname) {
    ResultSet rs = null;
    int id = -1;
    ArrayList alLeaves = new ArrayList();
    TreeSet tstmp = null;
    Iterator it = null;

    try {

      String lookupDir = topDir;
      if (!prefix.equals("")) lookupDir += "/" + prefix;

      id = this.lookupId(lookupDir); // lookup current dir

      if (id > 0) {
        rs = st.executeQuery("SELECT id,name FROM repository_data WHERE parentid = " + id + " and value is null ORDER BY name");

        tstmp = new TreeSet();
        while (rs.next()) {
          String sName = rs.getString("name");
          if (!prefix.equals("")) sName = "/" + sName;

          tstmp.add(prefix + sName);

        }
        rs.close();
        rs = null;

        it = tstmp.iterator();
        for (int i = 0; it.hasNext(); i++) {
          alLeaves.add((String) it.next());
        }

        rs = st.executeQuery("SELECT name FROM repository_data WHERE parentid = " + id + " and value = 'Dir' ORDER BY name");

        tstmp = new TreeSet();
        while (rs.next()) {
          String sName = rs.getString("name");
          tstmp.add(sName);
        }
        rs.close();
        rs = null;

        it = tstmp.iterator();
        while (it.hasNext()) {
          String name = (String) it.next();

          String sPrefix = prefix;
          if (!sPrefix.equals("") && !sPrefix.endsWith("/")) sPrefix += "/";

          alLeaves.addAll(this.listFilesRecursive(st, topDir, sPrefix + name, name));
        }
      }
    }
    catch (SQLException sqle) {
      sqle.printStackTrace();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    finally {
      try {
        if (rs != null) rs.close();
      }
      catch (Exception e) {
      }
    }
    return alLeaves;
  }

  private int lookupId(String fullname) {

    int foundID = 0;

    StringTokenizer tokenizer = new StringTokenizer(fullname,"/");
    Connection db = null;
    PreparedStatement stFirst = null;
    PreparedStatement st = null;

    ResultSet rs = null;

    try {
      db = this.getConnection();
      stFirst = db.prepareStatement("select id from repository_data where name=? and parentid is null");
      st = db.prepareStatement("select id from repository_data where name=? and parentid=?");

      boolean isFirst = true;

      while(tokenizer.hasMoreTokens()) {
        String currToken = tokenizer.nextToken();

        if(!isFirst) {
          st.setString(1, currToken);
          st.setInt(2, foundID);
          rs = st.executeQuery();   
        } else {
          stFirst.setString(1, currToken);
          rs = stFirst.executeQuery();
        }

        if(rs.next()) {
          foundID = rs.getInt("id");
        } else {
          foundID = 0;
          break;  // ignore the rest
        }
        isFirst = false;
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      this.release(db);
    }

    return foundID;
  }


  public boolean setZipFile(String path, byte[] data) {
    boolean retObj = false;

    while(path.endsWith("/")) path = path.substring(0, path.length()-1);
    
    ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(data));
    try {
      ZipEntry entry = null;
      while ((entry = zis.getNextEntry()) != null) {
        if (!entry.isDirectory()) {
          String name = entry.getName();
          
        }
      }
      retObj = true;
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    try {
      zis.close();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    return retObj;
  }

  public boolean setFile(String name, byte[] data) {
    return setFile(name, data, false);
  }

  public boolean setFile(String fullname, byte[] data, boolean bIsDir) {


    boolean retObj = false;

    Connection db = null;
    Statement st = null;
    ResultSet rs = null;
    PreparedStatement pst = null;
    int id = 0;
    int parentid = 0;

    String shortname = fullname.substring(fullname.lastIndexOf("/") + 1);

    try {

      id = createId(fullname);

      if (id != 0) {
        // open connection
        db = this.getConnection();
        db.setAutoCommit(false);
        st = db.createStatement();

        // get blob handler

        rs = st.executeQuery("SELECT parentid FROM repository_data WHERE id=" + id);
        if (rs.next()) {
          parentid = rs.getInt("parentid");
        }
        rs.close();

        String sIsDir = "null";
        if (bIsDir) sIsDir = "'Dir'";

        st.executeUpdate("DELETE FROM repository_data WHERE id=" + id);  // TODO improve this
        st.executeUpdate("INSERT INTO repository_data (id,parentid,name,value,data,modification) VALUES (" + id + "," + parentid
            + ",'" + shortname + "'," + sIsDir + ",EMPTY_BLOB(),SYSDATE)");

        st.close();

        pst = db.prepareStatement("UPDATE repository_data SET data=?,modification=SYSDATE WHERE id=?");

        ByteArrayInputStream is = new ByteArrayInputStream(data);

        pst.setLong(2, id);
        pst.setBinaryStream(1, is, data.length);
        pst.execute();
        is.close();
        pst.close();
        db.commit();

        Logger.debug("", this, "", "Set file: "+fullname);

      }
      retObj = true;
    }
    catch (Exception sqle) {
      retObj = false;
      sqle.printStackTrace();
      try {
        if (db != null) db.rollback();
      }
      catch (Exception e) {
      }
    }
    finally {
      try {
        if (pst != null) pst.close();
      }
      catch (Exception e) {
      }
      try {
        if (rs != null) rs.close();
      }
      catch (Exception e) {
      }
      try {
        if (st != null) st.close();
      }
      catch (Exception e) {
      }
      this.release(db);
    }
    return retObj;
  }

  public boolean deleteFile(String name) {
    return this.delete(name, false, null);
  }

  public boolean deleteFile(String name, boolean abProcessRecursively, Collection deletedItems) {
    return this.delete(name, abProcessRecursively, deletedItems);
  }

  public String[] listFilesAllLeaves(String repname) {

    Connection db = null;
    Statement st = null;
    ArrayList alLeaves = null;

    try {

      db = this.getConnection();
      st = db.createStatement();

      String topDir = repname;

      if (topDir == null || topDir.equals("null")) {
        topDir = "";
      }
      else if (!topDir.startsWith("/")) {
        topDir = "/" + topDir;
      }

      alLeaves = this.listFilesRecursive(st, topDir, "", "");

    }
    catch (SQLException sqle) {
      sqle.printStackTrace();
    }
    finally {
      try {
        if (st != null) st.close();
      }
      catch (Exception e) {
      }
      this.release(db);
    }

    if (alLeaves != null && alLeaves.size() > 0) {
      String[] fileList = new String[alLeaves.size()];
      for (int i = 0; i < alLeaves.size(); i++) {
        fileList[i] = (String) alLeaves.get(i);
      }
      return fileList;
    }
    else {
      return null;
    }

  }


  public String[] listFiles(String name) {

	  RepositoryObject[] ro = this.listFilesInfo(name);
	  String[] ret = null;

	  if (ro == null) {
		  return null;
	  }
	  else {
        ret = new String[ro.length];
		  for(int i = 0; i < ro.length; i++) {
			  ret[i] = ro[i].getName();
		  }
		  return ret;
	  }

  }


  public RepositoryObject[] listFilesInfo(String fullname) {

    Connection db = null;
    Statement st = null;
    ResultSet rs = null;
    int id = 0;
    ArrayList fileList = null;

    try {
      id = lookupId(fullname);

      if (id != 0) {
        fileList = new ArrayList();

        db = this.getConnection();
        db.setAutoCommit(true);
        st = db.createStatement();
        rs = st.executeQuery("SELECT id,parentid,name,modification FROM repository_data WHERE parentid=" + id);
        while (rs.next()) {
        	RepositoryObject ro = new RepositoryObject();
        	ro.setId(rs.getInt("id"));
        	ro.setParentId(rs.getInt("parentid"));
        	ro.setName(rs.getString("name"));
        	ro.setModification(rs.getTimestamp("modification"));
        	ro.setIsFile(true);
          ro.setFullPath(fullname);
          fileList.add(ro);
        }
        rs.close();
        rs = null;
      }
    }
    catch (SQLException sqle) {
      sqle.printStackTrace();
    }
    finally {
      try {
        if (rs != null) rs.close();
      }
      catch (Exception e) {
      }
      try {
        if (st != null) st.close();
      }
      catch (Exception e) {
    }
      this.release(db);
    }
    if (fileList == null) return null;

    RepositoryObject[] files = new RepositoryObject[fileList.size()];
    for (int i = 0; i < fileList.size(); i++)
      files[i] = (RepositoryObject) fileList.get(i);
    return files;
  }

  public boolean moveFile(String name, String from, String to) {


    boolean retObj = false;

    if (name == null || name.equals("") || from == null || from.equals("") || to == null || to.equals("")) {
      return retObj;
    }

    Connection db = null;
    Statement st = null;

    String stmp = null;

    try {
      int idfrom = this.lookupId(from);
      int idto = this.createId(to);

      // try to delete existing file (if it exists)
      String sDestFile = to;
      if (sDestFile.charAt((sDestFile.length() - 1)) != '/') {
        sDestFile += "/";
      }
      sDestFile += name;
      if (!this.deleteFile(sDestFile)) {
        // oops...
        return false;
      }

      String sName = from;

      if (sName.charAt((sName.length() - 1)) != '/') {
        sName += "/";
      }
      sName += name;
      int idname = this.lookupId(sName);   // TODO fazer isto legivel
      int ntmp = 0;

      if (idfrom != 0 && idto != 0 && idname != 0) {

        db = this.getConnection();
        db.setAutoCommit(false);
        st = db.createStatement();

        stmp = "UPDATE repository_data SET parentid=" + idto + " WHERE id=" + idname + " AND parentid=" + idfrom;

        ntmp = st.executeUpdate(stmp);

        if (ntmp == 1) {
          db.commit();
          retObj = true;
        }
        else {
          db.rollback();
          retObj = false;
        }

      }
    }
    catch (Exception e) {
      if (db != null) {
        try {
          db.rollback();
        }
        catch (Exception e2) {
        }
      }
      e.printStackTrace();
    }
    finally {
      try {
        if (st != null) st.close();
      }
      catch (Exception e) {
      }
      this.release(db);
    }

    return retObj;
  }

  public boolean checkAvailability() {
    int id = lookupId("/"+Const.SYSTEM_ORGANIZATION);
    return id != 0;
  }
}
