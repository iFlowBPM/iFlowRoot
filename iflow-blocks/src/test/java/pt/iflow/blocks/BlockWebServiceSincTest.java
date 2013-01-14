package pt.iflow.blocks;

import java.net.MalformedURLException;
import java.util.Map;

import mockit.Mockit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pt.iflow.api.blocks.Attribute;
import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;

public class BlockWebServiceSincTest extends AbstractBlockTest {

  private Block block;

  @Before
  public void setUp() throws Exception {
    super.setUp();
    Mockit.redefineMethods(BlockWebServiceSinc.class, MockClassLibrary.MockedBlockWebServiceSinc.class);

    // test object...
    this.block = new MockClassLibrary.MockedBlockWebServiceSinc();

    // set up test object with fake information...
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
    assertTrue(ports.length == 1);
    assertEquals(MockClassLibrary.JUNIT_PORT_IN, ports[0].getConnectedPortName());
  }

  @Test
  public void testGetEventPort() {
    assertNull(block.getEventPort());
  }

  @Test
  public void testGetOutPorts() {
    Port[] ports = block.getOutPorts(userInfo);
    assertTrue(ports.length == 3);
    assertEquals(MockClassLibrary.JUNIT_PORT_OK, ports[0].getConnectedPortName());
    assertEquals(MockClassLibrary.JUNIT_PORT_ERROR, ports[1].getConnectedPortName());
    assertEquals(MockClassLibrary.JUNIT_PORT_TIMEOUT, ports[2].getConnectedPortName());
  }

  @Test
  public void testBefore() {
    assertEquals("", block.before(userInfo, procData));
  }

  @Test
  public void testCanProceed() {
    assertTrue(block.canProceed(userInfo, procData));
  }

  @Test
  public void testAfter_portTimeout() {
    block.addAttribute(new Attribute(MockClassLibrary._sPROPS_PREFIX + MockClassLibrary._sTIMEOUT, "-1"));
    block.refreshCache(userInfo);
    assertEquals(MockClassLibrary.JUNIT_PORT_TIMEOUT, block.after(userInfo, procData).getConnectedPortName());
  }

  @Test
  public void testAfter_portOk() {
    block.addAttribute(new Attribute(MockClassLibrary._sPROPS_PREFIX + MockClassLibrary._sTIMEOUT, "1"));
    block.addAttribute(new Attribute(MockClassLibrary._sPROPS_PREFIX + MockClassLibrary._sRETRIES, "1"));

    block.addAttribute(new Attribute(MockClassLibrary._sOUTPUT_PREFIX + MockClassLibrary._sTIMEOUT, "1"));
    block.addAttribute(new Attribute(MockClassLibrary._sOUTPUT_PREFIX + MockClassLibrary._sRETRIES, "1"));

    block.addAttribute(new Attribute(MockClassLibrary._sINPUT_PREFIX + MockClassLibrary._sOPERATION, "isArray"));
    block.addAttribute(new Attribute(MockClassLibrary._sINPUT_PREFIX + MockClassLibrary._sRETRIES, "isNotArray"));
    block.addAttribute(new Attribute(MockClassLibrary._sINPUT_PREFIX + MockClassLibrary._sTIMEOUT, "1"));
    block.addAttribute(new Attribute(MockClassLibrary._sINPUT_PREFIX + MockClassLibrary._sRETRIES, "1"));
    block.refreshCache(userInfo);

    assertEquals(MockClassLibrary.JUNIT_PORT_OK, block.after(userInfo, procData).getConnectedPortName());
  }

  @Test
  public void testGetDescription() {
    block.addAttribute(new Attribute(MockClassLibrary._sPROPS_PREFIX + MockClassLibrary._sWSDL, MockClassLibrary.JUNIT_WSDL));
    block.refreshCache(userInfo);
    assertTrue(block.getDescription(userInfo, procData).contains(MockClassLibrary.JUNIT_WSDL));
  }

  @Test
  public void testGetResult() {
    assertTrue(block.getResult(userInfo, procData).contains(MockClassLibrary.JUNIT_WSDL));
  }

  @Test
  public void testGetUrl() {
    assertEquals("", block.getUrl(userInfo, procData));
  }

  private void setFakePorts() {
    Port port = new Port();
    port.setConnectedBlockId(MockClassLibrary.GENERAL_INT_RETURN);
    port.setName(MockClassLibrary.JUNIT_PORT);
    port.setConnectedPortName(MockClassLibrary.JUNIT_PORT_ERROR);
    ((BlockWebServiceSinc) block).portError = port;
    port = new Port();
    port.setConnectedBlockId(MockClassLibrary.GENERAL_INT_RETURN);
    port.setName(MockClassLibrary.JUNIT_PORT);
    port.setConnectedPortName(MockClassLibrary.JUNIT_PORT_EVENT);
    ((BlockWebServiceSinc) block).portEvent = port;
    port = new Port();
    port.setConnectedBlockId(MockClassLibrary.GENERAL_INT_RETURN);
    port.setName(MockClassLibrary.JUNIT_PORT);
    port.setConnectedPortName(MockClassLibrary.JUNIT_PORT_IN);
    ((BlockWebServiceSinc) block).portIn = port;
    port = new Port();
    port.setConnectedBlockId(MockClassLibrary.GENERAL_INT_RETURN);
    port.setName(MockClassLibrary.JUNIT_PORT);
    port.setConnectedPortName(MockClassLibrary.JUNIT_PORT_OK);
    ((BlockWebServiceSinc) block).portOk = port;
    port = new Port();
    port.setConnectedBlockId(MockClassLibrary.GENERAL_INT_RETURN);
    port.setName(MockClassLibrary.JUNIT_PORT);
    port.setConnectedPortName(MockClassLibrary.JUNIT_PORT_TIMEOUT);
    ((BlockWebServiceSinc) block).portTimeout = port;
  }

  private void setFakeAttributes() {
    block.addAttribute(new Attribute(MockClassLibrary._sPROPS_PREFIX + MockClassLibrary._sWSDL, MockClassLibrary.JUNIT_WSDL));
    block.addAttribute(new Attribute(MockClassLibrary._sPROPS_PREFIX + MockClassLibrary._sSERVICE, MockClassLibrary.JUNIT_SERVICE));
    block.addAttribute(new Attribute(MockClassLibrary._sPROPS_PREFIX + MockClassLibrary._sPORT, MockClassLibrary.JUNIT_PORT));
    block.addAttribute(new Attribute(MockClassLibrary._sPROPS_PREFIX + MockClassLibrary._sOPERATION,
        MockClassLibrary.JUNIT_OPERATION));
    block.addAttribute(new Attribute(MockClassLibrary._sPROPS_PREFIX + MockClassLibrary._sTIMEOUT, ""
        + MockClassLibrary.GENERAL_INT_RETURN));
    block.addAttribute(new Attribute(MockClassLibrary._sPROPS_PREFIX + MockClassLibrary._sRETRIES, ""
        + MockClassLibrary.GENERAL_INT_RETURN));
    block.refreshCache(userInfo);
  }
}
