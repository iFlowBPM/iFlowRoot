package pt.iflow.api.utils;

import java.util.Random;

import org.xml.sax.InputSource;

public class RandomStringInputSource extends InputSource {

  private char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
  private Random rnd = new Random(System.currentTimeMillis());
  
  
  public char[] nextString() {
    char[] result = new char[rnd.nextInt(50)+1];
    for(int i = 0; i < result.length; i++)
      result[i] = chars[rnd.nextInt(chars.length)];
    return result;
  }
}
