package pt.iflow.api.blocks.form;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Result;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.flows.FlowSetting;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.XslTransformerFactory;

/**
 * Utilitario para aplicação de transformações XSLT a formulários.
 * 
 * @author Oscar Lopes
 *
 */
public abstract class TransformUtility {
	/**
	 * The TransformUtility class to instantiate
	 */
	private static final Class<? extends TransformUtility> theClass;
	/**
	 * Dummy source since API requires a non null InputSource
	 */
	static final InputSource XMLSource = new InputSource("XML FormSource");
	static {
		// if SAXTransformerFactory feature is available, use it
		if (TransformerFactory.newInstance().getFeature(SAXTransformerFactory.FEATURE))
			theClass = TransformUtilitySAX.class;
		else
			theClass = TransformUtilityTRaX.class;
	}

	/**
	 * The XMLReader to use as SAX event producer
	 */
	final FormXMLReader formReader;
	/**
	 * Additional paramters to pass to XSLT StyleSheet
	 */
	final Map<String, Object> parameters;

	/**
	 * Constructor
	 * 
	 * @param reader
	 *            FormXMLReader instance to use as event producer
	 */
	TransformUtility(FormXMLReader reader) {
		formReader = reader;
		parameters = new HashMap<String, Object>();
	}

	/**
	 * Set a StyleSheet parameter
	 * 
	 * @param name
	 *            Parameter name
	 * @param value
	 *            Parameter value
	 * @see javax.xml.transform.Transformer#setParameter(String, Object);
	 */
	public void setParameter(String name, Object value) {
		this.parameters.put(name, value);
	}

	/**
	 * Create a new instance of TransformUtility using a FormXMLReader as SAX
	 * event producer
	 * 
	 * @param formReader
	 *            FormXMLReader instance - SAX event producer
	 * @return TransformUtility instance or null if error occured
	 */
	public static TransformUtility newInstance(FormXMLReader formReader) {
		TransformUtility result = null;
		try {
			result = theClass.getConstructor(FormXMLReader.class).newInstance(formReader);
		} catch (Exception e) {
			Logger.error(null, "TransformUtility", "newInstance", "Error instantiating utility", e);
		}
		return result;
	}

	/**
	 * Output transformation result to a stream.
	 * 
	 * @param output
	 *            transformation result destination
	 * @param debugOutput
	 *            XML output generated SAX events to this stream (XML not
	 *            transformed). Use null to disable.
	 * @throws TransformerException
	 * @throws SAXException
	 * @throws IOException
	 */
	public abstract void transform(Writer output, Writer debugOutput)
			throws TransformerException, SAXException, IOException;

	/**
	 * Retrieve a Templates instance from cache for this FormXMLReader instance.
	 * This method first try to get a XSL configured in the form. If not found,
	 * try to fetch the default XSL for this flow. Then try to fetch
	 * organization default XSL. If everything fails, return null.
	 * 
	 * @param reader
	 *            The form, since it contains all information
	 * @return A Templates instance or null if none found
	 */
	static Templates getTemplates(FormXMLReader reader) throws TransformerException {
		final UserInfoInterface userInfo = reader.getUserInfo();
		final ProcessData procData = reader.getProcData();
		final FormProperties fProp = reader.getForm().getProperties();

		Templates templates = XslTransformerFactory.getTemplates(userInfo, fProp.getStylesheet());
		// transformer = XslTransformerFactory.getIdentityTransformer();
		if (null == templates) {
			Logger.info(userInfo.getUtilizador(), "TransformUtility", "getTemplates",
					"Stylesheet not found. Trying flow stylesheet...");
			// some default XSL processing...
			String sDefaultXslName = "";
			FlowSetting defXSLSetting = BeanFactory.getFlowSettingsBean()
					.getFlowSetting(reader.getProcData().getFlowId(), Const.sDEFAULT_STYLESHEET);
			if (null != defXSLSetting)
				sDefaultXslName = defXSLSetting.getValue();

			Logger.debug(userInfo.getUtilizador(), "TransformUtility", "getTemplates",
					"[" + procData.getFlowId() + "," + procData.getPid() + "," + procData.getSubPid() + "] "
							+ "flow default xsl name=" + sDefaultXslName);
			templates = XslTransformerFactory.getTemplates(userInfo, sDefaultXslName);
		}

		if (null == templates) {
			Logger.info(userInfo.getUtilizador(), "TransformUtility", "getTemplates",
					"Flow stylesheet not found. Fail back to system default...");
			templates = XslTransformerFactory.getDefaultTemplates(userInfo);
		}

		if (null == templates)
			throw new TransformerException("Template not found");

		return templates;
	}

	// Regular - more elegant?
	/**
	 * Transform XML using plain TRaX, ie, invoking
	 * transformer.transform(source, result). This implemetation will use an
	 * auxilliary XMLReader and "identity" transformer to output debug
	 * information.
	 */
	private static class TransformUtilityTRaX extends TransformUtility {

		public TransformUtilityTRaX(FormXMLReader reader) {
			super(reader);
			Logger.debug(reader.getUserInfo().getUtilizador(), this, "<init>", "Created a TransformUtilityTRaX");
		}

		@Override
		public void transform(Writer output, Writer debugOutput)
				throws TransformerException, SAXException, IOException {
			Result result = new StreamResult(output);
			Templates template = getTemplates(formReader);
			Transformer transformer = null;

			XMLReader rdr = formReader;

			try {

				if (debugOutput != null)
					rdr = new FormDebugXMLReader(formReader, new StreamResult(debugOutput));

				SAXSource inputSource = new SAXSource(rdr, XMLSource);

				if (null != template) transformer = template.newTransformer();
				// set properties here...
				for (String key : parameters.keySet())
					if (null != transformer) {
						transformer.setParameter(key, parameters.get(key));
						transformer.transform(inputSource, result);
					}

			} catch (Exception e) {
				Logger.error("", "TransformUtility", "transform", "Null deference - TransformUtility - 174", e);
			} finally {
				try {
					if (null != debugOutput) debugOutput.close();
				} catch (IOException e) {
					Logger.error("", "TransformUtility", "transform", "Null deference - TransformUtility - 174", e);
				}
			}
		}

	}

	// SAXTransformerFactory facilities - more efficient?
	/**
	 * Transform XML using TRaX and SAXTransformerFactory, allowing us to use
	 * ContentHandlers directly without transformers. I believe it is more
	 * efficient than "regular" TRaX, but never tested this.
	 */
	private static class TransformUtilitySAX extends TransformUtility {

		public TransformUtilitySAX(FormXMLReader reader) {
			super(reader);
			Logger.debug(reader.getUserInfo().getUtilizador(), this, "<init>", "Created a TransformUtilitySAX");
		}

		@Override
		public void transform(Writer output, Writer debugOutput)
				throws TransformerException, SAXException, IOException {
			Result result = new StreamResult(output);
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Templates template = getTemplates(formReader);

			SAXTransformerFactory saxTransformerFactory = (SAXTransformerFactory) transformerFactory;
			TransformerHandler transformHandler = saxTransformerFactory.newTransformerHandler(template);
			// set properties here...
			Transformer transformer = transformHandler.getTransformer();
			for (String key : parameters.keySet())
				transformer.setParameter(key, parameters.get(key));
			transformHandler.setResult(result);
			formReader.setContentHandler(transformHandler);
			if (debugOutput != null) {
				TransformerHandler debugHandler = saxTransformerFactory.newTransformerHandler();
				debugHandler.setResult(new StreamResult(debugOutput));
				formReader.setDebugContentHandler(debugHandler);
			}
			formReader.parse(XMLSource);
		}

	}
}
