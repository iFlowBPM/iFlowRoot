package pt.iflow.api.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;

import org.xml.sax.InputSource;

import pt.iflow.api.transition.FlowStateHistoryTO;
import pt.iflow.api.transition.FlowStateHistoryTOList;
import pt.iflow.api.utils.Const;
import pt.iflow.connector.dms.ContentResult;

public class ProcessStateHistoryMarshaller {

	public static byte[] marshall(List<FlowStateHistoryTO> flowStateHistory) throws JAXBException {
		ByteArrayOutputStream retObj = new ByteArrayOutputStream();
		try {
			Writer writer = new OutputStreamWriter(retObj, "UTF-8");
			JAXBContext context = JAXBContext.newInstance(FlowStateHistoryTOList.class);
			Marshaller marshaller = context.createMarshaller();
			FlowStateHistoryTOList elements = new FlowStateHistoryTOList();
			elements.setElements( flowStateHistory );
			marshaller.marshal(elements, writer);
			writer.close();
		} catch (Exception e) {
			throw new JAXBException(e);
		}

		return retObj.toByteArray();
	}

	public static List<FlowStateHistoryTO> unmarshal(byte[] data) throws JAXBException {
		InputSource source = null;
		try {
			source = new InputSource(new InputStreamReader(new ByteArrayInputStream(data), Const.ENCODING));
			SAXParserFactory spf = SAXParserFactory.newInstance();
			spf.setFeature("http://xml.org/sax/features/external-general-entities", false);
			spf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
			spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			Source xmlSource = new SAXSource(spf.newSAXParser().getXMLReader(), source );
			JAXBContext context = JAXBContext.newInstance(FlowStateHistoryTOList.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			return ((FlowStateHistoryTOList) unmarshaller.unmarshal(xmlSource)).getElements();
		} catch (Exception e) {
			throw new JAXBException(e);
		}
		
		
	}
}
