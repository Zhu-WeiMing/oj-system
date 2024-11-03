package com.zwm.oj.judge.codeSandbox;

import com.zwm.oj.judge.codeSandbox.model.ExecuteCodeRequest;
import com.zwm.oj.judge.codeSandbox.model.ExecuteCodeResponse;

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
