package pt.iflow.api.cluster;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.db.DBQueryManager;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;

public class SharedObjectRefreshManager {
	
	private static SharedObjectRefreshManager instance = null;
	private List<Integer> doneRefreshes = null;
	
	private SharedObjectRefreshManager(){
		doneRefreshes = new ArrayList<Integer>();
	}
	
	public static SharedObjectRefreshManager getInstance() {
		if (instance == null)
			instance = new SharedObjectRefreshManager();
		return instance;
	}

	public void checkAndRefresh(){
		if (!Const.CLUSTER_ENABLED)
			return;
		Connection db = null;
	    PreparedStatement st = null;
	    ResultSet rs = null;
	    String sql = null;
	    UserInfoInterface userInfo = BeanFactory.getUserInfoFactory().newClassManager(this.getClass().getName());
	    
		try{
			db = Utils.getDataSource().getConnection();			
			sql = DBQueryManager.processQuery("SharedObjectRefresh.SELECT");			
			st = db.prepareStatement(sql);			
			Logger.debug("System", "SharedObjectRefreshManager", "stopManager", "sql: " + sql);
			st.execute();
			rs = st.getResultSet();
			
			while(rs.next())
				if(!doneRefreshes.contains(rs.getInt(1))){
					doneRefreshes.add(rs.getInt(1));					
					BeanFactory.getFlowHolderBean().refreshCacheFlow(userInfo, rs.getInt(2));  
				}
			
			
		} catch(Exception e){
			Logger.error("System", "SharedObjectRefreshManager", "stopManager", " reason: "+ e.getMessage());
		} finally {
	        DatabaseInterface.closeResources(db, st, sql, rs);
	    }				
	
	
	}		
	
	public void addRefreshToDo(Integer flowid){
		if (!Const.CLUSTER_ENABLED)
			return;
		Connection db = null;
	    PreparedStatement st = null;
	    ResultSet rs = null;
	    String sql = null;
		try{
			db = Utils.getDataSource().getConnection();			
			sql = DBQueryManager.processQuery("SharedObjectRefresh.INSERT", new Object[]{flowid});			
			st = db.prepareStatement(sql);			
			Logger.debug("System", "SharedObjectRefreshManager", "stopManager", "sql: " + sql);
			st.execute();			
		} catch(Exception e){
			Logger.error("System", "SharedObjectRefreshManager", "stopManager", " reason: "+ e.getMessage());
		} finally {
	        DatabaseInterface.closeResources(db, st, sql, rs);
	    }				
	
	}
	
		public void stopManager(){
			if (!Const.CLUSTER_ENABLED)
				return;
			Connection db = null;
		    Statement st = null;
		    ResultSet rs = null;
		    String sql = null;
			try{
				db = Utils.getDataSource().getConnection();
				st = db.createStatement();
				sql = DBQueryManager.processQuery("SharedObjectRefresh.DELETE");				
				Logger.debug("System", "SharedObjectRefreshManager", "stopManager", "sql: " + sql);
				st.executeUpdate(sql);			
			} catch(Exception e){
				Logger.error("System", "SharedObjectRefreshManager", "stopManager", " reason: "+ e.getMessage());
			} finally {
		        DatabaseInterface.closeResources(db, st, sql, rs);
		    }				
		
		}
}
