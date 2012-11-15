select 'drop table ' || table_name || ' cascade constraints purge;' from user_tables;
select 'drop sequence  ' ||  SEQUENCE_NAME || ';' from user_sequences;
select 'drop view ' || view_name || ' ;' from user_views;