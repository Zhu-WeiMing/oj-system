package com.zwm.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comments {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long postId;
    private Long userId;
    private Long parentId;
    private String content;
    private Date createTime;
    /**
     * 审核状态 0-待审核 1-审核通过 2-违规
     */
    private Integer examineStatus;
    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;
}
