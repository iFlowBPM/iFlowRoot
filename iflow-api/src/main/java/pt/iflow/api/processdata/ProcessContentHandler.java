/**
 * 
 */
package pt.iflow.api.processdata;

import java.util.Date;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import pt.iflow.api.core.ProcessCatalogue;
import pt.iflow.api.processtype.ProcessDataType;
import pt.iflow.api.utils.Logger;

class ProcessContentHandler extends DefaultHandler {

  static final class Token {
    String name;
    String text;
    ProcessListItemList itemList;
    ProcessDataType listType; 
    int pos;
  }

  private void debug(String msg, Throwable t) {
//      Logger.debug(null, this, "parser", msg, t);
  }
  
  private void info(String msg, Throwable t) {
//      Logger.info(null, this, "parser", msg, t);
  }
  
  private void warn(String msg, Throwable t) {
//      Logger.warning(null, this, "parser", msg, t);
  }
  
  private void error(String msg, Throwable t) {
//      Logger.error(null, this, "parser", msg, t);
  }
  
  private final ProcessCatalogue _catalogue;
  private final boolean _namespaceAware;
  private ProcessData _pd = null;

  /**
   * @param processXmlReader
   */
  ProcessContentHandler(final ProcessCatalogue catalogue, final boolean namespaceAware) {
    this._catalogue = catalogue;
    this._namespaceAware = namespaceAware;
  }
  
  ProcessData getProcessData() {
    return _pd;
  }

  private Stack<Token> treeParserStack;
  private Map<String,String> prefixMap;

  private StringBuilder sbText = null;
  private long startTime = 0L;
  private Token processError = null;

  public void startDocument() throws SAXException {
    treeParserStack = new Stack<Token>();
    sbText = new StringBuilder();
    prefixMap = new HashMap<String, String>();
    startTime = System.currentTimeMillis();
    info("Document parse started", null);
  }


  public void endDocument() throws SAXException {
    long end = System.currentTimeMillis();
    debug("Document parsing finished in "+(end-startTime)+" ms", null);
  }

  public void startPrefixMapping(String prefix, String uri) throws SAXException {
    super.startPrefixMapping(prefix, uri);
    if(!_namespaceAware) return;
    prefixMap.put(prefix, uri);
    debug("Prefix: '"+prefix+"'='"+uri+"'",null);
  }

  @Override
  public void endPrefixMapping(String prefix) throws SAXException {
    super.endPrefixMapping(prefix);
    if(!_namespaceAware) return;
    debug("End Prefix: '"+prefix+"'",null);
  }


