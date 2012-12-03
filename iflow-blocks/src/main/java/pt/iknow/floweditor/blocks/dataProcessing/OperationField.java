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
package pt.iknow.floweditor.blocks.dataProcessing;

import java.io.Serializable;

public class OperationField implements Serializable {
  private static final long serialVersionUID = 1823292921106032981L;
  
  public static final int TYPE_EXPRESSION = 1;
  public static final int TYPE_ANY = 2;
  public static final int TYPE_SCALAR = 3;
  public static final int TYPE_ARRAY = 4;

  private String desc;
  private int type;
  private String name;

  protected OperationField(String name, String desc) {
    this(TYPE_EXPRESSION, name, desc);
  }

  protected OperationField(int type, String name, String desc) {
    this.desc = desc;
    this.type = type;
    this.name = name;
  }

  public String getDesc() {
    return desc;
  }

  public int getType() {
    return type;
  }

  public String getName() {
    return name;
  }
}
