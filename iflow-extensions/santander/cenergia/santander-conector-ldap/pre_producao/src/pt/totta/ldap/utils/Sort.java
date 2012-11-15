package pt.totta.ldap.utils;

//import org.apache.log4j.PropertyConfigurator;
//import org.apache.log4j.Logger;

import java.util.*;
import java.text.*;

public class Sort
{
  // Create a logger for this class.
  //static Logger logger = Logger.getLogger(Sort.class.getName());


  private static CollationKey[] generateArrayOfKeys(Collator collator, Vector dataVector, String sortAttribute)
  {
    CollationKey[] keys = new CollationKey[dataVector.size()];
    
    for(int k = 0; k < keys.length; k++)
    {
      Hashtable hashtable = (Hashtable) dataVector.get(k);
      String str = (String) hashtable.get(sortAttribute);
      if(str == null || str.equals("null"))
        str = "";
      keys[k] = collator.getCollationKey(str);
    }
    return keys;
  }


  private static void quickSort(Collator collator, CollationKey[] keys, Vector dataVector, int lo0, int hi0, String sortAttribute)
  {
    int lo = lo0;
    int hi = hi0;
    
    if(lo >= hi)
      return;
    else
    if(lo == hi-1)
    {
      if(keys[lo].compareTo(keys[hi]) > 0)
      {
        CollationKey key = keys[lo];
        Hashtable hashtable = (Hashtable) dataVector.get(lo);
        keys[lo] = keys[hi];
        dataVector.set(lo, (Hashtable) dataVector.get(hi));
        keys[hi] = key;
        dataVector.set(hi, hashtable);
      }
      return;
    }
    
    CollationKey pivot = keys[(lo+hi) / 2];
    Hashtable hashtablePivot = (Hashtable) dataVector.get((lo+hi) / 2);
    keys[(lo+hi) / 2] = keys[hi];
    dataVector.set((lo+hi) / 2, (Hashtable) dataVector.get(hi));
    keys[hi] = pivot;
    dataVector.set(hi, hashtablePivot);
    
    while(lo < hi)
    {
      while((keys[lo].compareTo(pivot) <= 0) && (lo < hi))
        lo++;
      while((pivot.compareTo(keys[hi]) <= 0) && (lo < hi))
        hi--;
      if(lo < hi)
      {
        CollationKey key = keys[lo];
        Hashtable hashtable = (Hashtable) dataVector.get(lo);
        keys[lo] = keys[hi];
        dataVector.set(lo, (Hashtable) dataVector.get(hi));
        keys[hi] = key;
        dataVector.set(hi, hashtable);
      }
    }
    
    keys[hi0] = keys[hi];
    dataVector.set(hi0, (Hashtable) dataVector.get(hi));
    keys[hi] = pivot;
    dataVector.set(hi, hashtablePivot);
    
    quickSort(collator, keys, dataVector, lo0, lo-1, sortAttribute);
    quickSort(collator, keys, dataVector, hi+1, hi0, sortAttribute);
  }


  public static void vectorSort(Collator collator, Vector dataVector, String sortAttribute)
  {
    CollationKey[] keys = generateArrayOfKeys(collator, dataVector, sortAttribute);
    quickSort(collator, keys, dataVector, 0, dataVector.size()-1, sortAttribute);
    
    // Info.
    System.out.println("Sorted " + dataVector.size() + " entries using '" + sortAttribute + "' as sort attribute");
  }
}