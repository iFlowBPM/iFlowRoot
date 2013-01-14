package pt.iflow.update;

import java.lang.reflect.Modifier;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import pt.iflow.api.db.DBQueryManager;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.transition.LogTO;
import pt.iflow.api.transition.UpgradeLogTO;
import pt.iflow.api.upgrades.Upgradable;
import pt.iflow.api.utils.Logger;

/**
 * Abstractg update handler.
 * 
 * @author Luis Cabral
 * @since 04.01.2010
 * @version 06.01.2010
 */
public abstract class UpdateHandler {

  UpdateHandler() {
  }

  protected abstract String getSignature(Upgradable upgradable);

  protected void executeUpgradable(Upgradable upgradable, String fullpath) {
    boolean bUpgrade = canUpgrade(upgradable);
    boolean bExecute = (bUpgrade || upgradable.force());
    debug("executeUpgradable", (bExecute ? "Executing " : "Ignored ") + upgradable.signature()
        + (bExecute && !bUpgrade ? " [Forced]" : ""));
    if (bExecute) {
      UpgradeLogTO upgradeLog = new UpgradeLogTO();
      try {
        upgradable.execute(fullpath);
        upgradeLog.setExecuted(true);
        upgradeLog.getLog().setLog("Success");
      } catch (Exception e) {
        upgradeLog.getLog().setLog(e.getMessage());
        upgradeLog.setError(true);
      }
      upgradeLog.setSignature(getSignature(upgradable));
      upgradeLog.getLog().setCreationDate(new Timestamp(new Date().getTime()));
      upgradeLog.getLog().setLogId(getLogId(upgradable.signature()));
      persist(upgradeLog);
    }
  }


  protected boolean canUpgrade(Upgradable upgradable) {
    boolean retObj = true;
    StringBuffer query = new StringBuffer();
    query.append("SELECT " + UpgradeLogTO.EXECUTED);
    query.append(", " + UpgradeLogTO.ERROR);
    query.append(" FROM " + UpgradeLogTO.TABLE_NAME);
    query.append(" WHERE " + UpgradeLogTO.SIGNATURE + "=" + DBQueryManager.toQueryValue(getSignature(upgradable)));
    Iterator<Map<String, String>> iter = DatabaseInterface.executeQuery(query.toString()).iterator();
    while (iter.hasNext()) {
      Map<String, String> row = iter.next();
      boolean executed = (Integer.parseInt(row.get(UpgradeLogTO.EXECUTED)) == 1);
      boolean error = (Integer.parseInt(row.get(UpgradeLogTO.ERROR)) == 1);
      if (executed && !error) {
        retObj = false;
      }
      break;
    }
    return retObj;
  }

  protected void persist(UpgradeLogTO upgradable) {
    StringBuffer query1 = new StringBuffer();
    StringBuffer query2 = new StringBuffer();
    LogTO log = upgradable.getLog();
    if (exists(upgradable.getSignature())) {
      query2.append("UPDATE " + UpgradeLogTO.TABLE_NAME + " SET ");
      query2.append(UpgradeLogTO.EXECUTED + "=" + upgradable.getValueOf(UpgradeLogTO.EXECUTED));
      query2.append("," + UpgradeLogTO.ERROR + "=" + upgradable.getValueOf(UpgradeLogTO.ERROR));
      query2.append(" WHERE ");
      query2.append(UpgradeLogTO.SIGNATURE + "=" + upgradable.getValueOf(UpgradeLogTO.SIGNATURE));

      query1.append("UPDATE " + LogTO.TABLE_NAME + " SET ");
      query1.append(LogTO.LOG + "=" + log.getValueOf(LogTO.LOG));
      query1.append("," + LogTO.CREATION_DATE + "=" + log.getValueOf(LogTO.CREATION_DATE));
      query1.append(" WHERE ");
      query1.append(LogTO.LOG_ID + "=" + log.getValueOf(LogTO.LOG_ID));
    } else {
      query2.append("INSERT INTO " + UpgradeLogTO.TABLE_NAME + " (");
      query2.append(UpgradeLogTO.SIGNATURE);
      query2.append("," + UpgradeLogTO.EXECUTED);
      query2.append("," + UpgradeLogTO.ERROR);
      query2.append("," + UpgradeLogTO.LOG_ID);
      query2.append(") values (");
      query2.append(upgradable.getValueOf(UpgradeLogTO.SIGNATURE));
      query2.append("," + upgradable.getValueOf(UpgradeLogTO.EXECUTED));
      query2.append("," + upgradable.getValueOf(UpgradeLogTO.ERROR));
      query2.append("," + upgradable.getValueOf(UpgradeLogTO.LOG_ID));
      query2.append(")");

      query1.append("INSERT INTO " + LogTO.TABLE_NAME + " (");
      query1.append(LogTO.LOG_ID);
      query1.append("," + LogTO.LOG);
      query1.append("," + LogTO.CREATION_DATE);
      query1.append(") values (");
      query1.append(log.getValueOf(LogTO.LOG_ID));
      query1.append("," + log.getValueOf(LogTO.LOG));
      query1.append("," + log.getValueOf(LogTO.CREATION_DATE));
      query1.append(")");
    }
    debug("persist", "QUERY#1: " + query1.toString());
    debug("persist", "QUERY#1: " + query2.toString());
    DatabaseInterface.executeUpdates(query1.toString(), query2.toString());
  }

  protected boolean exists(String signature) {
    StringBuffer query = new StringBuffer();
    query.append("SELECT " + UpgradeLogTO.SIGNATURE);
    query.append(" FROM " + UpgradeLogTO.TABLE_NAME);
    query.append(" WHERE " + UpgradeLogTO.SIGNATURE + " LIKE " + DBQueryManager.toQueryValue(signature));
    return (DatabaseInterface.executeQuery(query.toString()).size() > 0);
  }

  protected int getLogId(String signature) {
    int retObj = -1;
    StringBuffer query = new StringBuffer();
    boolean bExists = exists(signature);
    if (bExists) {
      query.append("SELECT l." + LogTO.LOG_ID + " as " + LogTO.LOG_ID);
      query.append(" FROM " + LogTO.TABLE_NAME + " l");
      query.append(", " + UpgradeLogTO.TABLE_NAME + " ul");
      query.append(" WHERE l." + LogTO.LOG_ID + "=ul." + UpgradeLogTO.LOG_ID);
      query.append(" AND ul." + UpgradeLogTO.SIGNATURE + "=" + DBQueryManager.toQueryValue(signature));
    } else {
      query.append("SELECT max(" + LogTO.LOG_ID + ") as " + LogTO.LOG_ID);
      query.append(" FROM " + LogTO.TABLE_NAME);
    }
    Iterator<Map<String, String>> iter = DatabaseInterface.executeQuery(query.toString()).iterator();
    while (iter.hasNext()) {
      Map<String, String> row = iter.next();
      retObj = Integer.parseInt(row.get(LogTO.LOG_ID));
      break;
    }
    if (!bExists) {
      retObj = retObj + 1;
    }
    return retObj;
  }

  protected void debug(String method, String message) {
    if (Logger.isAdminDebugEnabled()) {
      Logger.adminDebug(this.getClass().getName(), method, message);
    }
  }

  @SuppressWarnings("unchecked")
  protected boolean canRunUpgradable(Class clazz) {
    int clazzModifiers = clazz.getModifiers();
    if (Modifier.isAbstract(clazzModifiers) || Modifier.isInterface(clazzModifiers))
      return false;
    
    return true;
  }
}
