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
