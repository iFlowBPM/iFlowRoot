package pt.iflow.flows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;

import pt.iflow.api.core.AdministrationFlowScheduleInterface;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.ProcessCatalogue;
import pt.iflow.api.core.ProcessManager;
import pt.iflow.api.db.DBQueryManager;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.flowSchedule.FlowScheduleDataInterface;
import pt.iflow.api.flows.Flow;
import pt.iflow.api.flows.FlowDeployListener;
import pt.iflow.api.flows.FlowHolder;
import pt.iflow.api.flows.FlowSetting;
import pt.iflow.api.flows.FlowSettings;
import pt.iflow.api.flows.FlowTemplate;
import pt.iflow.api.flows.FlowType;
import pt.iflow.api.flows.FlowVersionListener;
import pt.iflow.api.flows.IFlowData;
import pt.iflow.api.flows.NewFlowListener;
import pt.iflow.api.licensing.LicenseService;
import pt.iflow.api.licensing.LicenseServiceException;
import pt.iflow.api.licensing.LicenseServiceFactory;
import pt.iflow.api.presentation.DateUtility;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;
import pt.iflow.api.xml.FlowMarshaller;
import pt.iflow.api.xml.codegen.flow.XmlAttribute;
import pt.iflow.api.xml.codegen.flow.XmlBlock;
import pt.iflow.api.xml.codegen.flow.XmlCatalogVarAttribute;
import pt.iflow.api.xml.codegen.flow.XmlFlow;

/**
 * 
 * Flow resource manager/loader/"cache"
 * 
 * @author oscar
 * 
 */
public class FlowHolderBean implements FlowHolder {
    
    private static final int MAX_COMMENT_SIZE = 512;
    
    // KEY: flowid(Integer) | VALUE: data (FlowData)
    final private HashMap<String, Map<Integer, FlowData>> _hmFlowData = new HashMap<String, Map<Integer, FlowData>>();
    // KEY: flowid(Integer) | VALUE: date (java.util.Date)
    final private HashMap<Integer, Date> _hmFlowBuildDate = new HashMap<Integer, Date>();
    
    // used in Logger methods
    private static FlowHolder instance = null;

    private Map<String, NewFlowListener> newflowListeners = 
      Collections.synchronizedMap(new HashMap<String,NewFlowListener>());
    
    private Map<String, FlowDeployListener> deploylisteners = 
      Collections.synchronizedMap(new HashMap<String,FlowDeployListener>());

    private Map<String, FlowVersionListener> versionlisteners = 
      Collections.synchronizedMap(new HashMap<String,FlowVersionListener>());


    
    public static FlowHolder getInstance() {
        if (null == instance)
            instance = new FlowHolderBean();
        return instance;
    }
    
    // wait 5 seconds between flow building to avoid infinite looping
    private static final long BUILD_WAIT = (long) (1000 * 5);
    
    // Flow filtering codes
    private static final int nLIST_ALL = 0;
    private static final int nLIST_ONLINE = 1;
    private static final int nLIST_OFFLINE = 2;
    
    // aux methods
    
    /**
     * Read every byte from input stream and return as a byte array
     * 
     * @param is
     *            Data input stream
     * @return byte array
     */
    private static byte[] getBytes(InputStream is) throws IOException {
        if (is == null)
            return null;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        byte[] b = new byte[8092];
        int r;
        while ((r = is.read(b)) != -1)
            bout.write(b, 0, r);
        is.close();
        return bout.toByteArray();
    }
    
    private static final class State {
        /**
         * Flow ID number
         */
        int flowid;
        
        /**
         * Flow was created
         */
        boolean created;
        
        /**
         * Everything ended successfully
         */
        boolean success;
        
        /**
         * New (or current) version
         */
        int version;
        
    }
    
    private static final class DBFlow {
        @SuppressWarnings("unused")
        int flowid;
        String organizationId;
        byte[] data;
        String name;
        String file;
        boolean online;
        long created;
        long lastModified;
        int seriesId;
        String typeCode;
        String[] indexFields = new String[Const.INDEX_COLUMN_COUNT];
    }
    
    // interface methods
    
    /**
     * Check if the flow is online
     * 
     * @param userInfo
     * @param flowId
     * @return true if the flow is marked as online
     */
    public synchronized boolean isOnline(UserInfoInterface userInfo, int flowId) {
        // check if this flow is cached
        if (hasCachedFlow(userInfo, flowId)) {
            // only if the user is the same user
            IFlowData theFlow = getCachedFlow(userInfo, flowId);
            return theFlow.isOnline();
        }
        Connection db = null;
        Statement st = null;
        ResultSet rs = null;
        boolean result = false;
        
        // check if flow exists in DB and is enabled
        try {
            db = Utils.getDataSource().getConnection();
            st = db.createStatement();
            String stmp = "select organizationid from flow where flowid="
                    + flowId + " and enabled=1";
            rs = st.executeQuery(stmp);
            if (rs.next()) {
              if (userInfo.isSysAdmin() ||
                  rs.getString("organizationid").equalsIgnoreCase(userInfo.getOrganization()) || 
                  BeanFactory.getFlowSettingsBean().isGuestAccessible(userInfo, flowId)) {
                result = true;
              }
            }
            rs.close();
            st.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseInterface.closeResources(db, st, rs);
        }
        
        return result;
    }
    
    /**
     * Return the flow file name
     * 
     * @param userInfo
     * @param flowId
     * @return Flow file name or null if not found
     */
    public synchronized String getFlowFileName(UserInfoInterface userInfo,
            int flowId) {
        // check if this flow is cached
        if (hasCachedFlow(userInfo, flowId)) {
            IFlowData theFlow = getCachedFlow(userInfo, flowId); // only if the
            // user is the
            // same user
            return theFlow.getFileName();
        }
        
        Connection db = null;
        Statement st = null;
        ResultSet rs = null;
        String result = null;
        
        // check if flow exists in db and is disabled
        try {
            db = Utils.getDataSource().getConnection();
            
            // check if flow exists in db and is disabled
            st = db.createStatement();
            String stmp = "select flowfile from flow where flowid=" + flowId
                    + " and organizationid='" + userInfo.getOrganization()
                    + "'";
            rs = st.executeQuery(stmp);
            
            if (rs.next()) {
                result = rs.getString("flowfile");
            }
            rs.close();
            st.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseInterface.closeResources(db, st, rs);
        }
        
        return result;
    }
    
    /**
     * Return a list of all flows available to the user organization. <br>
     * <em>Do not return template flows!</em>
     * 
     * @param userInfo
     * @return
     */
    public IFlowData[] listFlows(UserInfoInterface userInfo) {
        return listFlows(userInfo, nLIST_ALL, null, null, false);
    }
    
    /**
     * Return a list of all flows available to the user organization. <br>
     * <em>Do not return template flows!</em>
     * 
     * @param userInfo
     * @return
     */
    public IFlowData[] listFlows(UserInfoInterface userInfo, FlowType type) {
        return listFlows(userInfo, nLIST_ALL, type, null, false);
    }
    
    /**
     * Return a list of online flows available to the user organization. <br>
     * <em>Do not return template flows!</em>
     * 
     * @param userInfo
     * @return
     */
    public FlowData[] listFlowsOnline(UserInfoInterface userInfo) {
        return listFlows(userInfo, nLIST_ONLINE, null, null, false);
    }
    
    /**
     * Return a list of online flows available to the user organization. <br>
     * <em>Do not return template flows!</em>
     * 
     * @param userInfo
     * @return
     */
    public FlowData[] listFlowsOnline(UserInfoInterface userInfo, FlowType type) {
        return listFlows(userInfo, nLIST_ONLINE, type, null, false);
    }

    public FlowData[] listFlowsOnline(UserInfoInterface userInfo, FlowType type, boolean showOnlyFlowsToBePresentInMenu) {
      return listFlows(userInfo, nLIST_ONLINE, type, null, showOnlyFlowsToBePresentInMenu);
    }

    public IFlowData[] listFlowsOnline(UserInfoInterface userInfo, FlowType type, FlowType[] typeExcluded) {
      return listFlows(userInfo, nLIST_ONLINE, type, typeExcluded, false);
    }

    public IFlowData[] listFlowsOnline(UserInfoInterface userInfo, FlowType type, FlowType[] typeExcluded,
        boolean showOnlyFlowsToBePresentInMenu) {
      return listFlows(userInfo, nLIST_ONLINE, type, typeExcluded, showOnlyFlowsToBePresentInMenu);
    }

    /**
     * Return a list of offline flows available to the user organization. <br>
     * <em>Do not return template flows!</em>
     * 
     * @param userInfo
     * @return
     */
    public IFlowData[] listFlowsOffline(UserInfoInterface userInfo) {
        return listFlows(userInfo, nLIST_OFFLINE, null, null, false);
    }
    
    /**
     * Return a list of offline flows available to the user organization. <br>
     * <em>Do not return template flows!</em>
     * 
     * @param userInfo
     * @return
     */
    public IFlowData[] listFlowsOffline(UserInfoInterface userInfo, FlowType type) {
        return listFlows(userInfo, nLIST_OFFLINE, type, null, false);
    }
    
    public IFlowData[] listFlowsOffline(UserInfoInterface userInfo, FlowType type, FlowType[] typeExcluded) {
      return listFlows(userInfo, nLIST_OFFLINE, type, typeExcluded, false);
  }
  
