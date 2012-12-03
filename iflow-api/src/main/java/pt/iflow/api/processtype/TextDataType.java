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
package pt.iflow.api.processtype;


public class TextDataType implements ProcessDataType {

  public Object convertFrom(String rawvalue) {
    return rawvalue;
  }

  public String convertTo(Object value) {
    return value == null ? "" : String.valueOf(value);
  }

  public String format(Object obj) {
    return obj == null ? null : String.valueOf(obj);
  }

  public Object parse(String source) {
    return source;
  }

  public String toString() {
    return "Text";
  }

  public Class<?> getSupportingClass() {
    return String.class;
  }

  public boolean isBindable() {
    return false;
  }
  
}
