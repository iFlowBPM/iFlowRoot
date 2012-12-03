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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BlockInfo {
  private String id;
  private String type;
  private boolean interaction;
  private List<Map<String,String>> outblocks;
  
  public BlockInfo() {
    outblocks = new ArrayList<Map<String,String>>();
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
  public boolean isInteraction() {
    return interaction;
  }
  public void setInteraction(boolean interaction) {
    this.interaction = interaction;
  }
  public List<Map<String, String>> getOutblocks() {
    return outblocks;
  }
  public void addOutblock(Map<String, String> outblocks) {
    this.outblocks.add(outblocks);
  }
  

}
