@echo.
@echo Testing...
java -classpath ..\dist\ldapquery_flat.jar;..\dist\ldapquery_flat.jar;lib\log4j-1.2.12.jar;lib\common.jar pt.totta.ldap.writerApi.SetPersonBankInfo
@echo Done.