package com.zwm.model.dto.comments;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentQueryRequest {
    private Long id;
    private Integer examineStatus;
}
