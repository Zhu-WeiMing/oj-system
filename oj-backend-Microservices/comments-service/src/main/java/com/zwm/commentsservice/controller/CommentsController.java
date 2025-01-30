package com.zwm.commentsservice.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zwm.client.service.ExamineFeignClient;
import com.zwm.client.service.UserFeignClient;
import com.zwm.commentsservice.mapper.CommentsMapper;
import com.zwm.commentsservice.service.CommentsService;
import com.zwm.common.annotation.AuthCheck;
import com.zwm.common.common.BaseResponse;
import com.zwm.common.common.ErrorCode;
import com.zwm.common.common.ResultUtils;
import com.zwm.common.constant.UserConstant;
import com.zwm.common.exception.BusinessException;
import com.zwm.common.exception.ThrowUtils;
import com.zwm.model.dto.comments.CommentQueryRequest;
import com.zwm.model.dto.comments.CommentSaveDto;
import com.zwm.model.dto.comments.CommentUpdateRequest;
import com.zwm.model.dto.comments.CommentsPageDto;
import com.zwm.model.dto.examine.ExamineDto;
import com.zwm.model.dto.examine.thirdApi.ThirdApiBanList;
import com.zwm.model.dto.post.PostQueryRequest;
import com.zwm.model.dto.post.PostUpdateRequest;
import com.zwm.model.entity.Comments;
import com.zwm.model.entity.Examine;
import com.zwm.model.entity.Post;
import com.zwm.model.entity.User;
import com.zwm.model.enums.ExamineStatusEnum;
import com.zwm.model.vo.CommentsVO;
import com.zwm.model.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/")
@Slf4j
public class CommentsController {
    @Resource
    private CommentsService commentsService;
    @Resource
    private UserFeignClient userFeignClient;
    @Resource
    private ExamineFeignClient examineFeignClient;

    /**
     * 获取父级评论(分页 )
     *
     * @param commentsPageDto
     * @return
     */
    @PostMapping("/listParentComments")
    public BaseResponse<IPage<CommentsVO>> listParentComments(@RequestBody CommentsPageDto commentsPageDto) {
        if (commentsPageDto.getPostId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        IPage<CommentsVO> commentsVOIPage = commentsService.listParentComments(commentsPageDto);
        return ResultUtils.success(commentsVOIPage);
    }

    /**
     * 递归获取子评论（分页）
     *
     * @param commentsPageDto
     * @return
     */
    @PostMapping("/listChildComments")
    public BaseResponse<IPage<CommentsVO>> listChildComments(@RequestBody CommentsPageDto commentsPageDto) {
        if (commentsPageDto.getPostId() == null || commentsPageDto.getParentId() == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        IPage<CommentsVO> commentsVOIPage = commentsService.listChildComments(commentsPageDto);
        return ResultUtils.success(commentsVOIPage);
    }

    /**
     * 发布评论
     *
     * @param commentSaveDto
     * @return
     */
    @PostMapping("/saveComment")
    public BaseResponse saveComment(@RequestBody CommentSaveDto commentSaveDto, HttpServletRequest request) {
        if (ObjectUtil.isEmpty(commentSaveDto.getContent()) || ObjectUtil.isEmpty(commentSaveDto.getPostId())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Comments comments = new Comments();
        BeanUtils.copyProperties(commentSaveDto, comments);
        User loginUser = userFeignClient.getLoginUser(request);
        comments.setCreateTime(new Date());
        comments.setIsDelete(0);
        comments.setUserId(loginUser.getId());
        commentsService.save(comments);
        long newCommentsId = comments.getId();
        comments.setId(newCommentsId);
        //审核
        ExamineDto examineDto = new ExamineDto();
        Examine examine = new Examine();

        examineDto.setContent(commentSaveDto.getContent());
        List<ThirdApiBanList> thirdApiBanList = examineFeignClient.examine(examineDto);
        if (!ObjectUtils.isEmpty(thirdApiBanList)) {
            comments.setExamineStatus(ExamineStatusEnum.EXAMINING.getValue());//审核中
        } else {
            comments.setExamineStatus(ExamineStatusEnum.EXAMINE_SUCCEED.getValue());//无违规
        }
        commentsService.updateById(comments);
        examine.setCommentsId(comments.getId());
        examine.setBanList(String.valueOf(thirdApiBanList));
        examine.setCreateTime(new Date());
        examine.setUpdateTime(new Date());
        examineFeignClient.save(examine);
        return ResultUtils.success("发布成功");
    }


    @PostMapping("/list")
    public BaseResponse<List<CommentsVO>> list(@RequestBody PostQueryRequest postQueryRequest) {
        //todo bug
        QueryWrapper<Comments> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(postQueryRequest.getId() != null, "id", postQueryRequest.getId());
        queryWrapper.eq(postQueryRequest.getExamineStatus() != null, "examineStatus", postQueryRequest.getExamineStatus());
        List<Comments> comments = commentsService.list(queryWrapper);
        List<CommentsVO> commentsVOList = new ArrayList<>();
        for (Comments comment : comments) {
            CommentsVO commentsVO = new CommentsVO();
            BeanUtils.copyProperties(comment, commentsVO);
            User user = userFeignClient.getById(comment.getUserId());
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            commentsVO.setUser(userVO);
            commentsVOList.add(commentsVO);
        }
        return ResultUtils.success(commentsVOList);
    }

    /**
     * 更新（仅管理员）
     *
     * @param commentUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<String> updatePost(@RequestBody CommentUpdateRequest commentUpdateRequest
            , HttpServletRequest request) {
        if (commentUpdateRequest == null || commentUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Comments comments = new Comments();
        BeanUtils.copyProperties(commentUpdateRequest, comments);
        boolean update = commentsService.updateById(comments);
        if (update = false) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        Examine examine = new Examine();
        examine.setCommentsId(commentUpdateRequest.getId());
//        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = userFeignClient.getLoginUser(request);
        examine.setExamineUserId(user.getId());
        examineFeignClient.update(examine);
        return ResultUtils.success("更新成功");
    }

}
