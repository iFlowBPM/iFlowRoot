package pt.iflow.api.utils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.db.DBQueryManager;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.transition.FlowStateLogTO;
import pt.iflow.api.transition.LogTO;

import org.owasp.esapi.ESAPI;

public class Logger {

	private static org.apache.log4j.Logger _logger = null;
	private static org.apache.log4j.Logger _trace_logger = null;
	private static org.apache.log4j.Logger _admin_logger = null;
	private static boolean loggerAvailable = true;
	private static boolean loggerLoaded = false;

	private final static String sJSP = "JSP";

	public final static String sQuery = "INSERT INTO application_log (date,type,value) VALUES (NOW(), ?,?)";

	private static DataSource ds;
	private static Connection db;

	static {
		ds = Utils.getDataSource();
		try {
			db = ds.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		initLogger();
		// purgeOldLogs();
	}

	public static synchronized void initLogger() {
		loggerLoaded = true;
		String stmp = System.getProperty("iflow.home");
		loggerAvailable = StringUtils.isNotEmpty(stmp);
		if (!loggerAvailable)
			return;
		org.apache.log4j.PropertyConfigurator.configure(stmp + "/config/iflow_log4j.properties");

		_logger = org.apache.log4j.Logger.getLogger("iflow@iKnow");
		_trace_logger = org.apache.log4j.Logger.getLogger("iflowtrace");
		_admin_logger = org.apache.log4j.Logger.getLogger("iflowadmin");
		if (_admin_logger == null)
			_admin_logger = _logger;
	}

	public static void purgeOldLogs() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -2);
		final String query = "delete from iflow.application_log where date < ?";

