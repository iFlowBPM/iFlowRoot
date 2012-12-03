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
package pt.iknow.floweditor.blocks;

/**
 * <p>Title: </p>
 * <p>Description: Dialogo para criar e editar eventos </p></p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: iKnow </p>
 * @author Pedro Monteiro
 * @version 1.0
 */

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import pt.iknow.floweditor.Atributo;
import pt.iknow.floweditor.FlowEditorAdapter;
import pt.iknow.iflow.RepositoryClient;
import pt.iknow.utils.swing.MyColumnEditorModel;
import pt.iknow.utils.swing.MyJTableX;
import pt.iknow.utils.swing.MyTableModel;

public class AlteraAtributosEventTrigger extends AbstractAlteraAtributos implements AlteraAtributosInterface {
    private static final long serialVersionUID = 7856715939266621714L;

    public static final String sFLOWID = "flowid";
    public static final String sPID = "pid";
    public static final String sSUBPID = "subPid";
    public static final String sBLOCKID = "blockid";

    JPanel panel1 = new JPanel();
    BorderLayout borderLayout1 = new BorderLayout();
    JPanel jPanel1 = new JPanel();
    JPanel jPanel2 = new JPanel();
    JButton jButton1 = new JButton();
    JButton jButton2 = new JButton();
    JPanel jPanel3 = new JPanel();
    JScrollPane jScrollPane1 = new JScrollPane();
    MyJTableX jTable1 = new MyJTableX();

    JTextField _jtfFid = null;
    JTextField _jtfPid = null;
    JTextField _jtfSubPid = null;
    JTextField _jtfBlockid = null;
    JTextField _jtfisAsynchronous = null;

    String[] saOptions = null;

    private final String[] AlteraAtributosColumnNames;
    private final String _sSELECT;
    
    public final String sDESC_FLOWID ;
    public final String sDESC_PID ;
    public final String sDESC_SUBPID ;
    public final String sDESC_BLOCKID ;

    private int exitStatus = EXIT_STATUS_CANCEL;
    private String[][] data;

    
    public AlteraAtributosEventTrigger(FlowEditorAdapter adapter) {
      super(adapter, "", true); //$NON-NLS-1$
      
      AlteraAtributosColumnNames = new String[]{
          adapter.getString("AlteraAtributosLaunchAsyncEvent.name"), //$NON-NLS-1$
          adapter.getString("AlteraAtributosLaunchAsyncEvent.value"), //$NON-NLS-1$
          };
      
      sDESC_FLOWID = adapter.getString("AlteraAtributosLaunchAsyncEvent.fid");
      sDESC_PID = adapter.getString("AlteraAtributosLaunchAsyncEvent.pid"); ;
      sDESC_SUBPID = adapter.getString("AlteraAtributosLaunchAsyncEvent.subpid");
      sDESC_BLOCKID = adapter.getString("AlteraAtributosLaunchAsyncEvent.blockid");
      
      _sSELECT = adapter.getString("AlteraAtributosEmail.choose"); //$NON-NLS-1$
    }


    public int getExitStatus() {
      return exitStatus;
    }

    public String[][] getNewAttributes() {
      data[0][0] = AlteraAtributosEventTrigger.sFLOWID;
      data[1][0] = AlteraAtributosEventTrigger.sPID;
      data[2][0] = AlteraAtributosEventTrigger.sSUBPID;
      data[3][0] = AlteraAtributosEventTrigger.sBLOCKID;
      
      return data;
    }

