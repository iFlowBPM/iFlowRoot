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

import pt.iflow.api.connectors.ConnectorInterface;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.NameValuePair;
import pt.iflow.api.xml.helpers.ConnectorTransferObject;

public class ConnectorMarshaller {

  public static byte[] marshall(NameValuePair<String, Class<ConnectorInterface>>[] connectors) throws MarshalException,
      ValidationException {
    ByteArrayOutputStream retObj = new ByteArrayOutputStream();
    try {
      Writer writer = new OutputStreamWriter(retObj, Const.ENCODING);
      Marshaller.marshal(encode(connectors), writer);
      writer.close();
    } catch (Exception e) {
      throw new MarshalException(e);
    }
    return retObj.toByteArray();
  }

  public static NameValuePair<String, String>[] unmarshal(byte[] data) throws MarshalException,
      ValidationException {
    InputSource source = null;
    try {
      source = new InputSource(new InputStreamReader(new ByteArrayInputStream(data), Const.ENCODING));
    } catch (UnsupportedEncodingException e) {
      throw new MarshalException(e);
    }
    return decode((ConnectorTransferObject) Unmarshaller.unmarshal(ConnectorTransferObject.class, source));
  }

  private static ConnectorTransferObject encode(NameValuePair<String, Class<ConnectorInterface>>[] connectors) {
    List<String> description = new ArrayList<String>();
    List<String> clazz = new ArrayList<String>();
    if (connectors != null) {
      for (NameValuePair<String, Class<ConnectorInterface>> connector : connectors) {
        description.add(connector.getName() == null ? "" : connector.getName());
        clazz.add(connector.getValue() == null ? "" : connector.getValue().getName());
      }
    }
    return new ConnectorTransferObject(description, clazz);
  }

  // now this method has a string value pair to remove editor classpath dependencies (with
  // class value, editor had to be able to load class)
  @SuppressWarnings("unchecked")
  private static NameValuePair<String, String>[] decode(ConnectorTransferObject connectors) {
    List<NameValuePair<String, String>> retObj = new ArrayList<NameValuePair<String, String>>();
    if (connectors != null) {
      for (int i = 0; i < connectors.getDescription().size(); i++) {
        NameValuePair<String, String> nvp;

        String sDescription = connectors.getDescription().get(i);
        String sClazz = connectors.getClazz().get(i);
        nvp = new NameValuePair<String, String>(sDescription, sClazz);
        retObj.add(nvp);
      }
    }
    return (NameValuePair<String, String>[]) retObj.toArray(new NameValuePair[retObj.size()]);
  }
}

