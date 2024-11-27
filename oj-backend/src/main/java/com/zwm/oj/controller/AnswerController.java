package com.zwm.oj.controller;

import com.zwm.oj.common.BaseResponse;
import com.zwm.oj.common.ErrorCode;
import com.zwm.oj.common.ResultUtils;
import com.zwm.oj.exception.BusinessException;
import com.zwm.oj.model.vo.AnswerGetVo;
import com.zwm.oj.model.entity.Answer;
import com.zwm.oj.service.AnswerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/answer")
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
    public BaseResponse<String> deleteByQuestionId(@RequestParam Long questionId, HttpServletRequest request) {
        if (questionId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        log.info("getByQuestionId:{}", questionId);
        answerService.deleteByQuestionId(questionId,request);
        return ResultUtils.success("删除成功！");
    }




}
