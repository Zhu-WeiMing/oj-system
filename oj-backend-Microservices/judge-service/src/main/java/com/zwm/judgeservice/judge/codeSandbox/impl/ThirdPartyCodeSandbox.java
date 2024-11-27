package com.zwm.judgeservice.judge.codeSandbox.impl;


import com.zwm.judgeservice.judge.codeSandbox.CodeSandbox;
import com.zwm.judgeservice.judge.codeSandbox.model.ExecuteCodeRequest;
import com.zwm.judgeservice.judge.codeSandbox.model.ExecuteCodeResponse;
import org.springframework.stereotype.Component;

/**
 * 第三方发代码沙箱（调用第三方的接口）
 */
@Component
public class ThirdPartyCodeSandbox implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest excuteCodeRequest) {
        System.out.println("第三方代码沙箱");
        return null;
    }
}
