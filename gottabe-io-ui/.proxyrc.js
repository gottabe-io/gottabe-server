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
const { createProxyMiddleware, responseInterceptor } = require('http-proxy-middleware');


const apiProxy  = createProxyMiddleware(["/api/user", "/oauth/token", "/oauth/revoke"], { target: 'http://localhost:8081', logLevel: 'debug', selfHandleResponse: true,
    onProxyRes: responseInterceptor(async (responseBuffer) => {
        return responseBuffer;
    }) });
const apiProxy2 = createProxyMiddleware(["/api/packages", "/api/organizations", "/api/reviews"], { target: 'http://localhost:8080', logLevel: 'debug', selfHandleResponse: true,
    onProxyRes: responseInterceptor(async (responseBuffer) => {
        return responseBuffer;
    }) });

/*
const apiProxy3  = createProxyMiddleware(["/api", "/oauth"],
    {
                target: 'http://localhost:8081',
                changeOrigin: true,
                router: {
                    '/api/user'         : 'http://localhost:8081',
                    '/oauth'            : 'http://localhost:8081',
                    '/api/packages'     : 'http://localhost:8080',
                    '/api/package'      : 'http://localhost:8080',
                    '/api/organization' : 'http://localhost:8080'
                },
                logLevel: 'debug',
                selfHandleResponse: true,
                onProxyRes: responseInterceptor(async (responseBuffer) => {
                    return responseBuffer;
                })
            });
*/
module.exports = function (app) {
    app.use(apiProxy);
    app.use(apiProxy2);
//    app.use(apiProxy3);
};

