package pt.iflow.api.connectors;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.flows.FlowSetting;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.connector.credentials.DMSCredential;
import pt.iknow.utils.crypt.CryptUtils;

/**
 * Handles generic data for DMS Connector.
 * 
 * @author Luís Cabral
 * @since 20/11/2009
 * @version 17/06/2010
 */
public class DMSConnectorUtils {

  // configuration constants
  public static final String CONFIG_PREFIX = "DMS_ACCESS_";

  public static final String CONFIG_DMS = DMSConnectorUtils.CONFIG_PREFIX + "ONOFF";
  public static final String CONFIG_DMS_DESC = "Acesso centralizado ao DMS";

  public static final String CONFIG_DMS_USER = DMSConnectorUtils.CONFIG_PREFIX + "USER";
  public static final String CONFIG_DMS_USER_DESC = DMSConnectorUtils.CONFIG_DMS_DESC + ": 1.utilizador";

  public static final String CONFIG_DMS_PASS = DMSConnectorUtils.CONFIG_PREFIX + "PWD";
  public static final String CONFIG_DMS_PASS_DESC = DMSConnectorUtils.CONFIG_DMS_DESC + ": 2.password";

  public static final String CONFIG_OPTION_YES = String.valueOf(true);
  public static final String CONFIG_OPTION_NO = String.valueOf(false);

  public static final String CONFIG_OPTION_YES_DESC = DMSConnectorUtils.CONFIG_PREFIX + "OPTION_YES";
  public static final String CONFIG_OPTION_NO_DESC = DMSConnectorUtils.CONFIG_PREFIX + "OPTION_NO";


  public static DMSCredential createCredential(UserInfoInterface userInfo) {
    return createCredential(userInfo, null);
  }
  
  @SuppressWarnings("deprecation")
  public static DMSCredential createCredential(UserInfoInterface userInfo, ProcessData procData) {
    String username = null;
    byte[] password = new byte[0];
    boolean bUser = true;
    if (procData != null) {
      DMSConnectorUtils dmsUtils = DMSConnectorUtils.parse(BeanFactory.getFlowBean().getFlowSettings(userInfo, procData.getFlowId()));
      if (dmsUtils.isOn()) {
        username = dmsUtils.getUser();
        password = dmsUtils.getPass();
        bUser = false;
      }
    }
    if (bUser) {
      username = userInfo.getUtilizador();
      password = userInfo.getPassword();
    }
    return DMSCredential.createCredential(username, password);
  }

  @SuppressWarnings("deprecation")
  public static DMSCredential createCredentialAuth(String user, String pass) {
    return DMSCredential.createCredential(user, pass.getBytes());
  }
  
  // message keys for description
  private static final String CONFIG_MESSAGE_PREFIX = "flow_settings_edit.dms.";

  public static String configMessageKey(String config) {
    return CONFIG_MESSAGE_PREFIX + config;
  }

  public static DMSConnectorUtils parse(FlowSetting[] settings) {
    DMSConnectorUtils ret = new DMSConnectorUtils();
    for (FlowSetting setting : settings) {
      if (StringUtils.equals(setting.getName(), DMSConnectorUtils.CONFIG_DMS)) {
        ret.on = StringUtils.equals(DMSConnectorUtils.CONFIG_OPTION_YES, setting.getValue());
      } else if (StringUtils.equals(setting.getName(), DMSConnectorUtils.CONFIG_DMS_USER)) {
        ret.user = setting.getValue();
      } else if (StringUtils.equals(setting.getName(), DMSConnectorUtils.CONFIG_DMS_PASS)) {
        ret.encriptedPass = setting.getValue();
      }
    }
    return ret;
  }

  private boolean on;
  private String user;
  private String encriptedPass;

  private DMSConnectorUtils() {
    this.on = false;
  }

  public boolean isOn() {
    return this.on;
  }

  public String getUser() {
    return this.user;
  }

  public String getUserAsHtml() {
    String parsedName = StringEscapeUtils.escapeHtml(this.user);
    return (StringUtils.isEmpty(parsedName) ? "" : parsedName);
  }
  
  public byte[] getPass() {
    if (StringUtils.isNotEmpty(this.encriptedPass)) {
    	CryptUtils crypto = new CryptUtils(DMSConnectorUtils.class.getName());
      return crypto.decrypt(this.encriptedPass).getBytes();
    }
    return null;
  }

  public String getPassForHtml() {
  	return (StringUtils.isEmpty(this.encriptedPass) ? "" : new String(getPass()));
  }

  public boolean validate() {
    return this.on && StringUtils.isNotEmpty(user) && StringUtils.isNotEmpty(encriptedPass);
  }
}
