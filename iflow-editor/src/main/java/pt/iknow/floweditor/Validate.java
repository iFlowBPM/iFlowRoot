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
 *  class: Validate
 *
 *  desc: dialogo para validação de entradas e saidas
 *
 ****************************************************/

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang.StringUtils;

public class Validate {

  static public void VerifyFlow(List<InstanciaComponente> components, JFrame janela) {
    List<ValidateInfo> lista = new ArrayList<ValidateInfo>();

    /* percorrer lista de componentes */
    for (int z = 0; z < components.size(); z++) {
      InstanciaComponente ic = components.get(z);

      /* encontrar entrada não ligada */
      for (int i = 0; i < ic.lista_estado_entradas.size(); i++) {
        boolean ja = false;
        List<Conector> l = ic.lista_estado_entradas.get(i);
        for (int k = 0; k < l.size() && !ja; k++) {
          Conector c = l.get(k);
          Conector c_in = Linha.daIn(c);
          if (c_in != null)
            if (c_in.Comp != null)
              ja = true;
        }
        if (!ja) {
          lista.add(new ValidateInfo(ic, (String) ic.C_B.desc_nomes_entradas.get(i), Mesg.ERROIn));
        }
      }

      /* encontrar saida não ligada */
      for (int i = 0; i < ic.lista_estado_saidas.size(); i++) {
        boolean ja = false;
        List<Conector> l = ic.lista_estado_saidas.get(i);
        for (int k = 0; k < l.size() && !ja; k++) {
          Conector c = l.get(k);
          Conector c_in = Linha.daOut(c);
          if (c_in != null)
            if (c_in.Comp != null)
              ja = true;
        }
        if (!ja) {
          lista.add(new ValidateInfo(ic, (String) ic.C_B.desc_nomes_saidas.get(i), Mesg.ERROOut));
        }
      }
    }

    /* mostrar mensagem adequada */
    if (lista.size() == 0)
      new Informacao(Mesg.WithoutError, janela);
    else {
      new Erro(Mesg.WithError, janela);
      new ValidateDialog(janela, Mesg.ValidateTitle, true, lista);
    }
  }

  /*****************************************************************************
   * classe auxiliar com informacao de entrada ou saida nao ligada
   */
  static class ValidateInfo {
    public InstanciaComponente componente;
    public String porto;
    public String erro;

    public ValidateInfo(InstanciaComponente c, String p, String e) {
      componente = c;
      porto = p;
      erro = e;
    }
  }

  /*****************************************************************************
   * dialogo con informacao de entradas / saidas não conectadas
   */
  static class ValidateDialog extends JDialog {
    private static final long serialVersionUID = 1847272458411286262L;

    JPanel panel1 = new JPanel();
    JPanel jPanel2 = new JPanel();
    BorderLayout borderLayout1 = new BorderLayout();
    JScrollPane jScrollPane1 = new JScrollPane();
    JTable jTable1 = new JTable();
    String[][] data;
    JButton jButton1 = new JButton();

    /* construtor */
    public ValidateDialog(Frame frame, String title, boolean modal, List<ValidateInfo> list) {
      super(frame, title, modal);

      jbInit(list);
      pack();
      setSize(600, 150);
      setVisible(true);
    }

    /* insere componentes */
    void jbInit(List<ValidateInfo> lista) {

      data = new String[lista.size()][4];

      /* lista para a tabela */
      for (int i = 0; i < lista.size(); i++) {
        ValidateInfo info = lista.get(i);
        String descr = info.componente.C_B.Descricao;
        if(StringUtils.isNotBlank(info.componente.C_B.descrKey))
          descr = Janela.getInstance().getBlockMessages().getString(info.componente.C_B.descrKey);

        data[i][0] = info.componente.Nome;
        data[i][1] = descr;
        data[i][2] = info.porto;
        data[i][3] = info.erro;
      }

      jTable1 = new JTable(data, Mesg.ValidateClumnNames);

      /* table model -> no edition possible */
      jTable1.setModel(new AbstractTableModel() {
        private static final long serialVersionUID = 1L;

        public String getColumnName(int col) {
          return Mesg.AlteraAtributosColumnNames[col].toString();
        }

        public int getRowCount() {
          return data.length;
        }

        public int getColumnCount() {
          return Mesg.AlteraAtributosColumnNames.length;
        }

        public Object getValueAt(int row, int col) {
          return data[row][col];
        }

        public boolean isCellEditable(int row, int col) {
          return false;
        }

        public void setValueAt(Object value, int row, int col) {
          data[row][col] = (String) value;
          fireTableCellUpdated(row, col);
        }
      });

      /* botao para sair */
      jButton1.setText(Mesg.OK);
      jButton1.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          jButton1_actionPerformed(e);
        }
      });

      /* config */
      this.getContentPane().add(jPanel2, BorderLayout.SOUTH);
      jPanel2.add(jButton1, null);
      panel1.setLayout(borderLayout1);
      getContentPane().add(panel1);
      panel1.add(jScrollPane1, BorderLayout.CENTER);
      jScrollPane1.getViewport().add(jTable1, null);
    }

    /* OK */
    void jButton1_actionPerformed(ActionEvent e) {
      dispose();
    }
  }
}
