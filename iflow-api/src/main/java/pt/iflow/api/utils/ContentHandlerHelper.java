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
package pt.iflow.api.utils;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class ContentHandlerHelper implements ContentHandler {

  private final AttributesImpl defaultAtts = new AttributesImpl();
  private final ContentHandler ch;
  private String uri;
  private char[] buffer = new char[8192];
  
  public ContentHandlerHelper(final ContentHandler ch) {
    this.ch = ch;
  }
  
  public void setDefaultURI(String uri) {
    this.uri = uri;
  }
  
  public void clearAtts() {
    defaultAtts.clear();
  }
  
  public void addAtt(String name, String value) {
    defaultAtts.addAttribute(uri, name, name, "CDATA", value);
  }
  
  public void addAtt(String uri, String localName, String name, String value) {
    defaultAtts.addAttribute(uri, localName, name, "CDATA", value);
  }
  
  public void setAtt(String name, String value) {
    defaultAtts.setValue(defaultAtts.getIndex(uri, name), value);
  }
  
  public void setAtt(int idx, String value) {
    defaultAtts.setValue(idx, value);
  }
  
  public int getAttIndex(String name) {
    return defaultAtts.getIndex(uri, name);
  }
  
  public void setElement(String name, String value) throws SAXException {
    startElement(uri, name, defaultAtts);
    setText(value);
    endElement(uri, name);
  }
  
  public void setElement(String uri, String name, String value) throws SAXException {
    startElement(uri, name, defaultAtts);
    setText(value);
    endElement(uri, name);
  }

  public void setElement(String name, String value, Attributes att) throws SAXException {
    startElement(uri, name, att);
    setText(value);
    endElement(uri, name);
  }
  
  public void setElement(String uri, String name, String value, Attributes att) throws SAXException {
    startElement(uri, name, att);
    setText(value);
    endElement(uri, name);
  }

  public void startElement(String name) throws SAXException {
    this.ch.startElement(uri, name, name, defaultAtts); // XXX qName esta ok??
  }

  public void startElement(String name, Attributes att) throws SAXException {
    this.ch.startElement(uri, name, name, att); // XXX qName esta ok??
  }

  public void startElement(String uri, String name) throws SAXException {
    this.ch.startElement(uri, name, name, defaultAtts); // XXX qName esta ok??
  }

  public void startElement(String uri, String name, Attributes att) throws SAXException {
    this.ch.startElement(uri, name, name, att); // XXX qName esta ok??
  }

  public void endElement(String name) throws SAXException {
    this.ch.endElement(uri, name, name); // XXX qName esta ok??
  }

  public void endElement(String uri, String name) throws SAXException {
    this.ch.endElement(uri, name, name); // XXX qName esta ok??
  }

  public void setText(String txt) throws SAXException {
    if(null == txt) return;
    if(buffer.length < txt.length())
      buffer = new char[txt.length()+1024]; // increase size
    txt.getChars(0, txt.length(), buffer, 0);
    this.ch.characters(buffer, 0, txt.length());
  }
  
  
  public void startDocument() throws SAXException {
    clearAtts();
    this.ch.startDocument();
  }

  public void endDocument() throws SAXException {
    this.ch.endDocument();
    clearAtts();
  }

  public void characters(char[] ch, int start, int length) throws SAXException {
    this.ch.characters(ch, start, length);
  }

  public void endElement(String uri, String localName, String name) throws SAXException {
    this.ch.endElement(uri, localName, name);
  }

  public void endPrefixMapping(String prefix) throws SAXException {
    this.ch.endPrefixMapping(prefix);
  }

  public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    this.ch.ignorableWhitespace(ch, start, length);
  }

  public void processingInstruction(String target, String data) throws SAXException {
    this.ch.processingInstruction(target, data);
  }

  public void setDocumentLocator(Locator locator) {
    this.ch.setDocumentLocator(locator);
  }

  public void skippedEntity(String name) throws SAXException {
    this.ch.skippedEntity(name);
  }

  public void startElement(String uri, String localName, String name, Attributes atts) throws SAXException {
    this.ch.startElement(uri, localName, name, atts);
  }

  public void startPrefixMapping(String prefix, String uri) throws SAXException {
    this.ch.startPrefixMapping(prefix, uri);
  }
}
