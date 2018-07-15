package pt.iflow.api.cluster;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import pt.iflow.api.db.DBQueryManager;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.Utils;

public class JobSupport {

	public static void addBeat(String nodeKey, Long beatTimeSeconds){
		Connection db = null;
	    Statement st = null;
	    ResultSet rs = null;
	    String sql = null;
		try{
			db = Utils.getDataSource().getConnection();
			st = db.createStatement();
			sql = DBQueryManager.processQuery("JobManager.ADD_BEAT", new Object[] {nodeKey, beatTimeSeconds});
			Logger.debug("System", "JobSupport", "addBeat", "sql: " + sql);
			st.executeUpdate(sql);			
		} catch(Exception e){
			Logger.error("System", "JobSupport", "addBeat", "nodeKey: " + nodeKey + " , reason: "+ e.getMessage());
		} finally {
	        //jcosta: DatabaseInterface.closeResources(db, st, sql, rs);
	        if (db != null) DatabaseInterface.safeClose(db);
	        if (st != null) DatabaseInterface.safeClose(st);
	        if (rs != null) DatabaseInterface.safeClose(rs);
	    }				
	}
	
	public static Boolean isMyBeatValid(String nodeKey){ 
		Connection db = null;
	    Statement st = null;
	    ResultSet rs = null;
	    String sql = null;
		try{
			db = Utils.getDataSource().getConnection();
			st = db.createStatement();
			sql = DBQueryManager.processQuery("JobManager.IS_MY_BEAT_VALID", new Object[] {nodeKey});
			Logger.debug("System", "JobSupport", "isMyBeatValid", "sql: " + sql);
			rs = st.executeQuery(sql);			
			rs.next();
			return rs.getBoolean(1);										
		} catch(Exception e){
			Logger.error("System", "JobSupport", "isMyBeatValid", "could not verify my beat validity, nodeKey: " + nodeKey  + " , reason: "+ e.getMessage());
			return Boolean.FALSE;
		} finally {
	        DatabaseInterface.closeResources(db, st, sql, rs);
	    }				
	}
	
	
	public static Boolean isBeatExpired(){ 
		Connection db = null;
	    Statement st = null;
	    ResultSet rs = null;
	    String sql = null;
		try{
			db = Utils.getDataSource().getConnection();
			st = db.createStatement();
			sql = DBQueryManager.processQuery("JobManager.IS_BEAT_EXPIRED");
			Logger.debug("System", "JobSupport", "isBeatExpired", "sql: " + sql);
			rs = st.executeQuery(sql);			
			rs.next();
			return rs.getBoolean(1);										
		} catch(Exception e){
			Logger.error("System", "JobSupport", "isBeatExpired", "could not verify beat expiration, reason: "+ e.getMessage());
			return Boolean.FALSE;
		} finally {
	        DatabaseInterface.closeResources(db, st, sql, rs);
	    }				
	}
	
}
