package pt.iflow.api.datatypes;

import java.text.Format;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.FormProps;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessListVariable;
import pt.iflow.api.processtype.ProcessDataType;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;

class DataTypeUtils {

  private DataTypeUtils() {}
  
  public static void setObject(final UserInfoInterface userInfo, final ProcessData procData, final String var, 
      final Object value, final Format formatter) {
    Setter setter = new Setter() {
      public void set(Object value) {
        procData.set(var, value);
        Logger.debug(userInfo.getUtilizador(), this, "set", 
            "set " + var + "(" + procData.getVariableDataType(var) + ") with " + 
            value + "(" + (value != null ? value.getClass().getName() : "null") + ")");
      }
    };
    internalSetObject(userInfo, procData, setter, var, -1, value, formatter);
  }
  
  public static void setListObject(final UserInfoInterface userInfo, final ProcessData procData, final String var, final int position, 
      final Object value, final Format formatter) {
    final ProcessListVariable listVar = procData.getList(var);
    Setter setter = new Setter() {
      public void set(Object value) {
        listVar.setItemValue(position, value);
        Logger.debug(userInfo.getUtilizador(), this, "set", 
            "set list item " + var + "[" + position + "](" + procData.getVariableDataType(var) + ") with " + 
            value + "(" + (value != null ? value.getClass().getName() : "null") + ")");
      }
    };
    internalSetObject(userInfo, procData, setter, var, position, value, formatter);
  }  
  
  private static void internalSetObject(final UserInfoInterface userInfo, final ProcessData procData, final Setter setter, final String var, final int position, 
      final Object value, final Format formatter) {
   
    ProcessDataType varType = procData.getVariableDataType(var);
    
    if (varType == null) {
      Logger.error(userInfo.getUtilizador(), "DataTypeUtils", "internalSetObject", 
          procData.getSignature() + "NULL type for var '" + var + "'. Check if it's defined in catalogue.");
      throw new NullPointerException("NULL type for var " + var);
    }
    
    Object myValue = value;
    if (value != null) {
      Class<?> varClass = varType.getSupportingClass();
      if (!varClass.isAssignableFrom(value.getClass())) {
        String strValue = formatter != null ? formatter.format(value) : String.valueOf(value);
        if (varClass.isAssignableFrom(Number.class)) {          
          try {
            myValue = java.lang.Double.parseDouble(strValue);
          }
          catch (NumberFormatException nfe) {
          }
        }
        else if (varClass.isAssignableFrom(String.class)) {      
          myValue = strValue; 
        }
      }
    }
    setter.set(myValue);
    
  }

  
  private interface Setter {
    public void set(Object value);
  }


  static boolean hidePrefix(Map<String, String> props) {
    String hp = props != null ? props.get(FormProps.PROP_HIDE_PREFIX) : null;
    return StringUtils.equals(hp, "true"); 
  }
  static boolean hideSufix(Map<String, String> props) {
    String hs = props != null ? props.get(FormProps.PROP_HIDE_SUFIX) : null;
    return StringUtils.equals(hs, "true"); 
  }
}
