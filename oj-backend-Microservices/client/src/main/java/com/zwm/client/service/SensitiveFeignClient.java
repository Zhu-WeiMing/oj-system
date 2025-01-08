package com.zwm.client.service;

import com.zwm.model.dto.sensitive.SensitiveDto;
import com.zwm.model.entity.BanList;
import com.zwm.model.entity.Sensitive;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "sensitive-service", path = "/api/sensitive")
public interface SensitiveFeignClient {
    @PostMapping("/examine")
    List<BanList> examine(@RequestBody SensitiveDto sensitiveDto);
}
