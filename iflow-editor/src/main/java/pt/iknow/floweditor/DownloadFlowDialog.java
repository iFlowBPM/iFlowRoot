/*
 * <p>Title: DownloadFlowDialog.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) Sep 2, 2005</p>
 * <p>Company: iKnow</p>
 * @author Pedro Monteiro
 * @version 1.0
 */

package pt.iknow.floweditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;

import pt.iflow.api.utils.FlowInfo;
import pt.iknow.floweditor.messages.Messages;
import pt.iknow.iflow.RepositoryClient;
import pt.iknow.utils.StringUtilities;


public class DownloadFlowDialog extends javax.swing.JDialog {
  private static final long serialVersionUID = 1L;

  private final static Color cONLINE = new Color(0x4D,0x89,0x31);
  private final static Color cOFFLINE = new Color(0xE8,0x02,0x02);

  private boolean isFlow = false;
  private boolean extendedAPI = false;

  Componente_Biblioteca compBib=null;
  private String _selected = null;
  private String prevSelected = null;
  private int _selected_index = -1;
  private int _selectedVersion = -1;

  private javax.swing.JTextArea descriptionLabel;
  private javax.swing.JComboBox versionsComboBox;
  private javax.swing.JRadioButton currentButton;
  private javax.swing.JRadioButton previousButton;
  private javax.swing.JButton cancelButton;
  private javax.swing.JButton okButton;
  private javax.swing.JList flowList;

  public String getFlowName() {
    return _selected;
  }

  public int getFlowVersion() {
    return _selectedVersion;
  }
  
  public String getFullFlowName() {
    String retObj = null;
    if (this._selected_index > -1) {
      retObj = _selected;
    }

    return retObj;
  }

  public DownloadFlowDialog(java.awt.Frame parent, boolean isFlow) {
    super(parent, true);
    this.isFlow = isFlow;
    initComponents();
    setTitle(isFlow ? Mesg.TitleDownloadFlow : Mesg.TitleDownloadSubFlow);
    pack();
    setSize(550,350);
    setResizable(true);
    setLocationRelativeTo(getParent());
    setVisible(true);
  }

