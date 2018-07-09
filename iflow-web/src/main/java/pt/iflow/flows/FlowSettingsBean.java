package pt.iflow.flows;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.collections15.OrderedMap;
import org.apache.commons.collections15.map.ListOrderedMap;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import pt.iflow.api.cluster.SharedObjectRefreshManager;
import pt.iflow.api.connectors.DMSConnectorUtils;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.db.DBQueryManager;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.flows.FlowSetting;
import pt.iflow.api.flows.FlowSettings;
import pt.iflow.api.flows.FlowSettingsListener;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;
import pt.iflow.api.utils.hotfolder.HotFolderConfig;
import pt.iflow.api.utils.mail.MailConfig;
import pt.iknow.utils.StringUtilities;

public class FlowSettingsBean implements FlowSettings {

  private static FlowSettingsBean instance = null;  
  
  private Hashtable<String, FlowSettingsListener> listeners = new Hashtable<String, FlowSettingsListener>();
  
  public static FlowSettingsBean getInstance() {
    if (null == instance)
      instance = new FlowSettingsBean();
    return instance;
  }

  public void saveFlowSettings(UserInfoInterface userInfo,
      FlowSetting[] afsaSettings) {
    saveFlowSettings(userInfo, afsaSettings, false);
  }

  public void saveFlowSettings(UserInfoInterface userInfo,
      FlowSetting[] afsaSettings, boolean abInitSettings) {
    DataSource ds = null;
    Connection db = null;
    Statement st = null;
    CallableStatement cst = null;
    ResultSet rs = null;
    String sLogin = userInfo.getUtilizador();
    int nMid = 0;
    FlowSetting fs = null;
    
    Logger.trace(this, "saveFlowSettings", sLogin + " call");

    if (null == afsaSettings || afsaSettings.length == 0) {
      Logger.info(userInfo.getUtilizador(), this, "saveFlowSettings",
      "Empty settings array. exiting....");
      return;
    }

    Set<Integer> flowids = new HashSet<Integer>(); 
    
    try {
      final String sQuery = DBQueryManager.getQuery("FlowSettings.UPDATEFLOWSETTING");
      ds = Utils.getDataSource();
      db = ds.getConnection();
      db.setAutoCommit(false);
      st = db.createStatement();
      cst = db.prepareCall(sQuery);

      if (Const.DB_TYPE.equalsIgnoreCase("SQLSERVER")) {
        st.execute(DBQueryManager.getQuery("FlowSettings.getNextMid"));
        if (st.getMoreResults())
          rs = st.getResultSet();
      } else {
        rs = st.executeQuery(DBQueryManager.getQuery("FlowSettings.getNextMid"));
      }
      try {
    	  if (rs!=null && rs.next()) {
    	        nMid = rs.getInt(1);
    	      } else {
    	        // oops..
    	        // throw new Exception("Unable to get next flow setting mid");
    	        nMid = 33;
    	        Logger.warning(userInfo.getUtilizador(), this,
    	            "saveFlowSettings",
    	        "Unable to get next flow setting mid");
    	      }
      } catch (Exception e) {
    	  Logger.error(userInfo.getUtilizador(), this,
  	            "Erro saveFlowSettings", "FlowSettingsBean.java:109");
    	  }
    
      finally{
      rs.close();
      }
      for (int set = 0; set < afsaSettings.length; set++) {

        fs = afsaSettings[set];

        // just to debug....
        StringBuffer debugValues = new StringBuffer();

        cst.setInt(1, fs.getFlowId());
        cst.setInt(2, nMid);
        cst.setString(3, fs.getName());
        cst.setString(4, fs.getDescription());

        if (Logger.isDebugEnabled()) {
          debugValues.append(fs.getFlowId()).append(", ");
          debugValues.append(nMid).append(", ");
          debugValues.append(fs.getName()).append(", ");
          debugValues.append(fs.getDescription()).append(", ");
        }

        cst.setString(5, abInitSettings ? null : fs.getValue());
        if (Logger.isDebugEnabled())
          debugValues.append(abInitSettings ? null : fs.getValue())
          .append(", ");

        // single settings are not allowed to hold query values
        cst.setInt(6, 0);

        if (abInitSettings) {
          cst.setInt(7, 2);
          if (Logger.isDebugEnabled())
            debugValues.append("0, 2");
        } else {
          cst.setInt(7, 0);
          if (Logger.isDebugEnabled())
            debugValues.append("0, 0");
        }

        if (Logger.isDebugEnabled()) {
          Logger.debug(sLogin, this, "saveFlowSettings",
              "QUERY1=updateFlowSettings(" + debugValues + ")");
        }

        cst.execute();

        if (!abInitSettings && fs.isListSetting()) {
          String[] asValues = fs.getValuesToSave();

          for (int i = 0; i < asValues.length; i++) {
            String sName = Utils.genListVar(fs.getName(), i);

            // update another setting
            cst.setInt(1, fs.getFlowId());
            cst.setInt(2, nMid);
            cst.setString(3, sName);
            cst.setString(4, fs.getDescription());

            if (Logger.isDebugEnabled()) {
              debugValues = new StringBuffer();
              debugValues.append(fs.getFlowId()).append(", ");
              debugValues.append(nMid).append(", ");
              debugValues.append(sName).append(", ");
              debugValues.append(fs.getDescription())
              .append(", ");
            }

            cst.setString(5,
                StringUtils.isEmpty(asValues[i]) ? null
                    : asValues[i]);
            if (Logger.isDebugEnabled())
              debugValues.append(
                  StringUtils.isEmpty(asValues[i]) ? null
                      : asValues[i]).append(", ");

            cst.setInt(6, fs.isQueryValue(i) ? 1 : 0);
            cst.setInt(7, 1);

            if (Logger.isDebugEnabled()) {
              debugValues.append(fs.isQueryValue(i) ? 1 : 0)
              .append(", 1");

              Logger.debug(sLogin, this, "saveFlowSettings",
                  "QUERY2=updateFlowSettings(" + debugValues
                  + ")");
            }

            cst.execute();

          } // for

        } // if

        flowids.add(fs.getFlowId());
        
      } // for
      db.commit();
    } catch (Exception e) {
      try {
        if (db != null)
          db.rollback();
      } catch (Exception ei) {
      }
      ;
      Logger.error(sLogin, this, "saveFlowSettings", "exception caught: "
          + e.getMessage(), e);
    } finally {
      DatabaseInterface.closeResources(db, cst, rs);
    }
    
  }

