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
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import pt.iflow.api.blocks.form.Field;
import pt.iflow.api.blocks.form.Form;
import pt.iflow.api.blocks.form.FormProperties;
import pt.iflow.api.blocks.form.Tab;
import pt.iknow.floweditor.blocks.AbstractAlteraAtributos;
import pt.iknow.floweditor.blocks.AlteraAtributosInterface;

import com.twolattes.json.Marshaller;

/*******************************************************************************
 * 
 * Project FLOW EDITOR
 * 
 * class: TemplateManager
 * 
 * desc: dialogo para gerir templates de formulários
 * 
 ******************************************************************************/

public class TemplateManager extends JDialog {

  private static final long serialVersionUID = -321L;
  
  private JButton addButton;
  private JButton remButton;
  private JButton editButton;
  private JList templatesList;
  private Desenho desenho;
  private Janela janela;
  private Class<AbstractAlteraAtributos> webFormDialog = null;

  /** Creates new form EncontraComponente */
  public TemplateManager(Janela janela, Desenho desenho) {
    super(janela, false);
    this.janela = janela;
    this.desenho = desenho;
    try {
      webFormDialog = janela.loadGUIClass(Janela.WEB_FORM_CLASS);
    } catch (ClassNotFoundException e) {
      FlowEditor.log("Could not load block Web Form GUI class. Is there something wrong with your config?", e);
    }
    initComponents();
    setTitle("Gestão de Templates");
  }

//  public static InstanciaComponente open(Janela janela, BlockSearchInterface filter) {
//    BlockFinder fc = new BlockFinder(janela, filter);
//    fc.setVisible(true);
//
//    return fc.componente;
//  }

