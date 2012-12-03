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
package pt.iflow.flows;

import java.util.ArrayList;

import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.xml.codegen.flow.XmlAttribute;
import pt.iflow.api.xml.codegen.flow.XmlAttributeHelper;
import pt.iflow.api.xml.codegen.flow.XmlBlock;
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
    for (XmlBlock block : getFlow().getXmlBlock())
      if (block.getType().equals(FORM_TYPE)) {
        resolveTemplateInBlock(block);
      }

    return flow;
  }

  private void resolveTemplateInBlock(XmlBlock formBlock) {
    for (XmlAttribute xmlAttribute : formBlock.getXmlAttribute())
      if (xmlAttribute.getName().endsWith("form_template")) {
        String templateName = xmlAttribute.getValue();
        XmlAttribute[] templateXmlAttribute = findTemplateBlock(templateName);
        Integer objId = retrieveObjId(xmlAttribute.getName());
        Integer maxObjIdValue = retrieveMaxObjIdValue(formBlock);
        String editionCond = retrieveCond("OBJ_" + objId + "_edition_cond", formBlock);
        String disableCond = retrieveCond("OBJ_" + objId + "_disabled_cond", formBlock);
        addFieldsToForm(formBlock, templateXmlAttribute, objId, editionCond, disableCond);
      }
  }

  private String retrieveCond(String editionCondAttr, XmlBlock formBlock) {
    for (XmlAttribute xmlAttribute : formBlock.getXmlAttribute())
      if (xmlAttribute.getName().equals(editionCondAttr))
        return xmlAttribute.getValue();

    return "";
  }

  private void addFieldsToForm(XmlBlock formBlock, XmlAttribute[] templateXmlAttribute, Integer objId, String editionCond,
      String disableCond) {
    ArrayList<XmlAttribute> finalAttributes = new ArrayList<XmlAttribute>();
    Integer objIdCounter = 0,templateObjIdCounter = 0;
    Boolean doneTemplate = false;
   
    // check and update conditions
    for (XmlAttribute xmlTemplateAttribute : templateXmlAttribute) {
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
    for (XmlAttribute xmlAttribute : formBlock.getXmlAttribute()) {
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
        else if (retrieveObjId(xmlAttribute.getName()) == objId && !doneTemplate) {
          for (XmlAttribute xmlTemplateAttribute : templateXmlAttribute) {
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
    formBlock.setXmlAttribute(finalAttributes.toArray(new XmlAttribute[finalAttributes.size()]));
  }

  private void addFieldsToForm(XmlBlock formBlock, XmlAttribute[] templateXmlAttribute, Integer objId, Integer maxObjIdValue,
      String editionCond, String disableCond) {
    ArrayList<XmlAttribute> templateAttributes = new ArrayList<XmlAttribute>();
    ArrayList<XmlAttribute> formAttributes = new ArrayList<XmlAttribute>();
    Integer maxObjId = 0;

    for (XmlAttribute xmlAttribute : templateXmlAttribute)
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

    for (XmlAttribute xmlAttribute : formBlock.getXmlAttribute())
      if (!xmlAttribute.getName().startsWith("OBJ_" + objId)) {
        if (xmlAttribute.getName().startsWith("OBJ_") && retrieveObjId(xmlAttribute.getName()) > objId) {
          xmlAttribute.setName("OBJ_" + (maxObjId - 1 + retrieveObjId(xmlAttribute.getName()))
              + xmlAttribute.getName().substring(xmlAttribute.getName().indexOf('_', 4)));
          xmlAttribute.setDescription(xmlAttribute.getName());
        }
        formAttributes.add(xmlAttribute);
      }

    formAttributes.addAll(templateAttributes);
    formBlock.setXmlAttribute(formAttributes.toArray(new XmlAttribute[formAttributes.size()]));
  }

  private Integer retrieveMaxObjIdValue(XmlBlock formBlock) {
    Integer maxObjIdValue = -1;
    for (XmlAttribute xmlAttribute : formBlock.getXmlAttribute())
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

  private XmlAttribute[] findTemplateBlock(String templateName) {
    for (XmlBlock block : getFlow().getXmlBlock())
      if (block.getName().equals(templateName) && block.getType().equals("BlockFormTemplate")) {
        ArrayList<XmlAttribute> tempArray = new ArrayList<XmlAttribute>();
        for (XmlAttribute oldAttribute : block.getXmlAttribute()) {
          XmlAttribute newAttribute = XmlAttributeHelper.cloneXmlAttribute(oldAttribute);
          tempArray.add(newAttribute);
        }
        return tempArray.toArray(new XmlAttribute[tempArray.size()]);
      }

    return new XmlAttribute[0];
  }

}
