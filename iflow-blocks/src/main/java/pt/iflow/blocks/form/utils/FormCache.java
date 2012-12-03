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
package pt.iflow.blocks.form.utils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import pt.iflow.api.blocks.FormProps;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.flows.FlowSetting;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.Utils;
import pt.iflow.blocks.BlockFormulario;
import pt.iflow.blocks.form.FieldInterface;

public class FormCache {

  private static final MessageFormat idformat = new MessageFormat("{0}/{1}");
  
  private static List<FormButton> _alDEF_BUTTONS = null;

  // Block's attributes cache grouped by field's position
  // KEY: Flow ID + "/" + Block ID (String)
  // VALUE: HashMap with field's attributes cache
  // KEY: field position (Integer)
  // VALUE: attributes(HashMap))
  // KEY: name(String)
  // VALUE: value(String))
  private static HashMap<String, HashMap<Integer, Properties>[]> _hmFieldAttrsCache = new HashMap<String, HashMap<Integer, Properties>[]>();

  // Block's list attributes cache grouped by field's position
  // KEY: Flow ID + "/" + Block ID (String)
  // VALUE: HashMap with field's attributes cache
  // KEY: field position (Integer)
  // VALUE: attributes(HashMap))
  // KEY: name(String)
  // VALUE: value(String))
  private static HashMap<String,String> _hmFieldListAttrsCache = new HashMap<String,String>();

  // Block's ordered field list cache
  // KEY: Flow ID + "/" + Block ID (String)
  // VALUE: Ordered field list (ArrayList(FieldInterface))
  private static HashMap<String, ArrayList<FieldInterface>> _hmFieldsCache = new HashMap<String, ArrayList<FieldInterface>>();

  // Block's ordered button list cache
  // KEY: Flow ID + "/" + Block ID (String)
  // VALUE: Ordered button list (ArrayList(HashMap with button's attributes))
  private static HashMap<String, List<FormButton>> _hmButtonCache = new HashMap<String, List<FormButton>>();

  private static final FormCache INSTANCE = new FormCache();
  
  static {
    _alDEF_BUTTONS = new ArrayList<FormButton>();
    _alDEF_BUTTONS.add(new FormButton(0, 0, FormButtonType.CANCEL));
    _alDEF_BUTTONS.add(new FormButton(1, 1, FormButtonType.RESET));
    _alDEF_BUTTONS.add(new FormButton(2, 2, FormButtonType.SAVE));
    _alDEF_BUTTONS.add(new FormButton(3, 3, FormButtonType.PRINT));
    _alDEF_BUTTONS.add(new FormButton(4, 4, FormButtonType.NEXT));
  }
  
  private FormCache() {}
  
  private static String getFullId(BlockFormulario block) {
    return idformat.format(new Object[] {block.getFlowId(), block.getId()});
  }
  
  public static void refreshCache(BlockFormulario block) {
    String fullid = getFullId(block);
    if (_hmFieldAttrsCache != null && _hmFieldAttrsCache.containsKey(fullid)) {
      _hmFieldAttrsCache.remove(fullid);
    }
    if (_hmFieldListAttrsCache != null && _hmFieldListAttrsCache.containsKey(fullid)) {
      _hmFieldListAttrsCache.remove(fullid);
    }
    if (_hmFieldsCache != null && _hmFieldsCache.containsKey(fullid)) {
      _hmFieldsCache.remove(fullid);
    }
    if (_hmButtonCache != null && _hmButtonCache.containsKey(fullid)) {
      _hmButtonCache.remove(fullid);
    }
    init(block);
  }
  
