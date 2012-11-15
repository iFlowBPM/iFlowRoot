Os ficheiros .jar da applet são assinados automáticamente.
Para tal é necessário ter o ficheiro tools.jar no classpath do tomcat

AppletServletHelper é gerado através de uma execução maven.
Isto deverá acontecer automáticamente na construção do projecto, mas caso isto
não aconteça basta correr "mvn package" e a classe aparecerá em "./target/generated-sources"

NOTA: se no arranque do tomcat nao for encontrado o resource applet.vm, verificar no buildpath
do projecto se o source folder src/main/resources está a ser excluido (remover o excludes */*)

Foram adicionados novos parametros à applet (ver applet.vm) que permitem aumentar
o tamanho de memória usada pela JVM. Estes parametros requerem o plugin de nova
geração, disponível a partir da versão 1.6.0_10 do Java e só funcionam no 
firefox 3 ou superior ou internet explorer 6 ou superior. Caso estas condições
não se verifiquem, a applet funciona com os valores por omissão: partilhará a 
memória máxima de 64MB com todas as applets carregadas no momento.
Mais info: http://java.sun.com/developer/technicalArticles/javase/newapplets/

Configurar plugin java de nova geração no UBUNTU

1. Instalar java 6 
# sudo apt-get install sun-java6-bin sun-java6-jre

2. Seleccionar o plugin de nova geração
# sudo update-alternatives -set firefox-javaplugin.so /usr/lib/jvm/java-6-sun/jre/lib/i386/libnpjp2.so
# sudo update-alternatives -set mozilla-javaplugin.so /usr/lib/jvm/java-6-sun/jre/lib/i386/libnpjp2.so
# sudo update-alternatives -set xulrunner-1.9-javaplugin.so /usr/lib/jvm/java-6-sun/jre/lib/i386/libnpjp2.so
# sudo update-alternatives -set xulrunner-javaplugin.so /usr/lib/jvm/java-6-sun/jre/lib/i386/libnpjp2.so

3. Reiniciar o firefox