  private void initComponents() {
    javax.swing.JScrollPane jScrollPane1;
    javax.swing.JSplitPane jSplitPane;
    javax.swing.JPanel jPanel1;

    jSplitPane = new JSplitPane();
    okButton = new javax.swing.JButton();
    jPanel1 = new javax.swing.JPanel(new BorderLayout());
    jScrollPane1 = new javax.swing.JScrollPane();
    flowList = new javax.swing.JList();
    cancelButton = new javax.swing.JButton();

    // load information from repository
    RepositoryClient rep = Janela.getInstance().getRepository();
    this.extendedAPI = rep.hasExtendedAPI();
    ListCellRenderer cellRenderer = null;
    Object[] list = new Object[0];
    
    if(this.isFlow && this.extendedAPI) {
      Collection<FlowInfo> fileList = rep.listFlowsExtended();
      if (fileList != null) list = fileList.toArray();
      cellRenderer = new FlowInfoCellRenderer();
    } else if(this.isFlow && !this.extendedAPI) {
      String [] flowList = rep.listFlows();
      if(null != flowList) list = flowList;
      cellRenderer = new SubFlowCellRenderer();
    } else {
      String [] subFlowList = rep.listSubFlows();
      if(null != subFlowList) list = subFlowList;
      cellRenderer = new SubFlowCellRenderer();
    }

    flowList.setEnabled(list.length > 0);
    flowList.setListData(list);
    flowList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    flowList.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        jList1MouseClicked(evt);
      }
    });

    flowList.setCellRenderer(cellRenderer);

    setModal(true);
    addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentResized(java.awt.event.ComponentEvent evt) {
        formComponentResized(evt);
      }
    });

    addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowClosing(java.awt.event.WindowEvent evt) {
        closeDialog(evt);
      }
    });

    jScrollPane1.setMinimumSize(new java.awt.Dimension(100, 200));
    jScrollPane1.setPreferredSize(new java.awt.Dimension(250, 200));

    jScrollPane1.setViewportView(flowList);

    String onlineColor = getHTMLColor(cONLINE);
    String offlineColor = getHTMLColor(cOFFLINE);
    JLabel jltmp = new JLabel(
        Messages.getString(this.isFlow?"DownloadFlowDialog.flows":"DownloadFlowDialog.subflows", offlineColor, onlineColor), //$NON-NLS-1$ //$NON-NLS-2$
        JLabel.CENTER);
    FontMetrics fm = jltmp.getFontMetrics(jltmp.getFont());
    int height = fm.getHeight()+6;
    jltmp.setPreferredSize(new Dimension(250, height));
    jltmp.setMinimumSize(new java.awt.Dimension(100, height));
    jPanel1.add(jltmp, BorderLayout.NORTH);
    jPanel1.add(jScrollPane1, BorderLayout.CENTER);

    okButton.setText(Mesg.OK);
    if (flowList.isEnabled()) {
      okButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          okButtonActionPerformed(evt);
        }
      }); 
    }
    else {
      okButton.setEnabled(false);
    }
    cancelButton.setText(Mesg.Cancelar);
    cancelButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cancelButtonActionPerformed(evt);
      }
    });

    JPanel buttons = new JPanel();
    buttons.add(okButton);
    buttons.add(cancelButton);

    currentButton = new JRadioButton(Messages.getString("DownloadFlowDialog.currentVersion"), true); //$NON-NLS-1$
    currentButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e) {
        currentSelected();
      }
    });
    previousButton = new JRadioButton(Messages.getString("DownloadFlowDialog.previousVersion")); //$NON-NLS-1$
    previousButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e) {
        previousSelected();
      }
    });
    ButtonGroup buttonGroup = new ButtonGroup();
    buttonGroup.add(currentButton);
    buttonGroup.add(previousButton);
    
    versionsComboBox = new JComboBox();
    versionsComboBox.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e) {
        versionSelected();
      }
    });
    
    jSplitPane.add(jPanel1, JSplitPane.LEFT);
    JPanel pana = new JPanel(new GridBagLayout());
    GridBagConstraints gc = new GridBagConstraints();
    gc.fill = GridBagConstraints.HORIZONTAL;
    gc.gridx = 0;
    gc.gridy = 0;
    gc.weightx = 1.0;
    gc.insets = new Insets(3,3,3,3);
    JLabel versioningLabel = new JLabel(Messages.getString("DownloadFlowDialog.versionTitle")); //$NON-NLS-1$
    versioningLabel.setFont(versioningLabel.getFont().deriveFont(Font.BOLD));
    pana.add(versioningLabel, gc);
    gc.gridy++;
    pana.add(currentButton, gc);
    gc.gridy++;
    pana.add(previousButton, gc);
    gc.gridy++;
    pana.add(versionsComboBox, gc);
    gc.gridy++;
    pana.add(new JLabel(Messages.getString("DownloadFlowDialog.versionComment")), gc); //$NON-NLS-1$
    gc.gridy++;
    gc.fill = GridBagConstraints.BOTH;
    gc.weighty = 1.0;
    pana.add(new JScrollPane(descriptionLabel = new JTextArea()), gc);
    jSplitPane.add(pana, JSplitPane.RIGHT);
    
    getContentPane().add(jSplitPane, java.awt.BorderLayout.CENTER);
    getContentPane().add(buttons, java.awt.BorderLayout.SOUTH);

    descriptionLabel.setEditable(false);
    okButton.setEnabled(false);
    currentButton.setEnabled(false);
    previousButton.setEnabled(false);
    versionsComboBox.setEnabled(false);