    private synchronized FlowData[] listFlows(UserInfoInterface userInfo,
            int anSelection, FlowType type, FlowType[] typeExcluded, boolean showOnlyFlowsToBePresentInMenu) {
        FlowData[] retObj = new FlowData[] {};

        ArrayList<FlowScheduleDataInterface> listOfFlowJobs =  new ArrayList<FlowScheduleDataInterface>();
        try {
          AdministrationFlowScheduleInterface adminFlowScheduleBean = BeanFactory.getAdministrationFlowScheduleBean();
          listOfFlowJobs = adminFlowScheduleBean.getScheduledFlowsJobs(userInfo, null); // o segundo parametro ao ser null devolve os agendamentos de todos os utilizadores
        } catch (Exception e) {
          Logger.error(userInfo.getUtilizador(), this, "listFlows", "Unhindered error", e);
        }

        DataSource ds = null;
        Connection db = null;
        Statement st = null;
        ResultSet rs = null;
        
        FlowData fd = null;
        
        try {
            
            ds = Utils.getDataSource();
            db = ds.getConnection();
            st = db.createStatement();
            rs = null;

            StringBuffer sQuery = new StringBuffer();

            sQuery.append("select ");
            if (showOnlyFlowsToBePresentInMenu){
              sQuery.append("Distinct ");
            }
            sQuery.append("F.flowid, F.flowname, F.flowfile, F.enabled, F.organizationid, F.created, F.modified, F.seriesId, F.type_code ");
            sQuery.append("from flow F");
            if (showOnlyFlowsToBePresentInMenu){
              sQuery.append(", flow_settings FS");
            }
            sQuery.append(" where organizationid='").append(userInfo.getOrganization()).append("' ");
            if (anSelection != nLIST_ALL){
              sQuery.append("and enabled=").append((anSelection == nLIST_ONLINE ? 1 : 0));
            }
            if(null != type){
              sQuery.append(" and type_code='").append(type.getCode()).append("'");
            }
            if(null != typeExcluded){
              for (int i=0; i<typeExcluded.length;i++){
                sQuery.append(" and type_code<>'").append(typeExcluded[i].getCode()).append("'");
              }
            }
            if (showOnlyFlowsToBePresentInMenu){
              sQuery.append(" and F.flowid = FS.flowid");
              sQuery.append(" and FS.name like '").append(Const.sFLOW_MENU_ACCESSIBLE).append("'");
              sQuery.append(" and (");
              sQuery.append(" FS.value is null ");
              sQuery.append(" or ");
              sQuery.append(" FS.value like '").append(Const.sFLOW_MENU_ACCESSIBLE_YES).append("'");
              sQuery.append(" )");
            }
            sQuery.append(" order by F.flowid");

            rs = st.executeQuery(sQuery.toString());

            ArrayList<FlowData> altmp = new ArrayList<FlowData>();
            while (rs.next()) {
                int flowId = rs.getInt("flowid");
                if (hasCachedFlow(userInfo, flowId)) {
                    fd = getCachedFlow(userInfo, flowId);
                } else {
                    fd = new FlowData(flowId, 
                        rs.getString("flowname"), 
                        rs.getString("flowfile"), 
                        rs.getBoolean("enabled"),
                        rs.getString("organizationid"), 
                        rs.getTimestamp("created").getTime(), 
                        rs.getTimestamp("modified").getTime(), 
                        rs.getInt("seriesId"),
                        rs.getString("type_code")
                    );
                }

                try{
                  if (listOfFlowJobs !=null && listOfFlowJobs.size() > 0) {
                    for (FlowScheduleDataInterface flowJob:listOfFlowJobs){
                      if (flowJob.getFlowId() == flowId){
                        fd.setHasSchedules(true);
                        break;
                      }
                    }
                  }
                } catch (Exception e) {
                  Logger.error(userInfo.getUtilizador(), this, "listFlows", "Unhindered error", e);
                }

                altmp.add(fd);
            } // while
            
            retObj = altmp.toArray(new FlowData[altmp.size()]);
        } catch (Exception e) {
            Logger.error(userInfo.getUtilizador(), this, "listFlows",
                    "exception (" + e.getClass().getName() + ") caught: "
                            + e.getMessage(), e);
        } finally {
            DatabaseInterface.closeResources(db, st, rs);
        }
        
        return retObj;
    }
    
    /**
     * Return a list of names of every flow available to the user. <br>
     * <em>This method returns template flow names!</em>
     * 
     * @param userInfo
     * @return
     */
    public String[] listFlowNames(UserInfoInterface userInfo) {
        return listFlowNames(userInfo, nLIST_ALL);
    }
    
    /**
     * Return a list of names of online flows available to the user. <br>
     * <em>This method returns template flow names!</em>
     * 
     * @param userInfo
     * @return
     */
    public String[] listFlowNamesOnline(UserInfoInterface userInfo) {
        return listFlowNames(userInfo, nLIST_ONLINE);
    }
    
    /**
     * Return a list of names of offline flows available to the user. <br>
     * <em>This method returns template flow names!</em>
     * 
     * @param userInfo
     * @return
     */
    public String[] listFlowNamesOffline(UserInfoInterface userInfo) {
        return listFlowNames(userInfo, nLIST_OFFLINE);
    }
    
    private synchronized String[] listFlowNames(UserInfoInterface userInfo,
            int anSelection) {
        String[] retObj = null;
        
        DataSource ds = null;
        Connection db = null;
        Statement st = null;
        ResultSet rs = null;
        
        try {
            
            ds = Utils.getDataSource();
            db = ds.getConnection();
            st = db.createStatement();
            rs = null;
            
            // return every flowfile in orgnaizationid. Union will remove
            // duplicates (according to SQL specs)
            final String allOrgFlows = "select flowfile from flow where organizationid='"
                    + userInfo.getOrganization()
                    + "' UNION select name as flowfile from flow_template";
            
            // return all online names. templates are never online
            final String onlineOrgFlows = "select flowfile from flow where organizationid='"
                    + userInfo.getOrganization() + "' and enabled=1";
            
            // return every disabled flowfile in orgnaizationid. The unioned
            // query will exclude all org flows so it will not list an online
            // flow as offline
            final String offlineOrgFlows = "select flowfile from flow where organizationid='"
                    + userInfo.getOrganization()
                    + "' and enabled=0 UNION select name as flowfile from flow_template where name not in (select flowfile from flow where organizationid='"
                    + userInfo.getOrganization() + "')";
            
            String query;
            if (anSelection == nLIST_OFFLINE)
                query = offlineOrgFlows;
            else if (anSelection == nLIST_ONLINE)
                query = onlineOrgFlows;
            else
                query = allOrgFlows;
            
            rs = st.executeQuery(query);
            
            ArrayList<String> altmp = new ArrayList<String>();
            while (rs.next()) {
                altmp.add(rs.getString("flowfile"));
            } // while
            
            retObj = altmp.toArray(new String[altmp.size()]);
        } catch (Exception e) {
            Logger.error(userInfo.getUtilizador(), this, "listFlowNames",
                    "exception (" + e.getClass().getName() + ") caught: "
                            + e.getMessage(), e);
        } finally {
            DatabaseInterface.closeResources(db, st, rs);
        }
        
        return retObj;
    }
    
    /**
     * Return a list of names of every sub flow available to the user. <br>
     * <em>This method returns template flow names!</em>
     * 
     * @param userInfo
     * @return
     */
    public synchronized String[] listSubFlows(UserInfoInterface userInfo) {
        Connection db = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        String[] flows = null;
        
        try {
            db = Utils.getDataSource().getConnection();
            
            pst = db
                    .prepareStatement("SELECT flowfile FROM sub_flow WHERE organizationid=? UNION SELECT name as flowfile from sub_flow_template order by flowfile");
            
            // 1st read user flow
            pst.setString(1, userInfo.getOrganization());
            rs = pst.executeQuery();
            ArrayList<String> lst = new ArrayList<String>();
            while (rs.next()) {
                lst.add(rs.getString(1));
            }
            
            flows = lst.toArray(new String[lst.size()]);
        } catch (Exception e) {
            Logger.error(userInfo.getUtilizador(), this, "listSubFlows",
                    "exception (" + e.getClass().getName() + ") caught: "
                            + e.getMessage(), e);
        } finally {
            DatabaseInterface.closeResources(db, pst, rs);
        }
        
        return flows;
    }
    
    /**
     * Get the XML contents of the named flow.
     * 
     * If the requested name is a flow template, return template contents.
     * 
     * <br>
     * <em>This method will not create a new flow!</em>
     * 
     * @param userInfo
     * @param name
     *            file name
     * @return XML bytes
     */
    public synchronized byte[] readFlowData(UserInfoInterface userInfo,
            String name) {
        return readData(userInfo, name, true, -1);
    }
    
    /**
     * Get the XML contents of the named sub flow.
     * 
     * If the requested name is a sub flow template, return template contents.
     * 
     * <br>
     * <em>This method will not create a new sub flow!</em>
     * 
     * @param userInfo
     * @param name
     *            file name
     * @return XML bytes
     */
    public synchronized byte[] readSubFlowData(UserInfoInterface userInfo,
            String name) {
        return readData(userInfo, name, false, -1);
    }
    
    /**
     * Create or update a flow. <br>
     * The user must be organization administrator in order to create/update a
     * flow. <br>
     * <br>
     * If the flow is already online, it will be redeployed.
     * 
     * @param userInfo
     * @param file
     * @param name
     *            Used in flow creation. If null, assumes name=file.
     * @param data
     * @return updated/created flow id or -1 if an error occured
     */
    public int writeFlowData(UserInfoInterface userInfo,
            String file, String name, byte[] data) {
      return writeFlowData(userInfo, file, name, data, false, null);
    }
    
    /**
     * Create or update a flow. <br>
     * The user must be organization administrator in order to create/update a
     * flow. <br>
     * <br>
     * If the flow is already online, it will be redeployed.
     * 
     * @param userInfo
     * @param file
     * @param name
     *            Used in flow creation. If null, assumes name=file.
     * @param data
     * @return updated/created flow id or -1 if an error occured
     */
    public int writeFlowData(UserInfoInterface userInfo,
            String file, String name, byte[] data, String comment) {
      return writeFlowData(userInfo, file, name, data, true, comment);
    }

