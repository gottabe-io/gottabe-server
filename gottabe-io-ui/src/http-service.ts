import PubSub from 'pubsub-js';
import { Mutex } from './mutex';

const TOPIC_NAME = 'gottabe.credentials';
const LOCAL_STORE_KEY = 'gottabe.data.Iamback';
const API_ID = '4dae1aa5-8200-4f06-ba0c-8786f64f01c0';
const API_KEY = 'ac1ecd02-9e0c-4883-a0f0-c62dd5cccdc3';
const DEV_MODE = process.env.DEV_MODE;
console.log(DEV_MODE);
interface CredentialTypes {

	authToken: string;

	refreshToken: string;

};

const credentials : CredentialTypes = JSON.parse(localStorage.getItem(LOCAL_STORE_KEY) || '{ "authToken": null, "refreshToken": null }');

const saveCredentials = (creds : CredentialTypes) => localStorage.setItem(LOCAL_STORE_KEY, JSON.stringify(creds));

interface Config {
    baseUrl: string | null;
}

const config : Config = { baseUrl : null };

const setConfig = (_config: Config) => {
    config.baseUrl = _config.baseUrl;
}

const getConfig = () : Config => {
    return config;
}

setTimeout(function(){
	PubSub.publishSync(TOPIC_NAME, credentials);
}, 1);

