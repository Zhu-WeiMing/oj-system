package com.zwm.judgeservice.judge.service.impl;

import cn.hutool.json.JSONUtil;

import com.zwm.client.service.QuestionFeignClient;
import com.zwm.common.common.ErrorCode;
import com.zwm.common.exception.BusinessException;
import com.zwm.judgeservice.judge.codeSandbox.CodeSandbox;
import com.zwm.judgeservice.judge.codeSandbox.CodeSandboxFactory;
import com.zwm.judgeservice.judge.codeSandbox.CodeSandboxProxy;
import com.zwm.judgeservice.judge.codeSandbox.model.ExecuteCodeRequest;
import com.zwm.judgeservice.judge.codeSandbox.model.ExecuteCodeResponse;
import com.zwm.judgeservice.judge.codeSandbox.model.JudgeInfo;
import com.zwm.judgeservice.judge.service.JudgeService;
import com.zwm.judgeservice.judge.strategy.JudgeContext;
import com.zwm.judgeservice.judge.strategy.JudgeManager;
import com.zwm.model.dto.question.JudgeCase;
import com.zwm.model.entity.Question;
import com.zwm.model.entity.QuestionSubmit;
import com.zwm.model.enums.QuestionSubmitEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JudgeServiceImpl implements JudgeService {

    @Resource
    private QuestionFeignClient questionFeignClient;


    //默认为example
    @Value("${codesandbox.type:example}")
    private String type;

    @Resource
    private JudgeManager judgeManager;


    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {
        // 1)传入题目的提交id，获取到对应的题目、提交信息

        //获取提交题目的信息
        QuestionSubmit questionSubmit = questionFeignClient.getQuestionSubmitById(questionSubmitId);


        if (questionSubmit == null) {  //如果提交题目的信息为空，抛出去
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "提交记录不存在");
        }

        //获取题目信息
        Long questionId = questionSubmit.getQuestionId();
        Question question = questionFeignClient.getById(questionId);

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
        boolean update = questionFeignClient.updateQuestionSubmitById(questionSubmitUpdate);

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
        String jsonStr = JSONUtil.toJsonStr(judgeInfo);
        questionSubmitUpdate.setStatus(QuestionSubmitEnum.FAILED.getValue());
        if (jsonStr.contains(QuestionSubmitEnum.SUCCEED.getText())) {
            questionSubmitUpdate.setStatus(QuestionSubmitEnum.SUCCEED.getValue());
        }

        questionSubmitUpdate.setJudgeInfo(jsonStr);
        update = questionFeignClient.updateQuestionSubmitById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目状态更新失败");
        }
        QuestionSubmit questionSubmitResult = questionFeignClient.getQuestionSubmitById(questionId);
        return questionSubmitResult;
    }
}
