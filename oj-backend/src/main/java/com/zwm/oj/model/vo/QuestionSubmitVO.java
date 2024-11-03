package com.zwm.oj.model.vo;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.zwm.oj.model.dto.question.JudgeConfig;
import com.zwm.oj.model.entity.Question;
import com.zwm.oj.model.entity.QuestionSubmit;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 题目提交封装类
 *
 * @TableName question
 */
@Data
public class QuestionSubmitVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 编程语言
     */
    private String language;

    /**
     * 判题信息（json 对象）
     */
    private JudgeConfig judgeInfo;

    /**
     * 判题状态 0 - 待判题、1 - 判题中、2 - 成功、3 - 失败
     */
    private Integer status;

    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 提交代码
     */
    private String code;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 提交者信息
     */
    private UserVO userVO;

    /**
     *对应题目信息
     */
    private QuestionVO questionVO;




    /**
     * 包装类转对象
     *
     * @param questionSubmitVO
     * @return
     */
    public static QuestionSubmit voToObj(QuestionSubmitVO questionSubmitVO) {
        if (questionSubmitVO == null) {
            return null;
        }
        QuestionSubmit questionSubmit = new QuestionSubmit();
        BeanUtils.copyProperties(questionSubmitVO, questionSubmit);
        JudgeConfig judgeInfoObj = questionSubmitVO.getJudgeInfo();
        if (judgeInfoObj != null) {
            //把对象转化为json
            questionSubmit.setJudgeInfo(JSONUtil.toJsonStr(judgeInfoObj));
        }

        return questionSubmit;
    }

    /**
     * 对象转包装类
     *
     * @param questionSubmit
     * @return
     */
    public static QuestionSubmitVO objToVo(QuestionSubmit questionSubmit) {
        if (questionSubmit == null) {
            return null;
        }
        QuestionSubmitVO questionSubmitVO = new QuestionSubmitVO();
        BeanUtils.copyProperties(questionSubmit, questionSubmitVO);
        JudgeConfig judgeInfoStr = questionSubmitVO.getJudgeInfo();
        questionSubmit.setJudgeInfo(JSONUtil.toJsonStr(judgeInfoStr));
        return questionSubmitVO;
    }


}