const httpheaders = {
    /**
     * The HTTP Accept header field name.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-5.3.2">Section 5.3.2 of RFC 7231</a>
     */
    ACCEPT: 'Accept',
    /**
     * The HTTP Accept-Charset header field name.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-5.3.3">Section 5.3.3 of RFC 7231</a>
     */
    ACCEPT_CHARSET: 'Accept-Charset',
    /**
     * The HTTP Accept-Encoding header field name.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-5.3.4">Section 5.3.4 of RFC 7231</a>
     */
    ACCEPT_ENCODING: 'Accept-Encoding',
    /**
     * The HTTP Accept-Language header field name.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-5.3.5">Section 5.3.5 of RFC 7231</a>
     */
    ACCEPT_LANGUAGE: 'Accept-Language',
    /**
     * The HTTP Accept-Ranges header field name.
     * @see <a href="https://tools.ietf.org/html/rfc7233#section-2.3">Section 5.3.5 of RFC 7233</a>
     */
    ACCEPT_RANGES: 'Accept-Ranges',
    /**
     * The CORS Access-Control-Allow-Credentials response header field name.
     * @see <a href="https://www.w3.org/TR/cors/">CORS W3C recommendation</a>
     */
    ACCESS_CONTROL_ALLOW_CREDENTIALS: 'Access-Control-Allow-Credentials',
    /**
     * The CORS Access-Control-Allow-Headers response header field name.
     * @see <a href="https://www.w3.org/TR/cors/">CORS W3C recommendation</a>
     */
    ACCESS_CONTROL_ALLOW_HEADERS: 'Access-Control-Allow-Headers',
    /**
     * The CORS Access-Control-Allow-Methods response header field name.
     * @see <a href="https://www.w3.org/TR/cors/">CORS W3C recommendation</a>
     */
    ACCESS_CONTROL_ALLOW_METHODS: 'Access-Control-Allow-Methods',
    /**
     * The CORS Access-Control-Allow-Origin response header field name.
     * @see <a href="https://www.w3.org/TR/cors/">CORS W3C recommendation</a>
     */
    ACCESS_CONTROL_ALLOW_ORIGIN: 'Access-Control-Allow-Origin',
    /**
     * The CORS Access-Control-Expose-Headers response header field name.
     * @see <a href="https://www.w3.org/TR/cors/">CORS W3C recommendation</a>
     */
    ACCESS_CONTROL_EXPOSE_HEADERS: 'Access-Control-Expose-Headers',
    /**
     * The CORS Access-Control-Max-Age response header field name.
     * @see <a href="https://www.w3.org/TR/cors/">CORS W3C recommendation</a>
     */
    ACCESS_CONTROL_MAX_AGE: 'Access-Control-Max-Age',
    /**
     * The CORS Access-Control-Request-Headers request header field name.
     * @see <a href="https://www.w3.org/TR/cors/">CORS W3C recommendation</a>
     */
    ACCESS_CONTROL_REQUEST_HEADERS: 'Access-Control-Request-Headers',
    /**
     * The CORS Access-Control-Request-Method request header field name.
     * @see <a href="https://www.w3.org/TR/cors/">CORS W3C recommendation</a>
     */
    ACCESS_CONTROL_REQUEST_METHOD: 'Access-Control-Request-Method',
    /**
     * The HTTP Age header field name.
     * @see <a href="https://tools.ietf.org/html/rfc7234#section-5.1">Section 5.1 of RFC 7234</a>
     */
    AGE: 'Age',
    /**
     * The HTTP Allow header field name.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-7.4.1">Section 7.4.1 of RFC 7231</a>
     */
    ALLOW: 'Allow',
    /**
     * The HTTP Authorization header field name.
     * @see <a href="https://tools.ietf.org/html/rfc7235#section-4.2">Section 4.2 of RFC 7235</a>
     */
    AUTHORIZATION: 'Authorization',
    /**
     * The HTTP Cache-Control header field name.
     * @see <a href="https://tools.ietf.org/html/rfc7234#section-5.2">Section 5.2 of RFC 7234</a>
     */
    CACHE_CONTROL: 'Cache-Control',
    /**
     * The HTTP Connection header field name.
     * @see <a href="https://tools.ietf.org/html/rfc7230#section-6.1">Section 6.1 of RFC 7230</a>
     */
    CONNECTION: 'Connection',
    /**
     * The HTTP Content-Encoding header field name.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-3.1.2.2">Section 3.1.2.2 of RFC 7231</a>
     */
    CONTENT_ENCODING: 'Content-Encoding',
    /**
     * The HTTP Content-Disposition header field name.
     * @see <a href="https://tools.ietf.org/html/rfc6266">RFC 6266</a>
     */
    CONTENT_DISPOSITION: 'Content-Disposition',
    /**
     * The HTTP Content-Language header field name.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-3.1.3.2">Section 3.1.3.2 of RFC 7231</a>
     */
    CONTENT_LANGUAGE: 'Content-Language',
    /**
     * The HTTP Content-Length header field name.
     * @see <a href="https://tools.ietf.org/html/rfc7230#section-3.3.2">Section 3.3.2 of RFC 7230</a>
     */
    CONTENT_LENGTH: 'Content-Length',
    /**
     * The HTTP Content-Location header field name.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-3.1.4.2">Section 3.1.4.2 of RFC 7231</a>
     */
    CONTENT_LOCATION: 'Content-Location',
    /**
     * The HTTP Content-Range header field name.
     * @see <a href="https://tools.ietf.org/html/rfc7233#section-4.2">Section 4.2 of RFC 7233</a>
     */
    CONTENT_RANGE: 'Content-Range',
    /**
     * The HTTP Content-Type header field name.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-3.1.1.5">Section 3.1.1.5 of RFC 7231</a>
     */
    CONTENT_TYPE: 'Content-Type',
    /**
     * The HTTP Cookie header field name.
     * @see <a href="https://tools.ietf.org/html/rfc2109#section-4.3.4">Section 4.3.4 of RFC 2109</a>
     */
    COOKIE: 'Cookie',
    /**
     * The HTTP Date header field name.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-7.1.1.2">Section 7.1.1.2 of RFC 7231</a>
     */
    DATE: 'Date',
    /**
     * The HTTP ETag header field name.
     * @see <a href="https://tools.ietf.org/html/rfc7232#section-2.3">Section 2.3 of RFC 7232</a>
     */
    ETAG: 'ETag',
    /**
     * The HTTP Expect header field name.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-5.1.1">Section 5.1.1 of RFC 7231</a>
     */
    EXPECT: 'Expect',
    /**
     * The HTTP Expires header field name.
     * @see <a href="https://tools.ietf.org/html/rfc7234#section-5.3">Section 5.3 of RFC 7234</a>
     */
    EXPIRES: 'Expires',
    /**
     * The HTTP From header field name.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-5.5.1">Section 5.5.1 of RFC 7231</a>
     */
    FROM: 'From',
    /**
     * The HTTP Host header field name.
     * @see <a href="https://tools.ietf.org/html/rfc7230#section-5.4">Section 5.4 of RFC 7230</a>
     */
    HOST: 'Host',
    /**
     * The HTTP If-Match header field name.
     * @see <a href="https://tools.ietf.org/html/rfc7232#section-3.1">Section 3.1 of RFC 7232</a>
     */
    IF_MATCH: 'If-Match',
    /**
     * The HTTP If-Modified-Since header field name.
     * @see <a href="https://tools.ietf.org/html/rfc7232#section-3.3">Section 3.3 of RFC 7232</a>
     */
    IF_MODIFIED_SINCE: 'If-Modified-Since',
    /**
     * The HTTP If-None-Match header field name.
     * @see <a href="https://tools.ietf.org/html/rfc7232#section-3.2">Section 3.2 of RFC 7232</a>
     */
    IF_NONE_MATCH: 'If-None-Match',
    /**
     * The HTTP If-Range header field name.
     * @see <a href="https://tools.ietf.org/html/rfc7233#section-3.2">Section 3.2 of RFC 7233</a>
     */
    IF_RANGE: 'If-Range',
    /**
     * The HTTP If-Unmodified-Since header field name.
     * @see <a href="https://tools.ietf.org/html/rfc7232#section-3.4">Section 3.4 of RFC 7232</a>
     */
    IF_UNMODIFIED_SINCE: 'If-Unmodified-Since',
    /**
     * The HTTP Last-Modified header field name.
     * @see <a href="https://tools.ietf.org/html/rfc7232#section-2.2">Section 2.2 of RFC 7232</a>
     */
    LAST_MODIFIED: 'Last-Modified',
    /**
     * The HTTP Link header field name.
     * @see <a href="https://tools.ietf.org/html/rfc5988">RFC 5988</a>
     */
    LINK: 'Link',
    /**
     * The HTTP Location header field name.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-7.1.2">Section 7.1.2 of RFC 7231</a>
     */
    LOCATION: 'Location',
    /**
     * The HTTP Max-Forwards header field name.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-5.1.2">Section 5.1.2 of RFC 7231</a>
     */
    MAX_FORWARDS: 'Max-Forwards',
    /**
     * The HTTP Origin header field name.
     * @see <a href="https://tools.ietf.org/html/rfc6454">RFC 6454</a>
     */
    ORIGIN: 'Origin',
    /**
     * The HTTP Pragma header field name.
     * @see <a href="https://tools.ietf.org/html/rfc7234#section-5.4">Section 5.4 of RFC 7234</a>
     */
    PRAGMA: 'Pragma',
    /**
     * The HTTP Proxy-Authenticate header field name.
     * @see <a href="https://tools.ietf.org/html/rfc7235#section-4.3">Section 4.3 of RFC 7235</a>
     */
    PROXY_AUTHENTICATE: 'Proxy-Authenticate',
    /**
     * The HTTP Proxy-Authorization header field name.
     * @see <a href="https://tools.ietf.org/html/rfc7235#section-4.4">Section 4.4 of RFC 7235</a>
     */
    PROXY_AUTHORIZATION: 'Proxy-Authorization',
    /**
     * The HTTP Range header field name.
     * @see <a href="https://tools.ietf.org/html/rfc7233#section-3.1">Section 3.1 of RFC 7233</a>
     */
    RANGE: 'Range',
    /**
     * The HTTP Referer header field name.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-5.5.2">Section 5.5.2 of RFC 7231</a>
     */
    REFERER: 'Referer',
    /**
     * The HTTP Retry-After header field name.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-7.1.3">Section 7.1.3 of RFC 7231</a>
     */
    RETRY_AFTER: 'Retry-After',
    /**
     * The HTTP Server header field name.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-7.4.2">Section 7.4.2 of RFC 7231</a>
     */
    SERVER: 'Server',
    /**
     * The HTTP Set-Cookie header field name.
     * @see <a href="https://tools.ietf.org/html/rfc2109#section-4.2.2">Section 4.2.2 of RFC 2109</a>
     */
    SET_COOKIE: 'Set-Cookie',
    /**
     * The HTTP Set-Cookie2 header field name.
     * @see <a href="https://tools.ietf.org/html/rfc2965">RFC 2965</a>
     */
    SET_COOKIE2: 'Set-Cookie2',
    /**
     * The HTTP TE header field name.
     * @see <a href="https://tools.ietf.org/html/rfc7230#section-4.3">Section 4.3 of RFC 7230</a>
     */
    TE: 'TE',
    /**
     * The HTTP Trailer header field name.
     * @see <a href="https://tools.ietf.org/html/rfc7230#section-4.4">Section 4.4 of RFC 7230</a>
     */
    TRAILER: 'Trailer',
    /**
     * The HTTP Transfer-Encoding header field name.
     * @see <a href="https://tools.ietf.org/html/rfc7230#section-3.3.1">Section 3.3.1 of RFC 7230</a>
     */
    TRANSFER_ENCODING: 'Transfer-Encoding',
    /**
     * The HTTP Upgrade header field name.
     * @see <a href="https://tools.ietf.org/html/rfc7230#section-6.7">Section 6.7 of RFC 7230</a>
     */
    UPGRADE: 'Upgrade',
    /**
     * The HTTP User-Agent header field name.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-5.5.3">Section 5.5.3 of RFC 7231</a>
     */
    USER_AGENT: 'User-Agent',
    /**
     * The HTTP Vary header field name.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-7.1.4">Section 7.1.4 of RFC 7231</a>
     */
    VARY: 'Vary',
    /**
     * The HTTP Via header field name.
     * @see <a href="https://tools.ietf.org/html/rfc7230#section-5.7.1">Section 5.7.1 of RFC 7230</a>
     */
    VIA: 'Via',
    /**
     * The HTTP Warning header field name.
     * @see <a href="https://tools.ietf.org/html/rfc7234#section-5.5">Section 5.5 of RFC 7234</a>
     */
    WARNING: 'Warning',
    /**
     * The HTTP WWW-Authenticate header field name.
     * @see <a href="https://tools.ietf.org/html/rfc7235#section-4.1">Section 4.1 of RFC 7235</a>
     */
    WWW_AUTHENTICATE: 'WWW-Authenticate',

};