  public void exportFlowSettings(UserInfoInterface userInfo, int flowid,
      PrintStream apsOut) {

    String sLogin = userInfo.getUtilizador();

    if (!(userInfo.isOrgAdmin() || userInfo.isSysAdmin())) {
      Logger.warning(sLogin, this, "exportFlowSettings",
      "User is not admin.");
      return;
    }

    DataSource ds = null;
    Connection db = null;
    Statement st = null;
    ResultSet rs = null;
    String sQuery = null;
    String stmp = null;

    Logger.trace(this, "exportFlowSettings", sLogin + " call");

    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      st = db.createStatement();
      rs = null;

      apsOut.println("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
      apsOut.print("<!DOCTYPE flowsettings [");
      apsOut.print(" <!ENTITY nbsp \"&#160;\">");
      apsOut.print(" <!ENTITY atilde \"&#227;\">");
      apsOut.print(" <!ENTITY otilde \"&#245;\">");
      apsOut.print(" <!ENTITY Atilde \"&#195;\">");
      apsOut.print(" <!ENTITY Otilde \"&#213;\">");
      apsOut.print(" <!ENTITY aacute \"&#225;\">");
      apsOut.print(" <!ENTITY eacute \"&#233;\">");
      apsOut.print(" <!ENTITY iacute \"&#237;\">");
      apsOut.print(" <!ENTITY oacute \"&#243;\">");
      apsOut.print(" <!ENTITY uacute \"&#250;\">");
      apsOut.print(" <!ENTITY Aacute \"&#193;\">");
      apsOut.print(" <!ENTITY Eacute \"&#201;\">");
      apsOut.print(" <!ENTITY Iacute \"&#205;\">");
      apsOut.print(" <!ENTITY Oacute \"&#211;\">");
      apsOut.print(" <!ENTITY Uacute \"&#218;\">");
      apsOut.print(" <!ENTITY agrave \"&#224;\">");
      apsOut.print(" <!ENTITY egrave \"&#232;\">");
      apsOut.print(" <!ENTITY igrave \"&#236;\">");
      apsOut.print(" <!ENTITY ograve \"&#242;\">");
      apsOut.print(" <!ENTITY ugrave \"&#249;\">");
      apsOut.print(" <!ENTITY Agrave \"&#192;\">");
      apsOut.print(" <!ENTITY Egrave \"&#200;\">");
      apsOut.print(" <!ENTITY Igrave \"&#204;\">");
      apsOut.print(" <!ENTITY Ograve \"&#210;\">");
      apsOut.print(" <!ENTITY Ugrave \"&#217;\">");
      apsOut.print(" <!ENTITY ccedil \"&#231;\">");
      apsOut.print(" <!ENTITY Ccedil \"&#199;\">");
      apsOut.println("]>");
      apsOut.println("<flowsettings>");

      sQuery = "select * from flow_settings where FLOWID=" + flowid + " order by name";

      rs = st.executeQuery(sQuery);

      while (rs.next()) {
        stmp = rs.getString("name");
        if (stmp == null || stmp.equals(""))
          continue;

        apsOut.println("  <setting>");

        apsOut.print("    <name>");
        apsOut.print(stmp);
        apsOut.println("</name>");

        stmp = rs.getString("description");
        if (stmp == null)
          stmp = "";
        apsOut.print("    <description>");
        apsOut.print(stmp);
        apsOut.println("</description>");

        stmp = rs.getString("value");
        if (stmp != null) {
          apsOut.print("    <value>");
          apsOut.print(stmp);
          apsOut.println("</value>");
        }

        apsOut.print("    <isQuery>");
        apsOut.print(rs.getBoolean("isQuery"));
        apsOut.println("</isQuery>");

        apsOut.println("  </setting>");

      }
      rs.close();
      rs = null;
      apsOut.println("</flowsettings>");
    } catch (Exception e) {
      Logger.error(sLogin, this, "exportFlowSettings", "exception caught: " + e.getMessage(), e);
    } finally {
      DatabaseInterface.closeResources(db, st, rs);
      apsOut.close();
    }
  }

