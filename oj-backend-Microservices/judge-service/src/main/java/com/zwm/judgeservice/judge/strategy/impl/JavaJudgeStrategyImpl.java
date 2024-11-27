package com.zwm.judgeservice.judge.strategy.impl;

import cn.hutool.json.JSONUtil;
import com.zwm.judgeservice.judge.codeSandbox.model.JudgeInfo;
import com.zwm.judgeservice.judge.strategy.JudgeContext;
import com.zwm.judgeservice.judge.strategy.JudgeStrategy;
import com.zwm.model.dto.question.JudgeCase;
import com.zwm.model.dto.question.JudgeConfig;
import com.zwm.model.entity.Question;
import com.zwm.model.enums.JudgeInfoMessageEnum;;


import java.util.List;

public class JavaJudgeStrategyImpl implements JudgeStrategy {

    /**
     * 判题逻辑
     * @param judgeContext
     * @return
     */
    @Override
    public JudgeInfo doJudge(JudgeContext judgeContext) {
        // 获取判题信息
        JudgeInfo judgeInfo = judgeContext.getJudgeInfo();
        List<String> inputList = judgeContext.getInputList();
        List<String> outputList = judgeContext.getOutputList();

        // 获取判题用例
        List<JudgeCase> judgeCaseList = judgeContext.getJudgeCaseList();
        Question question = judgeContext.getQuestion();
        JudgeInfoMessageEnum judgeInfoMessageEnum = JudgeInfoMessageEnum.ACCEPTED;

        // 判题实际执行时间
        long time = judgeInfo.getTime();
        // 判题实际占用内存
        long memory = judgeInfo.getMemory();

        JudgeInfo judgeInfoResponse = new JudgeInfo();
        judgeInfoResponse.setMemory(memory);
        judgeInfoResponse.setTime(time);
        // 判断执行结果和用例结果的数量是否一致
        if (outputList.size()!=inputList.size()){
            judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }
        // 依次判断每个用例是否一致
        for (int i = 0; i < judgeCaseList.size(); i++) {
            JudgeCase judgeCase = judgeCaseList.get(i);
            if (!judgeCase.getOutput().equals(outputList.get(i))){
                judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
                judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
                return judgeInfoResponse;
            }
        }

        // 判断其他条件是否符合要求，比如时间、内存限制

        String judgeConfigStr = question.getJudgeConfig();
        JudgeConfig judgeConfig = JSONUtil.toBean(judgeConfigStr, JudgeConfig.class);
        long memoryLimit = judgeConfig.getMemoryLimit();
        long timeLimit = judgeConfig.getTimeLimit();
        // JAVA 程序本身执行时间需要额外执行10s
        long JAVA_PROGRAM_EXECUTION_TIME = 10000L;
        if ((time - JAVA_PROGRAM_EXECUTION_TIME) > timeLimit){
            judgeInfoMessageEnum = JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }
        if (memory > memoryLimit){
            judgeInfoMessageEnum = JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }

        judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
        return judgeInfoResponse;
    }
}
