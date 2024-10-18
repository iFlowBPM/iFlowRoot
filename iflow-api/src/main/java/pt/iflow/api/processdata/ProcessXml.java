package pt.iflow.api.processdata;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.TimeZone;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.xml.serializer.ToXMLStream;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import pt.iflow.api.core.ProcessCatalogue;
import pt.iflow.api.utils.Logger;

public class ProcessXml {

  public static final SimpleDateFormat DateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

  private enum WriteMode {
    ONE_LINE(""), @SuppressWarnings("unused")
    MULTI_LINE(System.getProperty("line.separator"));
    private final String separator;

    private WriteMode(String separator) {
      this.separator = separator;
    }

    public String getSeparator() {
      return this.separator;
    }
  }; 
  private WriteMode _mode = WriteMode.ONE_LINE;

  static final String ATTR_FLOWID = "fid";
  static final String ATTR_PID = "pid";
  static final String ATTR_SUBPID = "spid";
  static final String ATTR_PNUMBER = "pnumber";
  static final String ATTR_CREATOR = "creator";
  static final String ATTR_CREATION_DATE = "creationDate";
  static final String ATTR_CURRENT_USER = "currentUser";
  static final String ATTR_LAST_UPDATE = "lastUpdate";
  static final String ATTR_CLOSED = "closed";
  static final String ATTR_NAME = "n";
  static final String ATTR_SIZE = "s";
  static final String ATTR_POSITION = "p";

  static final String ELEMENT_PROCESSDATA = "processdata";
  static final String ELEMENT_ERROR = "e";
  static final String ELEMENT_VAR = "a";
  static final String ELEMENT_LISTVAR = "l";
  static final String ELEMENT_LISTITEM = "i";
  static final String ELEMENT_APPDATA = "d";

  static final String NS_PROCESSDATA = "http://www.iflow.pt/ProcessData";
  static final String NS_SCHEMA = "http://www.w3.org/2001/XMLSchema";
  static final String NS_SCHEMA_INSTANCE = "http://www.w3.org/2001/XMLSchema-instance";

  private ProcessData _pd;

  static {
    DateFormatter.setTimeZone(TimeZone.getDefault());
  }

  public ProcessXml(ProcessCatalogue catalogue, Reader xmlCharStream) {
    long start = System.currentTimeMillis();
    ProcessXmlReader reader = new ProcessXmlReader(catalogue, xmlCharStream);

    //    init(reader.read());
    init(reader.readSAX());

    long end = System.currentTimeMillis();
    Logger.trace("ProcessXml","<init>","Process parsed in "+(end-start)+" ms");
  }

  public ProcessXml(ProcessData pd) {
    init(pd);
  }

  void init(ProcessData pd) {
    _pd = pd;
  }

