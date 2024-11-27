package com.zwm.judgeservice.judge.strategy;


import com.zwm.judgeservice.judge.codeSandbox.model.JudgeInfo;

/**
 * 判题策略
 * 可能有多种判题策略
 * 1）单纯的将示例中的输出和代码沙箱处理过后的输出结果进行数量和结果的比较
 */
public interface JudgeStrategy {

    /**
     * 执行判题
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext);
}
