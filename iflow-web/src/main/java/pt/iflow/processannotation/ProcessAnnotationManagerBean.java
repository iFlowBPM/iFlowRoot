package pt.iflow.processannotation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.processannotation.ProcessAnnotationManager;
import pt.iflow.api.processannotation.ProcessComment;
import pt.iflow.api.processannotation.ProcessLabel;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;

public class ProcessAnnotationManagerBean implements ProcessAnnotationManager {

  private static ProcessAnnotationManagerBean instance = null;

  private ProcessAnnotationManagerBean() {
  }

  public static ProcessAnnotationManagerBean getInstance() {
    if(null == instance)
      instance = new ProcessAnnotationManagerBean();
    return instance;
  }

  //Annotations
  public void deleteAnnotations(UserInfoInterface userInfo, int flowid, int pid, int subpid) {
    removeDeadline(userInfo, flowid, pid, subpid);
    removeComment(userInfo, flowid, pid, subpid);
    removeLabels(userInfo, flowid, pid, subpid);
  }

  //Labels
  public void addLabel(UserInfoInterface userInfo, int flowid, int pid, int subpid, String[] label) {
    addLabel(userInfo, flowid, pid, subpid, label, false);
  }

  public void addLabel(UserInfoInterface userInfo, int flowid, int pid, int subpid, String[] label, boolean saveHistory) {
    Connection db = null;
    Statement st = null;
    String query = "";
    String datenow = now();
    PreparedStatement stmt = null;
    
    try {
      db = DatabaseInterface.getConnection(userInfo);
      st = db.createStatement();      
      query = "insert into process_label (labelid, flowid, pid, subpid) values (?,?,?,?)";         
       
      for(int i = 0; i < label.length; i++){
    	  stmt = db.prepareStatement(query);
    	  stmt.setString(1, label[i]); stmt.setString(2, ""+flowid); stmt.setString(3, ""+pid);  stmt.setString(4, ""+subpid);
    	  stmt.execute();
    	  stmt.close();    	  
      }
      
      

      if(saveHistory){
        st = db.createStatement();      
        query = "insert into process_label_history (labelid, flowid, pid, subpid, userid, date) values (?,?,?,?,?,?)";         
        
        for(int i = 0; i < label.length; i++){
        	stmt = db.prepareStatement(query);
        	stmt.setString(1, label[i]); stmt.setString(2, ""+flowid); stmt.setString(3, ""+pid);  stmt.setString(4, ""+subpid); stmt.setString(5, userInfo.getUtilizador()); stmt.setString(6, datenow);
        	stmt.execute();
      	  	stmt.close();            
        }
        
        st.close();
      }
    } catch (SQLException sqle) {
      Logger.error(userInfo.getUtilizador(), this, "addLabel","caught sql exception: " + sqle.getMessage(), sqle);
    } finally {
      //DatabaseInterface.closeResources(db, st);
    	try {if (db != null) db.close(); } catch (SQLException e) {}
    	try {if (st != null) st.close(); } catch (SQLException e) {}
    }
  }

  public void removeLabel(UserInfoInterface userInfo, int flowid, int pid, int subpid, String[] label) {
    Connection db = null;
    Statement st = null;
    String query = "";
    PreparedStatement stmt = null;
    
    try {
      db = DatabaseInterface.getConnection(userInfo);
      st = db.createStatement();
      query = "delete from process_label where labelid=? and flowid=? and pid=? and subpid=?";

      for(int i = 0; i < label.length; i++){
    	  stmt = db.prepareStatement(query);
    	  stmt.setString(1, label[i]); stmt.setString(2, ""+flowid); stmt.setString(3, ""+pid); stmt.setString(4, ""+subpid);
          stmt.execute();
          stmt.close();
      }  
      st.close();            
    } catch (SQLException sqle) {
      Logger.error(userInfo.getUtilizador(), this, "removeLabel","caught sql exception: " + sqle.getMessage(), sqle);
    } finally {
      DatabaseInterface.closeResources(db, st);
    }
  }

