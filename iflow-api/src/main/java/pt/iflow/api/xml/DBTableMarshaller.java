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
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;

import org.xml.sax.InputSource;

import pt.iflow.api.db.DBTable;
import pt.iflow.api.utils.Const;
import pt.iflow.api.xml.helpers.ConnectorTransferObject;

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
		try 
		{
			source = new InputSource(new InputStreamReader(new ByteArrayInputStream(data), Const.ENCODING));
			SAXParserFactory spf = SAXParserFactory.newInstance();
			spf.setFeature("http://xml.org/sax/features/external-general-entities", false);
			spf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
			spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			Source xmlSource = new SAXSource(spf.newSAXParser().getXMLReader(), source );
			JAXBContext context = JAXBContext.newInstance(DBTable.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			return (DBTable) unmarshaller.unmarshal(xmlSource);
		} 
		catch (Exception e) {
			throw new JAXBException(e);
		}
		
	}
}
