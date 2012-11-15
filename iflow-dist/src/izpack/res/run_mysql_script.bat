@echo off
mysql ${database.schema} --user=${databaseadmin.username} --password=${databaseadmin.password} -e "source ${INSTALL_PATH}/db-install/${database.type}/create_database.sql"