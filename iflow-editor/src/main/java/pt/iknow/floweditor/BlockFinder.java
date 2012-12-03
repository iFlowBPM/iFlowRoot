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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

/*******************************************************************************
 * 
 * Project FLOW EDITOR
 * 
 * class: FindComponent
 * 
 * desc: dialogo para encontrar um dado bloco no fluxo
 * 
 ******************************************************************************/

public class BlockFinder extends JDialog {

  private static final long serialVersionUID = -321L;
  
  private JButton cancelButton;
  private JButton searchButton;
  private JTextField searchField;
  private JList blockList;
  private BlockSearchInterface filter;
  

  /** Creates new form EncontraComponente */
  public BlockFinder(Janela janela, BlockSearchInterface filter) {
    super(janela, false);
    this.filter = filter;
    initComponents();
    setTitle(Mesg.SearchBlock);
  }

//  public static InstanciaComponente open(Janela janela, BlockSearchInterface filter) {
//    BlockFinder fc = new BlockFinder(janela, filter);
//    fc.setVisible(true);
//
//    return fc.componente;
//  }

  private void initComponents() {
    JPanel jPanel2 = new JPanel();
    searchField = new JTextField();
    searchButton = new JButton();
    JScrollPane jScrollPane1 = new JScrollPane();
    blockList = new JList();
    cancelButton = new JButton();

    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent evt) {
        cancelButtonActionPerformed();
      }
    });

    searchField.setMaximumSize(new Dimension(150, 20));
    searchField.setMinimumSize(new Dimension(150, 20));
    searchField.setPreferredSize(new Dimension(150, 20));
    searchField.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        searchFieldActionPerformed();
      }
    });

    jPanel2.add(searchField);

    searchButton.setText(Mesg.Search);
    searchButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        searchFieldActionPerformed();
      }
    });

    jPanel2.add(searchButton);

    getContentPane().add(jPanel2, BorderLayout.NORTH);

    jScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    jScrollPane1.setMaximumSize(new Dimension(250, 220));
    jScrollPane1.setMinimumSize(new Dimension(250, 220));
    jScrollPane1.setPreferredSize(new Dimension(250, 220));
    blockList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    blockList.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent evt) {
        listClicked(evt.getClickCount());
      }
    });

    jScrollPane1.setViewportView(blockList);

    getContentPane().add(jScrollPane1, BorderLayout.CENTER);
    
    JPanel panel = new JPanel();

    cancelButton.setText(Mesg.Close);
    cancelButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        cancelButtonActionPerformed();
      }
    });

    panel.add(cancelButton);

    getContentPane().add(panel, BorderLayout.SOUTH);
    searchFieldActionPerformed();
    pack();
  }

  /*****************************************************************************
   * EXIT
   */
  private void cancelButtonActionPerformed() {
    closeWindow();
  }

  /*****************************************************************************
   * selecciona um componente
   */
  private void listClicked(int clicks) {
    // Add your handling code here:
    if (clicks >= 2) {
      // closeWindow();
      filter.gotoBlock((InstanciaComponente) blockList.getSelectedValue());
    }
  }

  private void closeWindow() {
    setVisible(false);
    dispose();
  }
  
  /*****************************************************************************
   * cria lista de componentes encontrados
   */
  private void searchFieldActionPerformed() {
    // to preserve selection
    InstanciaComponente block = (InstanciaComponente) blockList.getSelectedValue();
    
    InstanciaComponente[] components = filter.search(searchField.getText());
    
    blockList.setListData(components);
    
    if(block != null && Arrays.binarySearch(components, block) >= 0) {
      blockList.setSelectedValue(block, true);
    }
      

  }
}
