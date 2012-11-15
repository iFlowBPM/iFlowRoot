package pt.iflow.api.userdata;

public interface IMappedData {

  /**
   * Get a field value
   * 
   * @param fieldName
   * @return
   */
  public abstract String get(String fieldName);
}
