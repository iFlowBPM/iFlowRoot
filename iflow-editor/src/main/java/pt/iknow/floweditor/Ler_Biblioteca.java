package pt.iknow.floweditor;

/*****************************************************
 *
 *  Project FLOW EDITOR
 *
 *  class: Ler_Biblioteca
 *
 *  desc: le uma biblioteca de ficheiro
 *
 ****************************************************/

import java.awt.Point;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import pt.iflow.api.xml.LibraryMarshaller;
import pt.iflow.api.xml.codegen.library.BoolType;
import pt.iflow.api.xml.codegen.library.InOutType;
import pt.iflow.api.xml.codegen.library.SizeType;
import pt.iflow.api.xml.codegen.library.XmlAttributeType;
import pt.iflow.api.xml.codegen.library.XmlBlockColor;
import pt.iflow.api.xml.codegen.library.XmlBlockType;
import pt.iflow.api.xml.codegen.library.XmlLibrary;
import pt.iflow.api.xml.codegen.library.XmlPortType;

import pt.iknow.utils.StringUtilities;

/*******************************************************************************
 * Ler_Biblioteca
 */
public class Ler_Biblioteca {

  /*****************************************************************************
   * Funcao que le o ficheiro de biblioteca
   * 
   * @param nome_ficheiros
   *          nome do ficheiro
   * @return biblioteca lida do ficheiro
   */
  public static Library getLibraryFromFile(String nome_ficheiro, LibrarySet cbibs) throws Exception {
    Library bib = null;
    try {
      InputStream fin = new BufferedInputStream(new FileInputStream(nome_ficheiro));
      bib = getLibraryFromFile(fin, cbibs);
      fin.close();
    } catch (Exception e) {
      throw new Exception(Mesg.ErroLerBiblioteca, e);
    }

    return bib;
  }

  /*****************************************************************************
   * Funcao que le o ficheiro de biblioteca
   * 
   * @param nome_ficheiros
   *          nome do ficheiro
   * @return biblioteca lida do ficheiro
   */
  public static Library getLibraryFromFile(File ficheiro, LibrarySet cbibs) throws Exception {
    Library bib = null;
    try {
      InputStream fin = new BufferedInputStream(new FileInputStream(ficheiro));
      bib = getLibraryFromFile(fin, cbibs);
      fin.close();
    } catch (Exception e) {
      throw new Exception(Mesg.ErroLerBiblioteca, e);
    }

    return bib;
  }

  static public Library getLibraryFromData(byte [] data, LibrarySet cbibs) throws Exception {
    return getLibraryFromFile(new ByteArrayInputStream(data), cbibs);
  }
  