const initStates = () => {
    PubSub.subscribe(TOPIC_NAME, (_msg:any, value:any) => {
        Object.assign(credentials, value || {});
        saveCredentials(credentials);
    });
};

const revokeToken = () : Promise<void|Response> => {
    return withAuthHttpClient.post('/oauth/revoke/' + credentials.refreshToken, { body: null })
        .then((_res: any) => {
            PubSub.publishSync(TOPIC_NAME,{authToken: null, refreshToken: null});
        });
}

const requestNoAuth = (url:string, options:any) : Promise<Response> => {
    if (!url.startsWith('http://') && !url.startsWith('https://') && config.baseUrl)
        url = config.baseUrl + (config.baseUrl.startsWith('/') ? '' : '/') + (url.startsWith('/') ? url.substring(1) : url);
    return new Promise((resolve, reject) => {
        fetch(url, options)
            .then(resp => {
                console.log(resp);
                if (resp.status >= 400 || resp.status < 100) {
                    let error : any = new Error('HTTP status ' + resp.status + ': ' + resp.statusText);
                    error.response = resp;
                    return reject(error);
                } else {
                    let contentType = resp.headers.get(httpheaders.CONTENT_TYPE) || '';
                    let contentLength = parseInt(resp.headers.get(httpheaders.CONTENT_LENGTH) || '0');
                    if (contentType.toLowerCase().startsWith('application/json') && contentLength > 0) {
                        return resp.json()
                            .then(json => resolve(json))
                            .catch(error => reject(error));
                    }
                    return resolve(resp);
                }
            })
            .catch(reject);
    });
};

