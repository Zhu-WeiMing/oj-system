package com.zwm.answerservice.service;


import com.zwm.model.entity.Answer;
import com.zwm.model.entity.User;
import com.zwm.model.vo.AnswerGetVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface AnswerService {
    List<AnswerGetVo> getAnswerByQuestionId(Long questionId);
    void saveAnswer(Answer answer, HttpServletRequest request);
    void deleteByQuestionId(Long questionId, User currentUser);
}
