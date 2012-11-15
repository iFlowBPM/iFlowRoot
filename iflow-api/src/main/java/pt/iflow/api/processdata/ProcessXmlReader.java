package pt.iflow.api.processdata;

import java.io.InputStream;
import java.io.Reader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import pt.iflow.api.core.ProcessCatalogue;
import pt.iflow.api.processtype.ProcessDataType;
import pt.iflow.api.utils.Logger;

public class ProcessXmlReader {

  InputSource _streamSource;
  ProcessCatalogue _catalogue;

  public ProcessXmlReader(ProcessCatalogue catalogue, InputStream xmlStream) {
    this(catalogue, new InputSource(xmlStream));
  }

  public ProcessXmlReader(ProcessCatalogue catalogue, Reader xmlCharStream) {
    this(catalogue, new InputSource(xmlCharStream));
  }

  public ProcessXmlReader(ProcessCatalogue catalogue, InputSource streamSource) {
    _catalogue = catalogue;
    _streamSource = streamSource;
  }

  public ProcessData readSAX() {
    long start = System.currentTimeMillis();
    ProcessData pd = null;
    try {
      SAXParserFactory factory = SAXParserFactory.newInstance();
      factory.setNamespaceAware(false);
      factory.setValidating(false);
      // enable and set validator?
      // factory.setValidating(true);
      // // get validation driver:
      // SchemaFactory schemaFactory = SchemaFactory.newInstance(ProcessXml.NS_SCHEMA);
      // // create schema by reading it from an XSD file:
      // Schema schema = schemaFactory.newSchema(new StreamSource(ProcessXml.class.getResourceAsStream("/processdata.xsd")));
      // factory.setSchema(schema);
      SAXParser saxParser = factory.newSAXParser();
      
      start = System.currentTimeMillis();
      ProcessContentHandler h = new ProcessContentHandler(this._catalogue, saxParser.isNamespaceAware());
      saxParser.parse(_streamSource, h);
      pd = h.getProcessData();
      h = null;
    } catch (Throwable t) {
      Logger.error("ADMIN", this, "readSAX","Error reading process XML", t);
    }
    long end = System.currentTimeMillis();
    Logger.debug("ADMIN", this, "readSAX","SAX parsing finished in "+(end-start)+" ms");

    return pd;
  }
  
  public ProcessData read() {
    long start = System.currentTimeMillis();
    ProcessData pd = null;
    try {

      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      dbf.setNamespaceAware(false);
      DocumentBuilder db = dbf.newDocumentBuilder();
      start = System.currentTimeMillis();
      Document doc = db.parse(_streamSource);

      Element root = doc.getDocumentElement();	        
      root.normalize ();

      pd = new ProcessData(_catalogue, new ProcessHeader(root));             

      NodeList errors = doc.getElementsByTagName(ProcessXml.ELEMENT_ERROR);
      if (errors != null && errors.getLength() > 0) {
        Element error = (Element)errors.item(0);
        pd.initError(error.getChildNodes().item(0).getNodeValue());
      }				

      NodeList simpleVars = doc.getElementsByTagName(ProcessXml.ELEMENT_VAR);
      for (int node=0; simpleVars != null && node < simpleVars.getLength(); node++) {
        Element a = (Element)simpleVars.item(node);
        String name = ProcessVariable.getNameFromElement(a);
        ProcessDataType type = _catalogue.getDataType(name);
        ProcessSimpleVariable var = new ProcessSimpleVariable(type, a);
        pd.set(var, false);
      }

      NodeList listVars = doc.getElementsByTagName(ProcessXml.ELEMENT_LISTVAR);
      for (int node=0; listVars != null && node < listVars.getLength(); node++) {
        Element l = (Element)listVars.item(node);
        String name = ProcessVariable.getNameFromElement(l);
        ProcessDataType type = _catalogue.getDataType(name);
        ProcessListVariable list = new ProcessListVariable(type, l);
        pd.setList(list, false);				
      }
      
      NodeList dataVars = doc.getElementsByTagName(ProcessXml.ELEMENT_APPDATA);
      for (int node=0; dataVars != null && node < dataVars.getLength(); node++) {
        Element l = (Element)dataVars.item(node);
        String name = ProcessVariable.getNameFromElement(l);
        String value = l.getFirstChild().getNodeValue();
        if(null != name)
          pd._appData.put(name, value);
      }
    }
    catch (Throwable t) {
      t.printStackTrace();
    }
    long end = System.currentTimeMillis();
    Logger.debug("", this, "", "DOM parsing finished in "+(end-start)+" ms");
    return pd;
  }
}
