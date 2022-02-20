create type membership_event_type as enum ('REGISTRATION', 'RENEW');

create table if not exists membership_events
(
    id         serial
        primary key,
    user_id    bigint                not null,
    timestamp  timestamp             not null,
    valid_to   timestamp             not null,
    event_type membership_event_type not null
);

create type pass_event_type as enum ('ENTRANCE', 'EXIT');

create table if not exists pass_events
(
    id         serial
        primary key,
    user_id    bigint          not null,
    timestamp  timestamp       not null,
    event_type pass_event_type not null
);
