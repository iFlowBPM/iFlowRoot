package pt.iflow.flows;

import java.util.ArrayList;

import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.xml.codegen.flow.XmlAttributeHelper;
import pt.iflow.api.xml.codegen.flow.XmlAttributeType;
import pt.iflow.api.xml.codegen.flow.XmlBlockType;
import pt.iflow.api.xml.codegen.flow.XmlFlow;

public class FormTemplateResolver {

  private static final Object FORM_TYPE = "BlockFormulario";
  private XmlFlow flow;

  public FormTemplateResolver() {
  }

  public FormTemplateResolver(XmlFlow flow) {
    this.flow = flow;
  }

  public void setFlow(XmlFlow flow) {
    this.flow = flow;
  }

  public XmlFlow getFlow() {
    return flow;
  }

  public XmlFlow resolveTemplates(UserInfoInterface userInfo) {
    for (XmlBlockType block : getFlow().getXmlBlock())
      if (block.getName().equals(FORM_TYPE)) {
        resolveTemplateInBlock(block);
      }

    return flow;
  }

  private void resolveTemplateInBlock(XmlBlockType formBlock) {
    for (XmlAttributeType xmlAttribute : formBlock.getXmlAttribute())
      if (xmlAttribute.getName().endsWith("form_template")) {
        String templateName = xmlAttribute.getValue();
        XmlAttributeType[] templateXmlAttribute = findTemplateBlock(templateName);
        Integer objId = retrieveObjId(xmlAttribute.getName());
        Integer maxObjIdValue = retrieveMaxObjIdValue(formBlock);
        String editionCond = retrieveCond("OBJ_" + objId + "_edition_cond", formBlock);
        String disableCond = retrieveCond("OBJ_" + objId + "_disabled_cond", formBlock);
        addFieldsToForm(formBlock, templateXmlAttribute, objId, editionCond, disableCond);
      }
  }

  private String retrieveCond(String editionCondAttr, XmlBlockType formBlock) {
    for (XmlAttributeType xmlAttribute : formBlock.getXmlAttribute())
      if (xmlAttribute.getName().equals(editionCondAttr))
        return xmlAttribute.getValue();

    return "";
  }

  private void addFieldsToForm(XmlBlockType formBlock, XmlAttributeType[] templateXmlAttribute, Integer objId, String editionCond,
      String disableCond) {
    ArrayList<XmlAttributeType> finalAttributes = new ArrayList<XmlAttributeType>();
    Integer objIdCounter = 0,templateObjIdCounter = 0;
    Boolean doneTemplate = false;
   
    // check and update conditions
    for (XmlAttributeType xmlTemplateAttribute : templateXmlAttribute) {
      if ((xmlTemplateAttribute.getName().endsWith("_edition_cond") || xmlTemplateAttribute.getName().endsWith("_output_only"))
          && !editionCond.trim().equals(""))
        xmlTemplateAttribute.setValue(editionCond);

      if (xmlTemplateAttribute.getName().endsWith("_disabled_cond") && !disableCond.trim().equals(""))
        xmlTemplateAttribute.setValue(disableCond);

      if (xmlTemplateAttribute.getName().endsWith("_show_condition")) {
        if (!"".equals(disableCond.trim())){
          xmlTemplateAttribute.setValue("!"+disableCond);
        } else if(!"".equals(editionCond.trim())){
          xmlTemplateAttribute.setValue("!"+editionCond);
        }
      }
    }

    // insert template in form
    for (XmlAttributeType xmlAttribute : formBlock.getXmlAttribute()) {
      if (xmlAttribute.getName().startsWith("OBJ_")) {
        // if it's before template just add it
        if (retrieveObjId(xmlAttribute.getName()) < objId) {
          // xmlAttribute.setName("OBJ_" + (objIdCounter + retrieveObjId(xmlAttribute.getName()))
          // + xmlAttribute.getName().substring(xmlAttribute.getName().indexOf('_', 4)));
          // xmlAttribute.setDescription(xmlAttribute.getName());
          if (xmlAttribute.getName().endsWith("_ID"))
            objIdCounter++;
          finalAttributes.add(xmlAttribute);

        }
        // this is from template so update and add all at once
        else if (retrieveObjId(xmlAttribute.getName()).equals(objId) && !doneTemplate) {
          for (XmlAttributeType xmlTemplateAttribute : templateXmlAttribute) {
            if (xmlTemplateAttribute.getName().startsWith("OBJ_")) {
              xmlTemplateAttribute.setName("OBJ_" + (objIdCounter + retrieveObjId(xmlTemplateAttribute.getName()))
                  + xmlTemplateAttribute.getName().substring(xmlTemplateAttribute.getName().indexOf('_', 4)));
              xmlTemplateAttribute.setDescription(xmlTemplateAttribute.getName());
              if (xmlTemplateAttribute.getName().endsWith("_ID")) {
                xmlTemplateAttribute.setValue("" + retrieveObjId(xmlTemplateAttribute.getName()));
                templateObjIdCounter++;
              }
              finalAttributes.add(xmlTemplateAttribute);
            }
          }
          doneTemplate = true;
        }
        //after the template, we increase the ids according to how many compoments were in the template and add
        else if (retrieveObjId(xmlAttribute.getName()) > objId) {
          xmlAttribute.setName("OBJ_" + ((templateObjIdCounter - 1) + retrieveObjId(xmlAttribute.getName()))
              + xmlAttribute.getName().substring(xmlAttribute.getName().indexOf('_', 4)));
          xmlAttribute.setDescription(xmlAttribute.getName());
          if (xmlAttribute.getName().endsWith("_ID"))
            xmlAttribute.setValue("" + retrieveObjId(xmlAttribute.getName()));
          finalAttributes.add(xmlAttribute);
        }
      } else {
        // not a form field, just add it
        finalAttributes.add(xmlAttribute);
      }
    }
    formBlock.withXmlAttribute(finalAttributes.toArray(new XmlAttributeType[finalAttributes.size()]));
  }