  public static void init(BlockFormulario block) {
    String fullid = getFullId(block);    
    if ((_hmFieldAttrsCache == null || !_hmFieldAttrsCache.containsKey(fullid))
        && (_hmFieldListAttrsCache == null || !_hmFieldListAttrsCache.containsKey(fullid))) {
      generateFieldAttrsCache(block);
    }
    if (_hmFieldsCache == null || !_hmFieldsCache.containsKey(fullid)) {
      generateFieldsCache(block);
    }
    if (_hmButtonCache == null || !_hmButtonCache.containsKey(fullid)) {
      generateButtonCache(block);
    }
  }
  
  
  @SuppressWarnings("unchecked")
  private static void generateFieldAttrsCache(BlockFormulario block) {

    int flowid = block.getFlowId();
    int blockid = block.getId();
    HashMap<String,String> attributes = block.getAttributeMap();
    
    
    try {
      String sName = null;
      String sValue = null;
      Iterator<String> iter = null;
      HashMap<Integer, Properties> hmAttrs = new HashMap<Integer, Properties>();
      HashMap<Integer, HashMap<String, ArrayList<Object>>> hmListAttrs = new HashMap<Integer, HashMap<String, ArrayList<Object>>>();

      String stmp = null;
      Properties ptmp = null;
      HashMap<String, ArrayList<Object>> hmtmp = null;
      HashSet<String> hstmp = null;
      ArrayList<Object> altmp = null;
      int nRow = -1;
      int idx = -1;

      FlowSetting[] fsa = null;
      FlowSetting fs = null;
      String[] satmp = null;

      boolean bMultiple = false;

      fsa = BeanFactory.getFlowSettingsBean().getFlowSettings(flowid);

      iter = attributes.keySet().iterator();
      while (iter.hasNext()) {
        sName = (String) iter.next();

        stmp = FormProps.sNAME_PREFIX + FormProps.sJUNCTION;
        idx = sName.indexOf(stmp);
        if (idx == -1) {
          // this attribute does not represent a field
          continue;
        }

        sValue = attributes.get(sName);

        // now get object position
        idx += stmp.length();
        stmp = sName.substring(idx, sName.indexOf(FormProps.sJUNCTION, idx));

        int fieldNumber = -1;
        try {
          fieldNumber = new Integer(stmp);
        } catch (Exception e) {
          Logger.error(null, INSTANCE, "generateFieldAttrsCache", "caught exception getting object position: " + e.getMessage());
          continue;
        }

        sName = sName.substring(sName.indexOf(FormProps.sJUNCTION, idx) + FormProps.sJUNCTION.length());

        bMultiple = false;
        nRow = 0;
        if (sName.startsWith(FormProps.sROW)) {
          // multiple prop
          // remove ROW and JUNCTION separators.
          bMultiple = true;
          sName = sName.substring((FormProps.sROW.length() + FormProps.sJUNCTION.length()));
          try {
            stmp = sName.substring(0, sName.indexOf(FormProps.sJUNCTION));
            nRow = Integer.parseInt(stmp);
          } catch (Exception e) {
            nRow = 0;
          }
        }

        if (hmAttrs.containsKey(fieldNumber)) {
          ptmp = (Properties) hmAttrs.get(fieldNumber);
        } else {
          ptmp = new Properties();
        }

        // hmListAttrs will contain hashmaps with:
        // KEY: attribute name
        // VALUE: attribute value
        if (hmListAttrs.containsKey(fieldNumber)) {
          hmtmp = hmListAttrs.get(fieldNumber);
        } else {
          hmtmp = new HashMap<String, ArrayList<Object>>();
        }

        fs = null;
        satmp = null;
        hstmp = null;
        if (Utils.isListVar(sValue)) {
          for (int i = 0; fsa != null && i < fsa.length; i++) {
            if (sValue.equals(fsa[i].getName())) {
              fs = fsa[i];
              satmp = fs.getValuesToSave();
              // now check which values are queries
              for (int ii = 0; satmp != null && ii < satmp.length; ii++) {
                if (fs.isQueryValue(ii)) {
                  if (hstmp == null)
                    hstmp = new HashSet<String>();
                  hstmp.add(String.valueOf(ii));
                }
              }
              break;
            }
          }
        }

        stmp = sName;
        if (fs != null) {
          // list var
          if (bMultiple) {
            stmp = nRow + sName.substring(sName.indexOf(FormProps.sJUNCTION));
          } else {
            stmp = nRow + FormProps.sJUNCTION + sName;
          }
          nRow++;
        }

        if (fs != null && satmp != null) {
          altmp = new ArrayList<Object>();
          altmp.add(satmp);
          altmp.add(hstmp);
          hmtmp.put(stmp, altmp);
          altmp = null;
          Logger.debug(null, INSTANCE, "generateFieldAttrsCache", "importing LIST attribute " + stmp + " with " + satmp.length
              + " items FOR FIELD: " + fieldNumber + " AND BLOCK ID: " + blockid);
        }

        ptmp.setProperty(stmp, sValue);
        Logger.debug(null, INSTANCE, "generateFieldAttrsCache", "importing TEXT attribute " + stmp + "=" + sValue + " FOR FIELD: "
            + fieldNumber + " AND BLOCK ID: " + blockid);

        hmAttrs.put(fieldNumber, ptmp);
        hmListAttrs.put(fieldNumber, hmtmp);
      } // while

      HashMap[] hma = new HashMap[2];
      hma[BlockFormulario.nTXT_ATTR_IDX] = hmAttrs;
      hma[BlockFormulario.nLIST_ATTR_IDX] = hmListAttrs;

      synchronized (_hmFieldAttrsCache) {
        if (_hmFieldAttrsCache == null) {
          _hmFieldAttrsCache = new HashMap();
        }

        _hmFieldAttrsCache.put(getFullId(block), hma);
      }

    } // try
    catch (Exception _e) {
      Logger.error(null, INSTANCE, "generateFieldAttrsCache", "caught main exception: " + _e.getMessage(), _e);
    }
    
  }

