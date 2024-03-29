drop table if exists director_entity;
drop table if exists director_entity_movies;
drop table if exists movie_entity;
drop table if exists screening_entity;
drop sequence if exists hibernate_sequence;
create sequence hibernate_sequence start with 1 increment by 1;
create table director_entity
(
    id          bigint not null,
    family_name varchar(100),
    given_name  varchar(100),
    primary key (id)
);
create table director_entity_movies
(
    director_entity_id bigint not null,
    movies_id          bigint not null
);
create table movie_entity
(
    id          bigint  not null,
    title       varchar(150),
    year        integer not null check (year <= 2500 AND year >= 1880),
    director_id bigint,
    primary key (id)
);
create table screening_entity
(
    id       bigint not null,
    room     varchar(255),
    time     timestamp,
    movie_id bigint,
    primary key (id)
);
alter table director_entity_movies
    add constraint UK_nlxby8bhgbf2nqjdgw8uk5qp0 unique (movies_id);
alter table director_entity_movies
    add constraint FKavh2hu1a1vwaf4ov8hwvahvlt foreign key (movies_id) references movie_entity;
alter table director_entity_movies
    add constraint FK29on5ds31atcnjgtnqhds50sc foreign key (director_entity_id) references director_entity;
alter table movie_entity
    add constraint FKbvs9legr1vasnekr8q39phwni foreign key (director_id) references director_entity;
alter table screening_entity
    add constraint FK5l8i3imqsj6out4on14wlj7d7 foreign key (movie_id) references movie_entity;
