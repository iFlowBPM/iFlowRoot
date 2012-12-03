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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

/**
 * An abstract class that implements the SAX2 XMLReader interface. The intent of
 * this class is to make it easy for subclasses to act as SAX2 XMLReader
 * implementations. This makes it possible, for example, for them to emit SAX2
 * events that can be fed into an XSLT processor for transformation.
 */
public abstract class AbstractXMLReader implements org.xml.sax.XMLReader {
  private Map<String, Boolean> featureMap = new HashMap<String, Boolean>();
  private Map<String, Object> propertyMap = new HashMap<String, Object>();
  private EntityResolver entityResolver;
  private DTDHandler dtdHandler;
  private ContentHandler contentHandler;
  private ErrorHandler errorHandler;

  /**
   * The only abstract method in this class. Derived classes can parse any
   * source of data and emit SAX2 events to the ContentHandler.
   */
  public abstract void parse(InputSource input) throws IOException, SAXException;

  public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
    Boolean featureValue = this.featureMap.get(name);
    return (featureValue == null) ? false : featureValue.booleanValue();
  }

  public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
    this.featureMap.put(name, value);
  }

  public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
    return this.propertyMap.get(name);
  }

  public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
    this.propertyMap.put(name, value);
  }

  public void setEntityResolver(EntityResolver entityResolver) {
    this.entityResolver = entityResolver;
  }

  public EntityResolver getEntityResolver() {
    return this.entityResolver;
  }

  public void setDTDHandler(DTDHandler dtdHandler) {
    this.dtdHandler = dtdHandler;
  }

  public DTDHandler getDTDHandler() {
    return this.dtdHandler;
  }

  public void setContentHandler(ContentHandler contentHandler) {
    this.contentHandler = contentHandler;
  }

  public ContentHandler getContentHandler() {
    return this.contentHandler;
  }

  public void setErrorHandler(ErrorHandler errorHandler) {
    this.errorHandler = errorHandler;
  }

  public ErrorHandler getErrorHandler() {
    return this.errorHandler;
  }

  public ContentHandlerHelper getContentHandlerHelper() {
    return new ContentHandlerHelper(this.contentHandler);
  }

  public void parse(String systemId) throws IOException, SAXException {
    parse(new InputSource(systemId));
  }
}
