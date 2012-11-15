package pt.iflow.blocks.webform;


public enum WebWidgetEnum {
  w_header(PlainMessage.class),
  w_subheader(PlainMessage.class),
  w_message(PlainMessage.class),
  w_spacer(Separators.Filler.class),
  w_separator(Separators.HorizontalRule.class),
  w_link(Link.class),
  w_image(Image.class),
  w_chart(Chart.class),
  w_table(Table.class),
  w_bcontainer(ButtonContainer.class),
  w_button(Button.class),
  process_data(ProcessVariable.class),
  w_template(Template.class)
  ;

  private final Class<? extends AbstractWidget> widgetClass;
  private WebWidgetEnum(final Class<? extends AbstractWidget> widgetClass) {
    this.widgetClass = widgetClass;
  }

  public Class<? extends AbstractWidget> getWidgetClass() {
    return this.widgetClass;
  }
}
