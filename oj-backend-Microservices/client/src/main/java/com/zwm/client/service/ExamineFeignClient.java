package com.zwm.client.service;

import com.zwm.model.dto.examine.ExamineDto;
import com.zwm.model.dto.examine.thirdApi.ThirdApiBanList;
import com.zwm.model.entity.Examine;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "examine-service", path = "/api/examine")
public interface ExamineFeignClient {
    @PostMapping("/thirdExamine")
    List<ThirdApiBanList> examine(@RequestBody ExamineDto examineDto);

    @PostMapping("/save")
    boolean save(@RequestBody Examine examine);

    @PostMapping("/update")
    boolean update(@RequestBody Examine examine);
}
