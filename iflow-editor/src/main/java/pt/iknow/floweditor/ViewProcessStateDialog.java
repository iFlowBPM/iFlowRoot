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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collection;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.utils.FlowInfo;
import pt.iknow.floweditor.DownloadFlowDialog.FlowInfoCellRenderer;
import pt.iknow.floweditor.DownloadFlowDialog.SubFlowCellRenderer;
import pt.iknow.floweditor.messages.Messages;
import pt.iknow.iflow.RepositoryClient;

public class ViewProcessStateDialog extends JDialog {
  private static final long serialVersionUID = 3791634244843600719L;

  protected JComboBox flowList;
  protected JTextField textField;
  
  private final static Color ONLINE = new Color(0x4D, 0x89, 0x31);
  private final static Color OFFLINE = new Color(0xE8, 0x02, 0x02);
  private final static int MIN_HEIGHT = 75;
  private final static int MIN_WIDTH = 450;
  
  private boolean extendedAPI = false;

  private String selected = null;
  private String pnumber;
  private int selected_index = -1;
  private byte[] flowData;
  
  public ViewProcessStateDialog(Frame parent) {
    super(parent, true);
    initComponents();
    setTitle(Mesg.TitleDownloadFlow);
    setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
    pack();
    setResizable(false);
    setVisible(true);
  }


  public Integer getFlowId() {
    Integer retObj = null;
    FlowInfo flow = (FlowInfo) flowList.getItemAt(selected_index);
    retObj = flow.getId();
    return retObj;
  }
  
  public int getFlowVersion() {
    return -1;
  }

  public String getFullFlowName() {
    String retObj = null;
    if (this.selected_index > -1) {
      retObj = selected;
    }
    return retObj;
  }

  public String getProcessNumber() {
    return pnumber;
  }
  
  public byte[] getFlowData() {
    return flowData;
  }

  private void initComponents() {
    JPanel mainPanel = new JPanel(new BorderLayout());
    JPanel buttonPanel = new JPanel();
    JPanel flowPanel = new JPanel(new BorderLayout());
    JPanel processPanel = new JPanel(new BorderLayout());

    setUpWindowEvents();

    flowPanel.add(getFlowLabel(), BorderLayout.LINE_START);
    flowPanel.add(getFlowList(), BorderLayout.LINE_END);

    processPanel.add(getProcessLabel(), BorderLayout.LINE_START);
    processPanel.add(getProcessTextField(), BorderLayout.LINE_END);

    mainPanel.add(flowPanel, BorderLayout.PAGE_START);
    mainPanel.add(processPanel, BorderLayout.PAGE_END);
    buttonPanel.add(getOkButton());
    buttonPanel.add(getCancelButton());

    getContentPane().add(mainPanel, BorderLayout.CENTER);
    getContentPane().add(buttonPanel, BorderLayout.PAGE_END);
  }

  private JComponent getFlowLabel() {
    String onlineColor = getHTMLColor(ONLINE);
    String offlineColor = getHTMLColor(OFFLINE);
    JLabel label = new JLabel(Messages.getString("ViewProcessStateDialog.flows", offlineColor, onlineColor), JLabel.LEFT);
    FontMetrics fm = label.getFontMetrics(label.getFont());
    int height = fm.getHeight() + 6;
    label.setPreferredSize(new Dimension(250, height));
    label.setMinimumSize(new java.awt.Dimension(100, height));
    return label;
  }

  private JComponent getFlowList() {
    this.flowList = new JComboBox();

    // load information from repository
    RepositoryClient rep = Janela.getInstance().getRepository();
    this.extendedAPI = rep.hasExtendedAPI();
    ListCellRenderer cellRenderer = null;
    Object[] list = new Object[0];

    if (extendedAPI) {
      Collection<FlowInfo> listFlows = rep.listFlowsExtended();
      if (listFlows != null) {
        list = listFlows.toArray();
      }
      cellRenderer = new FlowInfoCellRenderer();
    } else {
      String[] listFlows = rep.listFlows();
      if (listFlows != null) {
        list = listFlows;
      }
      cellRenderer = new SubFlowCellRenderer();
    }

    flowList.setEnabled(list.length > 0);
    flowList.setModel(new DefaultComboBoxModel(list));
    flowList.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        flowListSelectionChanged(evt);
      }
    });
    flowList.setRenderer(cellRenderer);

    return flowList;
  }

  private JComponent getProcessLabel() {
    JLabel label = new JLabel(Messages.getString("ViewProcessStateDialog.process"), JLabel.LEFT);
    FontMetrics fm = label.getFontMetrics(label.getFont());
    int height = fm.getHeight() + 6;
    label.setPreferredSize(new Dimension(250, height));
    label.setMinimumSize(new java.awt.Dimension(100, height));
    return label;
  }

  private JComponent getProcessTextField() {
    this.textField = new JTextField();
    FontMetrics fm = textField.getFontMetrics(textField.getFont());
    int height = fm.getHeight() + 6;
    textField.setPreferredSize(new Dimension(250, height));
    textField.setMinimumSize(new java.awt.Dimension(100, height));
    return textField;
  }

  private JComponent getOkButton() {
    JButton okButton = new JButton();
    okButton.setText(Mesg.OK);
    if (flowList.isEnabled()) {
      okButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          processOkButton(evt);
        }
      });
    } else {
      okButton.setEnabled(false);
    }
    return okButton;
  }

  private JComponent getCancelButton() {
    JButton cancelButton = new JButton();
    cancelButton.setText(Mesg.Cancelar);
    cancelButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        processCancelButton(evt);
      }
    });
    return cancelButton;
  }

  private String getHTMLColor(Color c) {
    String hexColor = Integer.toHexString(c.getRGB()).substring(2);
    return "#" + hexColor;
  }

  // EVENTS
  private void processOkButton(ActionEvent evt) {
    if (selected != null) {
      this.pnumber = this.textField.getText();
      if(StringUtils.isNotBlank(pnumber)) {
        setVisible(false);
        dispose();
      } else {
        JOptionPane.showMessageDialog(this, Messages.getString("ViewProcessStateDialog.process.warning"));
      }
    }
  }
  
  private void processCancelButton(ActionEvent evt) {
    this.selected = null;
    this.pnumber = null;
    dispose();
  }
  
  private void setUpWindowEvents() {
    setModal(true);
    addComponentListener(new ComponentAdapter() {
      public void componentResized(ComponentEvent evt) {
        int currentHeight = getContentPane().getHeight();
        int currentWidth = getContentPane().getWidth();
        if (currentHeight < MIN_HEIGHT) {
          getContentPane().setSize(currentWidth, MIN_HEIGHT);
        }
        currentHeight = getContentPane().getHeight();
        if(currentWidth < MIN_WIDTH) {
          getContentPane().setSize(MIN_WIDTH, currentHeight);
        }
        getContentPane().repaint();
      }
    });
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent evt) {
        setVisible(false);
        dispose();
      }
    });
  }

  private void flowListSelectionChanged(ActionEvent evt) {
    if (this.extendedAPI) {
      selected = ((FlowInfo) flowList.getSelectedItem()).getFlowFile();
    } else {
      selected = (String) flowList.getSelectedItem();
    }
    // select latest automatically
    selected_index = flowList.getSelectedIndex();
  }
}
