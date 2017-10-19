package pt.iflow.api.db;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType; 
import javax.xml.bind.annotation.XmlAccessorType; 
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a DB table.
 * 
 * @author Luis Cabral
 * @since 22.01.2010
 * @version 22.01.2010
 */

@XmlAccessorType(XmlAccessType.FIELD) 
@XmlRootElement
public class DBTable {

  public static final String FIELD = "COLUMN_NAME";
  public static final String TYPE = "COLUMN_TYPE";
  public static final String NULL = "IS_NULLABLE";
  public static final String KEY = "COLUMN_KEY";
  public static final String DEFAULT = "COLUMN_DEFAULT";
  public static final String EXTRA = "EXTRA";
  public static final String VALUE = "VALUE";
  public static final String SQL = "SQL";
  public static final String COND = "CONDITIONAL";

  private String schema;
  private String table;

  // e lá vai o martelo!
  private List<String> fields;
  private List<String> types;
  private List<String> nullables;
  private List<String> keys;
  private List<String> defaults;
  private List<String> extras;
  private List<String> values;
  private List<String> sql;
  private List<String> conds;

  public DBTable() {
    this(null);
  }

  public DBTable(String table) {
    this(null, table);
  }

  public DBTable(String schema, String table) {
    this(schema, table, null);
  }

  public DBTable(String table, Map<String, List<String>> columns) {
    this(null, table, columns);
  }

  public DBTable(String schema, String table, Map<String, List<String>> columns) {
    super();
    this.schema = schema;
    this.table = table;

    if (columns == null) {
      columns = new HashMap<String, List<String>>();
    }
    Iterator<String> iter = columns.keySet().iterator();
    while (iter.hasNext()) {
      String key = iter.next();
      for (String value : columns.get(key)) {
        DBTableHelper.addItem(this, key, value, -1);
      }
    }
  }

  public String getSchema() {
    return schema;
  }

  public void setSchema(String schema) {
    this.schema = schema;
  }

  public String getTable() {
    return table;
  }

  public void setTable(String table) {
    this.table = table;
  }

  public List<String> getFields() {
    return fields;
  }

  public void setFields(List<String> fields) {
    this.fields = fields;
  }

  public List<String> getTypes() {
    return types;
  }

  public void setTypes(List<String> types) {
    this.types = types;
  }

  public List<String> getNullables() {
    return nullables;
  }

  public void setNullables(List<String> nullables) {
    this.nullables = nullables;
  }

  public List<String> getKeys() {
    return keys;
  }

  public void setKeys(List<String> keys) {
    this.keys = keys;
  }

  public List<String> getDefaults() {
    return defaults;
  }

  public void setDefaults(List<String> defaults) {
    this.defaults = defaults;
  }

  public List<String> getExtras() {
    return extras;
  }

  public void setExtras(List<String> extras) {
    this.extras = extras;
  }

  public List<String> getValues() {
    return values;
  }

  public void setValues(List<String> values) {
    this.values = values;
  }

  public List<String> getConds() {
    return conds;
  }

  public void setConds(List<String> conds) {
    this.conds = conds;
  }

  public List<String> getSql() {
    return sql;
  }

  public void setSql(List<String> sql) {
    this.sql = sql;
  }

}
