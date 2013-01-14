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
