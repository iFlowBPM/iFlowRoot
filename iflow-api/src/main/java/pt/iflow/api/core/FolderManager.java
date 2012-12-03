/*****************************************************************************************
    Infosistema iFlow - workflow and BPM platform
    Copyright(C) 2002-2012 Infosistema, S.A.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

    www.infosistema.com
    iflow@infosistema.com
    Av. Jose Gomes Ferreira, 11 3rd floor, s.34
    Miraflores
    1495-139 Alges Portugal
****************************************************************************************/
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
