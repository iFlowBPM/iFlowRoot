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
package pt.iflow.api.flows;

public enum FlowType {
  WORKFLOW("W"),
  SUPPORT("S"),
  SEARCH("E"),
  REPORTS("R");

  public static final String FLOW_TYPE = "FLOW_TYPE";

  private final String code;
  private FlowType(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }

  public static FlowType getFlowType(String code) {
    if(null == code) return WORKFLOW;
    FlowType [] types = FlowType.values();
    for(FlowType type : types) {
      if(type.getCode().equals(code))
        return type;
    }

    try {
      return valueOf(code);
    } catch(Exception e) {}
    return WORKFLOW;
  }

  public FlowType getNextType() {
    return getNextType(this);
  }
  
  public static FlowType getNextType(FlowType type) {
    if(null == type) return WORKFLOW;
    FlowType [] types = values();
    int pos = type.ordinal()+1;
    if(pos >= types.length) pos = 0;
    return types[pos];
    
  }
  
  public static FlowType[] returnProcessExcludedTypes(){
    FlowType[] excludedTypes = new FlowType[2];
    excludedTypes[0] = FlowType.SEARCH;
    excludedTypes[1] = FlowType.REPORTS;
    return excludedTypes;
  }
  
  public static FlowType[] returnDelegationExcludedTypes(){
    return FlowType.returnProcessExcludedTypes();
  }
}
