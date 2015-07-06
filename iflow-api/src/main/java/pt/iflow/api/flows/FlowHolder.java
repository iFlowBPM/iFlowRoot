package pt.iflow.api.flows;

import java.util.Collection;

import pt.iflow.api.utils.UserInfoInterface;

public interface FlowHolder {

  /**
   * Check if the flow is online
   * 
   * @param userInfo
   * @param flowId
   * @return true if the flow is marked as online
   */
  public abstract boolean isOnline(UserInfoInterface userInfo, int flowId);

  /**
   * Return the flow file name
   * 
   * @param userInfo
   * @param flowId
   * @return Flow file name or null if not found
   */
  public abstract String getFlowFileName(UserInfoInterface userInfo, int flowId);

  /**
   * Return a list of all flows available to the user organization.
   * <br>
   * <em>Do not return template flows!</em>
   * 
   * @param userInfo
   * @return
   */
  public abstract IFlowData[] listFlows(UserInfoInterface userInfo);

  /**
   * Return a list of all flows available to the user organization.
   * <br>
   * <em>Do not return template flows!</em>
   * 
   * @param userInfo
   * @return
   */
  public abstract IFlowData[] listFlows(UserInfoInterface userInfo, FlowType type);

  /**
   * Return a list of online flows available to the user organization.
   * <br>
   * <em>Do not return template flows!</em>
   * 
   * @param userInfo
   * @return
   */
  public abstract IFlowData[] listFlowsOnline(UserInfoInterface userInfo);

  /**
   * Return a list of online flows available to the user organization.
   * <br>
   * <em>Do not return template flows!</em>
   * 
   * @param userInfo
   * @return
   */
  public abstract IFlowData[] listFlowsOnline(UserInfoInterface userInfo, FlowType type);

  public abstract IFlowData[] listFlowsOnline(UserInfoInterface userInfo, FlowType type, boolean showOnlyFlowsToBePresentInMenu);

  /**
   * Return a list of online flows available to the user organization.
   * <br>
   * <em>Do not return template flows!</em>
   * 
   * @param userInfo
   * @return
   */
  public abstract IFlowData[] listFlowsOnline(UserInfoInterface userInfo, FlowType type, FlowType[] typeExcluded);
  
  public abstract IFlowData[] listFlowsOnline(UserInfoInterface userInfo, FlowType type, FlowType[] typeExcluded, boolean showOnlyFlowsToBePresentInMenu);

  /**
   * Return a list of offline flows available to the user organization.
   * <br>
   * <em>Do not return template flows!</em>
   * 
   * @param userInfo
   * @return
   */
  public abstract IFlowData[] listFlowsOffline(UserInfoInterface userInfo, FlowType type);

  /**
   * Return a list of offline flows available to the user organization.
   * <br>
   * <em>Do not return template flows!</em>
   * 
   * @param userInfo
   * @return
   */
  public abstract IFlowData[] listFlowsOffline(UserInfoInterface userInfo);

  /**
   * Return a list of offline flows available to the user organization.
   * <br>
   * <em>Do not return template flows!</em>
   * 
   * @param userInfo
   * @return
   */
  public abstract IFlowData[] listFlowsOffline(UserInfoInterface userInfo, FlowType type, FlowType[] typeExcluded);

   /**
   * Return a list of names of every flow available to the user.
   * <br>
   * <em>This method returns template flow names!</em>
   * 
   * @param userInfo
   * @return
   */
  public abstract String[] listFlowNames(UserInfoInterface userInfo);

  /**
   * Return a list of names of online flows available to the user.
   * <br>
   * <em>This method returns template flow names!</em>
   * 
   * @param userInfo
   * @return
   */
  public abstract String[] listFlowNamesOnline(UserInfoInterface userInfo);

  /**
   * Return a list of names of offline flows available to the user.
   * <br>
   * <em>This method returns template flow names!</em>
   * 
   * @param userInfo
   * @return
   */
  public abstract String[] listFlowNamesOffline(UserInfoInterface userInfo);

  /**
   * Return a list of names of every sub flow available to the user.
   * <br>
   * <em>This method returns template flow names!</em>
   * 
   * @param userInfo
   * @return
   */
  public abstract String[] listSubFlows(UserInfoInterface userInfo);

  /**
   * Return a list of flow templates available to the user.
   * <br>
   * <em>This method returns template flow names!</em>
   * 
   * @param userInfo
   * @return
   */
  public abstract FlowTemplate[] listFlowTemplates(UserInfoInterface userInfo);

  /**
   * Get the XML contents of the named flow.
   * 
   * If the requested name is a flow template, return template contents.
   * 
   * <br>
   * <em>This method will not create a new flow!</em>
   * 
   * @param userInfo
   * @param name file name
   * @return XML bytes
   */
  public abstract byte[] readFlowData(UserInfoInterface userInfo, String name);

  /**
   * Get the XML contents of the named sub flow.
   * 
   * If the requested name is a sub flow template, return template contents.
   * 
   * <br>
   * <em>This method will not create a new sub flow!</em>
   * 
   * @param userInfo
   * @param name file name
   * @return XML bytes
   */
  public abstract byte[] readSubFlowData(UserInfoInterface userInfo, String name);

  /**
   * Get the XML contents of the named flow template.
   * 
   * If the requested name is a flow template, return template contents.
   * 
   * <br>
   * <em>This method will not create a new flow!</em>
   * 
   * @param userInfo
   * @param name file name
   * @return XML bytes
   */
  public abstract byte[] readTemplateData(UserInfoInterface userInfo, String name);

