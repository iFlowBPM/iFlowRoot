package pt.iflow.applet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;



public class UtilityAppletWebStart {
	public static final String ACTION = "action";
	public static final String JSESSIONID = "JSESSIONID";
	public static final String DOCUMENTBASEURL = "DOCUMENTBASEURL";
	public static final String CHECKREQUESTFORAPPLET = "CHECKREQUESTFORAPPLET";
	
	public static void main(String []args) throws JSONException, IOException, InterruptedException{
		//String arg = "{\"CHECKREQUESTFORAPPLET\":\"http://localhost:8480/iFlow/CheckRequestForApplet\",\"DOCUMENTBASEURL\":\"http://localhost:8480/iFlow/DocumentService\"," +
		//		"\"JSESSIONID\":\"A3248FCA27D75B0A707D00A2002A1BB0\"}";
		String arg = args[0];
		System.out.println(arg);
		JSONObject arguments = new JSONObject(arg);		
		UtilityApplet ua = new UtilityApplet();		
		ua.setBaseURL(new URL("" + arguments.get(DOCUMENTBASEURL)));
		arguments.remove(DOCUMENTBASEURL);
		String checkReqForAppURL = arguments.get(CHECKREQUESTFORAPPLET).toString();
		arguments.remove(CHECKREQUESTFORAPPLET);
		ua.init();
		ua.start();
		
		String jSessionIdValue = arguments.getString(JSESSIONID);
		arguments.remove(JSESSIONID);
		
		WebClient wc = ua.createWebClient(JSESSIONID+"="+jSessionIdValue, arguments.toString());
		while(true){
			//check requests
			HttpURLConnection conn = (HttpURLConnection) (new URL(checkReqForAppURL)).openConnection();		
			conn.setDoInput(true);
			conn.setDoOutput(false);
			conn.setUseCaches(false);
			conn.setRequestMethod("GET"); //$NON-NLS-1$
			conn.setRequestProperty("Cookie", wc.getCookie());
			conn.setRequestProperty("Cookie", "APPLET" + wc.getCookie());
			conn.setReadTimeout(0);
			//execute if possible
			try{
				conn.connect();
				if(conn.getResponseCode()==405){
					System.out.println("No session - SHUTDOWN");
					break;
				}
				else if(conn.getResponseCode()==204){
					continue;
				}
				else {
					System.out.println("Request found - PROCESSING");
					 ByteArrayOutputStream out = new ByteArrayOutputStream();
				     byte [] b = new byte[8192];
				     int r;
				     InputStream is = conn.getInputStream();
				     while((r = is.read(b))>=0)
				       out.write(b, 0, r);
					
					
					arg = out.toString();
					System.out.println("Request arguments: " + arg);
					arguments = new JSONObject(arg);
					String actionValue = arguments.getString(ACTION);
					
					if ("modifyFile".equals(actionValue)){			
						ua.modifyFile(JSESSIONID+"="+jSessionIdValue, arguments.toString(), "false");
					} else if ("replaceFile".equals(actionValue)){			
						ua.replaceFile(JSESSIONID+"="+jSessionIdValue, arguments.toString(), "false");
					} else if ("scanFile".equals(actionValue)){			
						ua.scanFile(JSESSIONID+"="+jSessionIdValue, arguments.toString());
					} else if ("uploadFile".equals(actionValue)){			
						ua.uploadFile(JSESSIONID+"="+jSessionIdValue, arguments.toString(), "false");
					} 	
				}
			} catch (SocketTimeoutException e){
				System.out.println("Waited until timeout, will start again");
			} catch (Exception e){
				System.out.println("Unexpected error: " + e);
				e.printStackTrace();				
			} finally {
				conn.disconnect();
			}
			//sleep a while and repeat
			Thread.sleep(750);
		}				
	}
}
