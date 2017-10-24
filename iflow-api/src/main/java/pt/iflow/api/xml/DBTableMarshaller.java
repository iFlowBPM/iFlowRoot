package pt.iflow.api.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

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

	public static byte[] marshall(DBTable table) throws JAXBException {
		ByteArrayOutputStream retObj = new ByteArrayOutputStream();
		try {
			Writer writer = new OutputStreamWriter(retObj, "UTF-8");
			JAXBContext context = JAXBContext.newInstance(DBTable.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.marshal(table, writer);
			writer.close();
		} catch (Exception e) 
		{
			throw new JAXBException(e);
		}
		return retObj.toByteArray();
	}

	public static DBTable unmarshal(byte[] data) throws JAXBException {
		DBTable retObj = null;
		InputSource source = null;
		try {
			source = new InputSource(new InputStreamReader(new ByteArrayInputStream(data), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new JAXBException(e);
		}
		
		JAXBContext context = JAXBContext.newInstance(DBTable.class);
		
		Unmarshaller unmarshaller = context.createUnmarshaller();
		return (DBTable) unmarshaller.unmarshal(source);

	}
}
