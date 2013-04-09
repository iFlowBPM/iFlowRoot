package pt.iknow.floweditor;

import java.awt.Component;
import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import pt.iknow.utils.FileUtils;
import pt.iknow.utils.swing.CloseableTabbedPane;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;

public class S2RModule {
  private File filePath;
  private String id;
  private String name;
  private String description;
  List<R2RModule> applicationModules;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public List<R2RModule> getApplicationModules() {
    return applicationModules;
  }

  public void setApplicationModules(List<R2RModule> applicationModules) {
    this.applicationModules = applicationModules;
  }

  public S2RModule(String id, String name, String description, List<R2RModule> applicationModules) {
    super();
    this.id = id;
    this.name = name;
    this.description = description;
    this.applicationModules = applicationModules;
  }

  public S2RModule() {
    super();
    // TODO Auto-generated constructor stub
  }

  private String extractNodeContent(Node node) {
    try {
      // Set up the output transformer
      TransformerFactory transfac = TransformerFactory.newInstance();
      Transformer trans = transfac.newTransformer();
      trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
      trans.setOutputProperty(OutputKeys.INDENT, "yes");

      // Print the DOM node

      StringWriter sw = new StringWriter();
      StreamResult result = new StreamResult(sw);
      DOMSource source = new DOMSource(node);
      trans.transform(source, result);
      String xmlString = sw.toString();

      return xmlString;
    } catch (Exception e) {
      return "";
    }

  }

  public S2RModule(File s2rFile, Janela janela) {
    this();
    applicationModules = new ArrayList<R2RModule>();
    DOMParser parser = new DOMParser();
    try {
      XPathFactory xpf = XPathFactory.newInstance();
      XPath xpath = xpf.newXPath();
      parser.parse(s2rFile.getAbsolutePath());
      Document doc = parser.getDocument();
      doc.getDocumentElement().normalize();
      NodeList idList = doc.getElementsByTagName("id");
      NodeList nameList = doc.getElementsByTagName("name");
      NodeList descriptionList = doc.getElementsByTagName("description");
      this.setFilePath(s2rFile);
      this.setId(idList.item(0).getTextContent());
      this.setName(nameList.item(0).getTextContent());
      this.setDescription(descriptionList.item(0).getTextContent());

      NodeList r2rList = doc.getElementsByTagName("r2r");
      for (int i = 0; i < r2rList.getLength(); i++) {
        Node r2rNode = r2rList.item(i);
        NodeList r2rChildren = r2rNode.getChildNodes();
        R2RModule r2r = new R2RModule();
        for (int j = 0; j < r2rChildren.getLength(); j++) {
          Node children = r2rChildren.item(j);
          if (StringUtils.equalsIgnoreCase(children.getNodeName(), "name"))
            r2r.setNameFlowXml(children.getTextContent());
          if (StringUtils.equalsIgnoreCase(children.getNodeName(), "install"))
            r2r.setInstallFlowXml(extractNodeContent(getNonBlankContent(children.getFirstChild())));
          if (StringUtils.equalsIgnoreCase(children.getNodeName(), "uninstall"))
            r2r.setUninstallFlowXml(extractNodeContent(getNonBlankContent(children.getFirstChild())));
          if (StringUtils.equalsIgnoreCase(children.getNodeName(), "application"))
            r2r.setApplicationFlowXml(extractNodeContent(getNonBlankContent(children.getFirstChild())));
        }
        if (StringUtils.isNotBlank(r2r.getNameFlowXml()) && StringUtils.isNotBlank(r2r.getInstallFlowXml())
            && StringUtils.isNotBlank(r2r.getUninstallFlowXml()) && StringUtils.isNotBlank(r2r.getApplicationFlowXml()))
          applicationModules.add(r2r);
      }
    } catch (Exception e) {
      new Erro(e.getMessage(), janela);
    }

  }

  private Node getNonBlankContent(Node children) {
    if (children == null)
      return null;

    if (StringUtils.isBlank(children.getTextContent()))
      return getNonBlankContent(children.getNextSibling());

    return children;
  }

  public void setFilePath(File filePath) {
    this.filePath = filePath;
  }

  public File getFilePath() {
    return filePath;
  }

  public void saveToFile(File selectedFile, CloseableTabbedPane tabPane) {
    this.filePath = selectedFile;

    StringBuffer xml = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?><s2r>");
    xml.append("<id>").append(this.getId()).append("</id>");
    xml.append("<name>").append(this.getName()).append("</name>");
    xml.append("<description>").append(this.getDescription()).append("</description>");
    xml.append("<r2rList>");

    for (R2RModule r2r : applicationModules) {
      xml.append("<r2r>");

      xml.append("<name>").append(r2r.getNameFlowXml()).append("</name>");

      String installFlow = r2r.getInstallFlowXml(), unistallFlow = r2r.getUninstallFlowXml(), aplicationFlow = r2r
          .getApplicationFlowXml();
      for (int i = 0; i < tabPane.getComponentCount(); i++) {
        Component c = tabPane.getComponent(i);
        if (c instanceof DesenhoScrollPane) {
          R2RModule tabR2R = ((DesenhoScrollPane) c).getR2rFlow();
          int moduleR2R = ((DesenhoScrollPane) c).getR2rType();
          if (tabR2R != null && tabR2R == r2r) {
            StringWriter sw = new StringWriter();
            if (moduleR2R == 0){
              aplicationFlow = new String(((DesenhoScrollPane) c).getDesenho().getFlowData());
              r2r.setApplicationFlowXml(aplicationFlow);
            }
            if (moduleR2R == 1) {
              installFlow = new String(((DesenhoScrollPane) c).getDesenho().getFlowData());
              r2r.setInstallFlowXml(installFlow);
            }
            if (moduleR2R == 2){
              unistallFlow = new String(((DesenhoScrollPane) c).getDesenho().getFlowData());
              r2r.setUninstallFlowXml(unistallFlow);
            }
            break;
          }
        }
      }
      try {
        aplicationFlow = aplicationFlow.substring(aplicationFlow.indexOf("?>") == -1 ? 0 : aplicationFlow.indexOf("?>") + 2);
      installFlow = installFlow.substring(installFlow.indexOf("?>") == -1 ? 0 : installFlow.indexOf("?>") + 2);
      unistallFlow = unistallFlow.substring(unistallFlow.indexOf("?>") == -1 ? 0 : unistallFlow.indexOf("?>") + 2);
      } catch (Exception e) {
        int i = 0;
        i++;
      }
      xml.append("<install>").append(installFlow).append("</install>");
      xml.append("<uninstall>").append(unistallFlow).append("</uninstall>");
      xml.append("<application>").append(aplicationFlow).append("</application>");

      xml.append("</r2r>");
    }

    xml.append("</r2rList>");
    xml.append("</s2r>");
    FileUtils.writeFile(xml.toString(), selectedFile.getAbsolutePath());
  }
}
