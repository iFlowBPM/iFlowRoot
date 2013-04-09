package pt.iknow.iflow.common.test;

import pt.iflow.api.processdata.ProcessDataTest;
import pt.iflow.api.processdata.ProcessXmlTest;
import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for pt.iknow.iflow.common.test");
		//$JUnit-BEGIN$
		suite.addTestSuite(ProcessDataTest.class);
		suite.addTestSuite(ProcessXmlTest.class);
		//$JUnit-END$
		return suite;
	}

}
