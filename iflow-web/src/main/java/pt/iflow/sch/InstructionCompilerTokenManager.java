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
/* Generated By:JavaCC: Do not edit this line. InstructionCompilerTokenManager.java */
package pt.iflow.sch;

import pt.iflow.api.sch.InstructionCompilerConstants;

public class InstructionCompilerTokenManager implements InstructionCompilerConstants
{
private static final int jjStopStringLiteralDfa_0(int pos, long active0)
{
   switch (pos)
   {
      case 0:
         if ((active0 & 0xf00000L) != 0L)
         {
            jjmatchedKind = 28;
            return 18;
         }
         if ((active0 & 0x60000L) != 0L)
         {
            jjmatchedKind = 34;
            return -1;
         }
         return -1;
      case 1:
         if ((active0 & 0xf00000L) != 0L)
         {
            jjmatchedKind = 28;
            jjmatchedPos = 1;
            return 18;
         }
         if ((active0 & 0x60000L) != 0L)
         {
            if (jjmatchedPos == 0)
            {
               jjmatchedKind = 34;
               jjmatchedPos = 0;
            }
            return -1;
         }
         return -1;
      case 2:
         if ((active0 & 0xf00000L) != 0L)
         {
            jjmatchedKind = 28;
            jjmatchedPos = 2;
            return 18;
         }
         return -1;
      case 3:
         if ((active0 & 0xb00000L) != 0L)
         {
            jjmatchedKind = 28;
            jjmatchedPos = 3;
            return 18;
         }
         if ((active0 & 0x400000L) != 0L)
            return 18;
         return -1;
      case 4:
         if ((active0 & 0x100000L) != 0L)
         {
            jjmatchedKind = 28;
            jjmatchedPos = 4;
            return 18;
         }
         if ((active0 & 0xa00000L) != 0L)
            return 18;
         return -1;
      case 5:
         if ((active0 & 0x100000L) != 0L)
         {
            jjmatchedKind = 28;
            jjmatchedPos = 5;
            return 18;
         }
         return -1;
      case 6:
         if ((active0 & 0x100000L) != 0L)
         {
            jjmatchedKind = 28;
            jjmatchedPos = 6;
            return 18;
         }
         return -1;
      case 7:
         if ((active0 & 0x100000L) != 0L)
         {
            jjmatchedKind = 28;
            jjmatchedPos = 7;
            return 18;
         }
         return -1;
      case 8:
         if ((active0 & 0x100000L) != 0L)
         {
            jjmatchedKind = 28;
            jjmatchedPos = 8;
            return 18;
         }
         return -1;
      default :
         return -1;
   }
}
private static final int jjStartNfa_0(int pos, long active0)
{
   return jjMoveNfa_0(jjStopStringLiteralDfa_0(pos, active0), pos + 1);
}
static private final int jjStopAtPos(int pos, int kind)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   return pos + 1;
}
static private final int jjStartNfaWithStates_0(int pos, int kind, int state)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) { return pos + 1; }
   return jjMoveNfa_0(state, pos + 1);
}
static private final int jjMoveStringLiteralDfa0_0()
{
   switch(curChar)
   {
      case 9:
         return jjStopAtPos(0, 3);
      case 10:
         return jjStopAtPos(0, 4);
      case 13:
         return jjStopAtPos(0, 2);
      case 32:
         return jjStopAtPos(0, 1);
      case 33:
         jjmatchedKind = 19;
         return jjMoveStringLiteralDfa1_0(0x1000L);
      case 38:
         return jjMoveStringLiteralDfa1_0(0x20000L);
      case 40:
         return jjStopAtPos(0, 30);
      case 41:
         return jjStopAtPos(0, 31);
      case 42:
         return jjStopAtPos(0, 7);
      case 43:
         return jjStopAtPos(0, 5);
      case 45:
         return jjStopAtPos(0, 6);
      case 47:
         return jjStopAtPos(0, 8);
      case 60:
         jjmatchedKind = 15;
         return jjMoveStringLiteralDfa1_0(0x10000L);
      case 61:
         jjmatchedKind = 10;
         return jjMoveStringLiteralDfa1_0(0x800L);
      case 62:
         jjmatchedKind = 13;
         return jjMoveStringLiteralDfa1_0(0x4000L);
      case 91:
         return jjStopAtPos(0, 32);
      case 93:
         return jjStopAtPos(0, 33);
      case 94:
         return jjStopAtPos(0, 9);
      case 102:
         return jjMoveStringLiteralDfa1_0(0xa00000L);
      case 116:
         return jjMoveStringLiteralDfa1_0(0x400000L);
      case 118:
         return jjMoveStringLiteralDfa1_0(0x100000L);
      case 124:
         return jjMoveStringLiteralDfa1_0(0x40000L);
      default :
         return jjMoveNfa_0(2, 0);
   }
}
static private final int jjMoveStringLiteralDfa1_0(long active0)
{
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(0, active0);
      return 1;
   }
   switch(curChar)
   {
      case 38:
         if ((active0 & 0x20000L) != 0L)
            return jjStopAtPos(1, 17);
         break;
      case 61:
         if ((active0 & 0x800L) != 0L)
            return jjStopAtPos(1, 11);
         else if ((active0 & 0x1000L) != 0L)
            return jjStopAtPos(1, 12);
         else if ((active0 & 0x4000L) != 0L)
            return jjStopAtPos(1, 14);
         else if ((active0 & 0x10000L) != 0L)
            return jjStopAtPos(1, 16);
         break;
      case 97:
         return jjMoveStringLiteralDfa2_0(active0, 0xa00000L);
      case 101:
         return jjMoveStringLiteralDfa2_0(active0, 0x100000L);
      case 114:
         return jjMoveStringLiteralDfa2_0(active0, 0x400000L);
      case 124:
         if ((active0 & 0x40000L) != 0L)
            return jjStopAtPos(1, 18);
         break;
      default :
         break;
   }
   return jjStartNfa_0(0, active0);
}
static private final int jjMoveStringLiteralDfa2_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(0, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(1, active0);
      return 2;
   }
   switch(curChar)
   {
      case 108:
         return jjMoveStringLiteralDfa3_0(active0, 0xa00000L);
      case 114:
         return jjMoveStringLiteralDfa3_0(active0, 0x100000L);
      case 117:
         return jjMoveStringLiteralDfa3_0(active0, 0x400000L);
      default :
         break;
   }
   return jjStartNfa_0(1, active0);
}
static private final int jjMoveStringLiteralDfa3_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(1, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(2, active0);
      return 3;
   }
   switch(curChar)
   {
      case 100:
         return jjMoveStringLiteralDfa4_0(active0, 0x100000L);
      case 101:
         if ((active0 & 0x400000L) != 0L)
            return jjStartNfaWithStates_0(3, 22, 18);
         break;
      case 115:
         return jjMoveStringLiteralDfa4_0(active0, 0xa00000L);
      default :
         break;
   }
   return jjStartNfa_0(2, active0);
}
static private final int jjMoveStringLiteralDfa4_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(2, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(3, active0);
      return 4;
   }
   switch(curChar)
   {
      case 97:
         return jjMoveStringLiteralDfa5_0(active0, 0x100000L);
      case 101:
         if ((active0 & 0x800000L) != 0L)
            return jjStartNfaWithStates_0(4, 23, 18);
         break;
      case 111:
         if ((active0 & 0x200000L) != 0L)
            return jjStartNfaWithStates_0(4, 21, 18);
         break;
      default :
         break;
   }
   return jjStartNfa_0(3, active0);
}
static private final int jjMoveStringLiteralDfa5_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(3, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(4, active0);
      return 5;
   }
   switch(curChar)
   {
      case 100:
         return jjMoveStringLiteralDfa6_0(active0, 0x100000L);
      default :
         break;
   }
   return jjStartNfa_0(4, active0);
}
static private final int jjMoveStringLiteralDfa6_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(4, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(5, active0);
      return 6;
   }
   switch(curChar)
   {
      case 101:
         return jjMoveStringLiteralDfa7_0(active0, 0x100000L);
      default :
         break;
   }
   return jjStartNfa_0(5, active0);
}
static private final int jjMoveStringLiteralDfa7_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(5, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(6, active0);
      return 7;
   }
   switch(curChar)
   {
      case 105:
         return jjMoveStringLiteralDfa8_0(active0, 0x100000L);
      default :
         break;
   }
   return jjStartNfa_0(6, active0);
}
static private final int jjMoveStringLiteralDfa8_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(6, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(7, active0);
      return 8;
   }
   switch(curChar)
   {
      case 114:
         return jjMoveStringLiteralDfa9_0(active0, 0x100000L);
      default :
         break;
   }
   return jjStartNfa_0(7, active0);
}
static private final int jjMoveStringLiteralDfa9_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(7, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(8, active0);
      return 9;
   }
   switch(curChar)
   {
      case 111:
         if ((active0 & 0x100000L) != 0L)
            return jjStartNfaWithStates_0(9, 20, 18);
         break;
      default :
         break;
   }
   return jjStartNfa_0(8, active0);
}
static private final void jjCheckNAdd(int state)
{
   if (jjrounds[state] != jjround)
   {
      jjstateSet[jjnewStateCnt++] = state;
      jjrounds[state] = jjround;
   }
}
static private final void jjAddStates(int start, int end)
{
   do {
      jjstateSet[jjnewStateCnt++] = jjnextStates[start];
   } while (start++ != end);
}
static private final void jjCheckNAddTwoStates(int state1, int state2)
{
   jjCheckNAdd(state1);
   jjCheckNAdd(state2);
}
static private final void jjCheckNAddStates(int start, int end)
{
   do {
      jjCheckNAdd(jjnextStates[start]);
   } while (start++ != end);
}
static private final void jjCheckNAddStates(int start)
{
   jjCheckNAdd(jjnextStates[start]);
   jjCheckNAdd(jjnextStates[start + 1]);
}
static final long[] jjbitVec0 = {
   0x0L, 0x0L, 0xffffffffffffffffL, 0xffffffffffffffffL
};
static private final int jjMoveNfa_0(int startState, int curPos)
{
   int[] nextStates;
   int startsAt = 0;
   jjnewStateCnt = 18;
   int i = 1;
   jjstateSet[0] = startState;
   int j, kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff)
         ReInitRounds();
      if (curChar < 64)
      {
         long l = 1L << curChar;
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 18:
               case 1:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 28)
                     kind = 28;
                  jjCheckNAdd(1);
                  break;
               case 2:
                  if (kind > 34)
                     kind = 34;
                  if ((0x3ff000000000000L & l) != 0L)
                  {
                     if (kind > 24)
                        kind = 24;
                     jjCheckNAddStates(0, 6);
                  }
                  else if (curChar == 46)
                     jjCheckNAddTwoStates(4, 8);
                  break;
               case 3:
                  if (curChar == 46)
                     jjCheckNAddTwoStates(4, 8);
                  break;
               case 4:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 24)
                     kind = 24;
                  jjCheckNAddTwoStates(4, 5);
                  break;
               case 6:
                  if ((0x280000000000L & l) != 0L)
                     jjCheckNAdd(7);
                  break;
               case 7:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 24)
                     kind = 24;
                  jjCheckNAdd(7);
                  break;
               case 8:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 24)
                     kind = 24;
                  jjCheckNAdd(8);
                  break;
               case 9:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 24)
                     kind = 24;
                  jjCheckNAddStates(0, 6);
                  break;
               case 10:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 24)
                     kind = 24;
                  jjCheckNAddTwoStates(10, 5);
                  break;
               case 11:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 24)
                     kind = 24;
                  jjCheckNAddStates(7, 9);
                  break;
               case 12:
                  if (curChar == 46)
                     jjCheckNAdd(13);
                  break;
               case 13:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 24)
                     kind = 24;
                  jjCheckNAddTwoStates(13, 5);
                  break;
               case 14:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 24)
                     kind = 24;
                  jjCheckNAddTwoStates(14, 15);
                  break;
               case 15:
                  if (curChar == 46)
                     jjCheckNAdd(16);
                  break;
               case 16:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 24)
                     kind = 24;
                  jjCheckNAdd(16);
                  break;
               case 17:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 24)
                     kind = 24;
                  jjCheckNAdd(17);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 18:
                  if ((0x7fffffe87fffffeL & l) != 0L)
                  {
                     if (kind > 28)
                        kind = 28;
                     jjCheckNAdd(1);
                  }
                  if ((0x7fffffe87fffffeL & l) != 0L)
                  {
                     if (kind > 28)
                        kind = 28;
                     jjCheckNAddTwoStates(0, 1);
                  }
                  break;
               case 2:
                  if (kind > 34)
                     kind = 34;
                  if ((0x7fffffe87fffffeL & l) != 0L)
                  {
                     if (kind > 28)
                        kind = 28;
                     jjCheckNAddTwoStates(0, 1);
                  }
                  break;
               case 0:
                  if ((0x7fffffe87fffffeL & l) == 0L)
                     break;
                  if (kind > 28)
                     kind = 28;
                  jjCheckNAddTwoStates(0, 1);
                  break;
               case 1:
                  if ((0x7fffffe87fffffeL & l) == 0L)
                     break;
                  if (kind > 28)
                     kind = 28;
                  jjCheckNAdd(1);
                  break;
               case 5:
                  if ((0x2000000020L & l) != 0L)
                     jjAddStates(10, 11);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 2:
                  if ((jjbitVec0[i2] & l2) != 0L && kind > 34)
                     kind = 34;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      if (kind != 0x7fffffff)
      {
         jjmatchedKind = kind;
         jjmatchedPos = curPos;
         kind = 0x7fffffff;
      }
      ++curPos;
      if ((i = jjnewStateCnt) == (startsAt = 18 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
static final int[] jjnextStates = {
   10, 11, 12, 5, 14, 15, 17, 11, 12, 5, 6, 7, 
};
public static final String[] jjstrLiteralImages = {
"", null, null, null, "\12", "\53", "\55", "\52", "\57", "\136", "\75", 
"\75\75", "\41\75", "\76", "\76\75", "\74", "\74\75", "\46\46", "\174\174", "\41", 
"\166\145\162\144\141\144\145\151\162\157", "\146\141\154\163\157", "\164\162\165\145", "\146\141\154\163\145", null, null, 
null, null, null, null, "\50", "\51", "\133", "\135", null, };
public static final String[] lexStateNames = {
   "DEFAULT", 
};
static final long[] jjtoToken = {
   0x7d1fffff1L, 
};
static final long[] jjtoSkip = {
   0xeL, 
};
static private ASCII_CharStream input_stream;
static private final int[] jjrounds = new int[18];
static private final int[] jjstateSet = new int[36];
static protected char curChar;
public InstructionCompilerTokenManager(ASCII_CharStream stream)
{
   if (input_stream != null)
      throw new TokenMgrError("ERROR: Second call to constructor of static lexer. You must use ReInit() to initialize the static variables.", TokenMgrError.STATIC_LEXER_ERROR);
   input_stream = stream;
}
public InstructionCompilerTokenManager(ASCII_CharStream stream, int lexState)
{
   this(stream);
   SwitchTo(lexState);
}
static public void ReInit(ASCII_CharStream stream)
{
   jjmatchedPos = jjnewStateCnt = 0;
   curLexState = defaultLexState;
   input_stream = stream;
   ReInitRounds();
}
static private final void ReInitRounds()
{
   int i;
   jjround = 0x80000001;
   for (i = 18; i-- > 0;)
      jjrounds[i] = 0x80000000;
}
static public void ReInit(ASCII_CharStream stream, int lexState)
{
   ReInit(stream);
   SwitchTo(lexState);
}
static public void SwitchTo(int lexState)
{
   if (lexState >= 1 || lexState < 0)
      throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", TokenMgrError.INVALID_LEXICAL_STATE);
   else
      curLexState = lexState;
}

static private final Token jjFillToken()
{
   Token t = Token.newToken(jjmatchedKind);
   t.kind = jjmatchedKind;
   String im = jjstrLiteralImages[jjmatchedKind];
   t.image = (im == null) ? input_stream.GetImage() : im;
   t.beginLine = input_stream.getBeginLine();
   t.beginColumn = input_stream.getBeginColumn();
   t.endLine = input_stream.getEndLine();
   t.endColumn = input_stream.getEndColumn();
   return t;
}

static int curLexState = 0;
static int defaultLexState = 0;
static int jjnewStateCnt;
static int jjround;
static int jjmatchedPos;
static int jjmatchedKind;

public static final Token getNextToken() 
{
  int kind;
  Token specialToken = null;
  Token matchedToken;
  int curPos = 0;

  EOFLoop :
  for (;;)
  {   
   try   
   {     
      curChar = input_stream.BeginToken();
   }     
   catch(java.io.IOException e)
   {        
      jjmatchedKind = 0;
      matchedToken = jjFillToken();
      return matchedToken;
   }

   jjmatchedKind = 0x7fffffff;
   jjmatchedPos = 0;
   curPos = jjMoveStringLiteralDfa0_0();
   if (jjmatchedKind != 0x7fffffff)
   {
      if (jjmatchedPos + 1 < curPos)
         input_stream.backup(curPos - jjmatchedPos - 1);
      if ((jjtoToken[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L)
      {
         matchedToken = jjFillToken();
         return matchedToken;
      }
      else
      {
         continue EOFLoop;
      }
   }
   int error_line = input_stream.getEndLine();
   int error_column = input_stream.getEndColumn();
   String error_after = null;
   boolean EOFSeen = false;
   try { input_stream.readChar(); input_stream.backup(1); }
   catch (java.io.IOException e1) {
      EOFSeen = true;
      error_after = curPos <= 1 ? "" : input_stream.GetImage();
      if (curChar == '\n' || curChar == '\r') {
         error_line++;
         error_column = 0;
      }
      else
         error_column++;
   }
   if (!EOFSeen) {
      input_stream.backup(1);
      error_after = curPos <= 1 ? "" : input_stream.GetImage();
   }
   throw new TokenMgrError(EOFSeen, curLexState, error_line, error_column, error_after, curChar, TokenMgrError.LEXICAL_ERROR);
  }
}

}
