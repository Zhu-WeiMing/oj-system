package com.zwm.oj.judge.service.impl;

import cn.hutool.json.JSONUtil;
import com.zwm.oj.common.ErrorCode;
import com.zwm.oj.exception.BusinessException;
import com.zwm.oj.judge.codeSandbox.CodeSandbox;
import com.zwm.oj.judge.codeSandbox.CodeSandboxFactory;
import com.zwm.oj.judge.codeSandbox.CodeSandboxProxy;
import com.zwm.oj.judge.codeSandbox.model.ExecuteCodeRequest;
import com.zwm.oj.judge.codeSandbox.model.ExecuteCodeResponse;
import com.zwm.oj.judge.service.JudgeService;
import com.zwm.oj.judge.strategy.JudgeContext;
import com.zwm.oj.judge.strategy.JudgeManager;
import com.zwm.oj.model.dto.question.JudgeCase;
import com.zwm.oj.judge.codeSandbox.model.JudgeInfo;
import com.zwm.oj.model.entity.Question;
import com.zwm.oj.model.entity.QuestionSubmit;
import com.zwm.oj.model.enums.QuestionSubmitEnum;
import com.zwm.oj.service.QuestionService;
import com.zwm.oj.service.QuestionSubmitService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JudgeServiceImpl implements JudgeService {

    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    //默认为example
    @Value("${codesandbox.type:example}")
    private String type;

    @Resource
    private JudgeManager judgeManager;


    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {
        // 1)传入题目的提交id，获取到对应的题目、提交信息

        //获取提交题目的信息
        QuestionSubmit questionSubmit = questionSubmitService.getById(questionSubmitId);


        if (questionSubmit == null) {  //如果提交题目的信息为空，抛出去
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "提交记录不存在");
        }

        //获取题目信息
        Long questionId = questionSubmit.getQuestionId();
        Question question = questionService.getById(questionId);

        if (question == null) {     //如果题目的信息为空，抛出去
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }

        // 2) 如果题目提交状态不为等待中，就不用重复执行了 （修改状态为 等待中）
        if (!questionSubmit.getStatus().equals(QuestionSubmitEnum.WAITING.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目正在判题中");
        }

        // 3）更改判题状态（题目提交表单）的状态为 判题中 ，防止重复执行
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitEnum.RUNNING.getValue()); //修改状态为 判题中
        boolean update = questionSubmitService.updateById(questionSubmitUpdate);

        if (!update) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目状态更新失败");
        }

        // 4）调用沙箱，获取到执行结果
        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);
        String code = questionSubmit.getCode();
        String language = questionSubmit.getLanguage();
        // 获取输入用例
        String judgeCaseStr = question.getJudgeCase();
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCaseStr, JudgeCase.class);
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());
        codeSandbox = new CodeSandboxProxy(codeSandbox);
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        List<String> outputList = executeCodeResponse.getOutputList();
        // 5）根据代码沙箱执行结果，设置题目的判题状态和信息
        JudgeContext judgeContext = new JudgeContext();
        judgeContext.setJudgeInfo(executeCodeResponse.getJudgeInfo());
        judgeContext.setInputList(inputList);
        judgeContext.setOutputList(outputList);
        judgeContext.setJudgeCaseList(judgeCaseList);
        judgeContext.setQuestion(question);
        judgeContext.setQuestionSubmit(questionSubmit);

        //进行判题
        JudgeInfo judgeInfo = judgeManager.doJudge(judgeContext);

        // 6）更新判题结果信息
        questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitEnum.SUCCEED.getValue());
        questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        update = questionSubmitService.updateById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目状态更新失败");
        }
        QuestionSubmit questionSubmitResult = questionSubmitService.getById(questionId);
        return questionSubmitResult;
    }
}