  public String importFlowSettings(UserInfoInterface userInfo, int flowid, byte[] file) {
    String retObj = null;

    String sLogin = userInfo.getUtilizador();

    if (!(userInfo.isOrgAdmin() || userInfo.isSysAdmin())) {
      retObj = "User is not administrator.";
      Logger.warning(sLogin, this, "importFlowSettings",
      "User is not admin.");
      return retObj;
    }

    try {

      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      // Obtain an instance of a DocumentBuilder from the factory.
      DocumentBuilder db = dbf.newDocumentBuilder();
      // Parse the document.
      InputStream isInStream = new ByteArrayInputStream(file);
      Document doc = db.parse(isInStream);

      ArrayList<FlowSetting> alSettings = null;
      FlowSetting[] fsa = null;
      FlowSetting fs = null;

      String sName = null;
      String sDesc = null;
      String sValue = null;
      String sIsQuery = null;
      boolean bIsQuery = false;
      String stmp = null;
      String stmp2 = null;

      NodeList nl = doc.getElementsByTagName("setting");
      NodeList nl2 = null;
      NodeList nl3 = null;
      Node n = null;
      Node n2 = null;
      Node n3 = null;
      alSettings = new ArrayList<FlowSetting>();

      retObj = "erro no processamento do ficheiro";

      for (int setting = 0; setting < nl.getLength(); setting++) {
        // process settings
        n = nl.item(setting);

        nl2 = n.getChildNodes();

        sName = null;
        sDesc = null;
        sValue = null;
        sIsQuery = null;
        for (int item = 0; item < nl2.getLength(); item++) {
          n2 = nl2.item(item);

          stmp = n2.getNodeName();
          if (stmp == null)
            continue;

          nl3 = n2.getChildNodes();
          n3 = nl3.item(0);

          if (n3 == null)
            continue;

          stmp2 = n3.getNodeValue();

          if (stmp.equals("name")) {
            sName = stmp2;
          } else if (stmp.equals("description")) {
            sDesc = stmp2;
          } else if (stmp.equals("value")) {
            sValue = stmp2;
          } else if (stmp.equals("isQuery")) {
            sIsQuery = stmp2;
          } else {
            continue;
          }
        }

        if (sIsQuery != null && sIsQuery.equalsIgnoreCase("true")) {
          bIsQuery = true;
        } else {
          bIsQuery = false;
        }

        Logger.debug(sLogin, this, "importFlowSettings", "Importing setting " + sName + "=" + sValue + " for flow " + flowid);
        fs = new FlowSetting(flowid, sName, sDesc, sValue, bIsQuery, null);

        alSettings.add(fs);

      } // for setting

      retObj = "erro ao processar propriedades importadas";
      fsa = getFlowSettings(userInfo, flowid, alSettings);

      retObj = "erro ao guardar propriedades importadas";

      saveFlowSettings(userInfo, fsa);

      retObj = null;
    } catch (Exception e) {
      if (retObj == null) {
        retObj = "erro ao importar propriedades";
      }
      retObj += ": " + e.getMessage();

      Logger.error(sLogin, this, "importFlowSettings", retObj);
    }

    return retObj;
  }

