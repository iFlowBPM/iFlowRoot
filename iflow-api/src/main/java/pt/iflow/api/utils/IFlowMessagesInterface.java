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

import java.util.Hashtable;
import java.util.List;

public interface IFlowMessagesInterface {

	/**
	 * Replaces {n} values by passed attributes
	 * 
	 * @param asMessage
	 * @param asAttributes
	 * @return parsed string
	 */
	public abstract String getString(String asMessage, String... asAttributes);

	/**
	 * Replaces {n} values by passed attributes
	 * 
	 * @param asMessage
	 * @param asAttributes
	 * @return parsed string
	 */
    public abstract String getString(String key, Object... asAtributes);

    /**
     * Same functionality as {@link #getString(String, Object...)}, created for java 1.4 compatibility (due to Velocity tool).
     * 
     * @see #getString(String, Object...)
     */
    public abstract String getString(String key, List<String> asAtributes);

	/**
	 * Convenience method for single attribute
	 * 
	 * @param asMessage
	 * @param asAttribute
	 * @return parsed string
	 */
	public abstract String getString(String asMessage, String asAttribute);

	/**
	 * Convenience method for double attribute
	 * 
	 * @param asMessage
	 * @param asAttribute0
	 * @param asAttribute1
	 * @return parsed string
	 */
	public abstract String getString(String asMessage, String asAttribute0,
			String asAttribute1);
	
	public abstract String getString(String key);

	/**
	 * Retrieves all the key/value pair messages from the current resource bundle.
	 * 
	 * @return Hashtable with the retrieved messages
	 */
	public Hashtable<String, String> getMessages();
	
	public boolean hasKey(String key);
}
