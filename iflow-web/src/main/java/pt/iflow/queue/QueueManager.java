package pt.iflow.queue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.axis.utils.StringUtils;

import pt.iflow.api.db.DBQueryManager;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;

/**
 * <p>Title: QueueManager</p>
 * <p>Description: Manages the iFlow queue processes</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: iKnow</p>
 * @author iKnow
 * @version 1.0
 */

public class QueueManager {

  protected static final int NAN = -1;
  public static final int nETIQUETAS_QUEUE_OBJECT = 1;

  private static final String sPROPS_SEPARATOR = ";;;";

  private static QueueManager _qm = new QueueManager();

  private static Set<String> _hsProcCols = new HashSet<String>();
  private static Set<String> _hsDataCols = new HashSet<String>();

  public static final String sCOL_ID = "ID";
  public static final String sCOL_OBJECT = "OBJECT";
  public static final String sCOL_GROUPID = "GROUPID";
  public static final String sCOL_FLOWID = "FLOWID";
  public static final String sCOL_PID = "PID";
  public static final String sCOL_PROPERTIES = "PROPERTIES";
  public static final String sCOL_CREATION_DATE = "CREATION_DATE";
  public static final String sCOL_QUEUE_PROC_ID = "QUEUE_PROC_ID";
  public static final String sCOL_NAME = "NAME";
  public static final String sCOL_VALUE = "VALUE";
  
  /// Queries
  private static final String GET_QUEUE_PROC_IN = "QueueManager.GET_QUEUE_PROC_IN";
  private static final String GET_QUEUE_DATA_IN = "QueueManager.GET_QUEUE_DATA_IN";
  private static final String GET_QUEUE_PROC_IDS = "QueueManager.GET_QUEUE_PROC_IDS";
  private static final String GET_QUEUE_DATA_IDS = "QueueManager.GET_QUEUE_DATA_IDS";
  private static final String INSERT_QUEUE_PROC = "QueueManager.INSERT_QUEUE_PROC";
  private static final String INSERT_QUEUE_DATA = "QueueManager.INSERT_QUEUE_DATA";
  private static final String UPDATE_QUEUE_PROPERTIES = "QueueManager.UPDATE_QUEUE_PROPERTIES";
  private static final String DELETE_QUEUE_DATA = "QueueManager.DELETE_QUEUE_DATA";

  
  static {
    _hsProcCols.add("ID");
    _hsProcCols.add("OBJECT");
    _hsProcCols.add("GROUPID");
    _hsProcCols.add("FLOWID");
    _hsProcCols.add("PID");
    _hsProcCols.add("PROPERTIES");
    _hsProcCols.add("CREATION_DATE");

    _hsDataCols.add("QUEUE_PROC_ID");
    _hsDataCols.add("NAME");
    _hsDataCols.add("VALUE");
  }


  public static boolean setQueueProc(UserInfoInterface userInfo, QueueProc aqpProc) {
    boolean retObj = false;

    boolean bInsert = true;

    String sUser = userInfo.getUtilizador();

    if (aqpProc.getId() > 0) {
      bInsert = false;
    }

    DataSource ds = null;
    Connection db = null;
    Statement st = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    int nQueueId = 0;
    List<String> alProps = null;
    Properties pProps = null;

    // prepare props
    try {
      pProps = aqpProc.getProps();
      if (pProps != null && pProps.size() > 0) {
        Enumeration<?> enumer = pProps.propertyNames();
        alProps = new ArrayList<String>();
        while (enumer.hasMoreElements()) {
          String sProp = (String)enumer.nextElement();
          sProp += "=" + pProps.getProperty(sProp);
          alProps.add(sProp);
        }
      }
    }
    catch (Exception e) {
      alProps = null;
    }

    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      db.setAutoCommit(false);

      if (bInsert) {
        final String insertProcQuery = DBQueryManager.getQuery(QueueManager.INSERT_QUEUE_PROC);
        final String [] autoGenCols = new String[]{sCOL_ID};
        
        pst = db.prepareStatement(insertProcQuery, autoGenCols);
        
        // object
        pst.setInt(1,aqpProc.getObject()); 
        // groupid
        pst.setString(2,aqpProc.getGroup());
        // flowid
        pst.setInt(3,aqpProc.getFlowId()); 
        // pid
        pst.setInt(4,aqpProc.getPid()); 
        // properties
        String properties = null;
        if (alProps != null) properties = Utils.unTokenize(alProps,sPROPS_SEPARATOR);
        pst.setString(5,properties);
        // creation_date
        java.util.Date creationDate = aqpProc.getCreationDate();
        if(null == creationDate) creationDate = Calendar.getInstance().getTime();
        pst.setTimestamp(6, new Timestamp(creationDate.getTime()));


        pst.executeUpdate();
        rs = pst.getGeneratedKeys();
        
        if(rs.next()) {
          nQueueId = rs.getInt(1);
        }
        rs.close();
        pst.close();
        
      } else {  // not insert, update.
        
        // first get existing properties
        nQueueId = aqpProc.getId();
        st = db.createStatement();
        
        final String updatePropertiesQuery = DBQueryManager.getQuery(QueueManager.UPDATE_QUEUE_PROPERTIES);
        String properties = null;
        if (alProps != null) properties = Utils.unTokenize(alProps,sPROPS_SEPARATOR);
        
        pst = db.prepareStatement(updatePropertiesQuery);
        pst.setString(1, properties);
        pst.setInt(2, nQueueId);
        pst.executeUpdate();
        pst.close();
        
        final String deleteDataQuery = DBQueryManager.processQuery(QueueManager.DELETE_QUEUE_DATA, new Object[]{new Integer(nQueueId)});
        st.executeUpdate(deleteDataQuery);
        
      }

      // NOW HANDLE DATA
      final String insertDataQuery = DBQueryManager.getQuery(QueueManager.INSERT_QUEUE_DATA);
      pst = db.prepareStatement(insertDataQuery);
      
      Map<String,String> data = aqpProc.getData();
      if(data == null) data = new HashMap<String, String>();
      Iterator<String> iter = data.keySet().iterator();
      while (iter.hasNext()) {
        String name = (String) iter.next();
        String value = data.get(name);
        
        if(StringUtils.isEmpty(value)) continue; // do nothing
        
        pst.setInt(1, nQueueId);
        pst.setString(2, name);
        pst.setString(3, value);
        
        pst.executeUpdate();
      } // while
      pst.close();
      pst = null;
      
      
      db.commit();
      
      retObj = true;
      
      if (bInsert) {
        aqpProc.setId(nQueueId);
      }
      
    }
    catch (Exception e) {
      retObj = false;
      try {
        if (db != null) db.rollback();
      }
      catch (Exception e2) {
        Logger.error(sUser,_qm,"setQueueProc","Unable to rollback due to: " + e.getMessage(), e2);
      }
      Logger.error(sUser,_qm,"setQueueProc",e.getMessage(), e);
    }
    finally {
      DatabaseInterface.closeResources(db,st,pst,rs);
    }

