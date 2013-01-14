package pt.iknow.iflow.web.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for pt.iknow.iflow.web.test");
		//$JUnit-BEGIN$
		suite.addTestSuite(UserManagerBeanTestCase.class);
		//$JUnit-END$
		return suite;
	}

}
