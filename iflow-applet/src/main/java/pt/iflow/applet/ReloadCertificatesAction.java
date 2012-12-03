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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import javax.swing.JComboBox;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.infosistema.crypto.CryptoUtils;

public class ReloadCertificatesAction implements ActionListener {
  private static Log log = LogFactory.getLog(ReloadCertificatesAction.class);

  private final JComboBox combo;

  public ReloadCertificatesAction(JComboBox combo) {
    this.combo = combo;
  }

  public void actionPerformed(ActionEvent e) {
    loadCertificateStore();
  }


  private void loadCertificateStore() {

    KeyStore[] stores = new KeyStore[0];
    try {
      stores = CryptoUtils.getSystemKeyStores();
    } catch (GeneralSecurityException e1) {
      log.warn("Error reading certificate stores", e1); //$NON-NLS-1$
      combo.removeAllItems();
      combo.addItem(newDummyEntry(Messages.getString("WinPDFSignInfo.20"))); //$NON-NLS-1$
      combo.setEnabled(true);
      combo.setSelectedIndex(0);
      return;
    }

    combo.removeAllItems();
    combo.addItem(newDummyEntry(Messages.getString("WinPDFSignInfo.26"))); //$NON-NLS-1$

    for(KeyStore store : stores)
      loadCertificateStore(store);

    // empty combo...
    int elemCount = combo.getItemCount();

    // if no key item was added (don't forget dummy entry)
    if(elemCount == 1) {
      combo.removeAllItems();
      combo.addItem(newDummyEntry(Messages.getString("WinPDFSignInfo.21"))); //$NON-NLS-1$
    } else if(elemCount == 2) {
      // second position because first is dummy entry
      combo.setSelectedIndex(1);
    } else {
      combo.setSelectedIndex(0);
    }
    combo.setEnabled(true);
  }


  private void loadCertificateStore(KeyStore store) {
    try {
      Enumeration<String> aliases = store.aliases();
      while(aliases.hasMoreElements()) {
        String alias = (String) aliases.nextElement();
        X509Certificate cert = (X509Certificate) store.getCertificate(alias);
        IDEntry idEntry = new IDEntry(alias, store);

        // ignore invalid certificates and filter by signature
        if(!(idEntry.isValid() && idEntry.isSignature())) continue;

        if(store.isKeyEntry(alias) && cert != null) {
          combo.addItem(idEntry);
        }
      }
    } catch (KeyStoreException e1) {
      log.error("Keystore invalida", e1); //$NON-NLS-1$
    }
  }

  private class DummyIdEntry extends IDEntry {

    private String msg;

    public DummyIdEntry(String msg) throws KeyStoreException {
      super(null, null);
      this.msg = msg;
    }

    public boolean isValid() {
      return false;
    }

    public String toString() {
      return msg;
    }
    
  }

  private IDEntry newDummyEntry(String msg) {
    try {
      return new DummyIdEntry(msg);
    } catch (KeyStoreException e) {
      return null;
    }
  }


}
