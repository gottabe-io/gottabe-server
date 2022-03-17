/*
{
	"/api/user": {
		"target": "http://localhost:8081"
	},
	"/oauth/token": {
		"target": "http://localhost:8081"
	},
	"/oauth/revoke": {
		"target": "http://localhost:8081"
	},
	"/api/match": {
		"target": "http://localhost:8080"
	},
	"/api/hiscores": {
		"target": "http://localhost:8080"
	}
}
*/
const { createProxyMiddleware } = require('http-proxy-middleware');

const apiProxy = createProxyMiddleware(["/api/user", "/oauth/token", "/oauth/revoke"], { target: 'http://localhost:8081' });

module.exports = function (app) {
    app.use(apiProxy);
};
