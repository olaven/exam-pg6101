drop table if exists reservation_entity;
drop sequence if exists hibernate_sequence;
create sequence hibernate_sequence start with 1 increment by 1;
create table reservation_entity (id bigint not null, screeningid bigint not null, username varchar(200), primary key (id));