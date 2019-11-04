drop table if exists director_entity;
drop table if exists director_entity_movies;
drop table if exists movie_entity;
drop sequence if exists hibernate_sequence;
create sequence hibernate_sequence start with 1 increment by 1;
create table director_entity (id bigint not null, family_name varchar(100), given_name varchar(100), primary key (id));
create table director_entity_movies (director_entity_id bigint not null, movies_id bigint not null);
create table movie_entity (id bigint not null, title varchar(150), year integer not null check (year<=2500 AND year>=1880), director_id bigint, primary key (id));
