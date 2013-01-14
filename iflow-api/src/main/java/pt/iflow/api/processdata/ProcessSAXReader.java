/**
 * 
 */
package pt.iflow.api.processdata;

import java.io.IOException;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import pt.iflow.api.utils.AbstractXMLReader;
import pt.iflow.api.utils.ContentHandlerHelper;
import pt.iflow.api.utils.Logger;

class ProcessSAXReader extends AbstractXMLReader {
  final ProcessData process;
  
  ProcessSAXReader(final ProcessData process) {
    this.process = process;
  }
  
  
  public void parse(InputSource input) throws IOException, SAXException {
    String sysId = input.getSystemId();
    if(!StringUtils.equals(ProcessXml.NS_PROCESSDATA, sysId)) throw new SAXException("Invalid input source System ID: "+sysId);
    long start = System.currentTimeMillis();
    ContentHandlerHelper ch = getContentHandlerHelper();
    ch.startDocument();
    ch.setDefaultURI(ProcessXml.NS_PROCESSDATA);
    
    // create prefix/schema mapping
    ch.startPrefixMapping("", ProcessXml.NS_PROCESSDATA);
    ch.startPrefixMapping("xsi", ProcessXml.NS_SCHEMA_INSTANCE);
    
    // NS attributes
    ch.addAtt(ProcessXml.NS_SCHEMA_INSTANCE, "schemaLocation", "xsi:schemaLocation", ProcessXml.NS_PROCESSDATA+" processdata.xsd");
    // create process header attributes
    ch.addAtt(ProcessXml.ATTR_FLOWID, String.valueOf(process.getFlowId()));
    ch.addAtt(ProcessXml.ATTR_PID, String.valueOf(process.getPid()));
    ch.addAtt(ProcessXml.ATTR_SUBPID, String.valueOf(process.getSubPid()));
    ch.addAtt(ProcessXml.ATTR_PNUMBER, process.getPNumber());
    ch.addAtt(ProcessXml.ATTR_CREATOR, process.getCreator());
    ch.addAtt(ProcessXml.ATTR_CREATION_DATE, ProcessXml.DateFormatter.format(process.getCreationDate()));
    ch.addAtt(ProcessXml.ATTR_CURRENT_USER, process.getCurrentUser());
    ch.addAtt(ProcessXml.ATTR_LAST_UPDATE, ProcessXml.DateFormatter.format(process.getLastUpdate()));
    ch.addAtt(ProcessXml.ATTR_CLOSED, process.isClosed()?"1":"0");
    
    ch.startElement(ProcessXml.ELEMENT_PROCESSDATA);
    ch.clearAtts();
    ch.setElement(ProcessXml.ELEMENT_ERROR, process.getErrorOrigRawValue());
    
    // Iterate values
    Collection<ProcessSimpleVariable> simpleVars = process.getSimpleVariables();
    ch.addAtt(ProcessXml.ATTR_NAME, ""); 
    int attrNameIdx = ch.getAttIndex(ProcessXml.ATTR_NAME);
    for(ProcessSimpleVariable var : simpleVars) {
      if(null == var || var.getType() == null || var.getType().isBindable()) continue;
      
      String rawValue = var.getRawValue();
      if(null == rawValue) continue;
      // update var name attribute
      ch.setAtt(attrNameIdx, var.getName());
      
      // create var element
      ch.setElement(ProcessXml.ELEMENT_VAR, rawValue);
    }
    
    // prepare auxiliar attributes object
    AttributesImpl itemsAtts = new AttributesImpl();
    itemsAtts.addAttribute(ProcessXml.NS_PROCESSDATA, ProcessXml.ATTR_POSITION, ProcessXml.ATTR_POSITION, "CDATA", "");
    int itemAttPos = itemsAtts.getIndex(ProcessXml.NS_PROCESSDATA, ProcessXml.ATTR_POSITION);
    
    Collection<ProcessListVariable> listVars = process.getListVariables();
    ProcessListItemList items;
    ch.addAtt(ProcessXml.ATTR_SIZE, ""); 
    int attrSizeIdx = ch.getAttIndex(ProcessXml.ATTR_SIZE);
    for(ProcessListVariable var : listVars) {
      if(var == null || var.getType() == null|| var.getType().isBindable())  continue;

      // update list attributes
      ch.setAtt(attrNameIdx, var.getName());
      ch.setAtt(attrSizeIdx, String.valueOf(var.size()));
      
      // open list tag
      ch.startElement(ProcessXml.ELEMENT_LISTVAR);
      
      // store list items
      items = var.getItems();
      for(ProcessListItem item : items) {
        if(null == item) continue;
        String rawValue = item.getRawValue();
        if(null == rawValue) continue;
        itemsAtts.setValue(itemAttPos, String.valueOf(item.getPosition()));
        ch.setElement(ProcessXml.ELEMENT_LISTITEM, rawValue, itemsAtts);
      }
      
      // close list tag
      ch.endElement(ProcessXml.ELEMENT_LISTVAR);
    }
    
    ch.clearAtts();
    ch.addAtt(ProcessXml.ATTR_NAME, "");
    attrNameIdx = ch.getAttIndex(ProcessXml.ATTR_NAME);
    for(String name : process._appData.keySet()) {
      if(null == name) continue;
      String value = process._appData.get(name);
      if(null == value) continue;
      ch.setAtt(attrNameIdx, name);

      ch.setElement(ProcessXml.ELEMENT_APPDATA, value, itemsAtts);
    }


    // close process data tag
    ch.endElement(ProcessXml.NS_PROCESSDATA, ProcessXml.ELEMENT_PROCESSDATA, ProcessXml.ELEMENT_PROCESSDATA);
    ch.endDocument();
    long end = System.currentTimeMillis();
    Logger.debug("", this, "", "Document generated in "+ (end-start)+" ms");
  }
  
}