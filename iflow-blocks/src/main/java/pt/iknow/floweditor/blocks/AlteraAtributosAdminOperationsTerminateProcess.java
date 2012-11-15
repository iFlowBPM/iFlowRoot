package pt.iknow.floweditor.blocks;

import pt.iknow.floweditor.FlowEditorAdapter;

public class AlteraAtributosAdminOperationsTerminateProcess extends AlteraAtributosAdminOperations{
  private static final long serialVersionUID = 1L;

  public AlteraAtributosAdminOperationsTerminateProcess(FlowEditorAdapter adapter) {
    super(adapter, adapter.getString("AlteraAtributosAdminOperations.title"), true); //$NON-NLS-1$
  }
}