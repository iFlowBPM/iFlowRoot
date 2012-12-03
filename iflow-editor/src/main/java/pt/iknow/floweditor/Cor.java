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
package pt.iknow.floweditor;

/*****************************************************
 *
 *  Project FLOW EDITOR
 *
 *  class: Cor
 *
 *  desc: informação sobre as cores do editor
 *
 ****************************************************/

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.commons.lang.StringUtils;

import pt.iknow.floweditor.messages.Messages;

/**
 * Constructed for singleton usage, but allows external instantiations for object specific colors.
 */
public class Cor {

  public static final Color BACKGROUND = new Color(235, 235, 235);
  public static final Color CURRENT_BLOCK = Color.BLACK;
  public static final Color TOUCHED_BLOCK = Color.GREEN.darker();
  public static final Color UNTOUCHED_BLOCK = Color.GRAY;
  public static final Color TOUCHED_LINE = Color.GREEN;
  public static final Color UNTOUCHED_LINE = Color.LIGHT_GRAY;
  
  private static Cor instance;

  static Cor getInstance() {
    if (instance == null) {
      instance = new Cor();
    }
    return instance;
  }
  
  // Color Palette
  enum Colors {
    Activo, Componente, CompSelected, Fundo, Cross, Invalido, BackSelected, Circulo, Rectangulo, Ponto
  };

  private Color Activo;
  private Color Componente;
  private Color Fundo;
  private Color Cross;
  private Color Ponto;
  private Color Invalido;
  private Color Circulo;
  private Color Rectangulo;
  private Color CompSelected;
  private Color BackSelected;


  // Font Palette
  enum Fonts {
    Main, Tipo_8_Plain, Tipo_8_Bold, Tipo_10_Plain, Tipo_10_Bold
  };

  private Font Main;
  private Font Tipo_8_Plain;
  private Font Tipo_10_Plain;
  private Font Tipo_8_Bold;
  private Font Tipo_10_Bold;

  public Cor() {
    resetPalette();
  }

  /**
   * Caixa de dialogo para alterar cores.
   */
  public void alteraCores(JFrame janela) {
    JDialogCor dialog = new JDialogCor(janela, Mesg.MudaCores);
    JPanel jp = new JPanel();

    dialog.buttons = new CorButton[Mesg.CompComCor.length];

    /* adiciona componentes de alteracao dde cores */
    for (int i = 0; i < Mesg.CompComCor.length; i++) {
      JPanel jpp = new JPanel();
      jpp.setBorder(BorderFactory.createTitledBorder(Mesg.CompComCor[i]));

      CorButton cb = new CorButton(getCor(Mesg.CompComCor[i]));
      jpp.setSize(250, 80);
      jpp.setMinimumSize(new Dimension(250, 80));
      jpp.setMaximumSize(new Dimension(250, 80));
      jpp.setPreferredSize(new Dimension(250, 80));
      jpp.add(cb);
      jp.add(jpp);
      /* guarda botao */
      dialog.buttons[i] = cb;
    }

    /* butoes */
    JPanel jpnew = new JPanel();
    JButton jb = new JButton(Mesg.OK);
    ActionListenerCor alc = new ActionListenerCor(dialog);
    jb.addActionListener(alc);
    jpnew.add(jb);
    jb = new JButton(Mesg.Cancelar);
    jb.addActionListener(alc);
    jpnew.add(jb);

    /* sizes */
    dialog.getContentPane().add(jpnew, BorderLayout.SOUTH);
    JScrollPane jsp = new JScrollPane(jp);
    jp.setPreferredSize(new Dimension(300, 85 * Mesg.CompComCor.length));
    jp.setSize(new Dimension(300, 85 * Mesg.CompComCor.length));
    jp.setMaximumSize(new Dimension(300, 1000));
    jsp.setMaximumSize(new Dimension(300, 1000));
    Container contentPane = dialog.getContentPane();
    contentPane.add(jsp);

    dialog.setSize(340, 450);
    dialog.setVisible(true);

  }