  static public Library getLibraryFromFile(InputStream in, LibrarySet cbibs) throws Exception {
    Library bib;
    try {
      // Unmarshal XML file.
      XmlLibrary xmlLibrary = LibraryMarshaller.unmarshal(in);

      bib = new Library(xmlLibrary.getName(), ""); //$NON-NLS-1$
      bib.setDescription(xmlLibrary.getDescription());
      bib.setDescriptionKey(xmlLibrary.getI18NDescription().getValue());
      bib.setNameKey(xmlLibrary.getI18NName().getValue());

      for (int i = 0; i < xmlLibrary.getXmlBlock().size(); i++) {
        XmlBlockType block = xmlLibrary.getXmlBlock().get(i);
        String name = block.getType();
        String descricao = block.getDescription();
        String descrKey = block.getI18N().getValue();
        if (StringUtilities.isEmpty(descricao))
          descricao = name.substring(5);

        if (cbibs.hasLibrary(bib.getName()) || (bib.getComponent(descricao) == null && cbibs.getComponent(descricao) == null)) {
          // update/reload biblioteca or new component

          int nEntradas = 0;
          int nSaidas = 0;

          /* informacao dos portos */
          ArrayList<String> portosEntrada = new ArrayList<String>();
          ArrayList<String> portosSaida = new ArrayList<String>();
          ArrayList<String> portosDescEntrada = new ArrayList<String>();
          ArrayList<String> portosDescSaida = new ArrayList<String>();

          /* portos */
          for (int j = 0; j < block.getXmlPort().size(); j++) {
            XmlPortType port = block.getXmlPort().get(j);
            if (port.getInOut() == InOutType.IN ) {
              nEntradas++;
              portosEntrada.add(port.getName());
              String desc = port.getDescription();
              if (desc != null)
                portosDescEntrada.add(desc);
              else
                portosDescEntrada.add(port.getName());
            } else {
              nSaidas++;
              portosSaida.add(port.getName());
              String desc = port.getDescription();
              if (desc != null)
                portosDescSaida.add(desc);
              else
                portosDescSaida.add(port.getName());
            }

          }

          /* posicao das entradas e saidas */
          Point p_e[] = new Point[nEntradas];
          Point p_s[] = new Point[nSaidas];

          int _i = 0;
          int _j = 0;
          for (int j = 0; j < block.getXmlPort().size(); j++) {
            XmlPortType port = block.getXmlPort().get(j);
            if (port.getInOut() == InOutType.IN) {
              if (port.getPosition() != null)
                p_e[_i] = new Point(port.getPosition().getX(), port.getPosition().getY());
              else
                p_e[_i] = new Point(-10, 10 + 10 * _i);
              _i++;
            } else {
              if (port.getPosition() != null)
                p_s[_j] = new Point(port.getPosition().getX(), port.getPosition().getY());
              else
                p_s[_j] = new Point(10, 10 + 10 * _j);
              _j++;

            }
          }

          /* atributos */
          ArrayList<Atributo> attrs = new ArrayList<Atributo>();
          for (int j = 0; j < block.getXmlAttribute().size(); j++) {
            XmlAttributeType attr = block.getXmlAttribute().get(j);
            attrs.add(new AtributoImpl(attr.getName(), attr.getValue().getValue(), 
            		attr.getDescription(), attr.getValueType().toArray( new String[ attr.getValueType().size()] ) ));
          }

          /* tamanho do bloco */
          int largX = 0;
          int largY = 0;
          SizeType pos = block.getSize();
          if (pos != null) {
            largX = pos.getX();
            largY = pos.getY();
          } else {
            int _ins = -15;
            for (int aux = 0; aux < nEntradas; aux++)
              _ins = Math.max(_ins, Janela.FM_Tipo_8.stringWidth((String) portosDescEntrada.get(aux)));
            int _outs = -15;
            for (int aux = 0; aux < nSaidas; aux++)
              _outs = Math.max(_outs, Janela.FM_Tipo_8.stringWidth((String) portosDescSaida.get(aux)));
            largX = 20 + _ins + _outs;
            largY = 10 + Math.max(nEntradas, nSaidas) * 10;
          }

          _i = 0;
          _j = 0;
          for (int j = 0; j < block.getXmlPort().size(); j++) {
            XmlPortType port = block.getXmlPort().get(j);
            if (port.getInOut() == InOutType.OUT) {
              if (port.getPosition() == null)
                p_s[_j].x = 10 + largX;
              _j++;
            }
          }

          /* desenho */
          String imageName = "1"; //$NON-NLS-1$
          String imageAux = block.getImage();
          if (imageAux != null) {
            imageName = imageAux;
          }

          String nomeClasseAlteraAtributos = block.getClassName();
          String nomeFicheiroDefenicao = block.getFileName();

          boolean automatic = false;
          if (block.getAutomatic() != null) {
            automatic = ( block.getAutomatic() == BoolType.TRUE || block.getAutomatic() == BoolType.YES );
          }
          
          XmlBlockColor color = block.getColor();

          Componente_Biblioteca cb = new Componente_Biblioteca(nEntradas, nSaidas, name, imageName, largX, largY, p_e, p_s,
              portosEntrada, portosSaida, portosDescEntrada, portosDescSaida, attrs, nomeClasseAlteraAtributos,
              nomeFicheiroDefenicao, descricao, automatic, color, descrKey);
          bib.addSimpleComponent(cb);
        }
      }
    } catch (Exception e) {
      throw new Exception(Mesg.ErroLerBiblioteca, e);
    }

    return bib;
  }

}
