create table if not exists user_groups (
    id serial primary key,
    name varchar(64) not null,
    owner_id integer not null references users(id) on delete cascade
);

create table if not exists user_group_association (
    user_id integer not null references users(id) on delete cascade,
    group_id integer not null references user_groups(id) on delete cascade,
    primary key(user_id, group_id)
);

alter table polls
add column user_group_id integer not null references user_groups(id);