  public Color getCor(String nome) {
    if (StringUtils.equalsIgnoreCase(nome, Mesg.CompComCor[0]))
      return this.Fundo;
    if (StringUtils.equalsIgnoreCase(nome, Mesg.CompComCor[1]))
      return this.Componente;
    if (StringUtils.equalsIgnoreCase(nome, Mesg.CompComCor[2]))
      return this.Activo;
    if (StringUtils.equalsIgnoreCase(nome, Mesg.CompComCor[3]))
      return this.Ponto;
    if (StringUtils.equalsIgnoreCase(nome, Mesg.CompComCor[4]))
      return this.Circulo;
    if (StringUtils.equalsIgnoreCase(nome, Mesg.CompComCor[5]))
      return this.Rectangulo;
    if (StringUtils.equalsIgnoreCase(nome, Mesg.CompComCor[6]))
      return this.Cross;
    if (StringUtils.equalsIgnoreCase(nome, Mesg.CompComCor[7]))
      return this.BackSelected;
    if (StringUtils.equalsIgnoreCase(nome, Mesg.CompComCor[8]))
      return this.CompSelected;
    return null;
  }

  public void setCor(String nome, Color color) {
    if (StringUtils.equalsIgnoreCase(nome, Mesg.CompComCor[0])) {
      this.Fundo = color;
    } else if (StringUtils.equalsIgnoreCase(nome, Mesg.CompComCor[1])) {
      this.Componente = color;
    } else if (StringUtils.equalsIgnoreCase(nome, Mesg.CompComCor[2])) {
      this.Activo = color;
    } else if (StringUtils.equalsIgnoreCase(nome, Mesg.CompComCor[3])) {
      this.Ponto = color;
    } else if (StringUtils.equalsIgnoreCase(nome, Mesg.CompComCor[4])) {
      this.Circulo = color;
    } else if (StringUtils.equalsIgnoreCase(nome, Mesg.CompComCor[5])) {
      this.Rectangulo = color;
    } else if (StringUtils.equalsIgnoreCase(nome, Mesg.CompComCor[6])) {
      this.Cross = color;
    } else if (StringUtils.equalsIgnoreCase(nome, Mesg.CompComCor[7])) {
      this.BackSelected = color;
    } else if (StringUtils.equalsIgnoreCase(nome, Mesg.CompComCor[8])) {
      this.CompSelected = color;
    }
  }

  static class JDialogCor extends JDialog {
    private static final long serialVersionUID = -2822505252852515420L;
    CorButton[] buttons;

    public JDialogCor(JFrame jf, String nome) {
      super(jf, nome, true);
    }
  }

  static class ActionListenerCor implements ActionListener {
    JDialogCor jd;

    public ActionListenerCor(JDialogCor jd) {
      this.jd = jd;
    }

    public void actionPerformed(ActionEvent ev) {
      String accao = ev.getActionCommand();
      if (accao.equals(Mesg.OK)) {
        instance.Fundo = jd.buttons[0].myColor;
        instance.Componente = jd.buttons[1].myColor;
        instance.Activo = jd.buttons[2].myColor;
        instance.Ponto = jd.buttons[3].myColor;
        instance.Circulo = jd.buttons[4].myColor;
        instance.Rectangulo = jd.buttons[5].myColor;
        instance.Cross = jd.buttons[6].myColor;
        instance.BackSelected = jd.buttons[7].myColor;
        instance.CompSelected = jd.buttons[8].myColor;

        jd.dispose();
      } else if (accao.equals(Mesg.Cancelar)) {
        jd.dispose();
      }

    }
  }

  /*****************************************************************************
   * botao especial que contem um desenho com a cor
   */
  static class CorButton extends JButton {
    private static final long serialVersionUID = -8753020671421317812L;
    Color myColor;

