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
package pt.iflow.api.utils;

import pt.iknow.utils.crypt.CryptUtils;


public final class RepositoryWebOpCodes {
  
  /**
   * Prevent instantiation of this class
   */
  private RepositoryWebOpCodes(){}
  
  /**
   * Key to encrypt passwords
   */
  public static final CryptUtils _crypt = new CryptUtils("achave");

  /**
   * Default encoding for data sharing
   */
  public static final String DEFAULT_ENCODING = "UTF-8";
  
  
  /**
   * Internal representation of an invalid OP code
   */
  public static final int INVALID_REQUEST = -1;
  
  /**
   * OP code to check repository connectivity
   */
  public static final int CHECK_CONNECTION = 13;
  
  /**
   * OP code to validate authentication
   */
  public static final int AUTHENTICATE_USER = 14;
  
  
  // New features
  
  /**
   * OP code to list existing Profiles
   */
  public static final int LIST_PROFILES = 20;

  /**
   * OP code to list existing StyleSheets
   */
  public static final int LIST_STYLESHEETS = 21;
  
  /**
   * OP code to list existing Email templates
   */
  public static final int LIST_EMAILS = 22;

  /**
   * OP code to list existing Print Templates
   */
  public static final int LIST_PRINTS = 23;
  
  /**
   * OP code to list existing Chart styles
   */
  public static final int LIST_CHARTS = 24;

  /**
   * OP code to list existing Flows
   */
  public static final int LIST_FLOWS = 25;

  /**
   * OP code to list existing Sub Flows
   */
  public static final int LIST_SUB_FLOWS = 26;

  /**
   * OP code to list existing Libraries
   */
  public static final int LIST_LIBRARIES = 27;

  /**
   * OP code to list existing WebServices
   */
  public static final int LIST_WEBSERVICES = 28;

  /**
   * OP code to list existing Events
   */
  public static final int LIST_EVENTS = 29;

  // 2. Getters
  
  /**
   * OP code to get an existing Flow
   */
  public static final int GET_FLOW = 30;

  /**
   * OP code to get an existing Sub Flow
   */
  public static final int GET_SUBFLOW = 31;
    
  /**
   * OP code to get an existing Library
   */
  public static final int GET_LIBRARY = 32;

  /**
   * OP code to get an existing WebService
   */
  public static final int GET_WEBSERVICE = 33;

  /**
   * OP code to get an existing Event
   */
  public static final int GET_EVENT = 34;

  /**
   * OP code to get an existing Icon
   */
  public static final int GET_ICON = 35;
  
  /**
   * OP code to get a class
   */
  public static final int GET_CLASS = 36;
  
  /**
   * OP code to get the flow's full state history
   */
  public static final int GET_FLOW_STATE_HISTORY = 37;
  
  /**
   * OP code to get a state's log for a process 
   */
  public static final int GET_FLOW_STATE_LOGS = 38;
  
  
  
  // 3. Setters

  /**
   * OP code to deploy a new Flow
   */
  public static final int SET_FLOW = 40;

  /**
   * OP code to deploy a new Sub Flow
   */
  public static final int SET_SUBFLOW = 41;
    
  /**
   * OP code to upload a new Web Service
   */
  public static final int SET_WEBSERVICE = 42;

  
  // Extended data
  
  /**
   * OP code to get extended information about a flow
   */
  public static final int HAS_EXTENDED_API = 50;

  /**
   * OP code to get extended information about a flow
   */
  public static final int GET_FLOW_INFO = 51;

  /**
   * OP code to list flows with extended information
   */
  public static final int LIST_FLOWS_EXTENDED = 52;

  /**
   * OP code to logout user
   */
  public static final int LOGOUT = 53;

  /**
   * OP code to retrieve a zip file with all icons
   */
  public static final int GET_ZIPPED_ICONS = 54;

  /**
   * OP code to retrieve a zipped file with all libraries
   */
  public static final int GET_ZIPPED_LIBRARIES = 55;

