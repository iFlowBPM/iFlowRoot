package pt.iflow.api.datatypes;

import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import pt.iflow.api.blocks.FormService;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessVariableValue;
import pt.iflow.api.utils.ServletUtils;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iknow.utils.html.FormData;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public interface DataTypeInterface {

  // act as a constructor
  public void init (UserInfoInterface userInfo, ProcessData procData, 
      Map<String,String> extraProps, Map<String,Object> deps);

  public int getID();
  public String getDescription();
  public String getShortDescription();
  public String getPrimitiveType();
  public String getPrimitiveTypeMethod();
  public String getFormPrefix();
  public String getFormPrefix(Object[] aoaArgs);
  public String getFormSuffix();
  public String getFormSuffix(Object[] aoaArgs);

  public String format(UserInfoInterface userInfo, 
      ProcessData procData,
      FormService service,
      int fieldNumber,
      boolean isOutput,
      String name,
      ProcessVariableValue value, 
      Properties props,
      ServletUtils response);

  public String formatRow(UserInfoInterface userInfo, 
      ProcessData procData,
      FormService service,
      int fieldNumber,
      boolean isOutput,
      int varIndex,
      String name,
      int row,
      ProcessVariableValue value, 
      Properties props,
      ServletUtils response);

  public String parseAndSet(UserInfoInterface userInfo,
      ProcessData procData, String name, FormData formData, Properties props,
      boolean ignoreValidation, StringBuilder logBuffer);

  public String parseAndSetList(UserInfoInterface userInfo,
      ProcessData procData, int varIndex, String name, int count, FormData formData, Properties props,
      boolean ignoreValidation, StringBuilder logBuffer);

  
  public void formPreProcess(UserInfoInterface userInfo,
      ProcessData procData, String name, Properties props, StringBuilder logBuffer); 
  
  
  /**
   * @deprecated
   */
  public String formatToHtml(Object input);
  /**
   * @deprecated
   */
  public String formatToHtml(Object input, Object[] aoaArgs);
  /**
   * @deprecated
   */
  public String formatToForm(Object input);
  /**
   * @deprecated
   */
  public String formatToForm(Object input, Object[] aoaArgs);
  /**
   * @deprecated
   */
  public String validateFormData(Object input);
  /**
   * @deprecated
   */
  public String validateFormData(Object input, Object[] aoaArgs);
  /**
   * @deprecated
   */
  public String getText(Object input);
  /**
   * @deprecated
   */
  public double getValue(Object input);
  public void setLocale(Locale locale);
}