const requestWithAuth = (url:string, options:any) : Promise<Response> => {
    options = options || { headers: {} };
    let authHeaders : any = {};
    if (credentials.authToken && (!options.headers || !options.headers['Authorization'])) {
        authHeaders['Authorization'] = 'Bearer ' + credentials.authToken;
    }
    let headers = Object.assign(options.headers || {}, authHeaders);
    return requestNoAuth(url, Object.assign({}, options || {}, { headers }));
};

const refreshMutex = new Mutex();
const refreshStatus = { needUpdate: false };

const requestAutoLogin = async(url:string, options:any) : Promise<Response> => {
    try {
        console.log('Waiting before Request: ' + url);
        await refreshMutex.wait(2000);
        console.log('Request: ' + url);
        return await requestWithAuth(url, options);
    } catch (e: any) {
        console.log('error: ' ,e);
        if (e.response.status == 401 || e.response.status == 403) {
            if (!refreshStatus.needUpdate) {
                refreshStatus.needUpdate = true;
                await refreshToken();
                refreshStatus.needUpdate = false;
            }
            return await requestWithAuth(url, options);
        } else throw e;
    }
};

const login = async(authData: any) => {
    let res: Response = await requestNoAuth('/oauth/token', { method: 'POST', body: authData, headers: {"Authorization": "Basic " + btoa(API_ID + ':' + API_KEY)}, credentials: "omit" });
    let v:any = await res.json()
    PubSub.publishSync(TOPIC_NAME,{authToken:v.access_token, refreshToken: v.refresh_token});
    return res;
};

