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
package pt.iflow.offline;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Calendar;

import org.apache.commons.io.FilenameUtils;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.documents.DocumentData;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessListVariable;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.connector.document.Document;
import pt.iknow.utils.FileUtils;
import pt.iknow.utils.StringUtilities;
import pt.iknow.utils.html.FormData;

public class OfflineManager {

  private static final OfflineManager instance = new OfflineManager();
  // XXX CHANGE OFFLINE FILES LOCATION
  private static final String OFFLINE_FOLDER = "/DEVELOPER_WEB_TESTS/offline_files/";
  public static final String OFFLINE_FILE_PARAMETER = "offline_files";


  public static String getOfflineFilePath(String baseFilePath, String filename) {
    return FilenameUtils.normalize(baseFilePath + OFFLINE_FOLDER + filename);
  }

  public static void appendOfflineDocuments(UserInfoInterface userInfo, 
      FormData fdFormData, String baseFilePath, ProcessData procData, String docsVarName ) {

    ProcessListVariable docsVar = procData.getList(docsVarName);		

    String offlineFiles = fdFormData.getParameter(OFFLINE_FILE_PARAMETER);  
    if (StringUtilities.isNotEmpty(offlineFiles)) {
      String[] files = offlineFiles.split(",");
      for (String fileName : files) {
        if (StringUtilities.isEmpty(fileName))
          continue;

        String filePath = getOfflineFilePath(baseFilePath, fileName); 

        byte[] doc;
        try {
          doc = FileUtils.readBinaryFile(filePath);
        }
        catch (Exception e) {
          Logger.error(userInfo.getUtilizador(), instance, 
              "appendOfflineDocuments", 
              "error reading offline file " + filePath, e);      
          continue;
        }

        try {
          File f = new File(filePath);
          f.delete();
        }
        catch (Exception e) {
          Logger.error(userInfo.getUtilizador(), instance, 
              "appendOfflineDocuments", 
              "error deleting offline file " + filePath, e);      
        }

        DocumentData newDocument = new DocumentData();
        newDocument.setFileName(fileName);
        newDocument.setContent(doc);
        newDocument.setUpdated(Calendar.getInstance().getTime());

        Document savedDocument = 
          BeanFactory.getDocumentsBean().addDocument(userInfo, 
              procData, newDocument);

        try {
          docsVar.parseAndAddNewItem(
              String.valueOf(savedDocument.getDocId()));
        }
        catch (Exception e) {
          Logger.error(userInfo.getUtilizador(), instance, 
              "appendOfflineDocuments", 
              "error parsing document " + savedDocument.getDocId(), 
              e);      		    
        }
      }
    }
  }

  public static void uploadFile(InputStream inStream, String destFilePath) 
  throws Exception {
    File file = new File(destFilePath);
    FileOutputStream fos = new FileOutputStream(file);

    byte[] buf = new byte[1024];
    int len;
    while ((len = inStream.read(buf)) > 0) {
      fos.write(buf, 0, len);
    }

    fos.close();
  }
}
