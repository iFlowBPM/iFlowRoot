package pt.iflow.applet;

public interface FileAppletService {

  boolean isActive();
  
  void init(final WebClient webClient);
  
}
