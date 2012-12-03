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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.CellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.lang.StringUtils;
import org.exolab.castor.xml.MarshalException;

import pt.iflow.api.xml.FlowDocsMarshaller;
import pt.iflow.connector.dms.ContentResult;
import pt.iknow.floweditor.FlowEditorAdapter;

public abstract class AlteraAtributosDMS extends AbstractAlteraAtributos implements AlteraAtributosInterface {
  protected static final long serialVersionUID = 681655163260359906L;

  protected static final String PATH = "path";

  protected int exitStatus = EXIT_STATUS_CANCEL;

  protected Object[][] data;

  protected final String[] AlteraAtributosColumnNames;

  protected final String[] AlteraAtributosDMSDynColumns;

  protected JTextField pathField;

  private JTree tree = new JTree();
  private JCheckBox togglePath;
  private JButton okButton = new JButton();
  private JButton cancelButton = new JButton();

  protected AlteraAtributosDMS(FlowEditorAdapter adapter, String title) {
    super(adapter, title, true);

    AlteraAtributosColumnNames = new String[]{ adapter.getString("AlteraAtributos.column.name"),
        adapter.getString("AlteraAtributos.column.value") };

    AlteraAtributosDMSDynColumns = new String[]{ adapter.getString("AlteraAtributosDocumentDMS.column.dyn.name"),
        adapter.getString("AlteraAtributosDocumentDMS.column.dyn.value") };
  }

  protected abstract void okActionPerformed(ActionEvent e);

  protected abstract void stopEditing();

  protected void stopEditing(JTable table) {
    if (table != null) {
      CellEditor currentEditor = table.getCellEditor();
      if (currentEditor != null) {
        currentEditor.stopCellEditing();
      }
    }
  }

  protected JPanel getTreePanel() {
    if (pathField == null) {
      pathField = new JTextField("");
    }
    if (togglePath == null) {
      togglePath = new JCheckBox(adapter.getString("AlteraAtributosDocumentDMS.label.path"));
    }
    JPanel top = new JPanel(new BorderLayout());
    top.add(togglePath, BorderLayout.PAGE_END);
    top.add(pathField, BorderLayout.PAGE_START);
    JPanel container = new JPanel(new BorderLayout());
    container.add(top, BorderLayout.PAGE_START);
    container.add(getTreeFolder(), BorderLayout.CENTER);
    container.setMinimumSize(new Dimension(200, -1));
    return container;
  }

  protected JPanel getButtonPanel() {
    okButton.setText(OK);
    okButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        okActionPerformed(e);
      }
    });
    cancelButton.setText(Cancelar);
    cancelButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        cancelActionPerformed(e);
      }
    });
    JPanel buttonPanel = new JPanel();
    buttonPanel.add(okButton);
    buttonPanel.add(cancelButton);
    return buttonPanel;
  }

  protected void cancelActionPerformed(ActionEvent e) {
    exitStatus = EXIT_STATUS_CANCEL;
    dispose();
  }

  private JComponent getTreeFolder() {
    togglePath.setEnabled(true);
    togglePath.setToolTipText("");
    if (tree == null) {
      tree = new JTree();
    }
    tree.setModel(new MyTreeModel(buildTreeNode(null)));
    tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    tree.addTreeSelectionListener(new TreeSelectionListener() {
      public void valueChanged(TreeSelectionEvent e) {
        treeValueChanged(e);
      }
    });
    tree.addTreeWillExpandListener(new TreeWillExpandListener() {
      public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
        treeExpanded(event);
      }

      public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
      }
    });
    tree.doLayout();
    tree.setEnabled(togglePath.isSelected());
    pathField.setEnabled(!togglePath.isSelected());
    for (int i = 0; i < data.length; i++) {
      if (StringUtils.equals("" + data[i][0], PATH)) {
        pathField.setText("" + data[i][1]);
      }
    }
    togglePath.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        tree.setEnabled(togglePath.isSelected());
        pathField.setEnabled(!togglePath.isSelected());
      }
    });
    return new JScrollPane(tree);
  }

  private void treeValueChanged(TreeSelectionEvent e) {
    stopEditing();
    DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
    if (node == null || node.isRoot()) {
      return;
    }
    ContentResult content = (ContentResult) node.getUserObject();
    if (togglePath.isSelected()) {
      String path = content.getPath();
      if (!path.startsWith("\"")) {
        path = "\"" + path;
      }
      if (!path.endsWith("\"")) {
        path = path + "\"";
      }
      pathField.setText(path);
    }
  }

  private void treeExpanded(TreeExpansionEvent event) {
    TreePath path = event.getPath();
    DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
    node = buildTreeNode(node);
    tree.repaint();
  }

  private DefaultMutableTreeNode buildTreeNode(DefaultMutableTreeNode node) {
    if (node == null) {
      node = new DefaultMutableTreeNode(new ContentResult());
    }
    ContentResult content = (ContentResult) node.getUserObject();
    if (!content.isLeaf()) {
      try {
        byte[] btData = adapter.getRepository().flowDocsGetFiles("", content.getPath());
        content = FlowDocsMarshaller.unmarshalContentResult(btData);
        for (ContentResult contentChild : content.getChildren()) {
          if (!contentChild.isLeaf()) {
            node.add(new DefaultMutableTreeNode(contentChild));
          }
        }
      } catch (MarshalException e) {
        String errorMsg = adapter.getString("AlteraAtributosDMS.error.title") + ": "
            + adapter.getString("AlteraAtributosDMS.error.message");
        togglePath.setSelected(false);
        togglePath.setEnabled(false);
        togglePath.setToolTipText(errorMsg);
        adapter.log(errorMsg, e);
      } catch (Exception e) {
        adapter.log("Error!", e);
      }
    }
    return node;
  }
}
