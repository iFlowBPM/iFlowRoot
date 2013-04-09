/**
 * 
 */
package pt.iflow.api.utils;

/**
 * @author helder
 *
 */
public class InterfaceInfo {
  private int interfaceId = -1;
  private String name = null;
  private String descricao = null;

  /**
   * @param interfaceId
   * @param name
   * @param descricao
   */
  public InterfaceInfo(int interfaceId, String name, String descricao) {
    this.interfaceId = interfaceId;
    this.name = name;
    this.descricao = descricao;
  }

  public InterfaceInfo(){}
  
  
  /**
   * @return the interfaceId
   */
  public int getInterfaceId() {
    return interfaceId;
  }
  /**
   * @param interfaceId the interfaceId to set
   */
  public void setInterfaceId(int interfaceId) {
    this.interfaceId = interfaceId;
  }
  /**
   * @return the name
   */
  public String getName() {
    return name;
  }
  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }
  /**
   * @return the description
   */
  public String getDescription() {
    return descricao;
  }
  /**
   * @param description the descricao to set
   */
  public void setDescription(String description) {
    this.descricao = description;
  }
  
  //possibilidade de ter associado um array com a listagem dos 
  
  
  
}
