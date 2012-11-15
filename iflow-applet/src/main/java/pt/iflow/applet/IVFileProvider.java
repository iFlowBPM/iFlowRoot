package pt.iflow.applet;

import java.awt.Component;

public interface IVFileProvider {

  IVFile getFile(Component parent);
  
  IVFile chooseFile(Component parent);
  boolean isErrorSet();
  boolean replaceFile();
  
  DynamicField getDynamicField();
  String validateForm();
  
}
