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
