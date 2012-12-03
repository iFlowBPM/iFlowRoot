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

public class NameValuePair<N, V> {
  private N name;
  private V value;

  public NameValuePair() {
  }
  
  public NameValuePair(N name) {
    this();
    this.name = name;
  }
  
  public NameValuePair(N name, V value) {
    this(name);
    this.value = value;
  }

  public N getName() {
    return this.name;
  }

  public V getValue() {
    return this.value;
  }

  public NameValuePair<N, V> setName(N name) {
    this.name = name;
    return this;
  }

  public NameValuePair<N, V> setValue(V value) {
    this.value = value;
    return this;
  }

  public NameValuePair<N, V> setPair(NameValuePair<N, V> nvp) {
    return set(nvp.getName(), nvp.getValue());
  }

  public NameValuePair<N, V> set(N name, V value) {
    this.name = name;
    this.value = value;
    return this;
  }
  
  @Override
  public String toString() {
    String retObj = "";
    if (this.name instanceof String) {
      retObj += (String) this.name;
    }
    if (this.value instanceof String) {
      if (this.name instanceof String) {
        retObj += "=";
      }
      retObj += (String) this.value;
    }
    return retObj;
  }
}
