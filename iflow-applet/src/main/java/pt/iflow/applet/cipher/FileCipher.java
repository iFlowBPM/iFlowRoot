package pt.iflow.applet.cipher;

import pt.iflow.applet.DynamicFormProvider;
import pt.iflow.applet.FileAppletService;
import pt.iflow.applet.IVFile;


public interface FileCipher extends FileAppletService, DynamicFormProvider {
  
  IVFile encrypt(final IVFile file) throws CipherException;
  IVFile decrypt(final IVFile file) throws CipherException;
  
}
