/*
 *
 * Created on May 16, 2005 by iKnow
 *
  */

package pt.iflow.userdata.common;

import java.io.Serializable;
import java.util.Map;

import pt.iflow.api.userdata.OrganizationData;

/**
 * 
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright (c) 2005 iKnow</p>
 * 
 * @author iKnow
 */

public class MappedOrganizationData extends MappedData implements OrganizationData,Serializable {
                                                                                                  
  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;
	
  public MappedOrganizationData(Map<String,String> data, Map<String,String> map) {
    super(data,map);
  }

  public String getOrganizationId() {
    return get(OrganizationData.ORGANIZATIONID);
  }
  
  public String getDescription() {
    return get(OrganizationData.DESCRIPTION);
  }
  
  public String getName() {
    return get(OrganizationData.NAME);
  }
  
  
}