    public CorButton(Color cor) {
      super();
      myColor = cor;
      setSize(150, 50);
      JPanel o = new JPanel() {
        private static final long serialVersionUID = 1L;

        public void paint(Graphics g) {
          Dimension d = getSize();
          g.setColor(myColor);
          g.fillRoundRect(0, 0, d.width, d.height, 20, 20);
        }
      };
      o.setSize(60, 30);
      o.setMaximumSize(new Dimension(60, 30));
      o.setMinimumSize(new Dimension(60, 30));
      o.setPreferredSize(new Dimension(60, 30));

      add(o);

      /* em caso de seleccao -> chamar cx dialogo para alteracao */
      addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ev) {
          Color newColor = JColorChooser.showDialog(null, Messages.getString("Cor.choose.color"), myColor); //$NON-NLS-1$
          if (newColor != null)
            myColor = newColor;
        }

      });

    }
  }

  /**
   * Retrieves a given transient color for usage.
   * 
   * @param item
   *          Item color to be retrieved.
   * @return Color from given item.
   */
  public Color getColor(Colors item) {
    Color retObj = null;
    switch (item) {
    case Activo:
      retObj = Activo;
      break;
    case Componente:
      retObj = Componente;
      break;
    case CompSelected:
      retObj = CompSelected;
      break;
    case Fundo:
      retObj = Fundo;
      break;
    case Cross:
      retObj = Cross;
      break;
    case Invalido:
      retObj = Invalido;
      break;
    case BackSelected:
      retObj = BackSelected;
      break;
    case Circulo:
      retObj = Circulo;
      break;
    case Rectangulo:
      retObj = Rectangulo;
      break;
    case Ponto:
      retObj = Ponto;
      break;
    default:
      break;
    }
    return retObj;
  }

  /**
   * Updates transient color to the new given color.
   * 
   * @param item
   *          Item to update.
   * @param color
   *          New color.
   */
  public void setColor(Colors item, Color color) {
    switch (item) {
    case Activo:
      Activo = color;
      break;
    case Componente:
      Componente = color;
      break;
    case CompSelected:
      CompSelected = color;
      break;
    case Fundo:
      Fundo = color;
      break;
    case Cross:
      Cross = color;
      break;
    case Invalido:
      Invalido = color;
      break;
    case BackSelected:
      BackSelected = color;
      break;
    case Circulo:
      Circulo = color;
      break;
    case Rectangulo:
      Rectangulo = color;
      break;
    case Ponto:
      Ponto = color;
      break;
    default:
      break;
    }
  }

  public void setFont(Font font) {
    Main = font;
  }
  
  public void setFont(Fonts item, Font font) {
    switch (item) {
    case Tipo_8_Plain:
      Tipo_8_Plain = font;
      break;
    case Tipo_8_Bold:
      Tipo_8_Bold = font;
      break;
    case Tipo_10_Plain:
      Tipo_10_Plain = font;
      break;
    case Tipo_10_Bold:
      Tipo_10_Bold = font;
      break;
    case Main:
      Main = font;
      break;
    default:
      break;
    }
  }

  public Font getFont() {
    return Main;
  }
  
  public Font getFont(Fonts item) {
    Font retObj = null;
    switch (item) {
    case Tipo_8_Plain:
      retObj = Tipo_8_Plain;
      break;
    case Tipo_8_Bold:
      retObj = Tipo_8_Bold;
      break;
    case Tipo_10_Plain:
      retObj = Tipo_10_Plain;
      break;
    case Tipo_10_Bold:
      retObj = Tipo_10_Bold;
      break;
    case Main:
      retObj = Main;
      break;
    default:
      break;
    }
    return retObj;
  }

  public void resetPalette() {
    resetColors();
    resetFonts();
  }

  public void resetColors() {
    Fundo = Color.white;
    Componente = Color.black;
    Activo = Color.blue;
    Ponto = Color.black;
    Cross = Color.red;
    CompSelected = new Color(150, 150, 150);
    BackSelected = new Color(200, 200, 200);
    Invalido = Color.red;
    Circulo = Color.red;
    Rectangulo = Color.blue;
  }

  public void resetFonts() {
    Tipo_8_Plain = new Font("tipo8", Font.PLAIN, 10);
    Tipo_10_Plain = new Font("tipo10", Font.PLAIN, 12);
    Tipo_8_Bold = new Font("tipo8", Font.BOLD, 10);
    Tipo_10_Bold = new Font("tipo10", Font.BOLD, 12);
    Main = Tipo_8_Plain;
  }
}
