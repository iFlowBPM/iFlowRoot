/*****************************************************************************************
    Infosistema iFlow - workflow and BPM platform
    Copyright(C) 2002-2012 Infosistema, S.A.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

    www.infosistema.com
    iflow@infosistema.com
    Av. Jose Gomes Ferreira, 11 3rd floor, s.34
    Miraflores
    1495-139 Alges Portugal
****************************************************************************************/

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
