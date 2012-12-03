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
package pt.iflow.api.processdata;

import java.io.FileReader;
import java.lang.reflect.Array;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.ProcessCatalogue;
import pt.iflow.api.core.ProcessCatalogueImpl;
import pt.iflow.api.processtype.DateDataType;
import pt.iflow.api.processtype.FloatDataType;
import pt.iflow.api.processtype.IntegerDataType;
import pt.iflow.api.processtype.TextDataType;
import pt.iflow.api.utils.UserInfoInterface;

public class ProcessDataNameSpaceTest extends ProcessTestCase {
    
    ProcessCatalogue _catalogue;
    ProcessXml _pxml;
    String _xmlFile = "src/test/resources/ProcessData.xml";
    ProcessData _pd;
    UserInfoInterface userInfo;
    
    @Before
    public void setUp() throws Exception {
        super.setUp();
        
        userInfo = BeanFactory.getUserInfoFactory().newGuestUserInfo();
        
        ProcessCatalogueImpl catalogue = new ProcessCatalogueImpl();
        catalogue.setDataType("aText", new TextDataType());
        catalogue.setDataType("aInteger", new IntegerDataType());
        catalogue.setDataType("aFloat", new FloatDataType());
        catalogue.setDataType("aDate", new DateDataType());
        
        catalogue.setListDataType("lText", new TextDataType());
        catalogue.setListDataType("lInteger", new IntegerDataType());
        catalogue.setListDataType("lFloat", new FloatDataType());
        catalogue.setListDataType("lDate", new DateDataType());
        
        this._catalogue = catalogue;
        _pxml = new ProcessXml(catalogue, new FileReader(_xmlFile));
        
        _pd = _pxml.getProcessData();
        //_pd.resetEvaluator();
    }
    
    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }
    
    @Test
    public void testGetSet1() {
        initTest("TEST GET SET 1");
        
        try {
            System.out.println(_pd.evalAndUpdate(userInfo, "_aText"));
            assertEquals("Some text", _pd.evalAndUpdate(userInfo, "_aText"));
            
            _pd.evalAndUpdate(userInfo, "_aText=\"Other Text\";");
            
            System.out.println(_pd.get("aText").getValue());
            assertEquals("Other Text", _pd.get("aText").getValue());
            
        } catch (Throwable t) {
            t.printStackTrace();
            fail(t.getLocalizedMessage());
        }
        finishTest("TEST GET SET 1");
    }
    
    @Test
    public void testArrayGetSet2() {
        initTest("TEST GET SET 2");
        
        try {
            Object obj = _pd.evalAndUpdate(userInfo, "_lText");
            System.out.println(Array.get(obj, 0));
            assertEquals("Text value 0", Array.get(obj, 0));
            
            _pd.evalAndUpdate(userInfo, "_lText[1]=\"Text value 1\";");
            
            System.out.println(_pd.getList("lText").getItemValue(1));
            assertEquals("Text value 1", _pd.getList("lText").getItemValue(1));
            
        } catch (Throwable t) {
            t.printStackTrace();
            fail(t.getLocalizedMessage());
        }
        finishTest("TEST GET SET 2");
    }
    
    @Test
    public void testEvalBshNameSpace3() {
        initTest("TEST EVAL 3");
        
        try {
            float a, b;
            a = ((Number) _pd.get("aInteger").getValue()).floatValue();
            b = ((Number) _pd.get("aFloat").getValue()).floatValue();
            System.out.println("a=" + a + "; b=" + b + "; a+b=" + (a + b));
            Object obj = _pd.eval(userInfo, "aInteger+aFloat");
            System.out.println(obj);
            assertEquals(a + b, ((Number) obj).floatValue(), 0.0f);
        } catch (Throwable t) {
            t.printStackTrace();
            fail(t.getLocalizedMessage());
        }
        
        finishTest("TEST EVAL 3");
    }
    
    @Test
    public void testEvalBshNameSpace4() {
        initTest("TEST EVAL 4");
        
        try {
            float a, b;
            a = ((Number) _pd.get("aInteger").getValue()).floatValue();
            b = ((Number) _pd.get("aFloat").getValue()).floatValue();
            System.out
                    .println("a=" + a + "; b=" + b + "; a+b=" + (456 * a + b));
            Object obj = _pd.eval(userInfo, "int xpto = 456;\n xpto*aInteger+aFloat");
            System.out.println(obj);
            assertEquals(456 * a + b, ((Number) obj).floatValue(), 0.0f);
        } catch (Throwable t) {
            t.printStackTrace();
            fail(t.getLocalizedMessage());
        }
        
        finishTest("TEST EVAL 4");
    }
    
    @Test
    public void testEvalBshNameSpace5() {
        initTest("TEST EVAL 4");
        
        try {
            float a, b;
            int xpto = 123;
            a = ((Number) _pd.get("aInteger").getValue()).floatValue();
            b = ((Number) _pd.get("aFloat").getValue()).floatValue();
            System.out.println("a=" + a + "; b=" + b + "; a+b="
                    + (xpto * a + b));
            Object obj = _pd
                    .eval(userInfo, "import org.apache.commons.lang.StringUtils;\n"
                            + "String xpto=\""
                            + xpto
                            + "\";\n"
                            + "int n=0;\n"
                            + "if(StringUtils.isNotEmpty(xpto))\n"
                            + "  n = Integer.parseInt(xpto);\n"
                            + "System.out.println(\"xpto=\"+xpto+\"; n=\"+n);\n"
                            + "double gg = n*aInteger+aFloat;\n"
                            + "System.out.println(\"gg=\"+gg);\n"
                            + "return gg;\n");
            System.out.println(obj);
            assertEquals(xpto * a + b, ((Number) obj).floatValue(), 0.0f);
        } catch (Throwable t) {
            t.printStackTrace();
            fail(t.getLocalizedMessage());
        }
        
        finishTest("TEST EVAL 4");
    }
    
}
