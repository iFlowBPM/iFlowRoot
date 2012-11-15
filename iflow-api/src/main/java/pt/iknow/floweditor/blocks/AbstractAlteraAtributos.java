package pt.iknow.floweditor.blocks;

import javax.swing.JDialog;

import pt.iknow.floweditor.FlowEditorAdapter;

public abstract class AbstractAlteraAtributos extends JDialog implements AlteraAtributosInterface {
  private static final long serialVersionUID = -9153864796786247370L;
  
  protected final transient FlowEditorAdapter adapter;
  /* OK CANCEL */
  protected final String Cancelar;
  protected final String OK;

  public AbstractAlteraAtributos(FlowEditorAdapter adapter) {
    super(adapter.getParentFrame());
    this.adapter = adapter;
    /* OK CANCEL */
    Cancelar = adapter.getBlockMessages().getString("Common.cancel"); //$NON-NLS-1$
    OK = adapter.getBlockMessages().getString("Common.ok"); //$NON-NLS-1$
  }
  
  public AbstractAlteraAtributos(FlowEditorAdapter adapter, String title) {
    this(adapter);
    setTitle(title);
  }
  
  public AbstractAlteraAtributos(FlowEditorAdapter adapter, boolean modal) {
    this(adapter);
    setModal(modal);
  }
  
  public AbstractAlteraAtributos(FlowEditorAdapter adapter, String title, boolean modal) {
    this(adapter);
    setTitle(title);
    setModal(modal);
  }
  
  public FlowEditorAdapter getAdapter() {
    return adapter;
  }


  public static String asString(Object obj) {
    if(null == obj) return null;
    return String.valueOf(obj);
  }
  
  public static String [] asString(Object [] obj) {
    if(null == obj) return null;
    String [] res = new String[obj.length];
    for(int i = 0; i < res.length; i++)
      res[i] = asString(obj[i]);
    return res;
  }
  
  public static String [][] asString(Object [][] obj) {
    if(null == obj) return null;
    String [][] res = new String[obj.length][];
    for(int i = 0; i < res.length; i++)
      res[i] = asString(obj[i]);
    
    return res;
  }
  
}
