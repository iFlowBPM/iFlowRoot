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
package pt.iflow.api.blocks.form;

import java.util.ArrayList;
import java.util.List;

import com.twolattes.json.Entity;
import com.twolattes.json.Value;

@Entity
public class Form {
  @Value
  private List<Tab> tabs;
  @Value
  private FormProperties properties;

  public Form() {
  }

  public List<Tab> getTabs() {
    return tabs;
  }

  public void setTabs(List<Tab> tabs) {
    this.tabs = tabs;
  }

  public FormProperties getProperties() {
    return properties;
  }

  public void setProperties(FormProperties properties) {
    this.properties = properties;
  }

  public Form duplicate() throws CloneNotSupportedException {
    Form clone = new Form();
    clone.properties = properties.duplicate();
    clone.tabs = new ArrayList<Tab>();
    for(Tab tab : tabs)
      clone.tabs.add(tab.duplicate());
    return clone;
  }

}
