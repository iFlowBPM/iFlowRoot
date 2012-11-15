package pt.iflow.applet;

public class StringUtils {

  public static boolean isBlank(String s) {
    return s == null || s.trim().length()==0;
  }

  public static boolean isNotBlank(String s) {
    return s != null && s.trim().length()>0;
  }

  public static boolean isNotEmpty(String s) {
    return s != null && s.length()>0;
  }

  public static boolean isEmpty(String s) {
    return s == null || s.length()==0;
  }

}
