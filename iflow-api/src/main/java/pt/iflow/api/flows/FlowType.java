package pt.iflow.api.flows;

public enum FlowType {
  WORKFLOW("W"),
  SUPPORT("S"),
  SEARCH("E"),
  REPORTS("R");

  public static final String FLOW_TYPE = "FLOW_TYPE";

  private final String code;
  private FlowType(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }

  public static FlowType getFlowType(String code) {
    if(null == code) return WORKFLOW;
    FlowType [] types = FlowType.values();
    for(FlowType type : types) {
      if(type.getCode().equals(code))
        return type;
    }

    try {
      return valueOf(code);
    } catch(Exception e) {}
    return WORKFLOW;
  }

  public FlowType getNextType() {
    return getNextType(this);
  }
  
  public static FlowType getNextType(FlowType type) {
    if(null == type) return WORKFLOW;
    FlowType [] types = values();
    int pos = type.ordinal()+1;
    if(pos >= types.length) pos = 0;
    return types[pos];
    
  }
  
  public static FlowType[] returnProcessExcludedTypes(){
    FlowType[] excludedTypes = new FlowType[2];
    excludedTypes[0] = FlowType.SEARCH;
    excludedTypes[1] = FlowType.REPORTS;
    return excludedTypes;
  }
  
  public static FlowType[] returnDelegationExcludedTypes(){
    return FlowType.returnProcessExcludedTypes();
  }
}
