package pt.iflow.httpclient;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.impl.auth.NegotiateSchemeFactory;
import org.apache.http.impl.client.DefaultHttpClient;


public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		DefaultHttpClient httpclient;
		try {
			
			System.setProperty("java.security.krb5.conf", "krb5.conf");
			System.setProperty("java.security.auth.login.config", "login.conf");
			System.setProperty("javax.security.auth.useSubjectCredsOnly","false");
			
			httpclient = new DefaultHttpClient();
			
			NegotiateSchemeFactory nsf = new NegotiateSchemeFactory();
			httpclient.getAuthSchemes().register(AuthPolicy.SPNEGO, nsf);

			
			httpclient.getCredentialsProvider().setCredentials(new AuthScope("clsql3.munabrantes.pt", 7047,AuthScope.ANY_REALM,AuthPolicy.SPNEGO), new org.apache.http.auth.NTCredentials("svc-iflow@MUNABRANTES.PT", "WNYUlweqjxI9jqZkDBxA","","MUNABRANTES.PT"));
			
			
			
			//List authPrefs = new ArrayList(2);
			//authPrefs.add(AuthPolicy.SPNEGO);
			//authPrefs.add(AuthPolicy.BASIC);
			//authPrefs.add(AuthPolicy.NTLM);
			//authPrefs.add(AuthPolicy.SPNEGO);
			//httpclient.getParams().setParameter(org.apache.commons.httpclient.auth.AuthPolicy.AUTH_SCHEME_PRIORITY, authPrefs);
			
			HttpHost targetHost = new HttpHost("clsql3.munabrantes.pt", 7047, "http");
			//HttpHost proxy = new HttpHost("<ip>", 8080);
			//httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
			HttpGet httpget = new HttpGet("/DynamicsNAV/WS/Cidadela/Page/WS_Registo_Assiduidade");
			 
			System.out.println("executing request: " + httpget.getRequestLine());
			//System.out.println("via proxy: " + proxy);
			System.out.println("to target: " + targetHost);
			 			
			HttpResponse response = httpclient.execute(targetHost, httpget);
			HttpEntity entity = response.getEntity();

			System.out.println("----------------------------------------");
			System.out.println(response.getStatusLine());
			if (entity != null) {
			    System.out.println("Response content length: " + entity.getContentLength());
			}
			if (entity != null) {
			    entity.consumeContent();
			}
	        // When HttpClient instance is no longer needed,
	        // shut down the connection manager to ensure
	        // immediate deallocation of all system resources
	        httpclient.getConnectionManager().shutdown();

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
         
		
	}

}