  public FlowSetting getFlowSetting(int flowid, String settingVar) {

    FlowSetting retObj = null;

    DataSource ds = null;
    Connection db = null;
    Statement st = null;
    ResultSet rs = null;
    String sQuery = null;

    try {

      ds = Utils.getDataSource();
      db = ds.getConnection();
      st = db.createStatement();
      rs = null;

      sQuery = "select * from flow_settings where FLOWID=" + flowid + " and name='" + settingVar + "'";

      rs = st.executeQuery(sQuery);

      if (rs.next()) {
        retObj = new FlowSetting(rs.getInt("flowid"), rs
            .getString("name"), rs.getString("description"), rs
            .getString("value"), rs.getBoolean("isQuery"), rs
            .getTimestamp("mdate"));
      }
      rs.close();
      rs = null;

    } catch (Exception e) {
      Logger.error("", this, "getFlowSettings", "exception caught: " + e.getMessage(), e);
    } finally {
      DatabaseInterface.closeResources(db, st, rs);
    }

    return retObj;
  }

  public FlowSetting[] getFlowSettings(UserInfoInterface userInfo, int flowid) {

    FlowSetting[] retObj = null;

    DataSource ds = null;
    Connection db = null;
    Statement st = null;
    ResultSet rs = null;
    String sQuery = null;
    ArrayList<FlowSetting> altmp = null;
    FlowSetting fs = null;

    try {

      ds = Utils.getDataSource();
      db = ds.getConnection();
      st = db.createStatement();
      rs = null;

      sQuery = "select * from flow_settings where FLOWID=" + flowid + " order by name";

      rs = st.executeQuery(sQuery);

      altmp = new ArrayList<FlowSetting>();
      while (rs.next()) {
        fs = new FlowSetting(
            rs.getInt("flowid"),
            rs.getString("name"),
            rs.getString("description"),
            rs.getString("value"),
            rs .getBoolean("isQuery"),
            rs.getTimestamp("mdate"));
        altmp.add(fs);
      }

      rs.close();
      rs = null;

      ensureDefaultSettings(userInfo, flowid, altmp);

      retObj = getFlowSettings(userInfo, flowid, altmp);
    } catch (Exception e) {
      Logger.error(userInfo.getUtilizador(), this, "getFlowSettings", "exception caught: " + e.getMessage(), e);
    } finally {
      DatabaseInterface.closeResources(db, st, rs);
    }

    return retObj;
  }


  private void ensureDefaultSettings(UserInfoInterface userInfo, int flowid, ArrayList<FlowSetting> settings) {
    List<FlowSetting> defSettings = getDefaultSettings(flowid);
    for (FlowSetting fs : defSettings) {
      Object[] contains = this.contains(settings, fs);
      if ((Boolean) contains[0]) {
        settings.add(defSettings.indexOf(fs), settings.remove(((Integer) contains[1]).intValue()));
      } else {
        Logger.info(userInfo.getUtilizador(), this, "ensureDefaultSettings", 
            "Adding new default setting " + fs.getName() + " for flow " + flowid);
        settings.add(fs);
      }
    }
  }
  
