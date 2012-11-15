package pt.iflow.api.flows;

import java.io.Serializable;

/**
 * Simple representation of a Flow Template "metadata"
 * 
 * @author oscar
 *
 */
public class FlowTemplate implements Serializable {
  private static final long serialVersionUID = -6605667467326291076L;
  
  private String name;
  private String description;
  
  public FlowTemplate(String name, String description) {
    this.name = name;
    this.description = description;
  }

  /**
   * Get Flow Template Name
   * @return
   */
  public String getName() {
    return name;
  }

  /**
   * Get Flow Template Description
   * @return
   */
  public String getDescription() {
    return description;
  }
  
}
