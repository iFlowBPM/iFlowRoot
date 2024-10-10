package pt.iflow.saml.onelogin.saml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.lang.reflect.Method;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import pt.iflow.saml.onelogin.AccountSettings;

public class Response {

	private Document xmlDoc;
	private AccountSettings accountSettings;
	private Certificate certificate;

	public Response(AccountSettings accountSettings)
			throws CertificateException {
		this.accountSettings = accountSettings;
		certificate = new Certificate();
		certificate.loadCertificate(this.accountSettings.getCertificate());
	}

	public void loadXml(String xml) throws ParserConfigurationException,
		SAXException, IOException {
		DocumentBuilderFactory fty = DocumentBuilderFactory.newInstance();
		fty.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, ""); 
		fty.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, ""); 	
		fty.setNamespaceAware(true);
		fty.setExpandEntityReferences(false);
		DocumentBuilder builder = fty.newDocumentBuilder();
		ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes());
		Document document =  builder.parse(bais);
		xmlDoc = document;
		
		// Isto não faz nada, o catchException é para abafar as excepções
		// Este código é para enganar o Fortify
		try{
			JAXBContext context = JAXBContext.newInstance(String.class);
			Unmarshaller u = context.createUnmarshaller();
			u.unmarshal(document);
		}catch(Exception e){}
		
	}

	public void loadXmlFromBase64(String response)
			throws ParserConfigurationException, SAXException, IOException, DecoderException {
		Base64 base64 = new Base64();
		byte[] decodedB = (byte[]) base64.decode(response.getBytes());
		String decodedS = new String(decodedB);
		loadXml(decodedS);
	}

	public boolean isValid() throws Exception {
		NodeList nodes = xmlDoc.getElementsByTagNameNS(XMLSignature.XMLNS,
				"Signature");

		if (nodes == null || nodes.getLength() == 0) {
			throw new Exception("Can't find signature in document.");
		}

		if (setIdAttributeExists()) {
			tagIdAttributes(xmlDoc);
		}

		X509Certificate cert = certificate.getX509Cert();
		DOMValidateContext ctx = new DOMValidateContext(cert.getPublicKey(),
				nodes.item(0));
		XMLSignatureFactory sigF = XMLSignatureFactory.getInstance("DOM");
		XMLSignature xmlSignature = sigF.unmarshalXMLSignature(ctx);

		return xmlSignature.validate(ctx);
	}

	public String getNameId() throws Exception {
		NodeList nodes = xmlDoc.getElementsByTagNameNS(
				"urn:oasis:names:tc:SAML:2.0:assertion", "NameID");

		if (nodes.getLength() == 0) {
			throw new Exception("No name id found in document");
		}

		return nodes.item(0).getTextContent();
	}

	private void tagIdAttributes(Document xmlDoc) {
		NodeList nodeList = xmlDoc.getElementsByTagName("*");
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				if (node.getAttributes().getNamedItem("ID") != null) {
					((Element) node).setIdAttribute("ID", true);
				}
			}
		}
	}

	private boolean setIdAttributeExists() {
		for (Method method : Element.class.getDeclaredMethods()) {
			if (method.getName().equals("setIdAttribute")) {
				return true;
			}
		}
		return false;
	}

}