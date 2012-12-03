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
package pt.iflow.applet.signer;

import java.awt.Component;

import javax.swing.JOptionPane;

import pt.iflow.applet.DynamicForm;
import pt.iflow.applet.ExtensionFileFilter;
import pt.iflow.applet.IVFile;
import pt.iflow.applet.Messages;
import pt.iflow.applet.SwingTask;
import pt.iflow.applet.WebClient;


/**
 * No signature
 * @author ombl
 *
 */
public class PKCS7SignatureImpl implements FileSigner {

  public IVFile sign(IVFile file) {
    return file;
  }

  public String verify(IVFile file) {
    return null;
  }

  public boolean isActive() {
    return true;
  }

  public int openDialog(Component parent, SwingTask task) {
    JOptionPane.showMessageDialog(parent, Messages.getString("PKCS7SignatureImpl.0")); //$NON-NLS-1$
    return CANCEL;
  }

  public DynamicForm getForm() {
    return null;
  }

  public String validateForm() {
    return null;
  }
  
  public void init(final WebClient webClient) {
  }

  public ExtensionFileFilter getFilter() {
    return null;
  }

}
