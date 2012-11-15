/**
 * @(#)WriterSetup.java Oct 19, 2007
 *
 * Created by Infosistema, Sistemas de Informação S. A.
 * Av. José Gomes Ferreira, 11, Sala 42, Miraflores, 1495-139 Algés
 *
 * To ISBAN
 */
package pt.totta.ldap.writerApi;

import pt.totta.ldap.setup.Setup;

/**
 * @author Luis Sardinha - Infosistema
 *
 */
public class WriterSetup extends Setup {

    private static final String SECURITYPRINCIPAL = "uid=api_ldap, ou=Sistemas, ou=Utilizadores, o=Grupo Santander Totta, c=pt, o=Grupo Santander";
    private static final String SECURITYCREDENTIALS = "api_ldap_pre";

    /**
     * This method returns the SecurityPrincipal setup parameter.
     * <p>
     * @return The security principal.
     */
    public String getSecurityPrincipal()
    {
      return SECURITYPRINCIPAL;
    }

    /**
     * This method returns the SecurityCredentials setup parameter.
     * <p>
     * @return The security credentials.
     */
    public String getSecurityCredentials()
    {
      return SECURITYCREDENTIALS;
    }


}
