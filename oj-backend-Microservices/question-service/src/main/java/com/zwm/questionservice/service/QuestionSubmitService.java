package com.zwm.questionservice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zwm.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.zwm.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.zwm.model.entity.QuestionSubmit;
import com.zwm.model.entity.User;
import com.zwm.model.vo.QuestionSubmitDataVO;
import com.zwm.model.vo.QuestionSubmitVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


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


    /**
     * 根据 userId 获取QuestionSubmit列表，并只保留每个questionId的最新提交
     * @param userId
     * @return
     */
    List<QuestionSubmitVO> getLatestByUserId(Long userId);


    /**
     * 根据当前用户 获取提交数据
     * @param request
     * @return
     */
    QuestionSubmitDataVO getQuestionSubmitData(HttpServletRequest request);
}
