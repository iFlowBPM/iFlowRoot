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
package pt.iflow.blocks;

import java.util.HashSet;
import java.util.List;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;


/**
 * <p>Title: BlockSQL</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public abstract class BlockSQL extends Block {
  public Port portIn, portSuccess, portEmpty, portError;

  public static final String sDATASOURCE = "JNDIName";
  public static final String sSEL = "Select";
  public static final String sFROM = "From";
  public static final String sWHERE = "Where";
  public static final String sWHEREVARS = "WhereVars";
  public static final String sGROUP = "Group";
  public static final String sORDER = "Order";
  public static final String sSINGLE = "ResultadoUnitario";
  public static final String sINTO = "Into";
  public static final String sNAMES = "Names";
  public static final String sVALUES = "Values";
  public static final String sTABLE = "Tabela";
  public static final String sSET = "Set";
  public static final String sSETVARS = "SetVars";
  public static final String sVARS = "Vars";
  public static final String sQuery = "Query";
  public static final String sNumResult = "NumeroResultados";
  
  public static final String ESCAPE_CHARACTER = "EscapeCharacter";

  protected static final String sSEPARATOR = ",";
  protected static final String sFUNC_PREFIX = "F_";

  private static HashSet<String> _hsUFTables;
  private static java.util.Date _dtLastBuild;

  static {
    buildTableCache();
  }

  private static synchronized void buildTableCache() {
    try {
//    HashSet hs = new HashSet();
//    Repository rep = Utils.getRepBean();

//    String[] files = rep.listFiles(Const.SYSTEM_ORGANIZATION, "SystemTables");
//    for (int i=0; files != null && i < files.length; i++) {
//    hs.add(files[i]);
//    }
      _dtLastBuild = new java.util.Date();
      _hsUFTables = new HashSet<String>(Utils.getSystemTables());
    }
    catch (Exception e) {
      Logger.debug(null,new BlockSQLInsert(0,0,0,""),"buildTableCache", "exception caught: " + e.getMessage(), e);
    }
  }

  public BlockSQL(int anFlowId,int id, int subflowblockid, String filename) {
    super(anFlowId,id, subflowblockid, filename);
    hasInteraction = false;
    saveFlowState = false;

    java.util.Date dtOld = _dtLastBuild;
    java.util.Date dtNow = new java.util.Date();

    long ltmp = dtOld.getTime();
    ltmp += (1000*60*60*24);  // build cache once every day

    if (ltmp > dtNow.getTime()) {
      buildTableCache();
      Logger.debug(null,this,"constructor","refreshing tables cache");
    }
  }

  public Port[] getInPorts (UserInfoInterface userInfo) {
    Port[] retObj = new Port[1];
    retObj[0] = portIn;
    return retObj;
  }

  public Port getEventPort() {
    return null;
  }

  public Port[] getOutPorts (UserInfoInterface userInfo) {
    Port[] retObj = new Port[3];
    retObj[0] = portSuccess;
    retObj[1] = portEmpty;
    retObj[2] = portError;
    return retObj;
  }

  /**
   * No action in this block
   * @return always 'true'
   */
  public String before(UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }

  /**
   * No action in this block
   *
   * @param dataSet a value of type 'DataSet'
   * @return always 'true'
   */
  public boolean canProceed(UserInfoInterface userInfo, ProcessData procData) {
    return true;
  }

  /**
   * Executes the block main action
   *
   * @param dataSet a value of type 'DataSet'
   * @return the port to go to the next block
   */
  public abstract Port after(UserInfoInterface userInfo, ProcessData procData);

  public String getDescription (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, true, "SQL");
  }

  public String getResult (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, false, "SQL Efectuado");
  }

  public String getUrl (UserInfoInterface userInfo, ProcessData procData) {
    return "";
  }

  protected boolean isSystemTable(String dataSource, String asTableName) {
    boolean retObj = false;

    if (_hsUFTables.contains(asTableName.toLowerCase().trim())) {
      retObj = true;
    }
    else {
      retObj = false;
    }

    return retObj;
  }


  protected List<String> tokenize(String asString) {

    List<String> retObj = Utils.tokenize(asString, BlockSQL.sSEPARATOR);

    String stmp = null;
    String stmp2 = null;

    // check for db functions with separator (undo tokenize)
    for (int i=0; retObj != null; i++) {
      if (i >= retObj.size()) break;

      stmp = (String)retObj.get(i);
      stmp = stmp.trim();
      stmp2 = stmp.toUpperCase();

      if (stmp2.startsWith(BlockSQL.sFUNC_PREFIX)) {

        // remove func prefix
        stmp = stmp.substring(BlockSQL.sFUNC_PREFIX.length());

        if (stmp2.indexOf(")") > -1) {
          // no "," in function arguments
          // set new value (without function prefix)
          retObj.set(i,stmp);
        }
        else {
          // now concatenate arguments until ")" is found
          int j=i+1;
          for (; j < retObj.size(); j++) {
            stmp2 = (String)retObj.get(j);

            stmp = stmp.concat(",").concat(stmp2);

            if (stmp2.indexOf(")") > -1) {
              break;
            }
          }

          if (j > i) {
            retObj.set(i,stmp);
            for (int k=(i+1); k <= j; j--) {
              retObj.remove(k);
            }
          }
        }
      }
    }

    return retObj;
  }

}
