package pt.iknow.floweditor;

/*****************************************************
 *
 *  Project FLOW EDITOR
 *
 *  class: Ler_Ficheiro
 *
 *  desc: ler um flow de ficheiro
 *
 ****************************************************/

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashSet;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import pt.iflow.api.flows.FlowUpgrade;
import pt.iflow.api.utils.FlowInfo;
import pt.iflow.api.xml.FlowMarshaller;
import pt.iflow.api.xml.codegen.flow.XmlAttribute;
import pt.iflow.api.xml.codegen.flow.XmlBlock;
import pt.iflow.api.xml.codegen.flow.XmlCatalogVarAttribute;
import pt.iflow.api.xml.codegen.flow.XmlCatalogVars;
import pt.iflow.api.xml.codegen.flow.XmlFlow;
import pt.iflow.api.xml.codegen.flow.XmlFormTemplate;
import pt.iflow.api.xml.codegen.flow.XmlPort;
import pt.iknow.utils.StringUtilities;

/*******************************************************************************
 * 
 * Ler_Ficheiro
 * 
 * Funcao que le informacao de um circuito num ficheiro
 */
abstract class LerFicheiro {

  /*****************************************************************************
   * Funcao que le um circuito de um ficheiro
   * 
   * @param Nome_Ficheiro
   *          Nome do ficheiro a ler
   * @param bib
   *          Library com componenetes
   * @return retorna a lista com 2 elementos (lista de componentes e de linhas)
   */
  public static Ficheiro readFlow(Desenho desenho, File ficheiro) throws Exception {
    if(null == ficheiro) return null;
    FileInputStream fin = new FileInputStream(ficheiro);
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    byte[] buf = new byte[8192];
    int r = -1;
    while ((r = fin.read(buf)) != -1)
      bout.write(buf, 0, r);
    fin.close();

    return readFlow(desenho, bout.toByteArray(), ficheiro.getName(),null);
  }

