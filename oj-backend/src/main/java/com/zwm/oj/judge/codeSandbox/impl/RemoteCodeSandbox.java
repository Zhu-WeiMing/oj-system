package com.zwm.oj.judge.codeSandbox.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.zwm.oj.common.ErrorCode;
import com.zwm.oj.exception.BusinessException;
import com.zwm.oj.judge.codeSandbox.CodeSandbox;
import com.zwm.oj.judge.codeSandbox.model.ExecuteCodeRequest;
import com.zwm.oj.judge.codeSandbox.model.ExecuteCodeResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 远程代码沙箱(业务使用)
 */
public class RemoteCodeSandbox implements CodeSandbox {
    // 一般在公司内API服务使用
    private static final String AUTH_REQUEST_HEADER = "auth";
    private static final String AUTH_REQUEST_SECRET = "secretKey";
    private static final Logger log = LoggerFactory.getLogger(RemoteCodeSandbox.class);

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        log.info("***远程沙箱调用***");
        // todo 记得修改沙箱地址，确定连接的是本地沙箱还是服务器上的沙箱
        String url = "http://192.168.197.131:8090/executeCode";
        String json = JSONUtil.toJsonStr(executeCodeRequest);

        //http调用
        String responseStr = HttpUtil.createPost(url)
                .header(AUTH_REQUEST_HEADER, AUTH_REQUEST_SECRET)
                .body(json)
                .execute()
                .body();

        if (StringUtils.isBlank(responseStr)){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"executeCode remoteSandbox error,message = " + responseStr);
        }
        return JSONUtil.toBean(responseStr, ExecuteCodeResponse.class);
    }
}
