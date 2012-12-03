/*****************************************************************************************
    Infosistema iFlow - workflow and BPM platform
    Copyright(C) 2002-2012 Infosistema, S.A.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

    www.infosistema.com
    iflow@infosistema.com
    Av. Jose Gomes Ferreira, 11 3rd floor, s.34
    Miraflores
    1495-139 Alges Portugal
****************************************************************************************/
package pt.iflow.core;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pt.iflow.api.core.FolderManager;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.folder.Folder;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.applet.StringUtils;


public class FolderManagerBean implements FolderManager {

	  private static FolderManagerBean instance = null;
	  
	  final static HashMap<String,String> nextColors = new HashMap<String, String>();
	  final static String COLORS[] = new String[]{
		  "#9cc",
		  "#fc9",
		  "#f9c",
		  "#cc9",
		  "#c9c",
		  "#99c",
		  "#9c9",
		  "#c99",
		  "#dbb",
		  "#fe8"
	  };
	  
	  static {
		  for (int i=0; i< COLORS.length; i++) {
			  if (i==0) {
				  nextColors.put(COLORS[COLORS.length-1], COLORS[i]);
			  }
			  else {
				  nextColors.put(COLORS[i-1], COLORS[i]);
			  }
		  }
	  }
	  

	
	  public static FolderManagerBean getInstance() {
		    if (null == instance)
		      instance = new FolderManagerBean();
		    return instance;
		  }
	
	  public List<Folder> getUserFolders(UserInfoInterface userInfo){
		    String userid = userInfo.getUtilizador();
		    List<Folder> retObj = new ArrayList<Folder>();		    
		    
		    Connection db = null;
		    Statement st = null;
		    ResultSet rs = null;
		    int last = 0;

		    try {
		      db = DatabaseInterface.getConnection(userInfo);
		      st = db.createStatement();
		      rs = st.executeQuery("select id, name, color from folder where userid = '"+userid+"'");
		      
		      while (rs.next()) {
		    	  last = rs.getInt("id");
		    	  retObj.add(new Folder(last,rs.getString("name"), rs.getString("color")));
		      }
		      
		      rs.close();
		      rs = null;
		    } catch (SQLException sqle) {
		    	Logger.error(userid, this, "getUserFolders","caught sql exception: " + sqle.getMessage(), sqle);
		    } catch (Exception e) {
		    	Logger.error(userid, this, "getUserFolders","caught exception: " + e.getMessage(), e);
		    } finally {
		    	DatabaseInterface.closeResources(db, st, rs);
		    }
	  return retObj;
	  }
	  
	  public String getFolderColor(int folderid, List<Folder> folders){  
		  for(int i = 0; i < folders.size(); i++){
			  if(folders.get(i).getFolderid() == folderid)
				  return folders.get(i).getColor();
		  }		  
		  return "";
	  }
	  
	 public String getFolderName(int folderid, List<Folder> folders){  
         for(int i = 0; i < folders.size(); i++){
             if(folders.get(i).getFolderid() == folderid)
                 return folders.get(i).getName();
         }       
         return "";
     }
	  
	  public void setActivityToFolder(UserInfoInterface userInfo, String folderid, String activities){
		  String[] actividades = new String[0];
		  String[] dadosAct = new String[0];
		  
		  if(activities != null)
			  actividades = activities.split(";");

		  String query ="update activity set folderid="+folderid+" where";
		  
		  for(int i = 0; i < actividades.length; i++){
			  dadosAct = actividades[i].split("_");  
			  if(i < actividades.length-1)
				  query += " (flowid="+dadosAct[0]+" and pid="+dadosAct[1]+" and subpid="+dadosAct[2]+") or ";
			  else
				  query += "(flowid="+dadosAct[0]+" and pid="+dadosAct[1]+" and subpid="+dadosAct[2]+")";
		  }

		  Connection db = null;
		  Statement st = null;
	 
	   	  try {
		      db = DatabaseInterface.getConnection(userInfo);
		      st = db.createStatement();
		      st.executeUpdate(query);
		   } catch (SQLException sqle) {
		    	Logger.error(userInfo.getUtilizador(), this, "setActivityToFolder","caught sql exception: " + sqle.getMessage(), sqle);
		   } finally {
		    	DatabaseInterface.closeResources(db, st);
		   }
	  }
	  
