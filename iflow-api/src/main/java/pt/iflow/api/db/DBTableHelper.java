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
package pt.iflow.api.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections15.OrderedMap;
import org.apache.commons.collections15.OrderedMapIterator;
import org.apache.commons.collections15.map.ListOrderedMap;
import org.apache.commons.lang.StringUtils;

import pt.iflow.api.utils.Logger;

/**
 * DBTable helper class.
 * 
 * @author Luis Cabral
 * @since 22.01.2010
 * @version 27.01.2010
 */
public class DBTableHelper {

  private static Map<String, Map<Integer, String>> posCache;

  private DBTableHelper() {
  }

  public static List<Map<String, String>> getColumns(DBTable dbTable) {
    if (dbTable == null) {
      dbTable = new DBTable();
    }
    List<Map<String, String>> retObj = new ArrayList<Map<String, String>>();
    if (dbTable.getFields() != null) {
      for (int i = 0; i < dbTable.getFields().size(); i++) {
        HashMap<String, String> column = new HashMap<String, String>();
        column.put(DBTable.FIELD, dbTable.getFields().get(i));
        column.put(DBTable.TYPE, getListValue(dbTable.getTypes(), i));
        column.put(DBTable.NULL, getListValue(dbTable.getNullables(), i));
        column.put(DBTable.KEY, getListValue(dbTable.getKeys(), i));
        column.put(DBTable.DEFAULT, getListValue(dbTable.getDefaults(), i));
        column.put(DBTable.EXTRA, getListValue(dbTable.getExtras(), i));
        column.put(DBTable.VALUE, getListValue(dbTable.getValues(), i));
        column.put(DBTable.COND, getListValue(dbTable.getConds(), i));
        column.put(DBTable.SQL, getListValue(dbTable.getSql(), i));
        retObj.add(column);
      }
    }
    return retObj;
  }

  public static void addItem(DBTable dbTable, String name, String value, int pos) {
    if (dbTable == null) {
      dbTable = new DBTable();
    }
    if (StringUtils.equalsIgnoreCase(name, DBTable.FIELD)) {
      dbTable.setFields(addToList(dbTable.getFields(), name, value, pos));
    } else if (StringUtils.equalsIgnoreCase(name, DBTable.TYPE)) {
      dbTable.setTypes(addToList(dbTable.getTypes(), name, value, pos));
    } else if (StringUtils.equalsIgnoreCase(name, DBTable.NULL)) {
      dbTable.setNullables(addToList(dbTable.getNullables(), name, value, pos));
    } else if (StringUtils.equalsIgnoreCase(name, DBTable.KEY)) {
      dbTable.setKeys(addToList(dbTable.getKeys(), name, value, pos));
    } else if (StringUtils.equalsIgnoreCase(name, DBTable.DEFAULT)) {
      dbTable.setDefaults(addToList(dbTable.getDefaults(), name, value, pos));
    } else if (StringUtils.equalsIgnoreCase(name, DBTable.EXTRA)) {
      dbTable.setExtras(addToList(dbTable.getExtras(), name, value, pos));
    } else if (StringUtils.equalsIgnoreCase(name, DBTable.VALUE)) {
      dbTable.setValues(addToList(dbTable.getValues(), name, value, pos));
    } else if (StringUtils.equalsIgnoreCase(name, DBTable.COND)) {
      dbTable.setConds(addToList(dbTable.getConds(), name, value, pos));
    } else if (StringUtils.equalsIgnoreCase(name, DBTable.SQL)) {
      dbTable.setSql(addToList(dbTable.getSql(), name, value, pos));
    } else {
      Logger.warning(null, DBTable.class.getName(), "parseColumnItem", "Uknown column name: " + name);
    }
  }

  public static String getListValue(List<String> list, int pos) {
    String retObj = "";
    if (list != null && list.size() > pos) {
      retObj = list.get(pos);
    }
    return retObj;
  }

  public static void clearCache() {
    if (posCache == null) {
      posCache = new HashMap<String, Map<Integer, String>>();
    }
    posCache.clear();
  }

  private static List<String> addToList(List<String> list, String name, String value, int pos) {
    if (list == null) {
      list = new ArrayList<String>();
    }
    Map<Integer, String> tmpMap = new HashMap<Integer, String>();
    if (posCache.containsKey(name)) {
      tmpMap = posCache.get(name);
    }
    int storePos = pos;
    while (tmpMap.containsKey(storePos)) {
      storePos++;
    }
    tmpMap.put(storePos, value);
    posCache.put(name, tmpMap);
    if (pos < 0 || pos > list.size()) {
      pos = list.size();
    }
    list = updateListPositions(list, name);
    return list;
  }

  private static List<String> updateListPositions(List<String> list, String name) {
    if (posCache.containsKey(name)) {
      list = new ArrayList<String>();
      Map<Integer, String> tmpMap = posCache.get(name);
      OrderedMap<Integer, String> orderedMap = new ListOrderedMap<Integer, String>();
      Iterator<Integer> iter = tmpMap.keySet().iterator();
      while (iter.hasNext()) {
        int pos = iter.next();
        String value = tmpMap.get(pos);
        orderedMap.put(pos, value);
      }
      orderedMap = sort(orderedMap);
      OrderedMapIterator<Integer, String> orderedIter = orderedMap.orderedMapIterator();
      while (orderedIter.hasNext()) {
        int pos = orderedIter.next();
        String value = orderedMap.get(pos);
        list.add(value);
      }
    }
    return list;
  }

  private static OrderedMap<Integer, String> sort(OrderedMap<Integer, String> orderedMap) {
    List<Integer> order = new ArrayList<Integer>();
    Iterator<Integer> iter = orderedMap.keySet().iterator();
    while (iter.hasNext()) {
      int pos = iter.next();
      int i = 0;
      boolean added = false;
      while (i < order.size()) {
        if (pos < order.get(i)) {
          order.add(i, pos);
          added = true;
          break;
        }
        i++;
      }
      if (!added) {
        order.add(order.size(), pos);
      }
    }
    OrderedMap<Integer, String> retObj = new ListOrderedMap<Integer, String>();
    for (Integer pos : order) {
      retObj.put(pos, orderedMap.get(pos));
    }
    return retObj;
  }

}
