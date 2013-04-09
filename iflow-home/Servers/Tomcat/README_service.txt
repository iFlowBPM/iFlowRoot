Instalar nova instância/serviço do Tomcat:

Criar a seguinte estrutura de directórios, copiados a partir do 
directório de instalação do Tomcat:
    - conf
    - logs
    - temp
    - webapps
    - work

No directório "conf", alterar os portos configurados no ficheiro 
"server.xml" de forma a que não entrem em conflito com aplicações
existentes. Os portos a alterar são:
 - <Server port="">;
 - <Connector port="">; (HTTP e AJP)

Modificar o ficheiro "service.bat" de forma a corrigir os PATHs para
as aplicações/instalações/etc. Consultar:
http://tomcat.apache.org/tomcat-6.0-doc/printer/windows-service-howto.html

executar o ficheiro "service.bat" para criar o serviço "Apache Tomcat iFlow":
> service.bat install iFlow

Para remover o serviço:
> service.bat remove iFlow

