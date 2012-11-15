package pt.iflow.api.utils.series;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.Format;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;


public abstract class SeriesProcessor {

  protected static final String NUMBER_TAG = "{number}";
  protected static final String YEAR_TAG = "{year}";
  protected static final String NUMBER_YEAR_TAG = NUMBER_TAG + "'/'" + YEAR_TAG;

  private static final String PARAM_SEP = ";";
  
  public static final int STATE_NEW = 0;
  public static final int STATE_USED = 1;
  public static final int STATE_BURNED = 2;

  // key formats for messages
  public static final String DESCRIPTION_KEY_FORMAT = "series.{0}.description"; // 0: series processor
  public static final String STATE_DESCRIPTION_KEY_FORMAT = "series.state_descriptions.{0}";  // 0: series state 


  protected static final String ID = "id";
  protected static final String CREATED = "created";
  protected static final String ENABLED = "enabled";
  protected static final String STATE = "state";
  protected static final String NAME = "name";
  protected static final String TYPE = "kind";
  protected static final String PATTERN = "pattern";
  protected static final String PATTERN_FIELD_FORMATS = "pattern_field_formats";
  protected static final String START_WITH = "start_with";
  protected static final String MAX_VALUE = "max_value";
  protected static final String VALUE = "current_value";
  protected static final String EXTRA_OPTIONS = "extra_options";

  protected static final String ORGID = "organizationid";

  protected int _id = -1;

  protected boolean _gotNext = false;

  protected String _name;
  protected String _processor;	
  protected String _type;
  protected Timestamp _created;
  protected boolean _enabled;
  protected int _state;

  protected String _basePattern; 

  protected String _pattern;
  protected String _format;
  protected String _startWith;
  protected String _maxValue;
  protected String _extraOptions; 

  protected String _value;

  protected String _orgid; 

  protected SeriesProcessor() {
  }
  
  protected SeriesProcessor(UserInfoInterface userInfo) {
    if (userInfo != null) {
      _orgid = userInfo.getOrganization();
    }
  }

  protected void load(Map<String,String> data) {
    _id = Integer.valueOf(data.get(ID));
    
    _name = data.get(NAME);
    _created = new Timestamp(Long.parseLong(data.get(CREATED))); 
    _enabled = data.get(ENABLED).equals("1");
    _state = Integer.parseInt(data.get(STATE));

    _pattern = data.get(PATTERN);
    _format = data.get(PATTERN_FIELD_FORMATS);

    _startWith = data.get(START_WITH);
    _maxValue = data.get(MAX_VALUE);

    _extraOptions = data.get(EXTRA_OPTIONS);

    _orgid = data.get(ORGID);

    _value = data.get(VALUE);
  }

  public int getId() {
    return _id;
  }

  protected void setId(int id) {
    _id = id;
  }

  public String getOrganizationId() {
    return _orgid;
  }


  public String getName() {
    return _name;
  }

  public void setName(String name) throws SeriesException {

    if (!inDB()) {
      // not stored in db... ok to set name
      _name = name;
      return;
    }
    else {
      throw new ReadOnlyException(this);
    }
  }

  public String getProcessor() {
    return _processor;
  }

  public boolean isEnabled() {
    return _enabled;
  }

  public void enable() throws SeriesException {
    setEnabled(true);
  }

  public void disable() throws SeriesException {
    setEnabled(false);
  }

