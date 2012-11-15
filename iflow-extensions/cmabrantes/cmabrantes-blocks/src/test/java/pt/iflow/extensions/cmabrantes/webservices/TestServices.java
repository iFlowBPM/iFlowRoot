package pt.iflow.extensions.cmabrantes.webservices;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import microsoft_dynamics_schemas.WS_Ficha_Produto;
import microsoft_dynamics_schemas.WS_Ficha_Produto_Port;
import microsoft_dynamics_schemas.WS_Ficha_Produto_ServiceLocator;
import microsoft_dynamics_schemas.WS_Lista_Produtos;
import microsoft_dynamics_schemas.WS_Lista_Produtos_Port;
import microsoft_dynamics_schemas.WS_Lista_Produtos_ServiceLocator;
import microsoft_dynamics_schemas.WS_Registo_Assiduidade;
import microsoft_dynamics_schemas.WS_Registo_Assiduidade_Port;
import microsoft_dynamics_schemas.WS_Registo_Assiduidade_ServiceLocator;

public class TestServices {

  private static String cidadelaProxyUrl = "http://localhost:8080/cmabrantes-cidadelaproxy/CidadelaServlet";
  //private static String cidadelaProxyUrl = "http://10.101.8.12:8480/cmabrantes-cidadelaproxy/CidadelaServlet";

  static final String kuser = "svc-iflow@MUNABRANTES.PT"; // your account name
  static final String kpass = "WNYUlweqjxI9jqZkDBxA"; // your password for the account

  static class MyAuthenticator extends Authenticator {
    public PasswordAuthentication getPasswordAuthentication() {
      // I haven't checked getRequestingScheme() here, since for NTLM
      // and Negotiate, the usrname and password are all the same.
      System.err.println("Feeding username and password for " + getRequestingScheme());
      return (new PasswordAuthentication(kuser, kpass.toCharArray()));
    }
  }

  public static void testCidadela () {
    try {
      System.setProperty("java.security.krb5.conf", "D:\\krb5.conf");
      System.setProperty("java.security.auth.login.config", "D:\\login.conf");
      System.setProperty("javax.security.auth.useSubjectCredsOnly","false");
      Authenticator.setDefault(new MyAuthenticator());

      String strUrl = "http://clsql3.munabrantes.pt:7047/DynamicsNAV/WS/Cidadela/Page/WS_Registo_Assiduidade";
      //String strUrl = args[0];
      URL url = new URL(strUrl);
      HttpURLConnection conn = (HttpURLConnection)url.openConnection();
      InputStream ins = conn.getInputStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(ins));
      String str;
      while((str = reader.readLine()) != null)
        System.out.println(str);
    } catch (Exception e) {
      e.printStackTrace();
    }     
  }


  public static void testListaProdutos () {
    try {
      WS_Lista_Produtos_ServiceLocator locator = new WS_Lista_Produtos_ServiceLocator();
      WS_Lista_Produtos_Port service = locator.getWS_Lista_Produtos_Port(new URL(cidadelaProxyUrl));

      WS_Lista_Produtos result = service.read(""); //RAP10-00001

      result.getName();

      int y= 9;
      y++;


    } catch (Exception e) {
      int y = 0;
      y++;
    }     
  }

  public static void testFichaProduto() {
    try {
      WS_Ficha_Produto_ServiceLocator locator = new WS_Ficha_Produto_ServiceLocator();
      WS_Ficha_Produto_Port service = locator.getWS_Ficha_Produto_Port(new URL(cidadelaProxyUrl));

      WS_Ficha_Produto result = service.read("á-06431601E"); //RAP10-00001
      
      int y= 9;
      y++;


    } catch (Exception e) {
      int y = 0;
      y++;
    }     
  }

  public static void testAssiduidade () {
    try {
      WS_Registo_Assiduidade_ServiceLocator locator = new WS_Registo_Assiduidade_ServiceLocator();
      WS_Registo_Assiduidade_Port service = locator.getWS_Registo_Assiduidade_Port(new URL(cidadelaProxyUrl));

      WS_Registo_Assiduidade result = service.read("12"); //RAP10-00001

      result.getUser_ID();
      int y= 9;
      y++;


    } catch (Exception e) {
      int y = 0;
      y++;
    }     
  }

  public static void testService () {
    try {
      String strUrl = "http://10.101.8.12:8480/cmabrantes-cidadelaproxy/CidadelaServlet";
      //String strUrl = args[0];
      URL url = new URL(strUrl);
      InputStream ins = url.openConnection().getInputStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(ins));
      String str;
      while((str = reader.readLine()) != null)
        System.out.println(str);
    } catch (Exception e) {
      int y = 0;
      y++;
    }     
  }


  public static void main(String[] args) throws Throwable{
    //testCidadela();
    //testService();
    //testListaProdutos();
    testFichaProduto();
    //testAssiduidade();
  }
}
