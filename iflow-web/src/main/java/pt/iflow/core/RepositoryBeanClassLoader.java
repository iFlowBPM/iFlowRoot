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
