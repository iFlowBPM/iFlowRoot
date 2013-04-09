package pt.iflow.core;

import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.NoSuchElementException;

import pt.iflow.api.core.Repository;
import pt.iflow.api.core.RepositoryFile;

public class RepositoryBeanClassLoader extends ClassLoader {

  private Hashtable<String, Class<?>> classTable;
  private String organization; 

  public RepositoryBeanClassLoader (String organization) {
    super(Thread.currentThread().getContextClassLoader());
    this.organization = organization;
    this.classTable = new Hashtable<String, Class<?>>();
  }

  protected void reset() {
    this.classTable = new Hashtable<String, Class<?>>();
  }

  public Class<?> findClass(String name) throws ClassNotFoundException {
    Repository rep = RepositoryBean.getInstance();
    String filename = name.replace('.','/') + ".class";
    Class<?> c = classTable.get(filename);

    if (c != null)
      return c;

    byte[] data = null;
    data = rep.getClassFile(organization, filename).getResouceData();
    
    if (data == null || data.length == 0) throw new ClassNotFoundException();

    c = defineClass(name, data, 0, data.length);
    classTable.put(filename, c);
    
    return c;
  }

  
  protected URL findResource(String name) {
    URL result = null;
    
    Repository rep = RepositoryBean.getInstance();
    RepositoryFile f = rep.getClassFile(organization, name);
    
    if(null != f && f.exists()) {
      result = f.getURL();
    }
    
    return result;
  }

  protected Enumeration<URL> findResources(String name) {
    return new ResourceEnumeration(findResource(name));
  }

  public InputStream getResourceAsStreamX(String name) {
    Repository rep = RepositoryBean.getInstance();
    RepositoryFile f = rep.getClassFile(organization, name);
    if(null == f || !f.exists()) return super.getResourceAsStream(name);
    return f.getResourceAsStream();
  }

  private static final class ResourceEnumeration implements Enumeration<URL> {
    private URL next = null;
    
    public ResourceEnumeration (URL url) {
      this.next = url;
    }
    public boolean hasMoreElements() {
      return (next != null);
    }
    public URL nextElement() {
      if(this.next == null) throw new NoSuchElementException();
      URL next = this.next;
      this.next = null;
      return next;
    }
  }

}
