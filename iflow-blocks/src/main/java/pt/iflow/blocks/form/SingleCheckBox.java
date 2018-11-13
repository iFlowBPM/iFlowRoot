package pt.iflow.blocks.form;

import java.io.PrintStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.FormProps;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessListVariable;
import pt.iflow.api.utils.ServletUtils;
import pt.iflow.api.utils.UserInfoInterface;

public class SingleCheckBox extends Selection {

  private static final String TAG_OPTION_ORDER = "optionorder_";
  private static final String TAG_OPTION_VALUE = "option_";
  private static final String TAG_OPTION_TEXT = "text_";
  
  public String getDescription() {
    return "SingleCheckBox";
  }

  public void generateHTML(PrintStream out, Properties prop) {
    // todo
  }

  public void generateXSL(PrintStream out) {
    // todo
  }

  public void generateXML(PrintStream out, Properties prop) {
    // todo
  }

  public String getXML(Properties prop) {
    try {
      StringBuffer sb = new StringBuffer();
      StringBuffer sbHidden = new StringBuffer();
      String sValue = StringEscapeUtils.unescapeXml(prop.getProperty("value"));
      String sName = null;
      String sValue2 = null;
      String sVar = null;

      double dtmp = Double.NaN;

      try {
        dtmp = Double.parseDouble(sValue);
      }
      catch (Exception e) {
        dtmp = Double.NaN;
      }

      String stmp = "";      

      sVar = prop.getProperty("variable");
      sb.append("<field><type>singlecheckbox</type>");
      sb.append("<obligatory>").append(prop.getProperty(FormProps.sOBLIGATORY_PROP)).append("</obligatory>");
      sb.append("<text>").append(prop.getProperty("text")).append("</text>");
      sb.append("<variable>").append(sVar).append("</variable>");
      sb.append("<output_only>").append(prop.getProperty("output_only")).append("</output_only>");

      sb.append("<onchange_submit>").append(prop.getProperty("onchange_submit")).append("</onchange_submit>");
      sb.append("<value>").append(StringEscapeUtils.escapeXml(prop.getProperty("value"))).append("</value>");      

      sb.append("<even_field>").append(prop.getProperty("even_field")).append("</even_field>");

      sb.append("</field>");

      return sbHidden.toString() + sb.toString();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public boolean isOutputField() {
    return false;
  }

  public void setup(UserInfoInterface oUserInfo, ProcessData procData, Properties prop, ServletUtils response) {

    // default value
    String value = prop.getProperty(FormProps.sDEFAULT_VALUE);
    String text = prop.getProperty(FormProps.sDEFAULT_TEXT);

    value = value != null ? value : "";
    text = text != null ? text : "";

    List<String> names = new ArrayList<String>();
    List<String> descs = new ArrayList<String>();

    if (StringUtils.isNotEmpty(value) ||
        StringUtils.isNotEmpty(text)) {
      names.add(text);
      descs.add(value);
    }
    
    for (int i=0; prop.getProperty(i + "_text") != null; i++) {
      String sName = prop.getProperty(i + "_text");
      String sValue = prop.getProperty(i + "_value");

      String [] asNames = null;
      String [] asValues = null;

      if(null == sName || null == sValue) continue;

      boolean sNameArray = false;
      if(sName.startsWith("$")) {
        String var = sName.substring(1);
        if(procData.isListVar(var)) {
          sNameArray = true;
          ProcessListVariable list = procData.getList(var);
          asNames = new String[list.size()];
          for(int kk = 0; kk < asNames.length; kk++)
            asNames[kk] = list.getFormattedItem(kk);
        } else {
          sName = procData.getFormatted(var);
        }
      }

      boolean sValueArray = false;
      if(sValue.startsWith("$")) {
        String var = sValue.substring(1);
        if(procData.isListVar(var)) {
          sValueArray = true;
          ProcessListVariable list = procData.getList(var);
          asValues = new String[list.size()];
          for(int kk = 0; kk < asValues.length; kk++)
            asValues[kk] = list.getFormattedItem(kk);
        } else {
          sValue = procData.getFormatted(var);
        }
      }

      if(sValueArray && sNameArray) {
        int size = asNames.length > asValues.length? asValues.length: asNames.length;
        for(int j = 0; j < size; j++) {
          names.add(asNames[j]);
          descs.add(asValues[j]);
        }
      } else {
        names.add(sName);
        descs.add(sValue);
      }
    }

    // cleanup
    for (int i=0; prop.getProperty(i + "_text") != null; i++) {
      prop.remove(i + "_text");
      prop.remove(i + "_value");
    }
    
    // fill me up scotty
    int sizeVals = names.size();
    for(int i = 0; i < sizeVals; i++) {
      String optionValue = descs.get(i);
      String optionText = names.get(i);
      
      prop.setProperty(TAG_OPTION_ORDER + i, optionValue);
      prop.setProperty(TAG_OPTION_VALUE + optionValue, optionValue);
      prop.setProperty(TAG_OPTION_TEXT + optionValue, optionText);
    }
    if (sizeVals == 1) {
      // if only one option available, "select" it (set in procdata)
      // makes it possible to have output only selections that are set dynamically
      // by other selections (e.g: sqlselection that only has one option depending on another select
      // that submits on change)
      String optionText = names.get(0);
      String optionValue = descs.get(0);
      String var = prop.getProperty(FormProps.sVARIABLE);
      if (StringUtils.isNotEmpty(var)) {
        try {
          procData.parseAndSet(var, optionValue);
          procData.parseAndSet(var + FormProps.sLIST_TEXT_SUFFIX, optionText);
        }
        catch (ParseException pe) {
          // ignore
        }
      }
    }
  }

  public boolean isArrayTable() {
    return false;
  }
  public void initVariable(UserInfoInterface userInfo, ProcessData procData, String name, Properties props) {
  }
}
