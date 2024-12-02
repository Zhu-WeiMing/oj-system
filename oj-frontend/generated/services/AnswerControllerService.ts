/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { Answer } from '../models/Answer';
import type { BaseResponse_List_AnswerGetVo_ } from '../models/BaseResponse_List_AnswerGetVo_';
import type { BaseResponse_string_ } from '../models/BaseResponse_string_';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class AnswerControllerService {
    /**
     * deleteByQuestionId
     * @param questionId questionId
     * @returns BaseResponse_string_ OK
     * @throws ApiError
     */
    public static deleteByQuestionIdUsingGet(
        questionId: number,
    ): CancelablePromise<BaseResponse_string_> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/answer/deleteByQuestionId',
            query: {
                'questionId': questionId,
            },
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * getByQuestionId
     * @param questionId questionId
     * @returns BaseResponse_List_AnswerGetVo_ OK
     * @throws ApiError
     */
    public static getByQuestionIdUsingGet(
        questionId: number,
    ): CancelablePromise<BaseResponse_List_AnswerGetVo_> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/answer/getByQuestionId',
            query: {
                'questionId': questionId,
            },
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
    /**
     * postAnswer
     * @param answer answer
     * @returns BaseResponse_string_ OK
     * @returns any Created
     * @throws ApiError
     */
    public static postAnswerUsingPost(
        answer: Answer,
    ): CancelablePromise<BaseResponse_string_ | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/answer/postAnswer',
            body: answer,
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
}
