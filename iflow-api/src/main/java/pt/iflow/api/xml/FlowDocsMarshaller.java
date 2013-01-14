package pt.iflow.api.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.InputSource;

import pt.iflow.connector.dms.ContentResult;

public class FlowDocsMarshaller {

  public static byte[] marshallContentResult(ContentResult content) throws MarshalException, ValidationException {
    ByteArrayOutputStream retObj = new ByteArrayOutputStream();
    try {
      Writer writer = new OutputStreamWriter(retObj, "UTF-8");
      Marshaller.marshal(content, writer);
      writer.close();
    } catch (Exception e) {
      throw new MarshalException(e);
    }
    return retObj.toByteArray();
  }

  public static ContentResult unmarshalContentResult(byte[] data) throws MarshalException, ValidationException {
    ContentResult retObj = null;
    InputSource source = null;
    try {
      source = new InputSource(new InputStreamReader(new ByteArrayInputStream(data), "UTF-8"));
    } catch (UnsupportedEncodingException e) {
      throw new MarshalException(e);
    }
    retObj = (ContentResult) Unmarshaller.unmarshal(ContentResult.class, source);
    return retObj;
  }
}
