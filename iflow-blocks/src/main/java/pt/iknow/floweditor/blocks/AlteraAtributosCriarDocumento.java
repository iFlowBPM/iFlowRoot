package pt.iknow.floweditor.blocks;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import pt.iknow.floweditor.Atributo;
import pt.iknow.floweditor.FlowEditorAdapter;
import pt.iknow.utils.swing.ComboCellEditor;
import pt.iknow.utils.swing.MyColumnEditorModel;

public class AlteraAtributosCriarDocumento extends AlteraAtributos implements AlteraAtributosInterface {

  /** Generated serial version UID. */
  private static final long serialVersionUID = 7651187918085954486L;

  protected static final String TEMPLATE = "template";
  protected static final String VARIABLE = "variable";
  protected static final String FILENAME = "filename";
  
  public AlteraAtributosCriarDocumento(FlowEditorAdapter adapter) {
    super(adapter);
  }

  void customizeTable(List<Atributo> atributos) {
    super.customizeTable(atributos);
    MyColumnEditorModel cm = jTable1.getMyColumnEditorModel();
    String [] dataSources = adapter.getRepository().listPrintTemplates();
    
    for (int x = 0; x < atributos.size(); x++) {
      Atributo at = atributos.get(x);
      if (StringUtils.equals(TEMPLATE, at.getNome())) {
        cm.addEditorForCell(x, 1, new ComboCellEditor(dataSources, true));
        break;
      }
    }
  }
}
