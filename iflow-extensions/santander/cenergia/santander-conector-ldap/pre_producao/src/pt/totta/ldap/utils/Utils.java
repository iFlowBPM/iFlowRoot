package pt.totta.ldap.utils;

import java.io.*;
import java.util.*;
import java.text.*;
import java.security.*;

public class Utils
{
  /**
    * This method will get the current date.
    * @return The current date in yyyyMMdd format.
    */
  public String getCurrentDate()
  {
    Calendar calendar = Calendar.getInstance();
    Date currentTime = calendar.getTime();
    SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
    return dateFormatter.format(currentTime);
  }


  /**
    * This method will encrypt a password using MD5.
    * @param password The password to encrypt.
    * @return The encrypted password.
    */
  public String encrypt(String password)
  {
    StringBuffer encryptedPassword = new StringBuffer();
    MessageDigest digestAlgorithm = null;
    
    try
    {
      // Generates a MessageDigest object that implements the specified digest algorithm.
      digestAlgorithm = MessageDigest.getInstance("MD5", "SUN");
    }
    catch(NoSuchAlgorithmException e)
    {
      System.out.println("pt.totta.ldap.lowlevel.Utils.encrypt: No such algorithm.");
      return null;
    }
    catch(NoSuchProviderException e)
    {
      System.out.println("pt.totta.ldap.lowlevel.Utils.encrypt: No such provider.");
      return null;
    }
    
    // Convert this String into bytes according to the platform's default character encoding.
    byte passwordByteArray[] = password.getBytes();
    
    // Encrypt the password.
    byte encryptedPasswordByteArray[] = digestAlgorithm.digest(passwordByteArray);
    
    // Byte array to hexadecimal string buffer.
    for(int cicle = 0; cicle < encryptedPasswordByteArray.length; cicle++)
      encryptedPassword.append(Integer.toHexString(0x0100 + (encryptedPasswordByteArray[cicle] & 0x00FF)).substring(1));
    
    return encryptedPassword.toString();
  }


  /**
    * This method will replace a given String in a String.
    * @param string The String to process.
    * @param find What to find.
    * @param replace What to use when replacing.
    * @return The processed String.
    */
  public String replace(String string, String find, String replace)
  {
    int index = 0;
    while(true)
    {
      index = string.indexOf(find, index);
      if(index == -1)
        break;
      string = string.substring(0, index) + replace + string.substring(index + find.length(), string.length());
      index = index + replace.length();
    }
    return string;
  }


  /**
    * This method will replace a given String in a String.
    * @param string The String to process.
    * @param find What to find.
    * @param replace What to use when replacing.
    * @return The processed String.
    */
  public String userInput(String information)
  {
    BufferedReader cmdReader;
    String userInput = null;
    
    cmdReader = new BufferedReader(new InputStreamReader(System.in));
    
    System.out.print(information);
    
    try
    {
      while(userInput == null)
      {
        userInput = cmdReader.readLine();
      }
    }
    catch(IOException e)
    {
      System.out.println("pt.totta.ldap.api.Utils.userInput: " + e.getMessage() + ".");
      return null;
    }
    return(userInput);
  }
  
  public byte[] concatenate (byte[] l, byte[] r) {
	byte[] b = new byte [l.length + r.length];
	System.arraycopy (l, 0, b, 0, l.length);
	System.arraycopy (r, 0, b, l.length, r.length);
	return b;
   }
  
  public String toBase64(byte[] stringBytes) {
		String base64Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
		StringBuffer outBuff = new StringBuffer();
	    //byte[] stringBytes = str.getBytes();
	
	    for (int i=0; i < stringBytes.length; i += 3) {
	        int packed64 = 0;
	        int num64Chars = 4;
	        packed64 = stringBytes[i] << 16;
	        if (i+1 < stringBytes.length) {
	            packed64 = packed64 + (stringBytes[i+1] << 8);
	        } else {
	            num64Chars = 2;
	        }
	
	        if (i + 2 < stringBytes.length) {
	            packed64 = packed64 + stringBytes[i+2];
	        } else if (num64Chars == 4) {
	            num64Chars = 3;
	        }
	
	        for (int j=0; j < num64Chars; j++)
	            outBuff.append(base64Chars.charAt((packed64 >> (6 * (3-j))) & 63));
	
	        for (int j=num64Chars; j < 4; j++)
	            outBuff.append('=');
	    }
	    
	    System.out.println("Result 1 --> " + outBuff.toString());
	    return outBuff.toString();
  }
    
  public String Encode(byte[] stringBytes) {
      int bytePosition = 0;
      int comprimento;
      int fillBytes;
  
      if(stringBytes.length % 3 == 0) {
        comprimento = stringBytes.length * 4/3;
      } else {
        comprimento = (stringBytes.length/3) * 4 + 4;
      }
  
      byte[] original = new byte[3];
	  byte[] decoded = new byte[4];
      byte[] result = new byte[comprimento];
  
      fillBytes = 0;
  
      for(int i = 0; i < stringBytes.length; i+=3) {
        for(int j = 0; j < 3; j++) {
          original[j] = 0;
        }
  
        for(int j = 0; j < 4; j++) {
          decoded[j] = 0;
        }
  
        for(int j = 0; j < original.length; j++) {
          if(i + j < stringBytes.length) {
            original[j] = (byte)stringBytes[i+j];
          } else {
            fillBytes++;
          }
        }
  
        decoded[0] = (byte)((original[0] >> 2) & 0x3F);
        decoded[1] = (byte)((original[0] << 4 | original[1] >> 4) & 0x3F);
        decoded[2] = (byte)((original[1] << 2 | original[2] >> 6) & 0x3F);
        decoded[3] = (byte)(original[2] & 0x3F);
  
        for(int j = 0; j < 4; j++) {
          if(j < (4 - fillBytes)) {
            result[bytePosition++] = Encode(decoded[j]);
          } else {
            result[bytePosition++] = (byte)'=';
          }
        }
      }
      
      StringBuffer resultString = new StringBuffer();
      //System.out.print("RESULT --> ");
      for (int i=0; i< result.length; i++)
      	resultString.append((char)result[i]);
      System.out.println("Result 2 --> " + resultString.toString());
      return resultString.toString();
    }
  
    private byte Encode(byte oneByte) {
      if(oneByte <= 25) {
        return (byte)(oneByte +'A');
      } 
      if(oneByte <= 51) {
        return (byte)(oneByte - 26 +'a');
      }
      if(oneByte <= 61) {
        return (byte)(oneByte - 52 +'0');
      }
      if(oneByte == 62) {
        return (byte)'+';
      }
      if(oneByte == 63) {
        return (byte)'/';
      }
      return (byte)'=';
    }
  
}