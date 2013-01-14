package pt.iknow.floweditor.blocks.dataProcessing;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import pt.iknow.floweditor.FlowEditorAdapter;

public class OpNone extends Operation {

  public OpNone(FlowEditorAdapter adapter) {
    super(adapter);
  }
  
  public JPanel getPanel() {
    return new JPanel();
  }

  public boolean isIgnorable() {
    return true;
  }
  
  public String toString() {
    return "Please Choose";
  }

  public String getCode() {
    return "none";
  }

  public List<OperationField> getFields() {
    return new ArrayList<OperationField>();
  }

}