  private String customEscapeXml(String input) {

    StringBuilder escaped = new StringBuilder();

    for (char c : input.toCharArray()) {
      escaped.append(StringEscapeUtils.escapeXml(String.valueOf(c)));
    }

    // Replace additional entities
    String escapedStr = escaped.toString();

    String[] arr1 = {
            "&nbsp;", "&iexcl;", "&cent;", "&pound;", "&curren;", "&yen;", "&brvbar;", "&sect;", "&uml;", "&copy;", "&ordf;", "&laquo;", "&not;", "&shy;", "&reg;", "&macr;", "&deg;", "&plusmn;", "&sup2;", "&sup3;", "&acute;", "&micro;", "&para;", "&middot;", "&cedil;", "&sup1;", "&ordm;", "&raquo;", "&frac14;", "&frac12;", "&frac34;", "&iquest;", "&Agrave;", "&Aacute;", "&Acirc;", "&Atilde;", "&Auml;", "&Aring;", "&AElig;", "&Ccedil;", "&Egrave;", "&Eacute;", "&Ecirc;", "&Euml;", "&Igrave;", "&Iacute;", "&Icirc;", "&Iuml;", "&ETH;", "&Ntilde;", "&Ograve;", "&Oacute;", "&Ocirc;", "&Otilde;", "&Ouml;", "&times;", "&Oslash;", "&Ugrave;", "&Uacute;", "&Ucirc;", "&Uuml;", "&Yacute;", "&THORN;", "&szlig;", "&agrave;", "&aacute;", "&acirc;", "&atilde;", "&auml;", "&aring;", "&aelig;", "&ccedil;", "&egrave;", "&eacute;", "&ecirc;", "&euml;", "&igrave;", "&iacute;", "&icirc;", "&iuml;", "&eth;", "&ntilde;", "&ograve;", "&oacute;", "&ocirc;", "&otilde;", "&ouml;", "&divide;", "&oslash;", "&ugrave;", "&uacute;", "&ucirc;", "&uuml;", "&yacute;", "&thorn;", "&yuml;", "&quot;", "&amp;", "&lt;", "&gt;", "&OElig;", "&oelig;", "&Scaron;", "&scaron;", "&Yuml;", "&circ;", "&tilde;", "&ensp;", "&emsp;", "&thinsp;", "&zwnj;", "&zwj;", "&lrm;", "&rlm;", "&ndash;", "&mdash;", "&lsquo;", "&rsquo;", "&sbquo;", "&ldquo;", "&rdquo;", "&bdquo;", "&dagger;", "&Dagger;", "&permil;", "&lsaquo;", "&rsaquo;", "&euro;", "&fnof;", "&Alpha;", "&Beta;", "&Gamma;", "&Delta;", "&Epsilon;", "&Zeta;", "&Eta;", "&Theta;", "&Iota;", "&Kappa;", "&Lambda;", "&Mu;", "&Nu;", "&Xi;", "&Omicron;", "&Pi;", "&Rho;", "&Sigma;", "&Tau;", "&Upsilon;", "&Phi;", "&Chi;", "&Psi;", "&Omega;", "&alpha;", "&beta;", "&gamma;", "&delta;", "&epsilon;", "&zeta;", "&eta;", "&theta;", "&iota;", "&kappa;", "&lambda;", "&mu;", "&nu;", "&xi;", "&omicron;", "&pi;", "&rho;", "&sigmaf;", "&sigma;", "&tau;", "&upsilon;", "&phi;", "&chi;", "&psi;", "&omega;", "&thetasym;", "&upsih;", "&piv;", "&bull;", "&hellip;", "&prime;", "&Prime;", "&oline;", "&frasl;", "&weierp;", "&image;", "&real;", "&trade;", "&alefsym;", "&larr;", "&uarr;", "&rarr;", "&darr;", "&harr;", "&crarr;", "&lArr;", "&uArr;", "&rArr;", "&dArr;", "&hArr;", "&forall;", "&part;", "&exist;", "&empty;", "&nabla;", "&isin;", "&notin;", "&ni;", "&prod;", "&sum;", "&minus;", "&lowast;", "&radic;", "&prop;", "&infin;", "&ang;", "&and;", "&or;", "&cap;", "&cup;", "&int;", "&there4;", "&sim;", "&cong;", "&asymp;", "&ne;", "&equiv;", "&le;", "&ge;", "&sub;", "&sup;", "&nsub;", "&sube;", "&supe;", "&oplus;", "&otimes;", "&perp;", "&sdot;", "&lceil;", "&rceil;", "&lfloor;", "&rfloor;", "&lang;", "&rang;", "&loz;", "&spades;", "&clubs;", "&hearts;", "&diams;", "&Aring;", "&Iuml;", "&THORN;", "&oslash;"
    };

    String[] arr2 = {
            "&#160;", "&#161;", "&#162;", "&#163;", "&#164;", "&#165;", "&#166;", "&#167;", "&#168;", "&#169;", "&#170;", "&#171;", "&#172;", "&#173;", "&#174;", "&#175;", "&#176;", "&#177;", "&#178;", "&#179;", "&#180;", "&#181;", "&#182;", "&#183;", "&#184;", "&#185;", "&#186;", "&#187;", "&#188;", "&#189;", "&#190;", "&#191;", "&#192;", "&#193;", "&#194;", "&#195;", "&#196;", "&#197;", "&#198;", "&#199;", "&#200;", "&#201;", "&#202;", "&#203;", "&#204;", "&#205;", "&#206;", "&#207;", "&#208;", "&#209;", "&#210;", "&#211;", "&#212;", "&#213;", "&#214;", "&#215;", "&#216;", "&#217;", "&#218;", "&#219;", "&#220;", "&#221;", "&#222;", "&#223;", "&#224;", "&#225;", "&#226;", "&#227;", "&#228;", "&#229;", "&#230;", "&#231;", "&#232;", "&#233;", "&#234;", "&#235;", "&#236;", "&#237;", "&#238;", "&#239;", "&#240;", "&#241;", "&#242;", "&#243;", "&#244;", "&#245;", "&#246;", "&#247;", "&#248;", "&#249;", "&#250;", "&#251;", "&#252;", "&#253;", "&#254;", "&#255;", "&#34;", "&#38;", "&#60;", "&#62;", "&#338;", "&#339;", "&#352;", "&#353;", "&#376;", "&#710;", "&#732;", "&#8194;", "&#8195;", "&#8201;", "&#8204;", "&#8205;", "&#8206;", "&#8207;", "&#8211;", "&#8212;", "&#8216;", "&#8217;", "&#8218;", "&#8220;", "&#8221;", "&#8222;", "&#8224;", "&#8225;", "&#8240;", "&#8249;", "&#8250;", "&#8364;", "&#402;", "&#913;", "&#914;", "&#915;", "&#916;", "&#917;", "&#918;", "&#919;", "&#920;", "&#921;", "&#922;", "&#923;", "&#924;", "&#925;", "&#926;", "&#927;", "&#928;", "&#929;", "&#931;", "&#932;", "&#933;", "&#934;", "&#935;", "&#936;", "&#937;", "&#945;", "&#946;", "&#947;", "&#948;", "&#949;", "&#950;", "&#951;", "&#952;", "&#953;", "&#954;", "&#955;", "&#956;", "&#957;", "&#958;", "&#959;", "&#960;", "&#961;", "&#962;", "&#963;", "&#964;", "&#965;", "&#966;", "&#967;", "&#968;", "&#969;", "&#977;", "&#978;", "&#982;", "&#8226;", "&#8230;", "&#8242;", "&#8243;", "&#8254;", "&#8260;", "&#8472;", "&#8465;", "&#8476;", "&#8482;", "&#8501;", "&#8592;", "&#8593;", "&#8594;", "&#8595;", "&#8596;", "&#8629;", "&#8656;", "&#8657;", "&#8658;", "&#8659;", "&#8660;", "&#8704;", "&#8706;", "&#8707;", "&#8709;", "&#8711;", "&#8712;", "&#8713;", "&#8715;", "&#8719;", "&#8721;", "&#8722;", "&#8727;", "&#8730;", "&#8733;", "&#8734;", "&#8736;", "&#8743;", "&#8744;", "&#8745;", "&#8746;", "&#8747;", "&#8756;", "&#8764;", "&#8773;", "&#8776;", "&#8800;", "&#8801;", "&#8804;", "&#8805;", "&#8834;", "&#8835;", "&#8836;", "&#8838;", "&#8839;", "&#8853;", "&#8855;", "&#8869;", "&#8901;", "&#8968;", "&#8969;", "&#8970;", "&#8971;", "&#9001;", "&#9002;", "&#9674;", "&#9824;", "&#9827;", "&#9829;", "&#9830;", "&#197;", "&#207;", "&#222;", "&#248;"
    };

    for (int i = 0; i < arr1.length; i++) {
      escapedStr = escapedStr.replace(arr1[i], arr2[i]);
    }

    // Every other not identified character is replaced by a dot, to avoid exceptions in the XML parser
    escapedStr = escapedStr.replaceAll("&[A-Za-z]+;", ".");

    return escapedStr;
  }

