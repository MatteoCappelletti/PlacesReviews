
create table "user" (
    id serial,

    username text not null,
    password_hash text not null,
    role text not null,

    created_at timestamp not null default current_timestamp,

    constraint pk_user primary key (id),
    constraint uq_user_username unique (username),
    constraint ck_user_role_accepted_role check ( role in ('user', 'moderator') )
);

create table place (
    id serial,
    user_id int not null,

    name text not null,
    description text not null,
    city text not null,
    address text not null,

    latitude double precision not null, -- more correct point type
    longitude double precision not null, -- more correct point type

    created_at timestamp not null default current_timestamp,

    constraint pk_place primary key (id),
    constraint fk_place_user foreign key (user_id) references "user" (id)
);

create table review (
    id serial,
    reviewer_id int not null,
    place_id int not null,
    approver_id int default null,

    rating int not null,
    description text not null default '',

    created_at timestamp not null default current_timestamp,

    constraint pk_review primary key (id),
    constraint fk_review_user_reviewer foreign key (reviewer_id) references "user" (id),
    constraint fk_review_place foreign key (place_id) references place (id),
    constraint fk_review_user_approver foreign key (approver_id) references "user" (id),
    constraint ck_review_rating_range check ( rating >= 1 and rating <= 5 )
);

create table media (
    id serial,
    place_id int not null,

    path text not null,

    created_at timestamp not null default current_timestamp,

    constraint pk_media primary key (id),
    constraint fk_media_place foreign key (place_id) references place (id)
);

create table category (
    id serial,
    name text not null,

    created_at timestamp not null default current_timestamp,

    constraint pk_category primary key (id),
    constraint uq_category_name unique (name)
);

create table place_category (
    place_id int,
    category_id int,

    constraint pk_place_category primary key (place_id, category_id),
    constraint fk_place_category_place foreign key (place_id) references place (id),
    constraint fk_place_category_category foreign key (category_id) references category (id)
);
