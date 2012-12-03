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

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.spi.InitialContextFactory;
import javax.sql.DataSource;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;

public class MockInitialContextFactory implements InitialContextFactory {
    
    public Context getInitialContext(Hashtable < ?, ? > env) {
        Mockery mockery = new Mockery() {
            {
                setImposteriser(ClassImposteriser.INSTANCE);
            }
        };
        
        final Context ctx = mockery.mock(Context.class);
        final DataSource dataSource = mockery.mock(DataSource.class);
        final Connection conn = mockery.mock(Connection.class);
        final PreparedStatement ps = mockery.mock(PreparedStatement.class);
        final CallableStatement cs = mockery.mock(CallableStatement.class);
        final Statement st = mockery.mock(Statement.class);
        final ResultSet rs = mockery.mock(ResultSet.class);
        
        try {
            mockery.checking(new Expectations() {
                {
                    // Context
                    allowing(ctx).lookup(with(any(String.class)));
                    will(returnValue(dataSource));
                    
                    // DataSource
                    allowing(dataSource).getConnection();
                    will(returnValue(conn));
                    
                    // Connection
                    allowing(conn).prepareStatement(with(any(String.class)));
                    will(returnValue(ps));
                    allowing(conn).createStatement();
                    will(returnValue(st));
                    allowing(conn).setAutoCommit(with(any(Boolean.class)));
                    allowing(conn).prepareCall(with(any(String.class)));
                    will(returnValue(cs));
                    allowing(conn).close();
                    
                    // PreparedStatement
                    allowing(ps).setString(with(any(Integer.class)),
                            with(any(String.class)));
                    allowing(ps).setInt(with(any(Integer.class)),
                            with(any(Integer.class)));
                    allowing(ps).setTimestamp(with(any(Integer.class)),
                            with(any(Timestamp.class)));
                    allowing(ps).executeUpdate();
                    will(returnValue(1));
                    allowing(ps).executeQuery();
                    will(returnValue(rs));
                    allowing(ps).close();
                    
                    // CallableStatement
                    allowing(cs).registerOutParameter(with(any(Integer.class)),
                            with(any(Integer.class)));
                    allowing(cs).setInt(with(any(Integer.class)),
                            with(any(Integer.class)));
                    allowing(cs).setTimestamp(with(any(Integer.class)),
                            with(any(Timestamp.class)));
                    allowing(cs).getInt(with(any(Integer.class)));
                    will(returnValue(1));
                    allowing(cs).execute();
                    will(returnValue(true));
                    allowing(cs).close();
                    
                    // Statement
                    allowing(st).executeQuery(with(any(String.class)));
                    will(returnValue(rs));
                    allowing(st).executeUpdate(with(any(String.class)));
                    will(returnValue(1));
                    allowing(st).execute(with(any(String.class)));
                    will(returnValue(true));
                    allowing(st).close();
                    
                    // ResultSet
                    allowing(rs).next();
                    will(returnValue(false));
                    allowing(rs).close();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ctx;
    }
}
