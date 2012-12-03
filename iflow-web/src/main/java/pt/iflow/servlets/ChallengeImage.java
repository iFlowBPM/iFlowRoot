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
package pt.iflow.servlets;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.RandomStringUtils;

/**
 * Servlet implementation class for Servlet: AuthenticationServlet
 *
 * @web.servlet name="ChallengeImage"
 * 
 * @web.servlet-mapping url-pattern="/challenge"
 */
 public class ChallengeImage extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
   static final long serialVersionUID = 1L;
   
   
   /* (non-Java-doc)
    * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
    */
   protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
     String challengeText = RandomStringUtils.random(4, true, true);
     request.getSession().setAttribute("challenge", challengeText);
     
     
     BufferedImage img = new BufferedImage(120, 40, BufferedImage.TYPE_INT_RGB);
     Graphics2D graphics = img.createGraphics();
     
     // Turn on anti alias
     graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
     
     graphics.setPaint(Color.WHITE);
     graphics.fillRect(0,0,img.getWidth(), img.getHeight());
     
     graphics.setColor(Color.BLACK);
     Font f = new Font("Monospace", Font.BOLD, 30);
     graphics.setFont(f);
     graphics.drawString(challengeText, 23, 28);
     graphics.drawRect(0,0,img.getWidth()-1, img.getHeight()-1);
     graphics.dispose();

     img.flush();
     
     ByteArrayOutputStream b = new ByteArrayOutputStream();
     ImageIO.write(img, "png", b);
     byte [] data = b.toByteArray();
     
     
     response.setHeader("Content-Disposition","inline;filename=challenge.png");
     OutputStream out = response.getOutputStream();
     response.setContentLength(data.length);
     out.write(data);
     out.flush();
     out.close();
   }

}
