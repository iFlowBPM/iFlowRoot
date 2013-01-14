package pt.iknow.floweditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.transition.FlowStateHistoryTO;
import pt.iknow.floweditor.Cor.Colors;
import pt.iknow.floweditor.Cor.Fonts;
import pt.iknow.floweditor.objects.BlockState;
import pt.iknow.utils.swing.ButtonChanger;

public class CanvasBlockSorter extends JPanel {
  private static final long serialVersionUID = 80082618583276915L;

  private final Desenho canvas;
  private Cor palette;

  public CanvasBlockSorter(Desenho desenho, List<BlockState> blockStates) {
    this(desenho, blockStates, new Cor());
  }

  public CanvasBlockSorter(Desenho desenho, List<BlockState> blockStates, Cor palette) {
    super(new FlowLayout(FlowLayout.LEFT, 0, 0));

    this.canvas = desenho;
    this.palette = palette;

    for (BlockState blockState : blockStates) {
      final InstanciaComponente comp = blockState.getComponente();
      FlowStateHistoryTO flowState = blockState.getFlowStateHistory();

      Color color = Cor.TOUCHED_BLOCK;
      Font font = palette.getFont(Fonts.Tipo_8_Plain);
      if (blockState.isCurrentBlock()) {
        color = Cor.CURRENT_BLOCK;
        font = palette.getFont(Fonts.Tipo_8_Bold);
      }

      JLabel label;
      // Component Top
      JPanel top = new JPanel(new BorderLayout());
      top.setBackground(palette.getColor(Colors.Fundo));
      label = getLabel(comp.Nome, null, color, font);
      top.add(label, BorderLayout.PAGE_END);

      // Component Center
      JPanel center = new JPanel(new BorderLayout());
      center.setBackground(palette.getColor(Colors.Fundo));
      label = getLabel(null, new ImageIcon(comp.C_B.funcao_Desenho), color, font);
      center.add(label, BorderLayout.CENTER);

      // Component Bottom
      JPanel bottom = new JPanel(new BorderLayout());
      bottom.setBackground(palette.getColor(Colors.Fundo));
      
      String descr = comp.C_B.Descricao;
      if(StringUtils.isNotBlank(comp.C_B.descrKey))
        descr = desenho.janela.getBlockMessages().getString(comp.C_B.descrKey);
      

      label = getLabel(descr, null, color, palette.getFont(Fonts.Tipo_8_Bold));
      bottom.add(label, BorderLayout.PAGE_START);
      SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      label = getLabel(date.format(flowState.getMDate()), null, color.darker(), palette.getFont());
      bottom.add(label, BorderLayout.PAGE_END);

      // Main component
      JPanel item = new JPanel(new BorderLayout());
      item.add(top, BorderLayout.PAGE_START);
      item.add(center, BorderLayout.CENTER);
      item.add(bottom, BorderLayout.PAGE_END);
      item.setBackground(palette.getColor(Colors.Fundo));

      JButton button = new JButton();
      button.add(item);
      button.setPreferredSize(new Dimension(135, 100));
      button.setBackground(palette.getColor(Colors.Fundo));
      button.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          canvas.gotoComponente(comp);
          canvas.viewStateLogs();
        }
      });
      button.setBorderPainted(false);
      button.addMouseListener(new ButtonChanger());
      add(button);
    }
  }

  private JLabel getLabel(String text, Icon icon, Color color, Font font) {
    JLabel label = new JLabel(text, icon, JLabel.CENTER);
    label.setFont(font);
    label.setForeground(color);
    label.setBackground(palette.getColor(Colors.Fundo));
    return label;
  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    setBackground(palette.getColor(Colors.Fundo));
  }

  public Cor getPalette() {
    return palette;
  }

  public void setPalette(Cor palette) {
    this.palette = palette;
  }
}
