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
package pt.iflow.blocks.form;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.FormProps;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.ServletUtils;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class ArrayTable implements FieldInterface {

  public static String[] getPropertyNames() {
    String[] propertyNames = {
        "variableList",
        "formatList",
        "borderColor",
        "borderColorDark",
        "borderColorLight",
        "borderColorTitle",
        "borderColorDarkTitle",
        "borderColorLightTitle",
        "borderColorBody",
        "borderColorDarkBody",
        "borderColorLightBody",
        "backgroundColorTitle",
        "backgroundColorBody",
        "foregroundColorTitle",
        "foregroundColorBody",
        "classBody",
        "classTitle",
        "widthTitle",
        "widthBody",
        "heightTitle",
        "heightBody"
    };

    return propertyNames;
  }

  public static String[] getPropertyDescriptions() {
    String[] propertyDescriptions = {
        "Lista de Variáveis",
        "Lista de Formatações",
        "Côr Border",
        "Côr Border Escura",
        "Côr Border Clara",
        "Côr Border Topo",
        "Côr Border Escura Topo",
        "Côr Border Clara Topo",
        "Côr Border Resultados",
        "Côr Border Escura Resultados",
        "Côr Border Clara Resultados",
        "Côr Background Topo",
        "Côr Background Resultados",
        "Côr Foreground Topo",
        "Côr Foreground Resultados",
        "Estilo Topo",
        "Estilo Resultados",
        "Largura Células Topo",
        "Largura Células Resultados",
        "Altura Células Topo",
        "Altura Células Resultados",
    };
    return propertyDescriptions;
  }

  public String getDescription() {
    return "Tabela";
  }

  // table generation
  public void generateHTML (PrintStream out, Properties prop) {}

  public void generateXSL(PrintStream out) {}

  public void generateXML(PrintStream out, Properties prop) {}

  public String getXML(Properties prop) {
    try {
      String sVar = null;
      String stmp = null;
      String stmp2 = null;
      boolean btmp = false;

      StringBuffer sb = new StringBuffer();
      StringBuffer sb2 = null;
      sb.append("<field><type>arraytable</type>");

      sb.append("<fieldid>").append(prop.getProperty("fieldid")).append("</fieldid>");

      stmp2 = prop.getProperty("services_enabled");
      if (stmp2 != null && stmp2.equalsIgnoreCase("true")) {
        stmp2 = null;
      }
      else {
        stmp2 = "dummy";
      }

      sb.append("<print>");
      stmp = prop.getProperty("service_print");
      btmp = (new Boolean(stmp)).booleanValue();
      if (stmp2 == null && btmp) {
        // service print on
        sb.append("true");
      }
      else {
        sb.append("false");
      }
      sb.append("</print>");
      sb.append("<export>");
      stmp = prop.getProperty("service_export");
      btmp = (new Boolean(stmp)).booleanValue();
      if (stmp2 == null && btmp) {
        // service export on
        sb.append("true");
      }
      else {
        sb.append("false");
      }
      sb.append("</export>");

      btmp = false;

      // list with column names
      List<String> alColNames = new ArrayList<String>();
      Set<String> hsIgnoreColumns = new HashSet<String>();

      for (int i=0; true; i++) {
        String sTitle = prop.getProperty(i + "_title");
        if (sTitle == null) break;
        alColNames.add(sTitle);
        stmp = prop.getProperty(i + "_IGNORE");
        if (stmp != null && stmp.equals("true")) {
          hsIgnoreColumns.add(String.valueOf(i));
        }
      }

      if (alColNames.size() == hsIgnoreColumns.size()) {
        // no columns to display!!!!!
        return null;
      }

      // key=macrotitle; value=ArrayList(coltitles)
      HashMap<String,List<String>> hmColSpans = new HashMap<String, List<String>>();
      // key=macrotitle; value = coltitle
      List<String> alMacroTitles = new ArrayList<String>();

      for (int col=0; col < alColNames.size(); col++) {

        String sColName = alColNames.get(col);
        stmp = prop.getProperty(col + "_macrotitle");

        List<String> alCols = null;

        if (stmp != null && !stmp.equals("")) {
          if(!hsIgnoreColumns.contains(String.valueOf(col))) {  // desta maneira nao vai acrescentar o macrotitulo a mais
            if (hmColSpans.containsKey(stmp)) {
              alCols = hmColSpans.get(stmp);
            }
            else {
              alCols = new ArrayList<String>();
              alMacroTitles.add(stmp);
            }

            alCols.add(sColName);
            hmColSpans.put(stmp, alCols);
          }

        }
        else {
          alMacroTitles.add("");
        }
      }

      // validate header display
      if (!Utils.string2bool(prop.getProperty(FormProps.DISABLE_TABLE_HEADER))) {
        // macro header row
        if (hmColSpans.size() > 0) {
          // at least one macro header cell

          sb.append("<row>");
          for (int col=0; col < alMacroTitles.size(); col++) {
            String sMacroName = (String)alMacroTitles.get(col);

            int nColspan = 1;

            if (hmColSpans.containsKey(sMacroName)) {
              List<String> alMacroCols = hmColSpans.get(sMacroName);
              nColspan = alMacroCols.size();
            }


            sb.append("<col>");
            sb.append("<header>true</header>");	
            sb.append("<value>").append(sMacroName).append("</value>");
            sb.append("<align>center</align>");
            sb.append("<colspan>").append(nColspan).append("</colspan>");
            sb.append("</col>");
          }
          sb.append("</row>");
        }

        // header row
        sb.append("<row>");
        for (int col=0; col < alColNames.size(); col++) {
          if (hsIgnoreColumns.contains(String.valueOf(col))) continue;
          sb.append("<col>");
          sb.append("<header>true</header>");
          sb.append("<value>").append((String)alColNames.get(col)).append("</value>");
          sb.append("<align>").append(prop.getProperty(col + "_align")).append("</align>");
          sb.append("</col>");
        }
        sb.append("</row>");
      }

      int nMaxRow = -1;
      stmp = prop.getProperty("_MAX_ROW");
      try {
        nMaxRow = Integer.parseInt(stmp);
      }
      catch (Exception ee) {
        nMaxRow = -1;
      }

      boolean bOutLoop = false;
      if (nMaxRow < 0) bOutLoop = true;

      int nDataRows = 0;

      for (int row=0; row < nMaxRow || bOutLoop; row++) {
        
        String disabledRow = prop.getProperty("row_"+row + "_" + FormProps.DISABLED);
        if (StringUtils.equals(disabledRow, "true"))
          continue;

        StringBuilder sbRow = new StringBuilder();
        sbRow.append("<row>");

        sb2 = new StringBuffer();
        btmp = false;
        for (int col=0; col < alColNames.size(); col++) {

          if (hsIgnoreColumns.contains(String.valueOf(col))) continue;

          stmp = prop.getProperty(col + "_" + row + "_value");

          sVar = prop.getProperty(col + "_variable");

          String redBlue = prop.getProperty(col + "_" + row + "_value_control"); // nhiiiiiihihihihihihii

          if (stmp == null) {
            // table end
            bOutLoop = false;
            stmp = "";
          }

          if (nMaxRow > 0 || !stmp.equals("")) {
            btmp = true;
          }

          sb2.append("<col>");
          sb2.append("<header>false</header>");
          sb2.append("<value>").append(stmp).append("</value>");

          if (sVar != null && !sVar.equals("")) {
            sb2.append("<variable>").append(sVar).append("</variable>");
          }

          stmp = prop.getProperty(col + "_" + row + "_suffix");
          if (stmp == null) stmp = "";
          sb2.append("<suffix>").append(stmp).append("</suffix>");

          stmp = prop.getProperty(col + "_align");
          if (stmp == null) stmp = "";
          sb2.append("<align>").append(stmp).append("</align>");

          stmp = prop.getProperty(col + "_bgcolor");
          if (stmp == null) stmp = "";
          sb2.append("<bgcolor>").append(stmp).append("</bgcolor>");

          Logger.debug("ArrayTable",this,"getXML","Row: "+row+" Col: "+col+" STMP: "+stmp+" sVar: "+sVar+" RedBlue: "+redBlue);
          if(null != redBlue) { // MUAHAHAHAHAHAHAHAHA
            sb2.append("<"+redBlue+">yes</"+redBlue+">");
          }

          sb2.append("</col>");
        }

        if (btmp) {
          // btmp indicates that row has at least one cell with content

          // add row control list stuff
          stmp = prop.getProperty(row + "_" + FormProps.sROW_CONTROL_LIST);
          if (StringUtils.isNotEmpty(stmp)) {
            sbRow.append(stmp);
          }

          // add column data
          sbRow.append(sb2.toString());
          nDataRows++;
        }

        sbRow.append("</row>");
        
        if (btmp) {
          
          String subheader = prop.getProperty("row_"+ row + "_" + FormProps.SUB_HEADER);
          if (StringUtils.equals(subheader, "true")) {            
            sb.append("<row>");
            sb.append("<subheader>true</subheader>");
            for (int col=0; col < alColNames.size(); col++) {
              if (hsIgnoreColumns.contains(String.valueOf(col))) continue;
              String value = prop.getProperty("row_"+ row + "_" + FormProps.SUB_HEADER_COL_PREFIX + col);
              if (StringUtils.isEmpty(value))
                value = alColNames.get(col);
              sb.append("<col>");
              sb.append("<header>false</header>");
              sb.append("<subheader>true</subheader>");
              sb.append("<value>").append(value).append("</value>");
              
              stmp = prop.getProperty(col + "_align");
              if (stmp == null) stmp = "center";
              sb.append("<align>").append(stmp).append("</align>");

              stmp = prop.getProperty(col + "_bgcolor");
              if (StringUtils.isNotEmpty(stmp))
                sb.append("<bgcolor>").append(stmp).append("</bgcolor>");

              stmp = prop.getProperty(row + "_" + FormProps.sROW_CONTROL_LIST);
              if (StringUtils.isNotEmpty(stmp)) {
                sb.append(stmp);
              }

              sb.append("</col>");
            }
            sb.append("</row>");
          }
          
        }
        
        sb.append(sbRow);
      }

      // add properties with count of data rows and number of columns
      sb.append("<datarow_count>");
      sb.append(nDataRows);
      sb.append("</datarow_count>");

      sb.append("<column_count>");
      sb.append((alColNames.size() - hsIgnoreColumns.size()));
      sb.append("</column_count>");

      sb.append("<even_field>").append(prop.getProperty("even_field")).append("</even_field>");

      sb.append("</field>");

      return sb.toString();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public boolean isOutputField() {
    return true;
  }

  public boolean isArrayTable() {
    return true;
  }

  public void setup(UserInfoInterface userInfo, ProcessData procData, Properties props, ServletUtils response) {
  }

  public void initVariable(UserInfoInterface userInfo, ProcessData procData, String name, Properties props) {
  }
}
