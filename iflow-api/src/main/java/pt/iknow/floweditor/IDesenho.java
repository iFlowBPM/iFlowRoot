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
package pt.iknow.floweditor;

import java.util.Collection;
import java.util.Map;

public interface IDesenho {
  
  void newCatalog();
  
  Collection<Atributo> getCatalogue();
  
  /**
   * Add variable to catalog
   * 
   * @param variable variable to add
   * @param check check if variable exists
   * @return true if variable added/updated false otherwise or if check is true and variable exists
   */
  boolean addCatalogVariable(Atributo variable, boolean check);
  
  public boolean addCatalogVariable(String nome, String valor, boolean isSearchable, String publicName, String tipo, String format);

  /**
   * Get a form template.
   * 
   * @param name Template name
   * @return Template contents
   */
  public String getFormTemplate(String name);

  /**
   * Retrieve all form templates for current flow
   * 
   * @return
   */
  Map<String, String> getFormTemplates();
  
  /**
   * Set/unset a form template.
   * Remove a template if atributos parameter is null or empty.
   * 
   * @param name Template name
   * @param form Template attributes
   * @return Previous template
   */
  public String setFormTemplate(String name, String form);

  /**
   * Get the formTemplates list of the flow
   */
  public String[] getFormTemplatesList();

  
}
