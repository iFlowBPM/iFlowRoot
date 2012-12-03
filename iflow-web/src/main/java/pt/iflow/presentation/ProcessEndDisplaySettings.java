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
package pt.iflow.presentation;

public class ProcessEndDisplaySettings {

  public enum TaskType { NEWEST, LATEST };
  
  
  private int flowid = -1;
  private TaskType type = TaskType.NEWEST;
  private int num_tasks = 5;
  
  
  public static ProcessEndDisplaySettings getDefault() {
    return new ProcessEndDisplaySettings();
  }
  
  public ProcessEndDisplaySettings setFlowId(int flowid) {
    this.flowid = flowid;
    return this;
  }
  
  public ProcessEndDisplaySettings setFlowId(String flowid) {
    this.flowid = Integer.parseInt(flowid);
    return this;
  }
  
  
  public ProcessEndDisplaySettings setTaskType(TaskType type) {
    this.type = type;
    return this;
  }

  public ProcessEndDisplaySettings setNumTasks(int num_tasks) {
    this.num_tasks = num_tasks;
    return this;
  }

  public int getFlowid() {
    return flowid;
  }

  public TaskType getType() {
    return type;
  }

  public int getNumTasks() {
    return num_tasks;
  }
  
  public boolean hasFlowid() {
    return flowid > 0;
  }

}
