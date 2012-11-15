

João coloquei no SVN em CMAbrantes\Webservices o código dos blocos.

 
Para configurares deves alterar o ficheiro webservices.properties.

 

Existem 3 blocos de Criação de registo e um de leitura de registo:

 

BlockCreateRegistoAssiduidade: usa o cliente gerado pelo eclipse e funciona com o WS dummy (é o primeiro bloco que foi testado)

JAVA 6: BlockCreateRegistoAssiduidade1: usa o código retirado de http://blogs.msdn.com/b/freddyk/archive/2010/01/19/connecting-to-nav-web-services-from-java.aspx.

BlockCreateRegistoAssiduidade2: usa o código retirado de http://hc.apache.org/httpcomponents-client-ga/tutorial/html/authentication.html. Usa o NTLM e é o que tem mais probabilidade de funcionar.

                                                               (para usar este tens que alterar o pom.xml do iflow-web)

 

Para cada um destes existe uma biblioteca para o editor (cidadela.xml, cidadela1.xml, cidadela2.xml) e um fluxo de teste (CriarAssiduidade.xml, CriarAssiduidade1.xml, CriarAssiduidade2.xml)

 

O CriarAssiduidade.xml é o mesmo que já te enviei antes e não precisas de reinstalar.

 