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
package pt.iflow.datasources;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * Proxy datasource to encapsulate a dynamically loadaded datasource (from repository)
 * 
 * @author oscar
 */
public class IFlowDataSource implements DataSource {
  
  private final DataSource ds;
  
  public IFlowDataSource(Object ds) {
    this((DataSource)ds);
  }
  
  public IFlowDataSource(DataSource ds) {
    if(null == ds) throw new IllegalArgumentException("Invalid datasource");
    this.ds = ds;
  }

  public Class<? extends DataSource> getDataSourceClass() {
    return ds.getClass();
  }
  
  public DataSource getDataSource() {
    return ds;
  }
  
  public Connection getConnection() throws SQLException {
    return ds.getConnection();
  }

  public Connection getConnection(String username, String password) throws SQLException {
    return ds.getConnection(username, password);
  }

  public PrintWriter getLogWriter() throws SQLException {
    return ds.getLogWriter();
  }

  public int getLoginTimeout() throws SQLException {
    return ds.getLoginTimeout();
  }

  public void setLogWriter(PrintWriter out) throws SQLException {
    ds.setLogWriter(out);
  }

  public void setLoginTimeout(int seconds) throws SQLException {
    ds.setLoginTimeout(seconds);
  }

  public boolean isWrapperFor(Class cl) {
	  //TODO not needed
	  return true;
  }
  
  public Class<? extends DataSource> unwrap(Class cl) {
	//TODO not needed
	  return null;
  }
  
}
