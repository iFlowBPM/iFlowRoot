package pt.iflow.blocks.webform;


public enum ProcessVariableInputTypeEnum {
  text(TextInput.class),
  password(TextInput.class),
  textarea(TextInput.class),
  select(SelectInput.class),
  date(DateInput.class),
  checkbox(CheckboxInput.class),
  label(TextOutput.class),
  ;

  private final Class<? extends AbstractInput> widgetClass;
  private ProcessVariableInputTypeEnum(final Class<? extends AbstractInput> widgetClass) {
    this.widgetClass = widgetClass;
  }

  public Class<? extends AbstractInput> getWidgetClass() {
    return this.widgetClass;
  }
}
