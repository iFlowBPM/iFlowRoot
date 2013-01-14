package pt.iflow.api.utils;

import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.datatypes.Euro;
import pt.iflow.api.datatypes.Percentage;
import pt.iflow.api.msg.IMessages;


public class BshUtils {

  static final long ONE_HOUR = 60 * 60 * 1000L;
  
	// Implementar metodos publicos estaticos aqui para usar no bloco beanshell

  public static java.util.Date[] addDate(java.util.Date[] orig, java.util.Date date) {

    java.util.Date[] ret = null;
    if (orig == null) {
      ret = new java.util.Date[1];
    }
    else {
      ret = new java.util.Date[orig.length + 1];
      for (int i=0; i < orig.length; i++) {
        ret[i] = orig[i];
      }
    }
    
    ret[ret.length-1] = date;
    
    return ret;
  }

  
  public static String[] addString(String[] orig, String string) {

    String[] ret = null;
    if (orig == null) {
      ret = new String[1];
    }
    else {
      ret = new String[orig.length + 1];
      for (int i=0; i < orig.length; i++) {
        ret[i] = orig[i];
      }
    }
    
    ret[ret.length-1] = string;
    
    return ret;
  }

  public static String[] remString(String[] orig, int position) {

    String[] ret = null;
    if (orig != null && orig.length > 0) {
      ret = new String[orig.length - 1];
      for (int i=0, j=0; i < orig.length; i++) {
        if (i == position)
          continue;
        ret[j] = orig[i];
        j++;
      }
    }
    
    return ret;
  }

  public static String[] updString(String[] orig, String newValue, int position) {

    String[] ret = orig;
    if (orig != null) {
      if (orig.length <= position) {
        ret = new String[position+1];
        for (int i=0; i < orig.length; i++) {
          ret[i] = orig[i];
        }
      }
      ret[position] = newValue;
    }
    
    return ret;
  }

  public static String[] arrayTrim(String[] orig) {
    ArrayList<String> retArray = new ArrayList<String>();
    
    for (int i=0; i < orig.length; i++) {
      if (StringUtils.isNotEmpty(orig[i])) {
        retArray.add(orig[i]);
      }
    }
    
    String[] ret = new String[retArray.size()];
    return retArray.toArray(ret);
  }
  
  public static long daysBetween(Date d1, Date d2) {
    return ((d2.getTime() - d1.getTime() + ONE_HOUR) / (ONE_HOUR * 24));
  }    
  
  
  public static String worklog(UserInfoInterface userInfo, String worklog, String newtext) {
    if (StringUtils.isNotEmpty(newtext)) {
      if (StringUtils.isNotEmpty(worklog)) {
          worklog = worklog + "\r\n";
      }

      java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
      java.util.Date dt = new java.util.Date();
      
      worklog = worklog 
        + "[" + sdf.format(dt) + "] " + userInfo.getUtilizador() + ": " 
        + newtext;
        
    }
    return worklog;
  }
  
  public static String formatEur(UserInfoInterface userInfo, double value) {
    pt.iflow.api.datatypes.Euro eur = new Euro();
    eur.setLocale(userInfo.getUserSettings().getLocale());
    return eur.formatToHtml(value);    
  }
  
  public static String formatPercent(UserInfoInterface userInfo, double percent) {
    pt.iflow.api.datatypes.Percentage pct = new Percentage();
    pct.setLocale(userInfo.getUserSettings().getLocale());
    return pct.formatToHtml(percent);    
  }
  
  public static String formatDate(Date dt, String format) {
    if (dt == null) return "";
    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(format);
    return sdf.format(dt);    
  }

  /**
   * Returns a amount written in extended form
   * 
   * @param userInfo
   * @param value
   *          the monetary value
   * @return
   */

  public static String extendAmount(UserInfoInterface userInfo, double value) {
    IMessages msg = userInfo.getMessages();
    Extenso number = new Extenso(msg, value);
    return number.toString();
  }
}
