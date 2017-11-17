/**
 * RepositoryDownload.java
 *
 * Description:
 *
 * History: 01/15/02 - jpms - created.
 * $Id: PublicFiles.java 248 2007-08-01 13:54:31 +0000 (Qua, 01 Ago 2007) uid=mach,ou=Users,dc=iknow,dc=pt $
 */

package pt.iflow.servlets;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.Repository;
import pt.iflow.api.core.RepositoryFile;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.Setup;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.applet.AbstractAppletServletHelper;
import pt.iknow.utils.VelocityUtils;

import com.infosistema.crypto.Base64;
import com.infosistema.crypto.jar.JarSigner;

/**
* 
* <p>Title: </p>
* <p>Description: </p>
* <p>Copyright (c) 2005 iKnow</p>
* 
* @author iKnow
* 
* @web.servlet
* name="AppletServlet"
* load-on-startup="1"
* 
* @web.servlet-mapping
* url-pattern="*.jar"
* @web.servlet-mapping
* url-pattern="/javascript/applet.js"
* @web.servlet-mapping
* url-pattern="*.properties"
*/
public class AppletServlet extends HttpServlet {

  private static final long serialVersionUID = -7694763455961429069L;

  private AbstractAppletServletHelper helper = null;
  private String appletJs = null;
  private Map<String, MetaInfo> metaInfo = new HashMap<String, MetaInfo>();
  private File resourceDir = null;

  private Pattern pattern = Pattern.compile(".*(applet(_..(_..)?)?\\.properties)");
  
  private UserInfoInterface systemUser = null;
  
  private boolean initComplete = false;
  
  public AppletServlet() {
  }

