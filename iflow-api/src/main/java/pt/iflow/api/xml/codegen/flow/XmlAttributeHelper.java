package pt.iflow.api.xml.codegen.flow;

public class XmlAttributeHelper {
  public static XmlAttributeType cloneXmlAttribute(XmlAttributeType attribute) {
	  XmlAttributeType clonedObject = new XmlAttributeType();
    clonedObject.setDescription("" + attribute.getDescription());
    clonedObject.setName("" + attribute.getName());
    clonedObject.setValue("" + attribute.getValue());
    return clonedObject;

  }
}