  private void setEnabled(boolean enabled) throws SeriesException {

    if (!inDB()) {
      // not stored in db...
      _enabled = enabled;
      return;
    }

    DataSource ds = null;
    Connection db = null;
    PreparedStatement pstGet = null;
    PreparedStatement pstUpd = null;
    ResultSet rs = null;

    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      db.setAutoCommit(false);


      pstGet = db.prepareStatement(SeriesManager.GET_FOR_UPDATE);
      pstGet.setInt(1, getId());
      pstGet.setString(2, getOrganizationId());
      rs = pstGet.executeQuery();

      rs.next();

      _enabled = rs.getBoolean(ENABLED);

      rs.close();
      rs = null;


      if (_enabled == enabled) {
        db.commit();
        return; // no need to make changes 
      }

      _enabled = enabled;
      pstUpd = db.prepareStatement(SeriesManager.UPDATE_ENABLED_STATE);
      pstUpd.setBoolean(1, _enabled);
      pstUpd.setInt(2, getId());
      pstUpd.setString(3, getOrganizationId());
      pstUpd.executeUpdate();

      db.commit();

    }
    catch (Exception e) {
      if (db != null) { 
        try {
          db.rollback();
        }
        catch (Exception ee) {
          throw new SeriesException(ee);
        }
      }

      throw new SeriesException(e);
    }
    finally {
      DatabaseInterface.closeResources(db, pstUpd, pstGet, rs);
    }
  }

  public int getState() {
    return _state;
  }

  public String getType() {
    return _type;
  }

  public Timestamp getCreated() {
    return _created;
  }

  public String getPattern() {
    return _pattern;
  }

  protected void internalSetPattern(String pattern) throws SeriesException {
    if (inDB())
      throw new ReadOnlyException(this);

    _pattern = pattern;
  }

  public String getFormat() {
    return _format;
  }
  
  protected void internalSetFormat(String format) throws SeriesException {
    if (inDB())
      throw new ReadOnlyException(this);

    _format = format;
  }

  public String getStartWith() {
    return _startWith;	
  }

  protected void internalSetStartWith(String startWith) throws SeriesException {
    if (inDB())
      throw new ReadOnlyException(this);

    _startWith = startWith;
  }

  public String getMaxValue() {
    return _maxValue;		
  }

  protected void internalSetMaxValue(String maxValue) throws SeriesException {
    if (inDB())
      throw new ReadOnlyException(this);

    _maxValue = maxValue;
  }

  protected boolean inDB() {
    return getId() >= 0;
  }

  public abstract String getNext() throws SeriesException;

  public abstract String getCurrentValue() throws SeriesException;

  protected abstract Format getFormatter();

  public abstract String preview();

  protected static String getParamValue(String tag, String param) {
    if (StringUtils.isEmpty(param))
      return param;

    String toSearch = tag + "=";
    int offset = toSearch.length();
    int index = param.indexOf(toSearch);
    if (index == -1) {
      return null;
    }
    
    int startIndex = index + offset;
    int endIndex = param.indexOf(PARAM_SEP, index);
    if (endIndex == -1)
      endIndex = param.length();
    return param.substring(startIndex, endIndex);
  }

  protected static String setParamValue(String tag, String param) {
    if (StringUtils.isEmpty(param))
      return param;

    return tag + "=" + param;
  }
  
  protected static String appendParamValue(String currTag, String tag, String param) {
    String ret = "";
    if (StringUtils.isNotEmpty(currTag)) {
      ret = currTag + PARAM_SEP;
    }
    ret += setParamValue(tag, param);
    
    return ret;
  }

  public String getExtraOptions() {
    return _extraOptions;	  
  }

  public void setExtraOptions(String extraOptions) {
    _extraOptions = extraOptions;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("{");

    sb.append("id=").append(getId()).append(",");
    sb.append("orgid=").append(getOrganizationId()).append(",");
    sb.append("name=").append(getName()).append(",");
    sb.append("created=").append(getCreated()).append(",");
    sb.append("type=").append(getType()).append(",");
    sb.append("enabled=").append(isEnabled()).append(",");
    sb.append("state=").append(getState()).append(",");
    sb.append("pattern=").append(getPattern()).append(",");
    sb.append("format=").append(getFormat()).append(",");
    sb.append("startWith=").append(getStartWith()).append(",");
    sb.append("maxvalue=").append(getMaxValue()).append(",");
    sb.append("extraOptions=").append(getExtraOptions());

    sb.append("}");

    return sb.toString();
  }
}

