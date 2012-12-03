/*****************************************************************************************
    Infosistema iFlow - workflow and BPM platform
    Copyright(C) 2002-2012 Infosistema, S.A.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

    www.infosistema.com
    iflow@infosistema.com
    Av. Jose Gomes Ferreira, 11 3rd floor, s.34
    Miraflores
    1495-139 Alges Portugal
****************************************************************************************/
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
