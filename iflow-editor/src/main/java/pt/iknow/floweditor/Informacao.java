package pt.iknow.floweditor;

/*****************************************************
 *
 *  Project FLOW EDITOR
 *
 *  class: Informacao
 *
 *  desc: mostra informacao
 *
 ****************************************************/

import javax.swing.JFrame;
import javax.swing.JOptionPane;


public class  Informacao {
    public Informacao(String mensagem,JFrame janela) {
        JOptionPane.showMessageDialog(janela,
        
        mensagem,Mesg.Informacao,JOptionPane.INFORMATION_MESSAGE);
    }
    
}
