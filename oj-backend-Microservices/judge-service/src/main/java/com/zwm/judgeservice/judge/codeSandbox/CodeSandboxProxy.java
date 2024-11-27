package com.zwm.judgeservice.judge.codeSandbox;


import com.zwm.judgeservice.judge.codeSandbox.model.ExecuteCodeRequest;
import com.zwm.judgeservice.judge.codeSandbox.model.ExecuteCodeResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 代码沙箱代理：用于增强代码沙箱功能，把每次请求和响应信息打印
 */
@Slf4j
public class CodeSandboxProxy implements CodeSandbox{

    private final CodeSandbox codeSandbox;

    public CodeSandboxProxy(CodeSandbox codeSandbox) {
        this.codeSandbox = codeSandbox;
    }

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest excuteCodeRequest) {
        log.info("代码沙箱请求信息"+excuteCodeRequest.toString());
        ExecuteCodeResponse excuteCodeResponse = codeSandbox.executeCode(excuteCodeRequest);
        log.info("代码沙箱响应信息"+excuteCodeResponse.toString());
        return excuteCodeResponse;
    }
}
