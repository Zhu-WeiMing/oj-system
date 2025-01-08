package com.zwm.sensitiveservice.controller.inner;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.http.HttpUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zwm.client.service.SensitiveFeignClient;
import com.zwm.model.dto.sensitive.SensitiveDto;
import com.zwm.model.entity.BanList;
import com.zwm.model.entity.Sensitive;
import com.zwm.model.entity.SensitiveData;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/")
@Slf4j
public class SensitiveInnerController implements SensitiveFeignClient {

    @PostMapping("/examine")
    @Override
    public List<BanList> examine(@RequestBody SensitiveDto sensitiveDto) {
        Map<String, Object> params = new HashMap<>();
        params.put("text", sensitiveDto.getContent());
        String result = HttpUtil.post("https://www.xbgjw.com/api/ban", params);
        Gson gson = new Gson();
        // 解析
        Sensitive sensitive = gson.fromJson(result, Sensitive.class);
        // 获取
        SensitiveData sensitiveData = sensitive.getData();
        List<BanList> sensitiveList = sensitiveData.getBanList();//todo 存数据库
        if(! ArrayUtil.isEmpty(sensitiveList)){
            //todo 违规 转人工审核

        }

        return sensitiveList;
    }

}
