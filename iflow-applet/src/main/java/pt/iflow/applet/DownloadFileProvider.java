package pt.iflow.applet;

import java.awt.Component;

public class DownloadFileProvider implements IVFileProvider {
  
  private final WebClient webClient;
  
  public DownloadFileProvider(final WebClient webClient) {
    this.webClient = webClient;
  }

  public IVFile getFile(Component parent) {
    return webClient.getDocument(parent);
  }

  public IVFile chooseFile(Component parent) {
    return null;
  }
  
  public boolean isErrorSet() {
    return false;
  }

  public boolean replaceFile() {
    return true;
  }
  
  public DynamicField getDynamicField() {
    return null;
  }
  public String validateForm() {
    return null;
  }
}
