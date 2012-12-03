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

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ProcessDataTest extends ProcessTestCase {
    
    @Before
    public void setUp() throws Exception {
        super.setUp();
        
        _pd = new ProcessData(_catalogue);
        out("Finished setup:\n - process catalogue:\n" + _catalogue.toString());
    }
    
    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }
    
    @Test
    public void testSetGet() {
        
        initTest("TEST SET GET");
        
        // TEXT
        String testVar = "aText";
        Object testValue = "Text value";
        ProcessSimpleVariable var = _pd.get(testVar);
        var.setValue(testValue);
        out(testVar + " value=" + var.format());
        assertEquals(var.getValue(), testValue);
        
        // Integer
        testVar = "aInteger";
        testValue = new Integer(10);
        var = _pd.get(testVar);
        var.setValue(testValue);
        out(testVar + " value=" + var.format());
        assertEquals(var.getValue(), testValue);
        
        // Float
        testVar = "aFloat";
        testValue = new Float(33.5);
        var = _pd.get(testVar);
        var.setValue(testValue);
        out(testVar + " value=" + var.format());
        assertEquals(var.getValue(), testValue);
        
        // Date
        testVar = "aDate";
        testValue = new Date();
        var = _pd.get(testVar);
        var.setValue(testValue);
        out(testVar + " value=" + var.format());
        assertEquals(var.getValue(), testValue);
        
        out("result:\n" + _pd.toString(true));
        
        finishTest("TEST SET GET");
    }
    
    @Test
    public void testSetGetList() {
        
        initTest("TEST SET GET LIST");
        
        int listsSize = 4;
        
        // TEXT
        String testVar = "lText";
        Object testValuePrefix = "Text value";
        Object testValue = null;
        ProcessListVariable var = _pd.getList(testVar);
        for (int i = 0; i < listsSize; i++) {
            if (i == 2) {
                var.addNewItem(null);
                continue;
            }
            testValue = (String) testValuePrefix + i;
            var.addNewItem(testValue);
        }
        out(testVar + " size=" + var.size());
        assertEquals(var.size(), listsSize);
        assertNull(var.getItem(2));
        assertEquals(testValue, var.getItemValue(listsSize - 1));
        
        // INTEGER
        testVar = "lInteger";
        testValue = null;
        var = _pd.getList(testVar);
        for (int i = 0; i < listsSize; i++) {
            if (i == 2) {
                var.addNewItem(null);
                continue;
            }
            testValue = new Integer(10 + i);
            var.addNewItem(testValue);
        }
        out(testVar + " size=" + var.size());
        assertEquals(var.size(), listsSize);
        assertNull(var.getItem(2));
        assertEquals(testValue, var.getItemValue(listsSize - 1));
        
        // FLOAT
        testVar = "lFloat";
        testValue = null;
        var = _pd.getList(testVar);
        for (int i = 0; i < listsSize; i++) {
            if (i == 2) {
                var.addNewItem(null);
                continue;
            }
            testValue = new Float((double) 33 + ((double) i) / 2);
            var.addNewItem(testValue);
        }
        out(testVar + " size=" + var.size());
        assertEquals(var.size(), listsSize);
        assertNull(var.getItem(2));
        assertEquals(testValue, var.getItemValue(listsSize - 1));
        
        // DATE
        testVar = "lDate";
        testValue = null;
        var = _pd.getList(testVar);
        for (int i = 0; i < listsSize; i++) {
            if (i == 2) {
                var.addNewItem(null);
                continue;
            }
            Calendar cal = Calendar.getInstance();
            cal.setLenient(true);
            cal.add(Calendar.DAY_OF_MONTH, i);
            testValue = cal.getTime();
            var.addNewItem(testValue);
        }
        out(testVar + " size=" + var.size());
        assertEquals(var.size(), listsSize);
        assertNull(var.getItem(2));
        assertEquals(testValue, var.getItemValue(listsSize - 1));
        
        out("result:\n" + _pd.toString(true));
        
        finishTest("TEST SET GET LIST");
    }
    
    @Test
    public void testModified() {
        initTest("TEST MODIFIED");
        int caseNum = 0;
        _pd = _pxml.getProcessData(); // read a process...
        _pd.resetModified();
        out("Vamos a simples...");
        
        out("Case num: " + (++caseNum));
        assertFalse("Process is modified!", _pd.isModified());
        try {
            _pd.parseAndSet("aText", "Toma la fresquinho");
        } catch (ParseException e) {
            fail(e.getLocalizedMessage());
        }
        assertTrue("Process is not modified!", _pd.isModified());
        
        _pd.resetModified();
        
        out("Case num: " + (++caseNum));
        assertFalse("Process is modified!", _pd.isModified());
        _pd.clear("aText");
        assertTrue("Process is not modified!", _pd.isModified());
        
        _pd.resetModified();
        
        out("Case num: " + (++caseNum));
        assertFalse("Process is modified!", _pd.isModified());
        try {
            _pd.get("aText").parse("Muda outra vez");
        } catch (ParseException e) {
            fail(e.getLocalizedMessage());
        }
        assertTrue("Process is not modified!", _pd.isModified());
        
        _pd.resetModified();
        
        out("Case num: " + (++caseNum));
        out("Vamos a listas...");
        assertFalse("Process is modified!", _pd.isModified());
        try {
            _pd.getList("lText").parseAndSetItemValue(1, "Olha que bonito");
        } catch (ParseException e) {
            fail(e.getLocalizedMessage());
        }
        assertTrue("Process is not modified!", _pd.isModified());
        
        _pd.resetModified();
        
        out("Case num: " + (++caseNum));
        assertFalse("Process is modified!", _pd.isModified());
        try {
            _pd.getList("lText").parseAndSetItemValue(0, "Olha que bonito");
        } catch (ParseException e) {
            fail(e.getLocalizedMessage());
        }
        assertTrue("Process is not modified!", _pd.isModified());
        
        _pd.resetModified();
        
        out("Case num: " + (++caseNum));
        assertFalse("Process is modified!", _pd.isModified());
        try {
            _pd.getList("lText").parseAndAddNewItem("Olha que bonito");
        } catch (ParseException e) {
            fail(e.getLocalizedMessage());
        }
        assertTrue("Process is not modified!", _pd.isModified());
        
        _pd.resetModified();
        
        out("Case num: " + (++caseNum));
        assertFalse("Process is modified!", _pd.isModified());
        try {
            _pd.getList("lText").getItem(1).parse("Olha que bonitos");
        } catch (ParseException e) {
            fail(e.getLocalizedMessage());
        }
        assertTrue("Process is not modified!", _pd.isModified());
        
        _pd.resetModified();
        
        out("Case num: " + (++caseNum));
        assertFalse("Process is modified!", _pd.isModified());
        _pd.clearList("lText");
        assertTrue("Process is not modified!", _pd.isModified());
        
        _pd.resetModified();
        
        out("Case num: " + (++caseNum));
        assertFalse("Process is modified!", _pd.isModified());
        try {
            _pd.getList("lText").parseAndAddNewItem("Olha que bonito");
        } catch (ParseException e) {
            fail(e.getLocalizedMessage());
        }
        assertTrue("Process is not modified!", _pd.isModified());
        
        finishTest("TEST MODIFIED");
    }
    
    @Test
    public void testCopy() {
        ProcessData original = _pxml.getProcessData();
        
        assertTrue("Variable not blank", StringUtils.isBlank(_pd.getFormatted("aInteger")));
        assertNotNull("Variable null", original.getFormatted("aInteger"));
        assertEquals("62", original.getFormatted("aInteger"));
        
        _pd.copyFrom(original.get("aInteger"));
        
        assertNotNull("Variable null", _pd.getFormatted("aInteger"));
        assertNotNull("Variable null", original.getFormatted("aInteger"));
        assertEquals("62", _pd.getFormatted("aInteger"));
        assertEquals("62", original.getFormatted("aInteger"));
        
        original.set("aInteger", 33);
        
        assertNotNull("Variable null", _pd.getFormatted("aInteger"));
        assertNotNull("Variable null", original.getFormatted("aInteger"));
        assertEquals("62", _pd.getFormatted("aInteger"));
        assertEquals("33", original.getFormatted("aInteger"));
    }
}
