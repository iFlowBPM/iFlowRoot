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
package pt.iflow.api.utils;

import java.lang.reflect.Method;

/**
 * Classe auxiliar para reflexão em java
 * @author oscar
 *
 */
public final class Reflex {
  
  private Object instance;
  private Class<?> instanceClass;
  
  public Reflex(Object instance) {
    if(null == instance) throw new NullPointerException("Null instance not allowed");
    this.instance = instance;
    this.instanceClass = instance.getClass();
  }
  
  public Reflex(Class<?> instanceClass) {
    if(null == instanceClass) throw new NullPointerException("Null class not allowed");
    this.instance = null;
    this.instanceClass = instanceClass;
  }
  
  public Object getInstance() {
    return this.instance;
  }

  public Class<?> getInstanceClass() {
    return this.instanceClass;
  }

  public int getInt(String fld) throws ReflexException {
    try {
      return this.instanceClass.getField(fld).getInt(this.instance);
    } catch (Exception e) {
      throw new ReflexException(e);
    }
  }
  
  public double getDouble(String fld) throws ReflexException {
    try {
      return this.instanceClass.getField(fld).getDouble(this.instance);
    } catch (Exception e) {
      throw new ReflexException(e);
    }
  }
  
  public long getLong(String fld) throws ReflexException {
    try {
      return this.instanceClass.getField(fld).getLong(this.instance);
    } catch (Exception e) {
      throw new ReflexException(e);
    }
  }
  
  public float getFloat(String fld) throws ReflexException {
    try {
      return this.instanceClass.getField(fld).getFloat(this.instance);
    } catch (Exception e) {
      throw new ReflexException(e);
    }
  }
  
  public boolean getBoolean(String fld) throws ReflexException {
    try {
      return this.instanceClass.getField(fld).getBoolean(this.instance);
    } catch (Exception e) {
      throw new ReflexException(e);
    }
  }
  
  public String getString(String fld) throws ReflexException {
    return (String) get(fld);
  }
  
  public Object get(String fld) throws ReflexException {
    try {
      return this.instanceClass.getField(fld).get(this.instance);
    } catch (Exception e) {
      throw new ReflexException(e);
    }
  }
  
  public Object call(String mtd, Object ... args) throws ReflexException {
    Method [] mtds = this.instanceClass.getMethods();
    Method method = null;
    for(Method m : mtds) { // find candidates
      if(m.getName().equals(mtd) && m.getParameterTypes().length == args.length) {
        // this is a candidate.
        boolean perfect = true;
        boolean ok = true;
        Class<?>[] paramTypes = m.getParameterTypes();
        for(int i = 0; i < paramTypes.length; i++) {
          if(!paramTypes[i].equals(args[i].getClass())) {
            perfect = false;
          }
          if (!paramTypes[i].isAssignableFrom(args[i].getClass())) {
            ok = false;
            break; // nao precisa de continuar, este não presta
          }
        }
        if(perfect) {
          method = m;
          break;
        }
        if(ok && method == null) method = m; // i'm not sure how to choose between two methods, so let be the first....
      }
    }
    
    if(null == method) throw new ReflexException("Matching method not found...");
    
    try {
      return method.invoke(this.instance, args);
    } catch (Exception e) {
      throw new ReflexException(e);
    }
  }

  public boolean callBoolean(String mtd, Object ... args) throws ReflexException {
    Object o = call(mtd,args);
    boolean result = false;
    try {
      result = ((Boolean) o).booleanValue();
    } catch (Throwable t) {
      throw new ReflexException(t);
    }
    return result;
  }
  public int callInt(String mtd, Object ... args) throws ReflexException {
    Object o = call(mtd,args);
    int result = -1;
    try {
      result = ((Number) o).intValue();
    } catch (Throwable t) {
      throw new ReflexException(t);
    }
    return result;
  }
  public double callDouble(String mtd, Object ... args) throws ReflexException {
    Object o = call(mtd,args);
    double result = -1;
    try {
      result = ((Number) o).doubleValue();
    } catch (Throwable t) {
      throw new ReflexException(t);
    }
    return result;
  }
  public long callLong(String mtd, Object ... args) throws ReflexException {
    Object o = call(mtd,args);
    long result = -1;
    try {
      result = ((Number) o).longValue();
    } catch (Throwable t) {
      throw new ReflexException(t);
    }
    return result;
  }

}
