package pt.iflow.api.utils.mail.parsers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.mail.Message;

public interface MessageParser {

  public boolean parse(Message message) throws MessageParseException;

  public File saveFile(String filename, InputStream data) throws IOException;
  
}
