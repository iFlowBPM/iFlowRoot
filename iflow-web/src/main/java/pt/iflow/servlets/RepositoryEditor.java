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
package pt.iflow.servlets;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.activation.MimetypesFileTypeMap;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.Repository;
import pt.iflow.api.core.RepositoryFile;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;

import com.lowagie.text.html.HtmlEncoder;

/**
 * Allows editing of repository stored files.
 * 
 * @author Luis Cabral
 * @version 10.04.2009
 */
public class RepositoryEditor {

  /** Default Serial Version UID. */
  private static final long serialVersionUID = 1L;

  /**
   * C'tor.
   */
  private RepositoryEditor() {
  }

  /**
   * Store the given file into the repository.
   * 
   * @param userInfo
   *          User information.
   * @param file
   *          Name of the file being stored (path element is not required).
   * @param type
   *          Type of file being stored.
   * @param content
   *          Content of the file to store.
   * @return True if file was successfully stored, false otherwise.
   */
  public static boolean storeFile(UserInfoInterface userInfo, String file, String type, String content) {
    boolean ret = false;
    try {
      if (StringUtils.isBlank(file) || StringUtils.isBlank(type) || StringUtils.isBlank(content) || userInfo == null) {
        Logger.warning(userInfo.getUtilizador(), "RepositoryEditor", "storeFile", "Unable to save file (file=\"" + file
            + "\", type=\"" + type + "\").");
        return ret;
      }

      Repository rep = BeanFactory.getRepBean();
      byte[] data = content.getBytes();

      if (StringUtils.equals(type, ResourceNavConsts.STYLESHEETS)) {
        ret = rep.setStyleSheet(userInfo, file, data);
      } else if (StringUtils.equals(type, ResourceNavConsts.EMAIL_TEMPLATES)) {
        ret = rep.setEmailTemplate(userInfo, file, data);
      } else if (StringUtils.equals(type, ResourceNavConsts.PRINT_TEMPLATES)) {
        ret = rep.setPrintTemplate(userInfo, file, data);
      } else if (StringUtils.equals(type, ResourceNavConsts.PUBLIC_FILES)) {
        ret = rep.setWebFile(userInfo, file, data);
      }

      if (!ret) {
        Logger.warning(userInfo.getUtilizador(), "RepositoryEditor", "storeFile", "Unable to store file information (file=\""
            + file + "\", type=\"" + type + "\").");
      }
    } catch (Exception e) {
      Logger.error(userInfo.getUtilizador(), "RepositoryEditor", "storeFile", "Unable to store file information.", e);
    }
    return ret;
  }

  /**
   * Retrieves the given file from the repository.
   * 
   * @param userInfo
   *          User information.
   * @param file
   *          Name of the file being retrieved (path element is not required).
   * @param type
   *          Type of file being retrieved.
   * @return File content as an HTML encoded string.
   */
  public static String retrieveFile(UserInfoInterface userInfo, String file, String type) {
    StringBuffer retObj = new StringBuffer();
    try {
      if (StringUtils.isBlank(file) || StringUtils.isBlank(type) || userInfo == null) {
        Logger.warning(userInfo.getUtilizador(), "RepositoryEditor", "retrieveFile", "Unable to load file (file=\"" + file
            + "\", type=\"" + type + "\").");
        return retObj.toString();
      }

      Repository rep = BeanFactory.getRepBean();
      RepositoryFile repFile = null;

      if (StringUtils.equals(type, ResourceNavConsts.STYLESHEETS)) {
        repFile = rep.getStyleSheet(userInfo, file);
      } else if (StringUtils.equals(type, ResourceNavConsts.EMAIL_TEMPLATES)) {
        repFile = rep.getEmailTemplate(userInfo, file);
      } else if (StringUtils.equals(type, ResourceNavConsts.PRINT_TEMPLATES)) {
        repFile = rep.getPrintTemplate(userInfo, file);
      } else if (StringUtils.equals(type, ResourceNavConsts.PUBLIC_FILES)) {
        repFile = rep.getWebFile(userInfo, file);
      }

      if (repFile != null && repFile.exists()) {
        BufferedReader inputStream = new BufferedReader(new InputStreamReader(repFile.getResourceAsStream()));
        String line = "";
        while ((line = inputStream.readLine()) != null) {
          retObj.append(line + "\r");
        }
      } else {
        Logger.warning(userInfo.getUtilizador(), "RepositoryEditor", "retrieveFile", "Unable to edit file (file=\"" + file
            + "\", type=\"" + type + "\").");
      }
    } catch (Exception e) {
      Logger.error(userInfo.getUtilizador(), "RepositoryEditor", "retrieveFile", "Unable to retrieve file information.", e);
    }
    return HtmlEncoder.encode(retObj.toString());
  }

