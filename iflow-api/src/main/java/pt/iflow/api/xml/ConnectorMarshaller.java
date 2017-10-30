package pt.iflow.api.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;

import org.xml.sax.InputSource;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import pt.iflow.api.connectors.ConnectorInterface;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.NameValuePair;
import pt.iflow.api.xml.helpers.ConnectorTransferObject;

public class ConnectorMarshaller {

	public static byte[] marshall(NameValuePair<String, Class<ConnectorInterface>>[] connectors) throws JAXBException {
		ByteArrayOutputStream retObj = new ByteArrayOutputStream();
		try {
			Writer writer = new OutputStreamWriter(retObj, Const.ENCODING);
			JAXBContext context = JAXBContext.newInstance(ConnectorTransferObject.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.marshal(encode(connectors), writer);
			writer.close();
		} catch (Exception e) {
			throw new JAXBException(e);
		}
		return retObj.toByteArray();
	}

	public static NameValuePair<String, String>[] unmarshal(byte[] data) throws JAXBException {
		InputSource source = null;
		try {
			source = new InputSource(new InputStreamReader(new ByteArrayInputStream(data), Const.ENCODING));
			SAXParserFactory spf = SAXParserFactory.newInstance();
			spf.setFeature("http://xml.org/sax/features/external-general-entities", false);
			spf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
			spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			Source xmlSource = new SAXSource(spf.newSAXParser().getXMLReader(), source );
			JAXBContext context = JAXBContext.newInstance(ConnectorTransferObject.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			return decode( (ConnectorTransferObject) unmarshaller.unmarshal(xmlSource));
			
		} catch ( Exception e) 
		{
			throw new JAXBException(e);
		}
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

	// now this method has a string value pair to remove editor classpath
	// dependencies (with
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
