package pt.iknow.iflow.blocks.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for pt.iknow.iflow.blocks.test");
		//$JUnit-BEGIN$
		suite.addTestSuite(TestCaseBlock.class);
		//$JUnit-END$
		return suite;
	}

}
