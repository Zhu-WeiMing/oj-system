package com.zwm.questionservice.controller.inner;

import com.zwm.client.service.QuestionFeignClient;
import com.zwm.model.entity.Question;
import com.zwm.model.entity.QuestionSubmit;
import com.zwm.questionservice.service.QuestionService;
import com.zwm.questionservice.service.QuestionSubmitService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class QuestionInnerController implements QuestionFeignClient {
    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Override
    @GetMapping("/get/id")
    public Question getById(long id) {
        return questionService.getById(id);
    }

    @Override
    @GetMapping("/question_submit/get/id")
    public QuestionSubmit getQuestionSubmitById(long id) {
        return questionSubmitService.getById(id);
    }

    @Override
    @PostMapping("/question_submit/update")
    public boolean updateQuestionSubmitById(QuestionSubmit questionSubmit) {
        return questionSubmitService.updateById(questionSubmit);
    }
}
