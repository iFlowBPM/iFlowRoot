package pt.iflow.blocks;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import mockit.Mock;
import mockit.MockClass;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.core.ProcessManager;
import pt.iflow.api.core.Repository;
import pt.iflow.api.core.RepositoryFile;
import pt.iflow.api.errors.IErrorManager;
import pt.iflow.api.flows.Flow;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessHeader;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iknow.utils.DataSet;
import pt.iknow.utils.IDataSet;
import pt.iknow.utils.wsdl.WSDLUtils;

public class MockClassLibrary {
    
    public static final int GENERAL_INT_RETURN = -1;
    
    public static final String JUNIT_FILENAME = "JUnit.fileName";
    public static final String JUNIT_PORT = "[JUnit.Port]";
    public static final String JUNIT_OPERATION = "JUnit.Operation";
    public static final String JUNIT_SERVICE = "JUnit.Service";
    public static final String JUNIT_DESCRIPTION = "JUnit.Description";
    public static final String JUNIT_RESULT = "JUnit.Result";
    public static final String JUNIT_WSDL = "api.google.com_GoogleSearch.wsdl";
    public static final String JUNIT_USER = "JUnit.User";
    public static final String JUNIT_ORGANIZATION = "JUnit.Organization";
    
    public static final String JUNIT_PORT_IN = JUNIT_PORT + "In";
    public static final String JUNIT_PORT_OUT = JUNIT_PORT + "Out";
    public static final String JUNIT_PORT_OK = JUNIT_PORT + "Ok";
    public static final String JUNIT_PORT_ERROR = JUNIT_PORT + "Error";
    public static final String JUNIT_PORT_EVENT = JUNIT_PORT + "Event";
    public static final String JUNIT_PORT_TIMEOUT = JUNIT_PORT + "TimeOut";
    public static final String JUNIT_PORT_SUBPROC = JUNIT_PORT + "SubProc";
    public static final String JUNIT_PORT_OUT_THREAD = JUNIT_PORT + "OutThread";
    public static final String JUNIT_PORT_IN_THREAD = JUNIT_PORT + "InThread";
    
    public static final String _sPROPS_PREFIX = "P";
    public static final String _sINPUT_PREFIX = "I";
    public static final String _sOUTPUT_PREFIX = "O";
    
    public static final String _sWSDL = "WSDL";
    public static final String _sSERVICE = "SERVICE";
    public static final String _sPORT = "PORT";
    public static final String _sOPERATION = "OPERATION";
    public static final String _sTIMEOUT = "TIMEOUT";
    public static final String _sRETRIES = "RETRIES";
    
    @MockClass(realClass = pt.iflow.blocks.BlockWebServiceSinc.class)
    public static class MockedBlockWebServiceSinc extends BlockWebServiceSinc {
        public MockedBlockWebServiceSinc() {
            super(GENERAL_INT_RETURN, GENERAL_INT_RETURN, GENERAL_INT_RETURN,
                    JUNIT_FILENAME);
        }
        
        @Override
        @Mock
        protected WSDLUtils setWSDLUtils(InputStream aisWsdl, String asUrl)
                throws Exception {
            Mockery context = new Mockery() {
                {
                    setImposteriser(ClassImposteriser.INSTANCE);
                }
            };
            final WSDLUtils wu = context.mock(WSDLUtils.class);
            context.checking(new Expectations() {
                {
                    allowing(wu).callService(with(any(String.class)),
                            with(any(String.class)), with(any(String.class)),
                            with(-1), with(any(HashMap.class)));
                    will(returnValue(null));
                    allowing(wu).callService(with(any(String.class)),
                            with(any(String.class)), with(any(String.class)),
                            with(any(Integer.class)), with(any(HashMap.class)));
                    will(returnValue(new HashMap<String, Object>()));
                }
            });
            return wu;
        }
        
