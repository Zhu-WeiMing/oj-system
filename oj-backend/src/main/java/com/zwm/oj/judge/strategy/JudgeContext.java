package com.zwm.oj.judge.strategy;

import com.zwm.oj.model.dto.question.JudgeCase;
import com.zwm.oj.judge.codeSandbox.model.JudgeInfo;
import com.zwm.oj.model.entity.Question;
import com.zwm.oj.model.entity.QuestionSubmit;
import lombok.Data;

import java.util.List;

/**
 * 判题上下文(用于定义在策略中传递的参数)
 */
@Data
public class JudgeContext {

    // 判题信息
    private JudgeInfo judgeInfo;

    // 用户输入
    private List<String> inputList;

    // 用户输出（执行结果）
    private List<String> outputList;

    // 判题用例（标准答案）
    List<JudgeCase> judgeCaseList;

    private Question question;

    private QuestionSubmit questionSubmit;
}
