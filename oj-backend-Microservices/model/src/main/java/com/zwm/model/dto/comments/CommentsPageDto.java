package com.zwm.model.dto.comments;

import com.zwm.common.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public class CommentsPageDto extends PageRequest implements Serializable {
    private Long postId;
    private Long parentId;

}
