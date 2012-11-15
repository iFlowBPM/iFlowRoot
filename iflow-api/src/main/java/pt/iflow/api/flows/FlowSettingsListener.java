package pt.iflow.api.flows;

public interface FlowSettingsListener extends FlowListener {

  public void settingsChanged(int flowid);
}
