package com.zwm.commentsservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zwm.client.service.UserFeignClient;
import com.zwm.commentsservice.mapper.CommentsMapper;
import com.zwm.commentsservice.service.CommentsService;
import com.zwm.model.dto.comments.CommentsPageDto;
import com.zwm.model.enums.ExamineStatusEnum;
import com.zwm.model.vo.CommentsVO;
import com.zwm.model.entity.Comments;
import com.zwm.model.entity.User;
import com.zwm.model.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommentsServiceImpl extends ServiceImpl<CommentsMapper, Comments>
        implements CommentsService {


    @Resource
    private UserFeignClient userFeignClient;
    @Resource
    private CommentsMapper commentsMapper;

    /**
     * 获取父级评论(分页 )
     *
     * @param commentsPageDto
     * @return
     */
    @Override
    public IPage<CommentsVO> listParentComments(CommentsPageDto commentsPageDto) {
        // 创建分页对象
        long page = commentsPageDto.getCurrent();
        long pageSize = commentsPageDto.getPageSize();
        Page<Comments> commentPage = new Page<>(page, pageSize);

        // 查询父级评论
        QueryWrapper<Comments> wrapper = new QueryWrapper<>();
        wrapper.eq("postId", commentsPageDto.getPostId());
        wrapper.eq("isDelete", 0);
        wrapper.isNull("parentId");
        wrapper.eq("examineStatus", ExamineStatusEnum.EXAMINE_SUCCEED.getValue());
        IPage<Comments> commentsPage = commentsMapper.selectPage(commentPage, wrapper);

        List<Comments> commentsList = commentsPage.getRecords();

        // 查询用户信息
        List<Long> userIds = commentsList.stream().map(Comments::getUserId).distinct().collect(Collectors.toList());
        if (userIds.size() > 0) {
            Map<Long, User> userMap = userFeignClient.listByIds(userIds).stream()
                    .collect(Collectors.toMap(User::getId, user -> user));
            // 封装评论和用户信息
            List<CommentsVO> commentsVOList = commentsList.stream()
                    .map(comment -> {
                        CommentsVO vo = new CommentsVO();
                        vo.setId(comment.getId());
                        vo.setPostId(comment.getPostId());
                        vo.setParentId(comment.getParentId());
                        vo.setContent(comment.getContent());
                        vo.setCreateTime(comment.getCreateTime());
                        vo.setUserId(comment.getUserId());

                        // 设置用户信息
                        User user = userMap.get(comment.getUserId());
                        if (user != null) {
                            UserVO userVO = new UserVO();
                            BeanUtils.copyProperties(user, userVO);
                            vo.setUserVO(userVO);
                        }
                        //hasChild 是否有子评论处理
                        vo.setHasChild(false);
                        wrapper.clear();
                        wrapper.eq("parentId", comment.getId());
                        wrapper.eq("examineStatus", ExamineStatusEnum.EXAMINE_SUCCEED.getValue());
                        if (commentsMapper.exists(wrapper)) {
                            vo.setHasChild(true);
                        }
                        return vo;
                    })
                    .collect(Collectors.toList());

            // 设置分页结果
            IPage<CommentsVO> resultPage = new Page<>(page, pageSize);
            resultPage.setRecords(commentsVOList);
            resultPage.setCurrent(commentsPage.getCurrent());
            resultPage.setSize(commentsPage.getSize());
            resultPage.setTotal(commentsPage.getTotal());
            return resultPage;
        }

        return null;
    }


    /**
     * 递归获取子评论（分页）
     *
     * @param commentsPageDto
     * @return
     */
    @Override
    public IPage<CommentsVO> listChildComments(CommentsPageDto commentsPageDto) {
        // 创建分页对象
        long current = commentsPageDto.getCurrent();
        long pageSize = commentsPageDto.getPageSize();
        Page<Comments> commentPage = new Page<>(current, pageSize);

        // 查询当前父评论的子评论
        QueryWrapper<Comments> wrapper = new QueryWrapper<>();
        wrapper.eq("postId", commentsPageDto.getPostId());
        wrapper.eq("isDelete", 0);
        wrapper.eq("parentId", commentsPageDto.getParentId());
        wrapper.eq("examineStatus", ExamineStatusEnum.EXAMINE_SUCCEED.getValue());
        IPage<Comments> commentsPage = commentsMapper.selectPage(commentPage, wrapper);
        List<Comments> commentsList = commentsPage.getRecords();

        // 查询用户信息
        List<Long> userIds = commentsList.stream().map(Comments::getUserId).distinct().collect(Collectors.toList());
        Map<Long, User> userMap = userFeignClient.listByIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, user -> user));

        // 封装子评论和用户信息
        List<CommentsVO> commentsVOList = commentsList.stream()
                .map(comment -> mapToCommentsVO(comment, userMap))
                .collect(Collectors.toList());

        // 设置分页结果
        IPage<CommentsVO> resultPage = new Page<>(current, pageSize);
        resultPage.setRecords(commentsVOList);
        resultPage.setCurrent(commentsPage.getCurrent());
        resultPage.setSize(commentsPage.getSize());
        resultPage.setTotal(commentsPage.getTotal());

        return resultPage;
    }

    /**
     * 递归封装评论树（子评论）
     */
    private CommentsVO mapToCommentsVO(Comments comment, Map<Long, User> userMap) {
        CommentsVO commentsVO = new CommentsVO();
        BeanUtils.copyProperties(comment, commentsVO);

        // 设置用户信息
        User user = userMap.get(comment.getUserId());
        if (user != null) {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            commentsVO.setUserVO(userVO);
        }

        // 如果有子评论，递归设置子评论
        QueryWrapper<Comments> wrapper = new QueryWrapper<>();
        wrapper.eq("parentId", comment.getId());
        List<Comments> childComments = commentsMapper.selectList(wrapper);
        if (!childComments.isEmpty()) {
            List<CommentsVO> childCommentsVOList = childComments.stream()
                    .map(child -> mapToCommentsVO(child, userMap))
                    .collect(Collectors.toList());
            commentsVO.setChildComments(childCommentsVOList);
        }

        return commentsVO;
    }
}
