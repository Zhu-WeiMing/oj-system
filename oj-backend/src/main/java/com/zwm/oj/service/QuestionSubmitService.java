package com.zwm.oj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zwm.oj.model.dto.question.QuestionQueryRequest;
import com.zwm.oj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.zwm.oj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.zwm.oj.model.entity.Question;
import com.zwm.oj.model.entity.QuestionSubmit;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zwm.oj.model.entity.User;
import com.zwm.oj.model.vo.QuestionSubmitVO;
import com.zwm.oj.model.vo.QuestionVO;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhuweiming
 * @description 针对表【question_submit(帖子题目提交)】的数据库操作Service
 * @createDate 2024-07-23 22:33:03
 */
public interface QuestionSubmitService extends IService<QuestionSubmit> {

    /**
     * 题目提交
     *
     * @param questionSubmitAddRequest
     * @param loginUser
     * @return id
     */
    Long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser);

    /**
     * 获取查询条件
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest);


    /**
     * 获取题目封装
     *
     * @param questionSubmit
     * @param loginUser
     * @return
     */
    QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser);

    /**
     * 分页获取题目封装
     *
     * @param questionSubmitPage
     * @param loginUser
     * @return
     */
    Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser);


}
