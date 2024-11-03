package com.zwm.oj.judge.strategy.impl;

import cn.hutool.json.JSONUtil;
import com.zwm.oj.judge.strategy.JudgeContext;
import com.zwm.oj.judge.strategy.JudgeStrategy;
import com.zwm.oj.model.dto.question.JudgeCase;
import com.zwm.oj.model.dto.question.JudgeConfig;
import com.zwm.oj.judge.codeSandbox.model.JudgeInfo;
import com.zwm.oj.model.entity.Question;
import com.zwm.oj.model.enums.JudgeInfoMessageEnum;

import java.util.List;

public class DefaultJudgeStrategyImpl implements JudgeStrategy {

    /**
     * 判题逻辑
     * @param judgeContext
     * @return
     */
    @Override
    public JudgeInfo doJudge(JudgeContext judgeContext) {

        JudgeInfo judgeInfo = judgeContext.getJudgeInfo(); //获取判题信息
        List<String> inputList = judgeContext.getInputList(); //获取用户输入
        List<String> outputList = judgeContext.getOutputList(); //获取用户输出


        List<JudgeCase> judgeCaseList = judgeContext.getJudgeCaseList();// 获取判题用例
        Question question = judgeContext.getQuestion();//获取问题信息
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
        String judgeConfigStr = question.getJudgeConfig(); //获取判题配置
        JudgeConfig judgeConfig = JSONUtil.toBean(judgeConfigStr, JudgeConfig.class); //获取判题配置
        long memoryLimit = judgeConfig.getMemoryLimit();
        long timeLimit = judgeConfig.getTimeLimit();
        if (time > timeLimit){
            //超过限制时间
            judgeInfoMessageEnum = JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }
        if (memory > memoryLimit){
            //超过限制内存
            judgeInfoMessageEnum = JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }

        judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
        return judgeInfoResponse;
    }
}
