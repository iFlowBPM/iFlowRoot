package pt.iflow.utils.scanner;

import java.awt.Component;
import java.io.File;

public class XAdESSigner implements FileSigner {
  
  public void loadSignature(Component parent) {
    throw new RuntimeException("Not implemented"); //$NON-NLS-1$
  }

  public File sign(File file) {
    throw new RuntimeException("Not implemented"); //$NON-NLS-1$
  }

  public String verify(File file) {
    throw new RuntimeException("Not implemented"); //$NON-NLS-1$
  }

  public boolean isActive() {
    throw new RuntimeException("Not implemented"); //$NON-NLS-1$
  }
  
}
