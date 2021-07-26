create table users
(
    id       varchar(10) primary key,
    name     varchar(10) not null,
    password varchar(10) not null,
    email varchar(50) not null,
    level int not null,
    login int not null,
    recommend int not null
)