  /**
   * Check if given file is one of the given MIME types. The validation is made
   * with "startsWith", so adding only the start of the MIME type is acceptable
   * (eg.: check MIME type "text" for "text/plain" returns true).
   * 
   * @param userInfo
   *          User information.
   * @param file
   *          Name of the file being stored (path element is not required).
   * @param mimes
   *          MIME types to check for.
   * @param ignores
   *          Ignore file if has one of given suffixes, returning false.
   * @return True if the validation is successful, false otherwise.
   */
  public static boolean checkFileMIME(UserInfoInterface userInfo, String file, String[] mimes, String[] ignores) {
    boolean ret = false;
    if (ignores == null) {
      ignores = new String[] {};
    }
    if (userInfo != null && file != null && mimes != null && ignores != null) {
      String fileMIME = new MimetypesFileTypeMap().getContentType(file);
      for (String mime : mimes) {
        if (fileMIME.startsWith(mime)) {
          ret = true;
          break;
        }
      }

      for (String ignore : ignores) {
        if (file.endsWith(ignore)) {
          ret = false;
          break;
        }
      }

      if (Logger.isDebugEnabled()) {
        Logger.debug(userInfo.getUtilizador(), "RepositoryEditor", "checkFileMIME", "File \"" + file + "\" is MIME type \""
            + fileMIME + "\" (tried MIME types \"" + listArrayItemsAsString(mimes) + "\", ignored files with suffixes \""
            + listArrayItemsAsString(ignores) + "\",returning \"" + ret + "\").");
      }
    } else if (userInfo != null) {
      Logger.warning(userInfo.getUtilizador(), "RepositoryEditor", "checkFileMIME", "Unable to check file MIME type (file=" + file
          + ", mimes=" + listArrayItemsAsString(mimes) + ", ignores=" + listArrayItemsAsString(ignores) + ").");
    }
    return ret;
  }

  public static RepositoryFile[] listFiles(UserInfoInterface userInfo, String type) {
    RepositoryFile[] result = null;
    Repository rep = BeanFactory.getRepBean();
    if (ResourceNavConsts.STYLESHEETS.equals(type)) {
      result = rep.listStyleSheets(userInfo);
    } else if (ResourceNavConsts.EMAIL_TEMPLATES.equals(type)) {
      result = rep.listEmailTemplates(userInfo);
    } else if (ResourceNavConsts.PUBLIC_FILES.equals(type)) {
      result = rep.listWebFiles(userInfo);
    } else if (ResourceNavConsts.PRINT_TEMPLATES.equals(type)) {
      result = rep.listPrintTemplates(userInfo);
    } else {
      Logger.warning("", "", "", "Erro! Tipo desconhecido: " + type);
    }
    return result;
  }

  private static String listArrayItemsAsString(Object[] array) {
    StringBuffer retObj = new StringBuffer();
    retObj.append("{");
    for (int i = 0, l = array.length; i < l; i++) {
      Object item = array[i];
      if (i == 0) {
        retObj.append(item);
      } else {
        retObj.append(", " + item);
      }
    }
    retObj.append("}");
    return retObj.toString();
  }
}
