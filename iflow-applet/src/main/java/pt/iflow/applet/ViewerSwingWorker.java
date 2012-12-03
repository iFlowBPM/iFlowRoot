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
package pt.iflow.applet;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.UUID;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXSplitButton;
import org.psicover.pdf.PDFViewerPanel;
import org.psicover.pdf.PDFViewerToolbar;

import pt.iflow.applet.cipher.FileCipher;
import pt.iflow.applet.signer.FileSigner;

public class ViewerSwingWorker extends SwingWorker<IVFile, Object> {
  private JPopupMenu signMenu = new JPopupMenu(Messages.getString("ViewerSwingWorker.0")); //$NON-NLS-1$
  private JXSplitButton signButton = new JXSplitButton(null, null, signMenu);
  private PDFViewerPanel pdfPanel;
  private final Component parent;
  private final WebClient client;
  private final FileSigner signer;
  private final FileCipher cipher;
  private final String workerId;
  private IVFile vfile;
  private java.awt.Window window;
  
  public ViewerSwingWorker(final Component parent, final FileSigner signer, final FileCipher cipher, final WebClient client) {
    this.workerId = UUID.randomUUID().toString();
    this.parent = parent;
    this.signer = signer;
    this.cipher = cipher;
    this.client = client;
    initComponents();
  }
  
  private void initComponents() {
    Icon ico = SwingUtils.loadIcon("signature.png"); //$NON-NLS-1$
    if(null == ico)
      signButton = new JXSplitButton(Messages.getString("ViewerSwingWorker.2"), null, signMenu); //$NON-NLS-1$
    else
      signButton = new JXSplitButton(null, ico, signMenu);
    
    signButton.setToolTipText(Messages.getString("ViewerSwingWorker.3")); //$NON-NLS-1$
    signButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        sign();
      }
    });
    
    JMenuItem item = null;
    
    // assinatura
    item = new JMenuItem(Messages.getString("ViewerSwingWorker.4")); //$NON-NLS-1$
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        sign();
      }
    });
    signMenu.add(item);
    
    // verificar
    item = new JMenuItem(Messages.getString("ViewerSwingWorker.5")); //$NON-NLS-1$
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        verify();
      }
    });
    signMenu.add(item);
    
    // carregar
    item = new JMenuItem(Messages.getString("ViewerSwingWorker.6")); //$NON-NLS-1$
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        upload();
      }
    });
    signMenu.add(item);
    
    window = SwingUtils.getDialog(parent, Messages.getString("ViewerSwingWorker.7"), true); //$NON-NLS-1$

    // Add a window listener
    window.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        window.setVisible(false);
        window.dispose();
      }
    });


  }
  
  public String getWorkerId() {
    return this.workerId;
  }

  protected IVFile doInBackground() throws Exception {
    // TODO usar o file provider definido quando se instancia o objecto
    vfile = SwingUtils.showOpenFileDialog(parent, Messages.getString("ViewerSwingWorker.8"), this.signer.getFilter(), client.getVariable()); //$NON-NLS-1$
    if(null == vfile) return null;

    boolean fileOk = false;
    try {
      pdfPanel = new PDFViewerPanel(vfile.getFile());
      fileOk = true;
    } catch (IOException e) {
      JOptionPane.showMessageDialog(parent, Messages.getString("ViewerSwingWorker.9")+vfile.getName(), Messages.getString("ViewerSwingWorker.10"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
      vfile = null;
      window.dispose();
    }
    if(fileOk) {
      PDFViewerToolbar tb = pdfPanel.getToolbar();
      tb.addToolbarSeparator();
      tb.addToolbarComponent(signButton);
      fixMenuBorders(tb);
      window.setLayout(new BorderLayout());
      window.add(pdfPanel, BorderLayout.CENTER);
      window.pack();
      window.setSize(800, 600);
      window.setVisible(true);
    }

    return vfile;
  }

  private void fixMenuBorders(PDFViewerToolbar tb) {
    int cCount = tb.getComponentCount();
    for(int i = 0; i < cCount; i++) {
      Component c = tb.getComponent(i);
      if(c != null && c instanceof JXSplitButton) {
        JXSplitButton sbutton = (JXSplitButton) c;
        JPopupMenu smenu = sbutton.getDropDownMenu();
        if(smenu != null) {
          smenu.setBorderPainted(true);
          smenu.setBorder(BorderFactory.createLineBorder(Color.darkGray, 1));
        }
      }
      
    }
  }
  
  private void sign() {
    // TODO criar novo swingworker para processar a assinatura e carregamento do ficheiro?
    DynamicPanel formPanel = new DynamicPanel(signer, cipher);
    final Window dialog = SwingUtils.getDialog(parent, Messages.getString("ViewerSwingWorker.11"), true); //$NON-NLS-1$
    dialog.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        dialog.setVisible(false);
        dialog.dispose();
      }
    });
    dialog.setLayout(new BorderLayout());
    dialog.add(formPanel, BorderLayout.CENTER);

    dialog.pack();
    dialog.setVisible(true);
    
//    try {
//      IVFile signed = signer.sign(vfile);
//    } catch (SignerException e) {
//      // TODO Auto-generated catch block
//      e.printStackTrace();
//    }
    
    
    
    
  }

  private void verify() {
    System.out.println("Not implemented"); //$NON-NLS-1$

  }
  
  private void upload() {
    System.out.println("Not implemented"); //$NON-NLS-1$

  }

}
