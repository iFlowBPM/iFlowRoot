package pt.iflow.api.blocks;

public enum FormService {
  NONE(0),
  PRINT(1),
  EXPORT(2);

  private int code;
  
  private FormService(int code) {
    this.code = code;    
  }
  
  public int getCode() {
    return code;
  }
}
