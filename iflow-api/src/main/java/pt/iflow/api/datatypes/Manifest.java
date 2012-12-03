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
package pt.iflow.api.datatypes;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class Manifest {

  public static String[] getClasses() {
    String[] classes = {
      "pt.iflow.api.datatypes.Integer",      // ID=1 //$NON-NLS-1$
      "pt.iflow.api.datatypes.Double",       // ID=2 //$NON-NLS-1$
      "pt.iflow.api.datatypes.Percentage",   // ID=3 //$NON-NLS-1$
      "pt.iflow.api.datatypes.Text",         // ID=4 //$NON-NLS-1$
      "pt.iflow.api.datatypes.Euro",         // ID=5 //$NON-NLS-1$
      "pt.iflow.api.datatypes.Date",         // ID=6 //$NON-NLS-1$
      "pt.iflow.api.datatypes.CheckBox",     // ID=8 //$NON-NLS-1$
      "pt.iflow.api.datatypes.RadioButton",  // ID=9 //$NON-NLS-1$
      "pt.iflow.api.datatypes.Link",         // ID=10 //$NON-NLS-1$
      "pt.iflow.api.datatypes.FormTableText",      // ID=11 //$NON-NLS-1$
      "pt.iflow.api.datatypes.FormTableSelectBox", // ID=12 //$NON-NLS-1$
      "pt.iflow.api.datatypes.FormTableDocument",  // ID=13 //$NON-NLS-1$
      "pt.iflow.api.datatypes.NIF",  // ID=14 //$NON-NLS-1$
      "pt.iflow.api.datatypes.FormTableDialogBox",  // ID=15 //$NON-NLS-1$
    };
    return classes;
  }

}
