-- facturas.xml
create table tipo_facturas (tipoid integer primary key, tipotxt varchar(64));
insert into tipo_facturas values (1, 'Material Informático');
insert into tipo_facturas values (2, 'Economato');
insert into tipo_facturas values (3, 'Despesas de Representação');

-- stock mgmt (oracle)
create table stock_data (
  id integer not null,
  name varchar2(128),
  price number(15,3),
  quantity integer,
  constraint stock_data_pk primary key (id)
);
create sequence seq_stock_data;
