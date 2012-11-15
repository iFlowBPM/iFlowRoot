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
