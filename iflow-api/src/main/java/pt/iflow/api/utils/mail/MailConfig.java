/*****************************************************************************************
    Infosistema iFlow - workflow and BPM platform
    Copyright(C) 2002-2012 Infosistema, S.A.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

    www.infosistema.com
    iflow@infosistema.com
    Av. Jose Gomes Ferreira, 11 3rd floor, s.34
    Miraflores
    1495-139 Alges Portugal
****************************************************************************************/
package pt.iflow.api.utils.mail;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.flows.FlowSetting;
import pt.iknow.utils.crypt.CryptUtils;

public class MailConfig {

  // configuration constants
  public static final String CONFIG_PREFIX = "EMAIL_START_";

  public static final String CONFIG_ONOFF = CONFIG_PREFIX + "ONOFF";
  public static final String CONFIG_HOST = CONFIG_PREFIX + "HOST";
  public static final String CONFIG_PORT = CONFIG_PREFIX + "PORT";
  public static final String CONFIG_USER = CONFIG_PREFIX + "USER";
  public static final String CONFIG_PASS = CONFIG_PREFIX + "ENC_PASS";
  public static final String CONFIG_INBOX = CONFIG_PREFIX + "INBOX";
  public static final String CONFIG_SECURE = CONFIG_PREFIX + "SECURE";
  public static final String CONFIG_CHECK_INTERVAL = CONFIG_PREFIX + "INTERVAL";
  public static final String CONFIG_SUBSCRIBED_FOLDERS = CONFIG_PREFIX + "SUBS_FOLDERS";

  public static final String CONFIG_OPTION_YES = String.valueOf(true);
  public static final String CONFIG_OPTION_NO = String.valueOf(false);

  public static final String CONFIG_OPTION_YES_DESC = "CONFIG_OPTION_YES";
  public static final String CONFIG_OPTION_NO_DESC = "CONFIG_OPTION_NO";

  
  // message keys for description
  private static final String CONFIG_MESSAGE_PREFIX = "flow_settings_edit.mail."; 
  public static String configMessageKey(String config) {
    return CONFIG_MESSAGE_PREFIX + config;
  }
  
  // email supported properties for mapping
  public static final String PROP_FROM_EMAIL = CONFIG_PREFIX + "FROM_EMAIL";
  public static final String PROP_FROM_NAME = CONFIG_PREFIX + "FROM_NAME";
  public static final String PROP_SUBJECT = CONFIG_PREFIX + "SUBJECT";
  public static final String PROP_SENT_DATE = CONFIG_PREFIX + "SENT_DATE";
  public static final String PROP_DOCS_VAR = CONFIG_PREFIX + "DOCS_VAR";

  private static final String SUBS_FOLDER_SEP = ",";
  
  private boolean on;

  private boolean secure;
  private String host;
  private int port = -1;
  private String user;
  private String encriptedPass;
  private String inbox = "Inbox";
  private List<String> subsFolders;
  private long checkIntervalInSeconds = 60;
  
  private MailConfig() {}

  public boolean isOn() {
    return on;
  }

  public boolean isSecure() {
    return secure;
  }

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }

  public String getUser() {
    return user;
  }

  public byte[] getPass() {
    if (StringUtils.isNotEmpty(encriptedPass)) {
      CryptUtils c = new CryptUtils(MailConfig.class.getName());
      return c.decrypt(encriptedPass).getBytes();
    }
    return null;
  }

  public String getInbox() {
    return inbox;
  }

  public List<String> getSubsFolders() {
    return subsFolders;
  }

  public String getSubsFoldersAsString() {
    StringBuilder sb = new StringBuilder();
    for (String s : subsFolders) {
      if (sb.length() > 0)
        sb.append(SUBS_FOLDER_SEP);
      sb.append(s.trim());
    }
    return sb.toString();
  }


  public long getCheckIntervalInSeconds() {
    return checkIntervalInSeconds;
  }

  public static MailConfig parse(FlowSetting[] settings) {  
    MailConfig ret = new MailConfig();
    
    for (FlowSetting setting : settings) {
      if (StringUtils.equals(setting.getName(), CONFIG_ONOFF)) {
        ret.on = StringUtils.equals(CONFIG_OPTION_YES, setting.getValue());
      }      
      else if (StringUtils.equals(setting.getName(), CONFIG_HOST)) {
        ret.host = setting.getValue();
      }
      else if (StringUtils.equals(setting.getName(), CONFIG_PORT)) {
        String sport = setting.getValue(); 
        if (StringUtils.isNotEmpty(sport) && StringUtils.isNumeric(sport))
          ret.port = Integer.parseInt(sport); 
      }
      else if (StringUtils.equals(setting.getName(), CONFIG_USER)) {
        ret.user = setting.getValue();
      }
      else if (StringUtils.equals(setting.getName(), CONFIG_PASS)) {
        ret.encriptedPass = setting.getValue(); 
      }
      else if (StringUtils.equals(setting.getName(), CONFIG_INBOX)) {
        ret.inbox = setting.getValue();
      }
      else if (StringUtils.equals(setting.getName(), CONFIG_SECURE)) {
        ret.secure = StringUtils.equals(CONFIG_OPTION_YES, setting.getValue());
      }
      else if (StringUtils.equals(setting.getName(), CONFIG_CHECK_INTERVAL)) {
        String interval = setting.getValue();
        if (StringUtils.isNotEmpty(interval) && StringUtils.isNumeric(interval)) {
          ret.checkIntervalInSeconds = Long.parseLong(interval);
        }
      }
      else if (StringUtils.equals(setting.getName(), CONFIG_SUBSCRIBED_FOLDERS)) {
        ret.subsFolders = new ArrayList<String>();
        if (StringUtils.isNotEmpty(setting.getValue())) {
          StringTokenizer st = new StringTokenizer(setting.getValue(), SUBS_FOLDER_SEP);
          while (st.hasMoreTokens()) {
            ret.subsFolders.add(st.nextToken().trim());            
          }
        }
      }       
    }
  
    return ret;
  }
  
  public static MailConfig parseAndValidate(FlowSetting[] settings) {  
    MailConfig ret = parse(settings);
    
    return ret.validate() ? ret : null;
  }
  
  public boolean validate() {
    return on 
    && StringUtils.isNotEmpty(host) 
    && StringUtils.isNotEmpty(user)
    && StringUtils.isNotEmpty(encriptedPass)
    && (StringUtils.isNotEmpty(inbox) || 
        (subsFolders != null && subsFolders.size() > 0));
  }
}
