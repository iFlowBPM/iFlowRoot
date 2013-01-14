package pt.iflow.api.processdata;

import java.io.FileReader;

import junit.framework.TestCase;
import pt.iflow.api.core.ProcessCatalogue;
import pt.iflow.api.core.ProcessCatalogueImpl;
import pt.iflow.api.processtype.DateDataType;
import pt.iflow.api.processtype.FloatDataType;
import pt.iflow.api.processtype.IntegerDataType;
import pt.iflow.api.processtype.TextDataType;

public abstract class ProcessTestCase extends TestCase {
    private static final boolean DEBUG_OUT = false;
    
    protected ProcessCatalogue _catalogue;
    ProcessXml _pxml;
    String _xmlFile = "src/test/resources/ProcessData.xml";
    ProcessData _pd;
    
    public void setUp() throws Exception {
        super.setUp();
        
        ProcessCatalogueImpl catalogue = new ProcessCatalogueImpl();
        catalogue.setDataType("aText", new TextDataType());
        catalogue.setDataType("aInteger", new IntegerDataType());
        catalogue.setDataType("aFloat", new FloatDataType());
        catalogue.setDataType("aDate", new DateDataType());
        
        catalogue.setListDataType("lText", new TextDataType());
        catalogue.setListDataType("lInteger", new IntegerDataType());
        catalogue.setListDataType("lFloat", new FloatDataType());
        catalogue.setListDataType("lDate", new DateDataType());
        
        _catalogue = catalogue;
        _pxml = new ProcessXml(catalogue, new FileReader(_xmlFile));
        
        //out("Finished setup:\n - process catalogue:\n" + catalogue.toString() + "\n - process data:\n" + _pxml.getProcessData().toString(true));
    }
    
    public void tearDown() throws Exception {
        super.tearDown();
        _pd = null;
    }
    
    void out(String msg) {
        if (DEBUG_OUT)
            System.out.println(msg);
    }
    
    void initTest(String test) {
        out("\n =============== " + test + " =============== \n");
    }
    
    void finishTest(String test) {
        out("\n" + test + " - OK \n");
    }
}
