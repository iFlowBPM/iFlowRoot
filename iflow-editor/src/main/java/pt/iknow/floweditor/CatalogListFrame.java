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

import java.util.Collection;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.WindowConstants;
import javax.swing.tree.DefaultMutableTreeNode;


// TODO Pretendo extender JFrame e apresentar a tabela com o catalogo de variaveis
public class CatalogListFrame extends JFrame {
  /**
   * 
   */
  private static final long serialVersionUID = -5401848332963797223L;
  
  public CatalogListFrame() {
    initComponent();
  }
  
  private JTree catalogTree;
  private void initComponent() {
    
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    
    JScrollPane treeScrollPane = new JScrollPane();
    DefaultMutableTreeNode root = new DefaultMutableTreeNode();
    
    Desenho d = null;
    Collection<Atributo> catalogue = d.getCatalogue();
    
    for(Atributo a : catalogue) {
      DefaultMutableTreeNode node = new DefaultMutableTreeNode(a);
      root.add(node);
    }
    
    
    
    catalogTree = new JTree(root);

    setLayout(new java.awt.BorderLayout());

    treeScrollPane.setViewportView(catalogTree);

    add(treeScrollPane, java.awt.BorderLayout.CENTER);

  }
  

}
