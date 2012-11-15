package pt.iknow.floweditor.blocks;

import pt.iknow.floweditor.FlowEditorAdapter;


public class FieldDataException extends Exception {
  private static final long serialVersionUID = -7612896638221609570L;

  private StringBuffer _sbErrMsg;

  public FieldDataException(FlowEditorAdapter adapter, Integer aiPropID, boolean abIsPropRequired) {
    this(adapter, null, aiPropID, abIsPropRequired);
  }

  public FieldDataException(FlowEditorAdapter adapter, Integer aiRow, Integer aiPropID, boolean abIsPropRequired) {
    final String _sLinePrefix = adapter.getString("FieldDataException.line"); //$NON-NLS-1$
    final String _sLinePrefixJunction = " - "; //$NON-NLS-1$
    final String _sPrefix = adapter.getString("FieldDataException.the.field"); //$NON-NLS-1$
    final String _sReqSuffix = adapter.getString("FieldDataException.obligatory.field"); //$NON-NLS-1$
    final String _sBadValSuffix = adapter.getString("FieldDataException.invalid"); //$NON-NLS-1$
    this._sbErrMsg = new StringBuffer();
    if (aiRow != null) {
      int nRow = aiRow.intValue() + 1;
      this._sbErrMsg.append(_sLinePrefix);
      this._sbErrMsg.append(nRow);
      this._sbErrMsg.append(_sLinePrefixJunction);
    }

    this._sbErrMsg.append(_sPrefix);

    this._sbErrMsg.append(JSPFieldData.getPropLabel(aiPropID));
    if (abIsPropRequired) {
      this._sbErrMsg.append(_sReqSuffix);
    } else {
      this._sbErrMsg.append(_sBadValSuffix);
    }
  }

  public FieldDataException(FlowEditorAdapter adapter, int anPropID, boolean abIsPropRequired) {
    this(adapter, new Integer(anPropID), abIsPropRequired);
  }

  public FieldDataException(FlowEditorAdapter adapter, int anRow, int anPropID, boolean abIsPropRequired) {
    this(adapter, new Integer(anRow), new Integer(anPropID), abIsPropRequired);
  }

  public String getMessage() {
    return _sbErrMsg.toString();
  }

  public void append(FieldDataException afde) {
    this._sbErrMsg.append(afde.getMessage());
  }

} // protected class FieldDataException

