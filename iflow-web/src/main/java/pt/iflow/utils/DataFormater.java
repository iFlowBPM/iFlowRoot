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
//
// DataFormater.java
//
// Copyright (C) 2002, iKnow - Consultoria em Tecnologias de Informacao, Lda
//

/**
 * <p>Title: DataFormater</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: iKnow</p>
 * @author unascribed
 * @version 1.0
 */
package pt.iflow.utils;


import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataFormater implements Serializable
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
	
  private final static double EUR_TO_PTE_CONV = 200.482;

  /**
   * Constructor
   */
  public DataFormater()
  {
  }


  /**
   * Formats the currency in EUR.
   * @param d the number to be formated.
   * @param symbol the symbol appears or not.
   * @return the formated string.
   */
  public String formatCurrencyEUR (double d, boolean symbol)
  {
//     d = d / EUR_TO_PTE_CONV;

    DecimalFormat df = new DecimalFormat("#0.00\u00A4");
    DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();
    if (symbol)
      dfs.setCurrencySymbol(" EUR");
    else
      dfs.setCurrencySymbol("");

    dfs.setMonetaryDecimalSeparator(',');
    dfs.setGroupingSeparator('.');
    df.setDecimalFormatSymbols(dfs);
    df.setGroupingSize(3);
    df.setGroupingUsed(true);
    return df.format(d);
  }

  /**
   * Formats the currency in EUR.
   * @param d the number to be formated.
   * @param symbol the symbol appears or not.
   * @return the formated string.
   */
  public String formatCurrencyEUR2 (double d, boolean symbol)
  {
//     d = d / EUR_TO_PTE_CONV;

    DecimalFormat df = new DecimalFormat("#0.00\u00A4");
    DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();
    if (symbol)
      dfs.setCurrencySymbol(" EUR");
    else
      dfs.setCurrencySymbol("");

    dfs.setMonetaryDecimalSeparator('.');
    df.setDecimalFormatSymbols(dfs);
    df.setGroupingUsed(false);
    return df.format(d);
  }


  /**
   * Formats the currency in PTE.
   * @param d the number to be formated.
   * @param symbol the symbol appears or not.
   * @return the formated string.
   */
  public String formatCurrencyPTE (double d, boolean symbol)
  {
    d = d * EUR_TO_PTE_CONV;

    DecimalFormat df = null;
    if (symbol)
      df = new DecimalFormat("#0\u00A4");
    else
      df = new DecimalFormat("#0");
    DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();

    dfs.setGroupingSeparator('.');
    dfs.setCurrencySymbol("$00");
    df.setDecimalFormatSymbols(dfs);
    df.setGroupingSize(3);
    df.setGroupingUsed(true);
    return df.format(d);
  }



  /**
   * Converts a double to a string.
   * @param d the number to be converted.
   * @return the formated string or "" if NaN.
   */
  public String printNumber (double d)
  {
    DecimalFormat df = null;
    if (Double.isNaN(d))
      return "";
    if (d == ((double)((int) d)))
      df = new DecimalFormat("#0");
    else
      df = new DecimalFormat("#0.0#");
    return df.format(d);
  }

  /**
   * Converts a number to a string.
   * @param d the number to be converted.
   * @return the formated string or "NULL" if NaN.
   */
  public String printNullNumber (double d)
  {
    DecimalFormat df = null;
    if (Double.isNaN(d))
      return "NULL";
    if (d == ((double)((int) d)))
      df = new DecimalFormat("#0");
    else
      df = new DecimalFormat("#0.0#");
    return df.format(d);
  }

  /**
   * Converts a number to a PTE string.
   * @param d the number to be converted.
   * @param symbol the symbol appears or not.
   * @return the formated string or "" if NaN.
   */
  public String printPTE (double d, boolean symbol)
  {
     if (Double.isNaN(d))
       return "";
     else
       return formatCurrencyPTE (d, symbol);
  }

  /**
   * Converts a number to a EUR string.
   * @param d the number to be converted.
   * @param symbol symbol appears or not.
   * @return the formated string or "" if NaN.
   */
  public String printEUR (double d, boolean symbol)
  {
     if (Double.isNaN(d))
       return "";
     else
       return formatCurrencyEUR (d, symbol);
  }

  /**
   * Converts a number to a EUR string.
   * @param s the string to be converted.
   * @param symbol symbol appears or not.
   * @return the formated string or "" if NaN.
   */
  public String printEUR (String s, boolean symbol)
  {
    DataParser dp = new DataParser();
    double d = dp.parseCurrency(s);

    return this.printEUR(d, symbol);
  }

  /**
   * Converts a number to a EUR string.
   * @param d the number to be converted.
   * @param symbol symbol appears or not.
   * @return the formated string or "" if NaN.
   */
  public String printEUR2 (double d, boolean symbol)
  {
     if (Double.isNaN(d))
       return "";
     else
       return formatCurrencyEUR2 (d, symbol);
  }

  /**
   * Converts a number to a EUR string.
   * @param s the string to be converted.
   * @param symbol symbol appears or not.
   * @return the formated string or "" if NaN.
   */
  public String printEUR2 (String s, boolean symbol)
  {
    DataParser dp = new DataParser();
    double d = dp.parseCurrency2(s);

    return this.printEUR2(d, symbol);
  }

  /**
   * Converts a number to a string based on the currencyType.
   * @param d the number to be converted.
   * @param symbol the symbol appears or not.
   * @param currencyType the currency type.
   * @return the formated string or "" if NaN.
   */
  public String printCurrency (double d, boolean symbol, int currencyType)
  {
     if (Double.isNaN(d))
       return "";
     if (currencyType == 1)
       return formatCurrencyEUR (d, symbol);
     else
       return formatCurrencyPTE (d, symbol);
  }


  /**
   * Converts a number to a string, rounding to integer.
   * @param d the number to be converted.
   * @return the formated string or "" if NaN.
   */
  public String printInteger (double d)
  {
    String s = "";
    long i;

    if (Double.isNaN(d))
      return "";
    i = (long) d;
    if ((d-i) >= 0.5)
      i++;
    s = s + i;
    return s;
  }

  /**
   * Converts a number to a string in the percent form.
   * @param d the number to be converted.
   * @param dp the number of decimal places.
   * @param symbol the symbol appears or not.
   * @return the formated string or "" if NaN.
   */
  public String printPercent (double d, int dp, boolean symbol)
  {
    if (Double.isNaN(d))
      return "";

    d = d * 100;
    String str = "##0";
    if (dp > 0) {
      str = str + ".";
      for (int i = 0; i < dp; i++)
        str = str + "0";
    }

    DecimalFormat df = new DecimalFormat(str);
    if (symbol)
      return (df.format(d)+"%");
    else
      return df.format(d);
  }

  /**
   * Converts a number to a string in the percent form without multiplying by 100.
   * @param d the number to be converted.
   * @param dp the number of decimal places.
   * @param symbol the symbol appears or not.
   * @return the formated string or "" if NaN.
   */
  public String printPercent2 (double d, int dp, boolean symbol)
  {
    if (Double.isNaN(d))
      return "";

    String str = "##0";
    if (dp > 0) {
      str = str + ".";
      for (int i = 0; i < dp; i++)
        str = str + "0";
    }

    DecimalFormat df = new DecimalFormat(str);
    if (symbol)
      return (df.format(d)+"%");
    else
      return df.format(d);
  }

  /**
   * Converts a number to a string with dp decimal places.
   * @param d the number to be converted.
   * @param dp the number of decimal places.
   * @return the formated string or "" if NaN.
   */
  public String printDouble (double d, int dp)
  {
    if (Double.isNaN(d))
      return "";

    String str = "##0";
    if (dp > 0) {
      str = str + ".";
      for (int i = 0; i < dp; i++)
        str = str + "0";
    }

    DecimalFormat df = new DecimalFormat(str);
    return df.format(d);
  }

  /**
   * Converts a date to a string in the format dd-mm-yyyy.
   * @param date   date.
   * @return the formated String in the format dd-mm-yyyy.
   */
  public String PrintDate (Date date)
  {
    String str = new String();

    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
    str = dateFormatter.format(date);

    return str;
  }

}
