/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import {UserVO} from "./UserVO";

export type QuestionSubmitQueryRequest = {
    code?: string;
    current?: number;
    language?: string;
    pageSize?: number;
    questionId?: number;
    sortField?: string;
    sortOrder?: string;
    status?: number;
    userId?: number;
    userVO?: UserVO;
    judgeInfo?: {
        time?: string;
        memory?: string;
    }
};