    public void setDataIn(String title, List<Atributo> atributos) {
      setTitle(title);

      RepositoryClient rep = adapter.getRepository();
      if (rep != null && (saOptions == null || saOptions.length == 0)) {
        saOptions = rep.listMailTemplates();
      }
      if (saOptions == null) {
        saOptions = new String[0];
      }

      int size = atributos.size();
      if (size < 4) {
        size = 4;
      }
      data = new String[size][2];

      String stmp = null;

      // FID
      data[0][0] = sDESC_FLOWID;
      if (atributos != null && atributos.size() > 0 && atributos.get(0) != null)
        stmp = new String(((Atributo) atributos.get(0)).getValor());
      else
        stmp = null;
      if (stmp == null || stmp.equals("")) //$NON-NLS-1$
        stmp = ""; //$NON-NLS-1$
      data[0][1] = stmp;
      this._jtfFid = new JTextField();
      this._jtfFid.setText((String) data[0][1]);

      // PID
      data[1][0] = sDESC_PID;
      if (atributos != null && atributos.size() > 1 && atributos.get(1) != null)
        stmp = new String(((Atributo) atributos.get(1)).getValor());
      else
        stmp = null;
      if (stmp == null || stmp.equals("")) //$NON-NLS-1$
        stmp = ""; //$NON-NLS-1$
      data[1][1] = stmp;
      this._jtfPid = new JTextField();
      this._jtfPid.setText((String) data[1][1]);

      // SUBPID
      data[2][0] = sDESC_SUBPID;
      if (atributos != null && atributos.size() > 2 && atributos.get(2) != null)
        stmp = new String(((Atributo) atributos.get(2)).getValor());
      else
        stmp = null;
      if (stmp == null || stmp.equals("")) //$NON-NLS-1$
        stmp = ""; //$NON-NLS-1$
      data[2][1] = stmp;
      this._jtfSubPid = new JTextField();
      this._jtfSubPid.setText((String) data[2][1]);
      
   // BlockID
      data[3][0] = sDESC_BLOCKID;
      if (atributos != null && atributos.size() > 3 && atributos.get(3) != null)
        stmp = new String(((Atributo) atributos.get(3)).getValor());
      else
        stmp = null;
      if (stmp == null || stmp.equals("")) //$NON-NLS-1$
        stmp = ""; //$NON-NLS-1$
      data[3][1] = stmp;
      this._jtfBlockid = new JTextField();
      this._jtfBlockid.setText((String) data[3][1]);

      jTable1 = new MyJTableX(data, AlteraAtributosColumnNames);

      MyTableModel tableModel = new MyTableModel(AlteraAtributosColumnNames, data);
      tableModel.setColumnEditable(0, false);

      /* table model -> can not edit 1st collunn */
      jTable1.setModel(tableModel);

      jTable1.setRowSelectionAllowed(false);
      jTable1.setColumnSelectionAllowed(false);

      MyColumnEditorModel cm = new MyColumnEditorModel();
      jTable1.setMyColumnEditorModel(cm);

      DefaultCellEditor ed = null;
      ed = new DefaultCellEditor(this._jtfFid);
      cm.addEditorForCell(0, 1, ed);
      ed = new DefaultCellEditor(this._jtfPid);
      cm.addEditorForCell(1, 1, ed);
      ed = new DefaultCellEditor(this._jtfSubPid);
      cm.addEditorForCell(2, 1, ed);
      ed = new DefaultCellEditor(this._jtfBlockid);
      cm.addEditorForCell(3, 1, ed);

      /* criar botoes e arranjar dialogo */
      try {
        jbInit();
        pack();
      } catch (Exception ex) {
        adapter.log("error", ex);
      }

      this.setSize(500, 250);
      setVisible(true);
    }

    /**
     * Iniciar caixa de di?logo
     * 
     * @throws Exception
     */
    void jbInit() throws Exception {
      // configurar
      panel1.setLayout(borderLayout1);

      this.setSize(300, 250);

      /* resize */
      addComponentListener(new java.awt.event.ComponentAdapter() {
        public void componentResized(java.awt.event.ComponentEvent evt) {
          dialogComponentResized(evt);
        }
      });

      /* botao OK */
      jButton1.setText(OK); //$NON-NLS-1$

      jButton1.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          jButton1_actionPerformed(e);
        }
      });

      /* botao cancelar */
      jButton2.setText(Cancelar); //$NON-NLS-1$
      jButton2.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          jButton2_actionPerformed(e);
        }
      });

      jTable1.setRowSelectionAllowed(false);
      this.setModal(true);

      /* paineis */
      JPanel aux1 = new JPanel();
      aux1.setSize(100, 1);
      getContentPane().add(aux1, BorderLayout.WEST);
      JPanel aux2 = new JPanel();
      aux2.setSize(100, 1);
      getContentPane().add(aux2, BorderLayout.EAST);

      jScrollPane1.getViewport().add(jTable1, null);

      getContentPane().add(jScrollPane1, BorderLayout.CENTER);

      this.getContentPane().add(jPanel2, BorderLayout.SOUTH);
      jPanel2.add(jButton1, null);
      jPanel2.add(jButton2, null);
      this.getContentPane().add(jPanel3, BorderLayout.NORTH);

      dialogComponentResized(null);
      repaint();
    }

    /* OK */
    void jButton1_actionPerformed(ActionEvent e) {
      jTable1.stopEditing();

      // fid
      data[0][1] = jTable1.getStringAt(0, 1);

      // pid
      data[1][1] = jTable1.getStringAt(1, 1);

      // subpid
      data[2][1] = jTable1.getStringAt(2, 1);
      
      // blockid
      data[3][1] = jTable1.getStringAt(3, 1);

      exitStatus = EXIT_STATUS_OK;
      dispose();
    }

    /* Cancelar */
    void jButton2_actionPerformed(ActionEvent e) {
      exitStatus = EXIT_STATUS_CANCEL;
      dispose();
    }

    public void dialogComponentResized(java.awt.event.ComponentEvent evt) {
    }

}
