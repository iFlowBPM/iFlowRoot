package pt.iflow.api.processdata;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

import org.apache.commons.collections15.OrderedMap;
import org.apache.commons.collections15.map.ListOrderedMap;
import org.apache.commons.lang.StringUtils;

import pt.iflow.api.annotations.BindableMethod;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.ProcessCatalogue;
import pt.iflow.api.flows.FlowSetting;
import pt.iflow.api.processtype.DataTypeEnum;
import pt.iflow.api.userdata.UserData;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.DataSetVariables;
import pt.iknow.utils.StringUtilities;
/**
 * <p>Não é tão perfeito como queria, mas aqui vai uma explicação:</p>
 * <p>Para cada variavel dinâmica:
 * <ol>
 * <li>Criar um método sem argumentos que retorne o valor pretendido (por exemplo, getFlowId)</li>
 * <li>Adicionar a tag @BindableMethod e atribuir ao parametro <code>variables</code> um array 
 *     de strings com o nome das variaveis dinamicas</li>
 * </ol>
 * O resto deverá ser automático.</p>
 * 
 * <p>As variaveis que não estejam mapeadas nesta classe e que sejam definidas como dinâmicas, 
 * serão consideradas propriedades do fluxo (FlowSetting)</p>
 * 
 * @see BindableMethod
 * @see FlowSetting
 * @see ProcessCatalogue
 *  
 * @author ombl
 *
 */
public class DynamicBindDelegate implements BindDelegate {

  private static OrderedMap<String,Method> METHODS = new ListOrderedMap<String, Method>();

  static {
    Method[] methods = DynamicBindDelegate.class.getDeclaredMethods();//getMethods();
    for(Method method: methods) {
      BindableMethod annotation = method.getAnnotation(BindableMethod.class);
      if (annotation == null) continue;

      for(String var : annotation.variables()) {
        if(METHODS.containsKey(var))continue;
        METHODS.put(var, method);
      }
    }
  }
  
  public static String [] getDynamicVariables() {
    return METHODS.keySet().toArray(new String[METHODS.size()]);
  }
  
  public static DataTypeEnum getDynamicVariableDataType(String var) {
    DataTypeEnum retObj = DataTypeEnum.Text;
    Method method = METHODS.get(var);
    if(method != null) {
      BindableMethod annotation = method.getAnnotation(BindableMethod.class);
      if (annotation != null) {
        retObj = annotation.dataType();
      }
    }
    return retObj;
  }

  private ProcessData process;
  private String userName = null;
  private String fullName = null;
  private String userId = null;

  DynamicBindDelegate(ProcessData process) {
    this.process = process;
  }

  private static Method getMethod(String variable) {
    return METHODS.get(variable);
  }
  
  // check for process ownership changes and update users accordingly
  private void loadUser() {
    String currentUser = this.process.getCurrentUser();
    if(StringUtils.isEmpty(currentUser))
      currentUser = this.process.getCreator();
    
    if(!StringUtils.equals(userName, currentUser)) {
      UserData userInfo = BeanFactory.getAuthProfileBean().getUserInfo(currentUser);
      userName = userInfo == null ? null : currentUser;
      String aux = userInfo.get(UserData.EMPLOYEE_NUMBER);
      if (StringUtilities.isEmpty(aux)) aux = userInfo.get(UserData.EMPLOYEE_NUMBER_DEPRECATED);
      userId = userInfo == null ? null : aux;
      fullName = userInfo == null ? null : userInfo.get(UserData.FULL_NAME);
    }
  }
  
  // UserInfo binds
  
  @SuppressWarnings("unused")
  @BindableMethod(variables={DataSetVariables.USER_ID,DataSetVariables.UTILIZADOR})
  private String getUtilizador() {
    loadUser();
    return userName;
  }

  @SuppressWarnings("unused")
  @BindableMethod(variables={DataSetVariables.NOMEUTILIZADOR,DataSetVariables.NOMEUTILIZADORABREV})
  private String getUserFullName() {
    loadUser();
    return fullName;
  }

  @SuppressWarnings("unused")
  @BindableMethod(variables={DataSetVariables.NUMEMPREGADO})
  private String getUserId() {
    loadUser();
    return userId;
  }

  // Process binds
  
  @SuppressWarnings("unused")
  @BindableMethod(variables={Const.sPROCESS_LOCATION})
  private String getLocation() {
    return process.isInDB()?Const.sPROCESS_IN_DB:Const.sPROCESS_IN_SESSION;
  }

  @SuppressWarnings("unused")
  @BindableMethod(variables={DataSetVariables.FLOWID}, dataType=DataTypeEnum.Integer)
  private int getFlowId() {
    return process.getFlowId();
  }

  @SuppressWarnings("unused")
  @BindableMethod(variables={DataSetVariables.PID}, dataType=DataTypeEnum.Integer)
  private int getPid() {
    return process.getPid();
  }

  @SuppressWarnings("unused")
  @BindableMethod(variables={DataSetVariables.SUBPID}, dataType=DataTypeEnum.Integer)
  private int getSubPid() {
    return process.getSubPid();
  }

  @SuppressWarnings("unused")
  @BindableMethod(variables={DataSetVariables.PNUMBER})
  private String getPNumber() {
    return process.getPNumber();
  }

  @SuppressWarnings("unused")
  @BindableMethod(variables={DataSetVariables.PROCESS_CREATOR})
  private String getCreator() {
    return process.getCreator();
  }

  @SuppressWarnings("unused")
  @BindableMethod(variables={DataSetVariables.PROCESS_CREATION_DATE}, dataType=DataTypeEnum.Date)
  private Date getCreationDate() {
    return process.getCreationDate(); // TODO Como tenho acesso ao userinfo tb seria bom formatar isto
  }

  private String getFlowSetting(String settingName) {
    // try FlowSetting
    FlowSetting setting = BeanFactory.getFlowSettingsBean().getFlowSetting(process.getFlowId(), settingName);
    return setting.getValue();
  }

  public Object invoke(String variable) {
    Method method = getMethod(variable);
    if(null == method) {
      return getFlowSetting(variable);
    } else {
      try {
        return method.invoke(this);
      } catch (IllegalArgumentException e) {
        throw e;
      } catch (IllegalAccessException e) {
        throw new RuntimeException("Could not invoke method "+method, e);
      } catch (InvocationTargetException e) {
        throw new RuntimeException("Could not invoke method "+method, e);
      }
    }
  }
}


