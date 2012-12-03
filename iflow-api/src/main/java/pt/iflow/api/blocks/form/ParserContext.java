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
