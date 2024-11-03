package com.zwm.codesandbox.model;

import lombok.Data;

/**
 * 进程执行信息
 */
@Data
public class ExecuteMessage {
    /**
     * 进程执行结果
     */
    private Integer exitValue;

    /**
     * 进程执行成功信息
     */
    private String successMessage;

    /**
     * 进程执行失败信息
     */
    private String errorMessage;

    /**
     * 进程执行时间
     */
    private long time;

    /**
     * 进程执行内存
     */
    private long memory;

    /**
     * 进程执行状态
     */
    private Integer status;
}