  public void removeLabels(UserInfoInterface userInfo, int flowid, int pid, int subpid) {
    Connection db = null;
    Statement st = null;
    String query = "";
    try {
      db = DatabaseInterface.getConnection(userInfo);
      st = db.createStatement();
      query = "delete from process_label where flowid="+flowid+" and pid="+pid+" and subpid="+subpid;

      st.executeUpdate(query);            
    } catch (SQLException sqle) {
      Logger.error(userInfo.getUtilizador(), this, "removeLabel","caught sql exception: " + sqle.getMessage(), sqle);
    } finally {
      // DatabaseInterface.closeResources(db, st);
    	try {if (db != null) db.close(); } catch (SQLException e) {}
    	try {if (st != null) st.close(); } catch (SQLException e) {}
    }
  }

  public List<ProcessLabel> getProcessLabelList(UserInfoInterface userInfo, int flowid, int pid, int subpid) {
    String userid = userInfo.getUtilizador();
    List<ProcessLabel> retObj = new ArrayList<ProcessLabel>();          
    String query = "";

    Connection db = null;
    Statement st = null;
    ResultSet rs = null;

    try {
      db = DatabaseInterface.getConnection(userInfo);
      st = db.createStatement();
      query = "select p.labelid, l.name, l.description from label l, process_label p where " +
      "l.id = p.labelid and p.flowid="+flowid+" and p.pid="+pid+" and p.subpid="+subpid;
      rs = st.executeQuery(query); 

      while (rs.next()) {
        retObj.add(new ProcessLabel(rs.getInt("labelid"), rs.getString("name"), rs.getString("description")));
      }

      rs.close();
      rs = null;
    } catch (SQLException sqle) {
      Logger.error(userid, this, "getProcessLabelList","caught sql exception: " + sqle.getMessage(), sqle);
    } catch (Exception e) {
      Logger.error(userid, this, "getProcessLabelList","caught exception: " + e.getMessage(), e);
    } finally {
      // DatabaseInterface.closeResources(db, st, rs);
    	try {if (db != null) db.close(); } catch (SQLException e) {}
    	try {if (st != null) st.close(); } catch (SQLException e) {}
    	try {if (rs != null) rs.close(); } catch (SQLException e) {}    }
    return retObj;
  }

  public List<ProcessLabel> getLabelList(UserInfoInterface userInfo){
    String userid = userInfo.getUtilizador();
    List<ProcessLabel> retObj = new ArrayList<ProcessLabel>();            
    String query = "";

    Connection db = null;
    Statement st = null;
    ResultSet rs = null;

    try {
      db = DatabaseInterface.getConnection(userInfo);
      st = db.createStatement();
      query = "select id, name, description from label";
      rs = st.executeQuery(query); 

      while (rs.next()) {
        retObj.add(new ProcessLabel(rs.getInt("id"), rs.getString("name"), rs.getString("description")));
      }

      rs.close();
      rs = null;
    } catch (SQLException sqle) {
      Logger.error(userid, this, "getLabelList","caught sql exception: " + sqle.getMessage(), sqle);
    } catch (Exception e) {
      Logger.error(userid, this, "getLabelList","caught exception: " + e.getMessage(), e);
    } finally {
      //DatabaseInterface.closeResources(db, st, rs);
    	try {if (db != null) db.close(); } catch (SQLException e) {}
    	try {if (st != null) st.close(); } catch (SQLException e) {}
    	try {if (rs != null) rs.close(); } catch (SQLException e) {} 

    }
    return retObj;
  }

  public List<ProcessLabel> getLabelJoin(UserInfoInterface userInfo, int flowid, int pid, int subpid){
    List<ProcessLabel> labels = getLabelList(userInfo);
    List<ProcessLabel> proc_labels = getProcessLabelList(userInfo,flowid,pid,subpid);

    for(int i = 0; i<labels.size(); i++)
      for(int j = 0; j<proc_labels.size(); j++)
        if(labels.get(i).getId() == proc_labels.get(j).getId())
          labels.get(i).setCheck(true);
    return labels;
  }

  //Deadline
  public void addDeadline(UserInfoInterface userInfo, int flowid, int pid, int subpid, String deadline) {
    addDeadline(userInfo, flowid, pid, subpid, deadline, false);
  }

