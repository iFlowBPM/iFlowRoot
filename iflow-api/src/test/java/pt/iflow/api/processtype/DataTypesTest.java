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
