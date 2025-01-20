package com.zwm.commentsservice.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zwm.model.dto.comments.CommentsPageDto;
import com.zwm.model.vo.CommentsVO;
import com.zwm.model.entity.Comments;

public interface CommentsService extends IService<Comments> {

    /**
     * 获取父级评论
     *
     * @param commentsPageDto
     * @return
     */
    IPage<CommentsVO> listParentComments(CommentsPageDto commentsPageDto);

    /**
     * 递归获取子评论（分页）
     *
     * @param commentsPageDto
     * @return
     */
    IPage<CommentsVO> listChildComments(CommentsPageDto commentsPageDto);
}
