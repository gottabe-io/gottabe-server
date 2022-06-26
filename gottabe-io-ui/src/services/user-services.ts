import {autoLoginClient} from './http-services';
import {UserServices} from "gottabe-client";

export const userService : UserServices = new UserServices(autoLoginClient);

export const TOPIC_NAME = 'gottabe.user_data';
export const STORAGE_KEY = 'gottabe.data.mySelf';
