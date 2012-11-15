package pt.iflow.api.utils.mail.security;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

public class TrustedTrustManager implements X509TrustManager {

  public X509Certificate[] getAcceptedIssuers() {
    return new X509Certificate[0];
  }

  public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
  }

  public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
  }

}
