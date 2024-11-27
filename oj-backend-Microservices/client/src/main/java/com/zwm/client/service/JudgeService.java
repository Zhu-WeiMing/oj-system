package com.zwm.client.service;


import com.zwm.model.entity.QuestionSubmit;

/**
 * 判题服务
 * @author Snowyee
 */
public interface JudgeService {

    /**
     * 判题
     * @param questionSubmitId
     * @return
     */
    QuestionSubmit doJudge(long questionSubmitId);
}
