package pt.iflow.api.flows;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.utils.Logger;
import pt.iflow.api.xml.codegen.flow.XmlAttributeType;
import pt.iflow.api.xml.codegen.flow.XmlBlockType;
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
        List<XmlBlockType> blocks = new ArrayList<XmlBlockType>();
        for (XmlBlockType block : flow.getXmlBlock()) {
          block = upgradeBlock(version, block);
          blocks.add(block);
        }
        
        flow.getXmlBlock().clear();
        flow.withXmlBlock(blocks.toArray(new XmlBlockType[blocks.size()]));
        
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
  public static XmlBlockType upgradeBlock(String version, XmlBlockType block) {
    if (StringUtils.equals(version, VERSION_4X)) {
      List<XmlAttributeType> attributes = new ArrayList<XmlAttributeType>();
      for (XmlAttributeType attribute : block.getXmlAttribute()) {
        attribute = upgradeAttribute(version, attribute);
        attributes.add(attribute);
      }
      
      block.getXmlAttribute().clear();
      block.withXmlAttribute(attributes.toArray(new XmlAttributeType[attributes.size()]));
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
  public static XmlAttributeType upgradeAttribute(String version, XmlAttributeType attribute) {
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