  private void addFieldsToForm(XmlBlockType formBlock, XmlAttributeType[] templateXmlAttribute, Integer objId, Integer maxObjIdValue,
      String editionCond, String disableCond) {
    ArrayList<XmlAttributeType> templateAttributes = new ArrayList<XmlAttributeType>();
    ArrayList<XmlAttributeType> formAttributes = new ArrayList<XmlAttributeType>();
    Integer maxObjId = 0;

    for (XmlAttributeType xmlAttribute : templateXmlAttribute)
      if (xmlAttribute.getName().startsWith("OBJ_")) {
        xmlAttribute.setName("OBJ_" + (objId + retrieveObjId(xmlAttribute.getName()))
            + xmlAttribute.getName().substring(xmlAttribute.getName().indexOf('_', 4)));
        xmlAttribute.setDescription(xmlAttribute.getName());

        if (retrieveObjId(xmlAttribute.getName()) > maxObjId)
          maxObjId = retrieveObjId(xmlAttribute.getName());

        if (xmlAttribute.getName().endsWith("_ID")) {
          Integer oldObjIdValue = Integer.parseInt(xmlAttribute.getValue());
          oldObjIdValue += maxObjIdValue;
          xmlAttribute.setValue("" + oldObjIdValue);
        }

        if ((xmlAttribute.getName().endsWith("_edition_cond") || xmlAttribute.getName().endsWith("_output_only"))
            && !editionCond.trim().equals(""))
          xmlAttribute.setValue(editionCond);

        if (xmlAttribute.getName().endsWith("_disabled_cond") && !disableCond.trim().equals(""))
          xmlAttribute.setValue(disableCond);

        templateAttributes.add(xmlAttribute);
      }

    for (XmlAttributeType xmlAttribute : formBlock.getXmlAttribute())
      if (!xmlAttribute.getName().startsWith("OBJ_" + objId)) {
        if (xmlAttribute.getName().startsWith("OBJ_") && retrieveObjId(xmlAttribute.getName()) > objId) {
          xmlAttribute.setName("OBJ_" + (maxObjId - 1 + retrieveObjId(xmlAttribute.getName()))
              + xmlAttribute.getName().substring(xmlAttribute.getName().indexOf('_', 4)));
          xmlAttribute.setDescription(xmlAttribute.getName());
        }
        formAttributes.add(xmlAttribute);
      }

    formAttributes.addAll(templateAttributes);
    formBlock.withXmlAttribute(formAttributes.toArray(new XmlAttributeType[formAttributes.size()]));
  }

  private Integer retrieveMaxObjIdValue(XmlBlockType formBlock) {
    Integer maxObjIdValue = -1;
    for (XmlAttributeType xmlAttribute : formBlock.getXmlAttribute())
      if (xmlAttribute.getName().startsWith("OBJ_") && xmlAttribute.getName().endsWith("_ID")) {
        Integer temp = Integer.parseInt(xmlAttribute.getValue());
        if (temp > maxObjIdValue)
          maxObjIdValue = temp;
      }
    return maxObjIdValue;
  }

  private Integer retrieveObjId(String name) {
    int firstUnderscore = name.indexOf('_');
    int secondUnderscore = name.indexOf('_', firstUnderscore + 1);

    Integer number = null;
    try {
      number = Integer.parseInt(name.substring(firstUnderscore + 1, secondUnderscore));
    } catch (Exception e) {
      number = null;
    }
    return number;
  }

  private XmlAttributeType[] findTemplateBlock(String templateName) {
    for (XmlBlockType block : getFlow().getXmlBlock())
      if (block.getName().equals(templateName) && block.getName().equals("BlockFormTemplate")) {
        ArrayList<XmlAttributeType> tempArray = new ArrayList<XmlAttributeType>();
        for (XmlAttributeType oldAttribute : block.getXmlAttribute()) {
          XmlAttributeType newAttribute = XmlAttributeHelper.cloneXmlAttribute(oldAttribute);
          tempArray.add(newAttribute);
        }
        return tempArray.toArray(new XmlAttributeType[tempArray.size()]);
      }

    return new XmlAttributeType[0];
  }

}
