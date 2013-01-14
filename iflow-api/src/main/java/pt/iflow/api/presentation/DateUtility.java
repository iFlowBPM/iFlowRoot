package pt.iflow.api.presentation;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.utils.UserInfoInterface;

/**
 * 
 * This class provides some utility methods for date and time manipulation (timezone, etc)
 * @author oscar
 *
 */
public class DateUtility {

  
  private static String[] timezones = null;

  // TODO workout user locale and date symbols
  private static final String FORM_TODAY = "EEEE, dd.MM.yyyy";
  private static final String FORM_DATE = "dd/MM/yyyy";
  private static final String PROC_TIMESTAMP = "yyyy-MM-dd'&nbsp;<span class=\"unem\">['HH:mm']</span>'";

  
  public synchronized static String[] getAvailableTimezones() {
    if(null == timezones) {
      DecimalFormat df = new DecimalFormat("'GMT'+00':00';'GMT'-00':00'");
      ArrayList<String> tzs = new ArrayList<String>(29);
      tzs.add("UTC");
      tzs.add("GMT");
      tzs.add(TimeZone.getDefault().getID());
      for(int i = -12; i < 15; i++) {
        tzs.add(df.format(i));
      }
      timezones = tzs.toArray(new String[tzs.size()]);
    }
    return timezones;
  }
  
  public static String getToday(UserInfoInterface userInfo) {
    SimpleDateFormat formatter = new SimpleDateFormat(FORM_TODAY, userInfo.getUserSettings().getLocale());
    formatter.setTimeZone(userInfo.getUserSettings().getTimeZone());

    Date now = new Date();
    return formatter.format(now);
  }
  
  public static String formatTimestamp(UserInfoInterface userInfo, Date date) {
    TimeZone tz = userInfo.getUserSettings().getTimeZone();
    SimpleDateFormat fmt = new SimpleDateFormat(PROC_TIMESTAMP);
    fmt.setTimeZone(tz);
    return fmt.format(date);
  }
  
  public static String formatFormDate(UserInfoInterface userInfo, Date date) {
    TimeZone tz = userInfo.getUserSettings().getTimeZone();
    SimpleDateFormat fmt = new SimpleDateFormat(FORM_DATE);
    fmt.setTimeZone(tz);
    return fmt.format(date);
  }
  
  public static Date parseFormDate(UserInfoInterface userInfo, String date) throws ParseException {
    TimeZone tz = userInfo.getUserSettings().getTimeZone();
    SimpleDateFormat fmt = new SimpleDateFormat(FORM_DATE);
    fmt.setTimeZone(tz);
    return fmt.parse(date);
  }
  
  public static String formatUserDate(UserInfoInterface userInfo, Date date) {
    TimeZone tz = userInfo.getUserSettings().getTimeZone();
    Locale loc = userInfo.getUserSettings().getLocale();
    Calendar cal = Calendar.getInstance(tz, loc);
    SimpleDateFormat fmt = new SimpleDateFormat();
    fmt.setCalendar(cal);
    return fmt.format(date);
  }
  
  public static Date parseUserDate(UserInfoInterface userInfo, String date) throws ParseException {
    TimeZone tz = userInfo.getUserSettings().getTimeZone();
    Locale loc = userInfo.getUserSettings().getLocale();
    Calendar cal = Calendar.getInstance(tz, loc);
    SimpleDateFormat fmt = new SimpleDateFormat();
    fmt.setCalendar(cal);
    return fmt.parse(date);
  }
  
  public static String getMonthName(int month, Locale locale) {
    DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(locale);
    return dateFormatSymbols.getMonths()[month];
  }
  
  
  // Block methods use Organization time/location
  
  public static String newBlockDate(String orgId, String fmt) {
    return formatBlockDate(orgId, fmt, new Date());
  }
  
  public static DateFormat getBlockDateFormat(String orgId, String fmt) {
    TimeZone tz = BeanFactory.getSettingsBean().getOrganizationTimeZone(orgId);
    Locale loc = BeanFactory.getSettingsBean().getOrganizationLocale(orgId);
    Calendar cal = Calendar.getInstance(tz, loc);
    SimpleDateFormat formatter = new SimpleDateFormat(fmt);
    formatter.setCalendar(cal);
    return formatter;
  }
  
  public static String formatBlockDate(String orgId, String fmt, Date date) {
    DateFormat formatter = getBlockDateFormat(orgId, fmt);
    return formatter.format(date);
  }
  
  public static DateUtilityHelper getHelper(UserInfoInterface userInfo) {
    return new DateUtilityHelper(userInfo);
  }
  
  public static class DateUtilityHelper {
    private UserInfoInterface userInfo;
    public DateUtilityHelper(UserInfoInterface userInfo) {
      this.userInfo = userInfo;
    }
    public String getToday() {
      return DateUtility.getToday(userInfo);
    }
    
    public String formatTimestamp(Date date) {
      return DateUtility.formatTimestamp(userInfo,date);
    }
    
    public String formatFormDate(Date date) {
      return DateUtility.formatFormDate(userInfo,date);
    }
    
    public Date parseFormDate(String date) throws ParseException {
      return DateUtility.parseFormDate(userInfo,date);
    }
    
    public String formatUserDate(Date date) {
      return DateUtility.formatUserDate(userInfo,date);
    }
    
    public Date parseUserDate(String date) throws ParseException {
      return DateUtility.parseUserDate(userInfo,date);
    }
    
    public String getMonthName(int month) {
      return DateUtility.getMonthName(month, userInfo.getUserSettings().getLocale());
    }
  }

}
