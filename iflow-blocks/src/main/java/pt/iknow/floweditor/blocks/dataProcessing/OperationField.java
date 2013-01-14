package pt.iknow.floweditor.blocks.dataProcessing;

import java.io.Serializable;

public class OperationField implements Serializable {
  private static final long serialVersionUID = 1823292921106032981L;
  
  public static final int TYPE_EXPRESSION = 1;
  public static final int TYPE_ANY = 2;
  public static final int TYPE_SCALAR = 3;
  public static final int TYPE_ARRAY = 4;

  private String desc;
  private int type;
  private String name;

  protected OperationField(String name, String desc) {
    this(TYPE_EXPRESSION, name, desc);
  }

  protected OperationField(int type, String name, String desc) {
    this.desc = desc;
    this.type = type;
    this.name = name;
  }

  public String getDesc() {
    return desc;
  }

  public int getType() {
    return type;
  }

  public String getName() {
    return name;
  }
}
