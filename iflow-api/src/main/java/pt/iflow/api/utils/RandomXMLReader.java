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
