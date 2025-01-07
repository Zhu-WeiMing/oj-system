package com.zwm.model.vo;

import lombok.Data;

@Data
public class QuestionSubmitDataVO {
    /**
     * 解题总数
     */
    private Integer solveTotal;

    /**
     * 提交总数
     */
    private Integer commitTotal;

    /**
     * 通过率
     */
    private double passRate;
}
