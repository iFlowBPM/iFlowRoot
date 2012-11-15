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

import pt.iflow.api.db.DBTable;

/**
 * Marshaller for <i>DBTable</i>.
 * 
 * @author Luis Cabral
 * @since 22.01.2010
 * @version 22.01.2010
 */
public class DBTableMarshaller {

  public static byte[] marshall(DBTable table) throws MarshalException, ValidationException {
    ByteArrayOutputStream retObj = new ByteArrayOutputStream();
    try {
      Writer writer = new OutputStreamWriter(retObj, "UTF-8");
      Marshaller.marshal(table, writer);
      writer.close();
    } catch (Exception e) {
      throw new MarshalException(e);
    }
    return retObj.toByteArray();
  }

  public static DBTable unmarshal(byte[] data) throws MarshalException, ValidationException {
    DBTable retObj = null;
    InputSource source = null;
    try {
      source = new InputSource(new InputStreamReader(new ByteArrayInputStream(data), "UTF-8"));
    } catch (UnsupportedEncodingException e) {
      throw new MarshalException(e);
    }
    retObj = (DBTable) Unmarshaller.unmarshal(DBTable.class, source);
    return retObj;
  }
}
