-- spring oauth tables
    create table oauth_client_details (
        client_id varchar(256) primary key,
        resource_ids varchar(256),
        client_secret varchar(256),
        scope varchar(256),
        authorized_grant_types varchar(256),
        web_server_redirect_uri varchar(256),
        authorities varchar(256),
        access_token_validity integer,
        refresh_token_validity integer,
        additional_information varchar(4096),
        autoapprove varchar(256)
    );

    create table oauth_client_token (
        token_id varchar(256),
        token bytea,
        authentication_id varchar(256),
        user_name varchar(256),
        client_id varchar(256)
    );

    create table oauth_access_token (
        token_id varchar(256) primary key,
        refresh_id varchar(256),
        user_name varchar(256),
        client_id varchar(256),
        expires_at timestamp,
        creation timestamp,
        authentication bytea
    );

    create table oauth_refresh_token (
        refresh_id varchar(256) primary key,
        token_id varchar(256),
        user_name varchar(256),
        client_id varchar(256),
        expires_at timestamp
    );

    create table oauth_code (
        code varchar(256),
        authentication bytea
    );

    create table oauth_approvals (
        userid varchar(256),
        clientid varchar(256),
        scope varchar(256),
        status varchar(10),
        expiresat timestamp,
        lastmodifiedat timestamp
    );

-- Creation of common tables


    create table tbs_base_owner (
       id  bigserial not null,
        create_time timestamp,
        delete_time timestamp,
        last_updated timestamp,
        description varchar(1000),
        email varchar(100) not null,
        github_account varchar(100),
        name varchar(60) not null,
        nickname varchar(60) not null,
        twitter_account varchar(100),
        primary key (id)
    );

    create table tbs_organization (
       id int8 not null,
        owner_id int8,
        primary key (id)
    );

    create table tbs_organization_user (
       id  bigserial not null,
        create_time timestamp,
        delete_time timestamp,
        last_updated timestamp,
        active boolean not null,
        invite_date timestamp,
        role int4,
        organization_id int8,
        user_id int8,
        primary key (id)
    );

    create table tbs_package_data (
       id  bigserial not null,
        create_time timestamp,
        delete_time timestamp,
        last_updated timestamp,
        name varchar(100) not null,
        group_id int8,
        primary key (id)
    );

    create table tbs_package_file (
       id  bigserial not null,
        create_time timestamp,
        delete_time timestamp,
        last_updated timestamp,
        length int8,
        name varchar(200),
        uploaded boolean not null,
        release_id int8,
        primary key (id)
    );

    create table tbs_package_group (
       id  bigserial not null,
        create_time timestamp,
        delete_time timestamp,
        last_updated timestamp,
        description varchar(1000) not null,
        name varchar(200) not null,
        owner_id int8,
        primary key (id)
    );

    create table tbs_package_release (
       id  bigserial not null,
        create_time timestamp,
        delete_time timestamp,
        last_updated timestamp,
        description varchar(1000),
        documentation_url varchar(2083),
        issues_url varchar(2083),
        release_date timestamp,
        source_url varchar(2083),
        version varchar(1000) not null,
        package_data_id int8,
        primary key (id)
    );

    create table tbs_user (
       activation_code varchar(200),
        activation_expires timestamp,
        birth_date date,
        dt_locked timestamp,
        last_name varchar(120) not null,
        password varchar(200) not null,
        recovery_code varchar(200),
        recovery_expires timestamp,
        id int8 not null,
        primary key (id)
    );

    create table tbs_user_privacy_ops (
       id  bigserial not null,
        create_time timestamp,
        delete_time timestamp,
        last_updated timestamp,
        show_email boolean not null,
        show_github boolean not null,
        show_name boolean not null,
        show_twitter boolean not null,
        user_id int8,
        primary key (id)
    );

    alter table tbs_base_owner
       add constraint uk_org_mail unique (email);

    alter table tbs_base_owner
       add constraint uk_org_nick unique (nickname);

    alter table tbs_organization_user
       add constraint uk_orguser_org_user unique (organization_id, user_id);

    alter table tbs_package_data
       add constraint idx_packdata_groupname unique (group_id, name);

    alter table tbs_package_file
       add constraint idx_packagefile_releasename unique (release_id, name);
create index idx_group_owner on tbs_package_group (owner_id);

    alter table tbs_package_group
       add constraint idx_group_name unique (name);

    alter table tbs_package_release
       add constraint uk_packagerelease_version unique (package_data_id, version);

    alter table tbs_user_privacy_ops
       add constraint uk_privacy_user unique (user_id);

    alter table tbs_organization
       add constraint FKqr6r6b76bu2ww5ooll9km8afa
       foreign key (owner_id)
       references tbs_user;

    alter table tbs_organization
       add constraint FKsww7cqi9x7gfdb0154pkqs78f
       foreign key (id)
       references tbs_base_owner;

    alter table tbs_organization_user
       add constraint FKk79l5nmdx6ckfhh9b6od05rku
       foreign key (organization_id)
       references tbs_organization;

    alter table tbs_organization_user
       add constraint FK1y5sxk2c4y4ns2yrc9s0kodro
       foreign key (user_id)
       references tbs_user;

    alter table tbs_package_data
       add constraint FK457im5iigqoim3rkosjjrt2kn
       foreign key (group_id)
       references tbs_package_group;

    alter table tbs_package_file
       add constraint FK8h2ao5ub47o7eq4oolajhwant
       foreign key (release_id)
       references tbs_package_release;

    alter table tbs_package_group
       add constraint FK5t99k4aw0l3ui57mj4c6cka37
       foreign key (owner_id)
       references tbs_base_owner;

    alter table tbs_package_release
       add constraint FKkr02pcn2pkab9gkogrfagbpcf
       foreign key (package_data_id)
       references tbs_package_data;

    alter table tbs_user
       add constraint FKnqicawty71ly8e5p12jhumqkh
       foreign key (id)
       references tbs_base_owner;

    alter table tbs_user_privacy_ops
       add constraint FKgalxeyt6odd5dhcdv52soajwr
       foreign key (user_id)
       references tbs_user;
