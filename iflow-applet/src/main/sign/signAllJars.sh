./signJar.sh

jarsigner -keystore store -storepass iknow256 -signedjar WebContent/Form/SignedScannerTwain.jar \
~/.m2/repository/iknow-local-libs/scanner-twain/1.0/scanner-twain-1.0.jar  iKnowCert

jarsigner -keystore store -storepass iknow256 -signedjar WebContent/Form/SignedScannerSane.jar \
~/.m2/repository/iknow-local-libs/scanner-sane/1.0/scanner-sane-1.0.jar iKnowCert


jarsigner -keystore store -storepass iknow256 -signedjar WebContent/Form/SignedTiff.jar \
~/.m2/repository/iknow-local-libs/tiff/1.0/tiff-1.0.jar iKnowCert


