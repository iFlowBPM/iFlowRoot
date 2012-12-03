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
package pt.iflow.api.utils.mail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import pt.iflow.api.utils.mail.imap.IMAPMailPlainClient;
import pt.iflow.api.utils.mail.parsers.AbstractPropertiesMessageParser;
import pt.iflow.api.utils.mail.parsers.MessageParseException;
import pt.iflow.api.utils.mail.parsers.MessageParser;



public class MailTester {

  public static void main(String[] args) throws Exception {
    String host = args[0];
    String user = args[1];
    byte[] pass = args[2].getBytes();
    
    MailClient client = new IMAPMailPlainClient(host, user, pass);
    client.setDebug(false);    
    client.setInboxFolder("Inbox");

    client.connect();

    
    MessageParser parser = new AbstractPropertiesMessageParser() {
    
      public boolean parse(Message message) throws MessageParseException {
        
        try {
          System.out.println("FROM   : " + InternetAddress.toString(message.getFrom()));
          System.out.println("SUBJECT: " + message.getSubject());
          System.out.println("SENT   : " + message.getSentDate());

          Properties props = parseProperties(message);
          List<File> files = parseFiles(message);
          
          System.out.println("\nPROPS:");
          System.out.println("\t" + props);
          System.out.println("\nFILES:");
          for (File f : files) {
            System.out.println("\t" + f.getName() + ": size is " + f.length() + " bytes");
          }
        }
        catch (MessagingException me) {
          throw new MessageParseException(me);
        }
        return true;
      }
      
      public File saveFile(String filename, InputStream data) throws IOException {
        try {
          File f = new File(FilenameUtils.concat(System.getProperty("user.home"), filename));
          FileOutputStream fos = new FileOutputStream(f);

          int c;
          while ((c = data.read()) != -1) {
            fos.write((byte)c);
          }

          data.close();
          fos.close();
          
          return f;
        }
        catch (IOException e) {
          e.printStackTrace();
          throw e;
        }
      }
    };
    
    
    MailChecker mc = new MailChecker(1, 5, client, parser);
    mc.start();
 
    while (true) {
      BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
      try {
        String s = br.readLine();
        if (StringUtils.equals(s, "stop")) {
          mc.stop();
          break;
        }
      } catch (IOException ioe) {
        ioe.printStackTrace();
      }
    }
  }
}
