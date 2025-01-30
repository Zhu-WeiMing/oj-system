package com.zwm.model.vo;

import com.zwm.model.dto.examine.thirdApi.ThirdApiBanList;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ExamineVO {
    /**
     * id
     */
    private Long id;

    /**
     * 审批人id
     */
    private Long examineUserId;

    /**
     * 帖子id
     */
    private Long postId;

    /**
     * 评论id
     */
    private Long commentsId;

    /**
     * 违禁词
     */
    private List<ThirdApiBanList> banList;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


    /**
     * 审核人姓名
     */
    private String examineUserName;
}
