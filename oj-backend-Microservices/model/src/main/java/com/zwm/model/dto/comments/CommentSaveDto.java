package com.zwm.model.dto.comments;

import lombok.Data;

@Data
public class CommentSaveDto {
    private Long postId;
    private Long parentId;
    private String content;
}
