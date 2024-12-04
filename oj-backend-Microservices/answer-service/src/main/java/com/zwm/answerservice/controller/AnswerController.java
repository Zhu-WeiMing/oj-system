package com.zwm.answerservice.controller;

import com.zwm.answerservice.service.AnswerService;
import com.zwm.common.common.BaseResponse;
import com.zwm.common.common.ErrorCode;
import com.zwm.common.common.ResultUtils;
import com.zwm.common.exception.BusinessException;
import com.zwm.model.entity.Answer;
import com.zwm.model.entity.User;
import com.zwm.model.vo.AnswerGetVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.zwm.common.constant.UserConstant.USER_LOGIN_STATE;

@RestController
@RequestMapping("/")
@Slf4j
public class AnswerController {
    @Resource
    private AnswerService answerService;

    /**
     * 根据题号获取题解
     *
     * @param questionId
     * @return
     */
    @GetMapping("/getByQuestionId")
    public BaseResponse<List<AnswerGetVo>> getByQuestionId(@RequestParam Long questionId) {
        if (questionId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        log.info("getByQuestionId:{}", questionId);
        return ResultUtils.success(answerService.getAnswerByQuestionId(questionId));
    }

    /**
     * 发布题解
     * @param answer
     * @return
     */
    @PostMapping("/postAnswer")
    public BaseResponse<String> postAnswer(@RequestBody Answer answer, HttpServletRequest request) {
        if (answer == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        log.info("postAnswer:{}", answer);
        answerService.saveAnswer(answer,request);
        return ResultUtils.success("发布成功！");
    }


    @GetMapping("/deleteByQuestionId")
    public BaseResponse<String> deleteByQuestionId(@RequestParam Long questionId, User currentUser) {
        if (questionId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        answerService.deleteByQuestionId(questionId,currentUser);
        return ResultUtils.success("删除成功！");
    }




}
