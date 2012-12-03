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
package pt.iflow.fixtures.WebServices;

import java.net.URL;
import java.util.HashMap;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.wsdl.Operation;
import javax.wsdl.Port;
import javax.wsdl.Service;

import org.apache.struts.util.GenericDataSource;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.Repository;
import pt.iflow.api.core.RepositoryFile;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iknow.utils.wsdl.WSDLUtils;
import fit.ActionFixture;

public class WsdlUtilsFixture extends ActionFixture {

  protected String wsdlFile;
  protected String iflowHome;
  protected String service;
  protected String port;
  protected String operation;
  protected int timeOut;
  protected HashMap<String, Object> inputParams;

  protected void setUp() {
    try {
      this.tearDown();
    } catch (Exception e1) {
      // do nothing
    }
    if (this.inputParams == null) {
      this.inputParams = new HashMap<String, Object>();
    }
    System.setProperty("iflow.home", iflowHome);
    System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");
    System.setProperty(Context.URL_PKG_PREFIXES, "org.apache.naming");
    Logger.initLogger();
    try {
      InitialContext ic = new InitialContext();
      ic.createSubcontext("java:");
      ic.createSubcontext("java:comp");
      ic.createSubcontext("java:comp/env");
      ic.createSubcontext("java:comp/env/jdbc");

      GenericDataSource ds = new GenericDataSource();
      ds
          .setUrl("jdbc:mysql://localhost:3306/iflow?autoReconnect=true&amp;useUnicode=true&amp;characterEncoding=utf8&amp;characterSetResults=utf8&amp;noAccessToProcedureBodies=true");
      ds.setUser("iflow");
      ds.setPassword("iflow");
      ds.setDriverClass("com.mysql.jdbc.Driver");
      ic.bind("java:comp/env/jdbc/iFlowMyDS", ds);
    } catch (NamingException e) {
      e.printStackTrace();
    }
  }

  protected void tearDown() throws Exception {
    InitialContext ic = new InitialContext();
    ic.unbind("java:comp/env/jdbc/iFlowMyDS");
    ic.destroySubcontext("java:comp/env/jdbc");
    ic.destroySubcontext("java:comp/env");
    ic.destroySubcontext("java:comp");
    ic.destroySubcontext("java:");
    ic.close();
  }

  public String availableServices() {
    this.setUp();

    String response = "Failed!";
    if (inputParams != null && inputParams.containsKey("user") && inputParams.containsKey("password")) {
      UserInfoInterface userInfo = BeanFactory.getUserInfoFactory().newUserInfo();
      Repository rep = BeanFactory.getRepBean();

      userInfo.login((String) inputParams.get("user"), (String) inputParams.get("password"));
      if (userInfo.isLogged() && rep.checkConnection()) {
        RepositoryFile[] webServices = rep.listWebServices(userInfo);
        response = "";
        for (RepositoryFile webService : webServices) {
          String name = webService.getName();
          if (name.endsWith("wsdl")) {
            response += ", " + name;
          }
        }
        response = "{" + response.replaceFirst(", ", "") + "}";
      }
    }
    return response;
  }

  public String callServiceByURL() {
    this.setUp();

    HashMap<String, Object> response = new HashMap<String, Object>();
    try {
      response = callService(new URL(wsdlFile), false);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return response.toString();
  }

  public String callServiceByWSDLFile() {
    this.setUp();

    HashMap<String, Object> response = new HashMap<String, Object>();
    try {
      String endpoint = iflowHome + "/repository_data/1/WSDL/" + wsdlFile;
      response = callService(new URL(endpoint), true);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return response.toString();
  }

  protected HashMap<String, Object> callService(URL endpoint, boolean useAsString) throws Exception {
    WSDLUtils wu = null;
    if (useAsString) {
      wu = new WSDLUtils(endpoint.toString());
    } else {
      wu = new WSDLUtils(endpoint);
    }
    this.debug(wu, endpoint.toString());
    return wu.callService(this.service, this.port, this.operation, this.timeOut, this.inputParams);
  }

  protected void debug(WSDLUtils wu, String endpoint) {
    System.out.println("################## WebService Info ##################");
    System.out.println("WebService: " + endpoint);
    Service[] services = wu.getServices();
    for (Service service : services) {
      System.out.println("Service: " + service.getQName());
      Port[] ports = wu.getServicePorts(service);
      for (Port port : ports) {
        System.out.println("  Port: " + port.getName());
        Operation[] operations = wu.getPortOperations(port);
        for (Operation operation : operations) {
          System.out.println("    Operation: " + operation.getName());
        }
      }
    }
    System.out.println("Currently calling: ");
    System.out.println("  Service: " + this.service);
    System.out.println("  Port: " + this.port);
    System.out.println("  Operation: " + this.operation);
    System.out.println("#####################################################");
  }

  protected void addInputValue(String name, Object value) {
    if (this.inputParams == null) {
      this.inputParams = new HashMap<String, Object>();
    }
    inputParams.put(name, value);
  }

  public void setWsdlFile(String nWsdlFile) {
    this.wsdlFile = nWsdlFile;
  }

  public void setIflowHome(String nIflowHome) {
    this.iflowHome = nIflowHome;
  }

  public void setService(String nService) {
    this.service = nService;
  }

  public void setPort(String nPort) {
    this.port = nPort;
  }

  public void setOperation(String nOperation) {
    this.operation = nOperation;
  }

  public void setTimeOut(int nTimeOut) {
    this.timeOut = nTimeOut;
  }

  public void setUser(String user) {
    this.addInputValue("user", user);
  }

  public void setPassword(String password) {
    this.addInputValue("password", password);
  }
}
