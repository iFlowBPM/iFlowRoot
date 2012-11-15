package pt.iflow.api.processtype;

import java.text.NumberFormat;

import junit.framework.TestCase;

public class DataTypesTest  extends TestCase {

  @Override
  protected void setUp() throws Exception {
    // TODO Auto-generated method stub
    super.setUp();
  }

  @Override
  protected void tearDown() throws Exception {
    // TODO Auto-generated method stub
    super.tearDown();
  }

  
  public void testGetDataType() {
    DataTypeEnum expected = DataTypeEnum.Float;
    DataTypeEnum actual = DataTypeEnum.valueOf("Float");
    System.out.println("Actual: "+actual);
    assertEquals(expected, actual);
  }

  public void testUnknownDataType() {
    try {
      DataTypeEnum.valueOf("Unknown");
      fail("Should have thrown an exception...");
    } catch(IllegalArgumentException actual) {
      System.out.println("Actual: "+actual);
    } catch(Throwable t) {
      fail("I was not expecting this exception");
    }
  }

  public void testGetDataType2() {
    DataTypeEnum expected = DataTypeEnum.Date;
    DataTypeEnum actual = DataTypeEnum.getDataType("Date");
    System.out.println("Actual: "+actual);
    assertEquals(expected, actual);
  }

  public void testUnknownDataType2() {
    DataTypeEnum actual = DataTypeEnum.getDataType("Unknown");
    System.out.println("Actual: "+actual);
    assertEquals("Unknown data type found.", DataTypeEnum.Text, actual);
  }

  public void testNewDataTypeInstance() {
    ProcessDataType actual = DataTypeEnum.Integer.newDataTypeInstance();
    System.out.println("Actual: "+actual);
    assertNotNull("Instance could not be created.", actual);
  }
  
  public void testNewDataTypeInstance2() {
    NumberFormat argument = NumberFormat.getInstance();
    ProcessDataType actual = DataTypeEnum.Integer.newDataTypeInstance(argument);
    System.out.println("Actual: "+actual);
    assertNotNull("Instance could not be created.", actual);
  }
  
  public void testNewDataTypeInstance3() {
    ProcessDataType actual = DataTypeEnum.Integer.newDataTypeInstance(new Object());
    System.out.println("Actual: "+actual);
    assertNotNull("Instance could not be created.", actual);
  }
}
