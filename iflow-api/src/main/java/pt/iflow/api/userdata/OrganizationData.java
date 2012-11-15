/*
 *
 * Created on May 16, 2005 by iKnow
 *
  */

package pt.iflow.api.userdata;

import pt.iflow.api.userdata.IMappedData;


/**
 * 
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright (c) 2005 iKnow</p>
 * 
 * @author iKnow
 */

public interface OrganizationData extends IMappedData {
 
  public static final String ORGANIZATIONID = "ORGANIZATIONID";
  public static final String NAME = "NAME";
  public static final String DESCRIPTION = "DESCRIPTION";

  public abstract String getOrganizationId();
  public abstract String getDescription();
  public abstract String getName();
}
