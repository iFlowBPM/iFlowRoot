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
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class Port {

   private String name;
   private int connectedBlockId;
   private String connectedPortName;

   public Port() {
   }

   /**
    * Sets the port name.
    * @param nm the port name.
    */
   public void setName(String nm) {
      name = nm;
   }

   /**
    * Gets the port name.
    * @return the port name.
    */
   public String getName() {
      return name;
   }

   /**
    * Sets the connected block id.
    * @param id the connected block id.
    */
   public void setConnectedBlockId(int id) {
      connectedBlockId = id;
   }

   /**
    * Gets the connected block id.
    * @return the connected block id.
    */
   public int getConnectedBlockId() {
      return connectedBlockId;
   }

   /**
    * Sets the connected port name.
    * @param name the connected port name.
    */
   public void setConnectedPortName(String name) {
      connectedPortName = name;
   }

   /**
    * Gets the connected port name.
    * @return the connected port name.
    */
   public String getConnectedPortName() {
      return connectedPortName;
   }

}

