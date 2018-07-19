package pt.iflow.applet;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.filechooser.FileFilter;

public class ExtensionFileFilter extends FileFilter implements FilenameFilter {

  private String[] extensions;
  private String description;

  public ExtensionFileFilter(String description, String[] fileExtensions) {
    if (description == null)
      throw new NullPointerException();
    this.description = description;
    List<String> extts = new ArrayList<String>();
    for (int i = 0; i < fileExtensions.length; i++) {
      if (fileExtensions[i] == null)
        continue;
      extts.add(fileExtensions[i].toLowerCase());
    }
    if (extts.isEmpty())
      throw new NullPointerException();
    this.extensions = (String[]) extts.toArray(new String[extts.size()]);
  }

  public boolean accept(File file) {
    if (file.isDirectory()) {
      return true;
    } else {
      String path = file.getAbsolutePath().toLowerCase();
      for (int i = 0; i < extensions.length; i++) {
        String extension = extensions[i];
        
        if (path!=null && path.length() >= extension.length()) {
	        String cmp = path.substring(path.length() - extension.length());
	        if (cmp.equalsIgnoreCase(extension) && (path.charAt(path.length() - extension.length() - 1)) == '.')
	          return true;
	        }
      }
      return false;
    }
  }

  public String getDescription() {
    return this.description;
  }

  public boolean accept(File dir, String name) {
    return accept(new File(dir, name));
  }

}