const refreshToken = async() => {
    if (!credentials.refreshToken) throw new Error("Cannot refresh token");
    refreshMutex.lock();
    console.log('Trying to refresh the token');
    let authData = new FormData();
    authData.append("refresh_token", credentials.refreshToken)
    authData.append("grant_type", "refresh_token");
    try {
        let res: Response = await requestNoAuth('/oauth/token', {
            method: 'POST',
            body: authData,
            headers: {"Authorization": "Basic " + btoa(API_ID + ':' + API_KEY)},
            credentials: "omit"
        });
        let v: any = await res.json();
        PubSub.publishSync(TOPIC_NAME, {authToken: v.access_token, refreshToken: v.refresh_token});
        console.log('Success refreshing the token');
        refreshMutex.unlock();
        return res;
    } catch(e:any) {
        if (e.response.status >= 400 || e.response.status < 100) {
            PubSub.publishSync(TOPIC_NAME,{authToken:null, refreshToken: null});
        }
        console.log('Failed to refresh the token');
        refreshMutex.unlock();
        throw e;
    }
};

const insertQueryParams = (url:string, params:any) => {
    let url1 = url.indexOf('?') == -1 ? url + '?' : (url.indexOf('&') == url.length - 1 ? url : (url + '&'));
    Object.entries(params).forEach((e, i) => {
        url1 += (i == 0 ? '' : '&') + escape(e[0]) + '=' + escape(<string>e[1]);
    });
    return url1;
};

export interface HttpClient {
    get(url:string, options?:any) : Promise<Response>;
    post(url:string, options?:any) : Promise<Response>;
    put(url:string, options?:any) : Promise<Response>;
    delete(url:string, options?:any) : Promise<Response>;
    patch(url:string, options?:any) : Promise<Response>;
    request(url:string, options?:any) : Promise<Response>;
}

class DefaultHttpClient implements HttpClient {
    get (url:string, options?:any) : Promise<Response> {
        options = options || {};
        if (options.hasOwnProperty('params'))
            url = insertQueryParams(url, options.params);
        return this.request(url, Object.assign({}, options, { method: 'GET' }));
    }

    post (url:string, options?:any) : Promise<Response> {
        return this.request(url, Object.assign({}, options || {}, { method: 'POST' }));
    }

    put (url:string, options?:any) : Promise<Response> {
        return this.request(url, Object.assign({}, options || {}, { method: 'PUT' }));
    }

    delete (url:string, options?:any) : Promise<Response> {
        return this.request(url, Object.assign({}, options || {}, { method: 'DELETE' }));
    }

    patch (url:string, options?:any) : Promise<Response> {
        return this.request(url, Object.assign({}, options || {}, { method: 'PATCH' }));
    }

    request(url:string, options?:any) : Promise<Response> {
        return requestAutoLogin(url, options);
    }
}

class NoAuthHttpClient extends DefaultHttpClient {
    request(url:string, options:any) : Promise<Response> {
        return requestNoAuth(url, options);
    }
}

class WithAuthHttpClient extends DefaultHttpClient {
    request(url:string, options:any) : Promise<Response> {
        return requestWithAuth(url, options);
    }
}