  public ProcessData getProcessData() {
    return _pd;
  }

  public ProcessXmlWriter getWriter() {
    return new ProcessXmlWriter(_pd);
  }

  public String getXml() {
    String xml = getXmlSB();
    return xml;
  }
  
  String getXmlSAX() {
    // FIXME este ainda deve ter uns erros algures....
    ProcessSAXReader saxReader = new ProcessSAXReader(_pd);
    
    StringWriter sw = new StringWriter(2*1024);
    ToXMLStream toStream = new ToXMLStream();
    toStream.setWriter(sw);
    toStream.setEncoding("UTF-8");
    saxReader.setContentHandler(toStream);
    try {
      saxReader.parse(new InputSource(ProcessXml.NS_PROCESSDATA));
    } catch (IOException e) {
      Logger.error(null, "ProcessXml","getXmlSAX","Unexpected IOException", e);
    } catch (SAXException e) {
      Logger.error(null, "ProcessXml","getXmlSAX","Unexpected SAXException", e);
    }
    return sw.toString();
  }
  
  String getXmlSB() {
    StringBuilder sb = new StringBuilder();

    generateXmlHeader(sb);
    
    if (_pd.hasError()) {
      generateXmlError(_pd.getError(), sb);
    }
    
    for (ProcessSimpleVariable sv : _pd.getSimpleVariables())
      generateXmlSimpleVar(sv, sb);

    for (ProcessListVariable lv : _pd.getListVariables()) 
      generateXmlListVar(lv, sb);

    for(String name : _pd._appData.keySet())
      generateXmlAppData(name, _pd._appData.get(name), sb);


    generateXmlFooter(sb);
    
    return sb.toString();
  }

