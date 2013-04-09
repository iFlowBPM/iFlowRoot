
package pt.iknow.iflow;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import pt.iknow.floweditor.FlowEditor;

public class RepositoryClassLoader extends URLClassLoader {

  private final File cacheFolder;
  private final RepositoryClient _rep;

  public RepositoryClassLoader (File cacheFolder, RepositoryClient rep) {
    super(new URL[0]);
    
    this._rep = rep;
    this.cacheFolder = cacheFolder;
    try {
      addURL(this.cacheFolder.toURL());
    } catch (Exception e) {
      FlowEditor.log("Error setting cache folder", e);
    }
  }

  public RepositoryClassLoader (File cacheFolder, RepositoryClient rep, ClassLoader parent) {
    super(new URL[0], parent);
    
    this._rep = rep;
    this.cacheFolder = cacheFolder;
    try {
      addURL(this.cacheFolder.toURL());
    } catch (Exception e) {
      FlowEditor.log("Error setting cache folder", e);
    }
  }

  public Class<?> findClass(String name) throws ClassNotFoundException {
    try {
      return super.findClass(name);
    } catch(ClassNotFoundException e) {
      // not found, try repository
      String filename = name.replace('.','/') + ".class"; //$NON-NLS-1$

      byte [] data = _rep.getClassFile(filename);
      storeInCache(filename, data);

      if (data != null && data.length != 0) {
        return defineClass(name,data, 0, data.length);
      }
    }
    
    throw new ClassNotFoundException();
  }
  
  public URL findResource(String name) {
    URL url = super.findResource(name);
    if(null == url) {
      // not found. lets look at the repository
      byte [] data = _rep.getClassFile(name);
      if(null == data) return null;
      storeInCache(name, data);
      
      // try to locate the resource, again
      url = super.findResource(name);
    }
    return url;
  }
  
  
  public void setClassCacheFolder(File classCacheFolder) {
    try {
      addURL(classCacheFolder.toURL());
    } catch (MalformedURLException e) {
      FlowEditor.log("Error setting class cache folder", e);
    }
  }
  
  private synchronized byte [] loadFromCache(String filename) {
    if(cacheFolder == null) return null;
    
    byte [] data = null;
    if(filename.startsWith("/")) filename = filename.substring(1);
    File cachedFile = new File(cacheFolder, filename);
    if(cachedFile.exists() && cachedFile.isFile() && cachedFile.canRead()) {
      // read file contents
      InputStream in = null;
      ByteArrayOutputStream bout = new ByteArrayOutputStream((int)cachedFile.length());
      try {
        in = new FileInputStream(cachedFile);
        
        byte [] b = new byte[8192];
        int r;
        
        while((r = in.read(b))>=0) {
          bout.write(b, 0, r);
        }
        data = bout.toByteArray();
        
      } catch (IOException e) {
        FlowEditor.log("Error loading cached file "+filename, e);
      } finally {
        bout = null;
        if(in != null) {
          try {
            in.close();
          } catch (IOException e) {
          }
        }
      }
    }
    
    return data;
  }
  
  private void storeInCache(String filename, byte [] data) {
    if(cacheFolder == null) return;

    if(filename.startsWith("/")) filename = filename.substring(1);
    File cachedFile = new File(cacheFolder, filename);
    if(cachedFile.exists() && cachedFile.isFile() && cachedFile.canRead()) cachedFile.delete();
    
    // force creation of parent folder
    cachedFile.getParentFile().mkdirs();

    if(data != null) {
      OutputStream out = null;
      boolean delete = false;
      try {
        out = new FileOutputStream(cachedFile);
        out.write(data);
      } catch(IOException e) {
        FlowEditor.log("Error writing cached file "+filename, e);
        delete = true;
      } finally {
        if(null != out) {
          try {
            out.close();
          } catch (IOException e) {
          }
        }
        if(delete) cachedFile.delete();
      }
    }

  }
  
}
