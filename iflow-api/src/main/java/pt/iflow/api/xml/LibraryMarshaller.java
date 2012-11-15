package pt.iflow.api.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.InputSource;

import pt.iflow.api.xml.codegen.library.XmlLibrary;

public class LibraryMarshaller {
  
  public static byte[] marshall(XmlLibrary library) throws MarshalException, ValidationException {
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    try {
      Writer w = new OutputStreamWriter(bout, "UTF-8");
      Marshaller.marshal(library, w);
      w.close();
    } catch (Exception e) {
      throw new MarshalException(e);
    }

    return bout.toByteArray();
  }
  
  public static XmlLibrary unmarshal(byte [] data)
  throws MarshalException, ValidationException
  {
    return unmarshal(new ByteArrayInputStream(data));
  }
  
  public static XmlLibrary unmarshal(InputStream inStream)
  throws MarshalException, ValidationException
  {
    InputSource source = null;
    source = new InputSource(inStream); // guess encoding from file
    return (XmlLibrary) Unmarshaller.unmarshal(XmlLibrary.class, source);
  }
  

}