const APPLICATION_JSON_UTF_8 = 'application/json; charset=utf-8';
const defaultHttpClient = new DefaultHttpClient();
const noAuthHttpClient = new NoAuthHttpClient();
const withAuthHttpClient = new WithAuthHttpClient();

function createPostOptions(ent:any, options?:any) {
    let bodyData = JSON.stringify(ent);
    let headers :any = {};
    headers[httpheaders.ACCEPT] = APPLICATION_JSON_UTF_8;
    headers[httpheaders.CONTENT_TYPE] = APPLICATION_JSON_UTF_8;
    headers[httpheaders.CONTENT_LENGTH] = bodyData.length;
    return Object.assign({}, options || {}, {
        headers,
        body: bodyData
    });
}

type Constructor<T = any> = new (...args: any[]) => T;

const _RestService_create = <TRestBase extends Constructor>(baseUrl:string, client:HttpClient, RestBase:TRestBase) => {
    return class extends RestBase {
        /**
         * Execute a POST request to the base URL.
         * @param {*} entity the entity to create
         * @param {*?} options the options to the request
         * @returns the created entity
         */
        async create(entity:any, options:any) {
            options = createPostOptions(entity, options);
            return await client.post(baseUrl, options);
        }
    };
};

const _RestService_retrieve = <TRestBase extends Constructor>(baseUrl:string, client:HttpClient, RestBase:TRestBase) => {
    return class extends RestBase {
        /**
         * Execute a GET request to the base URL
         * @param {*?} params the query parameters of the request
         * @param {*?} options the options to the request
         * @returns {Array} the result of the query
         */
        async retrieve(params:any, options:any) {
            params = params || {};
            let headers : any = {};
            headers[httpheaders.ACCEPT] = APPLICATION_JSON_UTF_8;
            return await client.get(baseUrl, Object.assign({}, options || {}, { params, headers }));
        }
    };
};

const _RestService_update = <TRestBase extends Constructor>(baseUrl:string, client:HttpClient, RestBase:TRestBase) => {
    return class extends RestBase {
        /**
         * Execute a PUT request to the base URL
         * @param {*} entity the entity to be updated
         * @param {*?} options the options to the request
         * @returns the updated entity
         */
        async update(entity:any, options:any) {
            options = createPostOptions(entity, options);
            return await client.put(baseUrl, options);
        }
    };
};

const _RestService_delete = <TRestBase extends Constructor>(baseUrl:string, client:HttpClient, RestBase:TRestBase) => {
    return class extends RestBase {
        /**
         * Execute a DELETE request to the base URL
         * @param {*} entity the entity to be updated
         * @param {*?} options the options to the request
         * @returns nothing
         */
        async delete(entity:any, options:any) {
            options = createPostOptions(entity, options);
            return await client.delete(baseUrl, options);
        }
    };
};

/**
 * Extends the base class to create a rest service
 * @param {string} baseUrl the base url
 * @param {Function?} restBase the base class to the rest service. If it is null, the function will use the Object class
 * @param {*?} options the options to extend. Setting false to the properties create, retrieve, update and delete will remove the methods.
 * @returns {Function} the rest service implementation
 */
const RestService = <TRestBase extends Constructor>(baseUrl:string, restBase:TRestBase, options:any) => {
    let opt = Object.assign({ create: true, retrieve: true, update: true, delete: true, client: defaultHttpClient }, options || {});
    let clazz = restBase || Object;
    if (opt.create)
        clazz = _RestService_create(baseUrl, opt.client, clazz);
    if (opt.retrieve)
        clazz = _RestService_retrieve(baseUrl, opt.client, clazz);
    if (opt.update)
        clazz = _RestService_update(baseUrl, opt.client, clazz);
    if (opt.delete)
        clazz = _RestService_delete(baseUrl, opt.client, clazz);
    return clazz;
};

initStates();

const moduleExports = { defaultHttpClient, noAuthHttpClient, withAuthHttpClient, RestService, Headers: httpheaders, createPostOptions, setConfig, getConfig,
    revokeToken, login, refreshToken, requestAutoLogin
};

export default moduleExports;
