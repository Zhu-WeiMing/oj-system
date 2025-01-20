package com.zwm.examineservice.controller.inner;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.google.gson.Gson;
import com.zwm.client.service.ExamineFeignClient;
import com.zwm.common.common.ErrorCode;
import com.zwm.common.exception.BusinessException;
import com.zwm.examineservice.service.ExamineService;
import com.zwm.model.dto.examine.ExamineDto;
import com.zwm.model.dto.examine.thirdApi.ThirdApiBanList;
import com.zwm.model.dto.examine.thirdApi.ThirdApiExamine;
import com.zwm.model.dto.examine.thirdApi.ThirdApiExamineData;
import com.zwm.model.entity.Examine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/")
@Slf4j
public class ExamineInnerController implements ExamineFeignClient {
    @Resource
    private ExamineService examineService;

    @PostMapping("/thirdExamine")
    @Override
    public List<ThirdApiBanList> examine(@RequestBody ExamineDto examineDto) {
        Map<String, Object> params = new HashMap<>();
        params.put("text", examineDto.getContent());
        String result = HttpUtil.post("https://www.xbgjw.com/api/ban", params);
        Gson gson = new Gson();
        // 解析
        ThirdApiExamine thirdApiExamine = gson.fromJson(result, ThirdApiExamine.class);
        // 获取
        ThirdApiExamineData thirdApiExamineData = thirdApiExamine.getData();
        List<ThirdApiBanList> sensitiveList = thirdApiExamineData.getBanList();

        return sensitiveList;
    }

    @PostMapping("/save")
    @Override
    public boolean save(@RequestBody Examine examine) {
        if (ObjectUtil.isEmpty(examine)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        examineService.save(examine);
        return true;
    }

    @PostMapping("/update")
    @Override
    public boolean update(@RequestBody Examine examine) {
        if (ObjectUtil.isEmpty(examine)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LambdaUpdateWrapper<Examine> queryWrapper = new LambdaUpdateWrapper<>();
        queryWrapper.eq(Examine::getPostId, examine.getPostId());
        examineService.update(examine,queryWrapper);
        return true;
    }

}
