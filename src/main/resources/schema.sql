create TABLE IF NOT EXISTS users (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(512) NOT NULL,
  CONSTRAINT pk_user PRIMARY KEY (id),
  CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

create table if not exists items (
id BIGINT generated by default as identity not null,
name VARCHAR(255) not null,
description VARCHAR(255) not null,
available boolean default false,
owner_id integer not null references users(id),
constraint pk_item primary key (id)
);

create table if not exists bookings (
id BIGINT generated by default as identity not null,
start_date TIMESTAMP WITHOUT TIME zone not null,
end_date TIMESTAMP WITHOUT TIME zone not null,
item_id integer not null references items(id),
booker_id integer not null references users(id),
status varchar(15),
constraint pk_booking primary key (id)
);

create table if not exists requests (
id BIGINT generated by default as identity not null,
description varchar(255) not null,
requestor_id integer not null references users(id),
constraint pk_request primary key (id)
);

create table if not exists comments (
id BIGINT generated by default as identity not null,
text varchar(255) not null,
item_id integer not null references items(id),
author_id integer not null references users(id),
constraint pk_comment primary key (id)
);