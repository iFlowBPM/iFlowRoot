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
/*
 * <p>Title: DataElement.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) Sep 27, 2005</p>
 * <p>Company: iKnow</p>
 * @author Pedro Monteiro
 * @version 1.0
 */

package pt.iflow.api.services.types;

public class DataElementSet {
    public DataElement[] result = null;
    
    public DataElementSet() {
      super();
    }

    /**
     * @return Returns the result.
     */
    public DataElement[] getResult() {
        return result;
    }

    /**
     * @param result The result to set.
     */
    public void setResult(DataElement[] result) {
        this.result = result;
    }
}
