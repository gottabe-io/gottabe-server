
    create table tbs_package_release_review (
       id  bigserial not null,
        create_time timestamp,
        delete_time timestamp,
        last_updated timestamp,
        rate int4 not null,
        review varchar(1000),
        review_dislikes int4 not null,
        review_likes int4 not null,
        release_id int8 not null,
        user_id int8 not null,
        primary key (id)
    );

    create table tbs_review_like (
       id  bigserial not null,
        create_time timestamp,
        delete_time timestamp,
        last_updated timestamp,
        rate int4 not null,
        review_id int8 not null,
        user_id int8 not null,
        primary key (id)
    );
create index idx_pkgreleasereview_user on tbs_package_release_review (release_id, user_id);

    alter table tbs_review_like
       add constraint idx_reviewlike_user unique (review_id, user_id);

    alter table tbs_package_release_review 
       add constraint FKba0sc2d9q1m51r59e3b0t2jrn 
       foreign key (release_id) 
       references tbs_package_release;

    alter table tbs_package_release_review 
       add constraint FKny3di8qbifsivn5t1tsal3v41 
       foreign key (user_id) 
       references tbs_user;

    alter table tbs_review_like 
       add constraint FKoplkotrg9kopf2hxoarhp80o2 
       foreign key (review_id) 
       references tbs_package_release_review;

    alter table tbs_review_like 
       add constraint FKe9ahxaq3bhengm2c57q4kx5le 
       foreign key (user_id) 
       references tbs_user;