  private Object[] contains(List<FlowSetting> list, FlowSetting item) {
    Object[] result = new Object[] { false, -1 };
    for (int i = 0; i < list.size(); i++) {
      FlowSetting setting = list.get(i);
      if (StringUtils.equals(item.getName(), setting.getName())) {
        result = new Object[] { true, i };
        break;
      }
    }
    return result;
  }

  public FlowSetting[] getFlowSettings(int flowid) {
    UserInfoInterface userInfo = BeanFactory.getUserInfoFactory().newGuestUserInfo();
    return getFlowSettings(userInfo, flowid);
  }

  private FlowSetting[] getFlowSettings(UserInfoInterface userInfo, int flowid, ArrayList<FlowSetting> alSettings) {
    FlowSetting[] retObj = null;

    String sLogin = userInfo.getUtilizador();
    OrderedMap<String, FlowSetting> hmtmp = null;
    FlowSetting fs = null;
    FlowSetting fs2 = null;

    try {

      if (alSettings != null) {

        hmtmp = new ListOrderedMap<String, FlowSetting>();
        for (int i = 0; i < alSettings.size(); i++) {
          fs = (FlowSetting) alSettings.get(i);
          hmtmp.put(fs.getName(), fs);
        }

        String stmp = null;
        String stmp2 = null;
        String stmp3 = null;
        ArrayList<FlowSetting> altmp = new ArrayList<FlowSetting>();

        for (int i = 0; i < alSettings.size(); i++) {
          fs = (FlowSetting) alSettings.get(i);

          stmp = fs.getName();
          if (!hmtmp.containsKey(stmp)) {
            continue;
          }

          if (Utils.isListVar(stmp)) {
            stmp = Utils.getListVarName(fs.getName());

            fs = hmtmp.get(stmp);
            hmtmp.remove(stmp);

            ArrayList<String> altmp2 = new ArrayList<String>();
            ArrayList<Integer> altmp3 = new ArrayList<Integer>();
            for (int idx = 0; true; idx++) {
              stmp2 = Utils.genListVar(stmp, idx);

              if (!hmtmp.containsKey(stmp2)) {
                break;
              }

              fs2 = (FlowSetting) hmtmp.get(stmp2);
              stmp3 = fs2.getValue();
              hmtmp.remove(stmp2);

              altmp2.add(stmp3);
              if (fs2.isQueryValue()) {
                altmp3.add(idx);
              }
            }
            fs.setValues(altmp2, altmp3);
          }
          altmp.add(fs);
        }

        retObj = new FlowSetting[altmp.size()];
        for (int i = 0; i < altmp.size(); i++) {
          retObj[i] = (FlowSetting) altmp.get(i);
        }
      }
    } catch (Exception e) {
      Logger.error(sLogin, this, "getFlowSettings(private)", "exception caught: " + e.getMessage());
    }

    return retObj;
  }

  public void refreshFlowSettings(UserInfoInterface userInfo, int flowid) {
    Logger.debug(userInfo.getUtilizador(), this, "refreshFlowSettings", 
        "refreshing flow " + flowid);    
    BeanFactory.getFlowHolderBean().refreshFlow(userInfo, flowid);    
    if (listeners.size() > 0) {
      Logger.debug(userInfo.getUtilizador(), this, "refreshFlowSettings", 
          "notifying settings listeners for flow " + flowid);
      for (FlowSettingsListener listener : listeners.values()) {
        listener.settingsChanged(flowid);
      }
    }
  }

  public boolean removeFlowSetting(UserInfoInterface userInfo, int flowId, String name) {
    String sLogin = userInfo.getUtilizador();
    if (!(userInfo.isOrgAdmin() || userInfo.isSysAdmin())) {
      Logger.warning(sLogin, this, "removeFlowSetting", "User is not admin.");
      return false;
    }

    DataSource ds = null;
    Connection db = null;
    PreparedStatement st = null;
    String sQuery = null;
    boolean result = false;

    try {

      ds = Utils.getDataSource();
      db = ds.getConnection();

      sQuery = "delete from flow_settings where flowid=? and name=?";
      st = db.prepareStatement(sQuery);
      st.setInt(1, flowId);
      st.setString(2, name);

      result = (st.executeUpdate() != 0);

    } catch (Exception e) {
      Logger.error(sLogin, this, "removeFlowSetting", "exception caught: ", e);
    } finally {
      DatabaseInterface.closeResources(db, st);
    }

    return result;
  }