  public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
    String tag = name;
    if(_namespaceAware) tag = localName; // if namespace aware, localName contains tag without prefix, just like we want
    try {
      // check pending text...
      setText();
      Token var = new Token();

      if(tag.equals(ProcessXml.ELEMENT_PROCESSDATA)) {
        int _flowid = Integer.parseInt(attributes.getValue(ProcessXml.ATTR_FLOWID));
        int _pid = Integer.parseInt(attributes.getValue(ProcessXml.ATTR_PID));
        int _subpid = Integer.parseInt(attributes.getValue(ProcessXml.ATTR_SUBPID));
        String _pnumber = attributes.getValue(ProcessXml.ATTR_PNUMBER);
        String _creator = attributes.getValue(ProcessXml.ATTR_CREATOR);
        Date _creationDate = ProcessXml.DateFormatter.parse(attributes.getValue(ProcessXml.ATTR_CREATION_DATE));
        String _currentUser = attributes.getValue(ProcessXml.ATTR_CURRENT_USER);
        Date _lastUpdate = ProcessXml.DateFormatter.parse(attributes.getValue(ProcessXml.ATTR_LAST_UPDATE));
        boolean _closed = Integer.parseInt(attributes.getValue(ProcessXml.ATTR_CLOSED)) == 1;

        ProcessHeader header = new ProcessHeader(_flowid, _pid, _subpid);
        header.setPNumber(_pnumber);
        header.setCreator(_creator);
        header.setCreationDate(_creationDate);
        header.setCurrentUser(_currentUser);
        header.setLastUpdate(_lastUpdate);
        header.setClosed(_closed);
        _pd = new ProcessData(_catalogue, header);

      } else if(tag.equals(ProcessXml.ELEMENT_ERROR)) {
        processError = var;
      } else {
        String varName = attributes.getValue(ProcessXml.ATTR_NAME);
        var.name = varName;
        if(tag.equals(ProcessXml.ELEMENT_LISTVAR)) {
          int size = Integer.parseInt(attributes.getValue(ProcessXml.ATTR_SIZE));
          var.listType=_catalogue.getDataType(var.name);
          var.itemList = new ProcessListItemList(size);
        } else if(tag.equals(ProcessXml.ELEMENT_LISTITEM)) {
          int position = Integer.parseInt(attributes.getValue(ProcessXml.ATTR_POSITION));
          var.pos = position;
        }
      }

      treeParserStack.push(var);
    } catch (Exception e) {
      e.printStackTrace();
      throw new SAXException("Error parsig process.", e);
    }
  }

  public void endElement(String uri, String localName, String name) throws SAXException {
    String tag = name;
    Token var=null;
    ProcessSimpleVariable processVar=null;
    ProcessListVariable listVar=null;
    Token listToken=null;
    ProcessListItemList itemList= null;
    ProcessListItem item = null;
    if(_namespaceAware) tag = localName; // if namespace aware, localName contains tag without prefix, just like we want
    try {
      // check pending text...
      setText();
       var = treeParserStack.pop();

      ProcessDataType type;
	if(tag.equals(ProcessXml.ELEMENT_ERROR)) {
        if(null != processError)
          _pd.initError(processError.text);
        processError = null;
      } else if(tag.equals(ProcessXml.ELEMENT_VAR)) {
         type = _catalogue.getDataType(var.name);
        
        if (type == null) {
          Logger.error(null, this, "endElement", "null type for var \"" + var.name + "\"(removed/renamed var in catalogue?). Ignoring...");
        }
        else {
          if(!type.isBindable()) {
             processVar = new ProcessSimpleVariable(type, var.name);
            processVar._value = new InternalValue(processVar._type, var.text);
            _pd.set(processVar, false);
          }
        }
      } else if(tag.equals(ProcessXml.ELEMENT_LISTVAR)) {
         listVar = new ProcessListVariable(var.listType, var.name);
        listVar.setItems(var.itemList);
        _pd.setList(listVar, false);
      } else if(tag.equals(ProcessXml.ELEMENT_LISTITEM)) {
         listToken = treeParserStack.peek();
         itemList = listToken.itemList;
         type = listToken.listType;
        if (type == null) {
          Logger.error(null, this, "endElement", "null type for list item for list var \"" + listToken.name + "\"(removed/renamed var in catalogue?). Ignoring...");
        }
        else {        
           item = new ProcessListItem(new InternalValue(type, var.text));
          itemList.set(var.pos, item);
        }
      } else if(tag.equals(ProcessXml.ELEMENT_APPDATA)) {
        if(null != var.name)
          _pd._appData.put(var.name, var.text);
      }

    } catch(Exception e) {
      e.printStackTrace();
      throw new SAXException(e);
    }
  }

  public void characters(char[] ch, int start, int length) throws SAXException {
    if(length == 0) return; // zero size, do nothing!
    sbText.append(ch, start, length);
  }


  /**
   * <p>This is a very unconvenient method to address an unconfortable issue about SAX parsing.
   * <p>Please refer to: 
   * <a href="http://www.ibm.com/developerworks/xml/library/x-tipsaxdo4.html">Tip: SAX and document 
   * order -- deliver maximally contiguous text</a>
   */
  private void setText() {
    try {
      if(sbText.length() > 0) {
        // peek and set...
        Token obj = treeParserStack.peek();
        String text = sbText.toString();
        obj.text = text;
      }
    } catch(EmptyStackException e) {}
    sbText.setLength(0); // reset size
  }

  // Error handling

  public void error(SAXParseException e) throws SAXException {
    error("Parse error: "+e.getMessage()+ " line: "+e.getLineNumber()+" column: "+e.getColumnNumber(), null);
    debug("Parse error: public id="+e.getPublicId()+"; system id="+e.getSystemId(), e);
  }

  public void fatalError(SAXParseException e) throws SAXException {
    error("Fatal error: " + e.getMessage()+ " line: "+e.getLineNumber()+" column: "+e.getColumnNumber(), null);
    debug("Fatal error: public id="+e.getPublicId()+"; system id="+e.getSystemId(), e);
  }

  public void warning(SAXParseException e) throws SAXException {
    warn("Warning: " + e.getMessage()+ " line: "+e.getLineNumber()+" column: "+e.getColumnNumber(), null);
    debug("Warning: public id="+e.getPublicId()+"; system id="+e.getSystemId(), e);
  }

}