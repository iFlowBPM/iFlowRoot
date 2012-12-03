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
package pt.iflow.api.utils;

import java.io.Serializable;

/**
 * Simple bean to share flow data between iFlow and Editor
 * @author oscar
 *
 */
public class FlowInfo implements Serializable {
  private static final long serialVersionUID = 104L;
  
  private String flowName;
  private boolean online;
  private int id;
  private String flowFile;
  private String created;
  private String modified;
  private int maxBlockId;
  
  public FlowInfo () {
  }

  public FlowInfo (int id, String name, String fileName, boolean online, String created, String modified) {
    this.flowName = name;
    this.online = online;
    this.id = id;
    this.flowFile = fileName;
    this.created = created;
    this.modified = modified;
  }

  public FlowInfo (int id, String name, String fileName, boolean online, String created, String modified, int maxBlockId) {
      this.flowName = name;
      this.online = online;
      this.id = id;
      this.flowFile = fileName;
      this.created = created;
      this.modified = modified;
      this.maxBlockId = maxBlockId;
    }

  public String getFlowName() {
    return flowName;
  }

  public void setFlowName(String flowName) {
    this.flowName = flowName;
  }

  public boolean isOnline() {
    return online;
  }

  public void setOnline(boolean online) {
    this.online = online;
  }

  public int getId() {
    return id;
  }

  public void setId(int revision) {
    this.id = revision;
  }

  public String getFlowFile() {
    return flowFile;
  }

  public void setFlowFile(String flowFile) {
    this.flowFile = flowFile;
  }
  
  public String getCreated() {
    return created;
  }

  public void setCreated(String created) {
    this.created = created;
  }

  public String getModified() {
    return modified;
  }

  public void setModified(String modified) {
    this.modified = modified;
  }

  public void setMaxBlockId(int maxBlockId) {
    this.maxBlockId = maxBlockId;
  }

  public int getMaxBlockId() {
    return maxBlockId;
 }
}
