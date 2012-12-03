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
import java.net.URLEncoder;
import java.util.Properties;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.flows.FlowType;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.ServletUtils;
import pt.iflow.api.utils.UserInfoInterface;

public class Chart implements FieldInterface {
  private static final String sOPT_DATASOURCE = "datasource";
  private static final String sOPT_FLOWID = "flowid";
  private static final String sOPT_PID = "pid";
  private static final String sOPT_SUBPID = "subpid";

  public String getDescription() {
    return "Chart";
  }

  public void generateHTML(PrintStream out, Properties prop) {
  }

  public void generateXSL(PrintStream out) {
  }

  public void generateXML(PrintStream out, Properties prop) {
  }

  public String getXML(Properties prop) {
    try {
      StringBuffer sb = new StringBuffer();
      String stmp = null;
      String sAlign = "";
      String sTitle = "";
      String sWidth = "";
      String sHeight = "";
      String sTemplate = "";
      String sDSL = "";
      String sDSV = "";
      
      Logger.debug("", this, "", prop.toString());
      
      stmp = prop.getProperty("align");
      if (stmp == null) stmp = "center";
      sAlign = stmp;
      
      stmp = prop.getProperty("chart_title");
      if (stmp == null) stmp = "";
      sTitle = stmp;
      
      stmp = prop.getProperty("chart_template_code");
      if (stmp == null) stmp = "";
      sTemplate = stmp;
      
      stmp = prop.getProperty("chart_width");
      if (stmp == null) stmp = "";
      sWidth = stmp;
      
      stmp = prop.getProperty("chart_height");
      if (stmp == null) stmp = "";
      sHeight = stmp;
      
      sDSL = prop.getProperty("chart_dsl");
      sDSV = prop.getProperty("chart_dsv");
      
      String datasource = prop.getProperty(sOPT_DATASOURCE);
      if (datasource == null) datasource = "";
      String flowId = prop.getProperty(sOPT_FLOWID);
      String pid = prop.getProperty(sOPT_PID);
      String subPid = prop.getProperty(sOPT_SUBPID);
      String flowExecType = prop.getProperty(Const.FLOWEXECTYPE);
     
      
      sb.append("<field><type>image</type>");
      sb.append("<url><![CDATA[Chart?")
        .append("chart.title=").append(URLEncoder.encode(sTitle, "UTF-8"))
        .append("&width=").append(sWidth)
        .append("&height=").append(sHeight)
        .append("&template=").append(sTemplate)
        .append("&dsl=").append(sDSL)
        .append("&dsv=").append(sDSV)
        .append("&"+sOPT_DATASOURCE+"=").append(datasource)
        .append("&"+sOPT_FLOWID+"=").append(flowId)
        .append("&"+sOPT_PID+"=").append(pid)
        .append("&"+sOPT_SUBPID+"=").append(subPid)
        .append(flowExecType==null ? "" : "&"+Const.FLOWEXECTYPE+"="+flowExecType)
        .append("]]></url>");

      sb.append("<align>").append(sAlign).append("</align>");

      sb.append("<alt_text>").append(sTitle).append("</alt_text>");

      sb.append("<width>").append(sWidth).append("</width>");

      sb.append("<height>").append(sHeight).append("</height>");

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
    return false;
  }

  public void setup(UserInfoInterface userInfo, ProcessData procData, Properties props, ServletUtils response) {
    Logger.debug(userInfo.getUtilizador(),this,"setup","GRAFICOOOOOO");

    // Guardar PID, SUBPID, FLOWID e DATASOURCE
    // props.setProperty("datasource",sDATASOURCE);
    props.setProperty("flowid",String.valueOf(procData.getFlowId()));
    props.setProperty("pid",String.valueOf(procData.getPid()));
    props.setProperty("subpid",String.valueOf(procData.getSubPid()));

    FlowType flowType = BeanFactory.getFlowBean().getFlowType(userInfo, procData.getFlowId());
    
    if (FlowType.SEARCH.equals(flowType)) {
      props.setProperty(Const.FLOWEXECTYPE, "SEARCH");
    } else if (FlowType.REPORTS.equals(flowType)) {
      props.setProperty(Const.FLOWEXECTYPE, "REPORT");
    }

    // Alterar o titulo.
    String title = props.getProperty("chart_title");
    //if(null != title && title.startsWith("\"")) {
    if(title != null && procData.isTransformable(title, false)) {
      try {
        title = procData.transform(userInfo, title);
        props.setProperty("chart_title", title);
      } catch(Throwable t) {
        Logger.info(userInfo.getUtilizador(),this,"setup","Excepcao ao processar o grafico");
      }
    }
  }
  public void initVariable(UserInfoInterface userInfo, ProcessData procData, String name, Properties props) {
  }
}