    return retObj;
  }

  /**
   * Gets queue procs matching given conditions.
   *
   * @param userInfo calling user info
   * @param apConditions the conditions, if the form of name=colname, value=desired value(s)
   * @param apConditionsOperators the conditions' operators, in the form of name=colname
   * and value=operator (e.g. =, in, not null, ...) if not supplied, then only value of 
   * apConditions for given name is assumed (in case of more complicated conditions)
   *
   */
  public static QueueProc[] getQueueProcs(UserInfoInterface userInfo, Properties apConditions, Properties apConditionsOperators) {
    QueueProc[] retObj = null;

    String sUser = userInfo.getUtilizador();

    DataSource ds = null;
    Connection db = null;
    Statement st = null;
    ResultSet rs = null;
    boolean bSearchProc = false;
    boolean bSearchData = false;
    List<String> alProcs = null;

    try {
      StringBuffer sbQueueProcWhere = new StringBuffer();
      StringBuffer sbQueueDataWhere = new StringBuffer();
      
      if (apConditions != null && apConditions.size() > 0) {	
        StringBuffer sb = null;

        Enumeration<?> eCond = apConditions.propertyNames();
        while (eCond.hasMoreElements()) {
          String condProperty = (String)eCond.nextElement();
          String condConjunction = "";
          sb = null;
          if (_hsProcCols.contains(condProperty)) {
            condConjunction = bSearchProc ? " and " : " where ";
            bSearchProc = true;
            sb = sbQueueProcWhere;
          }
          else if (_hsDataCols.contains(condProperty)) {
            condConjunction = bSearchData ? " and " : " where ";
            bSearchData = true;
            sb = sbQueueDataWhere;
          }
          else {
            continue;
          }
          sb.append(condConjunction);
          String condOperator = apConditionsOperators.getProperty(condProperty);
          if (condOperator != null && !condOperator.equals("")) {
            sb.append(condProperty).append(condOperator);
          }
          sb.append(apConditions.getProperty(condProperty));
        }
      } // if (apConditions

      ds = Utils.getDataSource();
      db = ds.getConnection();
      st = db.createStatement();

      if (bSearchData) {
        final String queueDataQuery = DBQueryManager.processQuery(QueueManager.GET_QUEUE_DATA_IDS, new Object[]{sbQueueDataWhere});
        rs = st.executeQuery(queueDataQuery);

        bSearchData = false;
        while (rs.next()) {
          // use data proc id results to narrow proc search (append ids to where clause)
          String sId = rs.getString(QueueManager.sCOL_QUEUE_PROC_ID);
          if (bSearchData) {
            if (bSearchProc) {
              sbQueueProcWhere.append(" and ");
            }
            else {
              sbQueueProcWhere.append(" where ");
            }
            sbQueueProcWhere.append(QueueManager.sCOL_ID).append(" in (");
          }
          else {
            sbQueueProcWhere.append(",");
          }
          sbQueueProcWhere.append(sId);
          bSearchData = true;
        }
        if (bSearchData) {
          sbQueueProcWhere.append(")");
        }
      }

      alProcs = new ArrayList<String>();
      final String queueProcQuery = DBQueryManager.processQuery(QueueManager.GET_QUEUE_PROC_IDS, new Object[]{sbQueueProcWhere});
      rs = st.executeQuery(queueProcQuery);
      while (rs.next()) {
        String sId = rs.getString(QueueManager.sCOL_ID);
        alProcs.add(sId);
      }
    }
    catch (Exception e) {
      Logger.error(sUser,_qm,"getQueueProcs1",e.getMessage(),e);
    }
    finally {
      DatabaseInterface.closeResources(db,st,rs);
    }

    retObj = QueueManager.getQueueProcs(userInfo, alProcs);


    return retObj;
  }


  public static QueueProc getQueueProc(UserInfoInterface userInfo, int anQueueProcId) {
    QueueProc retObj = null;
    
    try {
      List<String> altmp = new ArrayList<String>();
      altmp.add(String.valueOf(anQueueProcId));
      
      retObj = (QueueManager.getQueueProcs(userInfo,altmp))[0];
    }
    catch (Exception e) {
      retObj = null;
    }

    return retObj;
  }


  public static QueueProc[] getQueueProcs(UserInfoInterface userInfo, List<String> aalQueueProcIds) {
    QueueProc[] retObj = null;

    String sUser = userInfo.getUtilizador();

    QueueProc qp = null;
    List<String> alProcs = null;
    HashMap<String,QueueProc> hmProcs = null;

    DataSource ds = null;
    Connection db = null;
    Statement st = null;
    ResultSet rs = null;
    String stmp = null;
    String stmp2 = null;
    StringBuffer sbtmp = null;
    List<String> altmp = null;
    Properties ptmp = null;
    int ntmp = 0;
    java.util.Date dt = null;

    try {

      if (aalQueueProcIds != null && aalQueueProcIds.size() > 0) {

        sbtmp = new StringBuffer();
        for (int ii=0; ii < aalQueueProcIds.size(); ii++) {
          if (ii > 0) {
            sbtmp.append(",");
          }
          sbtmp.append((String)aalQueueProcIds.get(ii));
        }

        ds = Utils.getDataSource();
        db = ds.getConnection();
        st = db.createStatement();

        stmp = DBQueryManager.processQuery(QueueManager.GET_QUEUE_PROC_IN, new Object[]{sbtmp});

        rs = st.executeQuery(stmp);

        alProcs = new ArrayList<String>();
        hmProcs = new HashMap<String, QueueProc>();
        while (rs.next()) {
          stmp = rs.getString(QueueManager.sCOL_ID);

          ptmp = null;
          altmp = Utils.tokenize(rs.getString(QueueManager.sCOL_PROPERTIES), QueueManager.sPROPS_SEPARATOR);
          if (altmp != null) {
            ptmp = new Properties();
            for (int jj=0; jj < altmp.size(); jj++) {
              stmp2 = (String)altmp.get(jj);
              ntmp = stmp2.indexOf("=");
              if (ntmp == -1) continue;
              ptmp.setProperty(stmp2.substring(0,ntmp),
                  stmp2.substring(ntmp+1));
            }
          }

          dt = null;
          try {
            dt = 
              new java.util.Date((rs.getTimestamp(QueueManager.sCOL_CREATION_DATE)).getTime());
          }
          catch (Exception ei) {
          }

          qp = new QueueProc(Integer.parseInt(stmp),
              rs.getInt(QueueManager.sCOL_OBJECT),
              rs.getString(QueueManager.sCOL_GROUPID),
              rs.getInt(QueueManager.sCOL_FLOWID),
              rs.getInt(QueueManager.sCOL_PID),
              ptmp,
              dt,
              new Hashtable<String, String>());
          alProcs.add(stmp);
          hmProcs.put(stmp,qp);
        }
        rs.close();
        rs = null;

        stmp = DBQueryManager.processQuery(QueueManager.GET_QUEUE_DATA_IN, new Object[]{sbtmp});

        rs = st.executeQuery(stmp);

        while (rs.next()) {
          stmp = rs.getString(QueueManager.sCOL_QUEUE_PROC_ID);
          qp = hmProcs.get(stmp);
          qp.setData(rs.getString(QueueManager.sCOL_NAME), rs.getString(QueueManager.sCOL_VALUE));
          hmProcs.put(stmp,qp);
        }

        retObj = new QueueProc[alProcs.size()];
        for (int jj=0; jj < alProcs.size(); jj++) {
          retObj[jj] = hmProcs.get((String)alProcs.get(jj));
        }

      } // if (aalQueueProcIds != null && 
    }
    catch (Exception e) {
      Logger.error(sUser,_qm,"getQueueProcs2",e.getMessage(), e);
    }
    finally {
      DatabaseInterface.closeResources(db,st,rs);
    }


    return retObj;
  }
}
