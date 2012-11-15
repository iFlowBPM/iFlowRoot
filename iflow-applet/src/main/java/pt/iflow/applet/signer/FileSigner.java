package pt.iflow.applet.signer;

import pt.iflow.applet.DynamicFormProvider;
import pt.iflow.applet.ExtensionFileFilter;
import pt.iflow.applet.FileAppletService;
import pt.iflow.applet.IVFile;


public interface FileSigner extends FileAppletService, DynamicFormProvider {
  
  IVFile sign(final IVFile file) throws SignerException;
  String verify(final IVFile file) throws SignerException;
  
  ExtensionFileFilter getFilter();
  
}