  private void initComponents() {
    JScrollPane jScrollPane1 = new JScrollPane();
    templatesList = new JList();
    editButton = new JButton();
    addButton = new JButton();
    remButton = new JButton();

    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent evt) {
        cancelButtonActionPerformed();
      }
    });

    jScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    jScrollPane1.setMaximumSize(new Dimension(250, 220));
    jScrollPane1.setMinimumSize(new Dimension(250, 220));
    jScrollPane1.setPreferredSize(new Dimension(250, 220));
    templatesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    templatesList.addListSelectionListener(new ListSelectionListener () {
      public void valueChanged(ListSelectionEvent e) {
        editButton.setEnabled(templatesList.getSelectedIndex()!=-1);
        remButton.setEnabled(templatesList.getSelectedIndex()!=-1);
      }
    });
    templatesList.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent evt) {
        listClicked(evt.getClickCount());
      }
    });
    jScrollPane1.setViewportView(templatesList);
    jScrollPane1.setBorder(BorderFactory.createTitledBorder("Templates"));

    getContentPane().add(jScrollPane1, BorderLayout.CENTER);
    
    JPanel panel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx=0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(3,1,2,1);
    panel.add(new JPanel(),gbc);
    addButton.setText("Nova");
    addButton.setToolTipText("Adiciona uma nova template");
    addButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        String s = (String)JOptionPane.showInputDialog(
            getParent(),
            "Indique o nome da nova template",
            "Nova template",
            JOptionPane.PLAIN_MESSAGE,
            null,
            null,
            "");
        if(s != null) {
          // create an empty template
          // Select and save...
          FlowEditor.log("Nova template: "+s);
          if(desenho.getFormTemplates().containsKey(s)) {
            JOptionPane.showMessageDialog(getParent(), "Já existe uma template com esse nome", "Nome invalido", JOptionPane.ERROR_MESSAGE);
            return;
          }
          modifyTemplate(s, true);
        }
        
        loadListData();
      }
    });
    panel.add(addButton, gbc);

    editButton.setText("Editar");
    editButton.setToolTipText("Altera a template seleccionada");
    editButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        String template = (String) templatesList.getSelectedValue();
        if(null == template) return;
        modifyTemplate(template, false);
        loadListData();
      }
    });
    panel.add(editButton, gbc);


    remButton.setText("Remover");
    remButton.setToolTipText("Remove a template seleccionada");
    remButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        String template = (String) templatesList.getSelectedValue();
        if(null == template) return;
        int ok = JOptionPane.showConfirmDialog(getParent(), "Tem a certeza de que quer remover a tempalte "+template+"?", "Atenção!", JOptionPane.YES_NO_OPTION);
        if(ok == JOptionPane.OK_OPTION) {
          desenho.setFormTemplate(template, null);
          desenho.setFlowChanged(true);
        }
        loadListData();
      }
    });
    panel.add(remButton, gbc);
    // provavelmente ha uma maneira mais elegante de fazer isto, mas assim eh mais facil
    gbc = (GridBagConstraints) gbc.clone();
    gbc.weighty=1.0;
    panel.add(new JPanel(), gbc);
    getContentPane().add(panel, BorderLayout.EAST);

    panel = new JPanel();
    JButton closeButton = new JButton("Fechar");
    closeButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        cancelButtonActionPerformed();
      }
    });
    panel.add(closeButton);
    getContentPane().add(panel, BorderLayout.SOUTH);
    loadListData();
    pack();
  }

  /*****************************************************************************
   * EXIT
   */
  private void cancelButtonActionPerformed() {
    closeWindow();
  }

  /*****************************************************************************
   * selecciona um componente
   */
  private void listClicked(int clicks) {
    // Add your handling code here:
    if (clicks >= 2) {
      modifyTemplate((String)templatesList.getSelectedValue(), false);
    }
  }

  private void closeWindow() {
    setVisible(false);
    dispose();
  }
  
  
  private String generateNewTemplate(String name) {
    Form form = new Form();
    FormProperties formProps = new FormProperties();
    formProps.setAutosubmit("false");
    formProps.setDescription("Template "+name);
    formProps.setTemplate("true");
    formProps.setResult("");
    formProps.setStylesheet("default.xsl");
    form.setProperties(formProps);
    List<Tab> tabs = new ArrayList<Tab>(1);
    Tab tab = new Tab();
    tab.setText(name);
    tab.setProperties(new HashMap<String, String>());
    tab.setFields(new ArrayList<Field>(0));
    tabs.add(tab);
    form.setTabs(tabs);
    
    return Marshaller.create(Form.class).marshall(form).toString();
  }
  
  /*****************************************************************************
   * cria lista de componentes encontrados
   */
  private void loadListData() {
    // search...
    String [] templates = new String[desenho.getFormTemplates().keySet().size()];
    
    templates = desenho.getFormTemplates().keySet().toArray(templates);
    final Locale locale = Locale.getDefault();
    final Collator collator = Collator.getInstance(locale);

    FlowEditor.log("Sorting templates using locale "+locale);
    Arrays.sort(templates, collator);
    templatesList.setListData(templates);
  }
  
  private void modifyTemplate(String template, boolean create) {
    if(null == template) return;
    try {
      String formTemplate = desenho.getFormTemplate(template);
      if(create && null != formTemplate) return;
      if(!create && null == formTemplate) return;
      
      AlteraAtributosInterface dialog = webFormDialog.getConstructor(FlowEditorAdapter.class).newInstance(new FlowEditorAdapterImpl(janela, desenho, null));
      List<Atributo> atributos = new ArrayList<Atributo>(1);
      if(create)
        formTemplate = generateNewTemplate(template);
      atributos.add(new AtributoImpl("FORM_0",formTemplate));
      dialog.setDataIn("Template: "+template, atributos);
      
      if (dialog.getExitStatus() == AlteraAtributosInterface.EXIT_STATUS_OK) {
        Object[][] newAttr = dialog.getNewAttributes();
        StringBuilder formStr = new StringBuilder();
        for(int i = 0; i < newAttr.length; i++) formStr.append(newAttr[i][1]);
        desenho.setFormTemplate(template, formStr.toString());
        // Notificar desenho de alteracao do estado.
        desenho.setFlowChanged(true);
      }

    } catch (Exception e) {
      FlowEditor.log("Error loading webFormDialog.", e);
    }

  }
}