  public void init() throws ServletException {
    resourceDir = new File(Const.fUPLOAD_TEMP_DIR,"appletData");
    resourceDir.mkdirs();

    if(initComplete) return;

    // Para evitar que o servidor aplicacional (tomcat) de timeout no boot, fazemos uma thread.
    new Thread(new Runnable() {
      public void run() {

        File keyStoreFile = null;
        try {
          byte[] b = new byte[8192];
          int r;
          InputStream cpStoreIn = null;
          PrivateKey privateKey;
          Certificate [] certChain;
          final String alias = "infosistema.com";
          final char [] password = "iknow256".toCharArray();
          try {
            //cpStoreIn = AppletServlet.class.getResourceAsStream("store");
        	cpStoreIn = Setup.getResource("store");
            KeyStore store = KeyStore.getInstance("JKS");
            store.load(cpStoreIn, password);
            certChain = store.getCertificateChain(alias);
            privateKey = (PrivateKey) store.getKey(alias, password);
          } catch (Exception e) {
            // notificate que a coisa nao correu bem
            Logger.adminError("AppletServlet", "init", "Could not load signature key.", e);
            return;
          } finally {
            if(null != cpStoreIn) {
              try {
              cpStoreIn.close();
              } catch(IOException e) {}
            }
          }
          
          JarSigner signer = new JarSigner(privateKey, certChain);
          
          MessageDigest md = null;
          try {
            md = MessageDigest.getInstance("SHA-1");
          } catch (NoSuchAlgorithmException e) {
            Logger.adminError("AppletServlet", "init", "SHA1 algorithm not found. Signature not possible.", e);
            return;
          }

          final Attributes.Name digestName = new  Attributes.Name("SHA1-Digest");
          
          File dataCache = new File(resourceDir, "manifest.mf");
          Manifest mf = new Manifest();
          mf.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
          if(dataCache.exists() && dataCache.canRead()) {
            InputStream mfIn = null;
            try {
              mfIn = new FileInputStream(dataCache);
              mf.read(mfIn);
            } catch(IOException e) {
              Logger.adminWarning("AppletServlet", "init", "Could not load manifest", e);
            } finally {
              try {
                mfIn.close();
              } catch (IOException e) {
              }
              mfIn = null;
            }
          }
          
          systemUser = BeanFactory.getUserInfoFactory().newSystemUserInfo();

          // setup applet jars and resources
          helper = AbstractAppletServletHelper.getInstance();
          try {
            appletJs = getAppletScript(systemUser);
          } catch(IOException e) {
            Logger.adminWarning("AppletServlet", "init", "could not generate applet script", e);
          }

          MetaInfo meta = new MetaInfo();
          meta.name = "applet.js";
          meta.contentType = getServletContext().getMimeType(meta.name);
          meta.lastModified = System.currentTimeMillis();
          meta.length = appletJs.length();
          meta.script = true;
          metaInfo.put(meta.name, meta);
          
          
          // register resource bundle (messages)
          Repository rep = BeanFactory.getRepBean();
          RepositoryFile[] messages = rep.listMessages(systemUser);
          
          for(RepositoryFile repFile : messages) {
            String name = repFile.getName();
            Matcher m = pattern.matcher(name);
            if(m.matches()) {
              meta = new MetaInfo();
              meta.name = m.group(1);
              meta.contentType = getServletContext().getMimeType(meta.name);
              meta.lastModified = repFile.getLastModified();
              meta.length = repFile.getSize();
              meta.fromRep = true;
              
              metaInfo.put(meta.name, meta);
            }
          }
          

          StringBuilder sb = new StringBuilder();

          Iterator<String> iter = helper.dependenecies();
          while(iter.hasNext()) {
            String name = iter.next();
            sb.append(",").append(name);

            URL url = getResourceURL(getServletContext(), name);
            if(null == url) continue;
            OutputStream out = null;
            InputStream in = null;
            File tmpFile = null;
            JarFile jarFile = null;
            try {
              meta = new MetaInfo();
              meta.name = name;
              
              
              File signedFile = new File(resourceDir, meta.name);

              // compute signed file digest
              in = url.openStream();
              tmpFile = File.createTempFile("tmp", ".jar", resourceDir);

              // o hash tem que ser do ficheiro dentro do WAR e nao do ficheiro assinado
              md.reset();
              out = new FileOutputStream(tmpFile);
              while((r = in.read(b)) >= 0) {
                out.write(b, 0, r);
                md.update(b, 0, r);
              }
              out.close();
              out = null;
              in.close();
              in = null;

              boolean mustSign = true;
              // byte [] hash = CryptoUtils.computeHash(signedFile, md);
              byte [] hash = md.digest();

              Attributes atts = mf.getAttributes(meta.name);
              if(atts != null) {
                String b64Hash = atts.getValue(digestName);
                // must sign if there is no hash or hashes are different
                mustSign = (null == b64Hash || !MessageDigest.isEqual(hash, Base64.decode(b64Hash))); 
              }

              if(mustSign) {
                Logger.adminInfo("AppletServlet", "init", "Signing file: "+meta.name);
                jarFile = new JarFile(tmpFile);
                out = new FileOutputStream(signedFile);
                
                signer.signJarFile(jarFile, out);

                
                // store hash
                if(atts == null) atts = new Attributes();
                atts.put(digestName, Base64.encodeBytes(hash, Base64.DONT_BREAK_LINES));
                mf.getEntries().put(meta.name, atts);
              } else {
                Logger.adminInfo("AppletServlet", "init", "File not changed... No signing required.");
              }
              
              // Insert metadata entry
              meta.contentType = getServletContext().getMimeType(meta.name);
              meta.lastModified = signedFile.lastModified();
              meta.length = (int) signedFile.length();
              metaInfo.put(meta.name, meta);
              Logger.adminInfo("AppletServlet", "init", "Applet resource registered: "+meta.name);
            } catch (IOException e) {
              Logger.adminWarning("AppletServlet", "init", "Applet resource NOT registered: "+meta.name, e);
            } catch (Throwable t) {
              Logger.adminWarning("AppletServlet", "init", "Unexpected error signing file: "+meta.name, t);
            } finally {
              if(null != out) {
                try {
                  out.close();
                } catch (IOException e) {
                }
              }
              if(null != in) {
                try {
                  in.close();
                } catch (IOException e) {
                }
              }
              if(null != tmpFile) tmpFile.delete();
            }
          }
          
          
          // write manifest for the future
          {
            OutputStream mfOut = null;
            try {
              mfOut = new FileOutputStream(dataCache);
              mf.write(mfOut);
            } catch(IOException e) {
              Logger.adminWarning("AppletServlet", "init", "Could not save manifest", e);
            } finally {
              try {
                mfOut.close();
              } catch (IOException e) {
              }
              mfOut = null;
            }
          }
          
          Logger.adminDebug("AppletServlet", "init", sb.toString());
        } finally {
          initComplete = true;
          if(null != keyStoreFile) keyStoreFile.delete();
          try {
            Logger.adminDebug("AppletServlet", "init", resourceDir.getCanonicalPath());
          } catch (IOException e) {
          }
          Logger.adminDebug("AppletServlet", "init", "AppletServlet init complete.");
        }
      }
    }).start();

  }
  
  protected MetaInfo sendHeader(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String name = request.getRequestURI();
    if(null == name) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return null;
    }
    
