package pt.iflow.api.blocks.form;

import org.apache.commons.lang.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class ParserContext {
  // an empty attribute for use with SAX
  private static final Attributes EMPTY_ATTR = new AttributesImpl( );
  private static final String XML_URI = "";//"http://www.iflow.pt/iFlow/FormXML";
  private static final String XML_PREFIX = "";//"form";
  private static final String XML_PREFIXED = "";//"form:";

  private ContentHandler ch;
  private ContentHandler dbg;
  
  public ParserContext(ContentHandler ch) {
    this(ch,null);
  }

  public ParserContext(ContentHandler ch, ContentHandler dbg) {
    if(null == ch) throw new IllegalArgumentException("Content Handler cannot be null");
    this.ch = ch;
    this.dbg = dbg;
  }

  public ParserContext startDocument() throws SAXException {
    this.ch.startDocument();
    this.ch.startPrefixMapping(XML_PREFIX, XML_URI);
    if(dbg != null) {
      this.dbg.startDocument();
      this.dbg.startPrefixMapping(XML_PREFIX, XML_URI);
    }
    return this;
  }
  
  public ParserContext endDocument() throws SAXException {
    this.ch.endPrefixMapping(XML_PREFIX);
    this.ch.endDocument();
    if(dbg != null) {
      this.dbg.endPrefixMapping(XML_PREFIX);
      this.dbg.endDocument();
    }
    return this;
  }
  
  public ParserContext startElement(String name) throws SAXException {
    this.ch.startElement(XML_URI, name, XML_PREFIXED+name, EMPTY_ATTR);
    if(dbg != null) {
      this.dbg.startElement(XML_URI, name, XML_PREFIXED+name, EMPTY_ATTR);
    }
    return this;
  }
  
  public ParserContext endElement(String name) throws SAXException {
    this.ch.endElement(XML_URI, name, XML_PREFIXED+name);
    if(dbg != null) {
      this.dbg.endElement(XML_URI, name, XML_PREFIXED+name);
    }
    return this;
  }
  
  public ParserContext addElement(String name, String content) throws SAXException {
    this.ch.startElement(XML_URI, name, XML_PREFIXED+name, EMPTY_ATTR);
    if(StringUtils.isNotEmpty(content)) {
      this.ch.characters(content.toCharArray(), 0, content.length());
    }
    this.ch.endElement(XML_URI, name, XML_PREFIXED+name);
    
    if(dbg != null) {
      this.dbg.startElement(XML_URI, name, XML_PREFIXED+name, EMPTY_ATTR);
      if(StringUtils.isNotEmpty(content)) {
        this.dbg.characters(content.toCharArray(), 0, content.length());
      }
      this.dbg.endElement(XML_URI, name, XML_PREFIXED+name);
    }
    return this;
  }
  
  public ParserContext addHiddenField(String name, String value) throws SAXException {
    return startElement("hidden").addElement("name", name).addElement("value", value).endElement("hidden");
  }
  
  public ParserContext appendText(String txt) throws SAXException {
    if(StringUtils.isNotEmpty(txt)) {
      this.ch.characters(txt.toCharArray(), 0, txt.length());
    }
    if(dbg != null) {
      if(StringUtils.isNotEmpty(txt)) {
        this.dbg.characters(txt.toCharArray(), 0, txt.length());
      }
    }
    return this;
  }
}
