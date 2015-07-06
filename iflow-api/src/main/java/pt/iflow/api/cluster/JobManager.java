package pt.iflow.api.cluster;

import static pt.iflow.api.cluster.JobSupport.addBeat;
import static pt.iflow.api.cluster.JobSupport.isBeatExpired;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;

import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.Utils;

public class JobManager extends Thread {

	private static JobManager instance = null;
	private String nodeKey = null;

	private JobManager() {		
		Connection db = null;
		CallableStatement cst = null;
		ResultSet rs = null;
		String sql = null;
		try {
			db = Utils.getDataSource().getConnection();
			cst = db.prepareCall("{call get_next_nodekey(?)}");
			cst.registerOutParameter(1, java.sql.Types.NUMERIC);
			cst.execute();
			nodeKey = "" + cst.getInt(1);

		} catch (Exception e) {
			Logger.error("System", "JobManager", "JobManager",
					"could nt get nodekey");
		} finally {
			DatabaseInterface.closeResources(db, cst, sql, rs);
		}
	}

	public void run() {
		Logger.debug("System", this, "run", "Started");
		while (true) {
			Long nextSleep = (long) 60;
			if (isBeatExpired()) {
				Logger.debug("System", this, "run",
						"Beat expired, activating nodeKey = " + getNodeKey());
				addBeat(getNodeKey(), Const.BEAT_ACTIVE_TIME);
				nextSleep = Const.BEAT_ACTIVE_CHECK_TIME;
			} else {
				Logger.debug("System", this, "run",	"Beat active, keep inactive nodeKey = " + getNodeKey());
				nextSleep = Const.BEAT_INACTIVE_CHECK_TIME;
			}
			try {
				Thread.sleep(nextSleep*1000);
			} catch (InterruptedException e) {
				continue;
			}
		}
	}

	public static JobManager getInstance() {
		if (instance == null)
			instance = new JobManager();
		return instance;
	}
	
	public Boolean isMyBeatValid(){
		if (Const.CLUSTER_ENABLED)
			return JobSupport.isMyBeatValid(getNodeKey());
		else
			return Boolean.TRUE;
	}
		
	public String getNodeKey() {
		return nodeKey;
	}

	public static void startManager() {
		getInstance().start();		
	}

}
