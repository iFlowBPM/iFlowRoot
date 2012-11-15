package pt.iflow.api.presentation;

import java.util.ArrayList;
import java.util.List;

import pt.iflow.api.flows.IFlowData;

public class FlowMenuItems {
  
  private List<IFlowData> flows;
  private List<ApplicationItem> links;
  
  public FlowMenuItems() {
    flows = new ArrayList<IFlowData>();
    links = new ArrayList<ApplicationItem>();
  }
  
  public void addFlowData(IFlowData fd) {
    flows.add(fd);
  }

  public void addLink(ApplicationItem link) {
    links.add(link);
  }

  public List<IFlowData> getFlows() {
    return flows;
  }

  public List<ApplicationItem> getLinks() {
    return links;
  }
  
  public boolean isEmpty() {
    return flows.isEmpty() && links.isEmpty();
  }  

}
