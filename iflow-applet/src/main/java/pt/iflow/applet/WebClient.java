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
package pt.iflow.applet;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.mail.internet.ContentDisposition;
import javax.mail.internet.ParseException;

import javax.swing.ProgressMonitorInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

public class WebClient implements UtilityConstants {
  
  private static Log log = LogFactory.getLog(WebClient.class);
  
  public static final String REMOVE_PARAM = "remove"; //$NON-NLS-1$
  public static final String UPDATE_PARAM = "update"; //$NON-NLS-1$
  public static final String FILE_PARAM = "file"; //$NON-NLS-1$
  public static final String IMAGE_PARAM = "image"; //$NON-NLS-1$
  public static final String DOCID_PARAM = "docid"; //$NON-NLS-1$
  public static final String MAXSIZE_PARAM = "maxsize"; //$NON-NLS-1$

  private String cookie;
  private String uploadLocation;
  private String downloadLocation;
  private URL baseURL;
  private JSONObject request;
  private String signatureServiceLocation;
  private String rubricServiceLocation;
  
  public WebClient(String cookie, JSONObject jsObj) {
    this.cookie = cookie;
    this.request = jsObj;
  }

  public String getCookie() {
    return cookie;
  }

  public void setCookie(String cookie) {
    this.cookie = cookie;
  }

  public String getUploadLocation() {
    return uploadLocation;
  }

  public void setUploadLocation(String uploadLocation) {
    this.uploadLocation = uploadLocation;
  }

  public String getDownloadLocation() {
    return downloadLocation;
  }

  public void setDownloadLocation(String downloadLocation) {
    this.downloadLocation = downloadLocation;
  }

  public String getSignatureServiceLocation() {
    return signatureServiceLocation;
  }

  public String getRubricServiceLocation() {
	    return rubricServiceLocation;
	  }
  
  public void setSignatureServiceLocation(String signatureServiceLocation) {
    this.signatureServiceLocation = signatureServiceLocation;
  }
  
  public void setRubricServiceLocation(String rubricServiceLocation) {
	    this.rubricServiceLocation = rubricServiceLocation;
  }
  
  public URL getBaseURL() {
    return baseURL;
  }

  public void setBaseURL(URL baseURL) {
    this.baseURL = baseURL;
  }

  public JSONObject getRequest() {
    return request;
  }

  public void setRequest(JSONObject request) {
    this.request = request;
  }
  
  
  public Map<String,String> getParameters() {
    Map<String,String> result = new HashMap<String,String>();
    
    Iterator<?> keys = request.keys();
    while(keys != null && keys.hasNext()) {
      String key = (String) keys.next();
      if(request.isNull(key)) continue;
      try {
        result.put(key, request.getString(key));
      } catch (JSONException e) {
        log.warn("Could not retrieve key value: "+key); //$NON-NLS-1$
      }
    }
    // fix docid
    if(getFileId() != null) {
      result.put(FILE_PARAM, getFileId());
      result.put(DOCID_PARAM, getFileId());
    }
    result.put("NUMASS", ""+LoadImageAction.getNumAss());
    result.put("RUBRICAR",""+LoadImageAction.getFlagRub());
    return result;
  }

  public String getFlowId() {
    return request.optString(FLOWID);
  }
  
  public String getPid() {
    return request.optString(PID);
  }
  
  public String getSubPid() {
    return request.optString(SUBPID);
  }
  
  public String getVariable() {
    return request.optString(VARIABLE);
  }
  
  public String getFileId() {
    return request.optString(FILEID);
  }

  public String getSignatureType() {
    return request.optString(SIGNATURE_TYPE);
  }
  
  public String getCipherType() {
    return request.optString(ENCRYPTION_TYPE);
  }
  
  // web client stuff
  public String postFile(Map<String,String> params, IVFile f) {
    return postFile(params, f, null);
  }
  
