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

  public IVFile getFile(Component parent) {
    if(null == file) { 
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
  
//  public DynamicField getFormComponent() {
//    final JPanel panel = new JPanel();
//    JButton button = new JButton("...");
//    button.setToolTipText("Procurar ficheiro");
//    button.addActionListener(new ActionListener() {
//      public void actionPerformed(ActionEvent e) {
//        // reset file
//        IVFile oldf = file;
//        file = null;
//        IVFile f = getFile(panel);
//        if(null == f) file = oldf;
//        else file = f;
//      }
//    });
//    // TODO add read only text area and button
//    return panel;
//  }
  
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
