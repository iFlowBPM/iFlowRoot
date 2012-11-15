package pt.iflow.api.utils;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.flows.Flow;
import pt.iflow.api.flows.FlowSetting;
import pt.iflow.api.processdata.EvalException;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessListVariable;

public class DataSetUtils {

  public static String getListTextForValue(UserInfoInterface auiUserInfo,
      ProcessData process,
      String asValueVar,
      String asValuesVar,
      String asNamesVar) {


    String sValText = null;

    if (StringUtils.isEmpty(asValuesVar)) {
      return sValText;
    }

    String sVal = null;
    try {
      sVal = process.transform(auiUserInfo, asValueVar);
    } catch (EvalException e1) {
    }

    try {
      if (StringUtils.isNotEmpty(asValuesVar) && StringUtils.isNotEmpty(asNamesVar) && StringUtils.isNotEmpty(sVal)) {

        ProcessListVariable vNames = process.getList(asNamesVar);
        ProcessListVariable vValues = process.getList(asValuesVar);


        if (null != vNames && null != vValues) {
          int ntmp = vValues.size();
          for (int i=0; i < ntmp; i++) {
            String sValue = vValues.getFormattedItem(i);
            if (StringUtils.equals(sValue, sVal)) {
              String sName = vNames.getFormattedItem(i);
              if (StringUtils.isNotEmpty(sName)) {
                sValText = sName;
              }
            }
          }
        }

        // try in flow settings
        if (sValText == null) {
          Flow flow = BeanFactory.getFlowBean();
          FlowSetting[] fsa = flow.getFlowSettings(auiUserInfo, process.getFlowId());
          int valuesPosition = -1;
          for (int i=0; fsa != null && i < fsa.length; i++) {
            valuesPosition = -1;
            if (fsa[i].getName().equals(asValuesVar) && fsa[i].isListSetting()) {
              String [] asValues = fsa[i].getValues(auiUserInfo, process);
              for (int ii=0; asValues != null && ii < asValues.length; ii++) {
                if (asValues[ii].equals(sVal)) {
                  valuesPosition = ii;
                  break;
                }
              }
              if (valuesPosition > -1) {
                break;
              }
            }
          }
          if (valuesPosition > -1) {
            if (StringUtils.isNotEmpty(asNamesVar)) {
              for (int i=0; fsa != null && i < fsa.length; i++) {
                if (fsa[i].getName().equals(asNamesVar) && fsa[i].isListSetting()) {
                  String [] asValues = fsa[i].getValues(auiUserInfo, process);
                  if (asValues.length > valuesPosition && StringUtils.isNotEmpty(asValues[valuesPosition])) {
                    sValText = asValues[valuesPosition];
                  }
                  break;
                }
              }
            }
          }
          fsa = null;
          flow = null;
        }
      }
    }
    catch (Exception e) {
      Logger.error(auiUserInfo.getUtilizador(), "DataSetUtils", "getListTextForValue", "Unable to get text: " + e.getMessage(), e);
      sValText = null;
    }

    if (StringUtils.isNotEmpty(sValText)) {
      sValText = sVal;
    }

    return sValText;

  }



  public static String[] getListVarValues(UserInfoInterface userInfo, 
      String asVarName,
      ProcessData adsDataSet) {
    String[] retObj = null;

    try {
      Flow flow = BeanFactory.getFlowBean();

      FlowSetting[] fsa = flow.getFlowSettings(userInfo, adsDataSet.getFlowId());

      for (int i=0; fsa != null && i < fsa.length; i++) {
        if (fsa[i].getName().equals(asVarName) && fsa[i].isListSetting()) {
          retObj = fsa[i].getValues(userInfo, adsDataSet);
          break;
        }
      }
    }
    catch (Exception e) {
      Logger.error(userInfo.getUtilizador(), "DataSetUtils", "getListVarValues", "Unable to get values: " + e.getMessage());
      retObj = null;
    }


    return retObj;
  }


  public static boolean isArrayVar(String var) {
    if (StringUtils.isEmpty(var))
      return false;
    var = var.trim();
    return var.indexOf("[") > 0 && var.indexOf("]") == (var.length() -1);
  }

  public static int getArrayVarPosition(String var, ProcessData procData) {
    int res = -1;
    if (!isArrayVar(var))
      return -1;

    int idx = var.indexOf("[") + 1;
    String pos = var.substring(idx, var.indexOf("]", idx));
    if (StringUtils.isNumeric(pos)) {
      res = Integer.parseInt(pos);
    } else {
      res = Integer.parseInt(procData.getFormatted(pos));
    }
    return res;
  }

  public static String getArrayVarName(String var) {
    if (!isArrayVar(var))
      return null;

    return var.substring(0, var.indexOf("["));
  }

  // XXX copy from DataSet class
  
  public final static String sLIST_PREFIX_START = "[";
  public final static String sLIST_PREFIX_END = "]";
  private final static String sLIST_LENGTH_SUFFIX = ".length";
//private final static String sLIST_LENGTH_SUFFIX2 = "_length";

  /**
   * Gets a key without list index if it exists
   *
   * @param key the key to check.
   *
   * @return key without index
   */
  public static String getKey(String key) {
    String retObj = key;

    if (key == null) return retObj;

    if (getIndexFromKey(key) > -1) {
      try {
        retObj = key.substring(0,key.lastIndexOf(sLIST_PREFIX_START));
      }
      catch (Exception e) {
      }
    }

    if (retObj.endsWith(sLIST_LENGTH_SUFFIX)) {
      retObj = retObj.substring(0,retObj.indexOf(sLIST_LENGTH_SUFFIX));
    }
    // Removed: cannot have a variable ending with _length
//    else if (retObj.endsWith(sLIST_LENGTH_SUFFIX2)) {
//      retObj = retObj.substring(0,retObj.indexOf(sLIST_LENGTH_SUFFIX2));
//    }
//
    return retObj;
  }

  /**
   * Get fully qualified key name
   *
   * @param key the key name
   * @param iundex the key's index
   * @return String with fully qualified name.
   */
  public static String getListKey(String key, int index) {
    String retObj = key;

    int _index = getIndexFromKey(key);

    if (_index == -1) {
      retObj = key + sLIST_PREFIX_START + index + sLIST_PREFIX_END;
    }
    else if (_index != index) {
      return null;
    }

    return retObj;
  }

  /**
   * Checks a key to determine if it's an array key
   *
   * @param key the fully qualified key name
   * @return true if key is an array key; false otherwise
   */
  public static boolean isArrayKey(String key) {
    return (getIndexFromKey(key)>-1);
  }


  /**
   * Gets a key's list index if it exists
   *
   * @param key the key to check.
   *
   * @return int with array index or -1 if not applicable
   */
  protected static int getIndexFromKey(String key) {
    int retObj = -1;

    if (key == null) return retObj;

    String stmp = null;

    try {

      if (key.endsWith(sLIST_PREFIX_END)) {
        int idx = key.lastIndexOf(sLIST_PREFIX_START) + sLIST_PREFIX_START.length();
        stmp = key.substring(idx, key.indexOf(sLIST_PREFIX_END, idx));

        retObj = Integer.parseInt(stmp);
      }
    }
    catch (Exception e) {
    }

    return retObj;
  }


}
