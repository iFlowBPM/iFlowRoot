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

import pt.iflow.connector.dms.ContentResult;

public class FlowDocsMarshaller {

	public static byte[] marshallContentResult(ContentResult content) throws JAXBException {
		ByteArrayOutputStream retObj = new ByteArrayOutputStream();
		try {
			Writer writer = new OutputStreamWriter(retObj, "UTF-8");
			JAXBContext context = JAXBContext.newInstance(ContentResult.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.marshal(content, writer);
			writer.close();
		} catch (Exception e) {
			throw new JAXBException(e);
		}
		return retObj.toByteArray();
	}

	public static ContentResult unmarshalContentResult(byte[] data) throws JAXBException {
		InputSource source = null;
		try {
			source = new InputSource(new InputStreamReader(new ByteArrayInputStream(data), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new JAXBException(e);
		}
		
		JAXBContext context = JAXBContext.newInstance(ContentResult.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		return (ContentResult) unmarshaller.unmarshal(source);
		
	}
}
