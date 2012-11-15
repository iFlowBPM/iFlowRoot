cd target/classes/
jar cvf ../../applet.jar pt/iknow/utils/scanner/
cd ../../
jarsigner -keystore store -storepass iknow256 -signedjar WebContent/Form/SignedApplet.jar applet.jar iKnowCert
rm -f applet.jar
