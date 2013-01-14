package bsh;

public class BshVariable extends Variable {
  public static final int V_DECLARATION=DECLARATION, V_ASSIGNMENT=ASSIGNMENT;

  /**
   * 
   */
  private static final long serialVersionUID = 2732643176495512454L;

  public BshVariable(String name, Class<?> type, LHS lhs) {
    super(name, type, lhs);
  }

  public BshVariable(String name, Class<?> type, Object value, Modifiers modifiers) throws UtilEvalError {
    super(name, type, value, modifiers);
  }

  public BshVariable(String name, Object value, Modifiers modifiers) throws UtilEvalError {
    super(name, value, modifiers);
  }

  public BshVariable(String name, String typeDescriptor, Object value, Modifiers modifiers) throws UtilEvalError {
    super(name, typeDescriptor, value, modifiers);
  }

}
