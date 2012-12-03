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
package pt.iflow.fixtures.WebServices;

import pt.iflow.api.services.types.DataElement;
import pt.iflow.api.services.types.DataElementSet;
import pt.iflow.api.services.types.StringSet;

public class IFlowRemoteFixture extends WsdlUtilsFixture {
  
  public void setStringSet(String stringSet) {
    StringSet ssFields = new StringSet();
    ssFields.setResult(stringSet.split(","));
    addInputValue("ssFields", ssFields);
  }
  
  public void setDataElementSet() {
    DataElementSet desFields = new DataElementSet();
    desFields.setResult(new DataElement[] {});
    addInputValue("desFields", desFields);
  }

  public void setFlowid(String flowid) {
    addInputValue("flowid", flowid);
  }

  public void setPid(String pid) {
    addInputValue("pid", pid);
  }

  public void setSubpid(String subpid) {
    addInputValue("subpid", subpid);
  }
}
