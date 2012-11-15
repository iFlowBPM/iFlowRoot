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
