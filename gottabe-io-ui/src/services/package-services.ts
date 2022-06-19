import http from './http-service';
import {PackageData, PackageGroup} from "../types";

const BASE_USER_URL = '/api/packages';
const httpClient = http.defaultHttpClient;

const PackageService = http.RestService(BASE_USER_URL, class {
    async mine(): Promise<PackageData[]> {
        return await (await httpClient.get(BASE_USER_URL + '/mine', {})).json();
    }
    async group(groupName: string, params?: any): Promise<PackageData[]> {
        let options:any = {};
        if (params) options.params = params;
        return await (await httpClient.get(BASE_USER_URL + '/' + groupName + '/all', options)).json();
    }
    async package(groupName: string, packageName: string, params?: any): Promise<PackageData[]> {
        let options:any = {};
        if (params) options.params = params;
        return await (await httpClient.get(BASE_USER_URL + '/' + groupName + '/' + packageName, options)).json();
    }
    async createGroup(group: PackageGroup) {
        return await httpClient.post(BASE_USER_URL, http.createPostOptions(group));
    }
    async createPackage(packageData: PackageData) {
        return await httpClient.post(BASE_USER_URL + "/" + packageData.group?.name, http.createPostOptions(packageData));
    }
}, { delete: false });
const packageService : any = new PackageService();

export default packageService;
