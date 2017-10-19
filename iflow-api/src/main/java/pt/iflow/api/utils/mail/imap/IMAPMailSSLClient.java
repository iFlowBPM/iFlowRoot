package pt.iflow.api.utils.mail.imap;

import java.security.Security;
import java.util.Properties;

import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import pt.iflow.api.utils.mail.MailConfig;

import com.sun.mail.imap.IMAPSSLStore;

public class IMAPMailSSLClient extends IMAPMailClient {

	public IMAPMailSSLClient(MailConfig config) throws Exception {
		this(config.getHost(), (config.getPort() <= 0 ? DEF_IMAP_SSL_PORT : config.getPort()), config.getUser(),
				config.getPass());

		setInboxFolder(config.getInbox());
		if (config.getSubsFolders() != null) {
			String[] subsFolders = new String[config.getSubsFolders().size()];
			subsFolders = config.getSubsFolders().toArray(subsFolders);
			subscribeFolders(subsFolders);
		}
	}

	public IMAPMailSSLClient(String asHost, String user, byte[] password) throws Exception {
		this(asHost, DEF_IMAP_SSL_PORT, user, password);
	}

	public IMAPMailSSLClient(String asHost, int anPort, String user, byte[] password) throws Exception {
		super(asHost, anPort, user, password);

		Properties props = System.getProperties();

		super.init(props);
	}

	private void initSSLProps() {
		Security.addProvider(new BouncyCastleProvider());
		Security.setProperty("ssl.SocketFactory.provider", "TrustedSSLSocketFactory");

		Properties props = System.getProperties();

		props.setProperty("mail.imap.socketFactory.class", SSL_FACTORY);
		props.setProperty("mail.imap.socketFactory.fallback", "false");
		props.setProperty("mail.imap.socketFactory.port", String.valueOf(this._nPort));
		props.setProperty("mail.store.protocol", "imaps");

		super.init(props);

	}

	private void clearSSLProps() {
		Security.removeProvider(new BouncyCastleProvider().getName());

		Properties props = System.getProperties();

		super.init(props);
	}

	@Override
	protected Store getStore(Session session, int attemptNbr) throws NoSuchProviderException {
		Store retObj = null;
		if (attemptNbr == 1) {
			retObj = new IMAPSSLStore(session,
					new URLName("imap://" + _authenticator.getPasswordAuthentication().getUserName()));
		} else if (attemptNbr == 2) {
			initSSLProps();
			retObj = session.getStore("imap");

			clearSSLProps();
		}
		return retObj;
	}
}
