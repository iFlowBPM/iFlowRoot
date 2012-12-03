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


/**
 * <p>Title: DataSet Variables</p>
 * <p>Description: This class stores all variables used in the DataSet</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: iKnow</p>
 * @author João Valentim
 * @version 1.0
 */

public class DataSetVariables {

  static final public String sPROCESS_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
  static final public String sPROCESS_SQL_DATE_FORMAT = "YYYY-MM-DD HH24:MI:SS";

  // CONSTS
  static final public String OPENED  = "Aberto";
  static final public String CLOSED  = "Fechado";
  static final public String USER_CANCELED  = "Cancelado pelo Utilizador";

  static final public String SESSION  = "SESSION";
  static final public String DB  = "DB";


  /**
   * Creator
   */
  static final public String USER_ID  	    = "userid";
  /**
   * Não existem referencias para esta
   * @deprecated
   */
  static final public String END      	    = "end";
  /**
   * ID do fluxo
   */
  static final public String FLOWID   	    = "flowid";
  /**
   * ID do processo
   */
  static final public String PID      	    = "pid";
  /**
   * ID da ramificação do processo
   */
  static final public String SUBPID         = "subpid";
  /**
   * Número do processo
   */
  static final public String PNUMBER   	    = "pnumber";
  
  /**
   * Creador do processo
   */
  static final public String CREATOR        = "creator";
  
  /**
   * LOGIN?? Usar UTILIZADOR
   * @deprecated
   */
  static final public String LOGIN    	    = "LOGIN";
  /**
   * @deprecated não usado?
   */
  static final public String STATE    	    = "STATE";
  /**
   * @deprecated não usado?
   */
  static final public String STATE_DESC     = "STATE_DESC";
  /**
   * Estado do processo/resultado da avaliação do bloco?
   * Não devia estar aqui!
   */
  static final public String RESULT    	    = "RESULT";
  /**
   * Linkar com data de modificação do processo.
   * Não devia estar aqui!
   */
  static final public String MDATE    	    = "MDATE";

  /* Linkar estas com o process head */
  static final public String PROCESS_CREATOR= "PROCESS_CREATOR";
  static final public String PROCESS_CREATION_DATE= "PROCESS_CREATION_DATE";
  static final public String PROCESS_STATE  = "PROCESS_STATE";
  static final public String PROCESS_STATE_DESC  = "PROCESS_STATE_DESC";
  static final public String PROCESS_SAVED  = "PROCESS_SAVED";
  static final public String PROCESS_NOT_IN_CREATOR= "PROCESS_NOT_IN_CREATOR";

  // Sobretudo temp data...
  static final public String FLOW_STATE         = "FLOW_STATE";
  static final public String FLOW_STATE_RESULT  = "FLOW_STATE_RESULT";

  // identificação do utilizador actual. "linkar" com objecto UserInfo 
  static final public String UTILIZADOR     = "UTILIZADOR";
  static final public String PERFIL         = "PERFIL";
  static final public String CODBALCAO      = "CODBALCAO";
  static final public String NOMEBALCAO     = "NOMEBALCAO";
  static final public String NUMEMPREGADO   = "NUMEMPREGADO";
  static final public String NOMEUTILIZADOR = "NOMEUTILIZADOR";
  static final public String NOMEUTILIZADORABREV = "NOMEUTILIZADORABREV";

  // stuff from activity table or activity aux
  static final public String ACT_USERID      = "USERID";
  static final public String ACT_FLOWID      = "FLOWID";
  static final public String ACT_PID         = "PID";
  static final public String ACT_SUBPID      = "SUBPID";
  static final public String ACT_TYPE        = "TYPE";
  static final public String ACT_PRIORITY    = "PRIORITY";
  static final public String ACT_CREATED     = "CREATED";
  static final public String ACT_STARTED     = "STARTED";
  static final public String ACT_ARCHIVED    = "ARCHIVED";
  static final public String ACT_DESCRIPTION = "DESCRIPTION";
  static final public String ACT_URL         = "URL";
  static final public String ACT_STATUS      = "STATUS";
  static final public String ACT_EMP         = "EMP";

  
}
