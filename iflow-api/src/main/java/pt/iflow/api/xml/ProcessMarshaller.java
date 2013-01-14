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

import pt.iflow.api.xml.codegen.processdata.Processdata;

public class ProcessMarshaller {
  
  public static byte[] marshall(Processdata process) throws MarshalException, ValidationException {
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    try {
      Writer w = new OutputStreamWriter(bout, "UTF-8");
      Marshaller.marshal(process, w);
      w.close();
    } catch (Exception e) {
      throw new MarshalException(e);
    }

    return bout.toByteArray();
  }
  
  public static Processdata unmarshal(byte [] data)
  throws MarshalException, ValidationException
  {
    return unmarshal(new ByteArrayInputStream(data));
  }
  
  public static Processdata unmarshal(InputStream inStream)
  throws MarshalException, ValidationException
  {
    InputSource source = null;
    source = new InputSource(inStream); // guess encoding from file
    return (Processdata) Unmarshaller.unmarshal(Processdata.class, source);
  }
  

}
