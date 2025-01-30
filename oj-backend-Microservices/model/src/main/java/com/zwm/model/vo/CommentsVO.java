package com.zwm.model.vo;

import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class CommentsVO {
    private Long id;
    private Long postId;
    private Long userId;
    private UserVO user;
    private Long parentId;
    private String content;
    private Date createTime;

    private boolean hasChild;
    /**
     * 审核状态 0-待审核 1-审核通过 2-违规
     */
    private Integer examineStatus;

    // 子评论
    private List<CommentsVO> childComments;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;
}