  public void addDeadline(UserInfoInterface userInfo, int flowid, int pid, int subpid, String deadline, boolean saveHistory) {
    Connection db = null;
    Statement st = null;
    String query = "";
    String datenow = now();
    int rows = 0;
    PreparedStatement stmt = null;

    try {
      deadline = convertDeadlineFormatBD(deadline);
    } catch (Exception e) {
      removeDeadline(userInfo, flowid, pid, subpid);
    }
    
    try {
      db = DatabaseInterface.getConnection(userInfo);
      st = db.createStatement();
      query = "Update deadline set deadline=?, userid=? where flowid=? and pid=? and subpid=?";
      stmt = db.prepareStatement(query); stmt.setString(1, deadline); stmt.setString(2, userInfo.getUtilizador()); stmt.setString(3, ""+flowid); stmt.setString(4, ""+pid); stmt.setString(5, ""+subpid); 
      rows = stmt.executeUpdate(); stmt.close();
      if(rows <= 0){
    	  query = "insert into deadline (deadline, userid, flowid, pid, subpid) values (?,?,?,?,?)";         
    	  stmt = db.prepareStatement(query);
    	  stmt.setString(1, deadline); stmt.setString(2, userInfo.getUtilizador()); stmt.setString(3, ""+flowid); stmt.setString(4, ""+pid); stmt.setString(5, ""+subpid);
    	  stmt.execute(); stmt.close();
      }
      st.close();
      if(saveHistory){
    	  query = "insert into deadline_history (deadline, flowid, pid, subpid, userid, date) values (?,?,?,?,?,?)";
    	  stmt = db.prepareStatement(query);
    	  stmt.setString(1, deadline); stmt.setString(2, ""+flowid); stmt.setString(3, ""+pid); stmt.setString(4, ""+subpid); stmt.setString(5, userInfo.getUtilizador()); stmt.setString(6, datenow);
    	  stmt.execute();stmt.close();
      }
    } catch (SQLException sqle) {
      Logger.error(userInfo.getUtilizador(), this, "addDeadline","caught sql exception: " + sqle.getMessage(), sqle);
    } finally {
      //DatabaseInterface.closeResources(db, st);
    	try {if (db != null) db.close(); } catch (SQLException e) {}
    	try {if (st != null) st.close(); } catch (SQLException e) {}
    	try {if (stmt != null) stmt.close(); } catch (SQLException e) {}

    }
  }

  public String convertDeadlineFormatBD(String deadline){
    String [] datas = new String[3];
    String newDealine = "";
    if(deadline.charAt(2) == '/'){
      datas = deadline.split("/");
      newDealine += datas[2]+"/"+datas[1]+"/"+datas[0];
      return newDealine;
    }else{
      return deadline;
    }
  }
  
  public String convertDeadlineFormatShow(String deadline){
    String [] datas = new String[3];
    String newDealine = "";
      datas = deadline.split("/");
      newDealine += datas[2]+"/"+datas[1]+"/"+datas[0];
      return newDealine;
  }
  
  public void removeDeadline(UserInfoInterface userInfo, int flowid, int pid, int subpid) {
    Connection db = null;
    Statement st = null;
    String query = "";

    try {
      db = DatabaseInterface.getConnection(userInfo);
      st = db.createStatement();
      query = "delete from deadline where flowid="+flowid+" and pid="+pid+" and subpid="+subpid;
      st.execute(query);

    } catch (SQLException sqle) {
      Logger.error(userInfo.getUtilizador(), this, "addDeadline","caught sql exception: " + sqle.getMessage(), sqle);
    } finally {
      //DatabaseInterface.closeResources(db, st);
    	try {if (db != null) db.close(); } catch (SQLException e) {}
    	try {if (st != null) st.close(); } catch (SQLException e) {}
    }
  }

  public String getProcessDeadline(UserInfoInterface userInfo, int flowid, int pid, int subpid) {
    String userid = userInfo.getUtilizador();
    String retObj = "";
    Connection db = null;
    Statement st = null;
    ResultSet rs = null;
    try {
      db = DatabaseInterface.getConnection(userInfo);
      st = db.createStatement();
      rs = st.executeQuery("select deadline from deadline where flowid="+flowid+" and pid="+pid+" and subpid ="+subpid);
      if(rs.next())
        retObj = convertDeadlineFormatShow(rs.getString("deadline"));
      rs.close();
      rs = null;
    } catch (SQLException sqle) {
      Logger.error(userid, this, "getProcessDeadline","caught sql exception: " + sqle.getMessage(), sqle);
    } catch (Exception e) {
      Logger.error(userid, this, "getProcessDeadline","caught exception: " + e.getMessage(), e);
    } finally {
      //DatabaseInterface.closeResources(db, st, rs);
	  	try {if (db != null) db.close(); } catch (SQLException e) {}
	  	try {if (st != null) st.close(); } catch (SQLException e) {}
	  	try {if (rs != null) rs.close(); } catch (SQLException e) {}
    }
    return retObj;
  }

