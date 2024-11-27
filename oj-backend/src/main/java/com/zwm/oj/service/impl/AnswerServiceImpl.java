package com.zwm.oj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zwm.oj.common.ErrorCode;
import com.zwm.oj.exception.BusinessException;
import com.zwm.oj.mapper.AnswerMapper;
import com.zwm.oj.model.entity.Question;
import com.zwm.oj.model.entity.User;
import com.zwm.oj.model.vo.AnswerGetVo;
import com.zwm.oj.model.entity.Answer;
import com.zwm.oj.service.AnswerService;
import com.zwm.oj.service.QuestionService;
import com.zwm.oj.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Service
public class AnswerServiceImpl extends ServiceImpl<AnswerMapper, Answer>
        implements AnswerService {
    @Resource
    private AnswerMapper answerMapper;
    @Resource
    private UserService userService;
    @Resource
    private QuestionService questionService;

    @Override
    public List<AnswerGetVo> getAnswerByQuestionId(Long questionId) {
        return answerMapper.getAnswerByQuestionId(questionId);
    }

    @Override
    public void saveAnswer(Answer answer, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        answer.setUserId(loginUser.getId());
        answerMapper.insert(answer);
    }

    @Override
    public void deleteByQuestionId(Long questionId,HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        //判断是否是本人或者管理员
        Question question = questionService.getById(questionId);
        // 仅本人或管理员可删除
        if (!question.getUserId().equals(loginUser.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        answerMapper.deleteAnswer(questionId);
    }
}
