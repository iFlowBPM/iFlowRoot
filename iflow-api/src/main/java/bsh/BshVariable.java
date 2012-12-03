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
package bsh;

public class BshVariable extends Variable {
  public static final int V_DECLARATION=DECLARATION, V_ASSIGNMENT=ASSIGNMENT;

  /**
   * 
   */
  private static final long serialVersionUID = 2732643176495512454L;

  public BshVariable(String name, Class<?> type, LHS lhs) {
    super(name, type, lhs);
  }

  public BshVariable(String name, Class<?> type, Object value, Modifiers modifiers) throws UtilEvalError {
    super(name, type, value, modifiers);
  }

  public BshVariable(String name, Object value, Modifiers modifiers) throws UtilEvalError {
    super(name, value, modifiers);
  }

  public BshVariable(String name, String typeDescriptor, Object value, Modifiers modifiers) throws UtilEvalError {
    super(name, typeDescriptor, value, modifiers);
  }

}
