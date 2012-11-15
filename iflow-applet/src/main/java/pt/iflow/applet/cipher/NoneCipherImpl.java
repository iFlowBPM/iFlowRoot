package pt.iflow.applet.cipher;

import java.awt.Component;

import pt.iflow.applet.DynamicForm;
import pt.iflow.applet.IVFile;
import pt.iflow.applet.WebClient;

/**
 * No encryption
 * 
 * @author ombl
 * 
 */
public class NoneCipherImpl implements FileCipher {

  public void loadSignature(Component parent) {
  }

  public boolean isActive() {
    return true;
  }

//  public int openDialog(Component parent, SwingTask task) {
//    task.execute();
//    return OK;
//  }
//
  public IVFile decrypt(IVFile file) throws CipherException {
    return file;
  }

  public IVFile encrypt(IVFile file) throws CipherException {
    return file;
  }

  public DynamicForm getForm() {
    return null;
  }
  
  public String validateForm() {
    return null;
  }
  
  public void init(WebClient webClient) {
  }

}
