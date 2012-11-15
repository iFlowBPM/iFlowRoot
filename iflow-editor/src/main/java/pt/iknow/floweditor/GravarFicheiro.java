package pt.iknow.floweditor;

/*****************************************************
 *
 *  Project FLOW EDITOR
 *
 *  class: Grava_Ficheiro
 *
 *  desc: grava em ficheiro flows
 *
 ****************************************************/

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import pt.iflow.api.flows.FlowUpgrade;
import pt.iflow.api.xml.FlowMarshaller;
import pt.iflow.api.xml.codegen.flow.XmlAttribute;
import pt.iflow.api.xml.codegen.flow.XmlBlock;
import pt.iflow.api.xml.codegen.flow.XmlCatalogVarAttribute;
import pt.iflow.api.xml.codegen.flow.XmlCatalogVars;
import pt.iflow.api.xml.codegen.flow.XmlFlow;
import pt.iflow.api.xml.codegen.flow.XmlFormTemplate;
import pt.iflow.api.xml.codegen.flow.XmlPort;
import pt.iflow.api.xml.codegen.flow.XmlPosition;

/*******************************************************************************
 * Gravar_Ficheiro
 */
abstract class GravarFicheiro {

  // private static final String extension = "." + Mesg.FileExtension.toLowerCase();//$NON-NLS-1$

  private static String generateFlowName(String flowId) {
    //    if (flowId.toLowerCase().endsWith(extension)) // Remove trailing .xml
    //      flowId = flowId.substring(0, flowId.length()-extension.length());
    return flowId;
  }

  /**
   * Gera o XML do fluxo para o outputstream indicado
   * 
   * 
   * @param desenho
   * @param flowId
   * @param flowName
   * @param bos
   * @throws Exception
   */
  public static void saveFlow(Desenho desenho, String flowId, String flowName, OutputStream bos)
  throws Exception {
    if (bos == null) throw new IllegalArgumentException("Output cannot be null!");

    try {
      String flowCodeName = generateFlowName(flowId);

      /** ***************** */
      XmlFlow flow = new XmlFlow();

      flow.setAuthor(Mesg.ProgramName);
      flow.setDescription(flowName);
      flow.setName(flowCodeName);
      flow.setVersion(Mesg.Version);

      // Variable catalog
      Collection<Atributo> catalogo = desenho.getCatalogue();
      if (catalogo != null && catalogo.size() > 0) {
        XmlCatalogVars xmlcv = new XmlCatalogVars();
        flow.setXmlCatalogVars(xmlcv);
        for (Atributo at : catalogo) {
          XmlCatalogVarAttribute catVarAttr = new XmlCatalogVarAttribute();

          catVarAttr.setDataType(at.getDataType());
          catVarAttr.setInitVal(at.getInitValue());
          catVarAttr.setIsSearchable(at.isSearchable());
          catVarAttr.setName(at.getNome());
          catVarAttr.setPublicName(at.getPublicName());
          catVarAttr.setFormat(at.getValor());

          xmlcv.addXmlCatalogVarAttribute(catVarAttr);

          //          XmlAttribute xmlattr = new XmlAttribute();
          //          xmlattr.setName(at.getNome());
          //          xmlattr.setValue(at.getValor());
          //          xmlattr.setDescription(at.getDescricao());
          //          xmlcv.addXmlAttribute(xmlattr);


        }
      }

      // templates

      Map<String,String> templates = desenho.getFormTemplates();
      for(String templateName : templates.keySet()) {
        String at = templates.get(templateName);
        XmlFormTemplate xmlAt = new XmlFormTemplate();
        xmlAt.setName(templateName);
        xmlAt.setValue(at);
        flow.addXmlFormTemplate(xmlAt);
      }



      /* blocks */
      List<XmlBlock> alBlocos = new ArrayList<XmlBlock>();
      List<InstanciaComponente> componentes = desenho.getComponentList();
      for (int i = 0; i < componentes.size(); i++) {
        InstanciaComponente ic = componentes.get(i);

        XmlBlock bloco = new XmlBlock();
        bloco.setId(ic.ID);
        bloco.setName(ic.Nome);
        bloco.setType(ic.C_B.Nome);

        /* atributos */
        List<Atributo> atributos = ic.getAtributos();
        XmlAttribute[] attrs = new XmlAttribute[atributos.size()];
        for (int k = 0; k < atributos.size(); k++) {
          Atributo at = atributos.get(k);
          XmlAttribute xmlAt = new XmlAttribute();
          xmlAt.setName(at.getNome());
          xmlAt.setValue(at.getValor() != null ? at.getValor() : "");
          if (at.getDescricao() != null)
            xmlAt.setDescription(at.getDescricao());
          else
            xmlAt.setDescription(""); //$NON-NLS-1$
          attrs[k] = xmlAt;
        }
        bloco.setXmlAttribute(attrs);

        /* portos */
        ArrayList<XmlPort> portList = new ArrayList<XmlPort>();

        /* ins */
        for (int k = 0; k < ic.lista_estado_entradas.size(); k++) {
          boolean ja = false;
          List<Conector> list2 = ic.lista_estado_entradas.get(k);
          for (int j = 0; j < list2.size() && !ja; j++) {
            Conector c = Linha.daIn(list2.get(j));
            if (c != null) {
              XmlPort p = new XmlPort();
              p.setName((String) ic.C_B.nomes_entradas.get(k));
              p.setConnectedBlockId(((InstanciaComponente) c.Comp).ID);
              p.setConnectedPortName((String) ((InstanciaComponente) c.Comp).C_B.nomes_saidas.get(c.Numero));
              portList.add(p);
              ja = true;
            }
          }
        }
        /* outs */
        for (int k = 0; k < ic.lista_estado_saidas.size(); k++) {
          List<Conector> list2 = ic.lista_estado_saidas.get(k);
          for (int j = 0; j < list2.size(); j++) {
            Conector c = Linha.daOut(list2.get(j));
            if (c != null) {
              XmlPort p = new XmlPort();
              p.setName((String) ic.C_B.nomes_saidas.get(k));
              p.setConnectedBlockId(((InstanciaComponente) c.Comp).ID);
              p.setConnectedPortName((String) ((InstanciaComponente) c.Comp).C_B.nomes_entradas.get(c.Numero));
              portList.add(p);
            }
          }
        }

        XmlPort[] portos = new XmlPort[portList.size()];
        portos = portList.toArray(portos);

        bloco.setXmlPort(portos);

        alBlocos.add(bloco);
        XmlPosition p = new XmlPosition();
        p.setX(ic.Posicao_X);
        p.setY(ic.Posicao_Y);
        bloco.setXmlPosition(p);
      } // for

      XmlBlock[] xmlblocos = new XmlBlock[alBlocos.size()];
      xmlblocos = alBlocos.toArray(xmlblocos);
      flow.setXmlBlock(xmlblocos);

      flow = FlowUpgrade.upgradeFlow(FlowUpgrade.VERSION_4X, flow);
      FlowEditor.log("Saving flow id "+flowId+" to outputstream..."); //$NON-NLS-1$ //$NON-NLS-2$

      byte[] data = FlowMarshaller.marshall(flow);

      bos.write(data);
    } catch (Exception e) {
      FlowEditor.log("error", e);
      throw new Exception(Mesg.ErroGravarFicheiro);
    }
  }

}
