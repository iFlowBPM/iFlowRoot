package pt.iflow.api.utils.series;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;

public class NumericSeriesProcessor extends SeriesProcessor {

  public static final String TYPE = NumericSeriesProcessor.class.getName();
  public static final String PROCESSOR = "NUMERIC";
  
  protected NumericSeriesProcessor() {
    this(null,null);
  }
  
  public NumericSeriesProcessor(UserInfoInterface userInfo) {
    this(userInfo, null);
  }

  public NumericSeriesProcessor(UserInfoInterface userInfo, String name) {

    super(userInfo);
    
    _processor = PROCESSOR;
    _type = TYPE;
    _name = name;
    _enabled = true;
    _state = STATE_NEW;

    // initialize with default values
    _basePattern = NUMBER_TAG;
    _pattern = _basePattern;
    _format = NUMBER_TAG + "=#";
    _startWith = NUMBER_TAG + "=1";

    _value = null;
  }

  @Override
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

     // rs.close();
      rs = null;


      int currValue = Integer.parseInt(getParamValue(NUMBER_TAG, _startWith));

      if (StringUtils.isNotEmpty(_value)) {
        currValue = Integer.parseInt(getParamValue(NUMBER_TAG, _value));
        currValue++;
      }

      _state = STATE_USED;

      if (StringUtils.isNotEmpty(_maxValue)) {
        int maxValue = Integer.parseInt(getParamValue(NUMBER_TAG, _maxValue));
        if (currValue > maxValue) 
          _state = STATE_BURNED;
      }


      if (_state == STATE_USED) {
        _value = setParamValue(NUMBER_TAG, String.valueOf(currValue)); 

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
    	  try {
        if (db != null){  db.rollback(); db.close(); }
    	  }catch (Exception ee) {
            throw new SeriesException(ee);
          }
        }
      }

      //DatabaseInterface.closeResources(db, pstUpd, pstGet, rs);
	    try { if(null != db) db.close(); } catch (SQLException e) {}
	    try { if(null != pstUpd) pstUpd.close(); } catch (SQLException e) {}
	    try { if(null != pstGet) pstGet.close(); } catch (SQLException e) {}
	   
	    
    return getCurrentValue();
  
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
    int currYear = Calendar.getInstance().get(Calendar.YEAR);
    myPattern = myPattern.replace(YEAR_TAG, String.valueOf(currYear));
    return new DecimalFormat(myPattern);
  }

  @Override
  public String preview() {
    return getFormatter().format(123);
  }


  public void setPattern(String prefix, String suffix) throws SeriesException {
    String myPrefix = StringUtils.isNotEmpty(prefix) ? "'" + prefix + "'" : "";
    String mySuffix = StringUtils.isNotEmpty(suffix) ? "'" + suffix + "'" : "";
    this.internalSetPattern(myPrefix + _basePattern + mySuffix);
  }

  public void setFormat(int numberDigits) throws SeriesException {
    setFormat(numberDigits, '0');
  }

  public void setFormat(int numberDigits, char padding) throws SeriesException {
    if (numberDigits > 0)
      this.internalSetFormat(setParamValue(NUMBER_TAG, StringUtils.rightPad("", numberDigits, padding)));
  }

  public void setStartWith(int startWith) throws SeriesException {
    this.internalSetStartWith(setParamValue(NUMBER_TAG, String.valueOf(startWith)));
  }

  public void setMaxValue(int maxValue) throws SeriesException {
    this.internalSetMaxValue(setParamValue(NUMBER_TAG, String.valueOf(maxValue)));
  }

}
