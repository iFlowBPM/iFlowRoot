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
    .append(StringEscapeUtils.escapeXml(error))
    .append("</").append(ELEMENT_ERROR).append(">").append(newLine());
  }

  StringBuilder generateXmlSimpleVar(ProcessSimpleVariable sv, StringBuilder sb) {
    if(sv == null || sv.getType() == null || sv.getType().isBindable()) {
      return sb;
    }

    String rawValue = sv.getRawValue();
    if(null == rawValue) return sb;

    return sb.append("<").append(ELEMENT_VAR).append(" ").append(ATTR_NAME).append("=\"")
    .append(sv.getName()).append("\">").append(StringEscapeUtils.escapeXml(sv.getRawValue()))
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
    .append(item.getPosition()).append("\">").append(StringEscapeUtils.escapeXml(rawValue))
    .append("</").append(ELEMENT_LISTITEM).append(">").append(newLine());
  }

  StringBuilder generateXmlAppData(String name, String value, StringBuilder sb) {
    if(name == null) return sb;
    
    // XXX Verificar se este precisa disto ou nao
    if(null == value) return sb;
    return sb.append("<").append(ELEMENT_APPDATA).append(" ").append(ATTR_NAME).append("=\"")
    .append(StringEscapeUtils.escapeXml(name)).append("\">").append(StringEscapeUtils.escapeXml(value))
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
