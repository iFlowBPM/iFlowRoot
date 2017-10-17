package pt.iflow.applet;

import java.awt.Component;

import pt.iflow.applet.DynamicField.Type;

public class FileDialogProvider implements IVFileProvider {
  
  private String variable;
  private boolean replace;
  private IVFile file;
  
  public FileDialogProvider(String variable, boolean replace) {
    this.variable = variable;
    this.replace = replace;
    this.file = null;
  }

  public FileDialogProvider(String variable, boolean replace, IVFile file) {
    this.variable = variable;
    this.replace = replace;
    this.file = file;
  }

  public IVFile getFile(Component parent) {
    if (null == file) { 
      return file = chooseFile(parent);
    }
    return file;
  }

  public IVFile chooseFile(Component parent) {
    return SwingUtils.showOpenFileDialog(parent, 
        Messages.getString("FileDialogProvider.0"), //$NON-NLS-1$
        new ExtensionFileFilter(Messages.getString("FileDialogProvider.1"), new String[] { "PDF" }), //$NON-NLS-1$ //$NON-NLS-2$
        variable
    );
  }

  public boolean isErrorSet() {
    return false;
  }

  public boolean replaceFile() {
    return replace;
  }
  
  public DynamicField getDynamicField() {
    return new DynamicField(Type.FILE, Messages.getString("FileDialogProvider.3")) { //$NON-NLS-1$
      @Override
      public void setValue(Object value) {
        super.setValue(value);
        file = (IVFile) value;
      }
    };
  }
  
  public String validateForm() {
    return null == file?Messages.getString("FileDialogProvider.4"):null; //$NON-NLS-1$
  }
}
