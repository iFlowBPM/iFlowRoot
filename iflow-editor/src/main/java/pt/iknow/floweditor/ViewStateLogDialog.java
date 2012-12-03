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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.SoftBevelBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.apache.commons.collections15.OrderedMap;
import org.apache.commons.collections15.map.ListOrderedMap;
import org.apache.commons.lang.StringUtils;

import pt.iflow.api.transition.FlowStateLogTO;
import pt.iflow.api.transition.LogTO;
import pt.iknow.floweditor.messages.Messages;
import pt.iknow.utils.swing.ButtonChanger;
import pt.iknow.utils.swing.MyTableModel;

public class ViewStateLogDialog extends JDialog {
  private static final long serialVersionUID = 4911541015422301611L;

  private JTable jTable;
  private JLabel pagingLabel;
  private JButton firstButton, previousButton, nextButton, lastButton;

  private final int ROWS = 20;
  private int currentPage;

  // Mandatory Columns
  private final String COLUMN_USERNAME = Messages.getString("ViewStateLogDialog.column.userName");
  private final String COLUMN_CREATION_DATE = Messages.getString("ViewStateLogDialog.column.creationDate");
  private final String COLUMN_LOG = Messages.getString("ViewStateLogDialog.column.log");
  private final String COLUMN_DETAIL = Messages.getString("ViewStateLogDialog.column.detail");
  // Optional Columns
  private final String COLUMN_CALLER = Messages.getString("ViewStateLogDialog.column.caller");
  private final String COLUMN_METHOD = Messages.getString("ViewStateLogDialog.column.method");

  private OrderedMap<Integer, List<FlowStateLogTO>> flowStateLog;
  private Map<PAGES, ImageIcon> images;

  private final int WIDTH = 600;
  private final int HEIGHT = 450;

  public ViewStateLogDialog(Frame parent, List<FlowStateLogTO> flowStateLogs, Map<PAGES, ImageIcon> images) {
    super(parent, true);

    this.flowStateLog = new ListOrderedMap<Integer, List<FlowStateLogTO>>();
    this.images = images;
    this.currentPage = 1;
    List<FlowStateLogTO> tmpList = new ArrayList<FlowStateLogTO>();
    int indexer = 0;
    for (FlowStateLogTO item : flowStateLogs) {
      tmpList.add(item);
      indexer++;
      if (indexer >= ROWS) {
        this.flowStateLog.put(this.currentPage, tmpList);
        tmpList = new ArrayList<FlowStateLogTO>();
        this.currentPage++;
        indexer = 0;
      }
    }
    if (!tmpList.isEmpty()) {
      this.flowStateLog.put(this.currentPage, tmpList);
    }
    tmpList = null;
    this.currentPage = 1;

    initComponents();
    String titleAppend = "";
    if (flowStateLogs.size() == 1) {
      titleAppend = " (" + flowStateLogs.size() + " " + Messages.getString("ViewStateLogDialog.title.append.1") + ")";
    } else {
      titleAppend = " (" + flowStateLogs.size() + " " + Messages.getString("ViewStateLogDialog.title.append.N") + ")";
    }
    setTitle(Messages.getString("ViewStateLogDialog.title") + titleAppend);
    pack();
    setSize(new Dimension(WIDTH, HEIGHT));
    setVisible(true);
  }

  private boolean hasColumn(List<FlowStateLogTO> flowStateLogs, String columnName) {
    boolean retObj = false;
    for (FlowStateLogTO item : flowStateLogs) {
      if (item.getLog() != null && StringUtils.isNotBlank(item.getLog().getValueOf(columnName))) {
        retObj = true;
        break;
      }
    }
    return retObj;
  }

  private void initComponents() {

    JPanel navItems = new JPanel(new GridLayout());
    navItems.add(getFirstPageButton());
    navItems.add(getPreviousPageButton());
    navItems.add(updatePagingLabel());
    navItems.add(getNextPageButton());
    navItems.add(getLastPageButton());

    JPanel navPanel = new JPanel();
    navPanel.add(navItems);

    JPanel exitPanel = new JPanel();
    exitPanel.add(getCloseButton());

    JPanel buttonPanel = new JPanel(new BorderLayout());
    buttonPanel.add(navPanel, BorderLayout.PAGE_START);
    buttonPanel.add(exitPanel, BorderLayout.PAGE_END);

    updateDataTable();
    getContentPane().add(new JScrollPane(jTable), BorderLayout.CENTER);
    getContentPane().add(buttonPanel, BorderLayout.PAGE_END);

    setUpWindowEvents();
  }

