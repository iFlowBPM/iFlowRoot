package pt.iflow.api.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.presentation.DateUtility;
import pt.iflow.api.processdata.EvalException;
import pt.iflow.api.processdata.ProcessData;
import pt.iknow.utils.crypt.CryptUtils;

public class Utils {

  private final static String sLIST_PREFIX = "@";
  private final static String sLIST_SUFFIX = "_#_";
  private static final CryptUtils _crypt = new CryptUtils("achave");

  public static final long ONE_MINUTE_MS = 1000*60;
  public static final long ONE_HOUR_MS = ONE_MINUTE_MS*60;
  public static final long ONE_DAY_MS = ONE_HOUR_MS*24;

  private static boolean doLog = true;
  
  public static DataSource getDataSource() {
    return Utils.getGenericDataSource(null);
  }

  public static DataSource getUserDataSource(String asDBPool) {
    if(!Const.INSTALL_LOCAL.equals(Const.INSTALL_TYPE) && isSystemDataSource(asDBPool)) return null; 

    return getGenericDataSource(asDBPool);
  }

  private static DataSource getGenericDataSource(String asDBPool) {
    DataSource retObj = null;

    if(asDBPool == null) {
      asDBPool = Const.NAME_DB_POOL;
    }

    try {
      InitialContext ctxt = new InitialContext ();
      retObj = (DataSource) ctxt.lookup(asDBPool);
      doLog = true;
    }
    catch (NamingException e) {
      if (doLog) {
        e.printStackTrace();
        doLog = false;
      }
    }


    return retObj;
  }

  public static boolean isSystemDataSource(String dsName) {
    return (dsName == null || dsName.equals(Const.NAME_DB_POOL));
  }


  /**
   * List all datasources available to this user
   * @param userInfo
   * @return
   */
  public static String [] getDataSources(UserInfoInterface userInfo) {
    ArrayList<String> nameList = new ArrayList<String>();

    try {
      InitialContext ctxt = new InitialContext ();
      final String baseCtxName = "java:comp/env";
      listContex((Context) ctxt.lookup(baseCtxName), baseCtxName, nameList);
      // TODO custom and per organization datasources

    }
    catch (NamingException e) {
      e.printStackTrace();
    }

    return nameList.toArray(new String [nameList.size()]);
  }

  private static void listContex(Context current, String currentName, List<String> names) throws NamingException {
    NamingEnumeration<Binding> ctxNames = current.listBindings("");
    while(ctxNames.hasMore()) {
      Binding name = ctxNames.next();
      String fullName = current.composeName(name.getName(), currentName);//currentName+CTX_NAME_SEP+name.getName();
      Logger.debug("", "Utils", "listContex", " name: '"+name.getName()+"' is relative? "+name.isRelative()+" class: "+name.getClassName()+" Full name: "+fullName);

      Object obj = name.getObject();
      if(obj instanceof DataSource) {
        if(Const.INSTALL_LOCAL.equals(Const.INSTALL_TYPE) || !isSystemDataSource(fullName))
          names.add(fullName);
      } else if(obj instanceof Context) {
        listContex((Context) obj, fullName, names);
      }

    }

  }

  /**
   * Generates the HRef tag for an anchor depending on abReadOnly.
   *
   * @param asLink the link
   * @param abReadOnly flag that indicates if this link should be 
   *                   read only/disabled
   * @return the generated html.
   */
  public static String genHRef(String asLink, boolean abReadOnly) {
    String retObj = "href=\"";

    if (abReadOnly) {
      retObj += "#\" disabled ";
    }
    else {
      retObj += asLink + "\" ";
    }

    return retObj;
  }


