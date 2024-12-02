/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { JudgeConfig } from './JudgeConfig';
import type { QuestionVO } from './QuestionVO';
import type { UserVO } from './UserVO';
export type QuestionSubmitVO = {
    code?: string;
    id?: number;
    judgeInfo?: JudgeConfig;
    language?: string;
    questionId?: number;
    questionVO?: QuestionVO;
    status?: number;
    updateTime?: string;
    userId?: number;
    userVO?: UserVO;
};

