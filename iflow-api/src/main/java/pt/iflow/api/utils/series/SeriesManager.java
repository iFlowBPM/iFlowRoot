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
package pt.iflow.api.utils.series;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import pt.iflow.api.db.DBQueryManager;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;

public class SeriesManager {

  protected static final String LOAD = "select * from series where " + SeriesProcessor.ID + "={0} and (" + SeriesProcessor.ORGID + " is null or " + SeriesProcessor.ORGID + "=''{1}'')";
  protected static final String LOAD_FROM_NAME = "select * from series where " + SeriesProcessor.NAME + "=''{0}'' and (" + SeriesProcessor.ORGID + " is null or " + SeriesProcessor.ORGID + "=''{1}'')";
  protected static final String PREPARE_INSERT = "insert into series (" + SeriesProcessor.CREATED + "," + SeriesProcessor.ENABLED + "," + SeriesProcessor.STATE + "," + SeriesProcessor.NAME + "," + SeriesProcessor.TYPE + "," + SeriesProcessor.PATTERN + "," + SeriesProcessor.PATTERN_FIELD_FORMATS + "," + SeriesProcessor.START_WITH + "," + SeriesProcessor.MAX_VALUE + "," + SeriesProcessor.EXTRA_OPTIONS + "," + SeriesProcessor.ORGID + ") values (?,?,?,?,?,?,?,?,?,?,?)";
  protected static final String GET_FOR_UPDATE = DBQueryManager.processQuery("Series.GET_FOR_UPDATE", new Object[] {SeriesProcessor.ID, SeriesProcessor.ORGID, SeriesProcessor.ORGID});
  protected static final String UPDATE_CURR_VALUE = "update series set " + SeriesProcessor.VALUE + "=?," + SeriesProcessor.STATE + "=? where " + SeriesProcessor.ID + "=? and (" + SeriesProcessor.ORGID + " is null or " + SeriesProcessor.ORGID + "=?)";
  protected static final String UPDATE_STATE = "update series set " + SeriesProcessor.STATE + "=? where " + SeriesProcessor.ID + "=? and (" + SeriesProcessor.ORGID + " is null or " + SeriesProcessor.ORGID + "=?)";
  protected static final String UPDATE_ENABLED_STATE = "update series set " + SeriesProcessor.ENABLED + "=? where " + SeriesProcessor.ID + "=? and (" + SeriesProcessor.ORGID + " is null or " + SeriesProcessor.ORGID + "=?)";



  public static SeriesProcessor createSeries(UserInfoInterface userInfo, SeriesProcessor series) throws Exception {


    Collection<Map<String,String>> exists = DatabaseInterface.executeQuery(MessageFormat.format(LOAD_FROM_NAME, series.getName(), series.getOrganizationId()));
    if (exists.size() > 0) {
      throw new DuplicateSeriesException(series);
    }

    DataSource ds = null;
    Connection db = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      db.setAutoCommit(true);

      pst = db.prepareStatement(PREPARE_INSERT, new String[] {"id"});
      pst.setLong(1, new Date().getTime());
      pst.setInt(2, series.isEnabled() ? 1 : 0);
      pst.setInt(3, series.getState());
      pst.setString(4, series.getName());
      pst.setString(5, series.getType());
      pst.setString(6, series.getPattern());
      pst.setString(7, series.getFormat());
      pst.setString(8, series.getStartWith());
      pst.setString(9, series.getMaxValue());
      pst.setString(10, series.getExtraOptions());
      pst.setString(11, series.getOrganizationId());
      
      pst.executeUpdate();
      rs = pst.getGeneratedKeys();

      rs.next();

      series.setId(rs.getInt(1));
    }
    catch (Exception e) {
      Logger.error(userInfo.getUtilizador(), "SeriesManager", "createSeries", "Exception caught", e);
      throw e;
    }
    finally {
      DatabaseInterface.closeResources(db, pst, rs);
    }