  StringBuilder generateXmlHeader(StringBuilder sb) {
    sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append(newLine());
    sb.append("<").append(ELEMENT_PROCESSDATA);
    sb.append(" ").append(ATTR_FLOWID).append("=\"").append(_pd.getFlowId()).append("\"");
    sb.append(" ").append(ATTR_PID).append("=\"").append(_pd.getPid()).append("\"");
    sb.append(" ").append(ATTR_SUBPID).append("=\"").append(_pd.getSubPid()).append("\"");
    sb.append(" ").append(ATTR_PNUMBER).append("=\"").append(_pd.getPNumber()).append("\"");

    sb.append(" ").append(ATTR_CREATOR).append("=\"").append(_pd.getCreator()).append("\"");
    sb.append(" ").append(ATTR_CREATION_DATE).append("=\"").append(DateFormatter.format(_pd.getCreationDate())).append("\"");
    sb.append(" ").append(ATTR_CURRENT_USER).append("=\"").append(_pd.getCurrentUser()).append("\"");
    sb.append(" ").append(ATTR_LAST_UPDATE).append("=\"").append(DateFormatter.format(_pd.getLastUpdate())).append("\"");
    sb.append(" ").append(ATTR_CLOSED).append("=\"").append((_pd.isClosed() ? "1" : "0")).append("\"");

    sb.append(" xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'");
    sb.append(" xmlns='").append(NS_PROCESSDATA).append("'");
    sb.append(" xsi:schemaLocation='").append(NS_PROCESSDATA).append(" processdata.xsd'>").append(newLine());

    return sb;
  }

  StringBuilder generateXmlError(String error, StringBuilder sb) {
    if(error == null) return sb;
    return sb.append("<").append(ELEMENT_ERROR).append(">")
    .append(customEscapeXml(error))
    .append("</").append(ELEMENT_ERROR).append(">").append(newLine());
  }

  StringBuilder generateXmlSimpleVar(ProcessSimpleVariable sv, StringBuilder sb) {
    if(sv == null || sv.getType() == null || sv.getType().isBindable()) {
      return sb;
    }

    String rawValue = sv.getRawValue();
    if(null == rawValue) return sb;

    return sb.append("<").append(ELEMENT_VAR).append(" ").append(ATTR_NAME).append("=\"")
    .append(sv.getName()).append("\">").append(customEscapeXml(sv.getRawValue()))
    .append("</").append(ELEMENT_VAR).append(">").append(newLine());
  }

  StringBuilder generateXmlListVar(ProcessListVariable lv, StringBuilder sb) {
    if(lv == null || lv.getType() == null|| lv.getType().isBindable()) {
      return sb;
    }

    sb.append("<").append(ELEMENT_LISTVAR).append(" ");
    sb.append(ATTR_NAME).append("=").append("\"").append(lv.getName()).append("\" ");
    sb.append(ATTR_SIZE).append("=").append("\"").append(lv.size()).append("\">").append(newLine());

    ListIterator<ProcessListItem> items = lv.getItemIterator();
    while(items.hasNext()) {
      ProcessListItem item = items.next();
      if (item == null)
        continue;
      generateXmlListItem(item, sb);
    }

    sb.append("</").append(ELEMENT_LISTVAR).append(">").append(newLine());

    return sb;
  }

