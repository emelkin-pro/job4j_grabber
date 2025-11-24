create table if not exists post (
id SERIAL PRIMARY KEY  ,
name text,
link text UNIQUE,
created TIMESTAMP
);