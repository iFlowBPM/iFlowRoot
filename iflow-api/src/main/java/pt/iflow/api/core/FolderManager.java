package pt.iflow.api.core;

import java.util.List;

import pt.iflow.api.folder.Folder;
import pt.iflow.api.utils.UserInfoInterface;


public interface FolderManager {
	
	  public abstract List<Folder> getUserFolders(UserInfoInterface userInfo);
	  
	  public String getFolderColor(int folderid, List<Folder> folders);
	  
	  public String getFolderName(int folderid, List<Folder> folders);
	  
	  public abstract void setActivityToFolder(UserInfoInterface userInfo, String folderid, String activities);
	  
	  public void editFolder(UserInfoInterface userInfo, String folderid, String foldername, String color);
	  
	  public void createFolder(UserInfoInterface userInfo, String foldername, String color);

	  public void deleteFolder(UserInfoInterface userInfo, String folderid);
	  
	  public abstract void setActivityToFolderByName(UserInfoInterface userInfo, String foldername, int flowid, int pid, int subpid);
	  
}
