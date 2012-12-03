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
package pt.iflow.applet;

// XXX Integrar com SwingTask e remover esta classe
public class TaskStatus {

  public static final int WORKING = 0;
  public static final int COMPLETE = 1;
  public static final int ERROR = 2;

  private final String taskId;
  private int status;
  private String result;

  public TaskStatus(String taskId) {
    this.taskId = taskId;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public void setResult(String result) {
    this.result = result;
  }

  public String getTaskId() {
    return taskId;
  }

  public int getStatus() {
    return status;
  }

  public String getResult() {
    return result;
  }

  
  public String toString() {
    return getJSON();
  }

  public String getJSON() {
    String stat = "working"; //$NON-NLS-1$
    switch (getStatus()) {
    case COMPLETE:
      stat = "complete"; //$NON-NLS-1$
      break;
    case ERROR:
      stat = "error"; //$NON-NLS-1$
      break;
    case WORKING:
    default:
      stat = "working"; //$NON-NLS-1$
      break;
    }
    return "{\"status\":\"" + stat + "\",\"result\":" + getResult() + "}"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
  }
  
  public static String getInvalidStatus() {
    return "{\"status\":\"invalid\"}"; //$NON-NLS-1$
  }
}
