package com.zwm.oj.service;

import com.zwm.oj.model.entity.Answer;
import com.zwm.oj.model.vo.AnswerGetVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface AnswerService {
    List<AnswerGetVo> getAnswerByQuestionId(Long questionId);
    void saveAnswer(Answer answer, HttpServletRequest request);
    void deleteByQuestionId(Long questionId,HttpServletRequest request);
}
