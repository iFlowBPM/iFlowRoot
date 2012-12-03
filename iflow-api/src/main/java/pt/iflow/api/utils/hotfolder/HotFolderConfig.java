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
package pt.iflow.api.utils.hotfolder;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.flows.FlowSetting;

public class HotFolderConfig {

  public static final String CONFIG_PREFIX = "HOT_FOLDER_";

  public static final String ONOFF = CONFIG_PREFIX + "ONOFF";
  public static final String ONOFF_DESC = "HotFolder";

  public static final String SUBS_FOLDERS = CONFIG_PREFIX + "FOLDERS";
  public static final String SUBS_FOLDERS_DESC = "Pastas onde pesquisar ficheiros novos";

  public static final String SEARCH_DEPTH = CONFIG_PREFIX + "DEPTH";
  public static final String SEARCH_DEPTH_DESC = "Profundidade da pesquisa";

  public static final String DOC_VAR = CONFIG_PREFIX + "DOCVAR";
  public static final String DOC_VAR_DESC = "Variável de novo processo para ficheiro encontrado";

  public static final String IN_USER = CONFIG_PREFIX + "IN_USER";
  public static final String IN_USER_DESC = "O utilizador a utilizar para criar o processo";

  public static final String CONFIG_OPTION_YES = String.valueOf(true);
  public static final String CONFIG_OPTION_NO = String.valueOf(false);
  public static final String CONFIG_OPTION_YES_DESC = "CONFIG_OPTION_YES";
  public static final String CONFIG_OPTION_NO_DESC = "CONFIG_OPTION_NO";
  
  private int flowid = -1;
  private boolean on = false;
  private List<String> subsFolders;
  private int depth = -1;
  private String docVar;
  private String inUser;
  
  private static final String SUBS_FOLDER_SEP = ",";
  
  // message keys for description
  private static final String CONFIG_MESSAGE_PREFIX = "flow_settings_edit.hotfolder."; 
  
  public HotFolderConfig(int flowid) {
    this.flowid = flowid;
  }

  public static String configMessageKey(String config) {
    return CONFIG_MESSAGE_PREFIX + config;
  }
  
  public static HotFolderConfig parse(int flowid, FlowSetting[] settings) {  
    HotFolderConfig ret = new HotFolderConfig(flowid);
    
    for (FlowSetting setting : settings) {
      if (StringUtils.equals(setting.getName(), ONOFF)) {
        ret.on = StringUtils.equals(CONFIG_OPTION_YES, setting.getValue());
      }      
      else if (StringUtils.equals(setting.getName(), SUBS_FOLDERS)) {
        ret.subsFolders = new ArrayList<String>();
        if (StringUtils.isNotEmpty(setting.getValue())) {
          StringTokenizer st = new StringTokenizer(setting.getValue(), SUBS_FOLDER_SEP);
          while (st.hasMoreTokens()) {
            String val = st.nextToken().trim();
            if (StringUtils.isNotEmpty(val))
              ret.subsFolders.add(val);            
          }
        }
      }                   
      else if (StringUtils.equals(setting.getName(), SEARCH_DEPTH)) {
        String sdepth = setting.getValue(); 
        if (StringUtils.isNotEmpty(sdepth) && StringUtils.isNumeric(sdepth))
          ret.depth = Integer.parseInt(sdepth); 
      }
      else if (StringUtils.equals(setting.getName(), DOC_VAR)) {
        ret.docVar = setting.getValue(); 
      }
      else if (StringUtils.equals(setting.getName(), IN_USER)) {
        ret.inUser = setting.getValue(); 
      }
    }
  
    return ret;
  }
  
  public static HotFolderConfig parseAndValidate(int flowid, FlowSetting[] settings) {  
    HotFolderConfig ret = parse(flowid, settings);
    
    return ret.validate() ? ret : null;
  }
  
  public boolean validate() {
    return on 
    && StringUtils.isNotEmpty(docVar) 
    && (subsFolders != null && subsFolders.size() > 0);
  }

  public int getFlowId() {
    return flowid;
  }
  
  public boolean isOn() {
    return on;
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

  public void setSubsFolders(List<String> subsFolders) {
    this.subsFolders = subsFolders;
  }

  public int getDepth() {
    return depth;
  }

  public void setDepth(int depth) {
    this.depth = depth;
  }

  public String getDocVar() {
    return docVar;
  }

  public void setDocVar(String docVar) {
    this.docVar = docVar;
  }
  
  public String getUser() {
    return inUser;
  }

  public void setUser(String inUser) {
    this.inUser = inUser;
  }

  @Override
  public String toString() {
    return "{flowid: " + flowid + "}{docVar: " + 
      docVar + "}{user: " + 
      inUser + "}{hasfolders: " + (subsFolders.size() > 0) + "}";
  }
}
