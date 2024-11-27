package com.zwm.judgeservice.judge.codeSandbox;


import com.zwm.judgeservice.judge.codeSandbox.model.ExecuteCodeRequest;
import com.zwm.judgeservice.judge.codeSandbox.model.ExecuteCodeResponse;

/**
 * 代码沙箱接口
 */

public interface CodeSandbox {

    /**
     * 执行代码
     * @param excuteCodeRequest
     * @return
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest excuteCodeRequest);
}
