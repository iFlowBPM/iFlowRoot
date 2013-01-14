package pt.iflow.api.flows;

public interface FlowVersionListener extends FlowListener {

  public void newVersion(int flowid);
  
}
