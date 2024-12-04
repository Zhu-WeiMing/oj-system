package com.zwm.judgeservice.judge.controller.inner;

import com.zwm.client.service.JudgeFeignClient;
import com.zwm.judgeservice.judge.service.JudgeService;
import com.zwm.model.entity.QuestionSubmit;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/")
public class JudgeInnerController implements JudgeFeignClient {
    @Resource
    private JudgeService judgeService;
    @Override
    @PostMapping("/do")
    public QuestionSubmit doJudge(@RequestParam("questionSubmitId")long questionSubmitId) {
        return judgeService.doJudge(questionSubmitId);
    }
}