    String [] tokens = name.split("/");
    if(tokens.length < 2) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return null;
    }
    
    String dir = tokens[tokens.length-2];
    String file = tokens[tokens.length-1];
    
    Logger.adminInfo("AppletServlet", "sendHeader", "Requesting resource (file): ");
    
    // check if we have the correct resource
    if("applet.js".equals(file) && !"javascript".equals(dir)) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return null;
    }
    
    
    MetaInfo meta = metaInfo.get(file);
    if(null == meta) { // not registered with us...
      return null;
    }
    
    if(meta.fromRep) {
      UserInfoInterface user = (UserInfoInterface) request.getSession().getAttribute(Const.USER_INFO);
      if(null == user) user = systemUser;
      RepositoryFile repFile = BeanFactory.getRepBean().getMessagesFile(user, meta.name);
      if(null == repFile || !repFile.exists()) {
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
        return null;
      }
      meta.lastModified = repFile.getLastModified();
      meta.length = repFile.getSize();
    }
    
    long modifiedSince = request.getDateHeader("If-Modified-Since");
    Logger.debug("", this, "", "If-Modified-Since="+modifiedSince);
    if(!meta.script && meta.lastModified<=modifiedSince){
      Logger.debug("", this, "", "file not modified.");
      response.sendError(HttpServletResponse.SC_NOT_MODIFIED, "File not modified");
      return null;
    }

    // prepare response
    response.setContentType(meta.contentType);
    if(!meta.script) {
      response.setContentLength(meta.length);
      response.setDateHeader("Last-Modified", meta.lastModified);
    }
    
    return meta;
  }
  
  private void checkLoaded() {
    while(!initComplete) {
      try {
        Thread.sleep(200L);
      } catch (InterruptedException e) {
      }
    }
  }
  
  protected void doHead(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    checkLoaded();
    sendHeader(request, response);
  }

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    checkLoaded();
    
    MetaInfo meta = sendHeader(request, response);
    if(null == meta) return;
    
    UserInfoInterface userInfo = (UserInfoInterface) request.getSession().getAttribute(Const.USER_INFO);
    if(null == userInfo) userInfo = systemUser;
    
    InputStream is = null;
    ServletOutputStream out = null;
    PrintWriter writer = null;
    try {
      if("applet.js".equals(meta.name)) {
        String script = getAppletScript(userInfo);
        response.setContentLength(script.length());
        // send applet
        writer = response.getWriter();
        writer.print(script);
        writer.flush();
      } else if(pattern.matcher(meta.name).matches()) {
        RepositoryFile file = BeanFactory.getRepBean().getMessagesFile(userInfo, meta.name);
        out = response.getOutputStream();
        file.writeToStream(out);
        out.flush();
      } else {
        // Send jar
        is = getResource(getServletContext(), meta.name);
        out = response.getOutputStream();
        byte [] b = new byte[8192];
        int r;
        while((r = is.read(b))>0)
          out.write(b, 0, r);

        out.flush();
      }
    } finally {
      // release resources
      if(null != writer) writer.close();
      
      try {
        if(null != is) is.close();
      } catch(IOException e) {}
      
      try {
        if(null != out) out.close();
      } catch(IOException e) {}
    }
  }

  
  private String getAppletScript(UserInfoInterface userInfo) throws IOException {
    Hashtable<String, Object> ctx = helper.getAppletScriptContext();
    ctx.put(AbstractAppletServletHelper.VAR_APPLET_LANG, userInfo.getUserSettings().getLangString());

    RepositoryFile rep = BeanFactory.getRepBean().getTheme(userInfo, "applet.vm");
    Reader reader = null;
    InputStream in = null;
    try {
      in = rep.getResourceAsStream();
      reader = new InputStreamReader(in, "UTF-8");
      return VelocityUtils.processTemplate(ctx, reader);
    } finally {
      if(null != reader) {
        try {
          reader.close();
        } catch (IOException e) {
        }
      }
      if(null != in) {
        try {
          in.close();
        } catch (IOException e) {
        }
      }
    }
  }
  
  private static class MetaInfo {
    String name;
    String contentType;
    long lastModified;
    int length;
    boolean fromRep = false;
    boolean script = false;
  }
  
  
  private InputStream getResource(ServletContext ctx, String resource) throws IOException {
    File f = new File(resourceDir, resource);
    if(f.exists() && f.isFile() && f.canRead())
      return new FileInputStream(f);
    return null;
  }

  private URL getResourceURL(ServletContext ctx, String resource) {
    URL result = null;
    
    Logger.adminDebug("AppletServlet", "getResourceURL", "Looking up resource: "+resource);
    
    try {
      result = getServletContext().getResource("/WEB-INF/lib/"+resource);
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
    
    // not found, try repository (System/Classes)
    if(null == result) {
      Logger.adminInfo("AppletServlet", "getResourceURL", "Not found, trying repository...");
      Repository rep = BeanFactory.getRepBean();
      RepositoryFile repFile = rep.getClassFile(Const.SYSTEM_ORGANIZATION, resource);
      if(null != repFile && repFile.exists())
        result = repFile.getURL();
    }
    
    return result;
  }

}