  public String postFile(Map<String,String> params, IVFile f, java.awt.Component parent) {
    String retObj = null;
    
    String uploadUrl = getUploadLocation();
    String cookie = getCookie();
    URL documentBase = getBaseURL();

    // Create URL object for upload service
    log.debug("Posting to: " + uploadUrl); //$NON-NLS-1$
    URL url = null;
    try {
      url = new URL(uploadUrl);
    } catch (MalformedURLException ex) {
      log.warn("From ServletCom CLIENT REQUEST:" + ex); //$NON-NLS-1$
      try {
        log.debug("Posting to: " + documentBase + " -> "+uploadUrl); //$NON-NLS-1$ //$NON-NLS-2$
        url = new URL(documentBase, uploadUrl);
      } catch (MalformedURLException e) {
        log.error("From ServletCom CLIENT REQUEST:" + e, e); //$NON-NLS-1$
        return null;
      }
    }

    // upload document
    final String lineEnd = "\r\n"; //$NON-NLS-1$
    final String twoHyphens = "--"; //$NON-NLS-1$
    final String boundary = "---------------------------" + System.currentTimeMillis(); //$NON-NLS-1$

    HttpURLConnection conn = null;
    DataOutputStream dos = null;
    InputStream inStream = null;

    int pos = 0;

    byte[] buffer = new byte[8192];
    int r;
    // ignore baddies....
    if (null == f)
      return null;
    if (!f.canRead())
      return null;
    if (!f.isFile())
      return null;

    InputStream in = null;
    try {
      // ------------------ CLIENT REQUEST

      // Open a HTTP connection to the URL
      conn = (HttpURLConnection) url.openConnection();

      // Allow Inputs
      conn.setDoInput(true);

      //Set Internal Buffer to 0
        conn.setChunkedStreamingMode(0);
        log.debug("Setting Connection setChunkedStreamingMode to (0)");
 
      
      // Allow Outputs
      conn.setDoOutput(true);

      // Don't use a cached copy.
      conn.setUseCaches(false);

      // Use a post method.
      conn.setRequestMethod("POST"); //$NON-NLS-1$

      conn.setRequestProperty("Connection", "Keep-Alive"); //$NON-NLS-1$ //$NON-NLS-2$

      conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary); //$NON-NLS-1$ //$NON-NLS-2$

      // set cookies
      if (null != cookie && cookie.trim().length() > 0)
        conn.setRequestProperty("Cookie", cookie); //$NON-NLS-1$

      dos = new DataOutputStream(conn.getOutputStream());

      // send process identification
      if (params != null && !params.isEmpty()) {
        for(Map.Entry<String,String> entry : params.entrySet()) {
          String pname = entry.getKey();
          String pvalue = entry.getValue();
          // ignore null params
          if(null == pname || "".equals(pname) || null == pvalue) continue; //$NON-NLS-1$

          // mark as update
          dos.writeBytes(twoHyphens + boundary + lineEnd);
          dos.writeBytes("Content-Disposition: form-data; name=\""+pname+"\"" + lineEnd); //$NON-NLS-1$ //$NON-NLS-2$
          dos.writeBytes("Content-Type: text/plain; charset=UTF-8" + lineEnd); //$NON-NLS-1$
          dos.writeBytes("Content-Transfer-Encoding: 8bit" + lineEnd); //$NON-NLS-1$
          dos.writeBytes(lineEnd);
          dos.write(pvalue.getBytes("UTF-8")); //$NON-NLS-1$
          dos.writeBytes(lineEnd);
        }
      }

      // Upload file
      dos.writeBytes(twoHyphens + boundary + lineEnd);
      dos.writeBytes("Content-Disposition: form-data; name=\""+FILE_PARAM+"\"; filename=\"" + URLEncoder.encode(f.getName(), "UTF-8") + "\"" + lineEnd); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      dos.writeBytes("Content-Length: " + f.getLength() + lineEnd); //$NON-NLS-1$
      dos.writeBytes(lineEnd);

      // output file

      // TODO fazer magia com uma barra de progresso
      in = f.getInputStream();
      in = new ProgressMonitorInputStream(parent, Messages.getString("WebClient.1"), in); //$NON-NLS-1$
      while ((r = in.read(buffer)) > 0)
        dos.write(buffer, 0, r);

      // send multipart form data necesssary after file data...
      dos.writeBytes(lineEnd);
      dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

      dos.flush();
      dos.close();

      in.close();
      in = null;
      
      // Read response
      inStream = null;
      ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
      try {
        inStream = new ProgressMonitorInputStream(null, Messages.getString("WebClient.2"), conn.getInputStream()); //$NON-NLS-1$
        while ((r = inStream.read(buffer)) > 0) {
          dataStream.write(buffer, 0, r);
        }
        inStream.close();

        String response = dataStream.toString("UTF-8"); //$NON-NLS-1$
        log.debug("Got response "+response+" for file "+f.getName()); //$NON-NLS-1$ //$NON-NLS-2$
        retObj = response;
        pos++;


      } catch (IOException ioex) {
        log.error("From (ServerResponse): " + ioex,ioex); //$NON-NLS-1$
      } finally {
        try {
          if (inStream != null)
            inStream.close();
        } catch (IOException e) {
        }
      }

    } catch (IOException ex) {
      log.error("From ServletCom CLIENT REQUEST:" + ex,ex); //$NON-NLS-1$
    } finally {
      try {
        if (in != null)
          in.close();
      } catch (IOException e) {
      }
    }
    log.info("Done with: " + f.getName()); //$NON-NLS-1$

