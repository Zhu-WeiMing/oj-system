package com.zwm.model.dto.comments;

import lombok.Data;

@Data
public class CommentUpdateRequest {
    private Long id;
    private Integer examineStatus;
}
