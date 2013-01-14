package pt.iflow.processannotation;

import java.sql.Connection;
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
    try {
      db = DatabaseInterface.getConnection(userInfo);
      st = db.createStatement();      
      query = "insert into process_label (labelid, flowid, pid, subpid) values ";         

      for(int i = 0; i < label.length; i++){
        if(i != label.length-1)
          query += "('"+label[i]+"','"+flowid+"','"+pid+"','"+subpid+"') , ";
        else
          query += "('"+label[i]+"','"+flowid+"','"+pid+"','"+subpid+"') ";
      }
      st.execute(query);
      st.close();

      if(saveHistory){
        st = db.createStatement();      
        query = "insert into process_label_history (labelid, flowid, pid, subpid, userid, date) values ";         

        for(int i = 0; i < label.length; i++){
          if(i != label.length-1)
            query += "('"+label[i]+"','"+flowid+"','"+pid+"','"+subpid+"','"+userInfo.getUtilizador()+"','"+datenow+"') , ";
          else
            query += "('"+label[i]+"','"+flowid+"','"+pid+"','"+subpid+"','"+userInfo.getUtilizador()+"','"+datenow+"') ";
        }
        st.execute(query);
        st.close();
      }
    } catch (SQLException sqle) {
      Logger.error(userInfo.getUtilizador(), this, "addLabel","caught sql exception: " + sqle.getMessage(), sqle);
    } finally {
      DatabaseInterface.closeResources(db, st);
    }
  }

  public void removeLabel(UserInfoInterface userInfo, int flowid, int pid, int subpid, String[] label) {
    Connection db = null;
    Statement st = null;
    String query = "";
    try {
      db = DatabaseInterface.getConnection(userInfo);
      st = db.createStatement();
      query = "delete from process_label where ";

      for(int i = 0; i < label.length; i++){
        if(i != label.length-1)
          query += " labelid="+label[i]+" and flowid="+flowid+" and pid="+pid+" and subpid="+subpid+" or";
        else
          query += " labelid="+label[i]+" and flowid="+flowid+" and pid="+pid+" and subpid="+subpid;
      }  
      st.executeUpdate(query);            
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
      DatabaseInterface.closeResources(db, st);
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
      DatabaseInterface.closeResources(db, st, rs);
    }
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
      DatabaseInterface.closeResources(db, st, rs);
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

    try {
      deadline = convertDeadlineFormatBD(deadline);
    } catch (Exception e) {
      removeDeadline(userInfo, flowid, pid, subpid);
    }
    
    try {
      db = DatabaseInterface.getConnection(userInfo);
      st = db.createStatement();
      query = "Update deadline set deadline='"+ deadline+"', userid='"+userInfo.getUtilizador()+"' where flowid='"+flowid+"' and pid='"+pid+"' and subpid='"+subpid+"'";
      rows = st.executeUpdate(query);

      if(rows <= 0){
        st.close();         
        st = db.createStatement();
        query = "insert into deadline (deadline, userid, flowid, pid, subpid) values ('"+ deadline+"','"+userInfo.getUtilizador()+"','"+flowid+"','"+pid+"','"+subpid+"')";
        st.executeUpdate(query);
      }

      st.close();
      if(saveHistory){
        st = db.createStatement();
        query = "insert into deadline_history (deadline, flowid, pid, subpid, userid, date) values ('"+deadline+"','"+flowid+"','"+pid+"','"+subpid+"','"+userInfo.getUtilizador()+"','"+datenow+"')";
        st.execute(query);
      }
    } catch (SQLException sqle) {
      Logger.error(userInfo.getUtilizador(), this, "addDeadline","caught sql exception: " + sqle.getMessage(), sqle);
    } finally {
      DatabaseInterface.closeResources(db, st);
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
      DatabaseInterface.closeResources(db, st);
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
      DatabaseInterface.closeResources(db, st, rs);
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

    try {
      db = DatabaseInterface.getConnection(userInfo);
      st = db.createStatement();
      query = "Update comment set date='"+ datenow+"', userid='"+userInfo.getUtilizador()+"', comment='"+comment+"' where flowid='"+flowid+"' and pid='"+pid+"' and subpid='"+subpid+"'";
      rows = st.executeUpdate(query);

      if(rows <= 0){
        st.close();         
        st = db.createStatement();
        query = "insert into comment (date, userid, comment, flowid, pid, subpid) values ";         
        query += "('"+datenow+"','"+userInfo.getUtilizador()+"','"+comment+"','"+flowid+"','"+pid+"','"+subpid+"')"; 
        st.executeUpdate(query);
      }

      st.close(); 
      if(saveHistory){
        st = db.createStatement();
        query = "insert into comment_history (comment, flowid, pid, subpid, userid, date) values ";         
        query += "('"+comment+"','"+flowid+"','"+pid+"','"+subpid+"','"+userInfo.getUtilizador()+"','"+datenow+"')"; 
        st.executeUpdate(query);
      }
    } catch (SQLException sqle) {
      Logger.error(userInfo.getUtilizador(), this, "addComment","caught sql exception: " + sqle.getMessage(), sqle);
    } finally {
      DatabaseInterface.closeResources(db, st);
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
      DatabaseInterface.closeResources(db, st);
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
      DatabaseInterface.closeResources(db, st);
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
      DatabaseInterface.closeResources(db, st, rs);
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
      DatabaseInterface.closeResources(db, st, rs);
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
      DatabaseInterface.closeResources(db, st, rs);
    }
    return retObj;
  }
}
