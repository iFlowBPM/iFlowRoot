package pt.iflow.api.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.InputSource;

import pt.iflow.api.transition.FlowStateLogTO;

public class ProcessStateLogMarshaller {

  public static byte[] marshall(List<FlowStateLogTO> flowStateLog) throws MarshalException, ValidationException {
    ByteArrayOutputStream retObj = new ByteArrayOutputStream();
    try {
      Writer writer = new OutputStreamWriter(retObj, "UTF-8");
      Marshaller.marshal(flowStateLog, writer);
      writer.close();
    } catch (Exception e) {
      throw new MarshalException(e);
    }

    return retObj.toByteArray();
  }

  @SuppressWarnings("unchecked")
  public static List<FlowStateLogTO> unmarshal(byte[] data) throws MarshalException, ValidationException {
    List<FlowStateLogTO> retObj = new ArrayList<FlowStateLogTO>();
    InputSource source = null;
    try {
      source = new InputSource(new InputStreamReader(new ByteArrayInputStream(data), "UTF-8"));
    } catch (UnsupportedEncodingException e) {
      throw new MarshalException(e);
    }
    retObj = (List<FlowStateLogTO>) Unmarshaller.unmarshal(List.class, source);
    return retObj;
  }
}
