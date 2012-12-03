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
// DataSet.java
//
// Copyright (C) 2002, iKnow - Consultoria em Tecnologias de Informacao, Lda
//

/**
 * <p>Title: DataParser</p>
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DataParser implements Serializable
{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

/**
   * Constructor
   */
  public DataParser()
  {
  }

  /**
   * Parses the currency.
   * @param str   the string currency to be parsed.
   * @return the parsed value.
   */
  public double parseCurrency (String str) {
    if (str == null || str.equals("") || str.length() == 0) {
      return Double.NaN;
    }

    StringBuffer sb = new StringBuffer();
    char[] o =  str.toCharArray();

    for (int i = 0; i < o.length; i++) {
      if (o[i] == '.') {
	continue;
      }
      if (o[i] == ',') {
	sb.append('.');
      }
      else {
	sb.append(o[i]);
      }
    }

    try {
      return Double.parseDouble(sb.toString());
    } catch (Exception e) {
      return Double.NaN;
    }
  }

  /**
   * Parses the currency.
   * @param str   the string currency to be parsed.
   * @return the parsed value.
   */
  public double parseCurrency2 (String str) {
    if (str == null || str.equals("") || str.length() == 0) {
      return Double.NaN;
    }

    try {
      return Double.parseDouble(str);
    } catch (Exception e) {
      return Double.NaN;
    }
  }

  /**
   * Parses a string to date.
   * @param str   date string in the format dd-mm-yyyy.
   * @return the Date value .
   */
  public Date parseStringToDate (String str)
  {
    Date d = new Date();
    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
    try{
      d = dateFormatter.parse(str);
    }
    catch(ParseException ee){
     d = null;
    }

    return d;
  }


  /**
   * Parses an expression and returns the first part (left side of '=')
   * @param expression
   * @return
   */
  public static String parseExpressionLeft(String expression) {
      String str;

      int index = expression.indexOf("=");
      str = expression.substring(0, index);
      //remove leading and trailing white spaces
      str = str.trim();

      return str;
   }

  /**
   * Parses an expression and returns the second part (right side of '=')
   * @param expression
   * @return
   */
  public static String parseExpressionRight(String expression) {
      String str;

      int index = expression.indexOf("=");
      str = expression.substring(index+1);
      //remove leading and trailing white spaces
      str = str.trim();

      return str;
   }

  /**
   * Parses a double into a double with intSize int part and decimalSize decimal part
   *
   * @param doubleNumber double to parse
   * @param intSize int part
   * @param decimalPlaces decimal places
   *
   * @return  currency code
   */
   public static String parseDouble(String doubleNumber, int intSize, int decimalPlaces) {
    String parsedNumber;

    if (doubleNumber.length() > intSize + decimalPlaces + 1) {
      // too big
      return "";
    }
    if (doubleNumber.equals("")) {
      doubleNumber = "0";
    }

    try {
      Double d = new Double(doubleNumber);
      if (d.isNaN()) {
        return "";
      }

      String str = "#";

      for (int i = 0; i < intSize ; i++) {
        str = str + "0";
      }


      if (decimalPlaces >= 0) {
        str = str + ".";
        for (int i = 0; i < decimalPlaces; i++)
          str = str + "0";
        }


        DecimalFormat df = new DecimalFormat(str);
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        df.setDecimalFormatSymbols(dfs);

        parsedNumber = df.format(d);
    } catch (Exception e) {
      parsedNumber = "";
    }

     return parsedNumber;
  }

}
