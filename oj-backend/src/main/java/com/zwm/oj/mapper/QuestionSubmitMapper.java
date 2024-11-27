package com.zwm.oj.mapper;

import com.zwm.oj.model.entity.QuestionSubmit;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author zhuweiming
 * @description 针对表【question_submit(帖子点赞)】的数据库操作Mapper
 * @createDate 2024-07-23 22:33:03
 * @Entity com.zwm.oj.model.entity.QuestionSubmit
 */
public interface QuestionSubmitMapper extends BaseMapper<QuestionSubmit> {
    @Select("SELECT MAX(status) AS maxStatus FROM question_submit WHERE questionId = #{questionId} AND userId = #{userId}")
    Integer getMaxStatus(long questionId, long userId);
}




