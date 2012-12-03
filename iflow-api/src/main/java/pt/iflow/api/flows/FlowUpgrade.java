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
package pt.iflow.api.flows;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.utils.Logger;
import pt.iflow.api.xml.codegen.flow.XmlAttribute;
import pt.iflow.api.xml.codegen.flow.XmlBlock;
import pt.iflow.api.xml.codegen.flow.XmlFlow;

/**
 * Upgrades a Flow for version compatibility.
 * 
 * @author Luis Cabral
 * @version 13.03.2009
 */
public class FlowUpgrade {

  /** Unknown iFlow version. */
  public final static String VERSION_UNKNOWN = "-1";
  /** Upgrade to iFlow version 4.X. */
  public final static String VERSION_4X = "4.X";

  /**
   * Map sorted by iFlow version, containing a List of attribute values to
   * replace. <br/>
   * This List composed by several array's of strings which contain, at position
   * 0, the value which will replace all other values represented by the rest of
   * the array. <br/>
   * Examples:
   * <table border="1">
   * <tr><th>Current Value</th><th>Array</th><th>Final value</th></tr>
   * <tr><td>"SomeValue"</td><td>{"Another", "Some"}</td><td>"AnotherValue"</td></tr>
   * <tr><td>"SomeValue"</td><td>{"This", "Some", "Or", "Another"}</td><td>"ThisValue"</td></tr>
   * <tr><td>"SomeOrAnotherValue"</td><td>{"", "Some", "Or", "Another"}</td><td>"Value"</td></tr>
   * </table>
   */
  private static Map<String, List<String[]>> upgradeAttributeValue;
  static {
    upgradeAttributeValue = new HashMap<String, List<String[]>>();
    List<String[]> version4x = new ArrayList<String[]>();
    version4x.add(new String[] { "pt.iflow.blocks.form", "pt.iknow.form" });
    version4x.add(new String[] { "pt.iflow.api.datatypes", "pt.iknow.datatypes" });

    upgradeAttributeValue.put(VERSION_4X, version4x);
  }

  /**
   * This class should be accessed in a static way.
   */
  private FlowUpgrade() {
  }

  /**
   * Upgrades the given flow to the given version.
   * 
   * @see #VERSION_4X
   * @param version
   *          Version we wish to upgrade to.
   * @param flow
   *          Flow to be upgraded.
   * @return Upgraded flow.
   */
  public static XmlFlow upgradeFlow(String version, XmlFlow flow) {
    if (!StringUtils.equals(flow.getIFlowVersion(), version)) {
      Logger.trace("FlowUpgrade", "upgradeFlow", "Upgrading flow '" + flow.getName() + "' from version '" + flow.getIFlowVersion()
          + "' to '" + version + "'.");
      if (StringUtils.equals(version, VERSION_4X)) {
        List<XmlBlock> blocks = new ArrayList<XmlBlock>();
        for (XmlBlock block : flow.getXmlBlock()) {
          block = upgradeBlock(version, block);
          blocks.add(block);
        }
        flow.setXmlBlock(blocks.toArray(new XmlBlock[blocks.size()]));
        
        flow.setIFlowVersion(VERSION_4X);
      }
    }
    return flow;
  }

  /**
   * Upgrades the given block to the given version. <br/>
   * Unless there is a specific reason for using this method directly, then
   * please use: {@link #upgradeFlow(String, XmlFlow)}
   * 
   * @see #VERSION_4X
   * @param version
   *          Version we wish to upgrade to.
   * @param block
   *          Block to be upgraded.
   * @return Upgraded block.
   */
  public static XmlBlock upgradeBlock(String version, XmlBlock block) {
    if (StringUtils.equals(version, VERSION_4X)) {
      List<XmlAttribute> attributes = new ArrayList<XmlAttribute>();
      for (XmlAttribute attribute : block.getXmlAttribute()) {
        attribute = upgradeAttribute(version, attribute);
        attributes.add(attribute);
      }
      block.setXmlAttribute(attributes.toArray(new XmlAttribute[attributes.size()]));
    }
    return block;
  }

  /**
   * Upgrades the given attribute to the given version. <br/>
   * Unless there is a specific reason for using this method directly, then
   * please use: {@link #upgradeFlow(String, XmlFlow)}
   * 
   * @see #VERSION_4X
   * @param version
   *          Version we wish to upgrade to.
   * @param attribute
   *          Attribute to be upgraded.
   * @return Upgraded attribute.
   */
  public static XmlAttribute upgradeAttribute(String version, XmlAttribute attribute) {
    if (StringUtils.equals(version, VERSION_4X)) {
      String value = attribute.getValue();
      for (String[] replacements : upgradeAttributeValue.get(VERSION_4X)) {
        for (String replacement : replacements) {
          if (StringUtils.contains(value, replacement) && !StringUtils.equals(replacement, replacements[0])) {
            value = value.replace(replacement, replacements[0]);
          }
        }
      }
      attribute.setValue(value);
    }
    return attribute;
  }
}
