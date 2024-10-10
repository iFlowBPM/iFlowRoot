package pt.iflow.search;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;

import javax.sql.DataSource;

import pt.iflow.api.db.DBQueryManager;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.Utils;

public class SearchIndexManager extends Thread {

	private static SearchIndexManager instance = null;
	private boolean keepRunning = false;

	private SearchIndexManager() {
	}

	public static SearchIndexManager get() {
		if (instance == null) {
			instance = new SearchIndexManager();
		}
		return instance;
	}

	public static void startManager() {
		SearchIndexManager mng = get();
		mng.keepRunning = true;
		mng.start();
	}

	public static void stopManager() {
		SearchIndexManager mng = get();
		mng.keepRunning = false;
		mng.interrupt();
	}

	public void run() {
		while (keepRunning) {
			// Check if it's time to update index
			Calendar now = Calendar.getInstance();
			Connection db = null;
			Statement st = null;
			PreparedStatement pst = null;
			ResultSet rs = null;
			
			try {
				if (now.get(Calendar.HOUR_OF_DAY) == 0) {
					// get processes that ran yesterday
					now.add(Calendar.DAY_OF_YEAR, -1);
					now.set(Calendar.HOUR_OF_DAY, 0);
					now.set(Calendar.MINUTE, 59);
					Date yesterdayBegin = (Date) now.getTime();
					now.set(Calendar.HOUR_OF_DAY, 23);
					now.set(Calendar.HOUR_OF_DAY, 59);
					Date yesterdayEnd = (Date) now.getTime();
					
					DataSource ds = Utils.getDataSource();
					db = ds.getConnection();
					st = db.createStatement();
					pst = db.prepareStatement(DBQueryManager
							.getQuery("SearchIndex.GET_ALTERED_PROCESSES"));
					pst.setDate(1, yesterdayBegin);
					pst.setDate(2, yesterdayEnd);
					// get procdata from processes and organization
					pst.execute();
					rs = pst.getResultSet();
					while(rs.next()){
						
					}
				}
				// wait 1 hour before checking again
				sleep(60000);
			} catch (InterruptedException e) {
				Logger.adminInfo("SearchIndexManager", "run",
						"Thread interrupted: ", e);
			} catch (SQLException e) {
				Logger.adminInfo("SearchIndexManager", "run",
						"Couldn't access database: ", e);
			} finally {
		      	try {if (db != null) db.close(); } catch (SQLException e) {}
		      	try {if (st != null) st.close(); } catch (SQLException e) {}
		      	try {if (pst != null) pst.close(); } catch (SQLException e) {}
		      	try {if (rs != null) rs.close(); } catch (SQLException e) {}
			}
		}
	}

}