	  public void editFolder(UserInfoInterface userInfo, String folderid, String foldername, String color){
		  Connection db = null;
		  Statement st = null;
	 
	   	  try {
		      db = DatabaseInterface.getConnection(userInfo);
		      st = db.createStatement();
              //st.executeUpdate("Update folder set name='"+foldername+"' , color='"+color+"' where id="+folderid);
              st.executeUpdate("Update folder set name='"+foldername+"' where id="+folderid);
		   } catch (SQLException sqle) {
		    	Logger.error(userInfo.getUtilizador(), this, "editFolder","caught sql exception: " + sqle.getMessage(), sqle);
		   } finally {
		    	DatabaseInterface.closeResources(db, st);
		   }
	  }
	  
	  public void createFolder(UserInfoInterface userInfo, String foldername, String color){
		  Connection db = null;
		  Statement st = null;
		  String query = "";
		  try {
			  List<Folder> userFolders = getUserFolders(userInfo);

			  // get last color of first color if empty
			  if (userFolders.size()==0) {
				  color = nextColors.get(COLORS[COLORS.length-1]);
			  }
			  else {
				  color = userFolders.get(userFolders.size()-1).getColor();
			  }
			  // next from last
			  color = nextColors.get(color);
			  if (color == null) { // current color does not exists
				  // get first
			    color = COLORS[0];
			  }

			  db = DatabaseInterface.getConnection(userInfo);
			  st = db.createStatement();
			  query = "insert into folder (name, color, userid) values ('"+foldername+"','"+color+"','"+userInfo.getUtilizador()+"')";
			  st.execute(query);
		  } catch (SQLException sqle) {
			  Logger.error(userInfo.getUtilizador(), this, "createFolder","caught sql exception: " + sqle.getMessage(), sqle);
		  } finally {
			  DatabaseInterface.closeResources(db, st);
		  }
	  }

	  public void deleteFolder(UserInfoInterface userInfo, String folderid){
		  Connection db = null;
		  Statement st = null;
	 
	   	  try {
		      db = DatabaseInterface.getConnection(userInfo);
		      st = db.createStatement();
		      st.executeUpdate("Update activity set folderid=NULL where folderid="+folderid);
		      st.close();
		      st = db.createStatement();
		      st.executeUpdate("delete from folder where id="+folderid);		      
		   } catch (SQLException sqle) {
		    	Logger.error(userInfo.getUtilizador(), this, "deleteFolder","caught sql exception: " + sqle.getMessage(), sqle);
		   } finally {
		    	DatabaseInterface.closeResources(db, st);
		   }
	  }
	  
	   public void setActivityToFolderByName(UserInfoInterface userInfo, String foldername, int flowid, int pid, int subpid){
         Connection db = null;
         Statement st = null;
         Statement st2 = null;
         ResultSet rs = null;
         String nextUser = "";
         try {
           String query = "select userid from activity where flowid ="+flowid+" and pid="+pid+" and subpid="+subpid;
           db = DatabaseInterface.getConnection(userInfo);
           st = db.createStatement();
           rs = st.executeQuery(query);
           
             while(rs.next()){
               nextUser = rs.getString("userid");
               
               if(!StringUtils.isEmpty(nextUser)){
                 query ="update activity set folderid = (select id from folder where name like '"+foldername+"' and userid = '"+nextUser+"')"+
                        "where flowid ="+flowid+" and pid="+pid+" and subpid="+subpid+" and userid='"+nextUser+"'";
                 st2 = db.createStatement();
                 st2.executeUpdate(query);
               }
             }
          } catch (SQLException sqle) {
               Logger.error(userInfo.getUtilizador(), this, "setActivityToFolderByName","caught sql exception: " + sqle.getMessage(), sqle);
          } finally {
               DatabaseInterface.closeResources(db, st, st2);
          }
     }
}
