INSERT INTO oauth_client_details
	(client_id, client_secret, scope, authorized_grant_types,
	web_server_redirect_uri, authorities, access_token_validity,
	refresh_token_validity, additional_information, autoapprove)
VALUES
	('4dae1aa5-8200-4f06-ba0c-8786f64f01c0', '{noop}ac1ecd02-9e0c-4883-a0f0-c62dd5cccdc3', 'read,write,user',
	'password,authorization_code,refresh_token', null, null, 36000, 604800, null, true);