  /**
   * Create or update a flow.
   * <br>
   * The user must be organization administrator in order to create/update a flow.
   * <br><br>
   * If the flow is already online, it will be redeployed.
   * 
   * @param userInfo
   * @param file
   * @param name Used in flow creation. If null, assumes name=file.
   * @param data
   * @return updated/created flow id or -1 if an error occured
   */
  public abstract int writeFlowData(UserInfoInterface userInfo, String file, String name, byte[] data);

  /**
   * Create or update a flow and make a new version.
   * <br>
   * The user must be organization administrator in order to create/update a flow.
   * <br><br>
   * If the flow is already online, it will be redeployed.
   * 
   * @param userInfo
   * @param file
   * @param name Used in flow creation. If null, assumes name=file.
   * @param data
   * @return updated/created flow id or -1 if an error occured
   */
  public abstract int writeFlowData(UserInfoInterface userInfo, String file, String name, byte[] data, String comment);

  /**
   * Create or update a sub flow.
   * <br>
   * The user must be organization administrator in order to create/update a sub flow.
   * <br><br>
   * This method will redeploy every deployed flow that uses this sub flow.
   * 
   * @param userInfo
   * @param file
   * @param name Used in sub flow creation. If null, assumes name=file.
   * @param data
   * @return updated or created sub flow id or -1 if an error occured
   */
  public abstract int writeSubFlowData(UserInfoInterface userInfo, String name, String description, byte[] data);

  /**
   * Create or update a sub flow and make a new version.
   * <br>
   * The user must be organization administrator in order to create/update a sub flow.
   * <br><br>
   * This method will redeploy every deployed flow that uses this sub flow.
   * 
   * @param userInfo
   * @param file
   * @param name Used in sub flow creation. If null, assumes name=file.
   * @param data
   * @param comment
   * @return updated or created sub flow id or -1 if an error occured
   */
  public abstract int writeSubFlowData(UserInfoInterface userInfo, String name, String description, byte[] data, String comment);

  /**
   * Return a flow from the flow cache or create a new one if necessary.
   * 
   * @param userInfo
   * @param flowId
   * @return
   */
  public abstract IFlowData getFlow(UserInfoInterface userInfo, int flowId);

  /**
   * Return a flow descriptor from database
   * 
   * @param userInfo
   * @param fileName
   * @return
   */
  public abstract IFlowData getFlow(UserInfoInterface userInfo, String fileName);

  /**
   * Return a flow from the flow cache or create a new one if necessary.
   * 
   * @param userInfo
   * @param flowId
   * @param create 
   * @return
   */
  public abstract IFlowData getFlow(UserInfoInterface userInfo, int flowId, boolean create);

  /**
   * Reread flow data from DB
   * 
   * @param userInfo
   * @param flowId
   * @return
   */
  public abstract IFlowData refreshFlow(UserInfoInterface userInfo, int flowId);

  /**
   * Instantiate a new Flow from FlowData.
   * <br>
   * A new flow will be created from a template if necessary.
   * 
   * @param userInfo
   * @param fileName flow file name
   * @return null if OK, otherwise error occurred
   */
  public abstract String deployFlow(UserInfoInterface userInfo, String fileName);

  /**
   * Free flow resources and mark it as offline.
   * 
   * @param userInfo
   * @param fileName
   * @return
   */
  public abstract String undeployFlow(UserInfoInterface userInfo, String asFile);

  /**
   * Remove a flow from database
   * 
   * @param userInfo
   * @param asFile
   * @param abProcs If true, remove processes
   * @return
   */
  public abstract boolean deleteFlow(UserInfoInterface userInfo, String asFile, boolean abProcs);

//  // checks if flow is in cache (cached flow may contain errors.. use flow data's hasError method)
//  public abstract boolean hasFlow(int anFlowId);
//
//  public abstract void removeFlow(UserInfo userInfo, int anFlowId);
//
  
  /**
   * Remove a flow template from database
   * 
   * @param userInfo
   * @param asFile
   * @return
   */
  public abstract boolean deleteFlowTemplate(UserInfoInterface userInfo, String asFile);

  /**
   * Upload a flow template to database
   * 
   * @param userInfo
   * @param name
   * @param description
   * @param data
   * @return
   */
  public abstract boolean uploadFlowTemplate(UserInfoInterface userInfo, String name, String description, byte [] data);

  // versioning stuff
  
  public abstract String [] getFlowVersions(UserInfoInterface userInfo, String name);
  public abstract String [] getSubFlowVersions(UserInfoInterface userInfo, String name);
  public abstract String getFlowComment(UserInfoInterface userInfo, String name, int version);
  public abstract String getSubFlowComment(UserInfoInterface userInfo, String name, int version);
  public abstract byte[] readFlowData(UserInfoInterface userInfo, String name, int version);
  public abstract byte[] readSubFlowData(UserInfoInterface userInfo, String name, int version);

  
  // series stuff
  public abstract boolean updateFlowSeries(UserInfoInterface userInfo, int flowId, int seriesId);
  public abstract boolean updateFlowsSeries(UserInfoInterface userInfo, int[] flowIds, int seriesId);
 
  public Collection<Integer> listFlowIds(UserInfoInterface userInfo);

  public void addNewFlowListener(String id, NewFlowListener listener);
  public void removeNewFlowListener(String id);

  public void addFlowDeployListener(String id, FlowDeployListener listener);
  public void removeFlowDeployListener(String id);

  public void addFlowVersionListener(String id, FlowVersionListener listener);
  public void removeFlowVersionListener(String id);
  
  public String getFlowOrganizationid(int flowid);
  
  public boolean updateFlowType(UserInfoInterface userInfo, int flowid);

  public boolean updateFlowShowInMenuRequirement(UserInfoInterface userInfo, int flowid);
  
  /**
   * refreshes the flow cache without triggering cluster syncronization
   * @param userInfo
   * @param flowId
   */
  public void refreshCacheFlow(UserInfoInterface userInfo, int flowId);
}