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
package pt.iknow.floweditor;

import java.text.MessageFormat;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Map.Entry;

import pt.iflow.api.msg.IMessages;

public class BlockMessages implements IMessages {
  private Hashtable<String, String> msgs;
  
  public BlockMessages(Hashtable<String, String> msgs) {
    this.msgs = new Hashtable<String, String>(msgs);
  }
  
  public BlockMessages(Properties msgs) {
    this.msgs = new Hashtable<String, String>();
    for(Entry<Object,Object> e : msgs.entrySet()) {
      if(e.getKey() == null || e.getValue() == null) continue;
      this.msgs.put(String.valueOf(e.getKey()), String.valueOf(e.getValue()));
    }
  }

  public Hashtable<String, String> getMessages() {
    return new Hashtable<String, String>(msgs);
  }

  public String getString(String asMessage, String... asAttributes) {
    return getString(asMessage, (Object[])asAttributes);
  }

  public String getString(String key, Object... asAttributes) {
    String sMessage = null;
    if (key != null) {
      return MessageFormat.format(getString(key), asAttributes);
    }
    return sMessage;
  }

  public String getString(String key, List<String> asAtributes) {
    return getString(key, null == asAtributes?(Object[])null:asAtributes.toArray());
  }

  public String getString(String asMessage, String asAttribute) {
    return getString(asMessage, new String[]{asAttribute});
  }

  public String getString(String asMessage, String asAttribute0, String asAttribute1) {
    return getString(asMessage, new String[]{asAttribute0, asAttribute1});
  }

  public String getString(String key) {
    if (hasKey(key))
      return msgs.get(key);
    else
      return '!' + key + '!';
  }

  public boolean hasKey(String key) {
    if(null == key)
      return false;
    return msgs.containsKey(key);
  }

}
