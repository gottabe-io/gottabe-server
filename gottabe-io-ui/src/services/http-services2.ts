import {builder, HttpClient, HttpHeaders} from 'igottp';
import {OauthTokenService} from 'gottabe-client';
import PubSub from 'pubsub-js';
import { Mutex } from '../util/mutex';

const TOPIC_NAME = 'gottabe.credentials';
const LOCAL_STORE_KEY = 'gottabe.data.Iamback';
const API_ID = '4dae1aa5-8200-4f06-ba0c-8786f64f01c0';
const API_KEY = 'ac1ecd02-9e0c-4883-a0f0-c62dd5cccdc3';

interface CredentialTypes {

    authToken: string;

    refreshToken: string;

};

const credentials : CredentialTypes = JSON.parse(localStorage.getItem(LOCAL_STORE_KEY) || '{ "authToken": null, "refreshToken": null }');

const saveCredentials = (creds : CredentialTypes) => localStorage.setItem(LOCAL_STORE_KEY, JSON.stringify(creds));

const headerSetter = (headers:any) => {
    if (credentials.authToken)
        headers[HttpHeaders.AUTHORIZATION] = 'Bearer ' + credentials.authToken;
};

const responseHeaders = (_headers:any) => {

};

export const defaultClient: HttpClient = builder()
    .build();

export const withAuthClient: HttpClient = builder()
        .withHeaderSetCallback(headerSetter)
        .withResponseHeadersCallback(responseHeaders)
        .build();

const oauthTokenService = new OauthTokenService(defaultClient);

const requestWithAuth = (url: string, options?:any) => withAuthClient.request(url, options);

const refreshMutex = new Mutex();
const refreshStatus = { needUpdate: false };

const requestAutoLogin = async(url:string, options:any) : Promise<Response> => {
    try {
        await refreshMutex.wait(2000);
        return await requestWithAuth(url, options);
    } catch (e: any) {
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

export const autoLoginClient: HttpClient = builder()
    .build();

autoLoginClient.request = requestAutoLogin;

export const login = async(authData: any) => {
    let v = await oauthTokenService.authenticate(authData, API_ID, API_KEY);
    PubSub.publishSync(TOPIC_NAME,{authToken:v.access_token, refreshToken: v.refresh_token});
    return v;
};

const refreshToken = async() => {
    if (!credentials.refreshToken) throw new Error("Cannot refresh token");
    refreshMutex.lock();
    let authData : any = {
        refresh_token: credentials.refreshToken,
        grant_type: "refresh_token"
    };
    try {
        let v: any = await oauthTokenService.authenticate(authData, API_ID, API_KEY);
        PubSub.publishSync(TOPIC_NAME, {authToken: v.access_token, refreshToken: v.refresh_token});
        refreshMutex.unlock();
        return v;
    } catch(e:any) {
        if (e.response.status >= 400 || e.response.status < 100) {
            PubSub.publishSync(TOPIC_NAME,{authToken:null, refreshToken: null});
        }
        refreshMutex.unlock();
        throw e;
    }
};

const initStates = () => {
    PubSub.subscribe(TOPIC_NAME, (_msg:any, value:any) => {
        Object.assign(credentials, value || {});
        saveCredentials(credentials);
    });
};

initStates();
