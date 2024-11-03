package com.zwm.oj.judge.service;

import com.zwm.oj.model.entity.QuestionSubmit;
import com.zwm.oj.model.vo.QuestionSubmitVO;

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
