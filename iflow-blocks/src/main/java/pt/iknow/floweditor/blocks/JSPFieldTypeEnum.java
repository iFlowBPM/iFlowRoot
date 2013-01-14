package pt.iknow.floweditor.blocks;

/**
 * JSP Fields (Form) codes and constants
 * 
 * @author ombl
 *
 */
public enum JSPFieldTypeEnum {
  FIELD_TYPE_NONE(0, "JSPFieldData.choose", "JSPFieldData.choose", "JSPFieldData", "none"),
  FIELD_TYPE_HEADER(1, "JSPFieldData.fieldtype.header", "JSPFieldData.tootltip.header", "JSPHeaderData", "pt.iflow.blocks.form.Header"),
  FIELD_TYPE_TEXT(2, "JSPFieldData.fieldtype.textbox", "JSPFieldData.tootltip.textbox", "JSPTextData", "pt.iflow.blocks.form.TextBox"),
  FIELD_TYPE_PASSWORD(3, "JSPFieldData.fieldtype.passwordbox", "JSPFieldData.tootltip.passwordbox", "JSPPasswordData", "pt.iflow.blocks.form.Password"),
  FIELD_TYPE_TABLE(4, "JSPFieldData.fieldtype.table", "JSPFieldData.tootltip.table", "JSPTableData", "pt.iflow.blocks.form.ArrayTable", true),
  FIELD_TYPE_SPACER(5, "JSPFieldData.fieldtype.space", "JSPFieldData.tootltip.space", "JSPSpacerData", "pt.iflow.blocks.form.Filler"),
  FIELD_TYPE_SELECT(6, "JSPFieldData.fieldtype.selectionlist", "JSPFieldData.tootltip.selectionlist", "JSPSelectData", "pt.iflow.blocks.form.Selection", true),
  FIELD_TYPE_TEXT_LABEL(7, "JSPFieldData.fieldtype.textout", "JSPFieldData.tootltip.textout", "JSPTextLabelData", "pt.iflow.blocks.form.TextLabel"),
  FIELD_TYPE_TEXT_AREA(8, "JSPFieldData.fieldtype.textarea", "JSPFieldData.tootltip.textarea", "JSPTextAreaData", "pt.iflow.blocks.form.TextArea"),
  FIELD_TYPE_LINK(9, "JSPFieldData.fieldtype.link", "JSPFieldData.tootltip.link", "JSPLinkData", "pt.iflow.blocks.form.Link"),
  FIELD_TYPE_TXT_MSG(10, "JSPFieldData.fieldtype.textmessage", "JSPFieldData.tootltip.textmessage", "JSPTextMessageData", "pt.iflow.blocks.form.TextMessage"),
  FIELD_TYPE_SEPARATOR(11, "JSPFieldData.fieldtype.separator", "JSPFieldData.tootltip.separator", "JSPSeparatorData",  "pt.iflow.blocks.form.Separator"),
  FIELD_TYPE_DATECAL(12, "JSPFieldData.fieldtype.datebox", "JSPFieldData.tootltip.datebox", "JSPDateCalData",  "pt.iflow.blocks.form.DateCal"),
  FIELD_TYPE_SQL_SELECT(13, "JSPFieldData.fieldtype.selectionlist.sql", "JSPFieldData.tootltip.selectionlist.sql", "JSPSQLSelectData", "pt.iflow.blocks.form.SQLSelection"),
  FIELD_TYPE_CONNECTOR(14, "JSPFieldData.fieldtype.selectionlist.external", "JSPFieldData.tootltip.selectionlist.external", "JSPConnectorData", "pt.iflow.blocks.form.ConSelection"),
  FIELD_TYPE_BLOCK_DIVISION(15, "JSPFieldData.fieldtype.newblockstart", "JSPFieldData.tootltip.newblockstart", "JSPBlockDivisionData", "pt.iflow.blocks.form.BlockDivision"),
  FIELD_TYPE_COLUMN_DIVISION(16, "JSPFieldData.fieldtype.newcolumn", "JSPFieldData.tootltip.newcolumn", "JSPColumnDivisionData", "pt.iflow.blocks.form.ColumnDivision"),
  FIELD_TYPE_IMAGE(17, "JSPFieldData.fieldtype.image", "JSPFieldData.tootltip.image", "JSPImageData", "pt.iflow.blocks.form.Image"),
  FIELD_TYPE_FILE(18, "JSPFieldData.fieldtype.file", "JSPFieldData.tootltip.file", "JSPFileData", "pt.iflow.blocks.form.File"),
  FIELD_TYPE_FIXED_TABLE(19, "JSPFieldData.fieldtype.fixedsizetable", "JSPFieldData.tootltip.fixedsizetable", "JSPFixedTableData", "pt.iflow.blocks.form.FixedTable"),
  FIELD_TYPE_CHART(20, "JSPFieldData.fieldtype.graphic", "JSPFieldData.tootltip.graphic", "JSPChartData", "pt.iflow.blocks.form.Chart"),
  FIELD_TYPE_SUBHEADER(21, "JSPFieldData.fieldtype.subheader", "JSPFieldData.tootltip.subheader", "JSPSubHeaderData", "pt.iflow.blocks.form.SubHeader"),
  FIELD_TYPE_PROCESS_TABLE(22, "JSPFieldData.fieldtype.processtable", "JSPFieldData.tootltip.processtable", "JSPProcessTableData", "pt.iflow.blocks.form.ProcessTable", true),
  FIELD_TYPE_TAB_DIVISION(23, "JSPFieldData.fieldtype.tabcontainer.start", "JSPFieldData.tootltip.tabcontainer.start", "JSPTabDivision", "pt.iflow.blocks.form.TabDivision"),
  FIELD_TYPE_TABEND_DIVISION(24, "JSPFieldData.fieldtype.tabcontainer.end", "JSPFieldData.tootltip.tabcontainer.end", "JSPTabEndDivision", "pt.iflow.blocks.form.TabEndDivision"),
  FIELD_TYPE_TAB_BEGIN(25, "JSPFieldData.fieldtype.tab.start", "JSPFieldData.tootltip.tab.start", "JSPTabBegin", "pt.iflow.blocks.form.TabBegin"),
  FIELD_TYPE_TAB_END(26, "JSPFieldData.fieldtype.tab.end", "JSPFieldData.tootltip.tab.end", "JSPTabEnd", "pt.iflow.blocks.form.TabEnd"),
  FIELD_TYPE_SQL_LABEL(27, "JSPFieldData.fieldtype.sqllabel", "JSPFieldData.tootltip.sqllabel", "JSPSQLLabelData", "pt.iflow.blocks.form.SQLLabel"),
  FIELD_TYPE_SQL_MESSAGE(28, "JSPFieldData.fieldtype.sqlmessage", "JSPFieldData.tootltip.sqlmessage", "JSPSQLMessageData", "pt.iflow.blocks.form.SQLMessage"),
  FIELD_TYPE_EXPRESSION_LABEL(29, "JSPFieldData.fieldtype.expressionlabel", "JSPFieldData.tootltip.expressionlabel", "JSPExpressionLabelData", "pt.iflow.blocks.form.ExpressionLabel"),
  FIELD_TYPE_DMS_TABLE(30, "JSPFieldData.fieldtype.dmstable", "JSPFieldData.tootltip.dmstable", "JSPDMSTableData", "pt.iflow.blocks.form.DMSTable"),
  FIELD_TYPE_RICH_TEXT_AREA(31, "JSPFieldData.fieldtype.richtextarea", "JSPFieldData.tootltip.richtextarea", "JSPRichTextAreaData", "pt.iflow.blocks.form.RichTextArea"),
  FIELD_TYPE_BUTTON(32, "JSPFieldData.fieldtype.button", "JSPFieldData.tootltip.button", "JSPButtonData", "pt.iflow.blocks.form.Button"),
  FIELD_TYPE_SUB_FLOW_FORM_FIELD(33, "JSPFieldData.fieldtype.popupformfield", "JSPFieldData.tootltip.popupformfield", "JSPPopupFormFieldData", "pt.iflow.blocks.form.PopupFormField", true),
  
