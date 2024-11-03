package com.zwm.oj.judge.codeSandbox.model;

import lombok.Data;

/**
 * 判题信息
 */
@Data
public class JudgeInfo {
    /**
     * 程序执行信息
     */
    private String message;
    /**
     * 时间(ms)
     */
    private long time;
    /**
     * 内存(kb)
     */
    private long memory;
}
