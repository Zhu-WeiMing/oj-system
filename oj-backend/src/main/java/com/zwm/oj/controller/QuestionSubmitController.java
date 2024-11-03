package com.zwm.oj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zwm.oj.common.BaseResponse;
import com.zwm.oj.common.ErrorCode;
import com.zwm.oj.common.ResultUtils;
import com.zwm.oj.exception.BusinessException;

import com.zwm.oj.exception.ThrowUtils;
import com.zwm.oj.model.dto.question.QuestionQueryRequest;
import com.zwm.oj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.zwm.oj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.zwm.oj.model.entity.Question;
import com.zwm.oj.model.entity.QuestionSubmit;
import com.zwm.oj.model.entity.User;
import com.zwm.oj.model.vo.QuestionSubmitVO;
import com.zwm.oj.model.vo.QuestionVO;
import com.zwm.oj.service.QuestionSubmitService;
import com.zwm.oj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 题目提交接口
 *
 *
 *
 */
@RestController
@RequestMapping("/question_submit")
@Slf4j
@Deprecated
public class QuestionSubmitController {

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private UserService userService;

    /**
     * 提交题目
     *
     * @param questionSubmitAddRequest
     * @param request
     * @return result id
     */
    @PostMapping("/")
    public BaseResponse<Long> doSubmitQuestion(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest,
            HttpServletRequest request) {
        if (questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能点赞
        final User loginUser = userService.getLoginUser(request);
        long questionId = questionSubmitAddRequest.getQuestionId();
        Long result = questionSubmitService.doQuestionSubmit(questionSubmitAddRequest, loginUser);
        return ResultUtils.success(result);
    }


    /**
     * 分页获取题目提交列表（除管理员外，普通用户只能非答案，提交代码等公开信息
     *
     * @param questionSubmitQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page")
    public BaseResponse<Page<QuestionSubmitVO>> listQuestionSubmitByPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest,
                                                                 HttpServletRequest request) {

        long current = questionSubmitQueryRequest.getCurrent();
        long size = questionSubmitQueryRequest.getPageSize();
        //从数据库中查询原始的题目提交分页信息
        Page<QuestionSubmit> questionSubmitPage = questionSubmitService.page(new Page<>(current, size),
                questionSubmitService.getQueryWrapper(questionSubmitQueryRequest));
        //返回脱敏信息
        final User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(questionSubmitService.getQuestionSubmitVOPage(questionSubmitPage,loginUser));
    }

}
