
package pt.iflow.sch;


public class InstructionCompilerBean extends Object implements java.io.Serializable {

   /**
	* 
	*/
   private static final long serialVersionUID = 1L;

   public boolean checkExpression (String s) {
     return InstructionCompiler.checkExpression(s);
   }

   public boolean checkCondition (String s) {
     return InstructionCompiler.checkCondition(s);
   }

   public String compileExpression (String s) {
     return InstructionCompiler.compileExpression(s);
   }
  
   public String compileCondition (String s) {
     return InstructionCompiler.compileCondition(s);
   }

   public String compile (String s) {
     return InstructionCompiler.compile(s);
   }

   public String analyzeExpression (String s) {
     return InstructionCompiler.analyzeExpression(s);
   }

   public String analyzeCondition (String s) {
     return InstructionCompiler.analyzeCondition(s);
   }

   public String analyze (String s) {
     return InstructionCompiler.analyze(s);
   }

   public String toHTML (String s) {
     return InstructionCompiler.toHTML(s);
   }

   public String compileJavascriptCondition (String s) {
     return InstructionCompiler.compileJavascriptCondition(s);
   }
}
