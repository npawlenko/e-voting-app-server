create table if not exists roles
(
    id SERIAL PRIMARY KEY,
    name VARCHAR(20)
);

create table if not exists users
(
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(64) NOT NULL,
    last_name VARCHAR(64) NOT NULL,
    email VARCHAR(64) NOT NULL UNIQUE,
    password VARCHAR(128) NOT NULL, --sha512
    role_id INTEGER REFERENCES roles(id) NOT NULL
);
comment on column users.password is 'hashed with SHA512';

create table if not exists polls
(
    id SERIAL PRIMARY KEY,
    question VARCHAR(64) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    creator_id INTEGER NOT NULL REFERENCES users(id)
);

create table if not exists poll_answers
(
    id SERIAL PRIMARY KEY,
    answer VARCHAR(64) NOT NULL,
    poll_id INTEGER NOT NULL REFERENCES polls(id),
    UNIQUE(id, poll_id)
);

create table if not exists votes
(
    id SERIAL PRIMARY KEY,
    casted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    answer_id INTEGER NOT NULL REFERENCES poll_answers(id),
    poll_id INTEGER NOT NULL REFERENCES polls(id),
    voter_id INTEGER NOT NULL REFERENCES users(id)
);