    private synchronized int writeFlowData(UserInfoInterface userInfo,
            String file, String name, byte[] data, boolean newVersion, String comment) {
        if (!userInfo.isOrgAdmin()) {
            Logger.debug(userInfo.getUtilizador(), this, "writeFlowData",
                    "No Adm privilege!!");
            return -1;
        }
        
        if (data == null)
            return -1;
        
        State result = insertOrUpdateFlow(userInfo, file, name, data, true,
                newVersion, comment);
        
        if (result.success && !result.created
                && hasCachedFlow(userInfo, result.flowid)) {
            IFlowData fd = getCachedFlow(userInfo, result.flowid);
            if (fd.isDeployed()) {
                refreshFlow(userInfo, result.flowid);
                notifyVersion(userInfo, result.flowid);
            }
        }
        
        return result.flowid;
    }

    
    private synchronized State insertOrUpdateFlow(UserInfoInterface userInfo,
            String file, String name, byte[] data, boolean forceCreate,
            boolean makeVersion, String comment) {
        // recorrer a um metodo privado para efectuar a actualizacao
        // propriamente dita.
        // Esse mesmo metodo sera usado pelo deploy no caso de ser necessario
        // actualizar o catalogo.
        
        State result = new State();
        Connection db = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        boolean flowFound = false;
        int flowid = -1;
        try {
            db = Utils.getDataSource().getConnection();
            db.setAutoCommit(false);
            
            String query = "select flowid,flowversion from flow where flowfile=? and organizationid=?";
            
            Logger.debug(userInfo.getUtilizador(), this, "insertOrUpdateFlow",
                    "Query1: " + query);
            pst = db.prepareStatement(query);
            pst.setString(1, file);
            pst.setString(2, userInfo.getOrganization());
            
            rs = pst.executeQuery();
            
            if (rs.next()) {
                flowFound = true;
                flowid = rs.getInt("flowid");
                result.version = rs.getInt("flowversion");
            }
            Logger.debug(userInfo.getUtilizador(), this, "insertOrUpdateFlow",
                    "Flow exists " + flowFound + "id: " + flowid + "; version "
                            + result.version);
            
            rs.close();
            pst.close();
            
            boolean copyToHistory = false;
            if (flowFound) {
                int arg = 0;
                query = "update flow set "
                        + (StringUtils.isNotEmpty(name) ? "flowname=?," : "")
                        + "flowdata=?,"
                        + (makeVersion ? "flowversion=flowversion+1," : "")
                        + "modified=? where flowid=?";
                Logger.debug(userInfo.getUtilizador(), this,
                        "insertOrUpdateFlow", "Query2a: " + query);
                pst = db.prepareStatement(query);
                if (StringUtils.isNotEmpty(name))
                    pst.setString(++arg, name);
                pst.setBinaryStream(++arg, new ByteArrayInputStream(data),
                        data.length);
                pst.setTimestamp(++arg, new Timestamp(System
                        .currentTimeMillis()));
                pst.setInt(++arg, flowid);
                int upd = pst.executeUpdate();
                pst.close();
                result.created = false;
                copyToHistory = (upd != 0);
            } else if (forceCreate) {
                if (null == name)
                    name = file;
                Timestamp now = new Timestamp(System.currentTimeMillis());
                query = DBQueryManager.getQuery("FlowHolder.INSERT_FLOW");
                Logger.debug(userInfo.getUtilizador(), this,
                        "insertOrUpdateFlow", "Query2b: " + query);
                pst = db.prepareStatement(query, new String[] { "flowid" });
                pst.setString(1, name);
                pst.setString(2, file);
                pst.setTimestamp(3, now);
                pst.setString(4, userInfo.getOrganization());
                pst.setBinaryStream(5, new ByteArrayInputStream(data),
                        data.length);
                pst.setTimestamp(6, now);
                pst.executeUpdate();
                rs = pst.getGeneratedKeys();
                if (rs.next()) {
                    result.version = 1;
                    result.created = true;
                    flowid = rs.getInt(1);
                    copyToHistory = true;
                }
                rs.close();
                pst.close();
                
                notifyNewFlow(userInfo, flowid);
                
            } else {
                throw new Exception("Cannot create flow.");
            }
            
            if (copyToHistory && makeVersion) {
                if (null != comment && comment.length() > MAX_COMMENT_SIZE)
                    comment = comment.substring(0, MAX_COMMENT_SIZE);
                query = DBQueryManager
                        .getQuery("FlowHolder.COPY_FLOW_TO_HISTORY");
                Logger.debug(userInfo.getUtilizador(), this,
                        "insertOrUpdateFlow", "Query3: " + query);
                pst = db.prepareStatement(query);
                pst.setString(1, comment);
                pst.setInt(2, flowid);
                pst.executeUpdate();
                pst.close();
                result.version++;
            }
            
            db.commit();
            result.success = true;
        } catch (Exception e) {
            try {
                db.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
            result.success = false;
        } finally {
            DatabaseInterface.closeResources(db, pst, rs);
        }
        
        result.flowid = flowid;
        
        return result;
    }
    
    public synchronized int writeSubFlowData(UserInfoInterface userInfo,
            String file, String name, byte[] data) {
        if (!userInfo.isOrgAdmin()) {
            Logger.debug(userInfo.getUtilizador(), this, "writeSubFlowData",
                    "No Adm privilege!!");
            return -1;
        }

        if (data == null){
          return -1;
        }

        if(existsFlowWithSameVariable(userInfo, name, data)){
          return -2;
        }

        State result = insertOrUpdateSubFlow(userInfo, file, name, data, true,
                false, null);
        
        if (result.success && !result.created) { // a subflow was updated.
            // redeploy deployed flows...
            Iterator<FlowData> iter = getCachedFlows(userInfo).iterator();
            while (iter.hasNext()) {
                IFlowData flow = (IFlowData) iter.next();
                if (flow.getOrganizationId().equals(userInfo.getOrganization())
                        && flow.isDeployed())
                    refreshFlow(userInfo, flow.getId());
            }
        }
        
        return result.flowid;
    }

    private boolean existsFlowWithSameVariable(UserInfoInterface userInfo, String subFlowName, byte[] subFlowData) {
      XmlFlow subFlow = null;
      try {
        subFlow = FlowMarshaller.unmarshal(subFlowData);
      } catch (Exception e1) {} 

      IFlowData[] allFlowData = this.listFlows(userInfo);
      for(IFlowData flowData: allFlowData){
        DBFlow dbFlow = readFlow(userInfo, flowData.getId());
        try {
          XmlFlow flow = FlowMarshaller.unmarshal(dbFlow.data);
          for(XmlBlock xmlBlock: flow.getXmlBlock()){
            if(xmlBlock.getType().equals("BlockSubFlow")){
              for(XmlAttribute xmlAttribute:xmlBlock.getXmlAttribute()){
                if(xmlAttribute.getName().equals("MNome do SubFluxo") && xmlAttribute.getValue().equals(subFlowName)){
                  for(XmlCatalogVarAttribute xmlCatalogueVar : flow.getXmlCatalogVars().getXmlCatalogVarAttribute()){
                    for(XmlCatalogVarAttribute xmlCatalogueVarSub : subFlow.getXmlCatalogVars().getXmlCatalogVarAttribute()){
                      if (xmlCatalogueVar.getName().equals(xmlCatalogueVarSub.getName())
                          && !isVarMappedToSubflow(xmlCatalogueVar.getName(), xmlCatalogueVarSub.getName(), xmlBlock)) {
                      Logger.error(userInfo.getUtilizador(), this, "uploadSubFlow", " exception caught: " + "In MainFlow"
                            + flow.getName() + " variables with the same name must be mapped one to the other! Main:"
                            + xmlCatalogueVar.getName() + " Sub:" + xmlCatalogueVarSub.getName());
                        return true;
                      }
                    }
                  }
                }
              }
            }
          }
        } catch (MarshalException e) {
          Logger.debug(userInfo.getUtilizador(), this, "writeSubFlowData",
              "MarshalException in flow " + flowData.getName());
        } catch (ValidationException e) {
          Logger.debug(userInfo.getUtilizador(), this, "writeSubFlowData",
              "ValidationException in flow " + flowData.getName());
        }
      }
      return false;
    }

  private boolean isVarMappedToSubflow(String flowVarName, String subFlowVarName, XmlBlock subFlowXmlBlock) {
    String[][] saInVars = SubFlowDataExpander.retrieveSubflowVarMappings(subFlowXmlBlock);

    for (int in = 0; in < saInVars.length; in++) {
      if (saInVars[in][SubFlowDataExpander.FLOW].equals(flowVarName)
          && saInVars[in][SubFlowDataExpander.SUBFLOW].equals(subFlowVarName))
        return true;
      else if (saInVars[in][SubFlowDataExpander.FLOW].equals("")
          && saInVars[in][SubFlowDataExpander.SUBFLOW].equals(subFlowVarName))
        return true;
    }

    return false;
  }

    public synchronized int writeSubFlowData(UserInfoInterface userInfo,
            String file, String name, byte[] data, String comment) {
        if (!userInfo.isOrgAdmin()) {
            Logger.debug(userInfo.getUtilizador(), this, "writeSubFlowData",
                    "No Adm privilege!!");
            return -1;
        }

        if (data == null){
          return -1;
        }

        if(existsFlowWithSameVariable(userInfo, name, data)){
          return -2;
        }

        State result = insertOrUpdateSubFlow(userInfo, file, name, data, true,
                true, comment);
        
        if (result.success && !result.created) { // a subflow was updated.
            // redeploy deployed flows...
            Iterator<FlowData> iter = getCachedFlows(userInfo).iterator();
            while (iter.hasNext()) {
                IFlowData flow = (IFlowData) iter.next();
                if (flow.getOrganizationId().equals(userInfo.getOrganization())
                        && flow.isDeployed())
                    refreshFlow(userInfo, flow.getId());
            }
        }
        
        return result.flowid;
    }
    
    private synchronized State insertOrUpdateSubFlow(
            UserInfoInterface userInfo, String file, String name, byte[] data,
            boolean forceCreate, boolean makeVersion, String comment) {
        // recorrer a um metodo privado para efectuar a actualizacao
        // propriamente dita.
        // Esse mesmo metodo sera usado pelo deploy no caso de ser necessario
        // actualizar o catalogo.
        
        State result = new State();
        Connection db = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        boolean flowFound = false;
        int flowid = -1;
        try {
            db = Utils.getDataSource().getConnection();
            db.setAutoCommit(false);
            
            String query = "select flowid,flowversion from sub_flow where flowfile=? and organizationid=?";
            Logger.debug(userInfo.getUtilizador(), this,
                    "insertOrUpdateSubFlow", "Query1: " + query);
            pst = db.prepareStatement(query);
            pst.setString(1, file);
            pst.setString(2, userInfo.getOrganization());
            
            rs = pst.executeQuery();
            
            if (rs.next()) {
                flowFound = true;
                flowid = rs.getInt("flowid");
                result.version = rs.getInt("flowversion");
            }
            
            rs.close();
            pst.close();
            
            boolean copyToHistory = false;
            if (flowFound) {
                query = "update sub_flow set flowdata=?,"
                        + (makeVersion ? "flowversion=flowversion+1," : "")
                        + "modified=? where flowid=?";
                Logger.debug(userInfo.getUtilizador(), this,
                        "insertOrUpdateSubFlow", "Query2a: " + query);
                pst = db.prepareStatement(query);
                pst.setBinaryStream(1, new ByteArrayInputStream(data),
                        data.length);
                pst.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                pst.setInt(3, flowid);
                int upd = pst.executeUpdate();
                pst.close();
                result.created = false;
                copyToHistory = (upd != 0);
            } else if (forceCreate) {
                if (null == name)
                    name = file;
                Timestamp now = new Timestamp(System.currentTimeMillis());
                query = DBQueryManager.getQuery("FlowHolder.INSERT_SUBFLOW");
                Logger.debug(userInfo.getUtilizador(), this,
                        "insertOrUpdateSubFlow", "Query2b: " + query);
                pst = db.prepareStatement(query, new String[] { "flowid" });
                pst.setString(1, name);
                pst.setString(2, file);
                pst.setTimestamp(3, now);
                pst.setString(4, userInfo.getOrganization());
                pst.setBinaryStream(5, new ByteArrayInputStream(data),
                        data.length);
                pst.setTimestamp(6, now);
                pst.executeUpdate();
                rs = pst.getGeneratedKeys();
                if (rs.next()) {
                    result.created = true;
                    flowid = rs.getInt(1);
                    copyToHistory = true;
                }
                rs.close();
                pst.close();
            } else {
                throw new Exception("Cannot create sub flow.");
            }
            
            // Copy to flow history.
            if (copyToHistory && makeVersion) {
                if (null != comment && comment.length() > MAX_COMMENT_SIZE)
                    comment = comment.substring(0, MAX_COMMENT_SIZE);
                query = DBQueryManager
                        .getQuery("FlowHolder.COPY_SUB_FLOW_TO_HISTORY");
                Logger.debug(userInfo.getUtilizador(), this,
                        "insertOrUpdateSubFlow", "Query3: " + query);
                pst = db.prepareStatement(query);
                pst.setString(1, comment);
                pst.setInt(2, flowid);
                pst.executeUpdate();
                pst.close();
                result.version++;
            }
            
            db.commit();
            result.success = true;
        } catch (Exception e) {
            try {
                db.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
            result.success = false;
        } finally {
            DatabaseInterface.closeResources(db, pst, rs);
        }
        
        result.flowid = flowid;
        
        return result;
    }
    
    /**
     * Return a flow from the flow cache or create a new one if necessary.
     * 
     * @param userInfo
     * @param flowId
     * @return
     */
    public synchronized FlowData getFlow(UserInfoInterface userInfo, int flowId) {
        return getFlow(userInfo, flowId, true);
    }
    
    /**
     * Return a flow from the flow cache or create a new one if necessary.
     * 
     * @param userInfo
     * @param flowId
     * @return
     */
    public synchronized FlowData getFlow(UserInfoInterface userInfo,
            int flowId, boolean create) {
        FlowData fd = null;
        
        if (hasCachedFlow(userInfo, flowId)) {
            fd = getCachedFlow(userInfo, flowId);
            if (!create)
                return fd;
            else {
                if (fd.isDeployed())
                    return fd; // if it is deployed, return.
            }
        }
        
        try {
            fd = buildFlowData(userInfo, flowId, create);
        } catch (FlowSecurityException e) {
            Logger.error(userInfo.getUtilizador(), this, "getFlow",
                    "Error building flow id = " + flowId
                            + " returning customized flowdata");
            fd = e.getFlowData();
        } catch (Throwable t) {
          t.printStackTrace();
        }
        if (null == fd) {
            Logger.error(userInfo.getUtilizador(), this, "getFlow",
                    "Build a null flow for flow id = " + flowId);
        } else {
            setCachedFlow(userInfo, fd);
        }
        
        return fd;
    }
    
    private synchronized FlowData buildFlowData(UserInfoInterface userInfo,
            int flowId, boolean deploy) throws FlowSecurityException {
        // TODO externalize this
//        Messages msg = Messages.getInstance(BeanFactory.getSettingsBean()
//                .getOrganizationLocale(userInfo.getOrganization()));
        
        String fileName = null;
        XmlFlow flow = null;
        FlowData fd = null;
        LicenseService licService = LicenseServiceFactory.getLicenseService();
        try {
            // first check if timewait period has already passed
            if (deploy && buildDateNotOk(userInfo, flowId)) {
            	Logger.error(userInfo.getUtilizador(),
            			this,
            			"buildFlow",
            			"Trying to build flow "
            			+ flowId
            			+ " during timewait build period. returning...");
            	return null;
            }
            
            DBFlow dbFlow = readFlow(userInfo, flowId);
            
            if (null == dbFlow) {
            	Logger.error(userInfo.getUtilizador(),
            			this,
            			"buildFlow",
            			"ReadFlow return null for flowid " +
            			flowId);
            	return null;
            }
            
            fileName = dbFlow.file;
            
            if (deploy && dbFlow.online) { 
              // Se o fluxo esta offline nao ha problema.
              boolean flowsOk = false;
              try {
                licService.canInstantiateFlows(userInfo, getCachedFlows(userInfo).size());
                flowsOk = true;
              } catch (LicenseServiceException e) {
                Logger.debug(userInfo.getUtilizador(), this, "buildFlow", "a LicenseServiceException was captured.");
              }

              if (!flowsOk) { // FIXME está errado
                Logger.warning(userInfo.getUtilizador(), this, "buildFlow", "Maximum number of deployed flows exceeded");
                fd = new FlowData(flowId, dbFlow.name, dbFlow.file,
                    dbFlow.online, dbFlow.organizationId,
                    dbFlow.created, dbFlow.lastModified,
                    dbFlow.seriesId,
                    dbFlow.typeCode);
                fd.setError("Maximum number of deployed flows exceeded");
                throw new MaximumFlowsException(fd);
              } else {
                Logger.info(userInfo.getUtilizador(), this, "buildFlow", "Number of flows accepted. (" + getCachedFlows(userInfo).size() + ")");
              }
            }
            
            if (dbFlow.data.length > 0) {

              Logger.debug(userInfo.getUtilizador(), this, "buildFlow", "Instantiating flow " + fileName + " (ID=" + flowId  + ")");

              flow = FlowMarshaller.unmarshal(dbFlow.data);
              Logger.debug(userInfo.getUtilizador(), this, "buildFlow", "Flow name = " + flow.getName());
              Logger.debug(userInfo.getUtilizador(), this, "buildFlow", "Flow version = " + flow.getVersion());
              Logger.debug(userInfo.getUtilizador(), this, "buildFlow", "Flow author = " + flow.getAuthor());
              Logger.debug(userInfo.getUtilizador(), this, "buildFlow", "Flow description = " + flow.getDescription());
              Logger.debug(userInfo.getUtilizador(), this, "buildFlow", "Total Number of Blocks read = " + flow.getXmlBlockCount());

              //Resolve subflows inside the main flow
              SubFlowDataExpander subFlowDataExpander = new SubFlowDataExpander(flow,new Integer(flowId));
              List<SubFlowMapping>  subFlowBlockMappings = subFlowDataExpander.expandSubFlow(userInfo);
              
        // Resolve form templates
        FormTemplateResolver formTemplateResolver = new FormTemplateResolver(flow);
        flow = formTemplateResolver.resolveTemplates(userInfo);

              if (deploy) {
                XmlBlock block = null;
                for (int b = 0; b < flow.getXmlBlockCount(); b++) {
                  block = flow.getXmlBlock(b);
                  Logger.debug(userInfo.getUtilizador(), this, "buildFlow", "Block Name is " + block.getName());
                }
              }

              String description = flow.getDescription();
              // backward compatibility. Older versions use "--" as
              // description
              if (StringUtils.isEmpty(description)
                  || "--".equals(description))
                description = flow.getName();
              fd = new FlowData(userInfo, flowId, description, fileName,
                  dbFlow.created, dbFlow.lastModified, dbFlow.seriesId, dbFlow.typeCode,
                  flow, deploy);
              fd.setOrganizationId(dbFlow.organizationId);
              
              
              if (deploy) {

                reindexFlow(userInfo, fd);

                if (fd.hasError()) {
                  Logger.error(userInfo.getUtilizador(), this, "buildFlow", "FlowData object NOT Constructed");
                } else {
                  // verificar num blocos no fd.. se > que permitido, faz
                  // reset ao fd e lanca excepcao!
                  boolean blocksOk = false;
                  try {
                    licService.canInstantiateFlowBlocks(userInfo, fd.getFlow().size());
                    blocksOk = true;
                  } catch (LicenseServiceException e) {
                    Logger.debug(userInfo.getUtilizador(), this, "buildFlow", "a LicenseServiceException was captured.");
                  }

                  if (!blocksOk) {
                    Logger.warning(userInfo.getUtilizador(), this, "buildFlow", "Maximum number of flow blocks exceeded");
                    clearCachedFlow(userInfo, flowId);
                    fd = new FlowData(flowId, description, fileName,
                        dbFlow.online, dbFlow.organizationId,
                        dbFlow.created, dbFlow.lastModified,
                        dbFlow.seriesId, dbFlow.typeCode);
                    fd.setError("Maximum number of flow blocks exceeded");
                    throw new MaximumBlocksException(fd);
                  } else {
                    Logger.debug(userInfo.getUtilizador(), this, "buildFlow", "Number of blocks accepted. (" + fd.getFlow().size() + ")");
                  }

                  Logger.debug(userInfo.getUtilizador(), this, "buildFlow", "FlowData object Constructed");
                  fd.setOnline(dbFlow.online);

                  // now going for settings save
                  FlowSettings settingsBean = BeanFactory.getFlowSettingsBean();
                  FlowSetting[] fsa = settingsBean.getFlowSettings(userInfo, flowId);
                  settingsBean.saveFlowSettings(userInfo, fsa);

                  Logger.debug(userInfo.getUtilizador(), this, "buildFlow", "Settings saved");
                  //saving max blockID and subflow mapping if they exist - useful for auditing subflows
                  if (saveSubFlowExpansionResult(subFlowBlockMappings,subFlowDataExpander.findMaxblockId(), flowId, userInfo))
                	  resyncDeployedFlowWithSubFlow(userInfo, subFlowBlockMappings, flowId, fd);
                }
                setBuildDate(userInfo, flowId);
              }
            } else {
              // No data in flow...
              fd = new FlowData(flowId, dbFlow.name, dbFlow.file,
                  dbFlow.online, dbFlow.organizationId,
                  dbFlow.created, dbFlow.lastModified, dbFlow.seriesId, dbFlow.typeCode);
            }
        } catch (Exception e) {
          Logger.error(userInfo.getUtilizador(), this, "buildFlow", "exception caught: " + e.getMessage());
          if (e instanceof FlowSecurityException) {
            throw (FlowSecurityException) e;
          }
        }
        return fd;
    }

  /**
   * Resync the state of this flow if necessary due to changes in it's subflows, must be done after saveSubFlowExpansionResult
   * 
   * @param userInfo
   * @param procData
   * @param flowId
   * @param fd
   * @throws Exception
   */
  private void resyncDeployedFlowWithSubFlow(UserInfoInterface userInfo, List<SubFlowMapping> subFlowBlockMappings, int flowId,
      FlowData fd) throws Exception {

    if(subFlowBlockMappings.size() == 0) return;
    
    Connection db = null;
    PreparedStatement pst = null;
    Flow flowBean = BeanFactory.getFlowBean();

    try {
      db = Utils.getDataSource().getConnection();
      pst = db.prepareStatement("SELECT created FROM iflow.subflow_block_mapping s where flowname=? group by created order by created desc");
      pst.setString(1, subFlowBlockMappings.get(0).getMainFlowName());
      ResultSet rst = pst.executeQuery();
      
      rst.next();
      Timestamp lastMapping =  rst.getTimestamp(1);
      rst.next();
      Timestamp preiousMapping = rst.getTimestamp(1);
      
      pst = db.
    		  prepareStatement("SELECT O.mapped_blockid as oid, N.mapped_blockid as nid FROM " +
    				  			"(SELECT sub_flowname, original_blockid,mapped_blockid FROM iflow.subflow_block_mapping s where flowname=? and created=?) O " +
    				  			"left join " +
    				  			"(SELECT sub_flowname, original_blockid,mapped_blockid FROM iflow.subflow_block_mapping s where flowname=? and created=?) N " +    				  			
    				  			"on (O.original_blockid=N.original_blockid and O.sub_flowname=N.sub_flowname) order by oid desc"); 
      pst.setString(1, subFlowBlockMappings.get(0).getMainFlowName());
      pst.setTimestamp(2, preiousMapping);
      pst.setString(3, subFlowBlockMappings.get(0).getMainFlowName());
      pst.setTimestamp(4, lastMapping);
      rst = pst.executeQuery();
      
      while(rst.next())
    	  flowBean.resyncFlow(userInfo, flowId, rst.getInt(1), rst.getInt(2), true, fd);
      
    } catch (Exception e) {
      Logger.error(userInfo.getUtilizador(), this, "resyncDeployedFlowWithSubFlow", "exception caught", e);
      e.printStackTrace();
    } finally {
      DatabaseInterface.closeResources(db, pst);
    }
  }

  private Boolean saveSubFlowExpansionResult(List<SubFlowMapping> subFlowBlockMappings, Integer maxblockId, int flowId,
      UserInfoInterface userInfo) {
	  Boolean mappingsChanged = false;
    if(subFlowBlockMappings==null || subFlowBlockMappings.size() == 0) return mappingsChanged;
    
    Connection db = null;
    PreparedStatement pst = null;
    try {
      db = Utils.getDataSource().getConnection();
      pst = db.prepareStatement("UPDATE flow SET max_block_id = ? WHERE flowid=?");
      pst.setInt(1, maxblockId);
      pst.setInt(2, flowId);
      pst.execute();

      // check if mappings have changed before saving them
      
      pst = db
          .prepareStatement("select flowname, sub_flowname, original_blockid, mapped_blockid from subflow_block_mapping "
              + "bm where bm.flowname=? and bm.created=(select max(created) from iflow.subflow_block_mapping where flowname=?) order by id");
      pst.setString(1, subFlowBlockMappings.get(0).getMainFlowName());
      pst.setString(2, subFlowBlockMappings.get(0).getMainFlowName());
      ResultSet rs = pst.executeQuery();

      for (SubFlowMapping subFlowMapping : subFlowBlockMappings) {
        if (!rs.next()) {
          mappingsChanged = true;
          break;
        }
        if (!rs.getString(2).equals(subFlowMapping.getSubFlowName())
            || !rs.getString(3).equals("" + subFlowMapping.getOriginalBlockId())
            || !rs.getString(4).equals("" + subFlowMapping.getMappedBlockId()))
          mappingsChanged = true;
      }
      if (rs.next())
        mappingsChanged = true;

      if (mappingsChanged) {
        Timestamp d = new Timestamp(new Date().getTime());
        for (SubFlowMapping subFlowMapping : subFlowBlockMappings) {
          pst = db
              .prepareStatement("INSERT INTO subflow_block_mapping (created,flowname, sub_flowname, original_blockid, mapped_blockid) VALUES(?,?,?,?,?)");
          pst.setTimestamp(1, d);
          pst.setString(2, subFlowMapping.getMainFlowName());
          pst.setString(3, subFlowMapping.getSubFlowName());
          pst.setInt(4, subFlowMapping.getOriginalBlockId());
          pst.setInt(5, subFlowMapping.getMappedBlockId());
          pst.execute();
        }
      }
      return mappingsChanged;
    } catch (Exception e) {
      Logger.error(userInfo.getUtilizador(), this, "readFlow", "exception caught", e);
      e.printStackTrace();
    } finally {
      DatabaseInterface.closeResources(db, pst);
    }
	return mappingsChanged;
  }

    private DBFlow readFlow(UserInfoInterface userInfo, int flowId) {
        Connection db = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        DBFlow flow = null;
        try {
            db = Utils.getDataSource().getConnection();
            pst = db.prepareStatement("SELECT * FROM flow WHERE flowid=?");
            // 1st read user flow
            pst.setInt(1, flowId);
            rs = pst.executeQuery();
            if (rs.next()) {
              // flow exists
              if (userInfo.isSysAdmin() ||
                  rs.getString("organizationid").equals(userInfo.getOrganization())
                  || BeanFactory.getFlowSettingsBean().isGuestAccessible(userInfo, flowId)) {
                flow = new DBFlow();
                flow.flowid = rs.getInt("flowid");
                flow.organizationId = rs.getString("organizationid"); // use db orgid, since sysadmin can perform actions with flows
                flow.file = rs.getString("flowfile");
                flow.name = rs.getString("flowname");
                flow.online = rs.getBoolean("enabled");
                InputStream is = rs.getBinaryStream("flowdata");
                flow.data = getBytes(is);
                flow.created = rs.getTimestamp("created").getTime();
                flow.lastModified = rs.getTimestamp("modified").getTime();
                for (int i = 0; i < Const.INDEX_COLUMN_COUNT; i++)
                  flow.indexFields[i] = rs.getString("name_idx" + i);
                flow.seriesId = rs.getInt("seriesid");
                flow.typeCode = rs.getString("type_code");
                
                is.close();
              }
            }
            rs.close();
        } catch (Exception e) {
          Logger.error(userInfo.getUtilizador(), this, "readFlow", "exception caught", e);  
          e.printStackTrace();
        } finally {
            DatabaseInterface.closeResources(db, pst, rs);
        }
        return flow;
    }
    
    /**
     * Reread flow data from DB
     * 
     * @param userInfo
     * @param anFlowId
     * @return
     */
    public synchronized FlowData refreshFlow(UserInfoInterface userInfo, int flowId) {
        clearCachedFlow(userInfo, flowId);
        return getFlow(userInfo, flowId);
    }
    
    /**
     * Instantiate a new Flow from FlowData. <br>
     * A new flow will be created from a template if necessary.
     * 
     * @param userInfo
     * @param fileName
     *            flow file name
     * @return null if OK, otherwise error occurred
     */
    public synchronized String deployFlow(UserInfoInterface userInfo, String fileName) {
        String retObj = null;
        int flowId = -1;
        
        String sLogin = userInfo.getUtilizador();
        
        Logger.trace(this, "deployFlow", sLogin + " call for file " + fileName);
        
        Connection db = null;
        Statement st = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        boolean found = false;
        try {
            db = Utils.getDataSource().getConnection();
            db.setAutoCommit(false);
            
            pst = db.prepareStatement("select flowid from flow where flowfile=? and organizationid=?");
            pst.setString(1, fileName);
            pst.setString(2, userInfo.getOrganization());
            rs = pst.executeQuery();
            
            if (rs.next()) {
                found = true;
                flowId = rs.getInt("flowid");
            }
            rs.close();
            pst.close();
            
            if (!found) {
                Timestamp now = new Timestamp(System.currentTimeMillis());
                pst = db.prepareStatement(DBQueryManager.getQuery("FlowHolder.COPY_FLOW_TEMPLATE"),
                        new String[] { "flowid" });
                pst.setTimestamp(1, now);
                pst.setString(2, userInfo.getOrganization());
                pst.setTimestamp(3, now);
                pst.setString(4, fileName);
                
                int updated = pst.executeUpdate();
                
                if (updated == 0)
                    throw new Exception("Could not copy flow template");
                
                rs = pst.getGeneratedKeys();
                if (rs.next()) {
                    flowId = rs.getInt(1);
                    found = true;
                } else {
                    throw new Exception("Could not get new flow id from template copy");
                }
                rs.close();
                pst.close();
                
            }
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            retObj = "O fluxo n&atilde;o foi encontrado: \"" + e.getMessage()
                    + "\"";
            Logger.error(sLogin, this, "deployFlow", retObj);
        } finally {
            DatabaseInterface.closeResources(db, pst, rs);
            db = null;
            pst = null;
            rs = null;
        }
        
        if (null != retObj)
            return retObj;
        if (!found)
            return "O fluxo n&atilde;o existe ou n&atilde;o est&aacute; dispon&iacute;vel";
        
        FlowData flow = refreshFlow(userInfo, flowId);
        // if(null == flow) return "Fluxo invalido.";
        boolean online = flow != null && flow.isDeployed();
        flow.setOnline(online);
        notifyDeploy(userInfo, flowId, online);
        try {
            db = Utils.getDataSource().getConnection();
            st = db.createStatement();
            st.executeUpdate("update flow set enabled=" + (online ? 1 : 0)
                    + " where flowid=" + flowId);
        } catch (Exception e) {
            e.printStackTrace();
            retObj = "N&atilde;o foi poss&iacute;vel actualizar o estado do fluxo.";
            Logger.error(sLogin, this, "deployFlow", retObj);
        } finally {
            DatabaseInterface.closeResources(db, st, rs);
            db = null;
            st = null;
            rs = null;
        }
                
        if (flow == null) {
            retObj = "Invalid flow"; // fd can be null at this point
        } else if (flow.hasError()) {
            retObj = flow.getError();
        }
        
        Logger.debug(sLogin, this, "deployFlow", fileName + " flow commited");
        return retObj;
    }
    
    /**
     * Free flow resources and mark it as offline.
     * 
     * @param userInfo
     * @param fileName
     * @return
     */
    public synchronized String undeployFlow(UserInfoInterface userInfo,
            String fileName) {
        
        String retObj = null;
        int flowId = -1;
        
        String sLogin = userInfo.getUtilizador();
        
        Logger.trace(this, "undeployFlow", sLogin + " call for file "
                + fileName);
        
        Connection db = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            db = Utils.getDataSource().getConnection();
            db.setAutoCommit(false);
            
            pst = db.prepareStatement("select flowid from flow where flowfile=? and organizationid=?");
            pst.setString(1, fileName);
            pst.setString(2, userInfo.getOrganization());
            rs = pst.executeQuery();
            
            if (rs.next()) {
                flowId = rs.getInt("flowid");
            }
            rs.close();
            pst.close();
            
            clearCachedFlow(userInfo, flowId);
            
            pst = db.prepareStatement("update flow set enabled=0 where flowfile=? and organizationid=?");
            pst.setString(1, fileName);
            pst.setString(2, userInfo.getOrganization());
            pst.executeUpdate();
            pst.close();
            
            db.commit();
            
            notifyDeploy(userInfo, flowId, false);
            
        } catch (Exception e) {
            e.printStackTrace();
            retObj = "N&atilde;o foi poss&iacute;vel actualizar o estado do fluxo.";
            Logger.error(sLogin, this, "deployFlow", retObj);
        } finally {
            DatabaseInterface.closeResources(db, pst, rs);
        }
        
        return retObj;
    }
    
    /**
     * Remove a flow from database
     * 
     * @param userInfo
     * @param asFile
     * @param abProcs
     *            If true, remove processes
     * @return
     */
    public boolean deleteFlow(UserInfoInterface userInfo, String asFile,
            boolean abProcs) {
        if (!userInfo.isOrgAdmin()) {
            Logger.debug(userInfo.getUtilizador(), this, "updateFlowData",
                    "No Adm privilege!!");
            // return "No Adm privilege!!";
            return false;
        }
        
        boolean retObj = false;
        
        String userid = userInfo.getUtilizador();
        
        Logger.trace(this, "deleteFlow", userid + " call with asFile=" + asFile
                + ",abProcs=" + abProcs);
        
        // se abProcs, apagar flow e estruturas e processos associados;
        // se !abProcs, mover processos abertos para historico e fazer disable
        // do flow
        // mover flow (xml file) do repositorio de offline para deleted
        // verificar se e preciso apagar o flow da cache do flowholder ou de
        // outra cache qualquer.
        
        DataSource ds = null;
        Connection db = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        
        int nFlowId = -1;
        
        try {
            
            ds = Utils.getDataSource();
            db = ds.getConnection();
            db.setAutoCommit(false);
            
            pst = db.prepareStatement("select flowid,enabled from flow where flowfile=? and organizationid=?");
            pst.setString(1, asFile);
            pst.setString(2, userInfo.getOrganization());
            
            rs = pst.executeQuery();
            
            boolean exists = false;
            boolean online = false;
            if (rs.next()) {
                exists = true;
                // flow already exists
                nFlowId = rs.getInt("flowid");
                online = rs.getBoolean("enabled");
            }
            rs.close();
            pst.close();
            rs = null;
            pst = null;
            
            if (!exists) {
                throw new Exception("No Flow for FlowFile=" + asFile);
            }
            if (online) {
                throw new Exception("Flow " + asFile + "is online!");
            }
            
            pst = db.prepareStatement("call deleteFlow(?,?,?)");
            pst.setString(1, userid);
            pst.setInt(2, nFlowId);
            pst.setInt(3, abProcs ? 1 : 0);
            
            pst.executeUpdate();
            
            pst.close();
            
            // db handled.. now handle repository
            db.commit();
            retObj = true;
        } catch (Exception e) {
            try {
                if (db != null)
                    db.rollback();
            } catch (Exception e1) {
            }
            Logger.error(userid, this, "deleteFlow", "exception caught: "
                    + e.getMessage());
            retObj = false;
        } finally {
            DatabaseInterface.closeResources(db, pst, rs);
        }
        
        return retObj;
    }
    
    // checks if flow is in cache (cached flow may contain errors.. use flow
    // data's hasError method)
    private synchronized boolean hasCachedFlow(UserInfoInterface userInfo,
            int anFlowId) {
        Integer iId = new Integer(anFlowId);
        String org = userInfo.getOrganization();
        if (!_hmFlowData.containsKey(org))
            return false;
        Map<Integer, FlowData> orgFlowData = _hmFlowData.get(org);
        
        return orgFlowData != null && orgFlowData.containsKey(iId)
                && orgFlowData.get(iId) != null;
    }
    
    private synchronized void clearCachedFlow(UserInfoInterface userInfo, int flowId) {
        Integer iId = new Integer(flowId);
        String org = userInfo.getOrganization();
        if (_hmFlowData.containsKey(org)) {
            Map<Integer, FlowData> orgFlowData = _hmFlowData.get(org);
            if (null != orgFlowData) {
                FlowData fd = orgFlowData.remove(iId);
                if (fd != null) {
                  fd.setOnline(false);
                }
            }
        }
        _hmFlowBuildDate.remove(iId);
        
    }
    
    private synchronized FlowData getCachedFlow(UserInfoInterface userInfo,
            int flowId) {
        FlowData fd = null;
        String org = userInfo.getOrganization();
        if (_hmFlowData.containsKey(org)) {
            Map<Integer, FlowData> orgFlowData = _hmFlowData.get(org);
            if (null != orgFlowData)
                fd = (FlowData) orgFlowData.get(new Integer(flowId));
        }
        return fd;
    }
    
    private synchronized void setCachedFlow(UserInfoInterface userInfo,
            FlowData flowData) {
        String org = userInfo.getOrganization();
        if (!_hmFlowData.containsKey(org) || _hmFlowData.get(org) == null) {
            _hmFlowData.put(org, new HashMap<Integer, FlowData>());
        }
        Map<Integer, FlowData> orgFlowData = _hmFlowData.get(org);
        orgFlowData.put(new Integer(flowData.getId()), flowData);
    }
    
    private synchronized Collection<FlowData> getCachedFlows(
            UserInfoInterface userInfo) {
        Collection<FlowData> coll = new ArrayList<FlowData>();
        String org = userInfo.getOrganization();
        if (_hmFlowData.containsKey(org)) {
            Map<Integer, FlowData> orgFlowData = _hmFlowData.get(org);
            if (null != orgFlowData)
                coll = orgFlowData.values();
        }
        return coll;
    }
    
    private synchronized void setBuildDate(UserInfoInterface userInfo,
            int flowId) {
        _hmFlowBuildDate.put(new Integer(flowId), new Date());
    }
    
    private synchronized boolean buildDateNotOk(UserInfoInterface userInfo,
            int flowId) {
        boolean notok = false;
        Integer iId = new Integer(flowId);
        if (_hmFlowBuildDate.containsKey(iId)) {
            Date dtOld = _hmFlowBuildDate.get(iId);
            Date dtNow = new Date();
            
            long ltmp = dtOld.getTime() + BUILD_WAIT;
            
            notok = ltmp > dtNow.getTime();
        }
        
        return notok;
    }
    
    public static class FlowSecurityException extends Exception {
        /**
   * 
   */
        private static final long serialVersionUID = -3027197719077289993L;
        FlowData flow;
        
        public FlowSecurityException(FlowData flow) {
            this.flow = flow;
        }
        
        public FlowSecurityException() {
            // TODO Auto-generated constructor stub
        }
        
        public FlowData getFlowData() {
            return flow;
        }
        
    }
    
    public static class MaximumFlowsException extends FlowSecurityException {
        
        /**
     * 
     */
        private static final long serialVersionUID = -8581734366970381600L;
        
        public MaximumFlowsException(FlowData flow) {
            super(flow);
        }
        
        public String getMessage() {
            return "Numero maximo de flows atingido.";
        }
    }
    
    public static class MaximumBlocksException extends FlowSecurityException {
        
        /**
     * 
     */
        private static final long serialVersionUID = -3826269373077991692L;
        
        public MaximumBlocksException(FlowData flow) {
            super(flow);
        }
        
        /**
     * 
     */
        
        public String getMessage() {
            return "Numero maximo de blocos atingido.";
        }
    }
    
    public IFlowData getFlow(UserInfoInterface userInfo, String fileName) {
        IFlowData retObj = null;
        
        DataSource ds = null;
        Connection db = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        
        try {
            
            ds = Utils.getDataSource();
            db = ds.getConnection();

            String sQuery = "select flowid,flowname,flowfile,enabled,organizationid,created,modified,seriesId,type_code,max_block_id from flow where organizationid=? and flowfile=?";

            st = db.prepareStatement(sQuery);
            st.setString(1, userInfo.getOrganization());
            st.setString(2, fileName);
            
            rs = st.executeQuery();
            if (rs.next()) {
                int flowId = rs.getInt("flowid");
                if (hasCachedFlow(userInfo, flowId)) {
                    retObj = getCachedFlow(userInfo, flowId);
                } else {
                    retObj = new FlowData(flowId,
                        rs.getString("flowname"),
                        rs.getString("flowfile"),
                        rs.getBoolean("enabled"),
                        rs.getString("organizationid"),
                        rs.getTimestamp("created").getTime(),
                        rs.getTimestamp("modified").getTime(),
                        rs.getInt("seriesId"),
                        rs.getString("type_code"),
                        rs.getInt("max_block_id")
                    );
                }
            } // while
        } catch (Exception e) {
            Logger.error(userInfo.getUtilizador(), this, "listFlows",
                    "exception (" + e.getClass().getName() + ") caught: "
                            + e.getMessage());
        } finally {
            DatabaseInterface.closeResources(db, st, rs);
        }
        
        return retObj;
    }
    
    public synchronized FlowTemplate[] listFlowTemplates(
            UserInfoInterface userInfo) {
        FlowTemplate[] retObj = null;
        
        DataSource ds = null;
        Connection db = null;
        Statement st = null;
        ResultSet rs = null;
        
        try {
            
            ds = Utils.getDataSource();
            db = ds.getConnection();
            st = db.createStatement();
            rs = null;
            
            // return every disabled flowfile in orgnaizationid. The unioned
            // query will exclude all org flows so it will not list an online
            // flow as offline
            final String query = "select name,description from flow_template";
            
            rs = st.executeQuery(query);
            
            ArrayList<FlowTemplate> altmp = new ArrayList<FlowTemplate>();
            while (rs.next()) {
                altmp.add(new FlowTemplate(rs.getString("name"), rs
                        .getString("description")));
            } // while
            
            retObj = altmp.toArray(new FlowTemplate[altmp.size()]);
        } catch (Exception e) {
            Logger.error(userInfo.getUtilizador(), this, "listFlowNames",
                    "exception (" + e.getClass().getName() + ") caught: "
                            + e.getMessage());
        } finally {
            DatabaseInterface.closeResources(db, st, rs);
        }
        
        return retObj;
    }
    
    public synchronized byte[] readTemplateData(UserInfoInterface userInfo,
            String name) {
        Connection db = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        byte[] data = null;
        try {
            db = Utils.getDataSource().getConnection();
            
            // copy from template
            st = db.prepareStatement("select data FROM flow_template where name=?");
            st.setString(1, name);
            rs = st.executeQuery();
            if (rs.next()) {
                InputStream is = rs.getBinaryStream("data");
                data = getBytes(is);
            }
            
        } catch (Exception e) {
            try {
                db.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            DatabaseInterface.closeResources(db, st, rs);
        }
        
        return data;
        
    }
    
    /**
     * Remove a flow template from database
     * 
     * @param userInfo
     * @param asFile
     * @return
     */
    public boolean deleteFlowTemplate(UserInfoInterface userInfo, String name) {
        if (!userInfo.isSysAdmin()) {
            Logger.error(userInfo.getUtilizador(), this, "deleteFlowTemplate",
                    "User is not System Admin.");
            return false;
        }
        
        Connection db = null;
        PreparedStatement st = null;
        boolean result = false;
        try {
            db = Utils.getDataSource().getConnection();
            
            // copy from template
            st = db.prepareStatement("delete FROM flow_template where name=?");
            st.setString(1, name);
            result = (st.executeUpdate() == 1);
        } catch (Exception e) {
            try {
                db.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            DatabaseInterface.closeResources(db, st);
        }
        
        return result;
    }
    
    /**
     * Upload a flow template to database
     * 
     * @param userInfo
     * @param name
     * @param description
     * @param data
     * @return
     */
    public boolean uploadFlowTemplate(UserInfoInterface userInfo, String name,
            String description, byte[] data) {
        if (!userInfo.isSysAdmin()) {
            Logger.error(userInfo.getUtilizador(), this, "uploadFlowTemplate",
                    "User is not System Admin.");
            return false;
        }
        
        if (null == data) {
            Logger.error(userInfo.getUtilizador(), this, "uploadFlowTemplate",
                    "Data is null");
            return false;
        }
        
        Connection db = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        boolean insert = true;
        boolean result = false;
        try {
            db = Utils.getDataSource().getConnection();
            db.setAutoCommit(false);
            
            // copy from template
            st = db.prepareStatement("select count(*) FROM flow_template where name=?");
            st.setString(1, name);
            rs = st.executeQuery();
            if (rs.next()) {
                insert = (rs.getInt(1) == 0);
            }
            rs.close();
            st.close();
            
            if (insert) {
                st = db.prepareStatement("insert into flow_template (description,data,name) values (?,?,?)");
            } else {
                st = db.prepareStatement("update flow_template set description = ?, data = ? where name = ?");
            }
            st.setString(1, description);
            ByteArrayInputStream bin = new ByteArrayInputStream(data);
            st.setBinaryStream(2, bin, data.length);
            st.setString(3, name);
            result = (st.executeUpdate() == 1);
            
            db.commit();
        } catch (Exception e) {
            try {
                db.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
            result = false;
        } finally {
            DatabaseInterface.closeResources(db, st, rs);
        }
        
        return result;
    }
    
    /**
     * Get the XML contents of the named flow version.
     * 
     * <br>
     * <em>This method will not create a new flow!</em>
     * 
     * @param userInfo
     * @param name
     *            file name
     * @return XML bytes
     */
    public synchronized byte[] readFlowData(UserInfoInterface userInfo,
            String name, int version) {
        return readData(userInfo, name, true, version);
    }
    
    /**
     * Get the XML contents of the named sub flow version.
     * 
     * <br>
     * <em>This method will not create a new sub flow!</em>
     * 
     * @param userInfo
     * @param name
     *            file name
     * @return XML bytes
     */
    public synchronized byte[] readSubFlowData(UserInfoInterface userInfo,
            String name, int version) {
        return readData(userInfo, name, false, version);
    }
    
    private synchronized byte[] readData(UserInfoInterface userInfo,
            String name, boolean isFlow, int version) {
        String query = null;
        Connection db = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        byte[] data = null;
        String sub = isFlow ? "" : "sub_";
        try {
            db = Utils.getDataSource().getConnection();
            if (version < 0) {
                query = "select flowdata from " + sub
                        + "flow where flowfile=? and organizationid=?";
            } else {
                query = "select h.data as flowdata from "
                        + sub
                        + "flow f, "
                        + sub
                        + "flow_history h where f.flowfile=? and f.organizationid=? and f.flowid=h.flowid and h.flowversion=?";
            }
            
            Logger.debug(userInfo.getUtilizador(), this, "readData",
                    "Executing query: " + query);
            Logger.debug(userInfo.getUtilizador(), this, "readData",
                    "Query params: name=" + name + "; organizationid="
                            + userInfo.getOrganization() + "; version="
                            + version);
            st = db.prepareStatement(query);
            st.setString(1, name);
            st.setString(2, userInfo.getOrganization());
            if (version >= 0)
                st.setInt(3, version);
            
            rs = st.executeQuery();
            
            boolean flowFound = false;
            if (rs.next()) {
                flowFound = true;
                InputStream is = rs.getBinaryStream(1);
                data = getBytes(is);
                Logger.debug(userInfo.getUtilizador(), this, "readData",
                        "Flow found. Bytes: " + data.length);
            }
            
            rs.close();
            st.close();
            
            if (!flowFound && version < 0) {
                Logger.debug(userInfo.getUtilizador(), this, "readData",
                        "Flow not found. Searching templates.");
                // copy from template
                query = "select data FROM " + sub
                        + "flow_template where name=?";
                Logger.debug(userInfo.getUtilizador(), this, "readData",
                        "Executing query: " + query);
                Logger.debug(userInfo.getUtilizador(), this, "readData",
                        "Query params: name=" + name);
                st = db.prepareStatement(query);
                st.setString(1, name);
                rs = st.executeQuery();
                if (rs.next()) {
                    InputStream is = rs.getBinaryStream(1);
                    data = getBytes(is);
                    Logger.debug(userInfo.getUtilizador(), this, "readData",
                            "Template found. Bytes: " + data.length);
                }
            }
            
        } catch (Exception e) {
            Logger.error(userInfo.getUtilizador(), this, "readData",
                    "Exception occured reading flow data.", e);
        } finally {
            DatabaseInterface.closeResources(db, st, rs);
        }
        
        return data;
    }
    
    public String getFlowComment(UserInfoInterface userInfo, String name,
            int version) {
        return getFlowVersionComment(userInfo, name, version, true);
    }
    
    public String getSubFlowComment(UserInfoInterface userInfo, String name,
            int version) {
        return getFlowVersionComment(userInfo, name, version, false);
    }
    
    public String[] getFlowVersions(UserInfoInterface userInfo, String name) {
        return getFlowVersions(userInfo, name, true);
    }
    
    public String[] getSubFlowVersions(UserInfoInterface userInfo, String name) {
        return getFlowVersions(userInfo, name, false);
    }
    
    private String[] getFlowVersions(UserInfoInterface userInfo, String name,
            boolean isFlow) {
        Connection db = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        final int strSize = 20;
        String sub = isFlow ? "" : "sub_";
        ArrayList<String> versions = new ArrayList<String>();
        try {
            db = Utils.getDataSource().getConnection();
            String query = DBQueryManager.processQuery(
                    "FlowHolder.LIST_FLOW_VERSIONS", new Object[] { sub });
            
            Logger.debug(userInfo.getUtilizador(), this, "getFlowVersions",
                    "Executing query: " + query);
            Logger.debug(userInfo.getUtilizador(), this, "getFlowVersions",
                    "Query params: name=" + name + "; organizationid="
                            + userInfo.getOrganization());
            st = db.prepareStatement(query);
            st.setString(1, name);
            st.setString(2, userInfo.getOrganization());
            
            rs = st.executeQuery();
            
            while (rs.next()) {
                int version = rs.getInt(1);
                Timestamp ts = rs.getTimestamp(2);
                String comment = rs.getString(3);
                if (null == comment)
                    comment = "";
                else if (comment.length() > strSize)
                    comment = comment.substring(0, strSize - 3) + "...";
                String versionLine = version + ";"
                        + DateUtility.formatTimestamp(userInfo, ts) + " - "
                        + comment;
                versions.add(versionLine);
                Logger.debug(userInfo.getUtilizador(), this, "getFlowVersions",
                        "Version line: " + versionLine);
            }
            Logger.debug(userInfo.getUtilizador(), this, "getFlowVersions",
                    "Flow found. Bytes: " + versions.size());
            
            rs.close();
            st.close();
            
        } catch (Exception e) {
            Logger.error(userInfo.getUtilizador(), this, "getFlowVersions",
                    "Exception occured reading flow data.", e);
        } finally {
            DatabaseInterface.closeResources(db, st, rs);
        }
        
        return versions.toArray(new String[versions.size()]);
    }
    
    private String getFlowVersionComment(UserInfoInterface userInfo,
            String name, int version, boolean isFlow) {
        if (version < 0) {
            return "";
        }
        Connection db = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        String sub = isFlow ? "" : "sub_";
        String comment = "";
        try {
            db = Utils.getDataSource().getConnection();
            String query = DBQueryManager.processQuery(
                    "FlowHolder.FLOW_COMMENT", new Object[] { sub });
            
            Logger.debug(userInfo.getUtilizador(), this,
                    "getFlowVersionComment", "Executing query: " + query);
            Logger.debug(userInfo.getUtilizador(), this,
                    "getFlowVersionComment", "Query params: name=" + name
                            + "; organizationid=" + userInfo.getOrganization()
                            + "; version=" + version);
            st = db.prepareStatement(query);
            st.setString(1, name);
            st.setString(2, userInfo.getOrganization());
            st.setInt(3, version);
            
            rs = st.executeQuery();
            
            if (rs.next()) {
                comment = rs.getString(1);
                Logger.debug(userInfo.getUtilizador(), this,
                        "getFlowVersionComment", "Comment found: " + comment);
            }
            
            rs.close();
            st.close();
            
        } catch (Exception e) {
            Logger.error(userInfo.getUtilizador(), this,
                    "getFlowVersionComment",
                    "Exception occured reading flow comment.", e);
        } finally {
            DatabaseInterface.closeResources(db, st, rs);
        }
        
        return comment;
    }
    
    private synchronized void reindexFlow(UserInfoInterface userInfo,
            IFlowData flow) {
        int flowid = flow.getId();
        
        DataSource ds = null;
        Connection db = null;
        Statement st = null;
        ResultSet rs = null;
        Object[] queryInfo = new Object[3]; // 0: flowid; 1: index position; 2:
        // var name
        queryInfo[0] = String.valueOf(flowid);
        
        Map<String, Integer> indexVar = flow.getIndexVars();
        // Map<String,String> varTypes = flow.getCatalogueVars();
        ProcessCatalogue catalog = flow.getCatalogue();
        try {
            ds = Utils.getDataSource();
            db = ds.getConnection();
            db.setAutoCommit(false);
            st = db.createStatement();
            String query = null;
            int nProcs = 0;
            
            // reset index
            for (String var : indexVar.keySet()) {
                int pos = indexVar.get(var);
                String escapedVar = StringEscapeUtils.escapeSql(var);
                String oldIdx = null;
                
                // get previous index
                query = "select name_idx" + pos + " from flow where flowid="
                        + flowid;
                Logger.debug(userInfo.getUtilizador(), this, "reindexFlow",
                        "Executing query: " + query);
                rs = st.executeQuery(query);
                if (rs.next())
                    oldIdx = rs.getString(1);
                Logger.debug(userInfo.getUtilizador(), this, "reindexFlow",
                        "Old index var: " + oldIdx);
                
                // if new var is empty or is the same as oldIdx, there is no
                // need to reindex
                if (StringUtils.isEmpty(var) || StringUtils.equals(oldIdx, var))
                    continue;
                
                if (catalog.isList(var)) {
                    Logger.warning(userInfo.getUtilizador(), this,
                            "reindexFlow", "Unindexable variable: '" + var
                                    + "'. skipping...");
                    continue;
                }
                
                queryInfo[1] = pos;
                queryInfo[2] = escapedVar;
                
                // update with new index
                query = "update flow set name_idx" + pos + "='" + escapedVar
                        + "' where flowid=" + flowid;
                Logger.debug(userInfo.getUtilizador(), this, "reindexFlow",
                        "Executing query: " + query);
                nProcs = st.executeUpdate(query);
                Logger.debug(userInfo.getUtilizador(), this, "reindexFlow",
                        "Set new index var: " + nProcs);
                
                // open processes
                query = "update process set idx" + pos + "=null where flowid="
                        + flowid;
                Logger.debug(userInfo.getUtilizador(), this, "reindexFlow",
                        "Executing query: " + query);
                nProcs = st.executeUpdate(query);
                Logger.debug(userInfo.getUtilizador(), this, "reindexFlow",
                        "Reset " + nProcs + " process index");
                
                query = DBQueryManager.processQuery("FlowHolder.REINDEX",
                        queryInfo);
                
                Logger.debug(userInfo.getUtilizador(), this, "reindexFlow",
                        "Executing query: " + query);
                nProcs = st.executeUpdate(query);
                Logger.debug(userInfo.getUtilizador(), this, "reindexFlow",
                        "Finished reindexing " + nProcs + " processes");
                
                // closed processes
                query = "update process_history set idx" + pos
                        + "=null where flowid=" + flowid + " and closed=1";
                Logger.debug(userInfo.getUtilizador(), this, "reindexFlow",
                        "Executing query: " + query);
                nProcs = st.executeUpdate(query);
                Logger.debug(userInfo.getUtilizador(), this, "reindexFlow",
                        "Reset " + nProcs + " closed process index");
                
                query = DBQueryManager.processQuery(
                        "FlowHolder.REINDEX_HISTORY", queryInfo);
                Logger.debug(userInfo.getUtilizador(), this, "reindexFlow",
                        "Executing query: " + query);
                nProcs = st.executeUpdate(query);
                Logger.debug(userInfo.getUtilizador(), this, "reindexFlow",
                        "Finished reindexing " + nProcs + " processes");
                
                db.commit();
            }
        } catch (SQLException e) {
            Logger.error(userInfo.getUtilizador(), this, "reindexFlow",
                    "Error reindexing process data.", e);
        } finally {
            // free db connections
            DatabaseInterface.closeResources(db, st, rs);
        }
    }
    
    public boolean updateFlowSeries(UserInfoInterface userInfo, int flowId,
            int seriesId) {
        int[] flowIds = new int[] { flowId };
        return updateFlowsSeries(userInfo, flowIds, seriesId);
    }
    
    public synchronized boolean updateFlowsSeries(UserInfoInterface userInfo,
            int[] flowIds, int seriesId) {
        
        DataSource ds = null;
        Connection db = null;
        PreparedStatement pst = null;
        
        try {
            ds = Utils.getDataSource();
            db = ds.getConnection();
            db.setAutoCommit(false);
            pst = db.prepareStatement("update flow set seriesid=? where flowid=?");
            
            pst.setInt(1, seriesId);
            
            for (int flowId : flowIds) {
                pst.setInt(2, flowId);
                
                Logger.debug(userInfo.getUtilizador(), this,
                        "updateFlowsSeries", "going to update flow " + flowId
                                + " with seriesId " + seriesId);
                int rows = pst.executeUpdate();
                if (rows != 1) {
                    throw new Exception("flow " + flowId
                            + " seriesId update returned " + rows
                            + "(should return only 1)");
                }
                Logger.debug(userInfo.getUtilizador(), this,
                        "updateFlowsSeries", "updated flow " + flowId
                                + " with seriesId " + seriesId);
            }
            
            db.commit();
            
            // refresh flows
            for (int flowId : flowIds) {
                refreshFlow(userInfo, flowId);
            }
            
            return true;
        } catch (Exception e) {
            try {
                if (db != null)
                    db.rollback();
            } catch (Exception ee) {
            }
            Logger.error(userInfo.getUtilizador(), this, "updateFlowSeries",
                    "Error updating flows with series " + seriesId, e);
            return false;
        } finally {
            // free db connections
            DatabaseInterface.closeResources(db, pst);
        }
    }
    
    public Collection<Integer> listFlowIds(UserInfoInterface userInfo) {
        
        if (null == userInfo || !userInfo.isSysAdmin())
            return null; // not authorized
            
        ArrayList<Integer> retObj = new ArrayList<Integer>();
        
        DataSource ds = null;
        Connection db = null;
        Statement st = null;
        ResultSet rs = null;
        
        try {
            
            ds = Utils.getDataSource();
            db = ds.getConnection();
            st = db.createStatement();
            rs = null;
            
            String sQuery = "select flowid from flow order by flowid";
            
            rs = st.executeQuery(sQuery);
            while (rs.next()) {
                retObj.add(rs.getInt("flowid"));
            } // while
        } catch (Exception e) {
            Logger.error(userInfo.getUtilizador(), this, "listFlowIds",
                    "exception (" + e.getClass().getName() + ") caught: "
                            + e.getMessage());
        } finally {
            DatabaseInterface.closeResources(db, st, rs);
        }
        
        return retObj;
    }

    private void notifyNewFlow(UserInfoInterface userInfo, int flowid) {
      for (NewFlowListener listener : newflowListeners.values()) {
        try {
          listener.flowAdded(flowid);
        }
        catch (Exception e) {
          Logger.error(userInfo.getUtilizador(), this, "notifyNewFlow", 
              "error notifying listener " + listener + " for new flow " + flowid, e);          
        }
      }         
      Logger.info(userInfo.getUtilizador(), this, "notifyNewFlow", 
          "notified listeners for new flow " + flowid);
    }
    
    public void addNewFlowListener(String id, NewFlowListener listener) {
      newflowListeners.put(id, listener);      
    }

    public void removeNewFlowListener(String id) {
      if (newflowListeners.containsKey(id)) {
        newflowListeners.remove(id);
      }
    }

    private void notifyDeploy(UserInfoInterface userInfo, int flowid, boolean bOnline) {
      for (FlowDeployListener l : deploylisteners.values()) {
        try {
          if (bOnline) {
            l.goOnline(flowid);
          }
          else {
            l.goOffline(flowid);
          }
        }
        catch (Exception e) {
          Logger.error(userInfo.getUtilizador(), this, "notifyDeploy", 
              "error notifying listener " + l + " for new flow " + flowid, e);          
        }
      }
      Logger.info(userInfo.getUtilizador(), this, "notifyDeploy", 
          "notified listeners for flow " + flowid);
    }
    
    public void addFlowDeployListener(String id, FlowDeployListener listener) {
      deploylisteners.put(id, listener);
    }

    public void removeFlowDeployListener(String id) {
      if (deploylisteners.containsKey(id)) {
        deploylisteners.remove(id);
      }
    }

    private void notifyVersion(UserInfoInterface userInfo, int flowid) {
      for (FlowVersionListener l : versionlisteners.values()) {
        try {
          l.newVersion(flowid);
        }
        catch (Exception e) {
          Logger.error(userInfo.getUtilizador(), this, "notifyVersion", 
              "error notifying listener " + l + " for flow " + flowid, e);          
        }
      }
      Logger.info(userInfo.getUtilizador(), this, "notifyVersion", 
          "notified listeners for flow " + flowid);
    }

    public void addFlowVersionListener(String id, FlowVersionListener listener) {
      versionlisteners.put(id, listener);
    }

    public void removeFlowVersionListener(String id) {
      if (versionlisteners.containsKey(id)) {
        versionlisteners.remove(id);
      }
    }
    
    public String getFlowOrganizationid(int flowid) {
      DataSource ds = null;
      Connection db = null;
      PreparedStatement st = null;
      ResultSet rs = null;
      // default value
      String result = Const.SYSTEM_ORGANIZATION;
      try {
          ds = Utils.getDataSource();
          db = ds.getConnection();
          st = db.prepareStatement("SELECT organizationid FROM flow WHERE flowid=?");
          st.setInt(1, flowid);
          rs = st.executeQuery();
          if (rs.next()) {
              result = rs.getString("organizationid");
          }
      } catch (SQLException e) {
          Logger.error(null, this, "getFlowOrganizationid", "Error retrieving organization associated with flow", e);
      } finally {
          DatabaseInterface.closeResources(db, st, rs);
      }
      return result;
  }

  public boolean updateFlowType(UserInfoInterface userInfo, int flowid) {
    
    if (null == userInfo || !userInfo.isOrgAdmin())
        return false; // not authorized
        
    if(flowid <= 0) return false;
    
    
    ProcessManager pm = BeanFactory.getProcessManagerBean();
    int procCount = pm.countFlowProcesses(userInfo, flowid, true, false);
    
    boolean result = false;
    synchronized(this) {
      DataSource ds = null;
      Connection db = null;
      PreparedStatement st = null;
      ResultSet rs = null;
      try {
        ds = Utils.getDataSource();
        db = ds.getConnection();
        db.setAutoCommit(false);
        st = db.prepareStatement("select type_code from flow where flowid=?");
        st.setInt(1, flowid);
        rs = st.executeQuery();
        FlowType currentType = null;
        if(rs.next())
          currentType = FlowType.getFlowType(rs.getString("type_code"));
        rs.close();
        rs = null;
        st.close();
        st = null;
        if(currentType == null) return false;
        FlowType newType = currentType.getNextType();
        
        
        // check if there are any processes
        if(FlowType.SUPPORT.equals(newType) && procCount != 0) {
          // existem processos (ou nao consegue saber o estado) nao pode mudar para suport
          return false;
        }
        
        
        
        st = db.prepareStatement("update flow set type_code=? where flowid=?");
        st.setString(1, newType.getCode());
        st.setInt(2, flowid);
        st.executeUpdate();
        
        FlowData fd = getCachedFlow(userInfo, flowid);
        if(fd != null) {
          fd.setFlowType(newType);
        }
        
        db.commit();
        result = true;
      } catch (SQLException e) {
        Logger.error(null, this, "getFlowOrganizationid", "Error retrieving organization associated with flow", e);
      } finally {
        DatabaseInterface.closeResources(db, st, rs);
      }
    }
    
    return result;
  }

  public boolean updateFlowShowInMenuRequirement(UserInfoInterface userInfo, int flowid) {
    if (null == userInfo || !userInfo.isOrgAdmin()) {
      return false; // not authorized
    }

    if (flowid <= 0) {
      return false;
    }

    boolean result = false;
    synchronized (this) {
      DataSource ds = null;
      Connection db = null;
      PreparedStatement st = null;
      ResultSet rs = null;
      try {
        ds = Utils.getDataSource();
        db = ds.getConnection();
        db.setAutoCommit(false);
        st = db.prepareStatement("SELECT flowid, name, description, value FROM flow_settings where flowid = ? and name like ?");
        st.setInt(1, flowid);
        st.setString(2, Const.sFLOW_MENU_ACCESSIBLE);
        rs = st.executeQuery();
        String currentValue = null;
        boolean hadData = false;
        if (rs.next()) {
          hadData = true;
          currentValue = rs.getString("value");
        } else {
          hadData = false;
        }
        rs.close();
        rs = null;
        st.close();
        st = null;

        if (hadData) {
          String newValue = null;
          if (currentValue == null || Const.sFLOW_MENU_ACCESSIBLE_YES.equals(currentValue)) {
            newValue = Const.sFLOW_MENU_ACCESSIBLE_NO;
          } else {
            newValue = Const.sFLOW_MENU_ACCESSIBLE_YES;
          }
          st = db.prepareStatement("update flow_settings set value=? where flowid=? and name like ?");
          st.setString(1, newValue);
          st.setInt(2, flowid);
          st.setString(3, Const.sFLOW_MENU_ACCESSIBLE);
          st.executeUpdate();

          db.commit();
          result = true;
        }
      } catch (SQLException e) {
        Logger.error(null, this, "updateFlowShowInMenuRequirement",
            "Error while atempting to update show in menu requirement field of flow [" + flowid + "]", e);
      } finally {
        DatabaseInterface.closeResources(db, st, rs);
      }
    }
    return result;
  }
}