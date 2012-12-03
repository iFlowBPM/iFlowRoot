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
package pt.iflow.core;

import java.io.InputStream;
import java.util.Properties;
import java.io.BufferedReader;
import java.io.InputStreamReader;


import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.RepositoryFile;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;

public class Version {

  private static final String DEF_VERSION = "4.0";
  private static final String NA = "-";
  private static final String VERSION_FILE = "pt/iflow/version.properties";
  
  public static final String VERSION;
  public static final String BUILD;  
  public static final String BUILD_DATE;
  public static final String WEB;
  public static final String EDITOR;
  public static final String BLOCKS;

  
  static {
    InputStream is = null;
    
    String version = DEF_VERSION;
    String build = NA;
    String date = NA;
    String web = NA;
    String editor = NA;
    String blocks = NA;
    
    @SuppressWarnings("unused")
    String web_release_notes = NA;
    @SuppressWarnings("unused")
    String blocks_release_notes = NA;
    
    try {
      RepositoryFile rf = BeanFactory.getRepBean().getClassFile(Const.SYSTEM_ORGANIZATION, VERSION_FILE); 
      is = rf.getResourceAsStream();

      Properties prop = new Properties();
      prop.load(is);
      
      version = prop.getProperty("version", version);
      build = prop.getProperty("build", build);
      date = prop.getProperty("buildDate", date);
      web = prop.getProperty("web", web);
      editor = prop.getProperty("editor", editor);
      blocks = prop.getProperty("blocks", blocks);
    }
    catch (Throwable t) {
      Logger.warning(null, "pt.iflow.core.Version", "<init>", "Error opening version descriptor.");      
    }
    finally {
      if (is != null) {
        try {
          is.close();
        }
        catch (Throwable t) {}
      }
    }
    VERSION = version;
    BUILD = build;
    BUILD_DATE = date;
    WEB = web;
    EDITOR = editor;
    BLOCKS = blocks;
    
    Logger.info(null, "pt.iflow.core.Version", "<init>", "iFlow version "+VERSION);
    Logger.info(null, "pt.iflow.core.Version", "<init>", "iFlow build "+BUILD);
    Logger.info(null, "pt.iflow.core.Version", "<init>", "iFlow buildDate "+BUILD_DATE);
    Logger.info(null, "pt.iflow.core.Version", "<init>", "iFlow web "+WEB);
    Logger.info(null, "pt.iflow.core.Version", "<init>", "iFlow editor "+EDITOR);
    Logger.info(null, "pt.iflow.core.Version", "<init>", "iFlow blocks "+BLOCKS);
    
  }

  public static String readWebReleaseNotes() {
    InputStream is = null;
    
    try {
      is = Version.class.getResourceAsStream("/pt/iflow/web/RELEASE_NOTES.txt");
      return readFromStream(is);
    }
    catch (Throwable t) {
      Logger.warning(null, "pt.iflow.core.Version", "readWebReleaseNotes", "Error opening web release notes file", t);      
    }
    return null;
  }

  public static String readBlocksReleaseNotes() {
    InputStream is = null;

    try {
      RepositoryFile rf = BeanFactory.getRepBean().getClassFile(Const.SYSTEM_ORGANIZATION, "pt/iflow/blocks/RELEASE_NOTES.txt"); 
      is = rf.getResourceAsStream();
      if (is==null) is = Version.class.getResourceAsStream("/pt/iflow/blocks/RELEASE_NOTES.txt");
      return readFromStream(is);
    }
    catch (Throwable t) {
      Logger.warning(null, "pt.iflow.core.Version", "readBlocksReleaseNotes", "Error opening blocks release notes file", t);      
    }
    return null;
  }
  
  public static String readEditorReleaseNotes() {
    InputStream is = null;

    try {
      is = Version.class.getResourceAsStream("/pt/iflow/editor/RELEASE_NOTES.txt");
      return readFromStream(is);
    }
    catch (Throwable t) {
      Logger.warning(null, "pt.iflow.core.Version", "readBlocksReleaseNotes", "Error opening blocks release notes file", t);      
    }
    return null;
  }
  
  private static String readFromStream(InputStream is) { 
    StringBuilder release_notes = new StringBuilder();
    
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(is));
      String line = null;
      while ((line = reader.readLine()) != null) {
        release_notes.append(line + "\n");
      }
    }
    catch (Throwable t) {
      Logger.warning(null, "pt.iflow.core.Version", "readFromStream", "Error processing release notes stream", t);      
    }
    finally {
      if (is != null) {
        try {
          is.close();
        }
        catch (Throwable t) {}
      }
    }
    return release_notes.length() == 0 ? NA : release_notes.toString();
  }
}
