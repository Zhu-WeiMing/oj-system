/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type {BaseResponse_string_} from '../models/BaseResponse_string_';
import type {CancelablePromise} from '../core/CancelablePromise';
import {OpenAPI} from '../core/OpenAPI';
import {request as __request} from '../core/request';
import {ExamineVO} from "../models/ExamineVO";
import {BaseResponse_ExamineVO_} from "../models/BaseResponse_ExamineVO_";

export class ExamineControllerService {
    /**
     * deleteByQuestionId
     * @param questionId questionId
     * @returns BaseResponse_string_ OK
     * @throws ApiError
     */
    public static getByPostIdGet(
        postId: number,
    ): CancelablePromise<BaseResponse_ExamineVO_> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/examine/getByPostId',
            query: {
                'postId': postId,
            },
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
}
