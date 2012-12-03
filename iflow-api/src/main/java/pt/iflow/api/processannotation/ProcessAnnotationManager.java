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
package pt.iflow.api.processannotation;

import java.util.List;

import pt.iflow.api.utils.UserInfoInterface;

public interface ProcessAnnotationManager {

  //Annotations
  public void deleteAnnotations(UserInfoInterface userInfo, int flowid, int pid, int subpid);

  //Labels
  public void addLabel(UserInfoInterface userInfo, int flowid, int pid, int subpid, String[] label);
  public void addLabel(UserInfoInterface userInfo, int flowid, int pid, int subpid, String[] label, boolean saveHistory);
  public void removeLabel(UserInfoInterface userInfo, int flowid, int pid, int subpid, String[] label);
  public void removeLabels(UserInfoInterface userInfo, int flowid, int pid, int subpid);
  public List<ProcessLabel> getProcessLabelList(UserInfoInterface userInfo, int flowid, int pid, int subpid);
  public List<ProcessLabel> getLabelList(UserInfoInterface userInfo);
  public List<ProcessLabel> getLabelJoin(UserInfoInterface userInfo, int flowid, int pid, int subpid);
  
  //Deadline
  public void addDeadline(UserInfoInterface userInfo, int flowid, int pid, int subpid, String deadline);
  public void addDeadline(UserInfoInterface userInfo, int flowid, int pid, int subpid, String deadline, boolean saveHistory);
  public void removeDeadline(UserInfoInterface userInfo, int flowid, int pid, int subpid);
  public String getProcessDeadline(UserInfoInterface userInfo, int flowid, int pid, int subpid);

  //Comments
  public void addComment(UserInfoInterface userInfo, int flowid, int pid, int subpid, String comment, boolean saveHistory);
  public void addComment(UserInfoInterface userInfo, int flowid, int pid, int subpid, String comment);
  public void removeComment(UserInfoInterface userInfo, String commentid);
  public void removeComment(UserInfoInterface userInfo, int flowid, int pid, int subpid);
  public ProcessComment getProcessComment(UserInfoInterface userInfo, int flowid, int pid, int subpid);
  public List<ProcessComment> getProcessComment_History(UserInfoInterface userInfo, int flowid, int pid, int subpid);

  /**
   * Returns a list of available task annotation labels, to be used by iflow editor
   * @return List<ProcessLabel>
   */
  public List<ProcessLabel> getLabelListToIflowEditor();
}