//  pack();
  }

  
  private String getHTMLColor(Color c) {
    String s = Integer.toHexString(c.getRGB()).substring(2); // remove unwanted ff
    return "#"+s; //$NON-NLS-1$
  }
  
  private void formComponentResized(java.awt.event.ComponentEvent evt) {
  }

  private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
    _selected = null;
    _selected_index = -1;
    dispose();
  }

  private void jList1MouseClicked(java.awt.event.MouseEvent evt) {
    if(this.isFlow && this.extendedAPI) {
      _selected = ((FlowInfo) flowList.getSelectedValue()).getFlowFile();
    } else {
      _selected = (String) flowList.getSelectedValue();
    }
    
    if (evt.getClickCount() == 1) {
      _selected_index = flowList.getSelectedIndex();
      okButton.setEnabled(_selected_index != -1);
      currentButton.setEnabled(_selected_index != -1);
      previousButton.setEnabled(_selected_index != -1);
      currentButton.setSelected(true);
      currentSelected();
    } else if (evt.getClickCount() == 2) {
      // select latest automatically
      _selected_index = flowList.getSelectedIndex();
      if(_selected_index != -1) {
        setVisible(false);
        dispose();
      }
    }
  }

  private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {
    if (_selected != null) {
      setVisible(false);
      dispose();
    }
  }

  private void closeDialog(java.awt.event.WindowEvent evt) {
    setVisible(false);
    dispose();
  }

  private void currentSelected() {
    FlowEditor.log("Current selected"); //$NON-NLS-1$
    versionsComboBox.setEnabled(false);
    descriptionLabel.setText(Messages.getString("DownloadFlowDialog.comment.current")); //$NON-NLS-1$
    okButton.setEnabled(true);
    _selectedVersion = -1;
  }
  
  private void previousSelected() {
    FlowEditor.log("Previous selected"); //$NON-NLS-1$
    RepositoryClient rep = Janela.getInstance().getRepository();
    if(!StringUtilities.isEqual(prevSelected, _selected)) {
      prevSelected = _selected;
      String [] versoes = null;
      if(this.isFlow)
        versoes = rep.listFlowVersions(this._selected);
      else 
        versoes = rep.listSubFlowVersions(this._selected);

      versionsComboBox.removeAllItems();
      versionsComboBox.addItem(new VersionItem());
      if(null != versoes) {
        for (String versao : versoes) {
          versionsComboBox.addItem(new VersionItem(versao));
        }
      }
    }
    versionsComboBox.setEnabled(true);
    versionSelected();
  }
  
  private void versionSelected() {
    FlowEditor.log("Versions selected"); //$NON-NLS-1$
    VersionItem item = (VersionItem) versionsComboBox.getSelectedItem();
    if(item != null) {
      _selectedVersion = item.id;
      okButton.setEnabled(item.id != -1);
      String comment = "";
      if(item.id == -1)
        comment = Messages.getString("DownloadFlowDialog.comment.choose"); //$NON-NLS-1$
      else if(this.isFlow)
        comment = Janela.getInstance().getRepository().getFlowVersionComment(_selected, item.id);
      else 
        comment = Janela.getInstance().getRepository().getSubFlowVersionComment(_selected, item.id);
      descriptionLabel.setText(comment);
    }
  }
  
  private static class VersionItem {
    private int id;
    private String description;
    public VersionItem() {
      this.id = -1;
      this.description = "&lt;"+Messages.getString("DownloadFlowDialog.choose")+"&gt;"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
    
    public VersionItem(String s) {
      int pos = s.indexOf(';');
      this.id = Integer.parseInt(s.substring(0, pos));
      this.description = "v"+this.id+" - "+s.substring(pos+1); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    public String toString() {
      return "<html>"+this.description+"</html>"; //$NON-NLS-1$ //$NON-NLS-2$
    }
  }
  
  
  static class SubFlowCellRenderer extends JLabel implements ListCellRenderer {
    private static final long serialVersionUID = 1L;

    public SubFlowCellRenderer() {
      setOpaque(true);
    }

    public Component getListCellRendererComponent(JList list,
        Object value,
        int index,
        boolean isSelected,
        boolean cellHasFocus) {
      
      String s = value.toString();
      setText(s);
      setFont(list.getFont());
      
      if (isSelected) {
        setBackground(list.getSelectionBackground());
      }
      else {
        setBackground(list.getBackground());
      }
      setEnabled(list.isEnabled());
      return this;
    }
  }
  
  static class FlowInfoCellRenderer extends JLabel implements ListCellRenderer {
    private static final long serialVersionUID = 1L;

    public FlowInfoCellRenderer() {
      setOpaque(true);
    }

    public Component getListCellRendererComponent(JList list,
        Object value,
        int index,
        boolean isSelected,
        boolean cellHasFocus) {
      
      FlowInfo flowInfo = (FlowInfo) value;
      String s = "("+flowInfo.getId()+") "+flowInfo.getFlowFile(); //$NON-NLS-1$ //$NON-NLS-2$
      setText(s);
      setFont(list.getFont());
      if (flowInfo.isOnline()) {
        setForeground(DownloadFlowDialog.cONLINE);
      }
      else {
        setForeground(DownloadFlowDialog.cOFFLINE);
      }
      if (isSelected) {
        setBackground(list.getSelectionBackground());
      }
      else {
        setBackground(list.getBackground());
      }
      setEnabled(list.isEnabled());
      return this;
    }
  }
}
