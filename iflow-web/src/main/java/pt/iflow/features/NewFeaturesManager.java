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
/*
 * <p>Title: NewFeaturesManager.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) Jul 26, 2005</p>
 * <p>Company: iKnow</p>
 * @author Pedro Monteiro
 * @version 1.0
 */

package pt.iflow.features;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;

public class NewFeaturesManager {

  public NewFeaturesManager() {
  }

  public static boolean insert(UserInfoInterface userInfo, NewFeaturesData nfFeature) {
    String userid = userInfo.getUtilizador();
    DataSource ds = null;
    Connection db = null;
    Statement st = null;

    boolean retObj = false;

    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      db.setAutoCommit(true);
      st = db.createStatement();
      StringBuffer sbInsertQuery = new StringBuffer();

      sbInsertQuery.append("insert into new_features (newfeaturesid, version, feature, ");
      sbInsertQuery.append("description, created) values (seq_new_features.nextval,'");
      sbInsertQuery.append(nfFeature.getVersion()).append("','").append(nfFeature.getFeature());
      sbInsertQuery.append("','").append(nfFeature.getDescription()).append("',");
      if (nfFeature.getCreated() == null) {
        sbInsertQuery.append("sysdate");
      } else {
        sbInsertQuery.append("'").append(nfFeature.getCreated()).append("'");
      }
      sbInsertQuery.append(")");

      Logger.debug(userid, NewFeaturesManager.class, "insertNewFeature", "NewFeaturesManager: insert: " + sbInsertQuery.toString());
      
      int i = st.executeUpdate(sbInsertQuery.toString());
      if (i > 0) {
        retObj = true;
      }

    } catch (SQLException sqle) {
      Logger.error(userid, NewFeaturesManager.class, "insertNewFeature", "caught sql exception: " + sqle.getMessage(), sqle);
    } catch (Exception e) {
      Logger.error(userid, NewFeaturesManager.class, "insertNewFeature", "caught exception: "
          + e.getMessage(), e);
    } finally {
      DatabaseInterface.closeResources(db, st);
    }

    return retObj;
  }

  public static boolean update(UserInfoInterface userInfo, NewFeaturesData nfa) {
    String userid = userInfo.getUtilizador();
    DataSource ds = null;
    Connection db = null;
    Statement st = null;

    boolean retObj = false;

    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      db.setAutoCommit(true);
      st = db.createStatement();
      StringBuffer sbtmp = new StringBuffer();

      sbtmp.append("update new_features set version='").append(nfa.getVersion());
      sbtmp.append("', feature='").append(nfa.getFeature());
      sbtmp.append("', description='").append(nfa.getDescription());
      sbtmp.append("', created=sysdate where newfeaturesid=").append(nfa.getId());

      Logger.debug(userid, NewFeaturesManager.class, "insertNewFeature", "NewFeaturesManager: update: " + sbtmp.toString());
      
      int i = st.executeUpdate(sbtmp.toString());
      if (i > 0) {
        retObj = true;
      }

    } catch (SQLException sqle) {
      Logger.error(userid, NewFeaturesManager.class, "insertNewFeature",
          "caught sql exception: " + sqle.getMessage(), sqle);
    } catch (Exception e) {
      Logger.error(userid, NewFeaturesManager.class, "insertNewFeature", "caught exception: "
          + e.getMessage(), e);
    } finally {
      DatabaseInterface.closeResources(db, st);
    }

    return retObj;
  }

  public static boolean delete(UserInfoInterface userInfo, NewFeaturesData nfa) {
    String userid = userInfo.getUtilizador();
    DataSource ds = null;
    Connection db = null;
    Statement st = null;

    boolean retObj = false;

    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      db.setAutoCommit(true);
      st = db.createStatement();
      StringBuffer sbtmp = new StringBuffer();

      sbtmp.append("delete from new_features where newfeaturesid=");
      sbtmp.append(nfa.getId());

      Logger.debug(userid, NewFeaturesManager.class, "insertNewFeature", "NewFeaturesManager: delete: " + sbtmp.toString());
      
      int i = st.executeUpdate(sbtmp.toString());
      if (i > 0) {
        retObj = true;
      }

    } catch (SQLException sqle) {
      Logger.error(userid, NewFeaturesManager.class, "insertNewFeature",
          "caught sql exception: " + sqle.getMessage(), sqle);
    } catch (Exception e) {
      Logger.error(userid, NewFeaturesManager.class, "insertNewFeature", "caught exception: "
          + e.getMessage(), e);
    } finally {
      DatabaseInterface.closeResources(db, st);
    }

    return retObj;
  }

  public static Map<String,List<NewFeaturesData>> getNewFeatures(UserInfoInterface userInfo) {
    String userid = userInfo.getUtilizador();
    List<NewFeaturesData> altmp = null;
    Map<String,List<NewFeaturesData>> httmp = new Hashtable<String, List<NewFeaturesData>>();

    DataSource ds = null;
    Connection db = null;
    Statement st = null;
    ResultSet rs = null;

    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      st = db.createStatement();
      StringBuffer sbtmp = new StringBuffer();

      sbtmp.append("select * from new_features order by version desc, created desc");
      rs = st.executeQuery(sbtmp.toString());

      while (rs.next()) {
        NewFeaturesData nfatmp = new NewFeaturesData(rs.getInt("newfeaturesid"),
            rs.getString("version"), rs.getString("feature"), 
            rs.getString("description"), rs.getTimestamp("created"));
        if (httmp.containsKey(nfatmp.getVersion())) {
          altmp = httmp.get(nfatmp.getVersion());
        } else {
          altmp = new ArrayList<NewFeaturesData>();
          httmp.put(nfatmp.getVersion(), altmp);
        }
        altmp.add(nfatmp);
        altmp = null;
      }

    } catch (SQLException sqle) {
      Logger.error(userid, NewFeaturesManager.class, "getNewFeatures",
          "caught sql exception: " + sqle.getMessage());
      sqle.printStackTrace();
      httmp = null;
    } catch (Exception e) {
      Logger.error(userid, NewFeaturesManager.class, "getNewFeatures", "caught exception: "
          + e.getMessage());
      e.printStackTrace();
      httmp = null;
    } finally {
      DatabaseInterface.closeResources(db, st, rs);
    }

    return httmp;
  }
}