  /**
   * Escape a string to use as JavaScript string.
   * 
   * @see StringEscapeUtils.escapeJavaScript(Writer out, String str)
   * 
   * @param text
   * The text string to escape
   * @return The escaped string.
   */
  public static String escapeJavaScript(String text) {
    if (text == null) {
      return null;
    }
    try {
      StringWriter writer = new StringWriter ((int)(text.length() * 1.5));
      StringEscapeUtils.escapeJavaScript(writer, text);
      return writer.toString();
    } catch (IOException e) {
      //assert false;
      //should be impossible
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Checks if a given var represents a list (used in flow settings)
   *
   * @param asVar the var to check
   * @return true if it represents a list
   */
  public static boolean isListVar(String asVar) {
    boolean retObj = false;

    try {
      if (asVar.startsWith(sLIST_PREFIX)) {
        retObj = true;
      }
    }
    catch (Exception e) {
    }

    return retObj;
  }

  /**
   * Gets a list var name (without the index, if present) (used in flow settings)
   *
   * @param asVar the var to check
   * @return var name
   */
  public static String getListVarName(String asVar) {
    String retObj = asVar;

    try {
      int ntmp = asVar.indexOf(sLIST_SUFFIX);
      if (ntmp > -1) {
        retObj = asVar.substring(0,ntmp);
      }
    }
    catch (Exception e) {
    }

    return retObj;
  }

  /**
   * Gets an  unlisted var name (without the index, if present) (used in flow settings)
   *
   * @param asVar the var to check
   * @return var name without list prefix and index
   */
  public static String unlistVarName(String asVar) {
    String retObj = asVar;

    try {
      int ntmp = asVar.indexOf(sLIST_PREFIX);
      if (ntmp > -1) {
        ntmp += sLIST_PREFIX.length();
        retObj = asVar.substring(ntmp);
        retObj = Utils.getListVarName(retObj);
      }
    }
    catch (Exception e) {
    }

    return retObj;
  }

  /**
   * Gets a list index (used in flow settings)
   *
   * @param asVar the var to check
   * @return index or -1 if no index
   */
  public static int getListIndex(String asVar) {
    int retObj = -1;

    try {
      String stmp = null;
      int ntmp = asVar.indexOf(sLIST_SUFFIX);
      if (ntmp > -1) {
        stmp = asVar.substring((ntmp + sLIST_SUFFIX.length()));
        retObj = Integer.parseInt(stmp);
      }
    }
    catch (Exception e) {
    }

    return retObj;
  }

  /**
   * Generates a list var (with index) (used in flow settings)
   *
   * @param asVar the var
   * @param anIndex the index
   * @return var name
   */
  public static String genListVar(String asVar, int anIndex) {
    return asVar + sLIST_SUFFIX + anIndex;
  }


  public static List<String> tokenize (String asSource, String asSeparator) {
    List<String> retObj = null;

    if (StringUtils.isEmpty(asSource) ||
        StringUtils.isEmpty(asSeparator)) {
      return null;
    }

    String[] vals = asSource.split(asSeparator);

    retObj = new ArrayList<String>();
    for (String val : vals) {
      if (StringUtils.isNotEmpty(val))
        val = val.trim();
      retObj.add(val);
    }

    return retObj;
  }

  public static String unTokenize (List<String> aalData, String asSeparator) {
    return StringUtils.join(aalData, asSeparator);
  }

  public static String genEventAppendex(String currentAppendex, String event, String append) {
    String retObj = (currentAppendex != null ? currentAppendex : "");

    if(retObj.contains(event)) {
      if(retObj.contains(event + "=\"javascript:")) {
        retObj = retObj.replace(event + "=\"javascript:", event + "=\"javascript:" + append);
      } else {
        retObj = retObj.replace(event + "=\"", event + "=\"" + append);
      }
    } else {
      retObj += " " + event + "=\"" + append + "\"";
    }

    return retObj;
  }

  public static String genHtmlSelect(String asName,
      String asSelectExtra,
      String asSelected,
      List<String> aalValues,
      List<String> aalNames) {

    StringBuffer retObj = new StringBuffer();

    if (aalValues == null || aalNames == null) {
      return "";
    }
    if (asSelectExtra == null) {
      asSelectExtra = "";
    }
    if (asSelected == null) {
      asSelected = "";
    }

    retObj.append("<select name=\"").append(asName).append("\"");
    retObj.append(" id=\"").append(asName).append("\"");
    retObj.append(" ").append(asSelectExtra).append(">");
    for (int i=0; i < aalValues.size(); i++) {
      retObj.append("<option value=\"").append(aalValues.get(i)).append("\"");
      if (asSelected.equals(aalValues.get(i))) {
        retObj.append(" selected");
      }
      retObj.append(">");
      retObj.append(aalNames.get(i)).append("</option>");  
    }
    retObj.append("</select>");

    return retObj.toString();
  }


  public static String replaceString(String asString, String asOldString, String asNewString) {
    int idx = -1;
    try {
      idx = asString.indexOf(asOldString);
      if (idx == -1) {
        return asString;
      }
    }
    catch (Exception e) {
      return asString;
    }

    StringBuffer retObj = new StringBuffer(asString);
    try {
      int nOldSize = asOldString.length();
      int nNewSize = asNewString.length();

      while (idx > -1) {
        try {
          retObj = retObj.replace(idx,idx+nOldSize,asNewString);
          idx = retObj.indexOf(asOldString,idx+nNewSize);
        }
        catch (Exception ei) {
          break;
        }
      }
    }
    catch (Exception ei) {
      return asString;
    }

    return retObj.toString();
  }

  public static String replaceStringIgnoreCase(String asString, String asOldString, String asNewString) {
    int idx = -1;
    String lowerString = null;
    String lowerOld = null;
    try {
      lowerString = asString.toLowerCase();
      lowerOld = asOldString.toLowerCase();
      idx = lowerString.indexOf(lowerOld);
      if (idx == -1) {
        return asString;
      }
    }
    catch (Exception e) {
      return asString;
    }

    StringBuffer retObj = new StringBuffer(asString);
    try {
      int nOldSize = asOldString.length();
      int nNewSize = asNewString.length();

      while (idx > -1) {
        try {
          retObj = retObj.replace(idx,idx+nOldSize,asNewString);
          idx = (lowerString = retObj.toString().toLowerCase()).indexOf(lowerOld,idx+nNewSize);
        }
        catch (Exception ei) {
          break;
        }
      }
    }
    catch (Exception ei) {
      return asString;
    }

    return retObj.toString();
  }

  public static String formatDecimalNumberSeparator(String asString) {

    String ret = asString;

    //if(asString.lastIndexOf(",") == asString.length()-3)
    ret = Utils.replaceString(asString,",",".");

    return ret;
  }



  public static boolean checkPattern(String asPattern, String asString) {
    boolean retObj = true;

    try {
      char[] ca = asPattern.toCharArray();
      char[] castring = asString.toCharArray();
      char[] digits = { '.',',','0','1','2','3','4','5','6','7','8','9' };
      int idx = 0;
      int ntmp = 0;
      StringBuffer sbtmp = null;
      StringBuffer sbtmp2 = null;

      for (int i=0; i < ca.length; i++) {
        switch (ca[i]) {
        case '#':
          sbtmp = new StringBuffer();
          sbtmp.append(castring[idx++]);
          ntmp = i+1;
          if (ntmp < ca.length) {
            if (ca[ntmp] == '+') {
              i = ntmp;
              for (; idx < castring.length; idx++) {
                for (ntmp=0; ntmp < digits.length; ntmp++) {
                  if (digits[ntmp] == castring[idx]) {
                    sbtmp.append(castring[idx]);
                    break;
                  }
                }
                if (ntmp == digits.length) {
                  break;
                }
              }
            }
            else if (ca[ntmp] == '{') {
              i = ntmp + 1;
              sbtmp2 = new StringBuffer();
              for (; ca[i] != '}'; i++) {
                sbtmp2.append(ca[i]);
              }
              i++;

              ntmp = Integer.parseInt(sbtmp2.toString());
              ntmp--; // first # already processed
              for (int j=0; j < ntmp; j++,idx++) {
                sbtmp.append(castring[idx]);
              }
            }
            new BigDecimal(sbtmp.toString());
          }
          break;
        default:
          idx++;
        }
      }
      if (idx < castring.length) {
        retObj = false;
      }
    }
    catch (Exception e) {
      retObj = false;
    }

    return retObj;
  }


  /**
   * @deprecated Use DatabaseInterface.closeResources
   * @see pt.iflow.api.db.DatabaseInterface#closeResources(Object...)
   * @param resources
   */
  public static void closeDB(Object... resources) {
    DatabaseInterface.closeResources(resources);
  }


  public static java.util.Date getFormDate(HttpServletRequest aRequest,
      String asRequestName) {
    java.util.Date retObj = null;

    String sYear = aRequest.getParameter(asRequestName + "_year");
    String sMonth = aRequest.getParameter(asRequestName + "_month");
    String sDay = aRequest.getParameter(asRequestName + "_day");
    String sHour = aRequest.getParameter(asRequestName + "_hour");
    String sMinute = aRequest.getParameter(asRequestName + "_minute");
    String sSecond = aRequest.getParameter(asRequestName + "_second");

    if (sYear == null || sYear.equals("") || sYear.equals("-1")) {
      sYear = null;
    }
    if (sMonth == null || sMonth.equals("") || sMonth.equals("-1")) {
      sMonth = null;
    }
    if (sDay == null || sDay.equals("") || sDay.equals("-1")) {
      sDay = null;
    }
    if (sHour == null || sHour.equals("") || sHour.equals("-1")) {
      sHour = null;
    }
    if (sMinute == null || sMinute.equals("") || sMinute.equals("-1")) {
      sMinute = null;
    }
    if (sSecond == null || sSecond.equals("") || sSecond.equals("-1")) {
      sSecond = null;
    }
    if (sYear == null && sMonth == null && sDay == null && 
        sHour == null && sMinute == null &&  sSecond == null) {
      String date = aRequest.getParameter(asRequestName);
      if(date == null) {
        return retObj;
      } else {
        String[] items = date.split("\\/|\\.|\\-", 3);
        if(items.length == 3) {
          sMonth = items[1];
          if(items[0].length() > 2) {
            sYear = items[0];
            sDay = items[2];
          } else {
            sDay = items[0];
            sYear = items[2];
          }
        }
      }
    }

    if (sMonth != null && sMonth.length() == 1) sMonth = "0" + sMonth;
    if (sDay != null && sDay.length() == 1) sDay = "0" + sDay;

    String stmp = "";
    String stmp2 = "";
    if (sYear != null) {
      stmp = sYear;
      stmp2 = "yyyy";
    }
    if (sMonth != null) {
      stmp += "-" + sMonth;
      stmp2 += "-MM";
    }
    if (sDay != null) {
      stmp += "-" + sDay;
      stmp2 += "-dd";
    }
    if (sHour != null) {
      stmp += " " + sHour;
      stmp2 += " HH";
    }
    if (sMinute != null) {
      stmp += ":" + sMinute;
      stmp2 += ":mm";
    }
    if (sSecond != null) {
      stmp += ":" + sSecond;
      stmp2 += ":ss";
    }

    SimpleDateFormat dateFormatter = new SimpleDateFormat(stmp2);

    try {
      retObj = dateFormatter.parse(stmp);
    }
    catch (Exception e) {
    }

    return retObj;
  }

  public static String formatFormDate(String asRequestName, java.util.Date adtDate) {
    return formatFormDate(asRequestName, adtDate, true);
  }

  public static String formatFormDate(String asRequestName, java.util.Date adtDate, boolean abStatus) {
    StringBuffer retObj = new StringBuffer();

    String sYear = "-1";
    String sMonth = "-1";
    String sDay = "-1";
    String sHour = "-1";
    String sMinute = "-1";

    int nStartYear = 2003;

    String stmp = null;
    ArrayList<String> altmp = null;
    ArrayList<String> altmp2 = null;
    Calendar calNow = Calendar.getInstance();
    String sExtra = "class=\"txt\"";
    if (!abStatus) {
      sExtra += " disabled";
    }

    if (adtDate != null) {
      try {
        sYear = Utils.date2string(adtDate,"yyyy");
        sMonth = Utils.date2string(adtDate,"MM");
        sDay = Utils.date2string(adtDate,"dd");
        sHour = Utils.date2string(adtDate,"HH");
        sMinute = Utils.date2string(adtDate,"mm");
      }
      catch (Exception e) {
      }
    }

    altmp = new ArrayList<String>();
    altmp2 = new ArrayList<String>();
    altmp.add("-1");
    altmp2.add("Ano");
    for (int i=nStartYear; i <= calNow.get(Calendar.YEAR); i++) {
      stmp = String.valueOf(i);
      altmp.add(stmp);
      altmp2.add(stmp);    
    }
    retObj.append(Utils.genHtmlSelect(asRequestName + "_year",sExtra,sYear,altmp,altmp2));
    retObj.append("&nbsp;");

    altmp = new ArrayList<String>();
    altmp2 = new ArrayList<String>();
    altmp.add("-1");
    altmp2.add("Mes");
    for (int i=1; i <= 12; i++) {
      stmp = String.valueOf(i);
      if (stmp.length() == 1) stmp = "0" + stmp;
      altmp.add(stmp);
      altmp2.add(stmp);    
    }
    retObj.append(Utils.genHtmlSelect(asRequestName + "_month",sExtra,sMonth,altmp,altmp2));
    retObj.append("&nbsp;");

    altmp = new ArrayList<String>();
    altmp2 = new ArrayList<String>();
    altmp.add("-1");
    altmp2.add("Dia");
    for (int i=1; i <= 31; i++) {
      stmp = String.valueOf(i);
      if (stmp.length() == 1) stmp = "0" + stmp;
      altmp.add(stmp);
      altmp2.add(stmp);    
    }
    retObj.append(Utils.genHtmlSelect(asRequestName + "_day",sExtra,sDay,altmp,altmp2));
    retObj.append("&nbsp;");

    altmp = new ArrayList<String>();
    altmp2 = new ArrayList<String>();
    altmp.add("-1");
    altmp2.add("Hora");
    for (int i=0; i <= 23; i++) {
      stmp = String.valueOf(i);
      if (stmp.length() == 1) stmp = "0" + stmp;
      altmp.add(stmp);
      altmp2.add(stmp);    
    }
    retObj.append(Utils.genHtmlSelect(asRequestName + "_hour",sExtra,sHour,altmp,altmp2));
    retObj.append("&nbsp;");

    altmp = new ArrayList<String>();
    altmp2 = new ArrayList<String>();
    altmp.add("-1");
    altmp2.add("Min");
    for (int i=0; i <= 59; i += 5) {
      stmp = String.valueOf(i);
      if (stmp.length() == 1) stmp = "0" + stmp;
      altmp.add(stmp);
      altmp2.add(stmp);    
    }
    retObj.append(Utils.genHtmlSelect(asRequestName + "_minute",sExtra,sMinute,altmp,altmp2));
    retObj.append("&nbsp;");

    return retObj.toString();
  }


  public static String genSQLDate(java.util.Date adtDate) {
    String retObj = null;

    try {
      String stmp = Utils.date2string(adtDate,"yyyy-MM-dd HH:mm:ss");
      if (stmp == null) throw new Exception();

      retObj = "to_date('" + stmp + "','YYYY-MM-DD HH24:MI:SS')";
    }
    catch (Exception e) {
      retObj = null;
    }

    return retObj;
  }


  public static String date2string(java.util.Date adtDate) {
    return Utils.date2string(adtDate,Const.sDEF_DATE_FORMAT);
  }

  public static String date2string(java.util.Date adtDate, String asDtFormat) {
    String retObj = null;

    try {
      SimpleDateFormat sdf = new SimpleDateFormat(asDtFormat);
      retObj = sdf.format(adtDate);
    }
    catch (Exception e) {
      retObj = null;
    }

    return retObj;    
  }


  public static java.util.Date string2date(String asDate) {
    return Utils.string2date(asDate,Const.sDEF_DATE_FORMAT);
  }

  public static java.util.Date string2date(String asDate, String asDtFormat) {
    java.util.Date retObj = null;

    try {
      SimpleDateFormat sdf = new SimpleDateFormat(asDtFormat);
      retObj = sdf.parse(asDate);
    }
    catch (Exception e) {
      retObj = null;
    }

    return retObj;    
  }

  public static boolean string2bool(String str) {
    return StringUtils.equalsIgnoreCase(str, "1")
    || StringUtils.equalsIgnoreCase(str, "true")
    || StringUtils.equalsIgnoreCase(str, "yes")
    || StringUtils.equalsIgnoreCase(str, "y")
    || StringUtils.equalsIgnoreCase(str, "sim")
    || StringUtils.equalsIgnoreCase(str, "s");
  }

  public static String getDuration(Timestamp tsStart, Timestamp tsStop) {
    String retObj = "";

    if (tsStart == null || tsStop == null) return retObj;

    try {
      long dias = (tsStop.getTime()-tsStart.getTime())/86400000;
      long t = (tsStop.getTime()-tsStart.getTime()) % 86400000;
      long horas = t/3600000;
      t = t % 3600000;
      long minutos = t/60000;
      t = t % 60000;
      long segundos = t/1000;
      if (dias != 0) {
        retObj = retObj+dias+"d,"+horas+"h,"+minutos+"m,"+segundos+"s";
      } else if (horas != 0) {
        retObj = retObj+horas+"h,"+minutos+"m,"+segundos+"s";
      } else if (minutos != 0) {
        retObj = retObj+minutos+"m,"+segundos+"s";
      } else if (segundos != 0) {
        retObj = retObj+segundos+"s";
      } else {
        retObj = "0s";
      }
    }
    catch (Exception e) {
      retObj = "";
    }

    return retObj;
  }


  /**
   * Gets the difference between two dates in minutes
   * considering only the workdays
   * @param d1 
   * @param d2 
   */
  public static long workMinutesDifference(java.util.Date d1, java.util.Date d2) {
    java.util.Date dAfter = null;
    java.util.Date dBefore = null;
    if (d1.after(d2)) { dAfter = d1; dBefore = d2; }
    else { dAfter = d2; dBefore = d1; }

    Calendar cBefore = Calendar.getInstance();
    cBefore.clear(); cBefore.setLenient(true);
    cBefore.setTime(dBefore);
    Calendar cAfter = Calendar.getInstance();
    cAfter.clear(); cAfter.setLenient(true);
    cAfter.setTime(dAfter);

    long totalDiff = (cAfter.getTime().getTime() - cBefore.getTime().getTime());
    long totalDiffMinut = totalDiff / ONE_MINUTE_MS;

    long diffWeekend = weekendMinutes(d1,d2);
    //  long weekendDays = weekendDaysBetweenExclusive(cBefore, cAfter);
    //  long weekendTime = weekendTime(cBefore, cAfter);
    // remove weekends from total diff
    //  long timeDiff = totalDiff - weekendTime - (weekendDays*ONE_DAY_MS);
    // convert to minutes
    long minuteDiff = totalDiffMinut - diffWeekend;

    return minuteDiff;
  }

  public static long weekendDaysBetween(Calendar cBefore, Calendar cAfter) {
    long daysdiff = (cAfter.getTime().getTime() - cBefore.getTime().getTime()) / (1000*60*60*24);
    return daysdiff - Utils.workDaysBetween(cBefore, cAfter);
  }

  //Excui as horas do dia inicial e do dia final
  public static long weekendDaysBetweenExclusive(Calendar cBefore, Calendar cAfter) {
    Calendar cIni = (Calendar)cBefore.clone();
    Calendar cEnd = (Calendar)cAfter.clone();
    cIni.set(Calendar.HOUR, 23);
    cIni.set(Calendar.MINUTE, 59);
    cIni.set(Calendar.SECOND, 59);
    cIni.set(Calendar.MILLISECOND, 0);
    cIni.add(Calendar.SECOND, 1);
    cEnd.set(Calendar.HOUR, 0);
    cEnd.set(Calendar.MINUTE, 0);
    cEnd.set(Calendar.SECOND, 0);
    cEnd.set(Calendar.MILLISECOND, 0);
    return weekendDaysBetween(cBefore, cAfter);
  }

  public static long workDaysBetween(Calendar cBefore, Calendar cAfter) {
    long daysdiff = (cAfter.getTime().getTime() - cBefore.getTime().getTime()) / (1000*60*60*24);
    if (daysdiff == 0) return 0;

    long weekends = daysdiff/7;
    long workdays = weekends*5;

    if (cBefore.get(Calendar.DAY_OF_WEEK) >= cAfter.get(Calendar.DAY_OF_WEEK)) {

      if (cBefore.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY &&
          cBefore.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
        workdays += (Calendar.SATURDAY - cBefore.get(Calendar.DAY_OF_WEEK));
      }

      if (cAfter.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY &&
          cAfter.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
        workdays += (cAfter.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY - 1);
      }

    } else {

      workdays += (cAfter.get(Calendar.DAY_OF_WEEK) - cBefore.get(Calendar.DAY_OF_WEEK));
      if (cBefore.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
        workdays--;

    }

    return workdays;
  }

  public static long weekendTime(Calendar cBefore, Calendar cAfter) {
    long res = 0l;
    Calendar cIni = cBefore;
    Calendar cEnd = cAfter;
    if (cBefore.after(cAfter)) {
      cIni = cAfter;
      cEnd = cBefore;
    }

    if (cIni.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cIni.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
      Calendar dt = Calendar.getInstance();
      dt.set(Calendar.DAY_OF_MONTH, cIni.get(Calendar.DAY_OF_MONTH));
      dt.set(Calendar.MONTH, cIni.get(Calendar.MONTH));
      dt.set(Calendar.YEAR, cIni.get(Calendar.YEAR));
      //Se for o mesmo dia
      if (cIni.get(Calendar.DAY_OF_MONTH) == cEnd.get(Calendar.DAY_OF_MONTH) && 
          cIni.get(Calendar.MONTH) == cEnd.get(Calendar.MONTH) && 
          cIni.get(Calendar.YEAR) == cEnd.get(Calendar.YEAR)) {
        dt.set(Calendar.HOUR_OF_DAY, cEnd.get(Calendar.HOUR_OF_DAY));
        dt.set(Calendar.MINUTE, cEnd.get(Calendar.MINUTE));
        dt.set(Calendar.SECOND, cEnd.get(Calendar.SECOND));
        dt.set(Calendar.MILLISECOND, cEnd.get(Calendar.MILLISECOND));
      } else {
        dt.set(Calendar.HOUR_OF_DAY, 0);
        dt.set(Calendar.MINUTE, 0);
        dt.set(Calendar.SECOND, 0);
        dt.set(Calendar.MILLISECOND, 0);
        dt.add(Calendar.DAY_OF_MONTH, 1);
      }
      res += (dt.getTime().getTime() - cIni.getTime().getTime());
    }

    if (cEnd.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cEnd.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
      Calendar dt = Calendar.getInstance();
      dt.set(Calendar.DAY_OF_MONTH, cEnd.get(Calendar.DAY_OF_MONTH));
      dt.set(Calendar.MONTH, cEnd.get(Calendar.MONTH));
      dt.set(Calendar.YEAR, cEnd.get(Calendar.YEAR));
      //Se for o mesmo dia
      if (cIni.get(Calendar.DAY_OF_MONTH) == cEnd.get(Calendar.DAY_OF_MONTH) && 
          cIni.get(Calendar.MONTH) == cEnd.get(Calendar.MONTH) && 
          cIni.get(Calendar.YEAR) == cEnd.get(Calendar.YEAR)) {
        dt.set(Calendar.HOUR_OF_DAY, cEnd.get(Calendar.HOUR_OF_DAY));
        dt.set(Calendar.MINUTE, cEnd.get(Calendar.MINUTE));
        dt.set(Calendar.SECOND, cEnd.get(Calendar.SECOND));
        dt.set(Calendar.MILLISECOND, cEnd.get(Calendar.MILLISECOND));
      } else {
        dt.set(Calendar.HOUR_OF_DAY, 0);
        dt.set(Calendar.MINUTE, 0);
        dt.set(Calendar.SECOND, 0);
        dt.set(Calendar.MILLISECOND, 0);
      }
      res += (cEnd.getTime().getTime() - dt.getTime().getTime());
    }
    return res;
  }

  public static int getWorkingDays(java.util.Date inicialDate,java.util.Date finalDate) {
    int dia_semana_inicial = 0;
    int dia_semana_final = 0;
    long dif = 0;
    int diferenca = 0;
    int semanas = 0;
    int dias_semana = 0;
    int dias_uteis = 0;

    Calendar cInicial = null;
    Calendar cFinal = null;

    cInicial = Calendar.getInstance();	
    cFinal = Calendar.getInstance(); 

    dif = finalDate.getTime() - inicialDate.getTime();
    diferenca = (int)(dif)/(1000*60*60*24);

    cInicial.setTime(inicialDate);
    cFinal.setTime(finalDate);

    dia_semana_inicial = cInicial.get(Calendar.DAY_OF_WEEK);
    dia_semana_final = cFinal.get(Calendar.DAY_OF_WEEK);

    semanas = (int)(diferenca)/7;
    dias_semana = (int)(diferenca)%7;
    dias_uteis = semanas * 5;

    if(dia_semana_inicial > dia_semana_final ) {
      dias_uteis += (dias_semana-2);
    }
    else if(dia_semana_final==7){
      dias_uteis += (dias_semana-1);	
    }
    else if(dia_semana_inicial==1){
      dias_uteis += (dias_semana-1);	
    }
    else {
      dias_uteis += dias_semana;
    }    

    return dias_uteis;
  }

  public static String encrypt(String toEncrypt) {
    return _crypt.encrypt(toEncrypt);
  }

  public static String decrypt(String toDecrypt) {
    return _crypt.decrypt(toDecrypt);
  }

  public static String transformStringAndPrepareForDB(UserInfoInterface userInfo, String asString, ProcessData adsDataSet) {
    return transformStringAndPrepareForDB(userInfo, asString, adsDataSet, "'");
  }

  public static String transformStringAndPrepareForDB(UserInfoInterface userInfo, String asString, ProcessData adsDataSet, String escapeSequence) {

    String retObj = asString;

    String sPattern = "_#C#__3MMM3__#C#_";
    // adjust pattern
    while (retObj.indexOf(sPattern) > -1) {
      sPattern += "0";
    }

    if (retObj != null && !retObj.equals("")) {
      retObj = Utils.replaceString(retObj, "'", sPattern);
      try {
        retObj = adsDataSet.transform(userInfo, retObj, true);
      } catch (EvalException e) {
        Logger.error("", Utils.class, "transformStringAndPrepareForDB", "Unable to transform string: " + Utils.replaceString(retObj, sPattern, "'"), e);
      }
    }
    if (retObj.equals("")) retObj = null;

    if (retObj != null && !retObj.equals("")) {
      retObj = Utils.replaceString(retObj, "'", escapeSequence + "'");
      retObj = Utils.replaceString(retObj, sPattern, "'");
    }

    return retObj;
  }

  private static Set<String> systemTables = null;

  public static Set<String> getSystemTables() {
    if (null == systemTables) {
      systemTables = new HashSet<String>();
      systemTables.add("activity");
      systemTables.add("activity_history");
      systemTables.add("counter");
      systemTables.add("data_date");
      systemTables.add("data_date_history");
      systemTables.add("data_numeric");
      systemTables.add("data_numeric_history");
      systemTables.add("data_string");
      systemTables.add("data_string_history");
      systemTables.add("dirty_email");
      systemTables.add("email");
      systemTables.add("flow");
      systemTables.add("flow_history");
      systemTables.add("flow_template");
      systemTables.add("sub_flow");
      systemTables.add("sub_flow_history");
      systemTables.add("sub_flow_template");
      systemTables.add("flow_roles");
      systemTables.add("flow_settings");
      systemTables.add("flow_settings_history");
      systemTables.add("flow_state");
      systemTables.add("flow_state_history");
      systemTables.add("modification");
      systemTables.add("process");
      systemTables.add("queue_data");
      systemTables.add("queue_proc");
      systemTables.add("tat91_tabelas");
      systemTables.add("trx_options");
      systemTables.add("users");
    }

    return systemTables;
  }



  public static Date fixDateAfter(Date after) {
    if (after != null) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(after);
      cal.set(Calendar.HOUR, 0);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MILLISECOND, 0);
      after = cal.getTime();
    }      
    return after;
  }

  public static Date fixDateBefore(Date before) {
    if (before != null) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(before);
      cal.set(Calendar.HOUR, 0);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MILLISECOND, 0);
      cal.add(Calendar.DATE, 1);
      before = cal.getTime();
    }      
    return before;
  }

  /**
   * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
   * 
   * @param packageName
   *          The base package
   * @return The classes
   * @throws ClassNotFoundException
   * @throws IOException
   * @see {@link #findClasses(File, String) }
   * @see {@link http://snippets.dzone.com/posts/show/4831 }
   */
  @SuppressWarnings("unchecked")
  public static List<Class> getClasses(String packageName) throws ClassNotFoundException, IOException {
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    assert classLoader != null;
    String path = packageName.replace('.', '/');
    Enumeration<URL> resources = classLoader.getResources(path);
    List<File> dirs = new ArrayList<File>();
    while (resources.hasMoreElements()) {
      URL resource = resources.nextElement();
      String fileName = resource.getFile();
      String fileNameDecoded = URLDecoder.decode(fileName, "UTF-8");
      dirs.add(new File(fileNameDecoded));
    }
    ArrayList<Class> classes = new ArrayList<Class>();
    for (File directory : dirs) {
      classes.addAll(findClasses(directory, packageName));
    }
    return classes;
  }


  /**
   * Recursive method used to find all classes in a given directory and subdirs.
   * 
   * @param directory
   *          The base directory
   * @param packageName
   *          The package name for classes found inside the base directory
   * @return The classes
   * @throws ClassNotFoundException
   * @see {@link #getClasses(String) }
   * @see {@link http://snippets.dzone.com/posts/show/4831 }
   */
  @SuppressWarnings("unchecked")
  private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
    List<Class> classes = new ArrayList<Class>();
    if (!directory.exists()) {
      String jarName = directory.getPath();
      if (StringUtils.isNotBlank(jarName)) {
        String sEnd = "jar";
        String sStart = "file:";
        int end = StringUtils.indexOf(jarName, sEnd);
        if (end > -1) {
          jarName = jarName.substring(0, end + sEnd.length());
          int start = StringUtils.indexOf(jarName, sStart);
          if (start > -1) {
            jarName = jarName.substring(start + sStart.length());
          }
          classes = getClasseNamesInPackage(jarName, packageName);
        }
      }
      return classes;
    }
    File[] files = directory.listFiles();
    for (File file : files) {
      String fileName = file.getName();
      if (file.isDirectory()) {
        assert !fileName.contains(".");
        classes.addAll(findClasses(file, packageName + "." + fileName));
      } else if (fileName.endsWith(".class") && !fileName.contains("$")) {
        try {
          classes.add(loadClass(packageName, fileName));
        } catch (ClassNotFoundException e) {
          // TODO
          e.printStackTrace();
        }
      }
    }
    return classes;
  }

  /**
   * Finds all classes in the Jar's package.
   * 
   * @param jarName
   *          Jar file name.
   * @param packageName
   *          Package to search in Jar File.
   * @return The classes
   * @see {@link http://www.rgagnon.com/javadetails/java-0513.html }
   */
  @SuppressWarnings("unchecked")
  private static List<Class> getClasseNamesInPackage(String jarName, String packageName) {
    List<Class> classes = new ArrayList<Class>();
    packageName = packageName.replaceAll("\\.", "/");
    try {
      JarInputStream jarFile = new JarInputStream(new FileInputStream(jarName));
      JarEntry jarEntry;

      while (true) {
        jarEntry = jarFile.getNextJarEntry();
        if (jarEntry == null) {
          break;
        }
        if ((jarEntry.getName().startsWith(packageName)) && (jarEntry.getName().endsWith(".class"))) {
          try {
            classes.add(loadClass(jarEntry.getName().replaceAll("/", "\\.")));
          } catch (ClassNotFoundException e) {
            // TODO
            e.printStackTrace();
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return classes;
  }

  @SuppressWarnings("unchecked")
  private static Class loadClass(String fileName) throws ClassNotFoundException {
    if (StringUtils.isBlank(fileName)) {
      return null;
    }
    String sEnd = ".class";
    if (StringUtils.endsWithIgnoreCase(fileName, sEnd)) {
      fileName = StringUtils.substring(fileName, 0, fileName.length() - sEnd.length());
    }
    String packageName = "";
    String[] split = fileName.split("\\.");
    for (int i = 0; i < split.length; i++) {
      if (i == split.length-1) {
        fileName = split[i];
      } else {
        packageName += "." + split[i];
      }
    }
    packageName = packageName.replaceFirst("\\.", "");
    fileName = fileName + sEnd;
    return loadClass(packageName, fileName);
  }

  @SuppressWarnings("unchecked")
  private static Class loadClass(String packageName, String fileName) throws ClassNotFoundException {
    Class retObj;
    try {
      retObj = Class.forName(packageName + '.' + fileName.substring(0, fileName.length() - 6));
    } catch (ExceptionInInitializerError e) {
      // happen, for example, in classes, which depend on Spring to inject some beans,
      // and which fail, if dependency is not fulfilled
      retObj = Class.forName(packageName + '.' + fileName.substring(0, fileName.length() - 6), false, Thread.currentThread()
          .getContextClassLoader());
    }
    return retObj;
  }

  /**
   * Gets the difference between two dates in minutes
   * considering only the workdays
   * @param d1 
   * @param d2 
   */
  public static long weekendMinutes(java.util.Date d1, java.util.Date d2) {
    java.util.Date dAfter = null;
    java.util.Date dBefore = null;
    if (d1.after(d2)) { dAfter = d1; dBefore = d2; }
    else { dAfter = d2; dBefore = d1; }

    Calendar cBefore = Calendar.getInstance();
    cBefore.clear(); cBefore.setLenient(true);
    cBefore.setTime(dBefore);
    Calendar cAfter = Calendar.getInstance();
    cAfter.clear(); cAfter.setLenient(true);
    cAfter.setTime(dAfter);

    int n = getWorkingDays(d1,d2);    
    int t = (cAfter.get(Calendar.DAY_OF_YEAR) - cBefore.get(Calendar.DAY_OF_YEAR));
    int fds = t - n;

    return (fds*24*60);
  }

  public static boolean checkStringToPattern (String value, String pattern){
    Pattern p = Pattern.compile(pattern);
    Matcher m = p.matcher(value);
    return m.matches();
  }

  public static String genFormDate(HttpServletResponse response, UserInfoInterface userInfo, String asRequestName, java.util.Date adtDate, String asFieldID) {
    StringBuffer retObj = new StringBuffer();

    String sDate = "";
    if (adtDate != null) {
      sDate = DateUtility.formatFormDate(userInfo, adtDate);
    }

    retObj.append("<input class=\"calendaricon\" type=\"text\" size=\"12\"");
    retObj.append(" id=\"").append(asFieldID).append("\"");
    retObj.append(" name=\"").append(asRequestName).append("\"");
    retObj.append(" value=\"").append(sDate).append("\"");
    retObj.append(" onmouseover=\"caltasks(this.id);this.onmouseover=null;\"/>");

    retObj.append("<img class=\"icon_clear\" src=\"images/icon_delete.png\"");
    retObj.append(" onclick=\"javascript:document.getElementById('" + asFieldID + "').value='';\"/>");

    return retObj.toString();
  }
}
