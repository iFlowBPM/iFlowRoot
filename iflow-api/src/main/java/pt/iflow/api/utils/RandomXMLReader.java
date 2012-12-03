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

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class RandomXMLReader extends AbstractXMLReader {
  
  // an empty attribute for use with SAX
  private static final Attributes EMPTY_ATTR = new AttributesImpl( );

  @Override
  public void parse(InputSource input) throws IOException, SAXException {
    // ignore input
    if(!(input instanceof RandomStringInputSource))
      throw new SAXException("Invalid inputsource");
    
    RandomStringInputSource ois = (RandomStringInputSource) input;
    
    // if no handler is registered to receive events, don't bother
    // to parse the CSV file
    ContentHandler ch = getContentHandler( );
    if (ch == null) {
      return;
    }
 
    ch.startDocument( );
 
    // emit <csvFile>
    ch.startElement("","","form",EMPTY_ATTR);
    for(int i = 0; i < 10; i++) {
      ch.startElement("","","line",EMPTY_ATTR);
      char[] str = ois.nextString();
      ch.characters(str, 0, str.length);
      ch.endElement("","","line");
    }
    ch.endElement("","","form");
    ch.endDocument( );
  }
 
}
