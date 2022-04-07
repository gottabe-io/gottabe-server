import http from './http-service';
import {UserPrivacy, UserProfile} from "../types";

const BASE_USER_URL = '/api/user';
const httpClient = http.defaultHttpClient;

const UserService = http.RestService(BASE_USER_URL, class {
    async resendActivation(email:string) {
        return await httpClient.post(BASE_USER_URL + '/resendActivation', {
            body: 'email=' + escape(email),
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
        });
    }
    async current() {
        return await httpClient.get(BASE_USER_URL + '/current', {});
    }
    async activate(activationCode:string) {
        return await httpClient.post(BASE_USER_URL + '/activate/' + activationCode, {});
    }
    async recover(email:string) {
        return await httpClient.post(BASE_USER_URL + '/recover/' + email, {});
    }
    async changePassword(recoverCode:string, password:string, passwordConfirmation:string) {
        return await httpClient.post(BASE_USER_URL + '/recover', {
            body: { recoverCode, password, passwordConfirmation },
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
        });
    }
    async changePhoto(file: Blob) {
        let data = new FormData()
        data.append('file', file);
        return await httpClient.post(BASE_USER_URL + '/current/avatar', {
            body: data
        });
    }
    async profile(data?: UserProfile) {
        if (!data)
            return await (await httpClient.get('/api/user/current/data')).json();
        else
            return await httpClient.patch('/api/user/current/data', http.createPostOptions(data));
    }
    async privacy(data?: UserPrivacy) {
        if (!data)
            return await (await httpClient.get('/api/user/current/privacy')).json();
        else
            return await httpClient.patch('/api/user/current/privacy', http.createPostOptions(data));
    }
    async tokens() {
        return await (await httpClient.get('/api/user/current/tokens')).json();
    }
    async createToken() {
        return await httpClient.post('/api/user/current/tokens');
    }
    async revokeToken(id: string) {
        let data = new FormData()
        data.append('tokenId', id);
        return await httpClient.delete('/api/user/current/tokens', { body: data });
    }
}, { delete: false });
const userService : any = new UserService();

export default userService;

export const TOPIC_NAME = 'gottabe.user_data';
export const STORAGE_KEY = 'gottabe.data.mySelf';
