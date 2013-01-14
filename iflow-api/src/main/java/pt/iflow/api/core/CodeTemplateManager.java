package pt.iflow.api.core;

import java.util.List;

import pt.iflow.api.documents.CodeTemplate;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.UserInfoInterface;

public interface CodeTemplateManager {

	  /**
	   * Lists the serial code templates stored in DB.
	   * 
	   * @param userInfo
	   *          User information.
	   * @param procData
	   *          Process data.
	   * @return CodeTemplate[]
	   *          Stored list of
	   *          serial code templates.
	   */
  List<CodeTemplate> listCodeTemplates(UserInfoInterface userInfo, ProcessData procDatas);
	  
	  /**
	   * Stores given code template in DB.
	   * 
	   * @param userInfo
	   *          User information.
	   * @param procData
	   *          Process data.
	   * @param template
	   *          Code template to store.
	   * @return boolean
	   *          True if the code template was added, false otherwise.
	   */
	boolean addCodeTemplate(UserInfoInterface userInfo, ProcessData procData, CodeTemplate template);
	
	/**
	 * Removes given code template from DB.
	 * 
	 * @param userInfo
	 *          User information.
	 * @param procData
	 *          Process data.
	 * @param name
	 *          Name of the code template to delete.
	 */
	void removeCodeTemplate(UserInfoInterface userInfo, ProcessData procData, String name);

  String generateSerialCode(UserInfoInterface userInfo, String templateName);

  String createNewSerialCode(UserInfoInterface userInfo, String templateName);

  Boolean markAsTag(UserInfoInterface userInfo, String templateName, Boolean mark);

  Boolean checkMetaTag(UserInfoInterface userInfo, Boolean mark);
}
