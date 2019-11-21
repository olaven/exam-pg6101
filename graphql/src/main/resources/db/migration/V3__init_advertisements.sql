drop table if exists advertisement_entity;
drop table if exists reservation_entity;
drop sequence if exists hibernate_sequence;
create sequence hibernate_sequence start with 1 increment by 1;
create table advertisement_entity (id bigint not null, message varchar(255), vote_count integer not null check (vote_count>=0), primary key (id));