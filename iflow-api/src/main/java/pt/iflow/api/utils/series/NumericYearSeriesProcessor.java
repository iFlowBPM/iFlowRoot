package pt.iflow.api.utils.series;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;


// TODO: use extra options attribute to store number-year separator, 
// value to start with after reset (now is always 1), 
// max value for year (now only limits number), ...

public class NumericYearSeriesProcessor extends NumericSeriesProcessor {

  public static final String TYPE = NumericYearSeriesProcessor.class.getName();
  public static final String PROCESSOR = "NUMERIC_YEAR";

  private static final int NUMBER = 0;
  private static final int YEAR = 1;

  protected NumericYearSeriesProcessor() {
    this(null, null);
  }

  public NumericYearSeriesProcessor(UserInfoInterface userInfo) {
    this(userInfo, null);
  }

  public NumericYearSeriesProcessor(UserInfoInterface userInfo, String name) {
    super(userInfo, name);

    _processor = PROCESSOR;
    _type = TYPE;

    // initialize with default values
    _basePattern = NUMBER_YEAR_TAG;
    _pattern = _basePattern;

  }

  public String getNext() throws SeriesException {
    
    if (!_enabled)
      throw new DisabledSeriesException(this);

    DataSource ds = null;
    Connection db = null;
    PreparedStatement pstGet = null;
    PreparedStatement pstUpd = null;
    ResultSet rs = null;

    boolean rollbackNeeded = false;

    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      db.setAutoCommit(false);


      pstGet = db.prepareStatement(SeriesManager.GET_FOR_UPDATE);
      pstGet.setInt(1, getId());
      pstGet.setString(2, getOrganizationId());
      rs = pstGet.executeQuery();

      rollbackNeeded = true;

      rs.next();

      _value = rs.getString(VALUE);
      int currState = rs.getInt(STATE);

      rs.close();
      rs = null;

      Calendar now = Calendar.getInstance();
      int nowYear = now.get(Calendar.YEAR);
      
      int[] currValues = new int[2];
      
      currValues[NUMBER] = Integer.parseInt(getParamValue(NUMBER_TAG, _startWith));
      currValues[YEAR] = nowYear;

      if (StringUtils.isNotEmpty(_value)) {
        currValues = parseValue(); 
        
        if (currValues[YEAR] == nowYear) {
          currValues[NUMBER]++;          
        }
        else if (nowYear < currValues[YEAR]) {
          //??? who discovered a time machine??
          Logger.error("seriesmanager", this, "getNext", "Stored year is after current year?!?! Going backwards?");
          throw new Exception("Stored year is after current year");
        }
        else {         
          currValues[NUMBER] = 1; // RESET NUMBER WHEN YEAR CHANGES!                   
          currValues[YEAR] = nowYear; // update year
        }
      }

      _state = STATE_USED;

      if (StringUtils.isNotEmpty(_maxValue)) {
        int maxValue = Integer.parseInt(getParamValue(NUMBER_TAG, _maxValue));
        if (currValues[NUMBER] > maxValue) 
          _state = STATE_BURNED;
        else {
          // check if year has also max value defined
          // TODO: define max year in maxvalue
          String maxYearStr = getParamValue(YEAR_TAG, _maxValue);
          if (StringUtils.isNotEmpty(maxYearStr)) {
            int maxYear = Integer.parseInt(maxYearStr);
            if (currValues[YEAR] > maxYear) 
              _state = STATE_BURNED;
          }
        }
      }


      if (_state == STATE_USED) {
        _value = appendParamValue(setParamValue(NUMBER_TAG, String.valueOf(currValues[NUMBER])), 
            YEAR_TAG, String.valueOf(currValues[YEAR])); 

        pstUpd = db.prepareStatement(SeriesManager.UPDATE_CURR_VALUE);
        pstUpd.setString(1, _value);
        pstUpd.setInt(2, _state);
        pstUpd.setInt(3, getId());
        pstUpd.setString(4, getOrganizationId());
        pstUpd.executeUpdate();

        db.commit();

        _gotNext = true;
      }
      else if (_state == STATE_BURNED && _state != currState) {
        pstUpd = db.prepareStatement(SeriesManager.UPDATE_STATE);
        pstUpd.setInt(1, _state);
        pstUpd.setInt(2, getId());
        pstUpd.setString(3, getOrganizationId());
        pstUpd.executeUpdate();

        db.commit();
      }

      rollbackNeeded = false;

      if (_state == STATE_BURNED) {
        if (_state != currState)
          throw new FirstOverlimitException(this); // first time
        else
          throw new OverlimitException(this);
      }

    }
    catch (SeriesException e) {
      throw e;
    }
    catch (Exception e) {
      throw new SeriesException(e);
    }
    finally {
      if (rollbackNeeded) {
        if (db != null) { 
          try {
            db.rollback();
          }
          catch (Exception ee) {
            throw new SeriesException(ee);
          }
        }
      }

      DatabaseInterface.closeResources(db, pstUpd, pstGet, rs);
    }

    return getCurrentValue();
  }

  private int[] parseValue() {
    int[] values = {-1,-1};

    String number = getParamValue(NUMBER_TAG, _value);
    if (number != null)
      values[NUMBER] = Integer.parseInt(number);
    
    String year = getParamValue(YEAR_TAG, _value);
    if (year != null)
      values[YEAR] = Integer.parseInt(year);
    
    return values;
  }
  
  @Override
  public String getCurrentValue() throws SeriesException {
    if (!_enabled)
      throw new DisabledSeriesException(this);

    if (StringUtils.isEmpty(_value))
      throw new NotInitializedException(this);

    double myValue = Double.parseDouble(getParamValue(NUMBER_TAG, _value));
    return getFormatter().format(myValue);
  }

  @Override
  protected NumberFormat getFormatter() {
    String myFormat = getParamValue(NUMBER_TAG, _format);
    String myPattern = _pattern.replace(NUMBER_TAG, myFormat);    
    String currYear = "";
    if (StringUtils.isEmpty(_value)) {
      Calendar now = Calendar.getInstance();
      currYear = String.valueOf(now.get(Calendar.YEAR));
    }
    else {
      currYear = getParamValue(YEAR_TAG, _value);
    }
    myPattern = myPattern.replace("'" + YEAR_TAG, String.valueOf(currYear) +"'");
    return new DecimalFormat(myPattern);
  }

}