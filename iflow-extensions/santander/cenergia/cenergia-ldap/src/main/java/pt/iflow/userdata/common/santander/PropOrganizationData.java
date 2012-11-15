package pt.iflow.userdata.common.santander;

import java.io.Serializable;
import java.util.Map;

import pt.iflow.api.userdata.OrganizationData;

public class PropOrganizationData implements OrganizationData, Serializable {
                                                                                                  
  private static final long serialVersionUID = 1L;
    
  public String getOrganizationId() {
    return get(OrganizationData.ORGANIZATIONID);
  }
  
  public String getDescription() {
    return get(OrganizationData.DESCRIPTION);
  }
  
  public String getName() {
    return get(OrganizationData.NAME);
  }
  
  public String get(String prop) {
    return get(OrganizationData.NAME);
  }
  
}
