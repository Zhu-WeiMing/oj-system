package com.zwm.questionservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.zwm.client.service.JudgeFeignClient;
import com.zwm.client.service.UserFeignClient;
import com.zwm.common.common.ErrorCode;
import com.zwm.common.constant.CommonConstant;
import com.zwm.common.exception.BusinessException;
import com.zwm.common.utils.SqlUtils;
import com.zwm.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.zwm.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.zwm.model.entity.Question;
import com.zwm.model.entity.QuestionSubmit;
import com.zwm.model.entity.User;
import com.zwm.model.enums.QuestionSubmitEnum;
import com.zwm.model.enums.QuestionSubmitLanguageEnum;
import com.zwm.model.vo.QuestionSubmitDataVO;
import com.zwm.model.vo.QuestionSubmitVO;
import com.zwm.model.vo.QuestionVO;
import com.zwm.questionservice.mapper.QuestionSubmitMapper;
import com.zwm.questionservice.service.QuestionService;
import com.zwm.questionservice.service.QuestionSubmitService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

/**
 * @author zhuweiming
 * @description 针对表【question_submit(帖子提交)】的数据库操作Service实现
 * @createDate 2024-07-23 22:33:03
 */
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
        implements QuestionSubmitService {


    @Resource
    private QuestionService questionService;
    @Resource
    private UserFeignClient userFeignClient;
    @Resource
    @Lazy
    private JudgeFeignClient judgeFeignClient;

    /**
     * 提交题目
     *
     * @param questionSubmitAddRequest
     * @param loginUser
     * @return
     */
    @Override
    public Long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser) {

        //校验编程语言是否合法
        String language = questionSubmitAddRequest.getLanguage();
        QuestionSubmitLanguageEnum languageEnum = QuestionSubmitLanguageEnum.getEnumByValue(language);
        if (languageEnum == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "编程语言错误");
        }

        Long questionId = questionSubmitAddRequest.getQuestionId();
        // 判断实体是否存在，根据类别获取实体
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        // 是否已提交
        long userId = loginUser.getId();
        // 每个用户串行提交题目
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setUserId(userId);
        questionSubmit.setQuestionId(questionId);
        questionSubmit.setCode(questionSubmitAddRequest.getCode());
        questionSubmit.setLanguage(language);
        // todo 设置初始状态
        questionSubmit.setStatus(QuestionSubmitEnum.WAITING.getValue());
        questionSubmit.setJudgeInfo("{}");

        boolean save = this.save(questionSubmit);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据插入失败");
        }
        //todo 执行判题服务
        Long questionSubmitId = questionSubmit.getId();
        CompletableFuture.runAsync(() -> {
            judgeFeignClient.doJudge(questionSubmitId);
        });
        return questionSubmit.getId();

    }


    /**
     * 获取查询包装类（用户根据哪些字段查询，根据前端传来的请求对象，得到 mybatis 框架支持查询 QueryWrapper 类）
     * 根据 *** 查询
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest) {
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        if (questionSubmitQueryRequest == null) {
            return queryWrapper;
        }

        String language = questionSubmitQueryRequest.getLanguage();
        Integer status = questionSubmitQueryRequest.getStatus();
        Long questionId = questionSubmitQueryRequest.getQuestionId();
        Long userId = questionSubmitQueryRequest.getUserId();
        String sortField = questionSubmitQueryRequest.getSortField();
        String sortOrder = questionSubmitQueryRequest.getSortOrder();


        queryWrapper.eq(StringUtils.isNotEmpty(language), "language", language);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(status), "status", status);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_DESC),
                sortField);
        return queryWrapper;
    }


    @Override
    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser) {
        QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
        //脱敏
        Long userId = loginUser.getId();
        if (userId != questionSubmit.getId() && !userFeignClient.isAdmin(loginUser)) {
            questionSubmitVO.setCode(null);
        }
        return questionSubmitVO;
    }


    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser) {
        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();
        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionSubmitPage.getCurrent(), questionSubmitPage.getSize(), questionSubmitPage.getTotal());
        if (CollectionUtils.isEmpty(questionSubmitList)) {
            return questionSubmitVOPage;
        }
        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream()
                .map(questionSubmit -> getQuestionSubmitVO(questionSubmit, loginUser))
                .collect(Collectors.toList());
        questionSubmitVOPage.setRecords(questionSubmitVOList);
        return questionSubmitVOPage;


    }

    @Autowired
    private QuestionSubmitMapper questionSubmitMapper;

    @Override
    public List<QuestionSubmitVO> getLatestByUserId(Long userId) {
// 根据userId查询所有的QuestionSubmit记录
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.orderByDesc("updateTime"); // 确保查询结果是按更新时间降序排列
        List<QuestionSubmit> allSubmits = list(queryWrapper);

        // 将查询结果按questionId分组，并在每个分组中选择最新的记录
        List<QuestionSubmit> questionSubmitList = allSubmits.stream()
                .collect(Collectors.groupingBy(
                        QuestionSubmit::getQuestionId,
                        Collectors.collectingAndThen(
                                Collectors.maxBy(Comparator.comparing(QuestionSubmit::getUpdateTime)),
                                optional -> optional.orElse(null) // 如果没有找到最大的，返回null
                        )
                ))
                .values()
                .stream()
                .filter(item -> item != null) // 过滤掉null值
                .collect(Collectors.toList());
        List<QuestionSubmitVO> questionSubmitVOList = new ArrayList<>();
        for (QuestionSubmit questionSubmit : questionSubmitList) {
            QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
            Question question = questionService.getById(questionSubmit.getQuestionId());

            Integer maxStatus = questionSubmitMapper.getMaxStatus(question.getId(), questionSubmit.getUserId());
            questionSubmitVO.setQuestionSubmitStatus(maxStatus == null ? 0 : maxStatus);
            QuestionVO questionVO = QuestionVO.objToVo(question);
            questionVO.setQuestionSubmitStatus(maxStatus == null ? 0 : maxStatus);
            questionSubmitVO.setQuestionVO(questionVO);


            questionSubmitVOList.add(questionSubmitVO);
        }
        return questionSubmitVOList;


    }

    @Override
    public QuestionSubmitDataVO getQuestionSubmitData(HttpServletRequest request) {
        User loginUser = userFeignClient.getLoginUser(request);
        Long userId = loginUser.getId();
        QuestionSubmitDataVO questionSubmitDataVO = new QuestionSubmitDataVO();

        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        //提交总数
        Integer commitTotal = Math.toIntExact(questionSubmitMapper.selectCount(queryWrapper));
        questionSubmitDataVO.setCommitTotal(commitTotal);
        queryWrapper.eq("status", "3");
        //解题总数
        // 使用流的方式去重，保留每个 questionId 的第一个记录
        List<QuestionSubmit> solveTotal = questionSubmitMapper.selectList(queryWrapper).stream()
                .collect(Collectors.groupingBy(QuestionSubmit::getQuestionId, Collectors.toList()))
                .entrySet().stream()
                .map(entry -> entry.getValue().get(0)) // 获取每个分组的第一个元素
                .collect(Collectors.toList());
        questionSubmitDataVO.setSolveTotal(solveTotal.size());
        //通过率
        questionSubmitDataVO.setPassRate((double) solveTotal.size() / commitTotal);

        return questionSubmitDataVO;
    }


}




