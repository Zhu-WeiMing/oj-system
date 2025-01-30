/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type {CancelablePromise} from '../core/CancelablePromise';
import {OpenAPI} from '../core/OpenAPI';
import {request as __request} from '../core/request';
import {BaseResponse_ExamineVO_} from "../models/BaseResponse_ExamineVO_";
import type {PostUpdateRequest} from "../models/PostUpdateRequest";
import type {BaseResponse_boolean_} from "../models/BaseResponse_boolean_";
import type {PostQueryRequest} from "../models/PostQueryRequest";

export class CommentsControllerService {

    /**
     * 获取父级评论(分页 )
     * @param postId
     * @param current
     * @param pageSize
     */
    public static listParentCommentsPage(
        postId: number,
        current: number,
        pageSize: number,
    ): any {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/comments/listParentComments',
            body: {
                'postId': postId,
                'pageSize': pageSize,
                'current': current
            },
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }


    /**
     * 递归获取子评论（分页）
     * @param postId
     * @param parentId
     * @param current
     * @param pageSize
     */
    public static listChildCommentsPage(
        postId: number,
        parentId: number,
        current: number,
        pageSize: number,
    ): any {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/comments/listChildComments',
            body: {
                'postId': postId,
                'parentId': parentId,
                'pageSize': pageSize,
                'current': current
            },
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }


    /**
     * 发布文章
     * @param postId
     * @param parentId
     * @param content
     */
    public static saveComment(
        postId: number,
        parentId: number,
        content: string,
    ): CancelablePromise<BaseResponse_ExamineVO_> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/comments/saveComment',
            body: {
                'postId': postId,
                'parentId': parentId,
                'content': content
            },
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }


    public static listComment(
        postQueryRequest: PostQueryRequest,
    ): any {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/comments/list',
            body: {
                postQueryRequest
            },
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }

    public static updatePostUsingPost(
        postUpdateRequest: PostUpdateRequest,
    ): CancelablePromise<BaseResponse_boolean_ | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/comments/update',
            body: postUpdateRequest,
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
}
