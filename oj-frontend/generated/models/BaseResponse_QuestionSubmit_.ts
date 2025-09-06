/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */

export type BaseResponse_QuestionSubmit_ = {
    code?: number;
    data?: {
        /**
         * 唯一标识符
         */
        id?: number;

        /**
         * 编程语言
         */
        language?: string;

        /**
         * 判题信息（JSON 对象）
         */
        judgeInfo?: string;

        /**
         * 判题状态 0 - 待判题、1 - 判题中、2 - 成功、3 - 失败
         */
        status?: number;

        /**
         * 题目 ID
         */
        questionId?: number;

        /**
         * 创建用户 ID
         */
        userId?: number;

        /**
         * 提交代码
         */
        code?: string;

        /**
         * 创建时间
         */
        createTime?: Date;

        /**
         * 更新时间
         */
        updateTime?: Date;

        /**
         * 是否删除
         */
        isDelete?: number;
    };
    message?: string;
};

