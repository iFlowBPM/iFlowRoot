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

import com.twolattes.json.Entity;
import com.twolattes.json.Value;

@Entity
public class FormProperties {
  @Value
  private String autosubmit;
  @Value
  private String result;
  @Value(name="desc")
  private String description;
  @Value(optional=true, name="printxsl")
  private String printStylesheet;
  @Value(name="pagexsl")
  private String stylesheet;
  @Value(optional=true, name="template")
  private String template = "false";

  public String getAutosubmit() {
    return autosubmit;
  }
  public void setAutosubmit(String autosubmit) {
    this.autosubmit = autosubmit;
  }
  public String getResult() {
    return result;
  }
  public void setResult(String result) {
    this.result = result;
  }
  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }
  public String getPrintStylesheet() {
    return printStylesheet;
  }
  public void setPrintStylesheet(String printStylesheet) {
    this.printStylesheet = printStylesheet;
  }
  public String getStylesheet() {
    return stylesheet;
  }
  public void setStylesheet(String stylesheet) {
    this.stylesheet = stylesheet;
  }
  public String getTemplate() {
    return template;
  }
  public void setTemplate(String template) {
    this.template = template;
  }

  public FormProperties duplicate() {
    FormProperties clone = new FormProperties();
    clone.autosubmit = autosubmit;
    clone.result = result;
    clone.description = description;
    clone.printStylesheet = printStylesheet;
    clone.stylesheet = stylesheet;
    clone.template = template;
    return clone;
  }
}
