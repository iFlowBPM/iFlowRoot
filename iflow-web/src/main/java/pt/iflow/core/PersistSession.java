package pt.iflow.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;

public class PersistSession {
  
  private static List<String> keys = new ArrayList<String>();
  private static String FILTERLABEL = "filterlabel";
  private static String FILTERDAYS = "filterdays";
  private static String FILTERFOLDER = "filterfolder";
  private static String FILTRO_SCROLL = "filtro_scroll";
  private static String FILTRO_PID = "filtro_pid";
  private static String FILTRO_SUBPID = "filtro_subpid";
  private static String FILTRO_SHOWFLOWID = "filtro_showflowid";
  private static String FILTRO_PNUMBER = "filtro_pnumber";
  private static String FILTRO_ORDER = "filtro_order";
  private static String FILTRO_NITEMS = "filtro_nItems";
  private static String FILTRO_STARTINDEX = "filtro_startindex";
  private static String FILTRO_NEXTSTARTINDEX = "filtro_nextstartindex";
  private static String FILTRO_DTBEFORE = "filtro_dtBefore";
  private static String FILTRO_DTAFTER = "filtro_dtAfter";
  private static String ACTIVITY_CONFIG = "ACTIVITY_CONFIG";
  private static String ACTIVITY_INDEX = "ACTIVITY_INDEX";
  private static String ACTIVITY_BATCH = "ACTIVITY_BATCH";
  
  static {
    keys.add(FILTERLABEL);
    keys.add(FILTERDAYS);
    keys.add(FILTERFOLDER);
    keys.add(FILTRO_SCROLL);
    keys.add(FILTRO_PID);
    keys.add(FILTRO_SUBPID);
    keys.add(FILTRO_SHOWFLOWID);
    keys.add(FILTRO_PNUMBER);
    keys.add(FILTRO_ORDER);
    keys.add(FILTRO_NITEMS);
    keys.add(FILTRO_STARTINDEX);
    keys.add(FILTRO_NEXTSTARTINDEX);
    keys.add(FILTRO_DTBEFORE);
    keys.add(FILTRO_DTAFTER);
    keys.add(ACTIVITY_CONFIG);
    keys.add(ACTIVITY_INDEX);
    keys.add(ACTIVITY_BATCH);
  }

  
  public void getSession(UserInfoInterface userInfo, HttpSession session){
    String userid = userInfo.getUtilizador();    
    Connection db = null;
    Statement st = null;
    ResultSet rs = null;
    Object[][] valores = new Object[0][2]; 
    byte[] buf = null;
    
    try {
      db = DatabaseInterface.getConnection(userInfo);
      st = db.createStatement();
      rs = st.executeQuery("select session from user_session where userid = '"+userid+"'");
      
      if (rs.next()) {
        buf = rs.getBytes("session");
      }
      
      if (buf != null) {
        ByteArrayInputStream bais = new ByteArrayInputStream(buf);
        ObjectInputStream objectIn = new ObjectInputStream(bais);
        valores = (Object[][]) objectIn.readObject();
        
      }
      
      rs.close();
      rs = null;
    } catch (SQLException sqle) {
        Logger.error(userid, "PersistSession", "getSession", "caught sql exception: " + sqle.getMessage(), sqle);
    } catch (Exception e) {
        Logger.error(userid, "PersistSession", "getSession", "caught exception: " + e.getMessage(), e);
    } finally {
        DatabaseInterface.closeResources(db, st, rs);
    }

    for(int i=0; i < valores.length; i++){
      session.setAttribute((String)valores[i][0], valores[i][1]);
    }
  }
  
  
  public void setSession(UserInfoInterface userInfo, HttpSession session){
    Object[][] valores = new Object[keys.size()][2];
    int rows = 0;
    int i = 0;

    for (String key : keys) {
      valores[i][0] = key;
      valores[i++][1] = session.getAttribute(key);
    }
    
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      ObjectOutputStream oout = new ObjectOutputStream(baos);
      oout.writeObject(valores);
      oout.close();
    } catch (IOException e) {
      return;
    } catch (Exception ex) {
      Logger.error(userInfo.getUtilizador(), "PersistSession", "setSession", "caught exception: " + ex.getMessage(), ex);
      return;
    }
        
    Connection db = null;
    PreparedStatement pst = null;
    try {
      db = DatabaseInterface.getConnection(userInfo);
      pst = db.prepareStatement("Update user_session set session=? where userid='"+userInfo.getUtilizador()+"'");
      pst.setBytes(1, baos.toByteArray());
      rows = pst.executeUpdate();

      if(rows <= 0){      
        db = DatabaseInterface.getConnection(userInfo); 
        pst = db.prepareStatement("insert into user_session (userid, session) values ('"+userInfo.getUtilizador()+"',?)");
        pst.setBytes(1, baos.toByteArray());
        pst.execute();
      }
    } catch (SQLException sqle) {
        Logger.error(userInfo.getUtilizador(), "PersistSession", "setSession","caught sql exception: " + sqle.getMessage(), sqle);
    } finally {
        DatabaseInterface.closeResources(db, pst);
    }
  }
  
}