  public static Ficheiro readFlow(Desenho desenho, byte[] filedata, String altName, FlowInfo finfo) throws Exception {
    if(null == filedata || filedata.length == 0) return null;
    Ficheiro ret = null;
    LibrarySet cbib = desenho.getLibrarySet();
    try {
      ret = new Ficheiro();

      ArrayList<InstanciaComponente> comp = new ArrayList<InstanciaComponente>();
      ArrayList<Linha> linhas = new ArrayList<Linha>();
      ret.componentes = comp;
      ret.linhas = linhas;

      int MAXID = 0;

      // Unmarshal XML file.

      XmlFlow flow = FlowMarshaller.unmarshal(filedata);
      flow = FlowUpgrade.upgradeFlow(FlowUpgrade.VERSION_4X, flow);
      
      ret.name = flow.getName();
      ret.author = flow.getAuthor();
      ret.description = flow.getDescription();
      
      ret.iFlowVersion = flow.getIFlowVersion();
      if(StringUtils.isBlank(ret.iFlowVersion)) {
        ret.iFlowVersion = FlowUpgrade.VERSION_UNKNOWN;
      }
      
      // old format
      if(StringUtilities.isEmpty(ret.description) || "--".equals(ret.description))  {
        ret.description = ret.name;
        // for compat, strip xml part
        if(FilenameUtils.isExtension(altName.toLowerCase(),"xml")) {
          ret.name = FilenameUtils.removeExtension(altName);
        }
      }
      ret.version = flow.getVersion();

      /* find equal ids */
      HashSet<Integer> col = new HashSet<Integer>();
      col.add(0);
      for (int i = 0; i < flow.getXmlBlockCount(); i++) {
        XmlBlock block = flow.getXmlBlock(i);
        int ident = block.getId();
        if(col.contains(ident))
          throw new Exception(Mesg.ErroLerFicheiroIdsIguais);
        col.add(ident);
      }

      /* create blocks */
      for (int i = 0; i < flow.getXmlBlockCount(); i++) {
        XmlBlock block = flow.getXmlBlock(i);
        String tipo = block.getType();

        Componente_Biblioteca cb = cbib.getComponent(tipo);
        if (cb == null)
          throw new Exception(Mesg.ErroTipoNaoExiste + " " + tipo); //$NON-NLS-1$
        InstanciaComponente ic = cb.cria(desenho);
        ic.Mudar_Nome(block.getName());
        ic.ID = block.getId();

        MAXID = Math.max(ic.ID, MAXID);
        ic.Posicao_X = block.getXmlPosition().getX();
        ic.Posicao_Y = block.getXmlPosition().getY();

        /* atributos */
        for (int k = 0; k < block.getXmlAttributeCount(); k++) {
          XmlAttribute attr = block.getXmlAttribute(k);
          Atributo atr = ic.getAtributo(attr.getName());
          if(null != atr) { // ja existe
            atr.setValor(attr.getValue());
          } else { // novo
            ic.addAtributo(new AtributoImpl(attr.getName(), attr.getValue(), attr.getDescription(), null));
          }
        }
        comp.add(ic);
      }
      //Check for Max Block Id existence 
      if(finfo!=null)
          MAXID = Math.max(finfo.getMaxBlockId(), MAXID);
      /* ligar blocos */
      for (int i = 0; i < flow.getXmlBlockCount(); i++) {
        XmlBlock block = flow.getXmlBlock(i);
        InstanciaComponente ic = daPorID(block.getId(), comp);

        for (int k = 0; k < block.getXmlPortCount(); k++) {
          XmlPort port = block.getXmlPort(k);
          String name = port.getName();

          int outPort = inListaString(name, ic.C_B.nomes_saidas);
          if (outPort >= 0) {
            InstanciaComponente ic2 = daPorID(port.getConnectedBlockId(), comp);
            if(ic2 != null) {
              int inPort = inListaString(port.getConnectedPortName(), ic2.C_B.nomes_entradas);
              if (inPort >= 0) {
                
                // fazer ligacoes
                Linha l = new Linha();
                
                l.escolhido = 1;
                Componente.Liga(l, 0, ic2, inPort);
                l.escolhido = 2;
                Componente.Liga(ic, outPort, l, 0);
                linhas.add(l);
              }
            }
          }

        }
      }

      /* CatalogVars */
      XmlCatalogVars xmlcv = flow.getXmlCatalogVars();
      // ArrayList<Atributo> catalogo = desenho.getVariableCatalog();
      // catalogo.clear();
      desenho.newCatalog();
      if (xmlcv != null) {
        for (int i = 0; i < xmlcv.getXmlCatalogVarAttributeCount(); i++) {
          XmlCatalogVarAttribute attr = xmlcv.getXmlCatalogVarAttribute(i);
          Atributo catAttr = new AtributoImpl();
          catAttr.setDataType(attr.getDataType());
          catAttr.setInitValue(attr.getInitVal());
          catAttr.setNome(attr.getName());
          catAttr.setPublicName(attr.getPublicName());
          catAttr.setSearchable(attr.getIsSearchable());

          // catalogo.add(catAttr);
          desenho.addCatalogVariable(catAttr);
        }
      }
      /* CatalogVars */


      /* Templates */

      XmlFormTemplate [] templates = flow.getXmlFormTemplate();
      for(int i = 0; templates != null && i < flow.getXmlFormTemplateCount(); i++) {
        if(null == templates) continue;
        String name = templates[i].getName();
        String form = templates[i].getValue();

        desenho.setFormTemplate(name, form);

      }

      /* Templates */

      ret.nextBlockNum = MAXID + 1;

    } catch (Exception e) {
      FlowEditor.log("error", e);
      throw e;
    }
    return ret;
  }

  /** **************************************** */
  private static InstanciaComponente daPorID(int id, ArrayList<InstanciaComponente> comp) {
    for (int i = 0; i < comp.size(); i++) {
      InstanciaComponente ic = comp.get(i);
      if (ic.ID == id)
        return ic;
    }
    return null;
  }

  /** ******************************************* */
  private static int inListaString(String a, ArrayList<String> l) {
    for (int i = 0; i < l.size(); i++)
      if ((l.get(i)).equals(a))
        return i;
    return -1;
  }

  public static class Ficheiro {
    public ArrayList<InstanciaComponente> componentes;
    public ArrayList<Linha> linhas;
    public String name;
    public String author;
    public String description;
    public String version;
    public String iFlowVersion;
    public int nextBlockNum;
  }
}