  StringBuilder generateXmlListItem(ProcessListItem item, StringBuilder sb) {
    if(item == null) return sb;
    String rawValue = item.getRawValue();
    if(null == rawValue) return sb;
    
    return sb.append("<").append(ELEMENT_LISTITEM).append(" ").append(ATTR_POSITION).append("=\"")
    .append(item.getPosition()).append("\">").append(customEscapeXml(rawValue))
    .append("</").append(ELEMENT_LISTITEM).append(">").append(newLine());
  }

  StringBuilder generateXmlAppData(String name, String value, StringBuilder sb) {
    if(name == null) return sb;
    
    // XXX Verificar se este precisa disto ou nao
    if(null == value) return sb;
    return sb.append("<").append(ELEMENT_APPDATA).append(" ").append(ATTR_NAME).append("=\"")
    .append(customEscapeXml(name)).append("\">").append(customEscapeXml(value))
    .append("</").append(ELEMENT_APPDATA).append(">").append(newLine());
  }

  StringBuilder generateXmlFooter(StringBuilder sb) {
    return sb.append("</").append(ELEMENT_PROCESSDATA).append(">").append(newLine());
  }

  String newLine() {
    return _mode.getSeparator(); 
  }

  public boolean validateXml() {
    return validateXml(new InputSource(getWriter()));
  }

  public static boolean validateXml(String xml) {
    return validateXml(new InputSource(new StringReader(xml)));
  }

  public static boolean validateXml(InputStream xmlStream) {
    return validateXml(new InputSource(xmlStream));
  }

  public static boolean validateXml(Reader xmlStream) {
    return validateXml(new InputSource(xmlStream));
  }

  private static boolean validateXml(InputSource xmlSource) {
    try {
      // get validation driver:
      SchemaFactory factory = SchemaFactory.newInstance(NS_SCHEMA);

      // create schema by reading it from an XSD file:
      // XXX - xsd location !!
      Schema schema = factory.newSchema(new StreamSource(ProcessXml.class.getResourceAsStream("/processdata.xsd")));
      // Schema schema = factory.newSchema(new StreamSource(new FileInputStream("schemas/processdata.xsd")));
      Validator validator = schema.newValidator();

      // at last perform validation:
      validator.validate(new SAXSource(xmlSource));

    }catch (SAXException ex) {
      ex.printStackTrace();
      return false;
    } catch (Exception ex) {
      ex.printStackTrace();
      return false;
    }
    return true;
  }


  public static String evaluateXPath(InputSource xmlSource, String xpathExpression) {
    return (String)evaluateXPath(xmlSource, xpathExpression, null);
  }

  public static Object evaluateXPath(InputSource xmlSource, String xpathExpression, QName returnType) {
    try {
      // XPath will build a DOM document with namespaces. We create one without namespaces
      DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
      docFactory.setNamespaceAware(false);// ignore prefixes
      docFactory.setValidating(false); // prevent boring errors
      DocumentBuilder docBuiler = docFactory.newDocumentBuilder();
      Document doc = docBuiler.parse(xmlSource);

      XPathFactory factory = javax.xml.xpath.XPathFactory.newInstance();
      XPath xpath = factory.newXPath();
      xpath.setNamespaceContext(new iFlowNamespaceContext());

      XPathExpression expression = xpath.compile(xpathExpression);

      if (returnType == null)
        return expression.evaluate(doc);
      else
        return expression.evaluate(doc, returnType);
    } 
    catch (Exception e) {
      e.printStackTrace();
    }        
    return null;
  }
}


class iFlowNamespaceContext implements NamespaceContext {

  public String getNamespaceURI(String prefix) {
    // As nossas queries não sao prefixadas.
    if(StringUtils.isEmpty(prefix)) return ProcessXml.NS_PROCESSDATA;
    if(StringUtils.equals("if",prefix)) return ProcessXml.NS_PROCESSDATA;
    return XMLConstants.NULL_NS_URI;
  }

  // This method isn't necessary for XPath processing.
  public String getPrefix(String uri) {
    return null;
  }

  // This method isn't necessary for XPath processing either.
  @SuppressWarnings("unchecked")
  public Iterator getPrefixes(String uri) {
    @SuppressWarnings("unused")
    String ss = ProcessXml.NS_PROCESSDATA;
    throw new UnsupportedOperationException();
  }

}