  public List<FlowSetting> getDefaultSettings(int anFlowId) {
    List<FlowSetting> altmp = new ArrayList<FlowSetting>();
    altmp.add(new FlowSetting(anFlowId, Const.sNOTIFY_USER, Const.sNOTIFY_USER_DESC));
    altmp.add(new FlowSetting(anFlowId, Const.sFORCE_NOTIFY_FOR_PROFILES, Const.sFORCE_NOTIFY_FOR_PROFILES_DESC));
    altmp.add(new FlowSetting(anFlowId, Const.sDENY_NOTIFY_FOR_PROFILES, Const.sDENY_NOTIFY_FOR_PROFILES_DESC));
    altmp.add(new FlowSetting(anFlowId, Const.sPROCESS_LOCATION, Const.sPROCESS_LOCATION_DESC));
    altmp.add(new FlowSetting(anFlowId, Const.sFLOW_ENTRY_PAGE_TITLE, Const.sFLOW_ENTRY_PAGE_TITLE_DESC));
    altmp.add(new FlowSetting(anFlowId, Const.sFLOW_ENTRY_PAGE_LINK, Const.sFLOW_ENTRY_PAGE_LINK_DESC));
    altmp.add(new FlowSetting(anFlowId, Const.sSHOW_SCHED_USERS, Const.sSHOW_SCHED_USERS_DESC));
    altmp.add(new FlowSetting(anFlowId, Const.sDIRECT_LINK_AUTHENTICATION, Const.sDIRECT_LINK_AUTHENTICATION_DESC));
    altmp.add(new FlowSetting(anFlowId, Const.sEMAIL_TEMPLATE_DIR, Const.sEMAIL_TEMPLATE_DIR_DESC));
    altmp.add(new FlowSetting(anFlowId, Const.sRUN_MAXIMIZED, Const.sRUN_MAXIMIZED_DESC));
    altmp.add(new FlowSetting(anFlowId, Const.sENABLE_HISTORY, Const.sENABLE_HISTORY_DESC));
    altmp.add(new FlowSetting(anFlowId, Const.sDEFAULT_STYLESHEET, Const.sDEFAULT_STYLESHEET_DESC));
    altmp.add(new FlowSetting(anFlowId, Const.sDETAIL_PRINT_STYLESHEET, Const.sDETAIL_PRINT_STYLESHEET_DESC));
    altmp.add(new FlowSetting(anFlowId, Const.sHASHED_DOCUMENT_URL, Const.sHASHED_DOCUMENT_URL_DESC));
    altmp.add(new FlowSetting(anFlowId, Const.sAUTO_ARCHIVE_PROCESS, Const.sAUTO_ARCHIVE_PROCESS_DESC));
    altmp.add(new FlowSetting(anFlowId, Const.sSHOW_ASSIGNED_TO, Const.sSHOW_ASSIGNED_TO_DESC));
    altmp.add(new FlowSetting(anFlowId, Const.sGUEST_ACCESSIBLE, Const.sGUEST_ACCESSIBLE_DESC));
    altmp.add(new FlowSetting(anFlowId, Const.sFLOW_FLOAT_FORMAT, Const.sFLOW_FLOAT_FORMAT_DESC));
    altmp.add(new FlowSetting(anFlowId, Const.sFLOW_INT_FORMAT, Const.sFLOW_INT_FORMAT_DESC));
    altmp.add(new FlowSetting(anFlowId, Const.sFLOW_DATE_FORMAT, Const.sFLOW_DATE_FORMAT_DESC));
    altmp.add(new FlowSetting(anFlowId, Const.sSEARCHABLE_BY_INTERVENIENT, Const.sSEARCHABLE_BY_INTERVENIENT_DESC));

    // accessible in menu
    altmp.add(new FlowSetting(anFlowId, Const.sFLOW_MENU_ACCESSIBLE, Const.sFLOW_MENU_ACCESSIBLE_DESC));

    // start with mail settings
    altmp.add(new FlowSetting(anFlowId, MailConfig.CONFIG_ONOFF, "Inicio por mail"));
    altmp.add(new FlowSetting(anFlowId, MailConfig.CONFIG_HOST, "Inicio por mail: 1.servidor"));
    altmp.add(new FlowSetting(anFlowId, MailConfig.CONFIG_PORT, "Inicio por mail: 2.porto"));
    altmp.add(new FlowSetting(anFlowId, MailConfig.CONFIG_USER, "Inicio por mail: 3.utilizador"));
    altmp.add(new FlowSetting(anFlowId, MailConfig.CONFIG_PASS, "Inicio por mail: 4.password"));
    altmp.add(new FlowSetting(anFlowId, MailConfig.CONFIG_SECURE, "Inicio por mail: 5.ligação segura"));
    altmp.add(new FlowSetting(anFlowId, MailConfig.CONFIG_INBOX, "Inicio por mail: 6.Inbox"));
    altmp.add(new FlowSetting(anFlowId, MailConfig.CONFIG_SUBSCRIBED_FOLDERS, "Inicio por mail: 7.folders a subscrever"));
    altmp.add(new FlowSetting(anFlowId, MailConfig.CONFIG_CHECK_INTERVAL,"Inicio por mail: 8.intervalo de busca"));

    //dms settings
    altmp.add(new FlowSetting(anFlowId, DMSConnectorUtils.CONFIG_DMS, DMSConnectorUtils.CONFIG_DMS_DESC));
    altmp.add(new FlowSetting(anFlowId, DMSConnectorUtils.CONFIG_DMS_USER, DMSConnectorUtils.CONFIG_DMS_USER_DESC));
    altmp.add(new FlowSetting(anFlowId, DMSConnectorUtils.CONFIG_DMS_PASS, DMSConnectorUtils.CONFIG_DMS_PASS_DESC));
    
    // hotfolder settings
    altmp.add(new FlowSetting(anFlowId, HotFolderConfig.ONOFF, HotFolderConfig.ONOFF_DESC));
    altmp.add(new FlowSetting(anFlowId, HotFolderConfig.SUBS_FOLDERS, HotFolderConfig.SUBS_FOLDERS_DESC));
    altmp.add(new FlowSetting(anFlowId, HotFolderConfig.SEARCH_DEPTH, HotFolderConfig.SEARCH_DEPTH_DESC));
    altmp.add(new FlowSetting(anFlowId, HotFolderConfig.DOC_VAR, HotFolderConfig.DOC_VAR_DESC));
    altmp.add(new FlowSetting(anFlowId, HotFolderConfig.IN_USER, HotFolderConfig.IN_USER_DESC));
    
    return Collections.unmodifiableList(altmp);
  }
  
