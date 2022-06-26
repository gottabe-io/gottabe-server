import {autoLoginClient} from './http-services';
import {ApiReviewsService} from "gottabe-client";

export const reviewsService : ApiReviewsService = new ApiReviewsService(autoLoginClient);
