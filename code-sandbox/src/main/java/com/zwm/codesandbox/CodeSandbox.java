package com.zwm.codesandbox;


import com.zwm.codesandbox.model.ExecuteCodeRequest;
import com.zwm.codesandbox.model.ExecuteCodeResponse;

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
