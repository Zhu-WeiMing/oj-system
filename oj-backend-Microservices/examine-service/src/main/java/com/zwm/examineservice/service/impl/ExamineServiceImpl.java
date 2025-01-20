package com.zwm.examineservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zwm.examineservice.mapper.ExamineMapper;
import com.zwm.examineservice.service.ExamineService;
import com.zwm.model.entity.Examine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ExamineServiceImpl extends ServiceImpl<ExamineMapper, Examine> implements ExamineService {
}
