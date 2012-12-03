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

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;

import pt.iflow.api.utils.mail.parsers.MessageParser;

public interface MailClient {

  public void setDebug(boolean debug);
  
  public void connect() throws MessagingException;
  public boolean isConnected();
  public void disconnect() throws MessagingException;
  
  public Folder getFolder(String folderName) throws MessagingException;
  
  public boolean checkNewMail() throws MessagingException;
  
  public void readUnreadMessages(MessageParser messageParser) throws MessagingException;
  
  public Message[] getFolderUnreadMessages(Folder folder) throws MessagingException;  
  
  public void setInboxFolder(String inboxFolder);
  public void setTopFolder(String topFolder);
  
  public String getId();
}
