package pt.iflow.api.flows;

public interface FlowDeployListener extends FlowListener {

  public void goOnline(int flowid);
  public void goOffline(int flowid);
  
}
