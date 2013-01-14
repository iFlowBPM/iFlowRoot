package pt.iflow.utils.scanner;

import java.awt.Component;
import java.io.File;


public interface FileSigner extends FileAppletService {
  
  File sign(final File file);
  String verify(final File file);
  
  void loadSignature(Component parent);
  
}
