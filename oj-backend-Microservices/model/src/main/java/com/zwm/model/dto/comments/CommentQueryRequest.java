package com.zwm.model.dto.comments;

import lombok.Data;

@Data
public class CommentQueryRequest {
    private Long id;
    private Integer examineStatus;
}