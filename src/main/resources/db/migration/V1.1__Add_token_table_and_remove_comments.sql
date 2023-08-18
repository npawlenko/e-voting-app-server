create table if not exists tokens (
    id SERIAL PRIMARY KEY,
    access_token VARCHAR(1000) NOT NULL,
    refresh_token VARCHAR(1000) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    user_id INTEGER REFERENCES users(id)
);

comment on column users.password is NULL;