        @Override
        @Mock
        protected Repository getRepBean() {
            Mockery context = new Mockery() {
                {
                    setImposteriser(ClassImposteriser.INSTANCE);
                }
            };
            final Repository rep = context.mock(Repository.class);
            final RepositoryFile repFile = context.mock(RepositoryFile.class);
            final InputStream is = context.mock(InputStream.class);
            try {
                context.checking(new Expectations() {
                    {
                        // Repository
                        allowing(rep).getWebService(
                                with(any(UserInfoInterface.class)),
                                with(any(String.class)));
                        will(returnValue(repFile));
                        
                        // RepositoryFile
                        allowing(repFile).getResourceAsStream();
                        will(returnValue(is));
                        allowing(repFile).getResouceData();
                        will(returnValue(new byte[] {}));
                        
                        // InputStream
                        allowing(is).close();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            return rep;
        }
    }
    
    @MockClass(realClass = pt.iflow.blocks.BlockSubFlowOut.class)
    public static class MockedBlockSubFlowOut extends BlockSubFlowOut {
        
        public MockedBlockSubFlowOut() {
            super(MockClassLibrary.GENERAL_INT_RETURN,
                    MockClassLibrary.GENERAL_INT_RETURN,
                    MockClassLibrary.GENERAL_INT_RETURN,
                    MockClassLibrary.JUNIT_FILENAME);
        }
        
        @Override
        @Mock
        protected IErrorManager getErrorManagerBean() {
            Mockery context = new Mockery() {
                {
                    setImposteriser(ClassImposteriser.INSTANCE);
                }
            };
            final IErrorManager iem = context.mock(IErrorManager.class);
            context.checking(new Expectations() {
                {
                    // IErrorManager
                    allowing(iem).register(with(any(String.class)),
                            with(any(Integer.class)), with(any(String.class)));
                    allowing(iem).init(with(any(UserInfoInterface.class)),
                            with(any(ProcessData.class)),
                            with(any(Object.class)), with(any(String.class)));
                    allowing(iem).fire(with(any(String.class)));
                    allowing(iem).close(with(any(String.class)));
                }
            });
            return iem;
        }
        
        @Override
        @Mock
        protected ProcessManager getProcessManagerBean() {
            Mockery context = new Mockery() {
                {
                    setImposteriser(ClassImposteriser.INSTANCE);
                }
            };
            final ProcessManager pm = context.mock(ProcessManager.class);
            context.checking(new Expectations() {
                {
                    // ProcessManager
                    allowing(pm).getSubPidsInBlock(
                            with(any(UserInfoInterface.class)),
                            with(any(ProcessData.class)),
                            with(any(Block.class)));
                    allowing(pm).getProcessData(
                            with(any(UserInfoInterface.class)),
                            with(any(ProcessHeader.class)),
                            with(any(Integer.class)));
                    will(returnValue(AbstractBlockTest.procData));
                }
            });
            return pm;
        }
        
        @Override
        @Mock
        protected Flow getFlowBean() {
            Mockery context = new Mockery() {
                {
                    setImposteriser(ClassImposteriser.INSTANCE);
                }
            };
            final Flow flow = context.mock(Flow.class);
            context.checking(new Expectations() {
                {
                    // Flow
                    allowing(flow).endProc(with(any(UserInfoInterface.class)),
                            with(any(ProcessData.class)));
                }
            });
            return flow;
        }
    }
    
    @MockClass(realClass = pt.iknow.utils.DataSet.class)
    public static class MockedDataSet extends DataSet {
        private static final long serialVersionUID = 1L;
        
        public MockedDataSet() {
            allowGetTempData = false;
        }
        
        private boolean allowGetTempData;
        
        public void allowGetTempData(boolean nAllowGetTempData) {
            this.allowGetTempData = nAllowGetTempData;
        }
        
        @Override
        @Mock
        public boolean isArray(String arg0) {
            return arg0.equalsIgnoreCase("isArray");
        }
        
        @Override
        @Mock
        public HashSet<?> getConstList() {
            return null;
        }
        
        @Override
        @Mock
        public boolean appendError(String arg0) {
            return false;
        }
        
        @Override
        @Mock
        public boolean fieldChanged(String arg0) {
            
            return false;
        }
        
        @Override
        @Mock
        public Iterator<?> getArrayKeys() {
            
            return null;
        }
        
        @Override
        @Mock
        public Map<?, ?> getCatalogueVars() {
            
            return null;
        }
        
        @Override
        @Mock
        public double getCurrencyValue(String arg0) {
            
            return 0;
        }
        
        @Override
        @Mock
        public double getCurrencyValue(String arg0, int arg1) {
            
            return 0;
        }
        
        @Override
        @Mock
        public Date getDate(String arg0) {
            
            return null;
        }
        
        @Override
        @Mock
        public double getDouble(String arg0) {
            
            return 0;
        }
        
        @Override
        @Mock
        public double getDouble(String arg0, int arg1) {
            
            return 0;
        }
        
        @Override
        @Mock
        public double[] getDoubleArray(String arg0) {
            
            return null;
        }
        
        @Override
        @Mock
        public ArrayList<?> getDoubleChangedKeys() {
            
            return null;
        }
        
        @Override
        @Mock
        public Iterator<?> getDoubleKeys() {
            
            return null;
        }
        
        @Override
        @Mock
        public String getError() {
            
            return null;
        }
        
        @Override
        @Mock
        public Iterator<?> getKeys() {
            
            return null;
        }
        
        @Override
        @Mock
        public int getLength(String arg0) {
            
            return 0;
        }
        
        @Override
        @Mock
        public String getString(String arg0) {
            
            return null;
        }
        
        @Override
        @Mock
        public String getString(String arg0, int arg1) {
            
            return null;
        }
        
        @Override
        @Mock
        public String[] getStringArray(String arg0) {
            
            return null;
        }
        
        @Override
        @Mock
        public ArrayList<?> getStringChangedKeys() {
            
            return null;
        }
        
        @Override
        @Mock
        public Iterator<?> getStringKeys() {
            
            return null;
        }
        
        @Override
        @Mock
        public String getTempData(String arg0) {
            if (this.allowGetTempData) {
                return "no";
            } else {
                return "yes";
            }
        }
        
        @Override
        @Mock
        public Iterator<?> getTempDataKeys() {
            
            return null;
        }
        
        @Override
        @Mock
        public String getType(String arg0) {
            
            return null;
        }
        
        @Override
        @Mock
        public String getType(String arg0, int arg1) {
            
            return null;
        }
        
        @Override
        @Mock
        public boolean hasError() {
            
            return false;
        }
        
        @Override
        @Mock
        public boolean hasVariable(String arg0) {
            
            return false;
        }
        
        @Override
        @Mock
        public String isChecked(String arg0, double arg1) {
            
            return null;
        }
        
        @Override
        @Mock
        public boolean isConst(String arg0) {
            
            return false;
        }
        
        @Override
        @Mock
        public String isSelected(String arg0, String arg1) {
            
            return null;
        }
        
        @Override
        @Mock
        public String isSelected(String arg0, double arg1) {
            
            return null;
        }
        
        @Override
        @Mock
        public void markChangedFields(boolean arg0) {
            
        }
        
        @Override
        @Mock
        public void removeCurrencyValue(String arg0) {
            
        }
        
        @Override
        @Mock
        public void removeCurrencyValue(String arg0, int arg1) {
            
        }
        
        @Override
        @Mock
        public void removeDouble(String arg0) {
            
        }
        
        @Override
        @Mock
        public void removeDouble(String arg0, int arg1) {
            
        }
        
        @Override
        @Mock
        public void removeList() {
            
        }
        
        @Override
        @Mock
        public void removeListItem(int arg0) {
            
        }
        
        @Override
        @Mock
        public void removeListVar(String arg0) {
            
        }
        
        @Override
        @Mock
        public void removeListVar(String arg0, boolean arg1) {
            
        }
        
        @Override
        @Mock
        public void removeString(String arg0) {
            
        }
        
        @Override
        @Mock
        public void removeString(String arg0, int arg1) {
            
        }
        
        @Override
        @Mock
        public void setConst(String arg0, String arg1) {
            
        }
        
        @Override
        @Mock
        public void setConst(String arg0, String arg1, int arg2) {
            
        }
        
        @Override
        @Mock
        public void setCurrencyType(int arg0) {
            
        }
        
        @Override
        @Mock
        public boolean setCurrencyValue(String arg0, double arg1) {
            
            return false;
        }
        
        @Override
        @Mock
        public boolean setCurrencyValue(String arg0, double arg1, int arg2) {
            
            return false;
        }
        
        @Override
        @Mock
        public boolean setDate(String arg0, Date arg1) {
            
            return false;
        }
        
        @Override
        @Mock
        public boolean setDouble(String arg0, double arg1) {
            
            return false;
        }
        
        @Override
        @Mock
        public boolean setDouble(String arg0, double arg1, int arg2) {
            
            return false;
        }
        
        @Override
        @Mock
        public boolean setDoubleArray(String arg0, double[] arg1) {
            
            return false;
        }
        
        @Override
        @Mock
        public boolean setError(String arg0) {
            
            return false;
        }
        
        @Override
        @Mock
        public void setListItem(IDataSet arg0, int arg1) {
            
        }
        
        @Override
        @Mock
        public boolean setString(String arg0, String arg1) {
            
            return false;
        }
        
        @Override
        @Mock
        public boolean setString(String arg0, String arg1, int arg2) {
            
            return false;
        }
        
        @Override
        @Mock
        public boolean setStringArray(String arg0, String[] arg1) {
            
            return false;
        }
        
        @Override
        @Mock
        public void setTempData(String arg0, String arg1) {
            
        }
        
        @Override
        @Mock
        public void unMarkChangeField(String arg0) {
            
        }
        
        @Override
        @Mock
        public void unMarkChangeFields() {
            
        }
        
        @Override
        @Mock
        public void update(IDataSet arg0) {
            
        }
        
        @Override
        @Mock
        public void zap() {
            
        }
        
        @Override
        @Mock
        public String getCatalogueVarDescr(String var) {
            return null;
        }
        
        @Override
        @Mock
        public void setCatalogueVars(Map hmCatVars, Map hmCatValues,
                Map hmCatDesc) {
            
        }
        
    }
}