  public enum PAGES {
    FIRST, LAST, PREVIOUS, NEXT, DETAILS
  };

  private void goToPage(PAGES page) {
    int minPage = 1;
    int maxPage = (this.flowStateLog.size() != 0 ? this.flowStateLog.size() : minPage);
    switch (page) {
    case FIRST:
      this.currentPage = minPage;
      break;
    case LAST:
      this.currentPage = maxPage;
      break;
    case PREVIOUS:
      this.currentPage = (this.currentPage > minPage ? this.currentPage - 1 : minPage);
      break;
    case NEXT:
      this.currentPage = (this.currentPage < maxPage ? this.currentPage + 1 : maxPage);
      break;
    default:
      break;
    }
    updatePagingLabel();
    updateDataTable();
    validate();
    repaint();
  }

  private void setUpWindowEvents() {
    setModal(true);
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent evt) {
        setVisible(false);
        dispose();
      }
    });
  }

  private JComponent getCloseButton() {
    JButton okButton = new JButton();
    okButton.setText(Mesg.Close);
    okButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        dispose();
      }
    });
    return okButton;
  }

  private JComponent updatePagingLabel() {
    if (pagingLabel == null) {
      pagingLabel = new JLabel();
    }
    pagingLabel.setText("   " + this.currentPage + "/" + (this.flowStateLog.size() > 0 ? this.flowStateLog.size() : 1) + "  ");

    int firstPage = 1;
    int lastPage = (this.flowStateLog.size() > 0 ? this.flowStateLog.size() : firstPage);
    if (this.currentPage == firstPage) {
      getFirstPageButton().setEnabled(false);
      getPreviousPageButton().setEnabled(false);
    } else {
      getFirstPageButton().setEnabled(true);
      getPreviousPageButton().setEnabled(true);
    }
    if (this.currentPage == lastPage) {
      getLastPageButton().setEnabled(false);
      getNextPageButton().setEnabled(false);
    } else {
      getLastPageButton().setEnabled(true);
      getNextPageButton().setEnabled(true);
    }
    pagingLabel.repaint();
    return pagingLabel;
  }

  private JComponent updateDataTable() {
    List<FlowStateLogTO> flowStateLogs = this.flowStateLog.get(this.currentPage);
    if (flowStateLogs == null) {
      flowStateLogs = new ArrayList<FlowStateLogTO>();
    }
    List<String> columns = new ArrayList<String>();
    columns.add(COLUMN_CREATION_DATE);
    columns.add(COLUMN_USERNAME);
    if (hasColumn(flowStateLogs, LogTO.CALLER)) {
      columns.add(COLUMN_CALLER);
    }
    if (hasColumn(flowStateLogs, LogTO.METHOD)) {
      columns.add(COLUMN_METHOD);
    }
    columns.add(COLUMN_LOG);
    columns.add(COLUMN_DETAIL);

    int row = 0;
    Object[][] rowData = new Object[flowStateLogs.size()][columns.size()];

    // display
    SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    // tooltip
    SimpleDateFormat toolTipFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    for (FlowStateLogTO stateLog : flowStateLogs) {
      int column = 0;
      LogTO contents = stateLog.getLog();
      if (column < columns.size()) {
        rowData[row][column] = createTableLabel(dateFormatter.format(stateLog.getLog().getCreationDate()), toolTipFormatter.format(stateLog.getLog().getCreationDate()), contents);
        column++;
      }
      if (column < columns.size()) {
        rowData[row][column] = createTableLabel(stateLog.getLog().getUsername(), contents);
        column++;
      }
      if (hasColumn(flowStateLogs, LogTO.CALLER) && column < columns.size()) {
        rowData[row][column] = createTableLabel(stateLog.getLog().getCaller(), contents);
        column++;
      }
      if (hasColumn(flowStateLogs, LogTO.METHOD) && column < columns.size()) {
        rowData[row][column] = createTableLabel(stateLog.getLog().getMethod(), contents);
        column++;
      }
      if (column < columns.size()) {
        rowData[row][column] = createTableLabel(stateLog.getLog().getLog(), contents);
        column++;
      }
      if (column < columns.size()) {
        rowData[row][column] = createTableButton(contents);
      }
      row++;
    }
    MyTableModel dataModel = new MyTableModel(columns.toArray(new String[columns.size()]), rowData);
    for (int r = 0; r < rowData.length; r++) {
      for (int c = 0, lim = rowData[r].length; c < lim; c++) {
        dataModel.setCellEditable(r, c, false);
      }
    }
    if (jTable == null) {
      jTable = new JTable(dataModel);
    }
    jTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

    int tabDateWidth = 125;
    int tabPreferredWidth = 60;
    int tabLogWidth = 250;
    int tabButtonWidth = 40;
    int i = 0;
    jTable.getColumnModel().getColumn(i).setMaxWidth(tabDateWidth);
    jTable.getColumnModel().getColumn(i).setMinWidth(tabDateWidth);
    i++;
    jTable.getColumnModel().getColumn(i).setPreferredWidth(tabPreferredWidth);
    i++;
    if (hasColumn(flowStateLogs, LogTO.CALLER)) {
      jTable.getColumnModel().getColumn(i).setPreferredWidth(tabPreferredWidth);
      i++;
    }
    if (hasColumn(flowStateLogs, LogTO.METHOD)) {
      jTable.getColumnModel().getColumn(i).setPreferredWidth(tabPreferredWidth);
      i++;
    }
    jTable.getColumnModel().getColumn(i).setPreferredWidth(tabLogWidth);
    i++;
    jTable.getColumnModel().getColumn(i).setMaxWidth(tabButtonWidth);
    jTable.getColumnModel().getColumn(i).setMinWidth(tabButtonWidth);
    jTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);

    jTable.setDefaultRenderer(JButton.class, new JTableComponentRenderer(jTable.getDefaultRenderer(JButton.class)));
    jTable.setDefaultRenderer(JLabel.class, new JTableComponentRenderer(jTable.getDefaultRenderer(JLabel.class)));

    jTable.doLayout();
    jTable.addMouseListener(new JTableButtonMouseListener(jTable));
    jTable.repaint();
    return jTable;
  }
  
  private JLabel createTableLabel(String text, final LogTO contents) {
    return createTableLabel(text, text, contents);
  }
  
  private JLabel createTableLabel(String text, String toolTip, final LogTO contents) {
    JLabel label = new JLabel(text);
    label.setToolTipText(toolTip);
    label.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent evt) {
        if(evt.getClickCount() >= 2) {
          showLogContents(contents);
        }
      }
    });
    return label;
  }
  
  private JButton createTableButton(final LogTO contents) {
    JButton button = new JButton(images.get(PAGES.DETAILS));
    button.setToolTipText(Messages.getString("ViewStateLogDialog.detail.tooltip"));
    button.setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED, Color.lightGray, Color.darkGray));
    button.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent evt) {
        showLogContents(contents);
      }
    });
    return button;
  }

  private void showLogContents(LogTO contents) {
    String msg = contents.getLog();
    msg = msg.replace(";", ";" + System.getProperty("line.separator"));
    JOptionPane.showMessageDialog(this, msg);
  }

  private JComponent getNextPageButton() {
    if (nextButton == null) {
      nextButton = new JButton(images.get(PAGES.NEXT));
      nextButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          goToPage(PAGES.NEXT);
        }
      });
      nextButton.setBorderPainted(false);
      nextButton.addMouseListener(new ButtonChanger());
    }
    return nextButton;
  }

  private JComponent getPreviousPageButton() {
    if (previousButton == null) {
      previousButton = new JButton(images.get(PAGES.PREVIOUS));
      previousButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          goToPage(PAGES.PREVIOUS);
        }
      });
      previousButton.setBorderPainted(false);
      previousButton.addMouseListener(new ButtonChanger());
    }
    return previousButton;
  }

  private JComponent getFirstPageButton() {
    if (firstButton == null) {
      firstButton = new JButton(images.get(PAGES.FIRST));
      firstButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          goToPage(PAGES.FIRST);
        }
      });
      firstButton.setBorderPainted(false);
      firstButton.addMouseListener(new ButtonChanger());
    }
    return firstButton;
  }

  private JComponent getLastPageButton() {
    if (lastButton == null) {
      lastButton = new JButton(images.get(PAGES.LAST));
      lastButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          goToPage(PAGES.LAST);
        }
      });
      lastButton.setBorderPainted(false);
      lastButton.addMouseListener(new ButtonChanger());
    }
    return lastButton;
  }
}

