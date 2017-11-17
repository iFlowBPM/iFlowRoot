package pt.iflow.utils.scanner;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JApplet;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import pt.iflow.applet.Messages;


public class FileApplet extends JApplet {
  private static final long serialVersionUID = -4868295868523367489L;

  private ScannerAccess _access;
  private FileSigner _signer;
  private Map<String,File> _files;
  private Map<String,String> _varNames;
  private Set<String> _toSign;

  public void init() {
   // JFrame.setDefaultLookAndFeelDecorated(true);
    try {
      super.init();
      _files = new HashMap<String,File>();
      _varNames = new HashMap<String, String>();
      _toSign = new HashSet<String>();
      AccessController.doPrivileged(new PrivilegedExceptionAction<String>() {
        public String run() {
          _access = new ScannerAccess();
          _signer = new PDFSigner();
          ImageIO.getImageWritersByFormatName("jpeg").next(); // ensure jpeg writer is loaded //$NON-NLS-1$

          try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
          } catch (Throwable t) { }
          return "done"; //$NON-NLS-1$
        }
      });
    } catch (PrivilegedActionException e) {
      e.printStackTrace();
    }
    System.out.println("Init complete"); //$NON-NLS-1$
  }

  public void start() {
    super.start(); 
    System.out.println("Start complete"); //$NON-NLS-1$
  }

  public void stop() {
    super.stop();
    System.out.println("Stop complete"); //$NON-NLS-1$
  }

  public synchronized boolean canScan() {
    //System.out.println("canScan start"); //$NON-NLS-1$
    boolean canScan = false;
    try {
      canScan = AccessController.doPrivileged(new PrivilegedExceptionAction<Boolean>() {
        public Boolean run() {
          return (_access != null & _access.isActive());
        }
      });
    } catch (PrivilegedActionException e) {
      e.printStackTrace();
    }
    System.out.println("canScan complete"); //$NON-NLS-1$
    return canScan;
  }

  public synchronized boolean canSign() {
   // System.out.println("canSign start"); //$NON-NLS-1$
    boolean canSign = false;
    try {
      canSign = AccessController.doPrivileged(new PrivilegedExceptionAction<Boolean>() {
        public Boolean run() {
          return (_signer != null & _signer.isActive());
        }
      });
    } catch (PrivilegedActionException e) {
      e.printStackTrace();
    }
    System.out.println("canSign complete"); //$NON-NLS-1$
    return canSign;
  }

  public synchronized String load(final String variableName) {
//    System.out.println("load start"); //$NON-NLS-1$
    String result = null;
    try {
      result = AccessController.doPrivileged(new PrivilegedExceptionAction<String>() {
        public String run() {
          return doLoad(variableName);
        }
      });
    } catch (PrivilegedActionException e) {
      e.printStackTrace();
    }

    System.out.println("load complete"); //$NON-NLS-1$
    return result;
  }

  public synchronized String loadAndSign(final String variableName) {
    //System.out.println("loadAndSign start"); //$NON-NLS-1$
	
    String result = null;
//    try {
//      result = AccessController.doPrivileged(new PrivilegedExceptionAction<String>() {
//        public String run() {
//          return doLoadAndSign(variableName);
//        }
//      });
//    } catch (PrivilegedActionException e) {
//      e.printStackTrace();
//    }
//    System.out.println("loadAndSign complete"); //$NON-NLS-1$
    return result;
  }

  public synchronized String scan(final String variableName) {
   // System.out.println("scan start"); //$NON-NLS-1$
    String result = null;
//    try {
//      result = AccessController.doPrivileged(new PrivilegedExceptionAction<String>() {
//        public String run() {
//          return doScan(variableName);
//        }
//      });
//    } catch (PrivilegedActionException e) {
//      e.printStackTrace();
//    }
//    System.out.println("scan complete"); //$NON-NLS-1$
    return result;
  }

  public synchronized String sendAll(String urlString, String flowid, String pid, String subpid) {
    return sendAll(urlString, flowid, pid, subpid, null);
  }

  public synchronized String sendAll(final String urlString, final String flowid, final String pid, final String subpid, final String cookies) {
    //System.out.println("sendAll start"); //$NON-NLS-1$
    String result = null;
//    try {
//      result = AccessController.doPrivileged(new PrivilegedExceptionAction<String>() {
//        public String run() {
//          return doSendAll(new ClientRequestInfo(urlString, flowid, pid, subpid, cookies));
//        }
//      });
//    } catch (PrivilegedActionException e) {
//      e.printStackTrace();
//    }
//    System.out.println("sendAll complete"); //$NON-NLS-1$
    return result;
  }

  public String getVariableName(String fileID) {
    return (String) _varNames.get(fileID);
  }


  public void remove(String filename) {
    this._toSign.remove(filename);
    this._files.remove(filename);
    this._varNames.remove(filename);
  }


  public String signLoadedFile(final String fileId, final String downloadUrl, final String uploadUrl, final String flowid, final String pid, final String subpid, final String cookies) {
//    System.out.println("signLoadedFile start"); //$NON-NLS-1$
    String result = null;
//    try {
//      result = AccessController.doPrivileged(new PrivilegedExceptionAction<String>() {
//        public String run() {
//          ClientRequestInfo cli = new ClientRequestInfo(uploadUrl, flowid, pid, subpid, cookies);
//          return doSignLoadedFile(cli, fileId, downloadUrl);
//        }
//      });
//    } catch (PrivilegedActionException e) {
//      e.printStackTrace();
//    }
//    System.out.println("signLoadedFile complete"); //$NON-NLS-1$
    return result;
  }

  public String verify(final String downloadUrl, final String cookies) {
    //System.out.println("signLoadedFile start"); //$NON-NLS-1$
    String result = null;
//    try {
//      result = AccessController.doPrivileged(new PrivilegedExceptionAction<String>() {
//        public String run() {
//          return doVerify(downloadUrl, cookies);
//        }
//      });
//    } catch (PrivilegedActionException e) {
//      e.printStackTrace();
//    }
//    System.out.println("signLoadedFile complete"); //$NON-NLS-1$
    return result;
  }

  private String doLoad(String variableName) {
    JFileChooser fd = new JFileChooser();

    fd.showOpenDialog(this);

    File theFile = fd.getSelectedFile();
    String fileName = null;

    if(theFile != null && theFile.canRead() && theFile.isFile()) {
      fileName = theFile.getName();
      _files.put(fileName, theFile);
      _varNames.put(fileName, variableName);
    }

    return fileName;
  }

  private String doScan(String variableName) {
    String name = null;

    if(this.canScan()) {
      try {
        File file = this._access.scan();
        name = file.getName();
        _files.put(name, file);
        _varNames.put(name, variableName);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    return name;
  }

  private String doSendAll(ClientRequestInfo clientId) {
    if(canSign() && !this._toSign.isEmpty()) {
      this._signer.loadSignature(this);
      // sign files...
      for(Iterator<String> iter = this._toSign.iterator(); iter.hasNext();) {
        String file = iter.next();
        File signedDoc = this._signer.sign(this._files.get(file));
        if(null == signedDoc) {
          JOptionPane.showMessageDialog(this, Messages.getString("FileApplet.21")+file, Messages.getString("FileApplet.22"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
          this._files.remove(file);
          this._varNames.remove(file);
        } else {
          this._files.put(file, signedDoc);
        }
      }

    }


    Map<String,String> result =  postFiles(clientId, this._files, false);
    System.out.println("File IDs: "); //$NON-NLS-1$
    StringBuffer sb = new StringBuffer();
    for(String name : result.keySet()) {
      String fid = result.get(name);
      System.out.println("   "+ name +" => '"+fid+"'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      if(null != fid) {
        sb.append(fid).append(';');
      }
    }
    
    return sb.toString();
  }

  private String doLoadAndSign(String variableName) {
    if(!canSign()) return null;

    JFileChooser jfc = new JFileChooser();
    jfc.setFileFilter(new ExtensionFileFilter(Messages.getString("FileApplet.27"), new String [] {"pdf"})); //$NON-NLS-1$ //$NON-NLS-2$

    jfc.showOpenDialog(this);

    File theFile = jfc.getSelectedFile();
    String fileName = null;
    File fileData = null;

    if(theFile != null && theFile.canRead() && theFile.isFile()) {
      fileName = theFile.getName();
      _toSign.add(fileName);
      _files.put(fileName, fileData);
      _varNames.put(fileName, variableName);
    }

    return fileName;
  }


  private String doSignLoadedFile(ClientRequestInfo client, String fileId, String downloadUrl) {
    if(!canSign()) return null;
    // ensure key is loaded
    this._signer.loadSignature(this);

    String retObj = null;
    File data = getDocument(downloadUrl, client.cookies);

    if(data == null) {
      JOptionPane.showMessageDialog(this, Messages.getString("FileApplet.29"), Messages.getString("FileApplet.30"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
      return null;
    }

    // sign document
    try {
      data = this._signer.sign(data);
    } catch (Throwable t) {
      t.printStackTrace();
    }

    if(data == null) {
      JOptionPane.showMessageDialog(this, Messages.getString("FileApplet.31") + //$NON-NLS-1$
          Messages.getString("FileApplet.32") + //$NON-NLS-1$
          Messages.getString("FileApplet.33"), Messages.getString("FileApplet.34"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
      return null;
    }

    Map<String, File> files = new HashMap<String, File>();
    files.put(fileId, data);

    Map<String,String> result = postFiles(client, files, true);
    retObj = result.get(fileId);
    
    System.out.println("File ID: "+fileId+"; Upload ID: "+retObj); //$NON-NLS-1$ //$NON-NLS-2$
    
    if(retObj == null) {
      JOptionPane.showMessageDialog(this, Messages.getString("FileApplet.37") + //$NON-NLS-1$
          Messages.getString("FileApplet.38") + //$NON-NLS-1$
          Messages.getString("FileApplet.39"), Messages.getString("FileApplet.40"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
    }

    return retObj;
  }


  private String doVerify(final String fileUrl, final String cookies) {
    if(!canSign()) return null;
    // ensure key is loaded
    this._signer.loadSignature(this);
    String retObj = null;

    File data = getDocument(fileUrl, cookies);

    if(data == null) {
      JOptionPane.showMessageDialog(this, Messages.getString("FileApplet.41"), Messages.getString("FileApplet.42"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
      return null;
    }


    // verify document
    try {
      retObj = this._signer.verify(data);
    } catch (Throwable t) {
      t.printStackTrace();
    }

    if(retObj != null)
      JOptionPane.showMessageDialog(this, retObj, Messages.getString("FileApplet.43"), JOptionPane.INFORMATION_MESSAGE); //$NON-NLS-1$

    return retObj;
  }

  private Map<String,String> postFiles(ClientRequestInfo clientId,  Map<String,File> files, boolean update) {
    Map<String,String> retObj = new HashMap<String, String>(files.size());

    // Create URL object for upload service 
    URL url = null;
    try {
      url = new URL(clientId.url);
    } catch (MalformedURLException ex) {
      System.out.println("From ServletCom CLIENT REQUEST:"+ex); //$NON-NLS-1$
      try {
        url = new URL(getDocumentBase(), clientId.url);
      }
      catch (MalformedURLException e) {
        System.out.println("From ServletCom CLIENT REQUEST:"+e); //$NON-NLS-1$
        return null;
      }
    }


    // upload document
    final String lineEnd = "\r\n"; //$NON-NLS-1$
    final String twoHyphens = "--"; //$NON-NLS-1$
    final String boundary =  "---------------------------"+System.currentTimeMillis(); //$NON-NLS-1$

    HttpURLConnection conn = null;
    DataOutputStream dos = null;
    InputStream inStream = null;

    int pos=0;

    byte [] buffer = new byte[8192];
    int r;
    for(String fName : files.keySet()) {
      if(null == fName) continue;
      File f = files.get(fName);
      // ignore baddies....
      if(null == f) continue;
      if(!f.canRead()) continue;
      if(!f.isFile()) continue;

      InputStream in = null;
      try {
        //------------------ CLIENT REQUEST

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

        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary); //$NON-NLS-1$ //$NON-NLS-2$

        // set cookies
        if(null != clientId.cookies && clientId.cookies.trim().length() > 0)
          conn.setRequestProperty("Cookie", clientId.cookies); //$NON-NLS-1$

        dos = new DataOutputStream( conn.getOutputStream() );

        // send process identification
        dos.writeBytes(twoHyphens + boundary + lineEnd);
        dos.writeBytes("Content-Disposition: form-data; name=\"flowid\"" + lineEnd); //$NON-NLS-1$
        dos.writeBytes("Content-Type: text/plain; charset=UTF-8" + lineEnd); //$NON-NLS-1$
        dos.writeBytes("Content-Transfer-Encoding: 8bit" + lineEnd); //$NON-NLS-1$
        dos.writeBytes(lineEnd);
        dos.write(clientId.flowid.getBytes("UTF-8")); //$NON-NLS-1$
        dos.writeBytes(lineEnd);
        dos.writeBytes(twoHyphens + boundary + lineEnd);
        dos.writeBytes("Content-Disposition: form-data; name=\"pid\"" + lineEnd); //$NON-NLS-1$
        dos.writeBytes("Content-Type: text/plain; charset=UTF-8" + lineEnd); //$NON-NLS-1$
        dos.writeBytes("Content-Transfer-Encoding: 8bit" + lineEnd); //$NON-NLS-1$
        dos.writeBytes(lineEnd);
        dos.write(clientId.pid.getBytes("UTF-8")); //$NON-NLS-1$
        dos.writeBytes(lineEnd);
        dos.writeBytes(twoHyphens + boundary + lineEnd);
        dos.writeBytes("Content-Disposition: form-data; name=\"subpid\"" + lineEnd); //$NON-NLS-1$
        dos.writeBytes("Content-Type: text/plain; charset=UTF-8" + lineEnd); //$NON-NLS-1$
        dos.writeBytes("Content-Transfer-Encoding: 8bit" + lineEnd); //$NON-NLS-1$
        dos.writeBytes(lineEnd);
        dos.write(clientId.subpid.getBytes("UTF-8")); //$NON-NLS-1$
        dos.writeBytes(lineEnd);

        if(update) {
          // mark as update
          dos.writeBytes(twoHyphens + boundary + lineEnd);
          dos.writeBytes("Content-Disposition: form-data; name=\"update\"" + lineEnd); //$NON-NLS-1$
          dos.writeBytes("Content-Type: text/plain; charset=UTF-8" + lineEnd); //$NON-NLS-1$
          dos.writeBytes("Content-Transfer-Encoding: 8bit" + lineEnd); //$NON-NLS-1$
          dos.writeBytes(lineEnd);
          dos.write("true".getBytes("UTF-8")); //$NON-NLS-1$ //$NON-NLS-2$
          dos.writeBytes(lineEnd);
        }
        
        
        // Upload file
        dos.writeBytes(twoHyphens + boundary + lineEnd);
        dos.writeBytes("Content-Disposition: form-data; name=\"upload\"; filename=\"" + fName +"\"" + lineEnd); //$NON-NLS-1$ //$NON-NLS-2$
        dos.writeBytes("Content-Length: " + f.length() + lineEnd); //$NON-NLS-1$
        dos.writeBytes(lineEnd);

        // output file
        in = new FileInputStream(f);
        while((r=in.read(buffer)) > 0)
          dos.write(buffer, 0, r);

        // send multipart form data necesssary after file data...
        dos.writeBytes(lineEnd);
        dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

        dos.flush();
        dos.close();

        // Read response
        inStream = null;
        try {
          ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
          inStream = conn.getInputStream();
          while((r = inStream.read(buffer)) > 0) {
            dataStream.write(buffer, 0, r);
          }
          
          retObj.put(fName, dataStream.toString("UTF-8")); //$NON-NLS-1$
          pos++;

          inStream.close();

        }
        catch (IOException ioex) {
          System.out.println("From (ServerResponse): "+ioex); //$NON-NLS-1$
        } finally {
          try {
            if(inStream != null) inStream.close();
          } catch(IOException e) {}
        }

      }
      catch (IOException ex) {
        System.out.println("From ServletCom CLIENT REQUEST:"+ex); //$NON-NLS-1$
      }
      finally {
        try {
          if(in != null) in.close();
        } catch (IOException e) {}
      }
      System.out.println("Done with: "+fName); //$NON-NLS-1$
    }
    
    return retObj;
  }

  private File getDocument(final String fileUrl, final String cookies) {
    HttpURLConnection conn = null;
    InputStream is = null;


    File file = null;
    FileOutputStream fout = null;
    // Read file
    try {
      // open a URL connection to the Servlet 

      URL url = null;

      try {
        url = new URL(fileUrl);
      } catch(MalformedURLException e) {
        url = new URL(this.getDocumentBase(), fileUrl);
      }
      System.out.println(url);


      // Open a HTTP connection to the URL

      conn = (HttpURLConnection) url.openConnection();

      // Allow Inputs
      conn.setDoInput(true);

      // Allow Outputs
      conn.setDoOutput(true);

      // Don't use a cached copy.
      conn.setUseCaches(false);

      // Use a post method.
      conn.setRequestMethod("GET"); //$NON-NLS-1$

      // set cookies
      if(null != cookies && cookies.trim().length() > 0)
        conn.setRequestProperty("Cookie", cookies); //$NON-NLS-1$

      conn.connect();
      is = conn.getInputStream();

      File tmpFile = File.createTempFile("sign _", ".tmp"); //$NON-NLS-1$ //$NON-NLS-2$


      fout = new FileOutputStream(tmpFile);
      byte [] b = new byte[8192];
      int r = 0;
      while((r = is.read(b)) != -1) fout.write(b, 0, r);

      file = tmpFile;
    } catch (IOException e) {
      e.printStackTrace();
      file = null;
    } finally {
      try {
        if(null != fout) fout.close();
      } catch (IOException e) {
      }
      try {
        if (null != is) is.close();
      } catch (IOException e) {
      }
      is = null;
      conn = null;
      fout = null;
    }

    return file;
  }


  static class ClientRequestInfo {
    String url;
    String flowid;
    String pid;
    String subpid;
    String cookies;

    ClientRequestInfo(String url,String flowid, String pid, String subpid, String cookies) {
      this.url = url;
      this.flowid = flowid;
      this.pid = pid;
      this.subpid = subpid;
      this.cookies = cookies;
    }
    
    public String toString() {
      StringBuilder sb = new StringBuilder("Client Request Info"); //$NON-NLS-1$
      sb.append(" url: '").append(url).append("'; flowid=").append(flowid); //$NON-NLS-1$ //$NON-NLS-2$
      sb.append("; pid=").append(pid).append("; subpid=").append(subpid); //$NON-NLS-1$ //$NON-NLS-2$
      return sb.toString();
    }
  }
}
