package pt.iflow.api.xml.codegen.flow;

public class XmlAttributeHelper {
  public static XmlAttribute cloneXmlAttribute(XmlAttribute attribute) {
    XmlAttribute clonedObject = new XmlAttribute();
    clonedObject.setDescription("" + attribute.getDescription());
    clonedObject.setName("" + attribute.getName());
    clonedObject.setValue("" + attribute.getValue());
    return clonedObject;

  }
}
