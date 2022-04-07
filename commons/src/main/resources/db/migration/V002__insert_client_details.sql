insert into oauth_client_details (
        client_id,
        client_secret,
        scope,
        authorized_grant_types,
        access_token_validity,
        refresh_token_validity
    ) values (
        '4dae1aa5-8200-4f06-ba0c-8786f64f01c0',
        'ac1ecd02-9e0c-4883-a0f0-c62dd5cccdc3',
        'read,write,user',
        'password,authorization_code,refresh_token',
        36000,
        604800
    );

insert into oauth_client_details (
        client_id,
        client_secret,
        scope,
        authorized_grant_types,
        access_token_validity,
        refresh_token_validity,
        autoapprove
    ) values (
        'ccb81ad6-62b7-407a-8a95-a8c31f656307',
        '059fcb2d-be4e-4449-b991-0bf7a1bcd03a',
        'cli',
        'cli',
        0,
        0,
        'none'
    );
