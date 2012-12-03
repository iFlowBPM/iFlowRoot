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
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.security.Provider;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pt.iflow.applet.cipher.FileCipher;
import pt.iflow.applet.signer.FileSigner;

import com.toedter.calendar.JDateChooser;

public class DynamicPanel extends JPanel {
  private static final long serialVersionUID = 1790147255606940760L;

  private static Log log = LogFactory.getLog(DynamicPanel.class);
  
  public static final String FORM_VALID = "FORM_IS_VALID"; //$NON-NLS-1$
  
  private final FileSigner signer;
  private final FileCipher cipher;
  private boolean guiRequired;
  private boolean formIsValid;

  public DynamicPanel(FileSigner signer, FileCipher cipher) {
    this.signer = signer;
    this.cipher = cipher;
    this.guiRequired = false;
    this.formIsValid = false;
    createFormPanel();
  }

  public FileSigner getFileSigner() {
    return signer;
  }

  public FileCipher getFileCipher() {
    return cipher;
  }

  private Container createFormPanel() {
    GridBagConstraints gbc = null;
    
    Container panel = this;
    panel.setLayout(new GridBagLayout());
    // Prepare signer form
    Component signerPanel = createPanel(signer.getForm());
    Component cipherPanel = createPanel(cipher.getForm());

    if(null != signerPanel) {
      gbc = new GridBagConstraints();
      gbc.gridx = 0;
      gbc.gridy = 1;
      gbc.weightx = 1.0;
      gbc.weighty = 1.0;
      gbc.fill = GridBagConstraints.BOTH;
      panel.add(signerPanel, gbc);
    }

    if(null != cipherPanel) {
      gbc = new GridBagConstraints();
      gbc.gridx = 0;
      gbc.gridy = 2;
      gbc.weightx = 1.0;
      gbc.weighty = 1.0;
      gbc.fill = GridBagConstraints.BOTH;
      panel.add(cipherPanel, gbc);
    }
    
    return panel;
  }
  
  public boolean isGuiRequired() {
    return this.guiRequired;
  }

  private Container createPanel(DynamicForm form) {
    // no form required
    if(null == form || form.isEmpty()) return null;
    this.guiRequired = true;

    ValidateFormListener actionValidator = new ValidateFormListener();

    Container panel = SwingUtils.newPanel();
    panel.setLayout(new GridBagLayout());

    int pos = 0;
    for(DynamicField field : form.getFields()) {
      // ainda não tenho nada alinhavado para isto....
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.gridx = 0;
      gbc.gridy = pos;
      gbc.insets = new Insets(2,2,2,2);
      gbc.anchor = GridBagConstraints.LINE_END;
      Component lbl = SwingUtils.newLabel(field.getLabel());
      panel.add(lbl, gbc);

      Component component = null;
      switch(field.getType()) {
      case SIGNATURE_CERTIFICATE:
        component = getCertificateComponent(field, actionValidator);
        break;
      case CHECKBOX:
        component = getCheckboxComponent(field, actionValidator);
        break;
      case DATE:
        component = getDateComponent(field, actionValidator);
        break;
      case TEXTFIELD:
      default:
        component = getTextComponent(field, actionValidator);
        break;
      }
      
      gbc = new GridBagConstraints();
      gbc.gridx = 1;
      gbc.gridy = pos;
      gbc.insets = new Insets(2,2,2,2);
      gbc.fill = GridBagConstraints.HORIZONTAL;
      gbc.weightx = 1.0;
      
      panel.add(component, gbc);
      
      // next position
      pos++;
    }

    SwingUtils.setBorder(panel, form.getTitle());
    return panel;
  }
  
  private JComponent getTextComponent(DynamicField field, DocumentListener textValidator) {
    JTextField jtf = new JTextField();
    MultiDocumentListener multiDocumentListener = new MultiDocumentListener();
    multiDocumentListener.addDocumentListener(new DynamicTextListener(jtf, field));
    multiDocumentListener.addDocumentListener(textValidator);
    jtf.getDocument().addDocumentListener(multiDocumentListener);

    return jtf;
  }
  
