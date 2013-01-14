package pt.iflow.applet.signer;

import java.awt.Component;

import javax.swing.JOptionPane;

import pt.iflow.applet.DynamicForm;
import pt.iflow.applet.ExtensionFileFilter;
import pt.iflow.applet.IVFile;
import pt.iflow.applet.Messages;
import pt.iflow.applet.SwingTask;
import pt.iflow.applet.WebClient;


/**
 * No signature
 * @author ombl
 *
 */
public class PKCS7SignatureImpl implements FileSigner {

  public IVFile sign(IVFile file) {
    return file;
  }

  public String verify(IVFile file) {
    return null;
  }

  public boolean isActive() {
    return true;
  }

  public int openDialog(Component parent, SwingTask task) {
    JOptionPane.showMessageDialog(parent, Messages.getString("PKCS7SignatureImpl.0")); //$NON-NLS-1$
    return CANCEL;
  }

  public DynamicForm getForm() {
    return null;
  }

  public String validateForm() {
    return null;
  }
  
  public void init(final WebClient webClient) {
  }

  public ExtensionFileFilter getFilter() {
    return null;
  }

}
