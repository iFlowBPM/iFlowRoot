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

import mockit.Mockit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pt.iflow.api.blocks.Attribute;
import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;

public class BlockSubFlowOutTest extends AbstractBlockTest {
    
    private Block block;
    
    @Before
    public void setUp() throws Exception {
        super.setUp();
        Mockit.redefineMethods(BlockSubFlowOut.class,
                MockClassLibrary.MockedBlockSubFlowOut.class);
        
        block = new MockClassLibrary.MockedBlockSubFlowOut();
        hasInteraction = false;
        bProcInDBRequired = true;
        
        setFakePorts();
        setFakeAttributes();
    }
    
    @After
    public void tearDown() throws Exception {
        super.tearDown();
        block = null;
    }
    
    @Test
    public void testProperties() {
        assertTrue(block.isEvent() == this.isEvent);
        assertTrue(block.hasEvent() == this.hasEvent);
        assertTrue(block.hasInteraction() == this.hasInteraction);
        assertTrue(block.isCodeGenerator() == this.isCodeGenerator);
        assertTrue(block.isJSPGenerator() == this.isJSPGenerator);
        assertTrue(block.isOnceExec() == this.isOnceExec);
        assertTrue(block.isProcInDBRequired() == this.bProcInDBRequired);
    }
    
    @Test
    public void testGetInPorts() {
        Port[] ports = block.getInPorts(userInfo);
        assertTrue(ports.length == 2);
        assertEquals(MockClassLibrary.JUNIT_PORT_IN, ports[0]
                .getConnectedPortName());
        assertEquals(MockClassLibrary.JUNIT_PORT_IN_THREAD, ports[1]
                .getConnectedPortName());
    }
    
    @Test
    public void testGetEventPort() {
        assertNull(block.getEventPort());
    }
    
    @Test
    public void testGetOutPorts() {
        Port[] ports = block.getOutPorts(userInfo);
        assertTrue(ports.length == 2);
        assertEquals(MockClassLibrary.JUNIT_PORT_OUT, ports[0]
                .getConnectedPortName());
        assertEquals(MockClassLibrary.JUNIT_PORT_ERROR, ports[1]
                .getConnectedPortName());
    }
    
    @Test
    public void testBefore() {
        assertEquals("", block.before(userInfo, procData));
    }
    
    @Test
    public void testCanProceed() {
        assertTrue(!block.canProceed(userInfo, procData));
    }
    
    @Test
    public void testAfter() {
        assertEquals(MockClassLibrary.JUNIT_PORT_ERROR, block.after(userInfo,
                procData).getConnectedPortName());
    }
    
    @Test
    public void testGetDescription() {
        assertEquals("Sa&iacute; SubFluxo " + block.getSubFlowFilename(), block
                .getDescription(userInfo, procData));
    }
    
    @Test
    public void testGetResult() {
        assertEquals("Sa&iacute; SubFluxo " + block.getSubFlowFilename()
                + " Conclu&iacute;da.", block.getResult(userInfo, procData));
    }
    
    @Test
    public void testGetUrl() {
        assertEquals("Forward/forward.jsp?flowid="
                + MockClassLibrary.GENERAL_INT_RETURN + "&pid="
                + MockClassLibrary.GENERAL_INT_RETURN + "&subpid="
                + MockClassLibrary.GENERAL_INT_RETURN, block.getUrl(userInfo,
                procData));
    }
    
    private void setFakePorts() {
        Port port = new Port();
        port.setConnectedBlockId(MockClassLibrary.GENERAL_INT_RETURN);
        port.setName(MockClassLibrary.JUNIT_PORT);
        port.setConnectedPortName(MockClassLibrary.JUNIT_PORT_ERROR);
        ((BlockSubFlowOut) block).portError = port;
        port = new Port();
        port.setConnectedBlockId(MockClassLibrary.GENERAL_INT_RETURN);
        port.setName(MockClassLibrary.JUNIT_PORT);
        port.setConnectedPortName(MockClassLibrary.JUNIT_PORT_EVENT);
        ((BlockSubFlowOut) block).portEvent = port;
        port = new Port();
        port.setConnectedBlockId(MockClassLibrary.GENERAL_INT_RETURN);
        port.setName(MockClassLibrary.JUNIT_PORT);
        port.setConnectedPortName(MockClassLibrary.JUNIT_PORT_IN);
        ((BlockSubFlowOut) block).portIn = port;
        port = new Port();
        port.setConnectedBlockId(MockClassLibrary.GENERAL_INT_RETURN);
        port.setName(MockClassLibrary.JUNIT_PORT);
        port.setConnectedPortName(MockClassLibrary.JUNIT_PORT_OUT);
        ((BlockSubFlowOut) block).portOut = port;
        port = new Port();
        port.setConnectedBlockId(MockClassLibrary.GENERAL_INT_RETURN);
        port.setName(MockClassLibrary.JUNIT_PORT);
        port.setConnectedPortName(MockClassLibrary.JUNIT_PORT_IN_THREAD);
        ((BlockSubFlowOut) block).portInThread = port;
    }
    
    private void setFakeAttributes() {
        block.addAttribute(new Attribute(MockClassLibrary._sPROPS_PREFIX
                + MockClassLibrary._sWSDL, MockClassLibrary.JUNIT_WSDL));
        block.addAttribute(new Attribute(MockClassLibrary._sPROPS_PREFIX
                + MockClassLibrary._sSERVICE, MockClassLibrary.JUNIT_SERVICE));
        block.addAttribute(new Attribute(MockClassLibrary._sPROPS_PREFIX
                + MockClassLibrary._sPORT, MockClassLibrary.JUNIT_PORT));
        block.addAttribute(new Attribute(MockClassLibrary._sPROPS_PREFIX
                + MockClassLibrary._sOPERATION,
                MockClassLibrary.JUNIT_OPERATION));
        block.addAttribute(new Attribute(MockClassLibrary._sPROPS_PREFIX
                + MockClassLibrary._sTIMEOUT, ""
                + MockClassLibrary.GENERAL_INT_RETURN));
        block.addAttribute(new Attribute(MockClassLibrary._sPROPS_PREFIX
                + MockClassLibrary._sRETRIES, ""
                + MockClassLibrary.GENERAL_INT_RETURN));
        block.refreshCache(userInfo);
    }
}