  public Set<String> getDefaultSettingsNames() {
    Set<String> settingNames = new HashSet<String>();
    
    List<FlowSetting> settings = getDefaultSettings(-1);
    for(FlowSetting s : settings)
      settingNames.add(s.getName());
    return Collections.unmodifiableSet(settingNames);
  }

  public boolean isGuestAccessible(UserInfoInterface userInfo, int flowId) {
    boolean response = false;
    Connection db = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    if (!userInfo.isGuest())
      return false;

    try {
      db = Utils.getDataSource().getConnection();
      pst = db.prepareStatement("SELECT value FROM flow_settings WHERE flowid=? AND name=?");
      pst.setInt(1, flowId);
      pst.setString(2, Const.sGUEST_ACCESSIBLE);
      rs = pst.executeQuery();
      if (rs.next()) {
        String value = rs.getString("value");
        if (value != null && StringUtilities.isAnyOfIgnoreCase(value, new String[] { Const.sGUEST_ACCESSIBLE_YES, "sim", "yes", "true", "1" })) {
          response = true;
        }
      }
      rs.close();
    } catch (SQLException e) {
      Logger.error(userInfo.getUtilizador(), this, "isGuestAccessible", "exception caught: ", e);
    } finally {
      DatabaseInterface.closeResources(db, pst, rs);
    }

    return response;
  }

  public void addFlowSettingsListener(String id, FlowSettingsListener listener) {    
	  
	  listeners.put(id, listener);
	  
  }

  public void removeFlowSettingsListener(String id) {	
    if (listeners.containsKey(id)) {
      listeners.remove(id);
    }    
  }
}