  //Comments
  public void addComment(UserInfoInterface userInfo, int flowid, int pid, int subpid, String comment) {
    addComment(userInfo, flowid, pid, subpid, comment, false);
  }

  public void addComment(UserInfoInterface userInfo, int flowid, int pid, int subpid, String comment, boolean saveHistory) {
    Connection db = null;
    Statement st = null;
    String query = "";
    String datenow = now();    
    int rows = 0;
    PreparedStatement stmt = null;

    try {
      db = DatabaseInterface.getConnection(userInfo);
      query = "Update comment set date=?, userid=?, comment=? where flowid=? and pid=? and subpid=?";
      stmt = db.prepareStatement(query); 
      stmt.setString(1, datenow); stmt.setString(2, userInfo.getUtilizador()); stmt.setString(3, comment); stmt.setString(4, ""+flowid); stmt.setString(5, ""+pid); stmt.setString(6, ""+subpid);
      rows = stmt.executeUpdate(); stmt.close();

      if(rows <= 0){
                 
        query = "insert into comment (date, userid, comment, flowid, pid, subpid) values (?,?,?,?,?,?)";
        stmt = db.prepareStatement(query);
        stmt.setString(1, datenow); stmt.setString(2, userInfo.getUtilizador()); stmt.setString(3, comment); stmt.setString(4, ""+flowid); stmt.setString(5, ""+pid); stmt.setString(6, ""+subpid);
        stmt.execute(); stmt.close();
      }

       
      if(saveHistory){
    	  query = "insert into comment_history (date, userid, comment, flowid, pid, subpid) values (?,?,?,?,?,?)";
    	  stmt = db.prepareStatement(query);       
    	  stmt.setString(1, datenow); stmt.setString(2, userInfo.getUtilizador()); stmt.setString(3, comment); stmt.setString(4, ""+flowid); stmt.setString(5, ""+pid); stmt.setString(6, ""+subpid);
    	  stmt.execute();stmt.close();
      }
    } catch (SQLException sqle) {
      Logger.error(userInfo.getUtilizador(), this, "addComment","caught sql exception: " + sqle.getMessage(), sqle);
    } finally {
      // DatabaseInterface.closeResources(db, st);
    	try {if (db != null) db.close(); } catch (SQLException e) {}
    	try {if (st != null) st.close(); } catch (SQLException e) {}
    	try {if (stmt != null) stmt.close(); } catch (SQLException e) {}

    }
  }

  public void removeComment(UserInfoInterface userInfo, String commentid){
    Connection db = null;
    Statement st = null;
    String query = "";
    try {
      db = DatabaseInterface.getConnection(userInfo);
      st = db.createStatement();
      query = "delete from comment where id="+commentid;

      st.executeUpdate(query);            
    } catch (SQLException sqle) {
      Logger.error(userInfo.getUtilizador(), this, "removeComment","caught sql exception: " + sqle.getMessage(), sqle);
    } finally {
      //DatabaseInterface.closeResources(db, st);
    	try {if (db != null) db.close(); } catch (SQLException e) {}
    	try {if (st != null) st.close(); } catch (SQLException e) {}    	
    }
  }

  public void removeComment(UserInfoInterface userInfo, int flowid, int pid, int subpid){
    Connection db = null;
    Statement st = null;
    String query = "";
    try {
      db = DatabaseInterface.getConnection(userInfo);
      st = db.createStatement();
      query = "delete from comment where flowid="+flowid+" and pid="+pid+" and subpid="+subpid;

      st.executeUpdate(query);            
    } catch (SQLException sqle) {
      Logger.error(userInfo.getUtilizador(), this, "removeComment","caught sql exception: " + sqle.getMessage(), sqle);
    } finally {
      // DatabaseInterface.closeResources(db, st);
    	try {if (db != null) db.close(); } catch (SQLException e) {}
    	try {if (st != null) st.close(); } catch (SQLException e) {}
    }
  }

