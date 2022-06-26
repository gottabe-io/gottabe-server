import {autoLoginClient} from './http-services';
import {ApiPackagesService} from "gottabe-client";

export const packageService : ApiPackagesService = new ApiPackagesService(autoLoginClient);
