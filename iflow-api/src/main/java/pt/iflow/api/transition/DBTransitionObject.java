package pt.iflow.api.transition;

interface DBTransitionObject {
  
  /**
   * Retrieves a string representation of the value of given column. Appropriate
   * fields (eg.: String, Timestamp) are encapsulated in SQL format (eg.: TEXT
   * is returned as 'TEXT').
   * 
   * @param columnName
   *          Column name to retrieve the value from, should be one of static
   *          <i>TABLE_COLUMNS</i>.
   * @return String representation of value found.
   */
  public String getValueOf(String columnName);
}
