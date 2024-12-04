package com.zwm.answerservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.zwm.answerservice.mapper.AnswerMapper;
import com.zwm.answerservice.service.AnswerService;
import com.zwm.client.service.QuestionFeignClient;
import com.zwm.client.service.UserFeignClient;
import com.zwm.common.common.ErrorCode;
import com.zwm.common.exception.BusinessException;
import com.zwm.model.entity.Answer;
import com.zwm.model.entity.Question;
import com.zwm.model.entity.User;
import com.zwm.model.vo.AnswerGetVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class AnswerServiceImpl extends ServiceImpl<AnswerMapper, Answer>
        implements AnswerService {
    @Resource
    private AnswerMapper answerMapper;
    @Resource
    private UserFeignClient userFeignClient;
    @Resource
    private QuestionFeignClient questionFeignClient;

    @Override
    public List<AnswerGetVo> getAnswerByQuestionId(Long questionId) {
        return answerMapper.getAnswerByQuestionId(questionId);
    }

    @Override
    public void saveAnswer(Answer answer, HttpServletRequest request) {
        User loginUser = userFeignClient.getLoginUser(request);
        answer.setUserId(loginUser.getId());
        answerMapper.insert(answer);
    }

    @Override
    public void deleteByQuestionId(Long questionId,User currentUser) {
        //判断是否是本人或者管理员
        Question question = questionFeignClient.getById(questionId);
        // 仅本人或管理员可删除
        if (!question.getUserId().equals(currentUser.getId()) && !userFeignClient.isAdmin(currentUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        answerMapper.deleteAnswer(questionId);
    }
}
