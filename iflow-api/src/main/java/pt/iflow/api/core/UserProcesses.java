package pt.iflow.api.core;

import java.util.List;
import java.util.Map;

public class UserProcesses {

  private List<List<String>> alData;
  private Map<String, Map<String,List<String>>> hmFlowUsers;
  
  public UserProcesses(List<List<String>> alData, Map<String, Map<String,List<String>>> hmFlowUsers) {
    this.alData = alData;
    this.hmFlowUsers = hmFlowUsers;
  }

  public List<List<String>> getAlData() {
    return alData;
  }

  public Map<String, Map<String,List<String>>> getHmFlowUsers() {
    return hmFlowUsers;
  }

}