  private Component getCertificateComponent(DynamicField field, ActionListener okButtonValidator) {
    Container aux = SwingUtils.newPanel();
    aux.setLayout(new BorderLayout());
    
    JComboBox jcbAlias = new JComboBox();
    jcbAlias.setRenderer(new MyComboBoxRenderer());
    jcbAlias.setEditable(false);
    jcbAlias.setEnabled(false);
    
    MultiActionListener multiActionListener = new MultiActionListener();
    multiActionListener.addListener(new DynamicComboListener(jcbAlias, field));
    multiActionListener.addListener(okButtonValidator);
    jcbAlias.addActionListener(multiActionListener);
    
    aux.add(jcbAlias, BorderLayout.CENTER);
    
    ActionListener action = new ReloadCertificatesAction(jcbAlias);
    Component jcbReload = SwingUtils.newButton("...", action); //$NON-NLS-1$
    aux.add(jcbReload, BorderLayout.EAST);
    
    action.actionPerformed(null);
    return aux;
  }
  
  private JComponent getCheckboxComponent(DynamicField field, ActionListener okButtonValidator) {
    JCheckBox jcb = new JCheckBox();
    Boolean val = (Boolean)field.getValue();
    jcb.setSelected(null == val?false:val);
    MultiActionListener multiActionListener = new MultiActionListener();
    multiActionListener.addListener(new DynamicAbstractButtonListener(jcb, field));
    multiActionListener.addListener(okButtonValidator);
    jcb.addActionListener(multiActionListener);
    return jcb;
  }
  
  private JComponent getDateComponent(DynamicField field, PropertyChangeListener okButtonValidator) {
    JDateChooser dateChooser = new JDateChooser("dd/MM/yyyy", "##/##/####", ' '); //$NON-NLS-1$ //$NON-NLS-2$
    MultiPropertyChangeListerner multiListener = new MultiPropertyChangeListerner();
    multiListener.addPropertyChangeListener(new DynamicDateChangeListener(dateChooser, field));
    multiListener.addPropertyChangeListener(okButtonValidator);
    dateChooser.addPropertyChangeListener("date", multiListener); //$NON-NLS-1$
    return dateChooser;
  }
  
  private static class MyComboBoxRenderer extends BasicComboBoxRenderer {
    private static final long serialVersionUID = -7278868658182801547L;

    public Component getListCellRendererComponent(JList list, Object value,
        int index, boolean isSelected, boolean cellHasFocus) {
      Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      if (isSelected) {
        if (-1 < index && value != null) {
          IDEntry entry = (IDEntry) value;
          Provider prov = entry.getProvider();
          String tooltip = Messages.getString("DynamicPanel.2"); //$NON-NLS-1$
          if(null != prov)
            tooltip = prov.getName().replace("SunPKCS11-", ""); //$NON-NLS-1$ //$NON-NLS-2$
          list.setToolTipText(tooltip);
        }
      }
      return c;
    }
  }

  private void validateForm() {
    boolean formWasValid = formIsValid;
    String signerValidation = signer.validateForm();
    String cipherValidation = cipher.validateForm();
    
    log.debug("Signer Validadion: '" + signerValidation + "'"); //$NON-NLS-1$ //$NON-NLS-2$
    log.debug("Cipher Validadion: '" + cipherValidation + "'"); //$NON-NLS-1$ //$NON-NLS-2$
    
    formIsValid = (signerValidation == null && cipherValidation == null);
    firePropertyChange(FORM_VALID, formWasValid, formIsValid);
  }
  
  private class ValidateFormListener implements ActionListener, DocumentListener, PropertyChangeListener {
    public void actionPerformed(ActionEvent e) {
      validateForm();
    }
    public void changedUpdate(DocumentEvent e) {
      validateForm();
    }
    public void insertUpdate(DocumentEvent e) {
      validateForm();
    }
    public void removeUpdate(DocumentEvent e) {
      validateForm();
    }
    public void propertyChange(PropertyChangeEvent evt) {
      validateForm();
    }
  }

}
