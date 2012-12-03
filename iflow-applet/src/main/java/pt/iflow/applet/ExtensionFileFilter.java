/*****************************************************************************************
    Infosistema iFlow - workflow and BPM platform
    Copyright(C) 2002-2012 Infosistema, S.A.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

    www.infosistema.com
    iflow@infosistema.com
    Av. Jose Gomes Ferreira, 11 3rd floor, s.34
    Miraflores
    1495-139 Alges Portugal
****************************************************************************************/
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
        if (path.endsWith(extension) && (path.charAt(path.length() - extension.length() - 1)) == '.')
          return true;
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
