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
import pt.iflow.api.xml.codegen.library.Size;
import pt.iflow.api.xml.codegen.library.XmlAttribute;
import pt.iflow.api.xml.codegen.library.XmlBlock;
import pt.iflow.api.xml.codegen.library.XmlLibrary;
import pt.iflow.api.xml.codegen.library.XmlPort;
import pt.iflow.api.xml.codegen.library.types.BoolType;
import pt.iflow.api.xml.codegen.library.types.InOutType;
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
      bib.setDescriptionKey(xmlLibrary.getI18nDescription());
      bib.setNameKey(xmlLibrary.getI18nName());

      for (int i = 0; i < xmlLibrary.getXmlBlockCount(); i++) {
        XmlBlock block = xmlLibrary.getXmlBlock(i);
        String name = block.getType();
        String descricao = block.getDescription();
        String descrKey = block.getI18n();
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
          for (int j = 0; j < block.getXmlPortCount(); j++) {
            XmlPort port = block.getXmlPort(j);
            if (port.getInOut().getType() == InOutType.IN_TYPE) {
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
          for (int j = 0; j < block.getXmlPortCount(); j++) {
            XmlPort port = block.getXmlPort(j);
            if (port.getInOut().getType() == InOutType.IN_TYPE) {
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
          for (int j = 0; j < block.getXmlAttributeCount(); j++) {
            XmlAttribute attr = block.getXmlAttribute(j);
            attrs.add(new AtributoImpl(attr.getName(), attr.getValue(), attr.getDescription(), attr.getValueType()));
          }

          /* tamanho do bloco */
          int largX = 0;
          int largY = 0;
          Size pos = block.getSize();
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
          for (int j = 0; j < block.getXmlPortCount(); j++) {
            XmlPort port = block.getXmlPort(j);
            if (port.getInOut().getType() == InOutType.OUT_TYPE) {
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
            int autotype = block.getAutomatic().getType();
            automatic = (autotype == BoolType.TRUE_TYPE || autotype == BoolType.YES_TYPE);
          }
          
          pt.iflow.api.xml.codegen.library.Color color = block.getColor();

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