  public ProcessComment getProcessComment(UserInfoInterface userInfo, int flowid, int pid, int subpid) {
    String userid = userInfo.getUtilizador();
    ProcessComment retObj = new ProcessComment("");         

    Connection db = null;
    Statement st = null;
    ResultSet rs = null;

    try {
      db = DatabaseInterface.getConnection(userInfo);
      st = db.createStatement();
      rs = st.executeQuery("select id,date,userid,comment from comment where flowid="+flowid+" and pid="+pid+" and subpid ="+subpid);

      if(rs.next())
        retObj = new ProcessComment(rs.getString("comment"), rs.getString("userid"), rs.getString("date"), rs.getInt("id"));

      rs.close();
      rs = null;
    } catch (SQLException sqle) {
      Logger.error(userid, this, "getProcessComment","caught sql exception: " + sqle.getMessage(), sqle);
    } catch (Exception e) {
      Logger.error(userid, this, "getProcessComment","caught exception: " + e.getMessage(), e);
    } finally {
      // DatabaseInterface.closeResources(db, st, rs);
    	try {if (db != null) db.close(); } catch (SQLException e) {}
    	try {if (st != null) st.close(); } catch (SQLException e) {}
    	try {if (rs != null) rs.close(); } catch (SQLException e) {}
    }
    return retObj;
  }

  public List<ProcessComment> getProcessComment_History(UserInfoInterface userInfo, int flowid, int pid, int subpid) {
    List<ProcessComment> retObj = new ArrayList<ProcessComment>();
    String userid = userInfo.getUtilizador();

    Connection db = null;
    Statement st = null;
    ResultSet rs = null;

    try {
      db = DatabaseInterface.getConnection(userInfo);
      st = db.createStatement();
      rs = st.executeQuery("select id,comment,userid,date from comment_history where flowid="+flowid+" and pid="+pid+" and subpid ="+subpid);

      while (rs.next())
        retObj.add(new ProcessComment(rs.getString("comment"), rs.getString("userid"), rs.getString("date"), rs.getInt("id")));

      rs.close();
      rs = null;
    } catch (SQLException sqle) {
      Logger.error(userid, this, "getProcessComment","caught sql exception: " + sqle.getMessage(), sqle);
    } catch (Exception e) {
      Logger.error(userid, this, "getProcessComment","caught exception: " + e.getMessage(), e);
    } finally {
      // DatabaseInterface.closeResources(db, st, rs);
    	try {if (db != null) db.close(); } catch (SQLException e) {}
    	try {if (st != null) st.close(); } catch (SQLException e) {}
    	try {if (rs != null) rs.close(); } catch (SQLException e) {}
    }
    return retObj;
  }

  private static String now() {
    String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
    Calendar cal = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
    return sdf.format(cal.getTime());
  }

  public List<ProcessLabel> getLabelListToIflowEditor(){
    String editor = "<iFlowEditor>";

    List<ProcessLabel> retObj = new ArrayList<ProcessLabel>();
    String query = "";

    Connection db = null;
    Statement st = null;
    ResultSet rs = null;

    try {
      db = Utils.getDataSource().getConnection();
      st = db.createStatement();
      query = "select id, name, description from label";
      rs = st.executeQuery(query); 

      while (rs.next()) {
        retObj.add(new ProcessLabel(rs.getInt("id"), rs.getString("name"), rs.getString("description")));
      }

      rs.close();
      rs = null;
    } catch (SQLException sqle) {
      Logger.error(editor, this, "getLabelListToIflowEditor","caught sql exception: " + sqle.getMessage(), sqle);
    } catch (Exception e) {
      Logger.error(editor, this, "getLabelListToIflowEditor","caught exception: " + e.getMessage(), e);
    } finally {
      // DatabaseInterface.closeResources(db, st, rs);
    	try {if (db != null) db.close(); } catch (SQLException e) {}
    	try {if (st != null) st.close(); } catch (SQLException e) {}
    	try {if (rs != null) rs.close(); } catch (SQLException e) {}
    }
    return retObj;
  }
}
