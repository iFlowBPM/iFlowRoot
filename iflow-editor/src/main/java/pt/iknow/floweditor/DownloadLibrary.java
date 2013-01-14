package pt.iknow.floweditor;

import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import pt.iknow.floweditor.messages.Messages;
import pt.iknow.iflow.RepositoryClient;

/*******************************************************************************
 * 
 * Project FLOW EDITOR
 * 
 * class: DownloadLibrary
 * 
 * desc: dialogo para fazer download de biblioteca
 * 
 ******************************************************************************/

public class DownloadLibrary extends javax.swing.JDialog {
  private static final long serialVersionUID = 7551343924201517019L;

  private LibrarySet bibliotecas = null;
  private RepositoryClient _repository = null;
  private boolean bModal = false;

  private javax.swing.JFrame parent;
  private JPanel jPanel1;
  private JButton jButtonAll;
  private JButton jButtonOk;
  private JButton jButtonCancel;
  private JScrollPane jScrollPane1;
  private ArrayList<JCheckBox> alLibraryBoxes;
  private boolean bAll = false;

  private boolean isOk = false;

  public DownloadLibrary(javax.swing.JFrame parent, boolean modal, LibrarySet bib, boolean abSelected) {
    super(parent, modal);
    bAll = abSelected;
    bibliotecas = bib;
    bModal = modal;
    this.parent = parent;
    initComponents();
    setTitle(Mesg.TitleDownloadLibrary);
    setSize(300, 320);
    setLocationRelativeTo(getParent());
    setVisible(true);
  }

  public DownloadLibrary(javax.swing.JFrame parent, boolean modal, LibrarySet bib) {
    this(parent, modal, bib, false);
  }

  private void initComponents() {
    JCheckBox cb = null;

    jPanel1 = new JPanel();
    jScrollPane1 = new JScrollPane();
    jButtonAll = new JButton();
    jButtonOk = new JButton();
    jButtonCancel = new JButton();
    alLibraryBoxes = new ArrayList<JCheckBox>();

    // load information from repository
    _repository = Janela.getInstance().getRepository();

    String[] fileList = _repository.listLibraries();

    if (fileList == null)
      fileList = new String[0];

    JPanel checkPanel = new JPanel(new GridLayout(0, 1));

    for (int i = 0; i < fileList.length; i++) {
        if ("default.xml".equals(fileList[i]))  //$NON-NLS-1$
            continue;
      cb = new JCheckBox(fileList[i]);

      cb.setSelected(bAll);
      alLibraryBoxes.add(cb);
      checkPanel.add(cb);
    }

    setModal(bModal);
    setResizable(false);

    addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowClosing(java.awt.event.WindowEvent evt) {
        closeDialog();
      }
    });

    jScrollPane1.setMaximumSize(new java.awt.Dimension(250, 200));
    jScrollPane1.setMinimumSize(new java.awt.Dimension(250, 200));
    jScrollPane1.setPreferredSize(new java.awt.Dimension(250, 200));

    jScrollPane1.setViewportView(checkPanel);

    JLabel jltmp = new JLabel(Messages.getString("DownloadLibrary.download.librarry")); //$NON-NLS-1$
    jPanel1.add(jltmp);

    jPanel1.add(jScrollPane1);

    jButtonAll.setText(Messages.getString("DownloadLibrary.all")); //$NON-NLS-1$
    jButtonAll.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButtonAllActionPerformed(evt);
      }
    });
    jButtonOk.setText(Mesg.OK);
    jButtonOk.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButtonOkActionPerformed(evt);
      }
    });
    jButtonCancel.setText(Mesg.Cancelar);
    jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButtonCancelActionPerformed(evt);
      }
    });

    jPanel1.add(jButtonAll);
    jPanel1.add(jButtonOk);
    jPanel1.add(jButtonCancel);

    getRootPane().setDefaultButton(jButtonOk);

    getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

  }

  private void jButtonAllActionPerformed(java.awt.event.ActionEvent evt) {
    // all

    bAll = !bAll;

    JCheckBox cb = null;
    for (int i = 0; i < alLibraryBoxes.size(); i++) {
      cb = alLibraryBoxes.get(i);
      cb.setSelected(bAll);
    }
  }

  private void jButtonOkActionPerformed(java.awt.event.ActionEvent evt) {
    // ok
    isOk = true;
    dispose();
  }

  private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {
    // CANCEL
    this.closeDialog();
  }

  private void closeDialog() {
    setVisible(false);
    dispose();
  }

  public RepositoryClient getRepository() {
    return _repository;
  }

  public Library[] getSelectedLibraries() {
    Library[] retObj = null;

    ArrayList<Library> alLibraries = new ArrayList<Library>();
    JCheckBox cb = null;
    Library b = null;

    for (int i = 0; i < alLibraryBoxes.size(); i++) {
      cb = alLibraryBoxes.get(i);
      if (cb.isSelected()) {
        String libraryFile = cb.getText();
        FlowEditor.log("  reading library " + libraryFile); //$NON-NLS-1$
        try {
          b = Ler_Biblioteca.getLibraryFromData(_repository.getLibrary(libraryFile), bibliotecas); //$NON-NLS-1$
          alLibraries.add(b);
        } catch (Exception er) {
          new Erro(Mesg.ErroBiblioteca + " " + libraryFile, parent); //$NON-NLS-1$
        }
      }
    }

    retObj = alLibraries.toArray(new Library[alLibraries.size()]);
    return retObj;
  }
  
  public boolean isOk() {
    return isOk;
  }
}
