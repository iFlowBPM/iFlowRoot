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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;

import org.apache.commons.lang.StringUtils;

public abstract class AbstractPropertiesMessageParser extends AbstractMessageParser {

  public Properties parseProperties(Message message) throws MessageParseException {
    Properties props = new Properties();
    try {    
      if (message.isMimeType("multipart/*")) {

        Multipart mp = (Multipart)message.getContent();
        for (int i = 0; i < mp.getCount(); i++) {
          Part bp = mp.getBodyPart(i);

          String disposition = bp.getDisposition();
          if (!StringUtils.equalsIgnoreCase(disposition, Part.ATTACHMENT)) {
            loadProperties(props, bp);
          }
        }
      }
      else {
        loadProperties(props, message);
      }
    }
    catch (Exception e) {
      throw new MessageParseException(e);
    }
    return props;
  }

  private void loadProperties(Properties props, Part part) throws MessageParseException, MessagingException, IOException {
    InputStream contentStream = null;
    
    String content = getText(part);
    if (StringUtils.isNotEmpty(content)) {
      contentStream = new ByteArrayInputStream(content.getBytes());
    }

    if (contentStream == null) {
      contentStream = part.getInputStream();
    }
    
    props.load(contentStream);
  }
  
  
}