  /**
   * OP code to get last modified status from icons (latest data from all files)
   */
  public static final int GET_LAST_MODIFIED_ICONS = 56;

  /**
   * OP code to get last modified status from libraries (latest data from all files)
   */
  public static final int GET_LAST_MODIFIED_LIBRARIES = 57;

  /**
   * OP code to retrieve a jar file with all classes (blocks)
   */
  public static final int GET_CLASS_JAR = 58;

  /**
   * OP code to get current user locale
   */
  public static final int GET_USER_LOCALE = 59;

  
  // Hashing 
  
  /**
   * OP code to get an hash code from an existing Flow
   */
  public static final int HASH_FLOW = 60;

  /**
   * OP code to get an hash code from an existing Sub Flow
   */
  public static final int HASH_SUBFLOW = 61;
    
  /**
   * OP code to get an hash code from an existing Library
   */
  public static final int HASH_LIBRARY = 62;

  /**
   * OP code to get an hash code from an existing WebService
   */
  public static final int HASH_WEBSERVICE = 63;

  /**
   * OP code to get an hash code from an existing Icon
   */
  public static final int HASH_ICON = 64;
  
  /**
   * OP code to get an hash code from an existing Class
   */
  public static final int HASH_CLASS = 65;

  /**
   * OP code to get an hash code computed from all icons
   */
  public static final int GET_ICONS_HASH = 66;

  /**
   * OP code to get an hash code computed from all libraries
   */
  public static final int GET_LIBRARIES_HASH = 67;

  /**
   * OP code to retrieve all modified classes/resources files
   */
  public static final int GET_MODIFIED_CLASS_HASH = 68;


  // Versioning
  
  /**
   * OP code to list all versions of a Flow
   */
  public static final int LIST_FLOW_VERSIONS = 70;
  
  /**
   * OP code to list all versions of a Sub Flow
   */
  public static final int LIST_SUB_FLOW_VERSIONS = 71;
  
  /**
   * OP code to get a version comment of a flow
   */
  public static final int GET_FLOW_VERSION_COMMENT = 72;
  
  /**
   * OP code to get a version comment of a sub flow
   */
  public static final int GET_SUB_FLOW_VERSION_COMMENT = 73;
  
  /**
   * OP code to get a versioned flow (not current)
   */
  public static final int GET_FLOW_VERSION = 74;
  
  /**
   * OP code to get a versioned sub flow (not current)
   */
  public static final int GET_SUB_FLOW_VERSION = 75;
  
  /**
   * OP code to upload and create a new flow version
   */
  public static final int SET_FLOW_VERSION = 76;
  
  /**
   * OP code to upload and create a new sub flow version
   */
  public static final int SET_SUB_FLOW_VERSION = 77;
  
  /**
   * OP code to get a versioned flow info
   */
  public static final int GET_FLOW_VERSION_INFO = 78;
  
  /**
   * OP code to get a versioned sub flow info
   */
  public static final int GET_SUB_FLOW_VERSION_INFO = 79;
 
  
  // Other features
  /**
   * OP code to list all available Data Sources in container
   */
  public static final int LIST_DATA_SOURCES = 80;

  /**
   * OP code to get all available connectors
   */
  public static final int GET_CONNECTORS = 81;

  // Flow Docs
  /**
   * OP code to get flow doc's files. 
   */
  public static final int FLOW_DOC_GET_FILES = 90;

  public static final int LIST_SIGNATURE_TYPES = 100;
  
  // i18n
  /**
   * OP code to retrieve a messages file
   */
  public static final int GET_I18N_MESSAGES = 110;
  
  /**
   * OP code to retrieve all modified classes/resources files
   */
  public static final int GET_MODIFIED_I18N_HASH = 111;

  /**
   * OP code to list extra User Properties
   */
  public static final int LIST_EXTRA_PROPERTIES = 112;

  // BD Synch
  /** OP code to get a table's description */
  public static final int DB_TABLE_DESC = 120;

  /**
   * OP code to list task annnotation labels
   */
  public static final int LIST_PROCESS_TASK_ANNOTATION_LABELS = 130;

}
