package com.zwm.postservice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.zwm.client.service.ExamineFeignClient;
import com.zwm.client.service.UserFeignClient;
import com.zwm.common.annotation.AuthCheck;
import com.zwm.common.common.BaseResponse;
import com.zwm.common.common.DeleteRequest;
import com.zwm.common.common.ErrorCode;
import com.zwm.common.common.ResultUtils;
import com.zwm.common.constant.UserConstant;
import com.zwm.common.exception.BusinessException;
import com.zwm.common.exception.ThrowUtils;
import com.zwm.model.dto.post.PostAddRequest;
import com.zwm.model.dto.post.PostEditRequest;
import com.zwm.model.dto.post.PostQueryRequest;
import com.zwm.model.dto.post.PostUpdateRequest;
import com.zwm.model.entity.Examine;
import com.zwm.model.entity.Post;
import com.zwm.model.entity.User;
import com.zwm.model.enums.ExamineStatusEnum;
import com.zwm.model.vo.PostVO;
import com.zwm.postservice.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.zwm.common.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 帖子接口
 */
@RestController
@RequestMapping("")
@Slf4j
public class PostController {

    @Resource
    private PostService postService;

    @Resource
    private UserFeignClient userFeignClient;

    private final static Gson GSON = new Gson();

    // region 增删改查

    /**
     * 创建
     *
     * @param postAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addPost(@RequestBody PostAddRequest postAddRequest, HttpServletRequest request) {
        if (postAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Post post = new Post();

        BeanUtils.copyProperties(postAddRequest, post);
        List<String> tags = postAddRequest.getTags();
        if (tags != null) {
            post.setTags(GSON.toJson(tags));
        }

        User loginUser = userFeignClient.getLoginUser(request);
        post.setUserId(loginUser.getId());
        post.setFavourNum(0);
        post.setThumbNum(0);
        post.setExamineStatus(ExamineStatusEnum.EXAMINING.getValue());
        post = postService.validPost(post, true);
        boolean result = postService.save(post);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newPostId = post.getId();
        post.setId(newPostId);
        postService.examinePost(post);
        return ResultUtils.success(newPostId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deletePost(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userFeignClient.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Post oldPost = postService.getById(id);
        ThrowUtils.throwIf(oldPost == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldPost.getUserId().equals(user.getId()) && !userFeignClient.isAdmin(user)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = postService.removeById(id);
        return ResultUtils.success(b);
    }


    @Resource
    private ExamineFeignClient examineFeignClient;

    /**
     * 更新（仅管理员）
     *
     * @param postUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updatePost(@RequestBody PostUpdateRequest postUpdateRequest
            , HttpServletRequest request) {
        if (postUpdateRequest == null || postUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Post post = new Post();

        BeanUtils.copyProperties(postUpdateRequest, post);
        List<String> tags = postUpdateRequest.getTags();
        if (tags != null) {
            post.setTags(GSON.toJson(tags));
        }
        // 参数校验
        post = postService.validPost(post, false);
        long id = postUpdateRequest.getId();
        // 判断是否存在
        Post oldPost = postService.getById(id);
        ThrowUtils.throwIf(oldPost == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = postService.updateById(post);
        //todo 更新 examine 数据库
        Examine examine = new Examine();
        examine.setPostId(postUpdateRequest.getId());
//        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = userFeignClient.getLoginUser(request);
        examine.setExamineUserId(user.getId());
        examineFeignClient.update(examine);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<PostVO> getPostVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Post post = postService.getById(id);
        if (post == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(postService.getPostVO(post, request));
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param postQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<PostVO>> listPostVOByPage(@RequestBody PostQueryRequest postQueryRequest,
                                                       HttpServletRequest request) {
        long current = postQueryRequest.getCurrent();
        long size = postQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Post> postPage = postService.page(new Page<>(current, size),
                postService.getQueryWrapper(postQueryRequest));
        return ResultUtils.success(postService.getPostVOPage(postPage, request));
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param postQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<PostVO>> listMyPostVOByPage(@RequestBody PostQueryRequest postQueryRequest,
                                                         HttpServletRequest request) {
        if (postQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userFeignClient.getLoginUser(request);
        postQueryRequest.setUserId(loginUser.getId());
        long current = postQueryRequest.getCurrent();
        long size = postQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Post> postPage = postService.page(new Page<>(current, size),
                postService.getQueryWrapper(postQueryRequest));
        return ResultUtils.success(postService.getPostVOPage(postPage, request));
    }

    // endregion

    /**
     * 分页搜索（从 ES 查询，封装类）
     *
     * @param postQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/search/page/vo")
    public BaseResponse<Page<PostVO>> searchPostVOByPage(@RequestBody PostQueryRequest postQueryRequest,
                                                         HttpServletRequest request) {
        long size = postQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Post> postPage = postService.searchFromEs(postQueryRequest);
        return ResultUtils.success(postService.getPostVOPage(postPage, request));
    }

    /**
     * 编辑（用户）
     *
     * @param postEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editPost(@RequestBody PostEditRequest postEditRequest, HttpServletRequest request) {
        if (postEditRequest == null || postEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Post post = new Post();

        BeanUtils.copyProperties(postEditRequest, post);
        List<String> tags = postEditRequest.getTags();
        if (tags != null) {
            post.setTags(GSON.toJson(tags));
        }
        // 参数校验
        post = postService.validPost(post, false);
        User loginUser = userFeignClient.getLoginUser(request);
        long id = postEditRequest.getId();
        // 判断是否存在
        Post oldPost = postService.getById(id);
        ThrowUtils.throwIf(oldPost == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldPost.getUserId().equals(loginUser.getId()) && !userFeignClient.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = postService.updateById(post);
        return ResultUtils.success(result);
    }

}