    return retObj;
  }
  
  public String postImage(ImageIconRep i, java.awt.Component parent) {
    String retObj = null;
    
    String imageserviceUrl = getSignatureServiceLocation();
    String cookie = getCookie();
    URL documentBase = getBaseURL();

    // Create URL object for upload service
    URL url = null;
    try {
      url = new URL(documentBase, imageserviceUrl);
    } catch (MalformedURLException e) {
      return null;
    } 

    // upload image
    final String lineEnd = "\r\n"; //$NON-NLS-1$
    final String twoHyphens = "--"; //$NON-NLS-1$
    final String boundary = "---------------------------" + System.currentTimeMillis(); //$NON-NLS-1$

    HttpURLConnection conn = null;
    DataOutputStream dos = null;
    InputStream inStream = null;
    
    // ignore baddies....
    if (null == i)
      return null;

    try {
      // ------------------ CLIENT REQUEST
      // Open a HTTP connection to the URL
      conn = (HttpURLConnection) url.openConnection();
      // Allow Inputs
      conn.setDoInput(true);
      // Allow Outputs
      conn.setDoOutput(true);
      // Don't use a cached copy.
      conn.setUseCaches(false);
      // Use a post method.
      conn.setRequestMethod("POST"); //$NON-NLS-1$
      conn.setRequestProperty("Connection", "Keep-Alive"); //$NON-NLS-1$ //$NON-NLS-2$
      //application/octet-stream
      conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary); //$NON-NLS-1$ //$NON-NLS-2$
      // set cookies
      if (null != cookie && cookie.trim().length() > 0)
        conn.setRequestProperty("Cookie", cookie); //$NON-NLS-1$

      dos = new DataOutputStream(conn.getOutputStream());
      
      // Upload image
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      ObjectOutputStream out = new ObjectOutputStream(bos);
      out.writeObject(i);
      out.close(); 

      dos.writeBytes(twoHyphens + boundary + lineEnd);
      dos.writeBytes("Content-Disposition: form-data; name=\"" + IMAGE_PARAM+ "\"; filename=\"image\"" + lineEnd); 
      dos.writeBytes("Content-Length: " + bos.toByteArray().length + lineEnd); 
      dos.writeBytes(lineEnd);

      // output file
      dos.write(bos.toByteArray(), 0, bos.toByteArray().length);

      // send multipart form data necesssary after file data...
      dos.writeBytes(lineEnd);
      dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

      dos.flush();
      dos.close();

      // Read response
      int r;
      inStream = null;
      ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
      try {
        retObj ="#" + Messages.getString("WebClient.2");
        inStream = new ProgressMonitorInputStream(null, Messages.getString("WebClient.2"), conn.getInputStream()); //$NON-NLS-1$
        retObj+="@";
        byte[] buffer = new byte[8192];
        while ((r = inStream.read(buffer)) > 0) {
          dataStream.write(buffer, 0, r);
        }
        inStream.close();

        String response = dataStream.toString("UTF-8"); //$NON-NLS-1$
        log.debug("Got response "+response+" for image."); //$NON-NLS-1$ //$NON-NLS-2$
        retObj = "response";
      } catch (IOException ioex) {
        log.error("From (ServerResponse): " + ioex,ioex); //$NON-NLS-1$
      } finally {
        try {
          if (inStream != null)
            inStream.close();
        } catch (IOException e) {
          return null;
        }
      }

    } catch (IOException ex) {
      return null;
    } catch (Exception e) {
      return null;
    }

    return retObj;
  }
 
  public IVFile getDocument() {
    return getDocument(null);
  }
  
  public IVFile getDocument(java.awt.Component parent) {
    HttpURLConnection conn = null;
    InputStream is = null;
    
    String downloadUrl = getDownloadLocation();
    String cookie = getCookie();
    URL documentBase = getBaseURL();

    IVFile file = null;
    // Read file
    try {
      // open a URL connection to the Servlet
      URL url = null;

      try {
        url = new URL(downloadUrl);
      } catch (MalformedURLException e) {
        url = new URL(documentBase, downloadUrl);
      }
      
      // Add extra params
      String query = url.getQuery();
      boolean emptyQuery = StringUtils.isBlank(query);
      boolean first = emptyQuery;
      
      Map<String,String> params = getParameters();
      StringBuilder sb = new StringBuilder(url.getPath());
      if(!emptyQuery) sb.append(query.trim());
      for(Entry<String,String> entry : params.entrySet()) {
        if(null == entry.getKey() || null == entry.getValue()) continue;
        String key = URLEncoder.encode(entry.getKey(), "UTF-8"); //$NON-NLS-1$
        String value = URLEncoder.encode(entry.getValue(), "UTF-8"); //$NON-NLS-1$
        
        // ckeck if key already exists....
        if(!emptyQuery && (query.contains("&"+key+"=") || query.contains("?"+key+"="))) continue; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        
        sb.append(first?"?":"&").append(key).append("=").append(value); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        first = false;
      }
           
      url = new URL(url.getProtocol(), url.getHost(), url.getPort(), sb.toString());
      
      log.debug("Resulting URL: "+url); //$NON-NLS-1$
      
      // Open a HTTP connection to the URL
      conn = (HttpURLConnection) url.openConnection();
      // Allow Inputs
      conn.setDoInput(true);
      // Allow Outputs
      conn.setDoOutput(false);
      // Don't use a cached copy.
      conn.setUseCaches(false);
      // Use a GET method.
      conn.setRequestMethod("GET"); //$NON-NLS-1$
      // set cookies
      if (null != cookie && cookie.trim().length() > 0)
        conn.setRequestProperty("Cookie", cookie); //$NON-NLS-1$

      conn.connect();
      if(conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
        log.error("Connection failed: "+conn.getResponseCode()+" "+conn.getResponseMessage()); //$NON-NLS-1$ //$NON-NLS-2$
        return null;
      }
      
      // guess file name
      ContentDisposition contentDisposition=null;
      String sContentDisp = conn.getHeaderField("Content-Disposition"); //$NON-NLS-1$
      
      //ler numero da proxima assinatura
      int numass = Integer.parseInt(conn.getHeaderField("NUMASS"));
      LoadImageAction.setNumAss(numass);
      
      try {
        contentDisposition = new ContentDisposition(sContentDisp);
      } catch (ParseException e) {
        log.error("Invalid Content-Disposition header from server: "+sContentDisp); //$NON-NLS-1$
      }
      
      String name = "file.dat"; //$NON-NLS-1$
      if(null != contentDisposition) {
        name = contentDisposition.getParameter("filename"); //$NON-NLS-1$
      }
      
      is = conn.getInputStream();
      is = new ProgressMonitorInputStream(parent, Messages.getString("WebClient.3"), is); //$NON-NLS-1$

      file = new TempVFile(name, getVariable(), is);
    } catch (IOException e) {
      log.error("nao foi possivel ler o ficheiro do servidor", e); //$NON-NLS-1$
      file = null;
    } finally {
      try {
        if (null != is)
          is.close();
      } catch (IOException e) {
      }
      is = null;
      conn = null;
    }
    return file;
  }
  
  public ImageIconRep getImage(java.awt.Component parent) {
    HttpURLConnection conn = null;
    InputStream is = null;
    String downloadUrl = getSignatureServiceLocation();
    String cookie = getCookie();
    URL documentBase = getBaseURL();

    ImageIconRep image = null;
    // Read file
    try {
      // open a URL connection to the Servlet
      URL url = new URL(documentBase, downloadUrl);
      
      // Add extra params
      String query = url.getQuery();
      boolean emptyQuery = StringUtils.isBlank(query);
      
      StringBuilder sb = new StringBuilder(url.getPath());
      if(!emptyQuery) sb.append(query.trim());
      
      url = new URL(url.getProtocol(), url.getHost(), url.getPort(), sb.toString());
      
      // Open a HTTP connection to the URL
      conn = (HttpURLConnection) url.openConnection();
      // Allow Inputs
      conn.setDoInput(true);
      // Allow Outputs
      conn.setDoOutput(false);
      // Don't use a cached copy.
      conn.setUseCaches(false);
      // Use a GET method.
      conn.setRequestMethod("GET"); //$NON-NLS-1$
      // set cookies
      if (null != cookie && cookie.trim().length() > 0)
        conn.setRequestProperty("Cookie", cookie); //$NON-NLS-1$
            
  	conn.connect();
      if(conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
        log.error("Connection failed: "+conn.getResponseCode()+" "+conn.getResponseMessage()); //$NON-NLS-1$ //$NON-NLS-2$
        return null;
      }
      
      is = conn.getInputStream();
      is = new ProgressMonitorInputStream(parent, Messages.getString("WebClient.3"), is);
      
      ObjectInputStream ois = new ObjectInputStream(is);
      image = (ImageIconRep)(ois.readObject());
      
      
      //LER PARAMETROS DA ASSINATURA
      String signature_position_style = conn.getHeaderField("SIGNATURE_POSITION_STYLE");
      String rubric_image = conn.getHeaderField("RUBRIC_IMAGE");
      String rubric_position_style = conn.getHeaderField("RUBRIC_POSITION_STYLE");
      
      LoadImageAction.setSignatureParameters(signature_position_style, rubric_image, rubric_position_style);
      
    } catch (Exception e) {
      image = null;
    } finally {
      try {
        if (null != is)
          is.close();
      } catch (IOException e) {
      }
      is = null;
      conn = null;
    }

    return image;
  }
    
  public ImageIconRep getRubricImage(java.awt.Component parent) {
	    HttpURLConnection conn = null;
	    InputStream is = null;
	    String downloadUrl = getRubricServiceLocation();
	    String cookie = getCookie();
	    URL documentBase = getBaseURL();

	    ImageIconRep image = null;
	    // Read file
	    try {
	      // open a URL connection to the Servlet
	      URL url = new URL(documentBase, downloadUrl);
	      
	      // Add extra params
	      String query = url.getQuery();
	      boolean emptyQuery = StringUtils.isBlank(query);
	      
	      StringBuilder sb = new StringBuilder(url.getPath());
	      if(!emptyQuery) sb.append(query.trim());
	      
	      url = new URL(url.getProtocol(), url.getHost(), url.getPort(), sb.toString());
	      
	      // Open a HTTP connection to the URL
	      conn = (HttpURLConnection) url.openConnection();
	      // Allow Inputs
	      conn.setDoInput(true);
	      // Allow Outputs
	      conn.setDoOutput(false);
	      // Don't use a cached copy.
	      conn.setUseCaches(false);
	      // Use a GET method.
	      conn.setRequestMethod("GET"); //$NON-NLS-1$
	      // set cookies
	      if (null != cookie && cookie.trim().length() > 0)
	        conn.setRequestProperty("Cookie", cookie); //$NON-NLS-1$
	            
	  	conn.connect();
	      if(conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
	        log.error("Connection failed: "+conn.getResponseCode()+" "+conn.getResponseMessage()); //$NON-NLS-1$ //$NON-NLS-2$
	        return null;
	      }
	      
	      is = conn.getInputStream();
	      is = new ProgressMonitorInputStream(parent, Messages.getString("WebClient.3"), is);
	      
	      ObjectInputStream ois = new ObjectInputStream(is);
	      image = (ImageIconRep)(ois.readObject());

	      
	    } catch (Exception e) {
	      image = null;
	    } finally {
	      try {
	        if (null != is)
	          is.close();
	      } catch (IOException e) {
	      }
	      is = null;
	      conn = null;
	    }

	    return image;
	  }
  
  protected String getString(Map<String,String> params) {
    byte [] data = getBytes(params);
    if(null == data) return null;
    try {
      return new String(data, "UTF-8"); //$NON-NLS-1$
    } catch (UnsupportedEncodingException e) {
      return new String(data);
    }
  }
  
  protected byte [] getBytes(Map<String,String> params) {
    HttpURLConnection conn = null;
    InputStream is = null;

    String downloadUrl = getDownloadLocation();
    String cookie = getCookie();
    URL documentBase = getBaseURL();

    byte [] result = null;
    // Read file
    try {
      // open a URL connection to the Servlet
      URL url = null;

      try {
        url = new URL(downloadUrl);
      } catch (MalformedURLException e) {
        url = new URL(documentBase, downloadUrl);
      }
      
      // Add extra params
      String query = url.getQuery();
      boolean emptyQuery = StringUtils.isBlank(query);
      boolean first = emptyQuery;
      
      StringBuilder sb = new StringBuilder(url.getPath());
      if(!emptyQuery) sb.append(query.trim());
      for(Entry<String,String> entry : params.entrySet()) {
        if(null == entry.getKey() || null == entry.getValue()) continue;
        String key = URLEncoder.encode(entry.getKey(), "UTF-8"); //$NON-NLS-1$
        String value = URLEncoder.encode(entry.getValue(), "UTF-8"); //$NON-NLS-1$
        
        // ckeck if key already exists....
        if(!emptyQuery && (query.contains("&"+key+"=") || query.contains("?"+key+"="))) continue; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        
        sb.append(first?"?":"&").append(key).append("=").append(value); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        first = false;
      }
      
      url = new URL(url.getProtocol(), url.getHost(), url.getPort(), sb.toString());
      
      log.debug("Resulting URL: "+url); //$NON-NLS-1$
      
      // Open a HTTP connection to the URL
      conn = (HttpURLConnection) url.openConnection();
      // Allow Inputs
      conn.setDoInput(true);
      // Allow Outputs
      conn.setDoOutput(false);
      // Don't use a cached copy.
      conn.setUseCaches(false);
      // Use a GET method.
      conn.setRequestMethod("GET"); //$NON-NLS-1$
      // set cookies
      if (null != cookie && cookie.trim().length() > 0)
        conn.setRequestProperty("Cookie", cookie); //$NON-NLS-1$

      conn.connect();
      if(conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
        log.error("Connection failed: "+conn.getResponseCode()+" "+conn.getResponseMessage()); //$NON-NLS-1$ //$NON-NLS-2$
        return null;
      }
      
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      byte [] b = new byte[8192];
      int r;
      is = conn.getInputStream();
      while((r = is.read(b))>=0)
        out.write(b, 0, r);
      result = out.toByteArray();
    } catch (IOException e) {
      log.error("nao foi possivel ler a resposta do servidor", e); //$NON-NLS-1$
      result = null;
    } finally {
      try {
        if (null != is)
          is.close();
      } catch (IOException e) {
      }
      is = null;
      conn = null;
    }

    return result;
  }
}
