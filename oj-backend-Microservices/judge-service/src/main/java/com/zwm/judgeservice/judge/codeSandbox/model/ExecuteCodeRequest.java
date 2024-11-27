package com.zwm.judgeservice.judge.codeSandbox.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 提交代码请求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteCodeRequest {
    /**
     * 输入列表
     */
    private List<String> inputList;
    /**
     * 代码
     */
    private String code;
    /**
     * 语言
     */
    private String language;
}
