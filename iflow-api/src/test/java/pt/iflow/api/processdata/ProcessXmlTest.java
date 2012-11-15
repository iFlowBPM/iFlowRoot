package pt.iflow.api.processdata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;

import javax.xml.xpath.XPathConstants;

import org.apache.xml.serializer.ToXMLStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import pt.iflow.api.processtype.IntegerDataType;

public class ProcessXmlTest extends ProcessTestCase {
    
    @Before
    public void setUp() throws Exception {
        super.setUp();
        _pxml = new ProcessXml(_catalogue, new FileReader(_xmlFile));
        
        // out("Finished setup:\n - process catalogue:\n" +
        // _catalogue.toString() + "\n - process data:\n" +
        // _pxml.getProcessData().toString(true));
    }
    
    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }
    
    @Test
    public void testGetWriter() {
        initTest("TEST GET WRITER");
        
        String xmlOut = _xmlFile.substring(0, _xmlFile.indexOf("."))
                + "Out.xml";
        
        ProcessXmlWriter reader = _pxml.getWriter();
        
        File f = new File(xmlOut);
        FileWriter fw;
        try {
            fw = new FileWriter(f);
            int r;
            char[] b = new char[8192];
            while ((r = reader.read(b)) != -1) {
                fw.write(b, 0, r);
            }
            fw.close();
            reader.close();
            out("File " + xmlOut + " wrote");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception writing " + xmlOut + " file ");
        }
        
        out("Validating wrote file...");
        try {
            assertTrue(ProcessXml.validateXml(new FileInputStream(xmlOut)));
            out("file is valid xml");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fail("File " + xmlOut + " not found");
        }
        
        finishTest("TEST GET WRITER");
    }
    
    @Test
    public void testGetXmlAndValidate() {
        initTest("TEST GET XML AND VALIDATE");
        
        String xml = _pxml.getXml();
        out("XML:\n" + xml + "\n");
        assertTrue(_pxml.validateXml());
        out("XML is valid!\n");
        
        finishTest("TEST GET XML AND VALIDATE");
    }
    
    @Test
    public void testEvaluateXPathText() {
        initTest("TEST EVALUATE XPATH TEXT");
        
        String expression = "/processdata/a[@n='aText']";
        String expectedValue = "Some text";
        
        String xpathValue = ProcessXml.evaluateXPath(new InputSource(_xmlFile),
                expression);
        assertEquals(expectedValue, xpathValue);
        
        finishTest("TEST EVALUATE XPATH");
    }
    
    @Test
    public void testEvaluateXPathInteger() {
        initTest("TEST EVALUATE XPATH INTEGER");
        
        String expression = "/processdata/a[@n='aInteger']";
        String expectedValue = "100000000000006200000";
        
        String xpathValue = ProcessXml.evaluateXPath(new InputSource(_xmlFile),
                expression);
        assertEquals(expectedValue, xpathValue);
        
        finishTest("TEST EVALUATE XPATH INTEGER");
    }
    
    @Test
    public void testEvaluateXPathIntegerCompare() {
        initTest("TEST EVALUATE XPATH INTEGER COMPARE");
        
        int compareWith = 0;
        String compareString = (new IntegerDataType()).convertTo(new Integer(
                compareWith));
        
        out("Finding values bigger than " + compareWith + " (raw: "
                + compareString + ")");
        
        String expression = "/processdata/a[text() > " + compareString + "]";
        NodeList nodes = (NodeList) ProcessXml.evaluateXPath(new InputSource(
                _xmlFile), expression, XPathConstants.NODESET);
        for (int i = 0; i < nodes.getLength(); i++) {
            Element element = (Element) nodes.item(i);
            String varname = element.getAttribute(ProcessXml.ATTR_NAME);
            out("Found at var " + varname);
            
            assertTrue(varname.equals("aInteger") || varname.equals("aFloat"));
        }
        
        // expression = "/if:processdata/l/i[text() > " + compareString +
        // "]/parent::*"; // gives l nodes
        expression = "/processdata/l/i[text() > " + compareString + "]";
        nodes = (NodeList) ProcessXml.evaluateXPath(new InputSource(_xmlFile),
                expression, XPathConstants.NODESET);
        for (int i = 0; i < nodes.getLength(); i++) {
            Element item = (Element) nodes.item(i);
            int position = Integer.parseInt(item
                    .getAttribute(ProcessXml.ATTR_POSITION));
            String varname = ((Element) item.getParentNode())
                    .getAttribute(ProcessXml.ATTR_NAME);
            out("Found at position " + position + " of list " + varname);
            
            assertTrue(varname.equals("lInteger") || varname.equals("lFloat"));
        }
        
        finishTest("TEST EVALUATE XPATH INTEGER COMPARE");
    }
    
    @Test
    public void testSAXReader() {
        initTest("TEST SAX Reader");
        
        String xmlOut = _xmlFile.substring(0, _xmlFile.indexOf("."))
                + "SAX.xml";
        ProcessData process = _pxml.getProcessData();
        
        ProcessSAXReader saxReader = new ProcessSAXReader(process);
        
        File f = new File(xmlOut);
        try {
          ToXMLStream toStream = new ToXMLStream();
          toStream.setWriter(new FileWriter(f));
          toStream.setEncoding("UTF-8");
          saxReader.setContentHandler(toStream);
          saxReader.parse(new InputSource(ProcessXml.NS_PROCESSDATA));
//            Transformer transf = TransformerFactory.newInstance()
//                    .newTransformer();
//            transf.setOutputProperty(OutputKeys.METHOD, "xml");
//            transf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
//            // transf.setOutputProperty(OutputKeys.INDENT, "yes");
//            // transf.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
//            transf.transform(new SAXSource(saxReader, new InputSource(
//                    ProcessXml.NS_PROCESSDATA)), new StreamResult(f));
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Exception writing " + xmlOut + " file ");
        }
        out("Validating wrote file...");
        try {
            assertTrue(ProcessXml.validateXml(new FileInputStream(xmlOut)));
            out("file is valid xml");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fail("File " + xmlOut + " not found");
        }
        
        finishTest("TEST SAX Reader");
    }
    
}
