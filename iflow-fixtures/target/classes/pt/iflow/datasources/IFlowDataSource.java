package pt.iflow.datasources;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

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

public Logger getParentLogger() throws SQLFeatureNotSupportedException {
	// TODO Auto-generated method stub
	return null;
}

public <T> T unwrap(Class<T> iface) throws SQLException {
	// TODO Auto-generated method stub
	return null;
}

public boolean isWrapperFor(Class<?> iface) throws SQLException {
	// TODO Auto-generated method stub
	return false;
}
  

}
