/*
 *
 * Created on May 16, 2005 by iKnow
 *
  */

package pt.iflow.userdata.common.santander;

import java.util.Map;

import pt.iflow.api.userdata.OrganizationalUnitData;

/**
 * 
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright (c) 2005 iKnow</p>
 * 
 * @author iKnow
 */

public class MappedOrganizationalUnitData extends MappedData implements OrganizationalUnitData {
  
  /**
   * 
   */
  private static final long serialVersionUID = -8637891001186449192L;

  public MappedOrganizationalUnitData(Map<String,String> data, Map<String,String> map) {
    super(data,map);
  }

}
