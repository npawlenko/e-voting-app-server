alter table votes
    add column from_system_account boolean default true;
alter table votes
    add column non_system_account_email varchar(64) null;

create table if not exists vote_tokens
(
    id      serial primary key,
    email   varchar(64)                  not null,
    token   text                         not null,
    poll_id bigint references polls (id) not null
);
alter table vote_tokens
    alter column id set data type BIGINT;

create table if not exists reset_tokens
(
    id         serial primary key,
    token      text                         not null,
    expires_at timestamp                    not null,
    user_id    bigint references users (id) not null
);
alter table reset_tokens
    alter column id set data type BIGINT;