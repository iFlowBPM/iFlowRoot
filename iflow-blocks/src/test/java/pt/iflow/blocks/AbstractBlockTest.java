package pt.iflow.blocks;

import junit.framework.TestCase;
import mockit.Mockit;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;

import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iknow.utils.DataSet;

public abstract class AbstractBlockTest extends TestCase {
    
    Mockery context = new Mockery() {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    
    protected boolean isEvent;
    protected boolean hasEvent;
    protected boolean hasInteraction;
    protected boolean isCodeGenerator;
    protected boolean isJSPGenerator;
    protected boolean isOnceExec;
    protected boolean bProcInDBRequired;
    
    protected static UserInfoInterface userInfo;
    protected static ProcessData procData;
    protected static DataSet dataSet;
    
    @Before
    public void setUp() throws Exception {
        super.setUp();
        ClassLoader.getSystemClassLoader().loadClass(
                "pt.iflow.blocks.MockInitialContextFactory");
        Logger.initLogger();
        
        // prepare mocks...
        userInfo = context.mock(UserInfoInterface.class);
        procData = context.mock(ProcessData.class);
        dataSet = new MockClassLibrary.MockedDataSet();
        
        isEvent = false;
        hasEvent = false;
        hasInteraction = false;
        isCodeGenerator = false;
        isJSPGenerator = false;
        isOnceExec = false;
        bProcInDBRequired = false;
        
        context.checking(new Expectations() {
            {
                // UserInfoInterface
                allowing(userInfo).getUserId();
                will(returnValue("" + MockClassLibrary.GENERAL_INT_RETURN));
                allowing(userInfo).getUtilizador();
                will(returnValue(MockClassLibrary.JUNIT_USER));
                allowing(userInfo).getOrganization();
                will(returnValue(MockClassLibrary.JUNIT_ORGANIZATION));
                
                // ProcessData
                allowing(procData).getFlowId();
                will(returnValue(MockClassLibrary.GENERAL_INT_RETURN));
                allowing(procData).getPid();
                will(returnValue(MockClassLibrary.GENERAL_INT_RETURN));
                allowing(procData).getSubPid();
                will(returnValue(MockClassLibrary.GENERAL_INT_RETURN));
                allowing(procData).getDataSet();
                will(returnValue(dataSet));
                allowing(procData).getFormatted(with(any(String.class)));
                will(returnValue("yes"));
                allowing(procData).isListVar(with(any(String.class)));
                will(returnValue(false));
                allowing(procData).transform(userInfo, with(any(String.class)));
                allowing(procData).clear(with(any(String.class)));
                allowing(procData).parseAndSet(with(any(String.class)),
                        with(any(String.class)));
                allowing(procData).getPNumber();
                will(returnValue("" + MockClassLibrary.GENERAL_INT_RETURN));
            }
        });
    }
    
    @After
    public void tearDown() throws Exception {
        super.tearDown();
        Mockit.restoreAllOriginalDefinitions();
        
        userInfo = null;
        procData = null;
        dataSet = null;
        
        isEvent = false;
        hasEvent = false;
        hasInteraction = false;
        isCodeGenerator = false;
        isJSPGenerator = false;
        isOnceExec = false;
        bProcInDBRequired = false;
    }
}
