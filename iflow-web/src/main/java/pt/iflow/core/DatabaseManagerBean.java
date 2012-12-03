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
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.core.DatabaseManager;
import pt.iflow.api.db.DBTable;
import pt.iflow.api.db.DBTableHelper;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.Utils;

/**
 * DatabaseManagerBean.
 * 
 * @author Luis Cabral
 * @since 22.01.2010
 * @version 25.01.2010
 * @see DatabaseManagerBean
 */
public class DatabaseManagerBean implements DatabaseManager {

  private static DatabaseManagerBean instance = null;

  private DatabaseManagerBean() {
  }

  public static DatabaseManagerBean getInstance() {
    if (null == instance)
      instance = new DatabaseManagerBean();
    return instance;
  }

  public DBTable getTableDescription(String dbPool, String table) {
    return getTableDescription(dbPool, null, table);
  }

  public DBTable getTableDescription(String dbPool, String schema, String table) {
    DBTable retObj = new DBTable(schema, table);

    if (StringUtils.isBlank(table)) {
      Logger.error(null, this, "getTableDescription", "Table name is empty!");
      return retObj;
    }

    Connection db = null;
    Statement st = null;
    ResultSet rs = null;
    try {
      if (StringUtils.isBlank(dbPool)) {
        db = Utils.getDataSource().getConnection();
      } else {
        db = Utils.getUserDataSource(dbPool).getConnection();
      }
      st = db.createStatement();
      if (StringUtils.isNotBlank(schema)) {
        table = schema + "." + table;
      }
      String query = "DESC " + table;
      if (Logger.isDebugEnabled()) {
        Logger.debug(null, this, "getTableDescription", "QUERY=" + query);
      }
      rs = st.executeQuery(query);
      DBTableHelper.clearCache();
      while (rs.next()) {
        ResultSetMetaData data = rs.getMetaData();
        for (int i = 1; i <= data.getColumnCount(); i++) {
          String name = data.getColumnName(i);
          String value = rs.getString(i);
          if (StringUtils.isBlank(value)) {
            value = "";
          }
          DBTableHelper.addItem(retObj, name, value, -1);
        }
      }
      if (retObj.getFields() != null) {
        List<String> conds = new ArrayList<String>();
        for (int i = 0; i < retObj.getFields().size(); i++) {
          String sKey = DBTableHelper.getListValue(retObj.getKeys(), i);
          boolean bCond = Utils.string2bool(DBTableHelper.getListValue(retObj.getConds(), i));
          conds.add(((bCond || StringUtils.containsIgnoreCase(sKey, "PRI")) ? "1" : "0"));
        }
        retObj.setConds(conds);
      }
      DBTableHelper.clearCache();
    } catch (SQLException e) {
      Logger.error(null, this, "getTableDescription", "SQLException caught: ", e);
    } finally {
      DatabaseInterface.closeResources(db, st, rs);
    }
    return retObj;
  }

}