  FIELD_TYPE_MENU_DIVISION(34, "JSPFieldData.fieldtype.menu.container.start", "JSPFieldData.fieldtype.menu.container.start", "JSPMenuDivision", "pt.iflow.blocks.form.MenuDivision"),
  FIELD_TYPE_MENU(35, "JSPFieldData.fieldtype.menu.start", "JSPFieldData.fieldtype.menu.start", "JSPMenuEntry", "pt.iflow.blocks.form.MenuEntry"),
  FIELD_TYPE_MENUEND_DIVISION(36, "JSPFieldData.fieldtype.menu.container.end", "JSPFieldData.fieldtype.menu.container.end", "JSPMenuEndDivision", "pt.iflow.blocks.form.MenuEndDivision"),
  FIELD_TYPE_FORM_TEMPLATE(
      37, "JSPFieldData.fieldtype.formtemplate", "JSPFieldData.tootltip.formtemplate", "JSPFormTemplate",
      "pt.iflow.blocks.form.FormTemplate"),
  ;

  private final int code;
  private final String descrKey;
  private final String tooltipKey;
  private final String editorClass;
  private final String engineClass;
  private final boolean extraButtons;

  private JSPFieldTypeEnum(int code, String descrKey, String tooltipKey, String editorClass, String engineClass) {
    this(code, descrKey, tooltipKey, editorClass, engineClass, false);
  }

  private JSPFieldTypeEnum(int code, String descrKey, String tooltipKey, String editorClass, String engineClass, boolean extraButtons) {
    this.code = code;
    this.descrKey = descrKey;
    this.tooltipKey = tooltipKey;
    this.editorClass = editorClass;
    this.engineClass = engineClass;
    this.extraButtons = extraButtons;
  }

  public int getCode() {
    return code;
  }
  public String getDescrKey() {
    return descrKey;
  }
  public String getTooltipKey() {
    return tooltipKey;
  }
  public String getEditorClass() {
    return editorClass;
  }
  public String getEngineClass() {
    return engineClass;
  }

  public boolean isExtraButtons() {
    return extraButtons;
  }

}
