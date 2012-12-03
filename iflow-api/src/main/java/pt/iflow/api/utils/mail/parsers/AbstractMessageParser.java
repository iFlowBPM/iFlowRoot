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
package pt.iflow.api.utils.mail.parsers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

public abstract class AbstractMessageParser implements MessageParser {


  public abstract boolean parse(Message message) throws MessageParseException;

  public abstract File saveFile(String filename, InputStream data) throws IOException;

  public List<File> parseFiles(Message message) throws MessageParseException {
    List<File> files = new ArrayList<File>();

    try {
      if (message.isMimeType("multipart/*")) {
        Multipart mp = (Multipart)message.getContent();
        for (int i = 0; i < mp.getCount(); i++) {
          Part bp = mp.getBodyPart(i);
          String disposition = bp.getDisposition();
          if (StringUtils.equalsIgnoreCase(disposition, Part.ATTACHMENT)) {
            files.add(saveFile(bp.getFileName(), bp.getInputStream()));          
          }
        }
      }  
    }
    catch (Exception e) {
      throw new MessageParseException(e);
    }
    return files;
  }

  public String getText(Part p) throws MessageParseException {

    try {
      if (p.isMimeType("text/*")) {

        String s = getStringContent(p);

        if (p.isMimeType("text/html")) {
          return removeHtmlTags(s);
        }
        if (p.isMimeType("text/xml")) {
          return StringEscapeUtils.unescapeXml(s);
        }

        return s;
      }

      if (p.isMimeType("multipart/alternative")) {
        // prefer html text over plain text
        Multipart mp = (Multipart)p.getContent();
        String text = null;
        for (int i = 0; i < mp.getCount(); i++) {
          Part bp = mp.getBodyPart(i);
          if (bp.isMimeType("text/plain")) {
            if (text == null)
              text = getText(bp);
            continue;
          } 
          else if (bp.isMimeType("text/html") || bp.isMimeType("text/xml")) {
            String s = getText(bp);
            if (s != null) {
              return s;
            }
          } 
          else {
            return getText(bp);
          }
        }
        return text;
      } 
      else if (p.isMimeType("multipart/*")) {
        Multipart mp = (Multipart)p.getContent();
        for (int i = 0; i < mp.getCount(); i++) {
          Part bp = mp.getBodyPart(i);
          String disposition = bp.getDisposition();
          if (StringUtils.equals(disposition, Part.ATTACHMENT)) {
            continue;
          }
          String s = getText(bp);
          if (s != null)
            return s;
        }
      }
    }
    catch (Exception e) {
      throw new MessageParseException(e);
    }
    return null;
  }

  private String removeHtmlTags(String str) {
    if (StringUtils.isNotEmpty(str)) {
      str = StringEscapeUtils.unescapeHtml(str);
      str = str.replaceAll(Pattern.quote("<br>"), "\n");
    }
    return str;
  }
  
  private String getStringContent(Part part) throws MessagingException, IOException  {
    try {
      return (String)part.getContent();
    } 
    catch (UnsupportedEncodingException uex) {
      StringBuilder sb = new StringBuilder();
      InputStream is = part.getInputStream();
      int c;
      while ((c = is.read()) != -1) {
        sb.append((char)c);
      }

      return sb.toString();
    } 
  }
 
  
}
