package com.zwm.judgeservice.judge.codeSandbox.model;

import lombok.Data;

import java.util.List;
/**
 * 代码沙箱响应接口
 */
@Data
public class ExecuteCodeResponse {
    /**
     * 输出结果
     */
    private List<String> outputList;
    /**
     * 接口信息
     */
    private String message;
    /**
     * 状态码
     */
    private String status;
    /**
     * 判题信息
     */
    private JudgeInfo judgeInfo;
}