    return series;		
  }

  public static SeriesProcessor getSeriesFromName(UserInfoInterface userInfo, String name) throws Exception {
    Collection<Map<String,String>> records = DatabaseInterface.executeQuery(MessageFormat.format(LOAD_FROM_NAME, name, userInfo.getOrganization()));

    if (records.size() == 0)
      throw new NotFoundException(name); 

    return loadSeries(records.iterator().next());
  }

  public static SeriesProcessor getSeries(UserInfoInterface userInfo, int id) throws Exception {
    Collection<Map<String,String>> records = DatabaseInterface.executeQuery(MessageFormat.format(LOAD, id, userInfo.getOrganization()));

    if (records.size() == 0)
      throw new NotFoundException(id); 

    return loadSeries(records.iterator().next());
  }

  @SuppressWarnings("unchecked")
  private static SeriesProcessor loadSeries(Map<String,String> data) throws Exception {

    Map<String,String> lowerData = toLowerCase(data);

    Logger.debug("seriesmanager", "SeriesManager", "loadSeries", "series data:  " + lowerData);

    String className = lowerData.get(SeriesProcessor.TYPE);
    Class<? extends SeriesProcessor> c = (Class<? extends SeriesProcessor>)Class.forName(className);
    SeriesProcessor series = c.newInstance();

    series.load(lowerData);

    return series;
  }

  private static Map<String,String> toLowerCase(Map<String,String> data) {
    Map<String,String> lowerData = new HashMap<String,String>();

    for(String key : data.keySet()) {
      lowerData.put(key.toLowerCase(), data.get(key));
    }
    return lowerData;
  }

  public static List<SeriesProcessor> listSeries(UserInfoInterface userInfo) throws Exception {
    EnumSet<SeriesFilter> filter = EnumSet.of(SeriesFilter.ALL);
    return listSeries(userInfo, filter);
  }

  public static List<SeriesProcessor> listSeries(UserInfoInterface userInfo, EnumSet<SeriesFilter> filter) throws Exception {

    if (filter.isEmpty() || (filter.contains(SeriesFilter.ENABLED) && filter.contains(SeriesFilter.DISABLED)))
      return new ArrayList<SeriesProcessor>();

    StringBuilder query = new StringBuilder("select * from series where (");
    query.append(SeriesProcessor.ORGID).append(" is null or ");
    query.append(SeriesProcessor.ORGID).append("='").append(userInfo.getOrganization()).append("')");
    
    if (!filter.contains(SeriesFilter.ALL)) {

      if (filter.contains(SeriesFilter.ENABLED) || filter.contains(SeriesFilter.DISABLED)) {
        String en = filter.contains(SeriesFilter.ENABLED) ? "1" : "0";
        query.append(" and ");
        query.append(SeriesProcessor.ENABLED).append("=").append(en);
      }

      if (filter.contains(SeriesFilter.NEW) || filter.contains(SeriesFilter.USED) || filter.contains(SeriesFilter.BURNED)) {
        query.append(" and ");

        query.append(SeriesProcessor.STATE).append(" in (");

        ArrayList<String> states = new ArrayList<String>();
        if (filter.contains(SeriesFilter.NEW))
          states.add(String.valueOf(SeriesProcessor.STATE_NEW));
        if (filter.contains(SeriesFilter.USED))
          states.add(String.valueOf(SeriesProcessor.STATE_USED));
        if (filter.contains(SeriesFilter.BURNED))
          states.add(String.valueOf(SeriesProcessor.STATE_BURNED));

        for(int i=0; i < states.size(); i++) {
          if (i > 0)
            query.append(",");
          query.append(states.get(i));
        }

        query.append(")");
      }
    }

    query.append(" order by ").append(SeriesProcessor.ID);

    Logger.debug("seriesmanager", "SeriesManager", "listSeries", "going to execute " + query.toString());

    Collection<Map<String,String>> results = DatabaseInterface.executeQuery(query.toString());

    Logger.debug("seriesmanager", "SeriesManager", "listSeries", "found " + results.size() + " results");

    List<SeriesProcessor> retObj = new ArrayList<SeriesProcessor>();

    if (!results.isEmpty()) {

      Iterator<Map<String,String>> it = results.iterator();
      while(it.hasNext()) {

        retObj.add(loadSeries(it.next()));
      }			
    }

    return retObj;
  }	

  public static String[] listAvailableProcessorTypes() {
    return new String[] {NumericSeriesProcessor.TYPE, NumericYearSeriesProcessor.TYPE};
  }
}

