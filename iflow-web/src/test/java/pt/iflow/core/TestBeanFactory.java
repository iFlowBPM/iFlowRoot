package pt.iflow.core;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.documents.Documents;
import pt.iflow.api.utils.Logger;

public class TestBeanFactory extends TestCase {
    
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }
    
    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }
    
    @Test
    public void testGetDocumentsBean() {
        Documents docs = BeanFactory.getDocumentsBean();
        Logger.debug("", "", "", docs.toString());
    }
    
}
