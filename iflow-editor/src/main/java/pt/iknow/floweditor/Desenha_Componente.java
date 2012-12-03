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
 *  class: Desenha_Componente
 *
 *  desc: desenha um dado componente
 *
 ****************************************************/

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

import org.apache.commons.lang.StringUtils;

/*****************************
 * Desenha_Componente
 */
public class Desenha_Componente {
    
    public static JPanel ppanel=new JPanel();
    
    /***************************************
     * funcao que desenha o componente
     * @param numero_funcao numero do desenho
     * @param g instancia de Graphics
     * @param Posicao_X posicao inicial x
     * @param Posicao_Y posicao inicial y
     * @param cb componente de biblioteca
     * @param cor cor com que se desenha o componenete
     */
     static public void Desenho_Componente(int numero_funcao,
    Image desenho,
    Graphics g,
    int Posicao_X,int Posicao_Y,
    Componente_Biblioteca cb,
    Color cor, Color activo) {
         Desenho_Componente(numero_funcao,desenho,g,Posicao_X,Posicao_Y,cb,cor,activo,ppanel);
     }
  
    static public void Desenho_Componente(int numero_funcao,
    Image desenho,
    Graphics g,
    int Posicao_X,int Posicao_Y,
    Componente_Biblioteca cb,
    Color cor, Color activo, JPanel jp) {
        
        /* variaveis auxiliares */
        int i;
        
        /* variaveis auxiliares */
        int x1=Posicao_X;
        int y1=Posicao_Y;
        int x2=cb.Largura_X;
        int y2=cb.Largura_Y;
        
        g.setColor(cor);
        /* desenhar nome */
        String descr = cb.Descricao;
        if(StringUtils.isNotBlank(cb.descrKey))
          descr = Janela.getInstance().getBlockMessages().getString(cb.descrKey);
        
        g.setFont(Cor.getInstance().getFont());
        int px=(x1+x2/2)-Janela.FM_Tipo_8.stringWidth(descr)/2;
        g.drawString(descr,px,y1+5+Janela.FM_Tipo_8.getHeight()/2+y2);
        
        
        /* drawImage*/
        if (desenho!=null) {
            int _x=desenho.getWidth(ppanel);
            int _y=desenho.getHeight(ppanel);
            cb.Largura_X=_x;
            cb.Largura_Y=_y;
            
            
             //   g.drawRect(x1,y1,_x,_y);
                //if (g.drawImage(desenho,x1,y1,x2,y2,ppanel))
            if (g.drawImage(desenho,x1,y1,_x,_y,jp)) {
                numero_funcao=-3; /* -3 not used */
                return;
            }
            else
                numero_funcao=1;
        }
        else
            /* componente complexos */
            if (numero_funcao==-2) {
                
                g.setColor(cor);
                g.fillRect(x1,y1,x2,y2);
                g.setColor(cor);
                g.drawRect(x1,y1,x2,y2);
            }
            else
                /* componentes compostos */
                if (numero_funcao==-1) {
                    g.setColor(activo);
                    g.fillRect(x1-3,y1-3,x2+6,y2+6);
                    g.setColor(cor);
                    g.drawRect(x1-3,y1-3,x2+6,y2+6);
                    
                    g.setColor(activo);
                    g.fillRect(x1,y1,x2,y2);
                    g.setColor(cor);
                    g.drawRect(x1,y1,x2,y2);
                }
                else
                    /* componente normal (rectangular) */
                {
                    g.setColor(activo);
                    g.fillRect(x1,y1,x2,y2);
                    g.setColor(cor);
                    g.drawRect(x1,y1,x2,y2);
                }
        
        /* desenhar entradas e saidas */
        g.setFont(Cor.getInstance().getFont());
        
        
        for (i=1; i<=cb.Num_Entradas;i++) {
            g.setColor(cor);
            g.drawLine(x1-10,y1+cb.Pontos_Entrada[i-1].y,
            x1,y1+cb.Pontos_Entrada[i-1].y);
            
            String s=(String)cb.desc_nomes_entradas.get(i-1);
            g.drawString(s,x1+2,y1+cb.Pontos_Entrada[i-1].y+
                Janela.FM_Tipo_8.getHeight()/4);
        }
        for (i=1; i<=cb.Num_Saidas;i++) {
            g.setColor(cor);
            g.drawLine(x1+x2,y1+cb.Pontos_Saida[i-1].y,
            x1+x2+10,y1+cb.Pontos_Saida[i-1].y);
            String s=(String)cb.desc_nomes_saidas.get(i-1);
            px=x1+x2-2-Janela.FM_Tipo_8.stringWidth(s);
            g.drawString(s,px,y1+cb.Pontos_Saida[i-1].y+
                Janela.FM_Tipo_8.getHeight()/4);
        }
    }
}
