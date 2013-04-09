package pt.iflow.api.core;

import pt.iflow.api.utils.UserInfoInterface;

public interface AdministrationProcessManager {

  boolean terminateProcess(UserInfoInterface userInfo, int flowid, int pid, int subpid);

  boolean redirectProcessToUser(UserInfoInterface userInfo, int flowid, int pid, int subpid, String currentUser, String newUser);

  boolean undoProcess(UserInfoInterface userInfo, int flowId, int pid, int subPid, int flowState, int mid,
      boolean registerTransaction);

}