package pt.iflow.api.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;

import pt.iflow.api.msg.IMessages;

public class Extenso {
  private ArrayList<Integer> nro;
  private BigInteger num;

  private String currencySingle; //$NON-NLS-1$
  private String currencyPlural; //$NON-NLS-1$
  private String and; //$NON-NLS-1$
  private String first; //$NON-NLS-1$
  private String de; //$NON-NLS-1$
  private String Qualificadores[][];
  private String Numeros[][]; //$NON-NLS-1$

  /**
   * Construtor
   */
  public Extenso() {
    nro = new ArrayList<Integer>();
  }

  /**
   * Construtor
   */
  public Extenso(IMessages msg) {

    currencySingle = msg.getString("utils.extended.amount.euro"); //$NON-NLS-1$
    currencyPlural = msg.getString("utils.extended.amount.euros"); //$NON-NLS-1$
    and = msg.getString("utils.extended.amount.e"); //$NON-NLS-1$
    first = msg.getString("utils.extended.amount.h"); //$NON-NLS-1$
    de = msg.getString("utils.extended.amount.de"); //$NON-NLS-1$
    Qualificadores = new String[][] {
        { msg.getString("utils.extended.amount.centimo"), msg.getString("utils.extended.amount.centimos") }, { "", "" }, { msg.getString("utils.extended.amount.thousand"), msg.getString("utils.extended.amount.thousand") }, { msg.getString("utils.extended.amount.milion"), msg.getString("utils.extended.amount.milions") }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
        { msg.getString("utils.extended.amount.bilion"), msg.getString("utils.extended.amount.bilions") }, { msg.getString("utils.extended.amount.trilion"), msg.getString("utils.extended.amount.trilions") }, { msg.getString("utils.extended.amount.quadrilion"), msg.getString("utils.extended.amount.quadrilions") }, { msg.getString("utils.extended.amount.quintilion"), msg.getString("utils.extended.amount.quintilions") }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
        { msg.getString("utils.extended.amount.sextilion"), msg.getString("utils.extended.amount.sextilions") }, { msg.getString("utils.extended.amount.septilion"), msg.getString("utils.extended.amount.septilions") } }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    Numeros = new String[][] {
        {
            msg.getString("utils.extended.amount.zero"), msg.getString("utils.extended.amount.one"), msg.getString("utils.extended.amount.two"), msg.getString("utils.extended.amount.three"), msg.getString("utils.extended.amount.four"), msg.getString("utils.extended.amount.five"), msg.getString("utils.extended.amount.six"), msg.getString("utils.extended.amount.seven"), msg.getString("utils.extended.amount.eight"), msg.getString("utils.extended.amount.nine"), msg.getString("utils.extended.amount.ten"), msg.getString("utils.extended.amount.eleven"), msg.getString("utils.extended.amount.twelve"), msg.getString("utils.extended.amount.thirteen"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$ //$NON-NLS-13$ //$NON-NLS-14$
            msg.getString("utils.extended.amount.fourteen"), msg.getString("utils.extended.amount.fifteen"), msg.getString("utils.extended.amount.sixteen"), msg.getString("utils.extended.amount.seventeen"), msg.getString("utils.extended.amount.eighteen"), msg.getString("utils.extended.amount.nineteen") }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
        {
            msg.getString("utils.extended.amount.twenty"), msg.getString("utils.extended.amount.thirty"), msg.getString("utils.extended.amount.forty"), msg.getString("utils.extended.amount.fifty"), msg.getString("utils.extended.amount.sixty"), msg.getString("utils.extended.amount.seventy"), msg.getString("utils.extended.amount.eighty"), msg.getString("utils.extended.amount.ninety") }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
        {
            msg.getString("utils.extended.amount.hundred"), msg.getString("utils.extended.amount.onehundred"), msg.getString("utils.extended.amount.twohundred"), msg.getString("utils.extended.amount.threehundred"), msg.getString("utils.extended.amount.fourhundred"), msg.getString("utils.extended.amount.fivehundred"), msg.getString("utils.extended.amount.sixhundred"), msg.getString("utils.extended.amount.sevenhundred"), msg.getString("utils.extended.amount.eighthundred"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
            msg.getString("utils.extended.amount.ninehundred") } };

    nro = new ArrayList<Integer>();
  }

  /**
   * Construtor
   * 
   *@param dec
   *          valor para colocar por extenso
   */
  public Extenso(IMessages msg, BigDecimal dec) {
    this(msg);
    setNumber(dec);
  }

  /**
   * Constructor for the Extenso object
   * 
   *@param dec
   *          valor para colocar por extenso
   */
  public Extenso(IMessages msg, double dec) {
    this(msg);
    setNumber(dec);
  }

  /**
   * Sets the Number attribute of the Extenso object
   * 
   *@param dec
   *          The new Number value
   */
  public void setNumber(BigDecimal dec) {
    // Converte para inteiro arredondando os centavos
    num = dec.setScale(2, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)).toBigInteger();

    // Adiciona valores
    nro.clear();
    if (num.equals(BigInteger.ZERO)) {
      // Centavos
      nro.add(new Integer(0));
      // Valor
      nro.add(new Integer(0));
    } else {
      // Adiciona centavos
      addRemainder(100);

      // Adiciona grupos de 1000
      while (!num.equals(BigInteger.ZERO)) {
        addRemainder(1000);
      }
    }
  }

  public void setNumber(double dec) {
    setNumber(new BigDecimal(dec));
  }

  /**
   * Description of the Method
   */
  public void show() {
    Iterator<Integer> valores = nro.iterator();

    while (valores.hasNext()) {
      Logger.debug("", this, "", ((Integer) valores.next()).toString());
    }
    Logger.debug("", this, "", toString());
  }

  /**
   * Description of the Method
   * 
   *@return Description of the Returned Value
   */
  public String toString() {
    StringBuffer buf = new StringBuffer();

    int ct;

    for (ct = nro.size() - 1; ct > 0; ct--) {
      // Se ja existe texto e o atual não é zero
      if (buf.length() > 0 && !ehGrupoZero(ct)) {
        buf.append(and);
      }
      buf.append(numToString(((Integer) nro.get(ct)).intValue(), ct));
    }
    if (buf.length() > 0) {
      if (ehUnicoGrupo())
        buf.append(de);
      while (buf.toString().endsWith(" ")) //$NON-NLS-1$
        buf.setLength(buf.length() - 1);
      if (ehPrimeiroGrupoUm())
        buf.insert(0, first);
      if (nro.size() == 2 && ((Integer) nro.get(1)).intValue() == 1) {
        buf.append(" " + currencySingle); //$NON-NLS-1$
      } else {
        buf.append(" " + currencyPlural); //$NON-NLS-1$
      }
      if (((Integer) nro.get(0)).intValue() != 0) {
        buf.append(and);
      }
    }
    if (((Integer) nro.get(0)).intValue() != 0) {
      buf.append(numToString(((Integer) nro.get(0)).intValue(), 0));
    }
    return buf.toString();
  }

  private boolean ehPrimeiroGrupoUm() {
    if (((Integer) nro.get(nro.size() - 1)).intValue() == 1)
      return true;
    return false;
  }

  /**
   * Adds a feature to the Remainder attribute of the Extenso object
   * 
   *@param divisor
   *          The feature to be added to the Remainder attribute
   */
  private void addRemainder(int divisor) {
    // Encontra newNum[0] = num modulo divisor, newNum[1] = num dividido divisor
    BigInteger[] newNum = num.divideAndRemainder(BigInteger.valueOf(divisor));

    // Adiciona modulo
    nro.add(new Integer(newNum[1].intValue()));

    // Altera numero
    num = newNum[0];
  }

  /**
   * Description of the Method
   * 
   *@return Description of the Returned Value
   */
  private boolean ehUnicoGrupo() {
    if (nro.size() <= 3)
      return false;
    if (!ehGrupoZero(1) && !ehGrupoZero(2))
      return false;
    boolean hasOne = false;
    for (int i = 3; i < nro.size(); i++) {
      if (((Integer) nro.get(i)).intValue() != 0) {
        if (hasOne)
          return false;
        hasOne = true;
      }
    }
    return true;
  }

  boolean ehGrupoZero(int ps) {
    if (ps <= 0 || ps >= nro.size())
      return true;
    return ((Integer) nro.get(ps)).intValue() == 0;
  }

  /**
   * Description of the Method
   * 
   *@param numero
   *          Description of Parameter
   *@param escala
   *          Description of Parameter
   *@return Description of the Returned Value
   */
  private String numToString(int numero, int escala) {
    int unidade = (numero % 10);
    int dezena = (numero % 100); // * nao pode dividir por 10 pois verifica de 0..19
    int centena = (numero / 100);
    StringBuffer buf = new StringBuffer();

    if (numero != 0) {
      if (centena != 0) {
        if (dezena == 0 && centena == 1) {
          buf.append(Numeros[2][0]);
        } else {
          buf.append(Numeros[2][centena]);
        }
      }

      if ((buf.length() > 0) && (dezena != 0)) {
        buf.append(and);
      }
      if (dezena > 19) {
        dezena /= 10;
        buf.append(Numeros[1][dezena - 2]);
        if (unidade != 0) {
          buf.append(and);
          buf.append(Numeros[0][unidade]);
        }
      } else if (centena == 0 || dezena != 0) {
        buf.append(Numeros[0][dezena]);
      }

      buf.append(" "); //$NON-NLS-1$
      if (numero == 1) {
        buf.append(Qualificadores[escala][0]);
      } else {
        buf.append(Qualificadores[escala][1]);
      }
    }

    return buf.toString();
  }
}
