package pt.iknow.floweditor.blocks;

/**
 * <p>Title: </p>
 * <p>Description: Diálogo para editar e criar o código bean shell</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: iKnow </p>
 * @author iKnow
 * @version 1.0
 */

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import pt.iknow.floweditor.Atributo;
import pt.iknow.floweditor.FlowEditorAdapter;

public class AlteraAtributosBeanShell extends AbstractAlteraAtributos implements AlteraAtributosInterface {
  private static final long serialVersionUID = 7332941294794991911L;

  private static final String sVAR_NAME = "code"; //$NON-NLS-1$

  JPanel jPanelMain = new JPanel(); // main window container
  JButton jButton1 = new JButton();
  JButton jButton2 = new JButton();
  JTextArea jTextArea = null;
  JEditorPane codeEditor = null;
  JScrollPane jScrollPane = null; // text area container

  private int exitStatus = EXIT_STATUS_CANCEL;
  private Object[][] data ;

  public AlteraAtributosBeanShell(FlowEditorAdapter adapter) {
    super(adapter, adapter.getString("AlteraAtributosBeanShell.title"), true); //$NON-NLS-1$
  }


  /**
   * getExitStatus
   * @return
   */
  public int getExitStatus() {
    return exitStatus;
  }

  /**
   * getNewAttributes
   * @return
   */
  public String[][] getNewAttributes() {
    String[][] newAttributes = new String[1][2];

    newAttributes[0][0] = sVAR_NAME;
    //newAttributes[0][1] = new String(jTextArea.getText());
    newAttributes[0][1] = new String(codeEditor.getText());

    return newAttributes;
  }

  public boolean fixedAttributes() {
    return true;
  }


  /**
   * setDataIn
   * @param title
   * @param atributos
   */
  public void setDataIn(String title, List<Atributo> atributos) {
    this.setTitle(title);

    data = new Object[1][2];
    Atributo atr = null;
    try {
      atr = (Atributo)atributos.get(0);
    } catch (IndexOutOfBoundsException e) {}

    if (atr != null) {
      data[0][0] = atr.getNome();
      data[0][1] = atr.getValor();
    }
    else {
      data[0][0] = sVAR_NAME;
      data[0][1] = ""; //$NON-NLS-1$
    }

    try {
      this.jbInit();
      this.pack();

    }
    catch(Exception ex) {
      adapter.log("error", ex);
    }

    this.setSize(700,400);
    this.setModal(true);
    this.repaint();
    this.setLocationRelativeTo(getParent());
    this.setVisible(true);
  }


  void jbInit()
  throws Exception {

    // MAIN WINDOW
    this.jPanelMain.setLayout(new BorderLayout());

    // borders
    JPanel aux=new JPanel();
    aux.setSize(100,1);
    this.jPanelMain.add(aux, BorderLayout.WEST);
    aux=new JPanel();
    aux.setSize(100,1);
    this.jPanelMain.add(aux, BorderLayout.EAST);
    aux=new JPanel();
    aux.setSize(1,100);
    this.jPanelMain.add(aux, BorderLayout.NORTH);



    // text area
    codeEditor = new JEditorPane();
    
    jScrollPane = new JScrollPane(codeEditor);
    codeEditor.setContentType("text/java");
    codeEditor.setText((String)data[0][1]);    
//    jTextArea =  new JTextArea(nROWS,nCOLS);
//    jTextArea.setText((String)data[0][1]);
//    jTextArea.setFont(new Font ("Monospaced",Font.PLAIN, 12)); //$NON-NLS-1$
//    jScrollPane = new JScrollPane(jTextArea);

    this.jPanelMain.add(jScrollPane, BorderLayout.CENTER);



    // buttons
    addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentResized(java.awt.event.ComponentEvent evt) {
        dialogComponentResized(evt);
      }
    });

    jButton1.setText(OK);
    jButton1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton1_actionPerformed(e);
      }
    });

    jButton2.setText(Cancelar);
    jButton2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton2_actionPerformed(e);
      }
    });


    aux = new JPanel();
    aux.add(jButton1, null);
    aux.add(jButton2, null);
    this.jPanelMain.add(aux, BorderLayout.SOUTH);

    this.getContentPane().add(this.jPanelMain);

  }

  /* OK */
  void jButton1_actionPerformed(ActionEvent e) {
    exitStatus=EXIT_STATUS_OK;
    dispose();
  }

  /* Cancelar */
  void jButton2_actionPerformed(ActionEvent e) {
    dispose();
  }

  public void dialogComponentResized(java.awt.event.ComponentEvent evt) { }

}
