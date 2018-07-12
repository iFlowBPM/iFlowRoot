package pt.iflow.blocks.form;

import java.io.BufferedReader;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.FormProps;
import pt.iflow.api.datatypes.FormTableText;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.ServletUtils;
import pt.iflow.api.utils.UserInfoInterface;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class FixedTable implements FieldInterface {

  static final Pattern NEW_PROPERTY_PATTERN = Pattern.compile("^\\s*([a-zA-Z])(\\d+)\\s*\\{(.*)\\}$");
  static final Pattern OLD_PAREN_END_PATTERN = Pattern.compile("\\]\\s*$");
  static final Pattern PROPERTY_PATTERN = Pattern.compile("^\\s*(?:\\w+)_(\\d+)_(\\d+)\\{(.*)\\}$");
  public static final String PROP_Y = "Y";
  public static final String PROP_X = "X";
  public static final String PROP_TYPE = "type";
  public static final String PROP_PERMS = "permissions";
  public static final String PROP_ID = "id";
  public static final String PROP_CALL = "call";
  public static final String PROP_SIZE = "size";
  public static final String PROP_MAXLENGTH = "maxlength";
  public static final String PROP_CLASS = "class";
  public static final String PROP_ALIGN = "align";
  public static final String PROP_BGCOLOR = "bgcolor";

  
  public static final String TYPE_TEXT = "text";
  public static final String TYPE_INPUT = "input";

  public static final String PERM_READONLY = "ro";
  
  public static final String SEP = "#";


  
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
      int nDataRows = 0;
      
      StringBuffer sb = new StringBuffer();

      sb.append("<field><type>arraytable</type>");

      sb.append("<fieldid>").append(prop.getProperty("fieldid")).append("</fieldid>");

      sb.append("<print>false</print>");
      sb.append("<export>false</export>");

      List<String> alColNames = new ArrayList<String>();
      
      Map<Integer,Map<Integer,CellElement>> hmProps = parseProps(prop);

      Map<Integer,CellElement> hm = null;
      int nCols = 0;
      for (int i=1; true; i++) {
        
        if (!hmProps.containsKey(i)) break;

        hm = hmProps.get(i); 

        if (hm.size() > nCols) nCols = hm.size();
      }
      hm = null;  

      hm = hmProps.get(1);
      for (int i=1; i <= nCols; i++) {
        CellElement ce = hm.get(i);
        
        if (hm.containsKey(i)) {
          if (ce.sContent == null || ce.sContent.equals(""))
            alColNames.add(" ");
          else
            alColNames.add(ce.sContent);
        }
        else
          alColNames.add(" ");
      }
      
      
      if (alColNames.size() == 0)
        return null;


      // header row
      sb.append("<row>");
      for (int col=0; col < nCols; col++) {
        sb.append("<col>");
        sb.append("<header>true</header>");
        sb.append("<value>");
        sb.append((String)alColNames.get(col));
        sb.append("</value>");
        sb.append("<align>center</align>");
        sb.append("</col>");
      }
      sb.append("</row>");


      Set<String> hsCalls = new HashSet<String>();
      
      
      for (int row=2; row <= hmProps.size(); row++) {
        hm = hmProps.get(row);

        sb.append("<row>");

        for (int col=1; col <= nCols; col++) {
          
          CellElement ce = null;
          String sValue = ""; 
          
          if (hm.containsKey(col)) ce = (CellElement)hm.get(col);
          
          try {
			
		
          if (ce != null) {
            sValue = ce.sContent;
            
            if (ce.sType.equals(TYPE_INPUT)) {

              String sSize = ce.sSize;
              String sMaxLength = ce.sMaxLength;
              
              if (StringUtils.isEmpty(sSize)) sSize = "10";
              if (StringUtils.isEmpty(sMaxLength)) sMaxLength = "10";

              
              // TODO: submit_onblur support
              sValue = FormTableText.genXml(sValue, sSize, sMaxLength, ce.sCall, null, null, ce.sId, false, PERM_READONLY.equals(ce.sPerms));
                            
              // add an extra hidden variable to avoid javascript error

              if (PERM_READONLY.equals(ce.sPerms)) {
                sValue = sValue + "<hidden><name>" +ce.sId + "_HIDDEN</name><value/></hidden>";
              }
              else {
                hsCalls.add(ce.sCall);
              }
            }
          }
          } catch (Exception e) {
        	 
  		}
          
          
          if (StringUtils.isEmpty(sValue)) sValue = " ";
          
          String sAlign = "center";
          if (ce != null && StringUtils.isNotEmpty(ce.sAlign))
            sAlign = ce.sAlign;

          String sBgColor = "";
          if (StringUtils.isNotEmpty(ce.sBgColor) && ce != null)
            sBgColor = "<bgcolor>" + ce.sBgColor + "</bgcolor>";

          sb.append("<col>");
          sb.append("<header>false</header>");                   
          sb.append("<align>").append(sAlign).append("</align>");
          sb.append("<value>").append(sValue).append("</value>");
          sb.append(sBgColor);
          sb.append("</col>");
          
        }
        
        nDataRows++;
        

        sb.append("</row>");
      }

      // add properties with count of data rows and number of columns
      sb.append("<datarow_count>");
      sb.append(nDataRows);
      sb.append("</datarow_count>");

      sb.append("<column_count>");
      sb.append(alColNames.size());
      sb.append("</column_count>");

      sb.append("<even_field>").append(prop.getProperty("even_field")).append("</even_field>");

      
      if (hsCalls.size() > 0) {
        sb.append("<extra>");
        Iterator<String> it = hsCalls.iterator();
        while (it.hasNext()) {
          sb.append((String)it.next()).append(";");
        }
        sb.append("</extra>");
      }
      
      
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
  
  
  public static Map<String,String> parseFixedProps(String sProp) {
    Map<String,String> retObj = new HashMap<String, String>();
    Matcher m = NEW_PROPERTY_PATTERN.matcher(sProp);
    int nX, nY;
    if(m.matches()) {
      nX = m.group(1).toUpperCase().charAt(0)-'A'+1;
      nY = Integer.parseInt(m.group(2));
    } else {
      Matcher aux = OLD_PAREN_END_PATTERN.matcher(sProp);
      if(aux.find())
        sProp = sProp.replaceFirst("\\]\\s*$", "}").replaceFirst("\\[", "{"); // replace expression xxx_1_1[....] to xxx_1_1{....}
      m = PROPERTY_PATTERN.matcher(sProp);
      if(!m.matches()) return null;
      nY = Integer.parseInt(m.group(1));
      nX = Integer.parseInt(m.group(2));
    }
    String sSuffix = m.group(3);

    String [] tokens = sSuffix.split("(?<![\\\\])"+SEP); // match non escaped SEPs
    
    String sType = null;
    String sId = null;
    String sPerms = null;
    String sCall = null;
    String sClass = null;
    String sSize = null;
    String sMaxLength = null;
    String sAlign = null;
    String sBgColor = null;
    
    for (int i=0; tokens != null && i < tokens.length; i++) {
      if(StringUtils.isEmpty(tokens[i])) continue;
      String sExtra = tokens[i].replaceAll("[\\\\]"+SEP, SEP); // Replace escaped SEP with unescaped SEP
      String sName = sExtra;
      String sValue = sExtra;
      int pos = sExtra.indexOf('=');
      if(pos != -1) {
        sName = sExtra.substring(0, pos);
        sValue = sExtra.substring(pos+1);
      }
      
      if (sName.equals(PROP_TYPE)) {
        sType = sValue;
      }
      else if (sName.equals(PROP_ID)) {
        sId = sValue;          
      }
      else if (sName.equals(PROP_PERMS)) {
        sPerms = sValue;            
      }
      else if (sName.equals(PROP_CALL)) {
        sCall = sValue;            
      }
      else if (sName.equals(PROP_SIZE)) {
        sSize = sValue;            
      }
      else if (sName.equals(PROP_MAXLENGTH)) {
        sMaxLength = sValue;            
      }
      else if (sName.equals(PROP_CLASS)) {
        sClass = sValue;            
      }
      else if (sName.equals(PROP_ALIGN)) {
        sAlign = sValue;            
      }
      else if (sName.equals(PROP_BGCOLOR)) {
        sBgColor = sValue;            
      }
    }
    
    retObj.put(PROP_Y, String.valueOf(nY));
    retObj.put(PROP_X, String.valueOf(nX));
    retObj.put(PROP_TYPE, sType);
    retObj.put(PROP_ID, sId);
    retObj.put(PROP_PERMS, sPerms);
    retObj.put(PROP_CALL, sCall);
    retObj.put(PROP_SIZE, sSize);
    retObj.put(PROP_MAXLENGTH, sMaxLength);
    retObj.put(PROP_CLASS, sClass);
    if (StringUtils.isNotEmpty(sAlign))
      retObj.put(PROP_ALIGN, sAlign);
    if (StringUtils.isNotEmpty(sBgColor))
      retObj.put(PROP_BGCOLOR, sBgColor);
    
    return retObj;
  }
  
  
  
  private Map<Integer, Map<Integer,CellElement>> parseProps(Properties pBlockProps) {
    Map<Integer,Map<Integer,CellElement>> retObj = new HashMap<Integer, Map<Integer,CellElement>>();

    try {
      Enumeration<?> enu = pBlockProps.keys();
      while (enu.hasMoreElements()) {
        String sProp = (String)enu.nextElement();

        Map<String,String> hmFixedProps = parseFixedProps(sProp);
        
        if (hmFixedProps == null) continue;
        
        
        CellElement ce = new CellElement();

        int sY = Integer.parseInt(hmFixedProps.get(PROP_Y));
        int sX = Integer.parseInt(hmFixedProps.get(PROP_X));

        ce.sContent = pBlockProps.getProperty(sProp);
        ce.nY = sY;
        ce.nX = sX;
        
        ce.sType = hmFixedProps.get(PROP_TYPE);
        ce.sId = hmFixedProps.get(PROP_ID);
        ce.sPerms = hmFixedProps.get(PROP_PERMS);
        ce.sCall = hmFixedProps.get(PROP_CALL);
        ce.sSize = hmFixedProps.get(PROP_SIZE);
        ce.sMaxLength = hmFixedProps.get(PROP_MAXLENGTH);
        ce.sClass = hmFixedProps.get(PROP_CLASS);
        ce.sAlign = hmFixedProps.get(PROP_ALIGN);
        ce.sBgColor = hmFixedProps.get(PROP_BGCOLOR);
        
        Map<Integer,CellElement> hmCols = new HashMap<Integer, CellElement>();
        if (retObj.containsKey(ce.nY)) {
          hmCols = retObj.get(ce.nY);
        }
        
        hmCols.put(ce.nX, ce);
        
        retObj.put(ce.nY, hmCols);
      } 
    }
    catch (Exception e){}
    
    return retObj;
  }
  
  private class CellElement {
    public int nX;
    public int nY;
    public String sContent;
    public String sType;
    public String sPerms;
    public String sId;
    public String sCall;
    public String sSize;
    public String sMaxLength;
    @SuppressWarnings("unused")
    public String sClass;
    public String sAlign;
    public String sBgColor;
    
    
    public String toString() {
      StringBuffer retObj = new StringBuffer();
      
      retObj.append("[").append(nY).append("-").append(nX).append("](");
      retObj.append(sType).append("-").append(sId).append("-").append(sPerms).append("-").append(sContent).append("-").append(sCall).append(")");
      
      return retObj.toString();
    }
  }

  public void setup(UserInfoInterface userInfo, ProcessData procData, Properties props, ServletUtils response) {
    String sTextArea = props.getProperty(FormProps.sTEXT_AREA);

    BufferedReader br = new BufferedReader(new StringReader(sTextArea));

    try {
      String sLine = null;
      while ((sLine = br.readLine()) != null) {
        sLine = sLine.trim();
        if(StringUtils.isEmpty(sLine)) continue;
        if(sLine.startsWith("#")) continue;

        int pos = sLine.lastIndexOf('=');
        String sProp = sLine;
        String sValue = "";
        if(pos != -1) {
          sProp = sLine.substring(0, pos).trim();
          sValue = sLine.substring(pos + 1).trim();
        }

        if (StringUtils.isNotEmpty(sValue)) {
          try {
            sValue = procData.transform(userInfo, sValue);
          }
          catch (Exception ei) {
            sValue = null;
          }
          
          if (sValue == null) sValue = "";
        } else {
          sValue = "";
        }

        props.put(sProp, sValue);
      }
    }
    catch (Exception e) {
      Logger.error(userInfo.getUtilizador(), this, "setup", 
          procData.getSignature() + "exception caught", e);
    }
  }

  public boolean isArrayTable() {
    return false;
  }
  public void initVariable(UserInfoInterface userInfo, ProcessData procData, String name, Properties props) {
  }
}
