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
package pt.iflow.api.core;

import java.util.List;
import java.util.Map;

public class UserProcesses {

  private List<List<String>> alData;
  private Map<String, Map<String,List<String>>> hmFlowUsers;
  
  public UserProcesses(List<List<String>> alData, Map<String, Map<String,List<String>>> hmFlowUsers) {
    this.alData = alData;
    this.hmFlowUsers = hmFlowUsers;
  }

  public List<List<String>> getAlData() {
    return alData;
  }

  public Map<String, Map<String,List<String>>> getHmFlowUsers() {
    return hmFlowUsers;
  }

}
