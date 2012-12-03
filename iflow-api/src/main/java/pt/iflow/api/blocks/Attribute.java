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
package pt.iflow.api.blocks;


/**
 * <p>Title: Attribute</p>
 * <p>Description: This class represents an attribute, with setters and getters for name and value </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: iKnow </p>
 * @author João Valentim
 * @version 1.0
 */

public class Attribute {

   private String name;
   private String value;

   public Attribute() {
   }

   public Attribute(String nm, String vl) {
      name  = nm;
      value = vl;
   }

   /**
    * Sets the attribute name.
    * @param nm the attribute name.
    */
   public void setName(String nm) {
      name = nm;
   }

   /**
    * Gets the attribute name.
    * @return the attribute name.
    */
   public String getName() {
      return name;
   }

   /**
    * Sets the attribute value.
    * @param nm the attribute value.
    */
   public void setValue(String vl) {
      value = vl;
   }

   /**
    * Gets the attribute value.
    * @return the attribute value.
    */
   public String getValue() {
      return value;
   }
}

