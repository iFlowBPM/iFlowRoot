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

import pt.iflow.api.transition.FlowStateHistoryTO;

public class ProcessStateHistoryMarshaller {

  public static byte[] marshall(List<FlowStateHistoryTO> flowStateHistory) throws MarshalException, ValidationException {
    ByteArrayOutputStream retObj = new ByteArrayOutputStream();
    try {
      Writer writer = new OutputStreamWriter(retObj, "UTF-8");
      Marshaller.marshal(flowStateHistory, writer);
      writer.close();
    } catch (Exception e) {
      throw new MarshalException(e);
    }

    return retObj.toByteArray();
  }

  @SuppressWarnings("unchecked")
  public static List<FlowStateHistoryTO> unmarshal(byte[] data) throws MarshalException, ValidationException {
    List<FlowStateHistoryTO> retObj = new ArrayList<FlowStateHistoryTO>();
    InputSource source = null;
    try {
      source = new InputSource(new InputStreamReader(new ByteArrayInputStream(data), "UTF-8"));
    } catch (UnsupportedEncodingException e) {
      throw new MarshalException(e);
    }
    retObj = (List<FlowStateHistoryTO>) Unmarshaller.unmarshal(List.class, source);
    return retObj;
  }
}
