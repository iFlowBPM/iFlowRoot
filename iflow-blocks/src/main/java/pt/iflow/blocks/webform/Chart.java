package pt.iflow.blocks.webform;

import java.net.URLEncoder;
import java.util.Map;

import org.xml.sax.SAXException;

import pt.iflow.api.blocks.form.Field;
import pt.iflow.api.blocks.form.ParserContext;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.flows.FlowType;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iknow.utils.html.FormData;

public class Chart extends AbstractWidget {
  private static final String sOPT_DATASOURCE = "datasource";
  private static final String sOPT_FLOWID = "flowid";
  private static final String sOPT_PID = "pid";
  private static final String sOPT_SUBPID = "subpid";

  public void generate(UserInfoInterface userInfo, ProcessData procData, ParserContext ch, Field field, boolean even) throws SAXException {
    Logger.info(userInfo.getUtilizador(), this, "generate", "Generating a Chart");
    Map<String,String> props = field.getProperties();
    StringBuilder url = new StringBuilder();
    String encTitle = props.get("title");
    
    try { 
      encTitle = URLEncoder.encode(encTitle, "UTF-8");
    } catch (Throwable t) {
      Logger.warning(userInfo.getUtilizador(), this, "generate", "Could not encode chart title as UTF-8 text");
    }
    FlowType flowType = BeanFactory.getFlowBean().getFlowType(userInfo, procData.getFlowId());
    
    url.append("Chart?")
    .append("chart.title=").append(encTitle)
    .append("&width=").append(props.get("width"))
    .append("&height=").append(props.get("height"))
    .append("&template=").append(props.get("template"))
    .append("&dsl=").append(props.get("dsl"))
    .append("&dsv=").append(props.get("dsv"))
    .append("&"+sOPT_DATASOURCE+"=").append("process")
    .append("&"+sOPT_FLOWID+"=").append(procData.getFlowId())
    .append("&"+sOPT_PID+"=").append(procData.getPid())
    .append("&"+sOPT_SUBPID+"=").append(procData.getSubPid())
    .append(FlowType.SEARCH.equals(flowType) ? "&" + Const.FLOWEXECTYPE + "=SEARCH" : 
                  (FlowType.REPORTS.equals(flowType) ? "&" + Const.FLOWEXECTYPE + "=REPORT" : ""));
    
    ch.startElement("field");
    ch.addElement("type", "image");
    ch.addElement("url", url.toString());
    ch.addElement("align", props.get("align"));
    ch.addElement("alt_text", props.get("alt_text"));
    ch.addElement("width", props.get("width"));
    ch.addElement("height", props.get("height"));
    ch.addElement("even_field", String.valueOf(even));
    ch.endElement("field");

  }

  public void process(UserInfoInterface userInfo, ProcessData process, Field field, FormData request) {
  }

}
