package pt.iflow.api.utils;

import java.util.Iterator;

public class CharSequenceIterator implements Iterator<Character> {

  private CharSequence sequence;
  private int pos;
  
  public CharSequenceIterator(CharSequence sequence) {
    if(null == sequence) throw new NullPointerException("Sequence cannot be null!");
    this.sequence = sequence;
    pos = 0;
  }
  
  public boolean hasNext() {
    return pos < sequence.length();
  }

  public Character next() {
    Character result = null;
    if(hasNext())
      result = sequence.charAt(pos++);
    return result;
  }

  public void remove() {
    throw new UnsupportedOperationException();
  }
  
  public String toString() {
    return "CharSequenceIterator position: "+pos+" of "+this.sequence.length();
  }

}
