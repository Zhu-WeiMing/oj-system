package com.zwm.judgeservice.judge.codeSandbox.impl;


import com.zwm.judgeservice.judge.codeSandbox.CodeSandbox;
import com.zwm.judgeservice.judge.codeSandbox.model.ExecuteCodeRequest;
import com.zwm.judgeservice.judge.codeSandbox.model.ExecuteCodeResponse;
import com.zwm.judgeservice.judge.codeSandbox.model.JudgeInfo;
import com.zwm.model.enums.JudgeInfoMessageEnum;
import com.zwm.model.enums.QuestionSubmitEnum;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 示例代码沙箱(仅为了跑通业务流程)
 */
@Component
public class ExampleCodeSandbox implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest excuteCodeRequest) {
        List<String> inputList = excuteCodeRequest.getInputList();
        String code = excuteCodeRequest.getCode();
        String language = excuteCodeRequest.getLanguage();
        ExecuteCodeResponse excuteCodeResponse = new ExecuteCodeResponse();
        excuteCodeResponse.setOutputList(inputList);
        excuteCodeResponse.setMessage("测试执行成功");
        excuteCodeResponse.setStatus(String.valueOf(QuestionSubmitEnum.SUCCEED.getValue()));
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setMessage(JudgeInfoMessageEnum.ACCEPTED.getText());
        judgeInfo.setTime(100L);
        judgeInfo.setMemory(100L);
        excuteCodeResponse.setJudgeInfo(judgeInfo);
        return excuteCodeResponse;
    }
}