  @SuppressWarnings("unchecked")
  private static void generateFieldsCache(BlockFormulario block) {
    String fullid = getFullId(block);
    ArrayList<FieldInterface> alFields = new ArrayList<FieldInterface>();
    HashMap<Integer, Properties> hmAttrs = ((HashMap<Integer, Properties>[]) _hmFieldAttrsCache.get(fullid))[BlockFormulario.nTXT_ATTR_IDX];

    FieldInterface fi = null;
    Properties ptmp = null;
    int fieldNumber = -1;
    String sClass = null;
    Class<? extends FieldInterface> cClass = null;

    for (int i = 0; i < hmAttrs.size(); i++) {
      fi = null;

      fieldNumber = new Integer(i);
      ptmp = (Properties) hmAttrs.get(fieldNumber);

      sClass = ptmp.getProperty(FormProps.sFIELD_TYPE);
      Logger.debug(null, INSTANCE, "generateFieldsCache", "field class=" + sClass + " FOR BLOCK ID: " + block.getId());

      try {
        cClass = (Class<? extends FieldInterface>)Class.forName(sClass);
        fi = cClass.newInstance();
        
      } catch (Exception e) {
        fi = null;
      }

      alFields.add(fi);
    }

    synchronized (_hmFieldsCache) {
      if (_hmFieldsCache == null) {
        _hmFieldsCache = new HashMap<String, ArrayList<FieldInterface>>();
      }

      _hmFieldsCache.put(fullid, alFields);
    }
    
  }

  private static void generateButtonCache(BlockFormulario block) {
    String fullid = getFullId(block);
    List<FormButton> alButtons = buildButtonList(block);
    if (null == alButtons || alButtons.isEmpty()) {
      // no buttons defined..
      return;
    }

    synchronized (_hmButtonCache) {
      _hmButtonCache.put(fullid, alButtons);
    }
    
  }
  
  public static List<FormButton> buildButtonList(final BlockFormulario abBlock) {
    List<FormButton> alButtons = new ArrayList<FormButton>();
    try {
      Iterator<String> iter = null;

      String sPos = null;
      String sName = null;
      String sVal = null;
      int ntmp = 0;
      HashMap<String, HashMap<String, String>> hmBut = new HashMap<String, HashMap<String, String>>();
      HashMap<String, String> hmtmp = null;

      iter = abBlock.getAttributeMap().keySet().iterator();
      while (iter.hasNext()) {
        sName = iter.next();

        if (!sName.startsWith(FormButton.ATTR_PREFIX)) {
          continue;
        }

        sVal = abBlock.getAttribute(sName);
        sPos = sName.substring(FormButton.ATTR_PREFIX.length());
        ntmp = sPos.indexOf("_");
        sName = sPos.substring(ntmp + 1);
        sPos = sPos.substring(0, ntmp);

        if (hmBut.containsKey(sPos)) {
          hmtmp = hmBut.get(sPos);
        } else {
          hmtmp = new HashMap<String, String>();
        }

        hmtmp.put(sName, sVal);

        hmBut.put(sPos, hmtmp);
      } // while

      for (int i = 0; i < hmBut.size(); i++) {
        hmtmp = null;
        hmtmp = hmBut.get(String.valueOf(i));
        if (hmtmp == null)
          continue;
        FormButton fb = new FormButton(hmtmp);
        alButtons.add(fb);
      }
      hmtmp = null;
      hmBut = null;

    } catch (Exception e) {
      Logger.error(null, abBlock, "generateButtonCache", "caught exception: " + e.getMessage());
    }
    return alButtons;
  }  

  
  public static List<FormButton> getDefaultButtons() {
    return _alDEF_BUTTONS;
  }
  
  
  public static ArrayList<FieldInterface> getFields(BlockFormulario block) {  
    return _hmFieldsCache.get(getFullId(block));
  }
  
  public static HashMap<Integer,Properties>[] getFieldAttributes(BlockFormulario block) {
    return _hmFieldAttrsCache.get(getFullId(block));
  }

  public static List<FormButton> getButtons(BlockFormulario block) {
    return _hmButtonCache.get(getFullId(block));
  }

}
