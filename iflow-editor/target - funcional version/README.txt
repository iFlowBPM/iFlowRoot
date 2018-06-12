De modo ao editor funcionar é necessário alterar para o seguinte código no ficheiro - LibraryMarshaller

//InputSource source = null;
//source = new InputSource(inStream); // guess encoding from file			
//SAXParserFactory spf = SAXParserFactory.newInstance();
//spf.setFeature("http://xml.org/sax/features/external-general-entities", false);
//spf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
//spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
//Source xmlSource = new SAXSource(spf.newSAXParser().getXMLReader(), source );
JAXBContext context = JAXBContext.newInstance(XmlLibrary.class);
Unmarshaller unmarshaller = context.createUnmarshaller();
//return (XmlLibrary) unmarshaller.unmarshal(xmlSource);
return (XmlLibrary) unmarshaller.unmarshal(inStream);


De modo ao editor não funcionar alterar para o seguinte código no ficheiro - LibraryMarshaller

	public static XmlLibrary unmarshal(InputStream inStream) throws Exception 
	{
		InputSource source = null;
		source = new InputSource(inStream); // guess encoding from file			
		SAXParserFactory spf = SAXParserFactory.newInstance();
		spf.setFeature("http://xml.org/sax/features/external-general-entities", false);
		spf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
		spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		Source xmlSource = new SAXSource(spf.newSAXParser().getXMLReader(), source );
		JAXBContext context = JAXBContext.newInstance(XmlLibrary.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		return (XmlLibrary) unmarshaller.unmarshal(xmlSource);
		
		//JAXBContext context = JAXBContext.newInstance(new Class[] { XmlLibrary.class });
	    //Unmarshaller unmarshaller = context.createUnmarshaller();
	    
	    //return (XmlLibrary)unmarshaller.unmarshal(inStream);
		
	}