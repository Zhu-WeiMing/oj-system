package com.zwm.examineservice.controller;

import com.alibaba.nacos.shaded.com.google.gson.reflect.TypeToken;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.Gson;
import com.zwm.client.service.UserFeignClient;
import com.zwm.common.common.BaseResponse;
import com.zwm.common.common.ErrorCode;
import com.zwm.common.common.ResultUtils;
import com.zwm.common.exception.BusinessException;
import com.zwm.examineservice.service.ExamineService;
import com.zwm.model.dto.examine.thirdApi.ThirdApiBanList;
import com.zwm.model.dto.examine.thirdApi.ThirdApiExamine;
import com.zwm.model.dto.examine.thirdApi.ThirdApiExamineData;
import com.zwm.model.entity.Examine;
import com.zwm.model.entity.User;
import com.zwm.model.vo.ExamineVO;
import com.zwm.model.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/")
@Slf4j
public class ExamineController {
    @Resource
    private ExamineService examineService;
    @Resource
    private UserFeignClient userFeignClient;

    @GetMapping("/getByPostId")
    public BaseResponse<ExamineVO> getByPostId(@RequestParam("postId") String postId) {
        if (postId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<Examine> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("postId", postId);
        Examine examine = examineService.getOne(queryWrapper);

        // 转换为 JSON 格式的字符串
        String json = convertToJson(examine.getBanList());
        Gson gson = new Gson();
        // 解析
        List<ThirdApiBanList> banList = gson.fromJson(json, List.class);
        ExamineVO examineVO = new ExamineVO();
        BeanUtils.copyProperties(examine, examineVO);
        examineVO.setBanList(banList);
        if (examine.getExamineUserId() != null) {
            User user = userFeignClient.getById(examine.getExamineUserId());
            examineVO.setExamineUserName(user.getUserName());
        }
        return ResultUtils.success(examineVO);
    }

    public static String convertToJson(String input) {
        // 使用正则表达式将字符串转换为 JSON 格式
        String jsonString = input
                .replaceAll("ThirdApiBanList\\(", "{")
                .replaceAll("\\)", "}")
                .replaceAll("word=", "\"word\": \"")
                .replaceAll(", category=", "\", \"category\": \"")
                .replaceAll(", explanation=", "\", \"explanation\": \"")
                .replaceAll(", index=", "\", \"index\": ")
                .replaceAll("}", "\"}");

        // 修复逗号和引号之间的空格
        jsonString = jsonString.replaceAll("\\s*\"", "\"").replaceAll("\",\\s*", "\", ");

        // 包装为 JSON 数组
        return "[" + jsonString + "]";
    }


}
