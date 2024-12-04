package com.zwm.client.service;

import com.zwm.model.entity.Question;
import com.zwm.model.entity.QuestionSubmit;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author zhuweiming
 * @description 针对表【question(题目)】的数据库操作Service
 * @createDate 2024-07-23 22:29:51
 */
@FeignClient(name = "question-service", path = "/api/question")
public interface QuestionFeignClient {

    @GetMapping("/get/id")
    Question getById(@RequestParam("id") long id);

    @GetMapping("/question_submit/get/id")
    QuestionSubmit getQuestionSubmitById(@RequestParam("id") long id);

    @PostMapping("/question_submit/update")
    boolean updateQuestionSubmitById(@RequestBody QuestionSubmit questionSubmit);
}