class JTableComponentRenderer implements TableCellRenderer {
  private TableCellRenderer defaultRenderer;

  public JTableComponentRenderer(TableCellRenderer renderer) {
    defaultRenderer = renderer;
  }

  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
      int column) {
    if (value instanceof Component)
      return (Component) value;
    return defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
  }
}

class JTableButtonMouseListener implements MouseListener {
  private JTable table;
  Color c1 = Color.lightGray; // highlight
  Color c2 = Color.darkGray; // shadow
  Border over = new SoftBevelBorder(SoftBevelBorder.RAISED, c1, c2);
  Border press = new SoftBevelBorder(SoftBevelBorder.LOWERED, c1, c2);

  public JTableButtonMouseListener(JTable table) {
    this.table = table;
  }

  public void mouseClicked(MouseEvent e) {
    selectLabels();
    forwardEventToComponent(e, getComponent(e));
  }

  public void mouseEntered(MouseEvent e) {
    Component component = getComponent(e);
    if (component != null && component instanceof JButton && ((JButton) component).isEnabled()) {
      ((JButton) component).setBorder(over);
    }
    forwardEventToComponent(e, component);
  }

  public void mouseExited(MouseEvent e) {
    Component component = getComponent(e);
    if (component != null && component instanceof JButton && ((JButton) component).isEnabled()) {
      ((JButton) component).setBorder(over);
    }
    forwardEventToComponent(e, component);
  }

