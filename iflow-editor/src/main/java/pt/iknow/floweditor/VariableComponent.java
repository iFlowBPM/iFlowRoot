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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;

import javax.accessibility.Accessible;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.lang.StringUtils;


// Componente para modificar valores de uma variavel de catalogo
public class VariableComponent extends JPanel implements Accessible {

  /**
   * 
   */
  private static final long serialVersionUID = -6509880499820528367L;

  public static final int CANCEL_OPTION = 1;

  public static final int APPROVE_OPTION = 0;

  private Atributo variable;
  private int returnValue = CANCEL_OPTION;

  public VariableComponent() {
    this(new AtributoImpl());
  }

  public VariableComponent(Atributo variable) {
    setVariable(variable);
    init();
  }

  public void setVariable(Atributo variable) {
    if(null == variable) throw new IllegalArgumentException("Variable cant be null");
    this.variable = variable;
  }

  public Atributo getVariable() {
    return this.variable;
  }

  public int showDialog(Component parent) throws HeadlessException {
    String title = "Alterar catalogo"; // TODO externalizar

    String ok = "Ok";
    String cancel = "Cancel";

    updateComponent();

    returnValue = CANCEL_OPTION;

    int result = JOptionPane.showOptionDialog(parent, this, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{ok, cancel}, ok);
    if(result == JOptionPane.OK_OPTION) {
      returnValue = APPROVE_OPTION;
      updateVariable();
    }

    return returnValue;
  }


  private JCheckBox isArrayCheckBox;
  private JComboBox dataTypeComboBox;
  private JTextField nameText;
  private JTextField publicNameText;
  private JTextField defaultValueText;

  private Map<String,DataTypeObj> typeMap = new HashMap<String,DataTypeObj>();
  private void init() {
    GridBagConstraints gridBagConstraints;

    JLabel nameLabel = new JLabel();
    nameText = new JTextField();
    JLabel publicNameLabel = new JLabel();
    publicNameText = new JTextField();
    JLabel dataTypeLabel = new JLabel();
    isArrayCheckBox = new JCheckBox();
    JLabel isArrayLabel = new JLabel();
    dataTypeComboBox = new JComboBox();
    JLabel defaultValueLabel = new JLabel();
    defaultValueText = new JTextField();

    setLayout(new GridBagLayout());

    nameLabel.setText("Variável");
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.anchor = GridBagConstraints.EAST;
    gridBagConstraints.insets = new Insets(1, 4, 1, 4);
    add(nameLabel, gridBagConstraints);

    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
    gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new Insets(1, 2, 1, 2);
    add(nameText, gridBagConstraints);

    publicNameLabel.setText("Nome público");
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = GridBagConstraints.EAST;
    gridBagConstraints.insets = new Insets(1, 4, 1, 4);
    add(publicNameLabel, gridBagConstraints);

    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
    gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new Insets(1, 2, 1, 2);
    add(publicNameText, gridBagConstraints);

    dataTypeLabel.setText("Tipo de Dados");
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = GridBagConstraints.EAST;
    gridBagConstraints.insets = new Insets(1, 4, 1, 4);
    add(dataTypeLabel, gridBagConstraints);

    // TODO sacar dataypes a partir do iFlow
    DataTypeObj [] values = new DataTypeObj[]{
        new DataTypeObj("text", "Texto"),  
        new DataTypeObj("int", "Inteiro"),  
        new DataTypeObj("float", "Decimal"),  
        new DataTypeObj("date", "Data"),  
        new DataTypeObj("custom", "Outro"),  
    };
    
    for(DataTypeObj obj : values)
      typeMap.put(obj.code, obj);
    
    dataTypeComboBox.setModel(new DefaultComboBoxModel(values));
    dataTypeComboBox.setSelectedIndex(0);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
    gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new Insets(1, 2, 1, 2);
    add(dataTypeComboBox, gridBagConstraints);

    isArrayLabel.setText("Array");
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.anchor = GridBagConstraints.EAST;
    gridBagConstraints.insets = new Insets(1, 4, 1, 4);
    add(isArrayLabel, gridBagConstraints);

    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
    gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new Insets(1, 2, 1, 2);
    add(isArrayCheckBox, gridBagConstraints);

    defaultValueLabel.setText("Valor inicial");
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.anchor = GridBagConstraints.EAST;
    gridBagConstraints.insets = new Insets(1, 4, 1, 4);
    add(defaultValueLabel, gridBagConstraints);

    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
    gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new Insets(1, 2, 1, 2);
    add(defaultValueText, gridBagConstraints);

    
    setPreferredSize(new Dimension(320,150));
  }

  private void updateComponent() {
    nameText.setText(variable.getNome());
    defaultValueText.setText(variable.getInitValue());
    publicNameText.setText(variable.getPublicName());
    
    String type = variable.getDataType();
    if(StringUtils.isBlank(type)) type = "Stext";
    boolean isArray = type.charAt(0)=='A';
    type = type.substring(1);
    
    dataTypeComboBox.setSelectedItem(typeMap.get(type));
    isArrayCheckBox.setSelected(isArray);
  }

  private void updateVariable() {
    variable.setNome(nameText.getText());
    variable.setInitValue(defaultValueText.getText());
    variable.setPublicName(publicNameText.getText());
    String isArray = isArrayCheckBox.isSelected()?"A":"S";
    
    DataTypeObj obj = (DataTypeObj)dataTypeComboBox.getSelectedItem();
    if(obj == null) obj = typeMap.get("text");
    
    variable.setDataType(isArray+obj.code);
  }


  public static void main(String [] args) {
    Atributo a = new AtributoImpl();
    a.setNome("zeze");
    a.setInitValue("123");
    a.setPublicName("XPtOO");
    VariableComponent vd = new VariableComponent(a);
    System.out.println(vd.showDialog(new JFrame()));
    System.out.println(vd.getVariable());
  }
  
  
  private static class DataTypeObj {
    String description;
    String code;
    
    DataTypeObj(String code, String description) {
      this.code = code;
      this.description = description;
    }
    
    public boolean equals(Object obj) {
      if(null == obj) return false;
      if(obj instanceof DataTypeObj) return code.equals(((DataTypeObj)obj).code);
      return false;
    }
    
    public String toString() {
      return this.description;
    }
  }
}
