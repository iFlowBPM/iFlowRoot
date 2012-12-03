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

import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mysql.jdbc.exceptions.MySQLNonTransientException;
import com.mysql.jdbc.exceptions.MySQLSyntaxErrorException;

public class ReadFromDBTest extends ProcessTestCase {
    
    Connection db = null;
    
    @Before
    public void setUp() throws Exception {
        super.setUp();
        
        Class.forName("com.mysql.jdbc.Driver");
        // Class.forName("oracle.jdbc.driver.OracleDriver");
        
        Properties props = new Properties();
        props.put("user", "iflow");
        props.put("password", "iflow");
        
        try {
            db = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/iflow", props);
            db.setAutoCommit(false);
            Statement st = db.createStatement();
            st.execute("delete from teste_xml");
            st.close();
            db.commit();
        } catch (Exception e) {
            // TODO
        }
    }
    
    @After
    public void tearDown() throws Exception {
        super.tearDown();
        if (db != null) {
            db.close();
        }
    }
    
    @Test
    public void testInsert() throws Exception {
        String xml = _pxml.getXml();
        PreparedStatement pst = null;
        try {
            pst = db
                    .prepareStatement("insert into teste_xml (id,dados) values (?,XMLTYPE.createXML(?))");
            pst.setInt(1, 1);
            pst.setCharacterStream(2, new StringReader(xml), xml.length());
            pst.executeUpdate();
            pst.setInt(1, 2);
            pst.setCharacterStream(2, new StringReader(xml), xml.length());
            pst.executeUpdate();
            pst.setInt(1, 3);
            pst.setCharacterStream(2, new StringReader(xml), xml.length());
            pst.executeUpdate();
            db.commit();
            
            pst = db
                    .prepareStatement("select ExtractValue(dados, '/process/a[n=\"aInteger\"]')"
                            + " from teste_xml where id=1");
            ResultSet rs = pst.executeQuery();
            String result = null;
            if (rs.next())
                result = rs.getString(1);
            
            out("Result: " + result);
            assertEquals("", result);
            rs.close();
            pst.close();
            pst = null;
            
        } catch (Exception e) {
            // TODO
        } finally {
            if (pst != null)
                pst.close();
        }
        
    }
    
}
