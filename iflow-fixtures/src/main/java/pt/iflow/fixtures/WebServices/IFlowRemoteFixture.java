package pt.iflow.fixtures.WebServices;

import pt.iflow.api.services.types.DataElement;
import pt.iflow.api.services.types.DataElementSet;
import pt.iflow.api.services.types.StringSet;

public class IFlowRemoteFixture extends WsdlUtilsFixture {
  
  public void setStringSet(String stringSet) {
    StringSet ssFields = new StringSet();
    ssFields.setResult(stringSet.split(","));
    addInputValue("ssFields", ssFields);
  }
  
  public void setDataElementSet() {
    DataElementSet desFields = new DataElementSet();
    desFields.setResult(new DataElement[] {});
    addInputValue("desFields", desFields);
  }

  public void setFlowid(String flowid) {
    addInputValue("flowid", flowid);
  }

  public void setPid(String pid) {
    addInputValue("pid", pid);
  }

  public void setSubpid(String subpid) {
    addInputValue("subpid", subpid);
  }
}