  public void mousePressed(MouseEvent e) {
    Component component = getComponent(e);
    if (component != null && component instanceof JButton && ((JButton) component).isEnabled()) {
      ((JButton) component).setBorder(press);
    }
    forwardEventToComponent(e, component);
  }

  public void mouseReleased(MouseEvent e) {
    Component component = getComponent(e);
    if (component != null && component instanceof JButton && ((JButton) component).isEnabled()) {
      ((JButton) component).setBorder(over);
    }
    forwardEventToComponent(e, component);
  }

  private final Color IS_NOT_SELECTED = new Color(0, 0, 0);
  private final Color IS_SELECTED = Color.BLUE;

  private void selectLabels() {
    int selectedRow = table.getSelectedRow();
    for (int row = 0; row < table.getRowCount(); row++) {
      for (int column = 0; column < table.getColumnCount(); column++) {
        Object value = table.getValueAt(row, column);
        if (value instanceof JLabel) {
          JLabel label = (JLabel) value;
          if (row == selectedRow) {
            label.setFont(new Font(label.getFont().getName(), Font.BOLD, label.getFont().getSize()));
            label.setBackground(IS_SELECTED);
          } else {
            label.setFont(new Font(label.getFont().getName(), Font.PLAIN, label.getFont().getSize()));
            label.setBackground(IS_NOT_SELECTED);
          }
          label.repaint();
        }
      }
    }
    table.repaint();
  }

  private Component getComponent(MouseEvent e) {
    TableColumnModel columnModel = table.getColumnModel();
    int column = columnModel.getColumnIndexAtX(e.getX());
    int row = e.getY() / table.getRowHeight();
    Object value;

    if (row >= table.getRowCount() || row < 0 || column >= table.getColumnCount() || column < 0) {
      return null;
    }

    value = table.getValueAt(row, column);

    if (!(value instanceof Component)) {
      return null;
    }

    return (Component) value;
  }

  private void forwardEventToComponent(MouseEvent e, Component component) {
    if (component == null) {
      return;
    }
    MouseEvent componentEvent = (MouseEvent) SwingUtilities.convertMouseEvent(table, e, component);
    component.dispatchEvent(componentEvent);
    table.repaint();
  }
}
