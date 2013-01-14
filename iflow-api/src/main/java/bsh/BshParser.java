package bsh;

import java.io.StringReader;

public class BshParser {

  /**
   * Use BeanShell Parser to check if expression contains errors or unused variables not in process.
   * @param expression
   * @return
   */
  public boolean checkExpression(String expression) {
    try {
      Parser parser = new Parser(new StringReader(expression));
      while( !parser.Line() ) {
        bsh.SimpleNode node = parser.popNode();
        // Use the node, etc. (See the bsh.BSH* classes)
      }
      
    } catch(Throwable t) {
      return false;
    }
    return true;
  }


}
