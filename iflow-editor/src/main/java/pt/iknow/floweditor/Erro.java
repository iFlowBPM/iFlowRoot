package pt.iknow.floweditor;

/*****************************************************
 *
 *  Project FLOW EDITOR
 *
 *  class: Erro
 *
 *  desc: info sobre erros
 *
 ****************************************************/

import javax.swing.JFrame;
import javax.swing.JOptionPane;


/*******************************
 * Erro
 */
public class Erro {
    
    
    /****************************************************
     * cria uma caiza de dialogo com a mensagem recebida
     */
    public Erro(String mensagem,JFrame janela) {
        JOptionPane.showMessageDialog(janela,
        mensagem,Mesg.WARN,
        JOptionPane.ERROR_MESSAGE);
    }
}
