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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.twolattes.json.Entity;
import com.twolattes.json.Value;

@Entity
public class Field {
  @Value
  private String id;
  @Value
  private String type;
  @Value(name="values")
  private Map<String,String> properties;
  @Value(optional=true)
  private List<Field> fields;
  @Value(optional=true)
  private String variable;

  public Field() {
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public List<Field> getFields() {
    return fields;
  }

  public void setFields(List<Field> tabs) {
    this.fields = tabs;
  }

  public Map<String,String> getProperties() {
    return properties;
  }

  public void setProperties(Map<String,String> properties) {
    this.properties = properties;
  }

  public String getVariable() {
    return variable;
  }

  public void setVariable(String name) {
    this.variable = name;
  }

  public Field duplicate() {
    Field clone = new Field();
    clone.id = id;
    clone.type = type;
    clone.variable = variable;
    clone.properties = new HashMap<String, String>(properties);
    if(fields != null) {
      clone.fields = new ArrayList<Field>();
      for(Field field : fields)
        clone.fields.add(field.duplicate());
    }
    return clone;
  }


}
