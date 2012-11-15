package com.infosistema.proxy.cidadela.servlets;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.infosistema.proxy.cidadela.properties.WebServiceProperties;

public class CidadelaServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
  static final long serialVersionUID = 1L;

  private static final String SOAPACTION = "soapaction";
  private static final String HOST = "host";

  static class MyAuthenticator extends Authenticator {
    public PasswordAuthentication getPasswordAuthentication() {
      return (new PasswordAuthentication(WebServiceProperties.getUserName(), WebServiceProperties.getPassword().toCharArray()));
    }
  }

  public CidadelaServlet() {
    super();
  }     

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    service(request, response);
  }     

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    service(request, response);
  }

  @Override
  protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    System.setProperty("java.security.krb5.conf", WebServiceProperties.getKrb5Conf());
    System.setProperty("java.security.auth.login.config", WebServiceProperties.getLoginConf());
    System.setProperty("javax.security.auth.useSubjectCredsOnly","false");
    Authenticator.setDefault(new MyAuthenticator());

    HttpURLConnection conn = null;
    DataOutputStream outStream = null;
      
    try {
      //Get URL
      if (request.getHeader(SOAPACTION)==null) return;
      String strUrl = WebServiceProperties.getBaseURL() + getWebservice(request.getHeader(SOAPACTION));
      URL url = new URL(strUrl);

      //Create Connection
      conn = (HttpURLConnection)url.openConnection();
      conn.setDoInput(true);
      conn.setDoOutput(true);
      conn.setUseCaches(false);

      Enumeration en = request.getHeaderNames();
      while (en.hasMoreElements()) {
        String name = en.nextElement().toString();
        if (name.equals(HOST))
          conn.addRequestProperty(name, WebServiceProperties.getHost());
        else
          conn.addRequestProperty(name, request.getHeader(name));
      }
      
      //Write the content
      outStream = new DataOutputStream(conn.getOutputStream());
      BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
      String str;
      while((str = reader.readLine()) != null) {
        outStream.writeBytes(str);
        log(str);
      }
      outStream.flush();
      
      //Processes the result
      InputStream ins = conn.getInputStream();
      reader = new BufferedReader(new InputStreamReader(ins));

      PrintWriter out = response.getWriter();
      while((str = reader.readLine()) != null)
      {
        out.println(str);
        log(str);
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    } finally {
      try {
      } catch (Exception e) {
      }
    }
  }
  
  private String getWebservice(String webservice) {
    int idxIni = 0;
    int idxFim = 0;
    int i = 0;
    while ((i= webservice.indexOf("/", i))>= 0) {
      idxIni = ++i;
    }
    i = 0;
    while ((i= webservice.indexOf(":", i+1))>= 0) {
      idxFim = i;
    }
    String action = webservice.substring(idxIni, idxFim);
    return WebServiceProperties.getActionURL(action);
  }
}