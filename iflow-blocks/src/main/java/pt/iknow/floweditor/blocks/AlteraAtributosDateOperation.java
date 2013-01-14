package pt.iknow.floweditor.blocks;

/*****************************************************
 *
 *  Project FLOW EDITOR
 *
 *  class: AlteraAtributos
 *
 *  desc: dialogo para alterar atributos de um bloco
 *
 ****************************************************/

import java.util.List;

import pt.iknow.floweditor.Atributo;
import pt.iknow.floweditor.FlowEditorAdapter;
import pt.iknow.utils.swing.ComboCellEditor;
import pt.iknow.utils.swing.MyColumnEditorModel;

public class AlteraAtributosDateOperation extends AlteraAtributos {
  
  private static final long serialVersionUID = 2203057221939898229L;

  protected static final String OPERATION = "operation";
  protected static final String TYPE = "type";

  private static String[] operations = new String[]  {
    "Add",
    "Remove"
  };
  
  private static String[] types = new String[]  {
    "Days",
    "Months",
    "Years"
  };
  
  public AlteraAtributosDateOperation(FlowEditorAdapter adapter) {
    super(adapter);
  }

  void customizeTable(List<Atributo> atributos) {
    super.customizeTable(atributos);
    // update editor
    MyColumnEditorModel cm = jTable1.getMyColumnEditorModel();
   
    int combos = 0;
    for (int x = 0; x < atributos.size(); x++) {
      Atributo at = atributos.get(x);
      if (OPERATION.equals(at.getNome())) {
        cm.addEditorForCell(x, 1, new ComboCellEditor(operations, false));
        combos++;
      }
      else if (TYPE.equals(at.getNome())) {
        cm.addEditorForCell(x, 1, new ComboCellEditor(types, false));
        combos++;
      } 

      if (combos == 2)
        break;
    }
  }
}
