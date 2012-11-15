package pt.iflow.api.flows;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BlockInfo {
  private String id;
  private String type;
  private boolean interaction;
  private List<Map<String,String>> outblocks;
  
  public BlockInfo() {
    outblocks = new ArrayList<Map<String,String>>();
  }
  
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  public String getType() {
    return type;
  }
  public void setType(String type) {
    this.type = type;
  }
  public boolean isInteraction() {
    return interaction;
  }
  public void setInteraction(boolean interaction) {
    this.interaction = interaction;
  }
  public List<Map<String, String>> getOutblocks() {
    return outblocks;
  }
  public void addOutblock(Map<String, String> outblocks) {
    this.outblocks.add(outblocks);
  }
  

}