		// apaga Logs antigos
		// Connection db = null;
		PreparedStatement st = null;
		// DataSource ds = null;
		try {
			// ds = Utils.getDataSource();
			// db = ds.getConnection();
			db.setAutoCommit(true);
			st = db.prepareStatement(query);
			st.setTimestamp(1, new Timestamp(cal.getTimeInMillis()));

			int n = st.executeUpdate();

			// st.close();
			// st = null;
			Logger.adminInfo("Logger", "purgeOldLogs", "Removed old Logs.");
		} catch (SQLException e) {
			Logger.adminWarning("Logger", "purgeOldLogs", "Error old logs.", e);
		} finally {
			DatabaseInterface.closeResources(db, st);
		}
	}

	public static boolean isInfoEnabled() {
		return _logger.isInfoEnabled();
	}

	public static boolean isDebugEnabled() {
		return _logger.isDebugEnabled();
	}

	public static boolean isAdminInfoEnabled() {
		return _admin_logger.isInfoEnabled();
	}

	public static boolean isAdminDebugEnabled() {
		return _admin_logger.isDebugEnabled();
	}

	public static void log(LogLevel logLevel, String asUser, String asCallerObject, String asMethodName,
			String asMessage, Throwable t) {
		log(_logger, logLevel, asUser, asCallerObject, asMethodName, asMessage, t);
	}

	public static void adminLog(LogLevel logLevel, String asManagerObject, String asMethodName, String asMessage,
			Throwable t) {
		log(_admin_logger, logLevel, "ADMIN", asManagerObject, asMethodName, asMessage, t);
	}

	private static void log(org.apache.log4j.Logger logger, LogLevel logLevel, String asUser, String asCallerObject,
			String asMethodName, String asMessage, Throwable t) {

		if (!loggerLoaded)
			initLogger();
		String sMessage = asMessage;
		String sClass = asCallerObject;
		String sMethod = asMethodName;
		String sUser = "";

		if (StringUtils.isEmpty(asMethodName)) {
			sMethod = "none";
		}

		if (logLevel != LogLevel.TRACE && logLevel != LogLevel.TRACE_JSP) {
			if (StringUtils.isEmpty(asUser)) {
				sUser = "ADMIN";
			} else {
				sUser = asUser;
			}
			sUser = sUser + " in ";
		}

		if (sClass != null) {
			sMessage = "[" + sUser + sMethod + "@" + sClass + "] - " + asMessage;
		}

		if (!loggerAvailable) {
			System.out.println(logLevel + " " + sMessage);
			return;
		}

		String clean = sMessage.replace('\n', '_').replace('\r', '_');

		clean = ESAPI.encoder().encodeForHTML(sMessage);
		if (!sMessage.equals(clean)) {
			clean += " (Encoded)";
		}

		// Connection db = null;
		// Statement st = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		switch (logLevel) {
		case DEBUG:

			if (logger.isDebugEnabled()) {
				logger.debug("", t);

				try {
					// db = Utils.getDataSource().getConnection();

					// st = db.createStatement();

					pst = db.prepareStatement(sQuery);

					pst.setString(1, "DEBUG");
					pst.setString(2, sMessage);

					pst.executeUpdate();

					// DatabaseInterface.closeResources(db, pst, st, rs);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();

					DatabaseInterface.closeResources(db);
					try {
						db = ds.getConnection();
					} catch (SQLException f) {
						f.printStackTrace();
					}
				}
			}
			break;
		case INFO:
			if (logger.isInfoEnabled()) {
				logger.info("", t);
				try {
					// db = Utils.getDataSource().getConnection();

					// st = db.createStatement();

					pst = db.prepareStatement(sQuery);

					pst.setString(1, "Info");
					pst.setString(2, sMessage);

					pst.executeUpdate();

					// DatabaseInterface.closeResources(db, pst, st, rs);

				} catch (Exception e) {
					e.printStackTrace();
					DatabaseInterface.closeResources(db);
					try {
						db = ds.getConnection();
					} catch (SQLException f) {
						f.printStackTrace();
					}
				}
			}
			break;
		case WARNING:
			logger.warn("", t);
			try {
				// db = Utils.getDataSource().getConnection();

				// st = db.createStatement();

				pst = db.prepareStatement(sQuery);

				pst.setString(1, "Warning");
				pst.setString(2, sMessage);

				pst.executeUpdate();

				// DatabaseInterface.closeResources(db, pst, st, rs);

			} catch (Exception e) {
				e.printStackTrace();

				DatabaseInterface.closeResources(db);
				try {
					db = ds.getConnection();
				} catch (SQLException f) {
					f.printStackTrace();
				}
			}

			break;
		case ERROR:
			logger.error("", t);
			try {
				// db = Utils.getDataSource().getConnection();

				// st = db.createStatement();

				pst = db.prepareStatement(sQuery);

				pst.setString(1, "Error");
				pst.setString(2, sMessage);

				pst.executeUpdate();

				// DatabaseInterface.closeResources(db, pst, st, rs);

			} catch (Exception e) {
				e.printStackTrace();

				DatabaseInterface.closeResources(db);
				try {
					db = ds.getConnection();
				} catch (SQLException f) {
					f.printStackTrace();
				}
			}
			break;
		case FATAL:
			logger.fatal("", t);
			try {
				// db = Utils.getDataSource().getConnection();

				// st = db.createStatement();

				pst = db.prepareStatement(sQuery);

				pst.setString(1, "Fatal");
				pst.setString(2, sMessage);

				pst.executeUpdate();

				// DatabaseInterface.closeResources(db, pst, st, rs);

			} catch (Exception e) {
				e.printStackTrace();

				DatabaseInterface.closeResources(db);
				try {
					db = ds.getConnection();
				} catch (SQLException f) {
					f.printStackTrace();
				}
			}
			break;
		case TRACE:
		case TRACE_JSP:
			if (logger.isInfoEnabled()) {
				logger.info("TRACE " + "", t);
				try {
					// db = Utils.getDataSource().getConnection();

					// st = db.createStatement();

					pst = db.prepareStatement(sQuery);

					pst.setString(1, "Trace");
					pst.setString(2, sMessage);

					pst.executeUpdate();

					// DatabaseInterface.closeResources(db, pst, st, rs);

				} catch (Exception e) {
					e.printStackTrace();

					DatabaseInterface.closeResources(db);
					try {
						db = ds.getConnection();
					} catch (SQLException f) {
						f.printStackTrace();
					}
				}
			} // also log with other
			if (_trace_logger.isInfoEnabled()) {
				_trace_logger.info("TRACE " + "", t);
			}
			break;
		default:
		}
	}

	/**
	 * @see {@link #logFlowState(ProcessData, int, String, String, String, String)}
	 */
	public static void logFlowState(UserInfoInterface userInfo, ProcessData procData, Block block, String message) {
		logFlowState(procData, block.getId(), userInfo.getUtilizador(), message, null, null);
	}

	/**
	 * @see {@link #logFlowState(ProcessData, int, String, String, String, String)}
	 */
	public static void logFlowState(UserInfoInterface userInfo, ProcessData procData, Block block, String message,
			Object caller, String method) {
		logFlowState(procData, block.getId(), userInfo.getUtilizador(), message,
				(caller != null ? caller.getClass().getName() : null), method);
	}

	/**
	 * Logs a specific process' state message to DB.
	 * 
	 * @param procData
	 *            Process Data.
	 * @param state
	 *            Block's ID (Process state).
	 * @param username
	 *            Active user during log procedure.
	 * @param message
	 *            Message to write in log.
	 * @param caller
	 *            [Optional]
	 * @param method
	 *            [Optional] Calling method.
	 */
	@SuppressWarnings("unused")
	public static void logFlowState(ProcessData procData, int state, String username, String message, String caller,
			String method) {
		if (!Const.DONT_LOG_IN_DB && procData.isInDB()) {
			Connection db = null;
			Statement st = null;
			PreparedStatement pst = null;
			ResultSet rs = null;
			try {
				db = Utils.getDataSource().getConnection();
				db.setAutoCommit(false);

				int flowid = procData.getFlowId();
				int pid = procData.getPid();
				int subpid = procData.getSubPid();
				int logId = 0;

				String logIdQuery = DBQueryManager.getQuery("Logger.GET_FLOW_STATE_LOG_ID");
				st = db.createStatement();
				try {
					if (Const.DB_TYPE.equalsIgnoreCase("SQLSERVER")) {
						st.execute(DBQueryManager.getQuery("FlowSettings.getNextMid"));

						if (null != rs && st.getMoreResults())
							rs = st.getResultSet();
						else
							rs = st.executeQuery(logIdQuery);

						if (rs.next()) {
							logId = rs.getInt(1);
						}

					}

				} catch (Exception e) {

				} finally {
					try {
						if (null != rs)
							rs.close();
						if (null != st)
							st.close();
						if(null != db)
							db.close();
						
					} catch (Exception e) {
						Logger.error(username, "Logger", "saveFlowStateLog", "caught exception: ", e);
					}
				}

				FlowStateLogTO flowStateLog = new FlowStateLogTO(flowid, pid, subpid, state,
						new LogTO(logId, username, caller, method, message, new Timestamp(new Date().getTime())));

				// Log Table
				StringBuffer query = new StringBuffer();
				query.append("INSERT INTO ").append(LogTO.TABLE_NAME);
				query.append(" (").append(LogTO.LOG_ID);
				query.append(" ,").append(LogTO.LOG);
				query.append(" ,").append(LogTO.CREATION_DATE);
				if (flowStateLog.getLog().getUsername() != null) {
					query.append(" ,").append(LogTO.USERNAME);
				}
				if (flowStateLog.getLog().getCaller() != null) {
					query.append(" ,").append(LogTO.CALLER);
				}
				if (flowStateLog.getLog().getMethod() != null) {
					query.append(" ,").append(LogTO.METHOD);
				}
				query.append(") VALUES (?,?,?");
				if (flowStateLog.getLog().getUsername() != null) {
					query.append(",?");
				}
				if (flowStateLog.getLog().getCaller() != null) {
					query.append(",?");
				}
				if (flowStateLog.getLog().getMethod() != null) {
					query.append(",?");
				}
				query.append(")");

				pst = db.prepareStatement(query.toString());
				int nextIndex = 1;
				pst.setInt(nextIndex, flowStateLog.getLog().getLogId());
				nextIndex++;
				pst.setString(nextIndex, flowStateLog.getLog().getLog());
				nextIndex++;
				pst.setTimestamp(nextIndex, flowStateLog.getLog().getCreationDate());
				nextIndex++;
				if (flowStateLog.getLog().getUsername() != null) {
					pst.setString(nextIndex, flowStateLog.getLog().getUsername());
					nextIndex++;
				}
				if (flowStateLog.getLog().getCaller() != null) {
					pst.setString(nextIndex, flowStateLog.getLog().getCaller());
					nextIndex++;
				}
				if (flowStateLog.getLog().getMethod() != null) {
					pst.setString(nextIndex, flowStateLog.getLog().getMethod());
				}

				pst.executeUpdate();

				// FlowStateLog table
				query = new StringBuffer();
				query.append("INSERT INTO ").append(FlowStateLogTO.TABLE_NAME);
				query.append(" (").append(FlowStateLogTO.FLOW_ID);
				query.append(",").append(FlowStateLogTO.PID);
				query.append(",").append(FlowStateLogTO.SUBPID);
				query.append(",").append(FlowStateLogTO.STATE);
				query.append(",").append(FlowStateLogTO.LOG_ID);
				query.append(") VALUES (?,?,?,?,?)");

				pst = db.prepareStatement(query.toString());
				pst.setInt(1, flowStateLog.getFlowid());
				pst.setInt(2, flowStateLog.getPid());
				pst.setInt(3, flowStateLog.getSubpid());
				pst.setInt(4, flowStateLog.getState());
				pst.setInt(5, flowStateLog.getLog().getLogId());
				pst.executeUpdate();

				db.commit();
			} catch (Exception ex) {
				Logger.error(username, "Logger", "saveFlowStateLog", "caught exception: ", ex);
				try {
					if (db != null) {
						db.rollback();
					}
				} catch (Exception e) {
				}
			} finally {
				DatabaseInterface.closeResources(db, pst, st, rs);
			}
		}
	}

	/**
	 * Log a debug message
	 * 
	 * @param asUser
	 *            Requesting user name
	 * @param aoCallerObject
	 *            Caller object
	 * @param asMethodName
	 *            Name of caller method
	 * @param asMessage
	 *            log message to write
	 */
	public static void debug(String asUser, Object aoCallerObject, String asMethodName, String asMessage) {
		debug(asUser, aoCallerObject, asMethodName, asMessage, null);
	}

	/**
	 * Log a debug message
	 * 
	 * @param asUser
	 *            Requesting user name
	 * @param aoCallerObject
	 *            Caller object
	 * @param asMethodName
	 *            Name of caller method
	 * @param asMessage
	 *            log message to write
	 * @param t
	 *            Throwable causing this log message
	 */
	public static void debug(String asUser, Object aoCallerObject, String asMethodName, String asMessage, Throwable t) {
		String stmp = null;
		if (aoCallerObject != null) {
			stmp = aoCallerObject.getClass().getName();
		}
		debug(asUser, stmp, asMethodName, asMessage, t);
	}

	/**
	 * Log a jsp debug message
	 * 
	 * @param asUser
	 *            Requesting user name
	 * @param asJspPage
	 *            jsp page name
	 * @param asMessage
	 *            log message to write
	 */
	public static void debugJsp(String asUser, String asJspPage, String asMessage) {
		debug(asUser, sJSP, asJspPage, asMessage);
	}

	/**
	 * Log a debug message
	 * 
	 * @param asUser
	 *            Requesting user name
	 * @param asCallerObject
	 *            Caller object name
	 * @param asMethodName
	 *            Name of caller method
	 * @param asMessage
	 *            log message to write
	 */
	public static void debug(String asUser, String asCallerObject, String asMethodName, String asMessage) {
		log(LogLevel.DEBUG, asUser, asCallerObject, asMethodName, asMessage, null);
	}

	/**
	 * Log a debug message
	 * 
	 * @param asUser
	 *            Requesting user name
	 * @param asCallerObject
	 *            Caller object name
	 * @param asMethodName
	 *            Name of caller method
	 * @param asMessage
	 *            log message to write
	 */
	public static void debug(String asUser, String asCallerObject, String asMethodName, String asMessage, Throwable t) {
		log(LogLevel.DEBUG, asUser, asCallerObject, asMethodName, asMessage, t);
	}

	public static void adminDebug(String managerObject, String asMethodName, String asMessage) {
		adminDebug(managerObject, asMethodName, asMessage, null);
	}

	public static void adminDebug(String managerObject, String asMethodName, String asMessage, Throwable t) {
		adminLog(LogLevel.DEBUG, managerObject, asMethodName, asMessage, t);
	}

	/**
	 * Log an info message
	 * 
	 * @param asUser
	 *            Requesting user name
	 * @param aoCallerObject
	 *            Caller object
	 * @param asMethodName
	 *            Name of caller method
	 * @param asMessage
	 *            log message to write
	 */
	public static void info(String asUser, Object aoCallerObject, String asMethodName, String asMessage) {
		String stmp = null;
		if (aoCallerObject != null) {
			stmp = aoCallerObject.getClass().getName();
		}
		info(asUser, stmp, asMethodName, asMessage, null);
	}

	/**
	 * Log an info message
	 * 
	 * @param asUser
	 *            Requesting user name
	 * @param aoCallerObject
	 *            Caller object
	 * @param asMethodName
	 *            Name of caller method
	 * @param asMessage
	 *            log message to write
	 * @param t
	 *            Throwable causing this log message
	 */
	public static void info(String asUser, Object aoCallerObject, String asMethodName, String asMessage, Throwable t) {
		String stmp = null;
		if (aoCallerObject != null) {
			stmp = aoCallerObject.getClass().getName();
		}
		info(asUser, stmp, asMethodName, asMessage, t);
	}

	/**
	 * Log a jsp info message
	 * 
	 * @param asUser
	 *            Requesting user name
	 * @param asJspPage
	 *            jsp page name
	 * @param asMessage
	 *            log message to write
	 */
	public static void infoJsp(String asUser, String asJspPage, String asMessage) {
		info(asUser, sJSP, asJspPage, asMessage);
	}

	/**
	 * Log a info message
	 * 
	 * @param asUser
	 *            Requesting user name
	 * @param asCallerObject
	 *            Caller object name
	 * @param asMethodName
	 *            Name of caller method
	 * @param asMessage
	 *            log message to write
	 */
	public static void info(String asUser, String asCallerObject, String asMethodName, String asMessage) {
		log(LogLevel.INFO, asUser, asCallerObject, asMethodName, asMessage, null);
	}

	/**
	 * Log a info message
	 * 
	 * @param asUser
	 *            Requesting user name
	 * @param asCallerObject
	 *            Caller object name
	 * @param asMethodName
	 *            Name of caller method
	 * @param asMessage
	 *            log message to write
	 */
	public static void info(String asUser, String asCallerObject, String asMethodName, String asMessage, Throwable t) {
		log(LogLevel.INFO, asUser, asCallerObject, asMethodName, asMessage, t);
	}

	public static void adminInfo(String managerObject, String asMethodName, String asMessage) {
		adminInfo(managerObject, asMethodName, asMessage, null);
	}

	public static void adminInfo(String managerObject, String asMethodName, String asMessage, Throwable t) {
		adminLog(LogLevel.INFO, managerObject, asMethodName, asMessage, t);
	}

	/**
	 * Log a warning message
	 * 
	 * @param asUser
	 *            Requesting user name
	 * @param aoCallerObject
	 *            Caller object
	 * @param asMethodName
	 *            Name of caller method
	 * @param asMessage
	 *            log message to write
	 */
	public static void warning(String asUser, Object aoCallerObject, String asMethodName, String asMessage) {
		warning(asUser, aoCallerObject, asMethodName, asMessage, null);
	}

	/**
	 * Log a warning message
	 * 
	 * @param asUser
	 *            Requesting user name
	 * @param aoCallerObject
	 *            Caller object
	 * @param asMethodName
	 *            Name of caller method
	 * @param asMessage
	 *            log message to write
	 */
	public static void warning(String asUser, Object aoCallerObject, String asMethodName, String asMessage,
			Throwable t) {
		String stmp = null;
		if (aoCallerObject != null) {
			stmp = aoCallerObject.getClass().getName();
		}
		warning(asUser, stmp, asMethodName, asMessage, t);
	}

	/**
	 * Log a jsp warning message
	 * 
	 * @param asUser
	 *            Requesting user name
	 * @param asJspPage
	 *            jsp page name
	 * @param asMessage
	 *            log message to write
	 */
	public static void warningJsp(String asUser, String asJspPage, String asMessage) {
		warning(asUser, sJSP, asJspPage, asMessage);
	}

	/**
	 * Log a warning message
	 * 
	 * @param asUser
	 *            Requesting user name
	 * @param asCallerObject
	 *            Caller object name
	 * @param asMethodName
	 *            Name of caller method
	 * @param asMessage
	 *            log message to write
	 */
	public static void warning(String asUser, String asCallerObject, String asMethodName, String asMessage) {
		log(LogLevel.WARNING, asUser, asCallerObject, asMethodName, asMessage, null);
	}

	/**
	 * Log a warning message
	 * 
	 * @param asUser
	 *            Requesting user name
	 * @param asCallerObject
	 *            Caller object name
	 * @param asMethodName
	 *            Name of caller method
	 * @param asMessage
	 *            log message to write
	 */
	public static void warning(String asUser, String asCallerObject, String asMethodName, String asMessage,
			Throwable t) {
		log(LogLevel.WARNING, asUser, asCallerObject, asMethodName, asMessage, t);
	}

	public static void adminWarning(String managerObject, String asMethodName, String asMessage) {
		adminWarning(managerObject, asMethodName, asMessage, null);
	}

	public static void adminWarning(String managerObject, String asMethodName, String asMessage, Throwable t) {
		adminLog(LogLevel.WARNING, managerObject, asMethodName, asMessage, t);
	}

	/**
	 * Log a ERROR message
	 * 
	 * @param asUser
	 *            Requesting user name
	 * @param aoCallerObject
	 *            Caller object
	 * @param asMethodName
	 *            Name of caller method
	 * @param asMessage
	 *            log message to write
	 */
	public static void error(String asUser, Object aoCallerObject, String asMethodName, String asMessage) {
		String stmp = null;
		if (aoCallerObject != null) {
			stmp = aoCallerObject.getClass().getName();
		}
		error(asUser, stmp, asMethodName, asMessage);
	}

	/**
	 * Log a ERROR message
	 * 
	 * @param asUser
	 *            Requesting user name
	 * @param aoCallerObject
	 *            Caller object
	 * @param asMethodName
	 *            Name of caller method
	 * @param asMessage
	 *            log message to write
	 */
	public static void error(String asUser, Object aoCallerObject, String asMethodName, String asMessage, Throwable t) {
		String stmp = null;
		if (aoCallerObject != null) {
			stmp = aoCallerObject.getClass().getName();
		}
		error(asUser, stmp, asMethodName, asMessage, t);
	}

	/**
	 * Log a jsp error message
	 * 
	 * @param asUser
	 *            Requesting user name
	 * @param asJspPage
	 *            jsp page name
	 * @param asMessage
	 *            log message to write
	 */
	public static void errorJsp(String asUser, String asJspPage, String asMessage) {
		error(asUser, sJSP, asJspPage, asMessage);
	}

	/**
	 * Log a jsp error message
	 * 
	 * @param asUser
	 *            Requesting user name
	 * @param asJspPage
	 *            jsp page name
	 * @param asMessage
	 *            log message to write
	 * @param t
	 *            Throwable cause
	 */
	public static void errorJsp(String asUser, String asJspPage, String asMessage, Throwable t) {
		error(asUser, sJSP, asJspPage, asMessage, t);
	}

	/**
	 * Log a ERROR message
	 * 
	 * @param asUser
	 *            Requesting user name
	 * @param asCallerObject
	 *            Caller object name
	 * @param asMethodName
	 *            Name of caller method
	 * @param asMessage
	 *            log message to write
	 */
	public static void error(String asUser, String asCallerObject, String asMethodName, String asMessage) {
		log(LogLevel.ERROR, asUser, asCallerObject, asMethodName, asMessage, null);
	}

	/**
	 * Log a ERROR message
	 * 
	 * @param asUser
	 *            Requesting user name
	 * @param asCallerObject
	 *            Caller object name
	 * @param asMethodName
	 *            Name of caller method
	 * @param asMessage
	 *            log message to write
	 */
	public static void error(String asUser, String asCallerObject, String asMethodName, String asMessage, Throwable t) {
		log(LogLevel.ERROR, asUser, asCallerObject, asMethodName, asMessage, t);
	}

	public static void adminError(String managerObject, String asMethodName, String asMessage) {
		adminError(managerObject, asMethodName, asMessage, null);
	}

	public static void adminError(String managerObject, String asMethodName, String asMessage, Throwable t) {
		adminLog(LogLevel.ERROR, managerObject, asMethodName, asMessage, t);
	}

	/**
	 * Log a fatal message
	 * 
	 * @param asUser
	 *            Requesting user name
	 * @param aoCallerObject
	 *            Caller object
	 * @param asMethodName
	 *            Name of caller method
	 * @param asMessage
	 *            log message to write
	 */
	public static void fatal(String asUser, Object aoCallerObject, String asMethodName, String asMessage) {
		String stmp = null;
		if (aoCallerObject != null) {
			stmp = aoCallerObject.getClass().getName();
		}
		fatal(asUser, stmp, asMethodName, asMessage);
	}

	/**
	 * Log a jsp fatal message
	 * 
	 * @param asUser
	 *            Requesting user name
	 * @param asJspPage
	 *            jsp page name
	 * @param asMessage
	 *            log message to write
	 */
	public static void fatalJsp(String asUser, String asJspPage, String asMessage) {
		fatal(asUser, sJSP, asJspPage, asMessage);
	}

	/**
	 * Log a fatal message
	 * 
	 * @param asUser
	 *            Requesting user name
	 * @param asCallerObject
	 *            Caller object name
	 * @param asMethodName
	 *            Name of caller method
	 * @param asMessage
	 *            log message to write
	 */
	public static void fatal(String asUser, String asCallerObject, String asMethodName, String asMessage) {
		log(LogLevel.FATAL, asUser, asCallerObject, asMethodName, asMessage, null);
	}

	/**
	 * Log a fatal message
	 * 
	 * @param asUser
	 *            Requesting user name
	 * @param asCallerObject
	 *            Caller object name
	 * @param asMethodName
	 *            Name of caller method
	 * @param asMessage
	 *            log message to write
	 */
	public static void fatal(String asUser, String asCallerObject, String asMethodName, String asMessage, Throwable t) {
		log(LogLevel.FATAL, asUser, asCallerObject, asMethodName, asMessage, t);
	}

	public static void adminFatal(String managerObject, String asMethodName, String asMessage) {
		adminFatal(managerObject, asMethodName, asMessage, null);
	}

	public static void adminFatal(String managerObject, String asMethodName, String asMessage, Throwable t) {
		adminLog(LogLevel.FATAL, managerObject, asMethodName, asMessage, t);
	}

	/**
	 * Log a trace message
	 * 
	 * @param asMessage
	 *            log message to write
	 */
	public static void trace(String asMessage) {
		trace(null, null, asMessage);
	}

	/**
	 * Log a trace message
	 * 
	 * @param aoCallerObject
	 *            Caller object
	 * @param asMethodName
	 *            Name of caller method
	 * @param asMessage
	 *            log message to write
	 */
	public static void trace(Object aoCallerObject, String asMethodName, String asMessage) {
		String stmp = null;
		if (aoCallerObject != null) {
			stmp = aoCallerObject.getClass().getName();
		}
		trace(stmp, asMethodName, asMessage);
	}

	/**
	 * Log a jsp trace message
	 * 
	 * @param asUser
	 *            Requesting user name
	 * @param asJspPage
	 *            jsp page name
	 * @param asMessage
	 *            log message to write
	 */
	public static void traceJsp(String asJspPage, String asMessage) {
		log(LogLevel.TRACE_JSP, null, sJSP, asJspPage, asMessage, null);
	}

	/**
	 * Log a trace message
	 * 
	 * @param asCallerObject
	 *            Caller object name
	 * @param asMethodName
	 *            Name of caller method
	 * @param asMessage
	 *            log message to write
	 */
	public static void trace(String asCallerObject, String asMethodName, String asMessage) {
		log(LogLevel.TRACE, null, asCallerObject, asMethodName, asMessage, null);
	}

	public static void adminTrace(String managerObject, String asMethodName, String asMessage) {
		adminLog(LogLevel.TRACE, managerObject, asMethodName, asMessage, null);
	}

	static String filterClassName(String asClassName) {
		String sRet = asClassName;
		int idx = sRet.lastIndexOf(".");

		if (idx > -1) {
			sRet = sRet.substring(++idx);
		}

		return sRet;
	}

	public static void profile(String caller, String msg, long start, long stop, long diff, long total) {
		String logMsg = caller + " PROFILE: " + msg + " start: " + start + " stop: " + stop + " diff: " + diff
				+ " total: " + total;
		_logger.fatal(logMsg);
		_trace_logger.fatal(logMsg);
	}

	public static void main(String[] args) {

		System.out.println("START\n");

		log(LogLevel.valueOf(args[0]), args[1], "Logger", "main", "Hello world", null);

		System.out.println("\nEND\n");
	}
}
